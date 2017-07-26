package de.cancersysdb.Import

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.Dataset
import de.cancersysdb.Sample
import de.cancersysdb.serviceClasses.ExternalSourceDescription
import de.cancersysdb.User
import de.cancersysdb.geneticStandards.TCGAClassObject
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * This Class Manages the Import from Other Instances of the CancerSysDB
 */
@Transactional
class CancersysMasterImporterService {

    GrailsApplication grailsApplication
    GenericCSVImporterService genericCSVImporterService
    StructuredCSVToDataImporterService cSVToDataImporterService
    SpringSecurityService springSecurityService
    /**
     *
     * @param context describes the Source of the Dataset.
     * @param resourcereference The Resource Reference is the Identifier that has Tobe Retrived.
     * @return Did Work?
     */
    ImportProtocol ImportFromCancersysMaster(String resourcereference, ExternalSourceDescription context) {
        ImportProtocol ip = new ImportProtocol()
        ip.ImportStart()

        ip.Message("Replicating " + resourcereference)
        //String report =""
        /**
         * Retrive Structured Data
         *
         * 1. Get the resource by using Rest Request
         *
         * 2. Parse the things recursively
         *
         * Example dataset -> Sample  -> Person -> Study
         *
         * Create(dataset)
         *  Create(Sample)
         *   Create(Person)
         *    Create(Study)
         *    return Study
         *   return Person
         *  return Sample
         * Return dataset
         *
         *
         */

        //Request the Resource
        RestBuilder restBuilder = new RestBuilder(connectTimeout: 1000, readTimeout: 20000);
        def resp
        try {
            resp = restBuilder.get(resourcereference) {
                header 'Accept', 'application/json'
                header 'Content-Type', 'application/json'
            }
        } catch (Exception e) {
            if ([MalformedURLException.class, IllegalArgumentException.class].contains(e.class)) {
                ip.Message("URL not valid, please check the link!")
            } else {
                log.debug(e.class)
                ip.Message("Error on requesting the resource")
            }
            ip.successful = false
            ip.ImportEnd()
            return ip
        }

        if (resp.status != 200) {

            if ([403, 401].contains(resp.status))
                ip.Message("Access Denied, the requested information in not publicly availible. If access is restricted download yourself and upload through the normal file Upload.")
            else if ([404].contains(resp.status))
                ip.Message("Resource not found!")
            else if ([204].contains(resp.status))
                ip.Message("No content for the requested rescource")
            else
                ip.Message("could not retrive data!")
            ip.successful = false
            ip.ImportEnd()
            return ip
        }

        if (!resp.json instanceof JSONObject) {

            ip.setSuccessful(false)
            return ip
        }

        //Create StructuralData
        Map VisitedNodes = [:]

        Dataset ds = createHierarchyFromJson(resp.json as JSONObject, VisitedNodes, context, ip)

        VisitedNodes.values().each {
            val ->
                ip.Message("created : " + val.class.simpleName + " : " + val.getId())


        }
        //Import Actual Data


        ip.successful = true
        try {
            resp.json.get("datas").each {
                k, v ->
                    if (ip.successful) {
                        def data = restBuilder.get(context.getuRL() + "export/" + k + "/" + resp.json.get("id")) {
                            header 'Accept', 'application/csv'
                            header 'Content-Type', 'application/csv'
                        }
                        String text = data.text
                        //Parse Headings
                        cSVToDataImporterService.importCSVToGeneticStandard(text, ds, k, ip)
                    }

            }
        } catch (Exception e) {
            ip.Message("Errors While Importing Datasets")
            return ip

        }
        ip.ImportEnd()
        //TODO ROllback
        return ip


    }


    Object createHierarchyFromJson(JSONObject result, Map VisitedNodes, ExternalSourceDescription context, ImportProtocol ip) {

        /**
         * Strategy
         *
         * 1. Check if Incomming Class Exists in Application
         * 2. Check if Incomming Data is from an External Source (not from the source the data was retrived from)
         * 3. Create the ExternalSourceInfo
         * 3 a) If The Data is originally from the incomming Source: create the Infos where the Dataset is from
         * 3 b) If The data is NOT originally from the incomming Source: remove ID attribue
         * 4. Create Init Map For Object
         * 5. Create Object and Save
         * 6. Return Object
         */


        def newDomainObject = null
        def initmap = [:]


        DefaultGrailsDomainClass dc = null

        String Key = ""

        //TODO  better, more abstract Mangement of Exceptions
        if (result.containsKey("id") && result.containsKey("class")) {
            dc = grailsApplication.getDomainClass(result.get("class"))
            Key = result.get("class") + result.get("id")

            if (VisitedNodes.containsKey(Key))
                return VisitedNodes.get(Key)
            ip.Message("Inserting : " + result.get("class"))

        } else {
            ip.Message("Failed Inserting insufficient Information")
            return null
        }//If A User is given, given a User

        if (result.get("class").equals(User.class.name)) {
            return springSecurityService.getCurrentUser()
        } else if (result.get("class").equals(TCGAClassObject.class.name)) {

            def q = TCGAClassObject.where {

                name == result.get("name") &&
                        { abbreviation == result.get("abbreviation") || abbreviation == null } &&
                        { code == result.get("code") || code == null } &&
                        { type == result.get("type") || type == null }


            }

            def temp = q.findAll()
            if (!temp.isEmpty())
                return temp.get(0)

        } else if (result.get("class").equals(Sample.class.name) && result.containsKey("SourceIdentifier")) {

            def temp = Sample.findBySourceIdentifier(result.containsKey("SourceIdentifier"))
            if (temp)
                return temp

        }
        Object out = null
        //Is Imported as Allready Exported from Somewhere Else?
        if (result.containsKey("uRI") && result.get("uRI")) {


            def query = dc.where {
                uRI == result.get("uRI")
            }
            out = query.find()
            if (out == null) {
                out = dc.findBySourceIdentifierAndExtSource(result.get("sourceIdentifier"), result.get("extSource"))
            }
            if (out) {
                return out
            }
        }

        def sourceIdentifier = dc.getClass().getSimpleName() + result.get("id")
        def extSource = context

        for (key in result.keys()) {
            def value = result.get(key)
            if (key.equals("id") || key.equals("class")) {
                continue
            } else
            //TODO User Right Strategy!
            //User Rights
            if (key.equals("shared") || key.equals("annon")) {
                initmap.put(key, true)
                continue
            } else if (value instanceof JSONArray) {
                //ProcessArrays
                def serializedList = []
                for (element in value) {
                    if (element instanceof JSONObject) {
                        element = createHierarchyFromJson(element, VisitedNodes, context, ip)

                    }
                    serializedList.add(element)

                }
                value = serializedList
            } else if (value instanceof JSONObject) {
                //A New Object Serialize
                value = createHierarchyFromJson(value, VisitedNodes, context, ip)


            }


            initmap.put(key, value)
        }


        if (!(initmap.containsKey("sourceIdentifier") && initmap.get("sourceIdentifier")))
            initmap.put("sourceIdentifier", sourceIdentifier)
        if (!(initmap.containsKey("extSource") && initmap.get("extSource")))
            initmap.put("extSource", context)

        try {
            newDomainObject = dc.clazz.newInstance(initmap)
            newDomainObject.save(failOnError: true, flush: true)
            ip.ImportedSuccessful(newDomainObject)
        } catch (Exception e) {
            ip.ImportedFailed(newDomainObject)
            log.debug("ImportedFailed " + e)
        }
        VisitedNodes.put(Key, newDomainObject)
        return newDomainObject
    }

}
