package de.cancersysdb.Import

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.ImportTools.ImporterServiceInterface
import de.cancersysdb.Dataset
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

@Transactional

/**
 * This Service should Handle all the Imports of Genetic Date Etc Into the Database by CSV and Familiar File Types Like BED and
 */
class FileImportService {
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    /**
     * This Service is The Main Integration class for external Imports
     * The Progress and Implemented Import Structures are Documented in the Redmine Wiki:
     * https://redmine.rrz.uni-koeln.de/projects/cancer-db/wiki/External_File_Type_Mappings
     *
     * @param File The Content of The CSV File
     * @param Filename The Filename of the CSV File
     * @param Into The Target Datatype in the Database
     * @param owner The Owner of the Data
     * @param annon Is The Data Annonymized
     * @param shared Is The Data Shared with other Users
     */
    SpringSecurityService springSecurityService
    GrailsApplication grailsApplication
    Map AllFilenamePatterns

    ImportProtocol ReadIn(
            def file, SourceFile sourceFile, String Into, User owner, boolean annon, boolean shared, ImportProtocol ip = null, Dataset ds = null) {

        /*http://stackoverflow.com/questions/2247453/intercept-service-method-calls-using-metaprogramming-in-groovy-grails
        Prepare Dynamic Choosing of Import Service
        grailsApplication.serviceClasses.findAll
           */
        ApplicationContext ctx = Holders.grailsApplication.mainContext


        AllFilenamePatterns = [:]


        grailsApplication.serviceClasses.each {

            if (it.clazz.interfaces.contains(ImporterServiceInterface.class)) {
                if (it.clazz.FilenamePattern.isEmpty()) {
                    Set keys = it.clazz.MapsTo.keySet()
                    keys.each { key ->
                        if (!AllFilenamePatterns.containsKey(key))
                            AllFilenamePatterns.put(key, [key])
                        else
                            AllFilenamePatterns.put(key, AllFilenamePatterns.get(key).add(key))
                    }
                } else
                    AllFilenamePatterns.putAll(it.clazz.FilenamePattern)
            }
        }
        log.debug(AllFilenamePatterns)

        String filename = sourceFile.getOriginalFilename()
        String filetype = this.determineFileType(filename)
        ImporterServiceInterface rightServicetoParse
        grailsApplication.serviceClasses.each {
            if (it.clazz.interfaces.contains(ImporterServiceInterface.class)) {
                //TODO Check if Import Type can be evaluated and is Known to the Database Maybe Earlyer in Import Process
                if (it.clazz.MapsTo.containsKey(filetype) && it.clazz.MapsTo.get(filetype).contains(Into)) {
                    log.debug("found Stuff" + it.shortName)
                    log.debug(it.clazz.MapsTo)
                    log.debug(filetype + "  " + Into)
                    try {
                        rightServicetoParse = (ImporterServiceInterface) ctx.getBean(it.shortName.substring(0, 1).toLowerCase() + it.shortName.substring(1))
                        if (rightServicetoParse == null)
                            log.debug("rightServicetoParse ==null")
                        else log.debug(rightServicetoParse)
                    } catch (Exception e) {

                        if (!rightServicetoParse)
                            rightServicetoParse = it.newInstance()
                    }
                }
            }
        }



        if (ip == null) {
            ip = new ImportProtocol()
            ip.Autoreport = true
            ip.AutoreportAferEveryNTHDataset = 10000
            ip.ImportStart()
            ip.Message("Importing File: "+ sourceFile.originalFilename)
        }
        if (ds == null) {
            ds = new Dataset(annon: annon, shared: shared, owner: owner)

            ds.save(failOnError: true)
        }
        ip.setDataset(ds)

        if (filetype) {
            try {

                ImportProtocol tempip = rightServicetoParse.importContent(filetype, Into, file, sourceFile, owner, annon, shared, ip, ds)
                if (tempip && !tempip.successful)
                    ip.Message("generic")

                return ip

            } catch (Exception e) {
                //Try Generic Import
                ip.Message(e.toString())
                ip.Message("generic  import Type not Recognized?!")
                return ip
            }

        }

        ip.Message("generic")
        return ip


    }

    /**
     * Simple Function to determine File Type
     * @param Filename The Filename
     * @return The File Type
     */
    String determineFileType(String Filename) {
        String out = null
        boolean outpattern = false
        log.debug(Filename)

        //If there is a Regex Pattern it weights more than a simple suffix Match
        AllFilenamePatterns.each { key, value ->
            log.debug(key + " " + value)
            value.each { val ->

                if (Filename ==~ val) {
                    log.debug("Pattern " + key)
                    out = key
                    outpattern = true
                } else if (!outpattern && Filename.endsWith(val))
                    out = key
            }

        }
        log.debug(out)


        return out

    }
}