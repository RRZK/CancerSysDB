package de.cancersysdb.workflow

import com.google.gson.stream.MalformedJsonException
import de.cancersysdb.FileService
import de.cancersysdb.Role
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import de.cancersysdb.UserDetail
import de.cancersysdb.UserRole
import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONElement

import java.nio.charset.MalformedInputException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

//TODO Documentation
@Transactional
class WorkflowManagementService {
    String workflowStoragePath
    def grailsApplication
    FileService fileService
    static Map NameToType = [

            "Long"  : Long.class.name,
            "String": String.class.name,
            "Float" : Float.class.name,
            "Intger": Integer.class.name,
            "Double": Double.class.name,

    ]

    boolean CheckWorkflowSources(ExecWorkflow ew) {
        boolean vaild = true
        ew.Files.each {

            File f = new File(workflowStoragePath + "/" + it)
            if (!f.exists())
                vaild = false

        }
        if (!vaild)
            return vaild

        ew.verificationCommands.each {
            String toExcec = workflowStoragePath + "/" + it


            if (toExcec.execute().exitValue() == 0)
                vaild = false

        }
        return vaild

    }

    /**
     * Parse JSON File and create Workflow
     * @param nameShowcase The Name of the Workflow
     * @return String with Errors or True when it worked
     */

