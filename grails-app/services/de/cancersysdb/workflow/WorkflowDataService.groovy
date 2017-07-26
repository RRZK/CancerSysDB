package de.cancersysdb.workflow

import de.andreasschmitt.export.ExportService
import de.cancersysdb.Dataset
import de.cancersysdb.EntityMetadata.ClinicalInformation
import de.cancersysdb.Patient
import de.cancersysdb.Sample
import de.cancersysdb.Study
import de.cancersysdb.User
import de.cancersysdb.geneticStandards.Gene
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import groovy.sql.DataSet
import org.codehaus.groovy.grails.commons.GrailsClass

import static de.cancersysdb.geneticStandards.Gene.*

/**
 * This Service Retrives the Data for a Workflow from the Database
 */
@Transactional
class WorkflowDataService {

    def grailsApplication
    SpringSecurityService springSecurityService
    ExportService exportService

    /**
     * Process Workflow Data Descriptions
     * @param wdf The WorkflowDataDescription Object thich Describes a File for an External Workflow
     * @param DataParams The Params for The Data Extraction
     * @return An Output stram with the CSV Content which can be Written to a File
     */
    OutputStream getCSVFile(WorkflowDataDescription wdf, Map DataParams) {
        Map params
        String q = extendHQLwithUserRights(wdf.getHqlQuery())
        log.debug("Query " + q)
        if (DataParams != null) {
            Map PFQ = wdf.getParametersForQuery()
            boolean check = this.checkDataparams(DataParams, PFQ)
            if (!check)
                return null
            params = DataParams
        } else {

            params = [:]
        }

        log.debug("params "+ params)
        def a = Dataset.executeQuery(
                q, params,[readOnly:true,timeout:600]
        )

        OutputStream os = new ByteArrayOutputStream()

        if (!a.isEmpty()) {
            List fieldstoExport = wdf.getOutputFields()
            List res = RedoQueryresultForExport(a,fieldstoExport)
            Map parameters = [ "separator": ",", "header.enabled":wdf.headers,"quoteCharacter":'\u0000' ]
            Map labels = [:]
            fieldstoExport.each {
                labels.put(it, it)
            }
            print labels
            print "headers " + wdf.headers


            exportService.export('csv', os, res, fieldstoExport, labels, [:], parameters)

        }
        return os

    }
    /**
     * Process Workflow Data Descriptions
     * @param wdf The WorkflowDataDescription Object thich Describes a File for an External Workflow
     * @param DataParams The Params for The Data Extraction
     * @return An Output stram with the CSV Content which can be Written to a File
     */
    OutputStream getBinFile(WorkflowDataDescription wdf, Map DataParams) {
        Map params
        String q = extendHQLwithUserRights(wdf.getHqlQuery())
        log.debug("Query " + q)
        if (DataParams != null) {
            Map PFQ = wdf.getParametersForQuery()
            boolean check = this.checkDataparams(DataParams, PFQ)
            if (!check)
                return null
            params = DataParams
        } else {

            params = [:]
        }

        log.debug("params "+ params)
        def a = Dataset.executeQuery(
                q, params,[readOnly:true,max: 1,timeout:600]
        )



        def Stuff = a.get(0)



        OutputStream os = new ByteArrayOutputStream(Stuff.length)
        os.write(Stuff)

        return os

    }
    /**
     * Process Workflow Data Descriptions
     * @param wdf The WorkflowDataDescription Object thich Describes a File for an External Workflow
     * @param DataParams The Params for The Data Extraction
     * @return An Output stram with the CSV Content which can be Written to a File
     */
    OutputStream getParamToFile(WorkflowDataDescription wdf, Map DataParams) {
        Map params

        if (DataParams != null) {
            Map PFQ = wdf.getParametersForQuery()
            boolean check = this.checkDataparams(DataParams, PFQ)
            if (!check)
                return null
            params = DataParams
        } else {

            params = [:]
        }


        if(params.size()!=1)
            log.error("More Than one parameter Exporting the First that i find..... !!!!")
        def a = params.get(params.keySet().first())

        OutputStream os = new ByteArrayOutputStream()


        if (!a.isEmpty()) {
            List fieldstoExport = wdf.getOutputFields()
            List res = RedoQueryresultForExport(a,fieldstoExport)

            Map parameters = [ "separator": ",", "header.enabled":wdf.headers,"quoteCharacter":'\u0000' ]
            print res
            Map labels = [:]
            fieldstoExport.each {
                labels.put(it, it)
            }
            print labels
            print "headers " + wdf.headers


            exportService.export('csv', os, res, fieldstoExport, labels, [:], parameters)

        }

        return os

    }

    /**
     * Add AccessRestriction to HQL Query
     * @param hql The hql Query to Query the Database
     * @return The Modified HQL wich will check the Dataset for Userrights
     */
    String extendHQLwithUserRights(String hql) {
        User u = springSecurityService.getCurrentUser()
        String out = ""

        def UserrightsAppendCondition = ""
        if(hql.contains("dataset ds ") ||hql.contains("datasets ds ") ){
            if(u)
                UserrightsAppendCondition = UserrightsAppendCondition.concat(" and (ds.shared =true or ds.owner = " + u.id + ") ")
            else
                UserrightsAppendCondition = UserrightsAppendCondition.concat(" and ds.shared =true and ds.annon =true ")
        }
        if(hql.contains("importInfo ii ") ||hql.contains("importInfos ii ") ){
            if(u)
                UserrightsAppendCondition = UserrightsAppendCondition.concat(" and (ii.shared =true or ii.owner = " + u.id + ") ")
            else
                UserrightsAppendCondition = UserrightsAppendCondition.concat(" and ii.shared =true and ii.annon =true ")
        }
        int groupByIndex = hql.indexOf("group by", hql.indexOf("where"))
        if (groupByIndex >= 0)
            out = hql.subSequence(0, groupByIndex) + UserrightsAppendCondition + hql.subSequence(groupByIndex, hql.length())
        else
            out = hql + UserrightsAppendCondition

        return out
    }

