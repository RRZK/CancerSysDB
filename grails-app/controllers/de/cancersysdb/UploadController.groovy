package de.cancersysdb

import de.cancersysdb.Import.MetadataImportService
import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.Import.ContextConstructionService
import de.cancersysdb.Import.FileImportService
import de.cancersysdb.Import.GenericCSVImporterService
import de.cancersysdb.Import.StructuredCSVToDataImporterService
import de.cancersysdb.workflow.ProcessedWorkflow
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.apache.commons.logging.LogFactory

@Transactional
@Secured("isFullyAuthenticated()")
class UploadController {
    /**
     * TODO Refactor
     * TODO Comment
     * TODO describe Rest API
     */
    private static final log = LogFactory.getLog(this)
    FileImportService fileImportService
    SpringSecurityService springSecurityService
    FileService fileService
    ContextConstructionService contextConstructionService
    StructuredCSVToDataImporterService structuredCSVToDataImporterService
    GenericCSVImporterService genericCSVImporterService
    MetadataImportService metadataImportService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    static responseFormats = ['json']
    def index() {
        Map r = [:]

        r["message"] = "Respode Everything"

        respond() as JSON

    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @Transactional
    def CreateDataset( ) {
        def f
        log.debug( "createDataset with Params "+ params)
        ImportProtocol ip
        User u = springSecurityService.getCurrentUser()
        if(!u){
            render(status: 401, text:"Not Logged in!")
            return
        }
        //print params
        if(!params.containsKey("annon") )
            params.annon =true
        if(!params.containsKey("shared") )
            params.shared =true
        ProcessedWorkflow processedWorkflow

        if(!params.containsKey("processedWorkflow")){
            if( params.description ){

                params.processedWorkflow = new ProcessedWorkflow(description: params.description, executor: u )

            }else
                params.processedWorkflow = null



        }
        processedWorkflow = params.processedWorkflow
        List<Sample> smps = []
        Dataset ds =null
        if(params.containsKey("samples")){
            log.debug( "by Samples")
            def samples = params.samples.split(",")
            for( samp in samples){

                Sample smp = null
                if( samp.toString().isLong()){
                    smp = Sample.findById(samp)
                    if(smp){
                        smps.add(smp)
                        continue
                    }
                }
                smp = Sample.findByURI(samp)
                if(smp){
                    smps.add(smp)
                    continue
                }
/*                smp = Sample.findByLabel(samp)
                if(smp){
                    smps.add(smp)
                    continue
                }*/
                smp = Sample.findBySourceIdentifier(samp)
                if(smp){
                    smps.add(smp)
                    continue
                }

            }
            ds = new Dataset(owner: u,annon: params.annon?:true, shared: params.shared?:true, samples: smps )
            if(params.description)
                ds.setNote(params.description)

            ip = new ImportProtocol()
            ip.dataset= ds


        }else if(request.getFile('file')) {
            log.debug( "by File")
            def tempf
            tempf= request.getFile('file')

            if(tempf.getOriginalFilename().toString().endsWith(".json"))
                ip = contextConstructionService.createContextFromJSON( fileService.fileToString(tempf) , u)
            else
                ip = contextConstructionService.createContextFromXML( fileService.fileToString(tempf) , u,params.annon?:true, params.shared?:true)
            ds = ip.getDataset()
            if(!ds) {

                log.debug( ip.errorMessages.toString())
                ip.successful = false
            }

        }
        else{
            log.debug( "Einfachso")

            ds = new Dataset(owner: u,annon: params.annon?:true, shared: params.shared?:true, note:params.description  )
            ip = new ImportProtocol()
            ip.dataset= ds

        }

        //processedWorkflow:processedWorkflow,




        if(ds && ds.save() ){
            def link = createLink(id:ds.id,controller: "Dataset",action: "show", absolute:true).toString()
            Map out = [:]
            out.put("messages",ip.messages)
            out.put("stat","successful")
            out.put("Link",link)
            out.put("DatasetId", ds.id)
            render(status: 200, text:out as JSON )
        }
        else{
            if(ds && ds.hasErrors())
                log.warning("Dataset errors! "+ ds.errors)

            if(ds)
                render(status: 500, text:"Failed to create Dataset "+ds.errors)
            else
                render(status: 500, text:"Failed to create Dataset")
        }

    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @Transactional
    def CreateContext( ) {
        log.debug("CreateContext "+ params)
        User u = springSecurityService.getCurrentUser()
        if(!u){
            render(status: 401, text:"Not Logged in!")
            return
        }

        def f
        Map out = [:]

        //log.debug( "CreateContext"+ params)
        if (request.getFile('file')) {
            def tempf
            tempf = request.getFile('file')
            ImportProtocol ip = new ImportProtocol()
            ip.successful = true
            def res
            if (tempf.getOriginalFilename().toString().endsWith(".json"))
                res =contextConstructionService.createSamplesFromJson(fileService.fileToString(tempf), u,ip, params.annon?:true, params.shared?:true)
            else
                res = contextConstructionService.createSamplesFromXML(fileService.fileToString(tempf), u,ip, params.annon?:true, params.shared?:true)
            if(!res.empty)
                ip.successful
            if (ip && ip.isSuccessful()){
                    out.put("stat","successful")
                    out.put("messages",ip.messages)
                    String outString = out as JSON
                    assert outString != ""
                    log.info(outString)
                    render(status: 200, text: outString)
                    return
                }
            else {
                out.put("stat","failed")
                out.put("error", ip.lastMessage)
                out.put("log",ip.messages)

                assert out as JSON != ""
                log.error(out as JSON)

                render(status: 400, text: out as JSON)
                return
            }

        }
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @Transactional
    def uploadToDataset( ) {
        def f
        //print request
        String Fileparam =""
        log.debug( "CallParams"+params)
        if(!params.containsKey("dataset")|| params.dataset == null ){
            //print "No Data"
            render(status: 400, text: "No Dataset given")
            return
        }

        assert params.dataset
        /*print params.each{
            k,v ->
                if(CommonsMultipartFile.equals(v.class) || v.class.name.equals("org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile") )
                    Fileparam = k

        }*/
        def tempf
        if(!Fileparam )
            tempf= request.getFile('file')

        if(!Fileparam &&!tempf ){
            //print "Nofile inrequ"
            render(status: 400, text:"Cant Find File in Request ")
            return
        }
        if(!tempf)
            f = params[Fileparam]
        else
            f = tempf

        if (!f || f.isEmpty()) {
            //print "Nofile"
            render(status: 400, text: "The Transmitted File is Empty")
            return
        }
        User u = springSecurityService.getCurrentUser()
/*
        def stream = f
*/

        def fil = fileService.SaveFile(f)
        def stream= fileService.GetExisting(fil)
        Dataset ds
        Long dsLong

        try{
            dsLong = Long.parseLong(params.dataset)
        }catch (NumberFormatException e){
            def text = "The Dataset input '"+params.dataset+"' is Bullshit, do better!"
            //print "dasdasdasd"
            render(status: 400, text: text)
            return

        }
        ds = Dataset.findByIdAndOwner(dsLong,u)
        if(ds == null){
            def text = "The Dataset in The Request does not Exist or you are not the owner of the Dataset!"
            //print "dasdasdasd"
            render(status: 405, text: text)
            return
        }

        if(stream && ds && params.dataType) {
            if(!ds.fileName)
                ds.setFileName(fil.originalFilename)
            ImportProtocol ip = fileImportService.ReadIn(stream,fil, params.dataType,u, params.annon?:true, params.shared?:true, null , ds)
            if(ip.lastMessage.contains("generic"))
                if( ds.matching )
                    genericCSVImporterService.ImportFile(stream,ds.matching,fil, u,params.annon?:true, params.shared?:true,  ip,ds)
                else
                    structuredCSVToDataImporterService.importCSVToGeneticStandard(stream, ds, params.dataType,ip)

            Map out = [:]
            //print "AAA"
            def link = createLink(id:ds.id,controller: "Dataset",action: "show", absolute:true).toString()
            out.put("link",link)
            //out.put("log", temp)
            if (ip.successful) {
                out.put("stat","successful")
                out.put("DatasetId", ds.id)
                out.put("messages",ip.messages)

                String outString = out as JSON
                assert outString != ""
                log.info(outString)
                render(status: 200, text: outString)
                return
            }
            else {
                out.put("stat","failed")
                if(ip.lastMessage.contains("generic")&&!ds.matching)
                    out.put("message","Data format not Understood please add Mapping into the Database")
                out.put("log", ip.messages )

                //TODO Hier wird nicht Ordentlich gelöscht!
                //ds.delete()
                assert out as JSON != ""
                log.error(out as JSON)
                //print "DDDD"
                //print out
                render(status: 400, text: out as JSON)
                return
            }
        }else{
            String text =""
            if(!stream)
                text = "noData submitted"
            else if(!ds)
                text = "noData submitted"
            else if(!params.dataType||params.dataType =="")
                text = "Not Datatype Specified"
            log.error(text)
            //print "CCCC"
            //print text
            render(status: 400, text: text)
            return

        }
        log.error("Something failed")
        render(status: 500, text: "Something failed")
        return
    }


    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @Transactional
    def clinicalData( ) {
        log.debug("createClinical Data  "+ params)
        User u = springSecurityService.getCurrentUser()
        if(!u){
            render(status: 401, text:"Not Logged in!")
            return
        }
        //print params
        if(!params.containsKey("annon") )
            params.annon =true
        if(!params.containsKey("shared") )
            params.shared =true
        def f
        Map out = [:]

        log.debug( "createMetaData"+ params)
        if (request.getFile('file')) {
            def tempf
            tempf = request.getFile('file')
            ImportProtocol ip = new ImportProtocol()
            ip.successful = true
            def res
            if(tempf.getOriginalFilename().toString().endsWith(".xml"))
                ip =metadataImportService.genericImportAttributesFromXML(tempf.getOriginalFilename(),fileService.fileToString(tempf),[:],u,ip, params.annon?:true, params.shared?:true) // createContextFromXML(stringfile,springSecurityService.getCurrentUser())
            else
                ip =metadataImportService.genericImportAttributesFromCSV(tempf.getOriginalFilename(),fileService.fileToString(tempf),[:],u,ip, params.annon?:true, params.shared?:true) // createContextFromXML(stringfile,springSecurityService.getCurrentUser())

            if (ip && ip.isSuccessful()){
                out.put("stat","successful")
                out.put("messages",ip.messages)
                String outString = out as JSON


                assert outString != ""
                log.info(outString)
                render(status: 200, text: outString)
                return
            }
            else {
                out.put("stat","failed")
                out.put("error", ip.lastMessage)
                out.put("log", ip.messages)
                assert out as JSON != ""
                log.error(out as JSON)

                render(status: 400, text: out as JSON)
                return
            }

        }
    }


}