    def createWorkflowFromJSON(String nameShowcase) {
        log.info(nameShowcase)
        //TODO Method to Big refactor!
        /*def todel = ConceptualWorkflow.findBySourceIdentifier(nameShowcase)
        if (!!todel){
            delteConceptualWorkflow(todel)
        }*/

        def showcaseWF = ConceptualWorkflow.findBySourceIdentifier(nameShowcase)
        
        //log.debug( nameShowcase + " showcaseWF -> " +showcaseWF  )

        if (!showcaseWF) {
            log.info("Importin " + nameShowcase + "  ")
            def jsonPath = grailsApplication.parentContext.getResource("").file.toString() + "/data/Workflows/" + nameShowcase + ".json"

            log.info("jsonPath?" + jsonPath)
            JSONElement res = JSON.parse(new FileReader(jsonPath))
            
            log.info("jsonparse")
            if (!res instanceof Map)
                throw new MalformedJsonException("Base thing must be an Asociative Array")

            ConceptualWorkflow cw = new ConceptualWorkflow(plainDescription: res.get("plainDescription"), sourceIdentifier: res.get("sourceIdentifier"))
            if (res.get("longDescription"))
                cw.setLongDescription(res.get("longDescription"))

            if(res.containsKey("outputFilesOrder")){
                def temp = res.get("outputFilesOrder")
                temp.each{
                    cw.addToOutputFilesOrder(it)
                }
            }else
                cw.outputFilesOrder= null

            //Read in Variables from the InputParameters File
            Map InputParameters = [:]
            def ip = res.get("InputParameters")
            
            log.info("step1")
            
            if (ip) {
                log.info("ip")
                if (!ip instanceof List)
                    throw new MalformedJsonException("InputParameters Must be A list of MAPs!")

                ip.each { Map it ->
                    def ident = it.get("identifier")

                    def typename = ""
                    if (NameToType.containsKey(it.get("dataType")))
                        typename = NameToType.get(it.get("dataType"))
                    else
                        typename = it.get("dataType")

                    //Is Parameter Optionalw
                    def optional = false
                    if (it.containsKey("optional"))
                        optional = it.get("optional")
                    if (optional)
                        optional = true
                    else
                        optional = false

                    def predefinedValueQuery = ""
                    if (it.containsKey("predefinedValuesQuery"))
                        predefinedValueQuery = it.get("predefinedValuesQuery")
                    WorkflowInputParameter temp = new WorkflowInputParameter(dataType: typename, description: it.get("description"), optional: optional, name: ident,predefinedValuesQuery: predefinedValueQuery)
                    temp.save(failOnError: true)
                    InputParameters.put(ident, temp)

                }
            }

            //Create Executable Part of the  Workflow
            
            log.info("step2")

            cw.execWorkflows = []
            cw.save(failOnError: true,flush: true)

            print cw.errors
            def ewDesc = res.get("execWorkflows")
            if (ewDesc) {
                log.info("ewDesc")
                if (!ewDesc instanceof List)
                    return ["execWorkflows Must be A list of MAPs!"]

                ewDesc.eachWithIndex { Map it, def index ->

                    ExecWorkflow ew = new ExecWorkflow(name: nameShowcase + index)

                    ew.setDescription(it.get("description"))
                    ew.outputFiles = it.get("outputFiles")
                    ew.setExcecutionCommands(it.get("ExcecutionCommands"))
                    ew.setVerificationCommands(it.get("VerificationCommands"))
                    ew.setFiles(it.get("setFiles"))
                    ew.setDataPath(nameShowcase + "/")
                    ew.inputData = []
                    cw.addToExecWorkflows(ew)
                    ew.setConceptualWorkflow(cw)
                    ew.save(failOnError: true,flush: true)


                    //Reconfigure The Input Data
                    it.get("inputData").each { Map inputDate ->
                        Map refinedparametersForQuery = [:]
                        Boolean parameterToFile=false
                        if(inputDate.containsKey("parameterToFile") && inputDate.get("parameterToFile"))
                            parameterToFile=true

                        if (inputDate.containsKey("parametersForQuery")) {
                            inputDate.get("parametersForQuery").each { key, identifiername ->
                                def temp = InputParameters.get(identifiername)
                                if (!temp || temp == null)
                                    throw new Exception("parametersForQuery have wrong Naming " + identifiername.toString() + " not found!")
                                refinedparametersForQuery.put(key, temp)
                            }
                        }
                        String query = ""

                        if(!parameterToFile){
                            //This is an Exeption so Querys can be formulated Multi Line in list....
                            if(!inputDate.containsKey("hqlQuery"))
                                throw new Exception("parameterToFile must be set and true or hqlQuery must be Given!")

                            if (inputDate.get("hqlQuery") instanceof List) {

                                inputDate.get("hqlQuery").each {
                                    if (query == "" || query.endsWith(" ") || it.startsWith(" "))
                                        query = query.concat(it)
                                    else
                                        query = query.concat(" " + it)

                                }
                            } else
                                query = inputDate.get("hqlQuery")
                            this.checkQuery(query)
                        }

                        Boolean binfile =false
                        if(inputDate.containsKey("binaryFile") && inputDate.get("binaryFile"))
                            binfile = true
                        Boolean headers =false
                        if(inputDate.containsKey("headers") && inputDate.get("headers"))
                            headers=true

                        WorkflowDataDescription wdf = new WorkflowDataDescription(hqlQuery: query, workflow: ew, outputName: inputDate.get("outputName"), binaryFile: binfile, name: inputDate.get("name"), headers:headers, parameterToFile: parameterToFile)
                        wdf.setOutputFields(inputDate.get("OutputFields"))

                        wdf.setParametersForQuery(refinedparametersForQuery)
                        ew.addToInputData(wdf)
                        wdf.setWorkflow(ew)
                        wdf.save(failOnError: true)

                    }

                    ew.save(failOnError: true)
                }
            }else
                return ["No execWorkflows given"]
            log.info("Imported Workflows from JSON finished")
            
            
            //Try to Import the Zipped Dependencies
            try {
                log.info("unzip")
                log.info("Started Importing Files")

                def zup = grailsApplication.parentContext.getResource("").file.toString() + "/data/Workflows/" + nameShowcase + ".zip"
                def MasterPath = grailsApplication.getConfig().cancersys.config.dataFilepath.toString() + "/WorkflowMasters"

                File tempf = new File(MasterPath + "/" + nameShowcase + "/")
                if (!tempf.exists()){
                    tempf.mkdir()
                    log.info("make dir")
                }
                    


                unzip(zup, tempf.absolutePath)

                log.info("Importing files finished")
                cw.save(failOnError: true)
                return cw
            }
            catch (def e) {
                def errors= [
                        "failing to import",
                        e.class.toString() + "   " + e.metaClass.toString(),
                        e.message

                ]

                errors.each {log.info(it)}


                cw.execWorkflows.each { exw ->
                    exw.inputData.each {
                        wdf ->

                            wdf.delete()

                    }
                    exw.delete()
                }
                return errors
            }
        }
        else{
            log.info("There allready is the showcaseWF : "+ showcaseWF.toString())
            //return ["There allready is the showcaseWF : "+ showcaseWF.toString()]
            //return ["return"]
        }
    }

