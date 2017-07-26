package de.cancersysdb

import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.workflow.ConceptualWorkflow
import de.cancersysdb.workflow.ExecWorkflow
import de.cancersysdb.workflow.ProcessedWorkflow
import de.cancersysdb.workflow.WorkflowDataService
import de.cancersysdb.workflow.WorkflowExecutionService
import de.cancersysdb.workflow.WorkflowInputParameter
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.json.JSONObject

@Secured(closure = {
    //Check If Portal or Logged in!
    ctx.getBean(GrailsApplication).config.cancersys.config.systemType.toString().toLowerCase().equals("public") || ctx.getBean(SpringSecurityService).isLoggedIn()
})
class ShowcaseController {
    /**
     * TODO Remove unused Functions!
     * TODO Refactor Move Functionality to Service
     * TODO Comment
     */

    GrailsApplication grailsApplication
    WorkflowExecutionService workflowExecutionService
    WorkflowDataService workflowDataService
    GeneService geneService
    def springSecurityService


    def index() {
        def user = springSecurityService.getCurrentUser()
        Map processedStuff =[:]
        def pws = []
        if(user) {
            pws.addAll( ProcessedWorkflow.findAllByExecutor(user))
        }
        //pws.addAll(ProcessedWorkflow.findAllByExecutorIsNull())
        if(pws){
            pws.each { it->
                if(!processedStuff.containsKey(it.conceptId))
                    processedStuff.put(it.conceptId,[it])
                else{
                    processedStuff.get(it.conceptId).add(it)
                }
            }
        }

        List cwfs =  ConceptualWorkflow.getAll()


        render (view: "overview",model: [ conceptualWorkflows:cwfs,  ps:processedStuff ])
    }
/*
    def workflowtest() {
        workflowExecutionService.processWorkflow1()
    }*/

/*
    def showcaseConstruct() {
        def WorkflowName
        if(params.containsKey("showcase")){
            WorkflowName= params.showcase
            log.debug( "Showcase Parameters "+ params.showcase)
            log.debug("WorkflowName"+ WorkflowName)
            ExecWorkflow ew  = ExecWorkflow.findByName(WorkflowName)
            log.debug("execution Workflow"+ ew)
            List DatatoInput = []
            log.debug( "collecting Data to Input")
            log.debug(""+ew.inputData)
            ew.inputData.each { inputData->
                log.debug( "inputData=$inputData")
                inputData.parametersForQuery.each { Fieldname, it ->
                    if(!DatatoInput.contains(it))
                        DatatoInput.add(it)
                }

            }
            log.debug( "Data collected")
            log.debug( "DatatoInput="+DatatoInput)



            render (view: "construct",model: [inputDataFields: DatatoInput, execWorkflow:ew ])
        }else{

            List ewfs =  ExecWorkflow.getAll()
            render (view: "choose",model: [ execWorkflows:ewfs ])

        }


    }
*/

    def WorkflowConstruct() {
        def WorkflowName
        Map predefinedValues=[:]
        if(params.containsKey("showcase")){
            WorkflowName= params.showcase
            log.debug( "params.showcase="+params.showcase)
            log.debug( "WorkflowName=$WorkflowName")
            ConceptualWorkflow cw  = ConceptualWorkflow.findBySourceIdentifier(WorkflowName)
            List DatatoInput = []
            log.debug( "collecting Data to Input")

            cw.execWorkflows.each { ew ->
                ew.inputData.each { inputData ->
                    if(inputData){
                        inputData.parametersForQuery.each { Fieldname, WorkflowInputParameter it ->
                            print DatatoInput
                            if (!DatatoInput.contains(it)) {
                                DatatoInput.add(it)
                                if(it.getPredefinedValuesQuery() && !"".equals(it.getPredefinedValuesQuery()) ){
                                    List values= workflowDataService.GetUniqueValuesForField(it.getPredefinedValuesQuery())
                                    if(values)
                                        predefinedValues.put(it,values)

                                }

                            }

                        }
                    }

                }
            }
            log.debug( "Data collected")
            log.debug( "DatatoInput="+DatatoInput)
            render (view: "constructConcept",model: [inputDataFields: DatatoInput, conceptualWorkflow:cw, predefinedValues:predefinedValues ])
        }else{
            List cwfs =  ConceptualWorkflow.getAll()
            render (view: "overview",model: [ conceptualWorkflows:cwfs ])
        }

    }