    /**
     * Repack the Results for Export with Export Service Plugin
     * @param results The Results of the HQL Query
     * @param fieldstoExport The Filed that should be Exportet
     * @return A Lust of Maps as Pseudo Objects which can bex exported by the ExportSerive
     */
    List RedoQueryresultForExport(List results, List fieldstoExport) {

        List res = []
        if (fieldstoExport.size() > 1) {
            results.each {
                def mappe = [:]
                it.eachWithIndex { thing, index ->

                    mappe.put(fieldstoExport[index], thing)


                }
                res.add(mappe)
            }
        } else {
            results.each {
                it
                def mappe = [:]

                mappe.put(fieldstoExport[0], it)
                res.add(mappe)

            }
        }
        return res
    }

    /**
     * Checks if Param Datatypes are Right and Tight
     * @param DataParams The Data Params which will be applied to this query
     * @param PFQ The Params wich are Needed for this query
     * @return boolean if Fale there is an Data error
     */
    boolean checkDataparams(Map DataParams, Map PFQ) {
        boolean out = true

        PFQ.each { fieldname, inputParameter ->

            if (!DataParams.containsKey(fieldname)) {
                log.debug("nonfound key " + fieldname)
                out = false
            } else {
                def data = DataParams.get(fieldname)
                log.debug(data)

                if (!(data.class.name.equals(inputParameter.getDataType())||data.class.simpleName.equals(inputParameter.getDataType()||(data.class.simpleName.equals("ArrayList") && inputParameter.getDataType().equals("List")) )  )) {
                    try {

                        if (inputParameter.getDataType() in [Dataset.name, Patient.name, Gene.name, Sample.name, Long.name, Study.name]) {
                            data = Long.parseLong(data)
                            DataParams.put(fieldname, data)
                            log.debug("to Long")
                        } else if (inputParameter.getDataType().equals(Integer.name)) {
                            data = Integer.parseInt(data)
                            DataParams.put(fieldname, data)
                            log.debug("to Integer")

                        } else if (inputParameter.getDataType().equals(String.name)) {
                            data = data.toString()
                            DataParams.put(fieldname, data)
                            log.debug("to String")

                        }
                    } catch (Error e) {
                        log.debug("There are error which are uncatched execptions while Evaluating if the Parameters Fit for the Query :"+ e.message)

                    }
                    if (!(data.class.name.equals(inputParameter.getDataType())||data.class.simpleName.equals(inputParameter.getDataType())||(data.class.simpleName.equals("ArrayList") && inputParameter.getDataType().equals("List")) )) {
                        log.debug("Wrong Datatype " + DataParams.get(fieldname).class.name + " " + inputParameter.getDataType())
                        out = false
                    }
                }
            }
        }

        return out
    }
    /**
     * Get Unique Values For a Field in the Database
     * @param attriubute The Attribute "DomainClass.AttributeName" as String
     * @return List of Strings that a Field contains an the people can view
     */
    List<String> GetUniqueValuesForField(String attriubute){
        //HQL Query

        if(attriubute.startsWith("select ")){

            //TODO Implement for real query !
            log.debug("cannot execute select Query feature not Implemented!")

            return null
        }else{
            String[] things= attriubute.split("\\.")
            if(things.length == 2){
                //Structural
                String HQLQuery
                if(things[0].equals(Study.simpleName )){

                    HQLQuery ="select distinct stud."+ things[1]+" from "+things[0]+" as stud Join stud.patients p join p.samples s join s.datasets ds"

                }
                else if(things[0].equals( Patient.simpleName )){

                    HQLQuery ="select distinct p."+ things[1]+" from "+things[0]+" as p join p.samples s join s.datasets ds"

                }
                else if(things[0].equals(Sample.simpleName )){

                    HQLQuery ="select distinct s."+ things[1]+" from "+things[0]+" as s join s.datasets ds"

                }
                else if(things[0].equals(DataSet.simpleName )){

                    HQLQuery ="select distinct ds."+ things[1]+" from "+things[0]+" as ds"

                }
                //Data Data
                else if(!things[0].equals("DataSet") && things[0].startsWith("Data") && ! things[0].endsWith("Annotation") ){

                    HQLQuery ="select distinct DataObject."+ things[1]+" from "+things[0]+" as DataObject join DataObject.dataset ds"

                }
                else if(things[0].equals(ClinicalInformation.simpleName )){


                    HQLQuery ="select distinct cd."+ things[1]+" from "+things[0]+" as cd join cd.ImportInfo ii"

                }else
                    return null
                log.debug(HQLQuery)
                //HQLQuery += " where true "
                def executeableQuery = this.extendHQLwithUserRights(HQLQuery)
                log.debug(executeableQuery)
                // THIS is a Security relevant HQL executer
                List a = Dataset.executeQuery(
                        executeableQuery, [readOnly:true]
                )

                return a

            }
            else{
                log.debug("Select Query Failed was Malformed . Conventions is DomainClass.AttributeName  Given: " +attriubute )

                return null
            }
        }


    }

}