    /**
     *
     * @param Name The name of the Workflow
     * @param Metafile The Json Description file of the Workflow
     * @param ZipFile The Zipfile containing the Scripts for execution
     * @return Messaages Messages about the Imprt and its Failures!
     */
    def createWorkflowFromUpload(String Name,Metafile, ZipFile) {
        def showcaseWF = ConceptualWorkflow.findBySourceIdentifier(Name)
        String MetaFilename
        String ZipFilename
        List<String> errors = []

        if (showcaseWF)
            return ["Workflow with Identifier "+Name+" allready exists"]
        //Upload and Save Files

        try{
        log.debug("Importing Workflow: " + Name + "  ")

        def FilePath = grailsApplication.parentContext.getResource("").file.toString() + "/data/Workflows/"

            MetaFilename = FilePath+ Name+".json"
            def fos= new FileOutputStream(new File(MetaFilename))
            Metafile.getBytes().each{ fos.write(it) }


            ZipFilename = FilePath+ Name+".zip"
            def fos2= new FileOutputStream(new File(ZipFilename))
            ZipFile.getBytes().each{ fos2.write(it) }


        }catch (Exception e){

            errors.add("Internal Error: Data could not be saved, please contact Admin")
            errors.add(e.toString())

            return errors

        }
        def temp = null
        try{
        temp= createWorkflowFromJSON(Name)
        }catch (Exception e){
            return ["Failed "+ e.message]
        }
        if(temp == null)
            return ["Null Returned"]
        else
            return temp


    }
    /**
     * This function checks if the HQL Query is Maluous and can be executed
     * @param query The Query to check!
     * @return void This Crashes if anythin is wrong with the Query
     */
    void checkQuery(String query) {
        String q = query.trim()

        List blockedKeywords = []
        //Packages
        blockedKeywords.addAll(grailsApplication.domainClasses.findAll {
            it.clazz.package.name == "de.cancersysdb.serviceClasses"
        }.class.simpleName)
        blockedKeywords.addAll(grailsApplication.domainClasses.findAll {
            it.clazz.package.name == "de.cancersysdb.geneticViz"
        }.class.simpleName)
        blockedKeywords.addAll(grailsApplication.domainClasses.findAll {
            it.clazz.package.name == "de.cancersysdb.workflow"
        }.class.simpleName)
        //Special Classes

        blockedKeywords.add(User.class.simpleName)
        blockedKeywords.add(UserDetail.class.simpleName)
        blockedKeywords.add(UserRole.class.simpleName)
        blockedKeywords.add(Role.class.simpleName)
        blockedKeywords.add(Role.class.simpleName)

        //Attributes
        //Security Attributes
        //TODO Automatically use the Fields of CsysProtectionInterface
        blockedKeywords.add("owner")
        blockedKeywords.add("annon")
        blockedKeywords.add("shared")

        blockedKeywords.addAll([User.class.simpleName, Role.class.simpleName, UserDetail.class.simpleName, UserRole.class.simpleName, SourceFile.class.simpleName])

        if (!q.toUpperCase().startsWith("SELECT"))
            throw new Exception("Query Must Start with SELECT, no other Queries Allowed :" + q)
        blockedKeywords.each {
            if (q.contains(it))
                throw new Exception("Query Contains forbidden Keyword '$it' :" + q)
        }
        if (!(q.contains("dataset ds") || q.contains("datasets ds") || q.contains("Dataset ds") || q.contains("Datasets ds")

                || q.contains("importInfos ii") || q.contains("importInfo ii")|| q.contains("ImportInfos ii") || q.contains("ImportInfo ii")))
            throw new Exception("Query MUST adress dataset(s) as ds or importInfo(s) as ii :" + q)


    }

    private boolean unzip(String zipFile, String destination) {

        // create a buffer to improve copy performance later.
        byte[] buffer = new byte[2048];

        // open the zip file stream
        InputStream theFile = new FileInputStream(zipFile);
        ZipInputStream stream = new ZipInputStream(theFile);
        String outdir = destination
        try {

            // now iterate through each item in the stream. The get next
            // entry call will return a ZipEntry for each file in the
            // stream
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {

                String outpath = outdir + "/" + entry.getName();
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(outpath);
                    int len = 0;
                    while ((len = stream.read(buffer)) > 0) {
                        output.write(buffer, 0, len);
                    }
                }
                finally {
                    // we must always close the output file
                    if (output != null) output.close();
                }
            }
        }
        finally {
            // we must always close the zip file.
            stream.close()
        }

    }


    def delteConceptualWorkflow(ConceptualWorkflow cw){
        def pws = ProcessedWorkflow.findAllByConcept(cw)

        pws.each {
            it.concept=null
            it.save(failOnError: true)
        }

        cw.delete()
        
        log.info("deleted")

    }
}
