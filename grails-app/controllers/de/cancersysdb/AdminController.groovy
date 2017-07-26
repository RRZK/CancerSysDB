package de.cancersysdb

import de.cancersysdb.Import.GeneAliasImportService
import de.cancersysdb.workflow.ConceptualWorkflow
import de.cancersysdb.workflow.WorkflowManagementService
import grails.plugin.springsecurity.annotation.Secured

import java.awt.*

@Secured(value=["hasRole('ROLE_ADMIN')"])
class AdminController {
    DatabaseDescriptionService databaseDescriptionService
    WorkflowManagementService workflowManagementService
    GeneAliasImportService geneAliasImportService
    /**
     * Show the Test HQL Site
     * @return
     */
    def testHQL(){
        Map description = databaseDescriptionService.getDataClassesInformation()

        render view: "execQuery", model:[description:description]

    }

    /**
     * Execute HQL-Query in read-only this is supposed to give admins the ability to run non destructive HQL Queries
     * This function is supposed to support people in developing Requests for workflows
     * @param HQLQuery The HQL Query Tobe Tested
     * @return Result which can is Rendered in the Frontend
     */
    def execHQL(String HQLQuery){

        try {
            def message =""
            //Timeout -> 10 Minutes
            def result = Dataset.executeQuery( HQLQuery,[:],[readOnly:true,timeout:600])

            if(result.empty)
                message = "Empty result!"

            render view: "resultQuery" , model:[result:result,message:message,query:HQLQuery]

        }catch ( Exception e ){

            render view: "resultQuery" , model:[error:e.message]
        }


    }

    def clinicalDataStats(){
        Map clinicaldescription =[:]
        clinicaldescription = databaseDescriptionService.describeAllClinicalDataNames()

        render view: "clinicalDataStats", model:[clinicaldescription:clinicaldescription]
    }
    def clinicalDataKeys(){
        Map clinicaldescription =[:]
        clinicaldescription = databaseDescriptionService.showAllClinicalKeys()

        render view: "clinicalDataKeys", model:[clinicaldescription:clinicaldescription]
    }
    def clinicalDataValues(String ClinicalKey){

        List clinicaldescription =[]
        if(!ClinicalKey)
            ClinicalKey =params["ClinicalKey"]
        if(ClinicalKey)
            clinicaldescription = databaseDescriptionService.describeClinicalKey(ClinicalKey)

        render view: "clinicalDataValues", model:[clinicaldescription:clinicaldescription, ClinicalKey:ClinicalKey]
    }

    def importWorkflow() {

            render view: "UploadWorkflow"
    }

    def workflows() {

        render view: "workflows"
    }

    def tools(){

        render view: "tools"
    }

    def toolsDownload(){


        File f =  new File(grailsApplication.parentContext.getResource("").file.toString() + "/Tools/"+params.filename)
        render(file: f, fileName: f.name, contentType: "text/plain")

    }


    def uploadWorkflow() {
        def Metadatafile
        def ZipFile
        def errors = []
        if (request.getFile('Metadatafile')) {

            Metadatafile = request.getFile('Metadatafile')

        }else
            errors.add("MetadataFile Not Uploaded")
        if (request.getFile('ZipFile')) {

            ZipFile = request.getFile('ZipFile')

        }else
            errors.add("Zipfile Not Uploaded")

        //TODO Error for the shit!
        def res
        def metastart = 0
        String metafilename= Metadatafile.getOriginalFilename().toString()

        String Zipfilename= ZipFile.getOriginalFilename().toString()

        if(!metafilename.endsWith("json") )
            errors.add("metafilename must end with Json")
        if(!Zipfilename.endsWith("zip") )
            errors.add("Zipfile must end with zip")

        if(metafilename.contains("/"))
            metastart = metafilename.lastIndexOf("/")
        String OrgfilenameMeta = metafilename.substring(metastart,(Metadatafile.getOriginalFilename().size())-(".json".size()))

        def Zipstart = 0
        if(Zipfilename.contains("/"))
            Zipstart = Zipfilename.lastIndexOf("/")

        String OrgfilenameZip = Zipfilename.substring(Zipstart,(ZipFile.getOriginalFilename().size())-(".zip".size()))

        if(!OrgfilenameMeta.equals(OrgfilenameZip))
            errors.add("The Zip and Json file must have the same Names exept for their endings!"+OrgfilenameMeta+" VS " +OrgfilenameZip)


        if(!errors.empty)
            render view: "WorkflowUploadResult", model:[UploadSuccessful:false,errors: errors]
        else{
            res = workflowManagementService.createWorkflowFromUpload(OrgfilenameMeta,Metadatafile,ZipFile)
        }

        if(res instanceof ConceptualWorkflow)
            render view: "WorkflowUploadResult", model:[UploadSuccessful:true,result:createLink(controller:  "conceptualWorkflow", action: "show", id:res.id)]
        else if(res != null && !res.empty){
            res.addAll(errors)
            render view: "WorkflowUploadResult", model:[UploadSuccessful:false,errors: res]
        }
        else if((res == null || res.empty) && !errors.empty )
            render view: "WorkflowUploadResult", model:[UploadSuccessful:false,errors: errors ]
        else
            render view: "WorkflowUploadResult", model:[UploadSuccessful:false,errors: ["Radio Blabla: " +res+" "+" "+ errors +" is List "  +res.class.toString()]]

    }


    //Experimental DEBUG

    def getGeneAliasListAsCSV(){


        render text: geneAliasImportService.printGeneAliasListAsCSVCandidate()

    }


}