    def showcaseExec() {
        def tempewf=  params.get("conceptualWorkflow")

        ConceptualWorkflow  cw = ConceptualWorkflow.get(tempewf)
        Map constructParameters=[:]
        List dataInput =[]
        Map AllparamsMap = [:]
        Map ParamUnique = [:]

        //Monst ... not good
        cw.execWorkflows.each { ExecWorkflow ew ->
            log.debug("ew input data" +ew.inputData)
            ew.inputData.each { inputData ->

                if(inputData!= null){
                    log.debug("ew input"+inputData+ "  "  +inputData.name + " "+inputData.parametersForQuery.size())
                    inputData.parametersForQuery.each { Fieldname, WorkflowInputParameter it ->

                        //if (!dataInput.contains(it)) {
                            dataInput.add(it)
                            //print it.id
                            //TODO!
                            //Parse Parameter and exception for gene
                            def inputValue
                            //If Parameter is Gene
                            if(it.dataType.equals("Gene")){
                                def identifier = params.get("identifier-"+it.id )
                                def type = params.get("Type-"+it.id )
                                Gene gene = geneService.getGeneByIdentifierAndType(identifier, type)
                                inputValue = gene

                            }else if(it.dataType.equals("List")){
                                print params
                                def value = params.get("value-"+it.id )
                                def Seperator = params.get("seperator-"+it.id )

                                if(Seperator.equals("New Line"))
                                    inputValue = value.split("\n").collect {it.toString().trim()}.asList()
                                else
                                    inputValue = value.split(Seperator).collect {it.toString().trim()}.asList()

                            }
                            else//Normal Value
                                inputValue = params.get(it.id + "")

                            log.debug( "inputValue="+inputValue)
                            log.debug( "inputData.name="+inputData.name)
                            if (!constructParameters.containsKey(inputData.name))
                                constructParameters.put(inputData.name, [:])
                            def ParamMap = constructParameters.get(inputData.name)

                            ParamMap.put(Fieldname, inputValue)
                            ParamUnique.put(it.name, inputValue)

                        //}
                    }
                }
            }
            AllparamsMap.put(ew.name, constructParameters)

        }
        log.debug("calling Execution")
        log.debug("UniqueParams"+ParamUnique)
        log.debug("AllParams"+AllparamsMap)

        Map processed =  workflowExecutionService.processWorkflow(cw,AllparamsMap,ParamUnique)
        def user = springSecurityService.getCurrentUser()
        log.debug("Result "+processed.get("status"))
        if(processed.get("status").equals("successful")){
            ProcessedWorkflow pw = (ProcessedWorkflow) processed.get("ProcessedWorkflow")


            if(pw){
                if(pw.executor == null || pw.executorId.equals(user.id)){
                    def prams = JSON.parse( pw.getUniqueCallerParameters())
                    def oprams =[:]
                    prams.each{ key,val->
                        if(val instanceof JSONObject)
                        {
                            if(val.get("class").endsWith("Gene"))

                                oprams.put(key,Gene.get(val.get("id")))
                        }else oprams.put(key,val)
                    }
                    render (view: "showProcessedWorkflow",model: [ processedWorkflow:pw, parameters:oprams ])
                }else
                    response.sendError(403,"forbidden")
            }else
                response.sendError(500,"Malfunctioning Deep!")

        }else{
            Map params= [:]
            AllparamsMap.each {
                key,value->
                    value.each {
                        a,b->
                            b.each{
                                k,v->
                                    params.put(k,v)

                            }

                    }

            }

            if ( user && user.getAuthorities().any() { it.authority == Role.ADMIN ||  it.authority == Role.MANAGER }){
                render view: "/showcase/noResult", model: ["inputData":ParamUnique,"workflow":cw,"ErrorMessage":processed.get("Errormessage"),"InternalErrormessage":processed.get("InternalErrormessage")  ]
            }else{
                render view: "/showcase/noResult", model: ["inputData":ParamUnique,"workflow":cw,"ErrorMessage":processed.get("Errormessage")]


            }
        }


    }

    def ShowresultFile() {
        if(params.Uuid && params.filename){
            ProcessedWorkflow pw = ProcessedWorkflow.findByUuid(params.Uuid)
            pw.getOutputFiles().containsKey(params.filename)

            File f =  new File(pw.getOutputFiles().get(params.filename))

            def type=""
            if(params.filename.endsWith("csv")){
                type = "text/plain"
            }
            else if(params.filename.endsWith("svg")) {

                type = "image/svg+xml"
            }else if(params.filename.endsWith("html")) {

                type = "text/html"
            }else{

                type = "application/octet-stream"
            }
            render(file: f, fileName: f.name, contentType: type)
        }
        else
            render status: "404", text: "Dataset or File not found"
    }

    def showResFileByName(String Uuid, String Filename) {

        if(Uuid && Filename){
            ProcessedWorkflow pw = ProcessedWorkflow.findByUuid(Uuid)
            pw.getOutputFiles().containsKey(Filename)
            log.debug("Requested File : "+Filename)
            File f =  new File(pw.getOutputFiles().get(Filename))

            def type=""
            if(Filename.endsWith("csv")){
                type = "text/plain"
            }
            else if(Filename.endsWith("svg")) {

                type = "image/svg+xml"
            }else if(Filename.endsWith("html")) {

                render text: f.getText(), contentType:"text/html", encoding:"UTF-8"
                return
            }
            render(file: f, fileName: f.name, contentType: type)
        }
        else
            render status: "404", text: "Dataset or File not found"
    }

    def showProcessedWorkflow(){
        def user = springSecurityService.getCurrentUser()

            def pw= ProcessedWorkflow.findByUuid(params.processedWorkflow)

            if(pw && (user || (GrailsApplication.hasProperty("config")&&GrailsApplication.config.cancersys.config.systemType.equals("public")))){

                  def prams = JSON.parse( pw.getUniqueCallerParameters())
                    def oprams =[:]
                    prams.each{ key,val->
                        if(val instanceof JSONObject)
                        {
                            if(val.get("class").endsWith("Gene"))

                                oprams.put(key,Gene.get(val.get("id")))
                        }else oprams.put(key,val)
                    }
                    render (view: "showProcessedWorkflow",model: [ processedWorkflow:pw, parameters:oprams])

/*                }
                else
                    response.sendError(403,"forbidden")*/
            }
            else {
                if(!(user || (GrailsApplication.hasProperty("config")&&GrailsApplication.config.cancersys.config.systemType.equals("public"))))
                    response.sendError(403, "forbidden")
                else
                    response.sendError(404, "not found")
            }
    }
}