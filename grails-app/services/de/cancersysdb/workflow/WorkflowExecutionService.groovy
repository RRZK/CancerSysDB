package de.cancersysdb.workflow

import de.cancersysdb.User
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import org.apache.commons.io.FileUtils

/**
 * This Service Executes Workflows.
 */
@Transactional
class WorkflowExecutionService {

    def grailsApplication
    WorkflowDataService workflowDataService
    SpringSecurityService springSecurityService
    String ProcessedStuffPath=null

    String getProcecessedWorkflowPath(){
        if(ProcessedStuffPath == null){
            ProcessedStuffPath = grailsApplication.getConfig().cancersys.config.dataFilepath.toString()+("workflows")

            File f = new File(ProcessedStuffPath)

            if (!f.exists())
                f.mkdir()

            if (!f.canWrite())
                log.error("Error could not Initiate the creation of the WorkflowResultDirectory")
        }
        return ProcessedStuffPath
    }



    /**
     *
     * @param concept
     * @param Data
     * @param inputParameterUnique
     * @return Map [Status: "Succsessful", "ProcessedWorkflow": ProcessedWorkflow] OR [Status: "Failed", "Errormessage":"This Workflow Failed", "InternalErrormessage":" Not Enough X" ]
     */
    Map processWorkflow(ConceptualWorkflow concept, Map Data, Map inputParameterUnique){
        //Create Processed Workflow
        Map out = [:]

        log.debug("Processing Started")
        log.debug("Parameteres for Process"+inputParameterUnique)
        ProcessedWorkflow pw = new ProcessedWorkflow()
        pw.start = new Date()
        pw.description = concept.plainDescription
        pw.executor = (User) springSecurityService.getCurrentUser()
        pw.concept = concept
        //TODO Hier besseres Marshalling
        pw.callerParameters = (Data as JSON).toString()
        pw.uniqueCallerParameters = (inputParameterUnique as JSON).toString()

        //Create Sandbox directory
        String path = createWorkflowSandbox(pw.uuid)
        pw.resultFileLocation = path
        pw.outputFiles = [:]

        //Execute the ExecutionWorkflows
        List ews = concept.getExecWorkflows()
        Map outputFiles = [:]

        //Copyscript to directory
        ews.each { ew ->
            String sourceDir = getWorkflowMasterDir(ew)
            //Retrive data and write to File
            fillWorkspacedir(path, sourceDir, ew, (Map) Data[ew.name])
        }

        boolean tsallgoodman = true
        for (ExecWorkflow ew in ews) {

            //Execute Commands
            Integer error


            for (String it in ew.excecutionCommands) {
                def env = System.getenv()
                def envlist = []
                env.each() { k,v -> envlist.push( "$k=$v" ) }

                def excec = it
                log.debug("path: " + path)
                log.debug("excec: " + excec)

                def sout = new StringBuilder(), serr = new StringBuilder()
                def process = excec.execute(envlist, new File(path))
                //def proc = excec.execute([] ,new File(path))
                process.consumeProcessOutput(sout, serr)
                process.waitFor()




                error = process.exitValue()
                log.debug("error " + error)
                if (error != 0) {
                    log.error("Process exited with Error " + error)
                    log.error(sout)
                    log.error(serr)
                    out.put("status","failed")

                    out.put("Errormessage","An Error occurred executing the Script")
                    out.put("InternalErrormessage","Process exited with Error "+ error+"\n sout : \n"+ sout + "\n serr: \n"+serr)

                    tsallgoodman = false

                    break
                }

            }

            ew.outputFiles.each {
                k, v ->

                    outputFiles.put(k, v)

            }
            //unsuccessful -> exit
            if (!tsallgoodman)
                break

        }
        //Success
        if (tsallgoodman) {
            boolean good = checkoutput(path, outputFiles)
            tsallgoodman = good
            if (tsallgoodman) {
                outputFiles.each {
                    k, v ->
                        pw.outputFiles.put(k, path + "/" + k)
                }
            }else{
                out.put("status","failed")

                out.put("Errormessage","An Internal Error occurred with the Workflow, please contact Administrator")
                out.put("InternalErrormessage","There is an Error on Filesystem Level")


            }

        }
        //Failure
        if (!tsallgoodman) {
            out.put("status","failed")

            pw.discard()
            removeWorkflowSandbox(path)
            return out
        } else {
            try{
                pw.save(failOnError: true)
                out.put("status","successful")

                out.put("ProcessedWorkflow",pw)
            } catch (Exception e){
                out.put("status","failed")

                out.put("Errormessage","An Internal Error occurred with the Workflow, please contact Administrator")
                out.put("InternalErrormessage",e.toString()+"\n"+pw.errors.toString() )
                // @return Map [Status: "successful", "ProcessedWorkflow": ProcessedWorkflow] OR [Status: "Failed", "Errormessage":"This Workflow Failed", "InternalErrormessage":" Not Enough X" ]

            }

        }
        return out
    }
    /**
     *
     * @param s
     * @param map
     * @return
     */
    protected boolean checkoutput(String s, Map map) {
        boolean out = true
        map.each {
            k, v ->

                if (out) {
                    //Simple Check if Output Exists
                    File f = new File(s + "/" + k)
                    if (!f.exists())
                        out = false
                    //TODO Process Filetypes
                }
        }


    }


    private String createWorkflowSandbox(String uuid) {


            def newpath = getProcecessedWorkflowPath() + "/" + uuid

            File f = new File(newpath)


                f.mkdir()

        return newpath

    }

    private boolean removeWorkflowSandbox(String path) {

        if (path.contains(getProcecessedWorkflowPath())) {
            File p = new File(path)
            if (!p.isDirectory())
                return false
            p.deleteDir()

            return p.exists()
        }
        return false

    }
    /**
     * Write All Data to The directory for Excecution
     * @param Path The Path to deploy this special execution Thing to
     * @param DirOfSources This is the Folder where the File Sources are Stored
     * @return
     */
    String fillWorkspacedir(String Path, String DirOfSources, ExecWorkflow ew, Map InputData) {
        File sources = new File(DirOfSources)
        log.debug(DirOfSources)
        log.debug("isDirectory " + sources.isDirectory())
        if (!sources.isDirectory())
            return "error"

        File d = new File(Path)
        FileUtils.copyDirectory(sources, d)

        ew.inputData.each { wdf->
            if(wdf){
                log.debug("Inputdatastructure="+InputData)
                ByteArrayOutputStream os =null

                if(wdf) {
                    if(wdf.parameterToFile){
                        log.debug("parameterToFile")
                        os = workflowDataService.getParamToFile(wdf, InputData[wdf.name])
                    }else if(wdf.binaryFile){
                        log.debug("Binary")
                        os = workflowDataService.getBinFile(wdf, InputData[wdf.name])
                    }else{

                        log.debug("CSV")
                        os = workflowDataService.getCSVFile(wdf, InputData[wdf.name])
                    }
                }
                if (os) {
                    FileOutputStream f = new FileOutputStream(Path + "/" + wdf.getOutputName())
                    os.writeTo(f)
                } else
                    log.error("Stream is Empty")
            }
        }


    }

    def getWorkflowMasterDir(ExecWorkflow ew) {
        def basepath = grailsApplication.getConfig().cancersys.config.dataFilepath.toString() + "/WorkflowMasters/" + ew.getDataPath()
        log.debug(basepath)

        return basepath


    }


}
