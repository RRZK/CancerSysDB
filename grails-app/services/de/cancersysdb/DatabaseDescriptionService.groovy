package de.cancersysdb

import de.cancersysdb.EntityMetadata.ClinicalInformation
import de.cancersysdb.EntityMetadata.ImportInfo
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import net.spy.memcached.internal.ListenableFuture
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * This Service displays the extensible capabilities of the Database
 * The Database is Extensible in differend Things like the datatypes that can be Provided.
 * This is Especially Important for the Construction of Workflows.
 */
class DatabaseDescriptionService {
    GrailsApplication grailsApplication
    DatasetService datasetService
    SpringSecurityService springSecurityService

    /**
     *
     * @return Map [Annotations:[Table1:AnnotationForTable1, ....],Fields:[TableName:[["Fieldname","Datatype","Required Or Optional" ],["foldChange","Integer","Required" ],.....]]
     */
    Map getDataClassesInformation()
    {
        Map out = [:]
        def UnusedTempUsingJustBecauseItsSaverAndTheFunctionCallIsNeededForInitialisation =datasetService.getDataClassNames()
        def dcs = datasetService.getDataclasses()
        print dcs
        ///List Annotations
        Map Annotations = [:]
        List IsAnnotation = []
        List MainData = []
        dcs.each { it ->
            def anno = datasetService.getAnnotationClassFor(it)
            if(anno) {
                Annotations.put(it.simpleName, anno.simpleName)
                IsAnnotation.add(anno.simpleName)
                MainData.add(it.simpleName)
            }else if(!it.simpleName.contains("Anno"))
                MainData.add(it.simpleName)
        }
        out.put("Annotations", Annotations)
        out.put("AnnotationClasses", IsAnnotation)
        out.put("MainClasses", MainData)
        // Fields
        Map Fields = [:]

        dcs.each {it ->
            Map FieldsAndTypes = datasetService.getFieldNamesandTypesForClass(it)
            List<String> reqfields = datasetService.getRequiredFieldNamesForClass(it)
            List<List> lists = []
            FieldsAndTypes.each {key,val->
                def line = []
                line[0] = key
                line[1] = val.simpleName
                if(reqfields.contains(key)){
                    line[2]="Required"
                }else
                    line[2]="Optional"
                lists.add(line)
            }
            Fields.put(it.simpleName,lists)

        }
        out.put("Fields", Fields)

        return  out




    }

    /**
     * Gets all Clinical Names
     * @return Map with occurrences of Paths and Names . The occurrences are lists of two dimentional, sorted Lists
     */
    @Secured(value=["hasRole('ROLE_ADMIN')"])
    Map describeAllClinicalDataNames(){
        Map out = [:]
        Map<String,Integer> FullPath = [:]
        Map<String,Integer> FullName = [:]
        def user = springSecurityService.getCurrentUser()

        //TODO Implement User restrictions!
        def iis= ImportInfo.findAll()

        def infos = []

        iis.each { ii->
            ii.infos.each {
                infos.add(it)
            }
        }

        infos.each { ClinicalInformation info->
            def Fullpath = info.location+"/"+info.name

            if(FullName.containsKey(info.name)){
                Integer val = FullName.get(info.name)
                val +=1
                FullName.put(info.name,val)

            }else
                FullName.put(info.name, new Integer(1))

            if(FullPath.containsKey(Fullpath)){
                Integer val = FullPath.get(Fullpath)
                val +=1
                FullPath.put(Fullpath,val)

            }else
                FullPath.put(Fullpath,new Integer(1))


        }
        List FP=[]
        FullPath.each {
            Object[] o = new Object[2]
            o[0]=it.value
            o[1] = it.key
            FP.add(o)

        }
        FP.sort{a,b-> a[0] <=> b[0] }
        List FN=[]
        FullName.each {
            Object[] o = new Object[2]
            o[0]=it.value
            o[1] = it.key
            FN.add(o)
        }
        FN.sort{a,b-> a[0] <=> b[0] }
        out["Paths"] = FP.reverse()
        out["Names"] = FN.reverse()

        return out
    }
    /**
     * Gets all Clinical Names
     * @return Map of Paths and Names . This is only a List without the Counts for this @see describeAllClinicalDataNames
     */
    @Secured(value=["hasRole('ROLE_ADMIN')"])
    Map showAllClinicalKeys(){
        Map out = [:]
        List FullPath = []
        List FullName = []
        FullName = ClinicalInformation.withCriteria {
            projections {
                distinct("exactName")
            }
        }
        def temp = ClinicalInformation.withCriteria {
            projections {
                groupProperty('location')
                groupProperty('exactName')
            }
        }
        temp.each {
            FullPath.add(it[0] + "/"+ it[1])
        }

        out["Paths"] = FullPath.sort()
        out["Names"] = FullName.sort()

        return out
    }


    /**
     * Gets all Clinical Values for a Specific Key
     * @return Map of Values and their Occurences for the given Key or Path
     */
    @Secured(value=["hasRole('ROLE_ADMIN')"])
    List describeClinicalKey(String key){
        String Path =""
        String Name= ""
        if(key.contains("/")){


            Name = key.subSequence(key.lastIndexOf("/")+1,key.size())

            Path = key.subSequence(0,key.lastIndexOf("/"))
        }else
            Name = key

        Map ou = [:]
        List out =[]

        def res
        if(Path =="")
            res = ClinicalInformation.findAllByExactName(Name)
        if(Path !="")
            res = ClinicalInformation.findAllByExactNameAndLocation(Name, Path)

        res.each{ ClinicalInformation it ->

            if(ou.containsKey(it.value)){
                Integer val = ou.get(it.value)
                val +=1
                ou.put(it.value,val)

            }else
                ou.put(it.value,new Integer(1))
        }
        ou.each {
            Object[] o = new Object[2]
            o[1]=it.value
            o[0] = it.key
            out.add(o)
        }
        out.sort{a,b-> a[1] <=> b[1] }
        out = out.reverse()
        return out
    }

}
