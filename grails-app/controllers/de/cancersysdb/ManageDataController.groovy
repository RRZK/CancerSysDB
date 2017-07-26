package de.cancersysdb

import de.cancersysdb.Controll.WebSocketShow
import de.cancersysdb.Import.MetadataImportService
import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.contextHandling.WebsocketRequestMeta
import de.cancersysdb.Import.AsyncGeneImportService
import de.cancersysdb.Import.CancersysMasterImporterService
import de.cancersysdb.Import.ContextConstructionService
import de.cancersysdb.Import.FileImportService
import de.cancersysdb.Import.GeneImportService
import de.cancersysdb.Import.GenericCSVImporterService
import de.cancersysdb.Import.TcgaClassParserService
import de.cancersysdb.serviceClasses.ExternalSourceDescription
import de.cancersysdb.serviceClasses.FiletypeToGeneticStandardMatching
import de.cancersysdb.TestData.AsyncTestdataCreatorService
import de.cancersysdb.TestData.TestdataCreatorService
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.springframework.messaging.handler.annotation.MessageMapping
@Secured("isFullyAuthenticated()")
class ManageDataController {
    /**
     * TODO Remove unsued Test Functions
     * TODO Comment
     */
    TcgaClassParserService tcgaClassParserService
    def springSecurityService
    AsyncGeneImportService asyncGeneImportService
    GeneImportService geneImportService
    AsyncTestdataCreatorService asyncTestdataCreatorService
    WebSocketIdentifierService webSocketIdentifierService
    TestdataCreatorService testdataCreatorService
    FileImportService fileImportService
    FileService fileService
    GenericCSVImporterService genericCSVImporterService
    DatasetService datasetService
    ContextConstructionService contextConstructionService
    CancersysMasterImporterService cancersysMasterImporterService
    MetadataImportService metadataImportService

    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def index(){

        render view: "/manageData/index"

    }

    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def  dataImport(Dataset ds){
        def dataTypes = datasetService.getDataClassNamesAsMaps()


        render view: "dataImport", model:[dataTypes:dataTypes,dataset:ds]

    }

    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def dataImportFromExternalSource(){

        ImportProtocol ip= null

        ExternalSourceDescription externalSourceDescriptionInstance
        if(!params.externalSourceDescriptionInstance && !params.externalSourceId && !params.RequestResource){
            externalSourceDescriptionInstance = null

        }
        else if(!params.externalSourceId ){
            //MANGE SELF REFERENCES!
            if(params.externalSourceDescriptionInstance.uRL.contains("//localhost"))
                params.externalSourceDescriptionInstance.uRL= params.externalSourceDescriptionInstance.uRL.replace("//localhost","//127.0.0.1")
            if(params.externalSourceDescriptionInstance.descriptionReference.contains("//localhost"))
                params.externalSourceDescriptionInstance.descriptionReference=params.externalSourceDescriptionInstance.descriptionReference.replace("//localhost","//127.0.0.1")
            externalSourceDescriptionInstance = ExternalSourceDescription.findByName(params.externalSourceDescriptionInstance.name)
            if(!externalSourceDescriptionInstance){

                externalSourceDescriptionInstance =  new ExternalSourceDescription(params.externalSourceDescriptionInstance)

                if(externalSourceDescriptionInstance.validate()){
                    externalSourceDescriptionInstance.save()
                }

            }

        }
        if(params.RequestResource && params.externalSourceId){

            externalSourceDescriptionInstance = ExternalSourceDescription.findById(params.externalSourceId)

            ip= cancersysMasterImporterService.ImportFromCancersysMaster(params.RequestResource, externalSourceDescriptionInstance)
        }

        def externalSources = ExternalSourceDescription.getAll()

        if(externalSourceDescriptionInstance != null && externalSourceDescriptionInstance.hasErrors()){
            flash.message = "Error"

            render view:"dataImportFromExternalSource",model:[externalSources:externalSources]
            return
        }

/*        out.put("stat","failed")
        out.put("DatasetId", ds.id)*/
        //text:["stat":"successful","DatasetId":ip.dataset.id] as JSON
        if(ip)
            render view: "importExternalSource", model:[externalSourceDescriptionInstance:externalSourceDescriptionInstance,externalSources:externalSources,importProtocol:ip]
        else
            render view: "importExternalSource", model:[externalSourceDescriptionInstance:externalSourceDescriptionInstance,externalSources:externalSources]

    }


    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def getExternalSourceByName(String externalSourceName){
        if(!externalSourceName)
            externalSourceName = params.externalSourceName

        ExternalSourceDescription externalSource = ExternalSourceDescription.findByName(externalSourceName)
        if(!externalSource){
            response.sendError( 404)
        }
        render externalSource as JSON
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @Transactional
    def importFile() {
        def f

        SourceFile info = null
        Dataset ds = null
        //File Handling
        if(params.dataset)
            ds =Dataset.get(params.dataset)
        //Check if the File allready existis and if Not Save
        if (params.ExistingFile ){
            try {
                info = SourceFile.findById(params.ExistingFile )
                f = fileService.GetExisting(info)
            } catch (e) {
                flash.message = 'file cannot be empty'
                dataImport(ds)
                return
            }
        }
        else {
            try {
                f = request.getFile('file')
            } catch (e) {
                flash.message = 'file cannot be empty'
                dataImport(ds)
                return
            }
            if (!f || f.empty) {
                flash.message = 'file cannot be empty'
                dataImport(ds)
                return
            }
            if (!params.dataType || "".equals(params.dataType)) {
                flash.message = 'Define Data Type!'
                dataImport(ds)
                return

            }
            info= fileService.SaveFile(f)
            f= fileService.GetExisting(info)
        }
        info.save( failOnError: true)

        // Import Handling

        def dataTypes = datasetService.getDataClassNamesAsMaps()
        String outrerror =""
        //Create Import Protocoll
        ImportProtocol ip = new ImportProtocol()
        ip.Autoreport = true
        ip.AutoreportAferEveryNTHDataset = 10000

        //Use the Generic Importer if a Mapping purposal is attached or the File Import Service
        if(params.Mapping){
            FiletypeToGeneticStandardMatching temp = genericCSVImporterService.mappingFromForm(info,params)
            if(temp)
                genericCSVImporterService.ImportFile(f,temp, info, springSecurityService.getCurrentUser(), params.Annon ? true : false , params.Shared? true : false,ip, ds)
            else{

                flash.message = message(message:"Error on data Import, please reimport.")
            }
        }else{
            fileImportService.ReadIn(f, info, params.dataType, springSecurityService.getCurrentUser(), params.Annon ? true : false , params.Shared? true : false,ip, ds)
            //Check for ErrorStrings
        }
        if(ip.getLastMessage().equals("generic")){
            Class klzz = datasetService.getDataClassForName(params.dataType)
            Map matchingCandidates = genericCSVImporterService.CreateMatchingCandidates(klzz, f,info)
            String preMappingError = genericCSVImporterService.errorCommentTypedCandidates(matchingCandidates)
            if(preMappingError==""){
                def mping = info.getMapping()
                if(mping == null ){
                    flash.message = 'Error on Upload'
                    redirect action: "importFile"
                    return
                }
                flash.message = 'Please Map your Data'
                importMatching(matchingCandidates, info,params.dataType, ds)
                return
            }else{
                flash.message = preMappingError
                render  view: "dataImport", model:[fileInfo:info.id,fileName:info.getOriginalFilename(),dataTypes: dataTypes,dataset:ds]
                return
            }

        }

        if(!ip.successful){
            log.debug( "import Unsuccessful")
            outrerror= ip.messages.join("\n")
            flash.message = message(message:outrerror)

            render  view: "dataImport", model:[fileInfo:info.id,fileName:info.getOriginalFilename(),importProtocol:ip,dataTypes: dataTypes, matchingType: params.dataType]
            return
        }else{
            flash.message = message(message:" Succsessfull imported  Gene Data")

            render  view: "importFinished", model:[fileInfo:info.id,fileName:info.getOriginalFilename(),importProtocol:ip ]

            return
        }
/*        render  view: "show", model:[fileInfo:info.id,fileName:info.getOriginalFilename(),importProtocol: ip ]

        return */

    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def importMatching(def matchingCandidates, SourceFile file, String type, Dataset dataset){

        def temp = datasetService.dataClassNames


        def dataTypes = []

        temp.each {dataTypes.add(["key":it,"label":it])}
        //print "FileID "+ file.id

        render view: "dataImport", model:[matchingType:type,matchingCandidates:matchingCandidates,fileInfo:file.id,fileName:file.getOriginalFilename(),machtingBlankValue:genericCSVImporterService.DoNotMap,dataset:dataset]

    }


    @Secured(["ROLE_ADMIN"])
    def GeneImport(){

        def rand = new Random()
        def messageSock = "/topic/gimport"+rand.nextInt(9999)
        def temp = new WebsocketRequestMeta()

        User user = springSecurityService.getCurrentUser()

        temp.setUser( user.getId())

        webSocketIdentifierService.SetStuff(messageSock,temp)


        List<WebSocketShow> sockShow = new ArrayList<WebSocketShow>()
        sockShow << new WebSocketShow(socket: messageSock, controller: "startGeneImport", description:"Update Embl Version")
        sockShow << new WebSocketShow(socket: messageSock, controller: "startGeneOntologyImport", description:"Update Geneontology")
        render view: "/importReportView", model:[Menus:sockShow]

    }

    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @MessageMapping("/startGeneImport")
    //@SendTo("/topic/gimport")
    protected String startGeneImport(String messageSock) {
        //String messageSock =session.getAttribute("GeneImportReport")

        if(messageSock) {
            asyncGeneImportService.UpdateGeneTable(messageSock)
            //asyncGeneService.GeneOntologyImport(messageSock,null)
        }
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @MessageMapping("/startGeneOntologyImport")
    //@SendTo("/topic/gimport")
    protected String startGeneOntologyImport(String messageSock) {
        //String messageSock =session.getAttribute("GeneImportReport")

        if(messageSock) {

            asyncGeneImportService.GeneOntologyImport(messageSock,null)
        }
    }
////////////////////////////////////////

    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def testData(){

        def rand = new Random()
        def messageSock = "/topic/createTestData"+rand.nextInt(9999)

        def temp = new WebsocketRequestMeta()
        User user = springSecurityService.getCurrentUser()

        log.debug("User id "+ user.getId())
        if(!user)
            return

        temp.setUser( user.getId())
        webSocketIdentifierService.SetStuff(messageSock,temp)
        List<WebSocketShow> sockShow = new ArrayList<WebSocketShow>()
        sockShow << new WebSocketShow(socket: messageSock, controller: "createTestData", description:"Create a single TestStudy")

        render view: "/importReportView", model:[Menus:sockShow]

    }


    //TODO FOR TEST FROM HERE

    //@Secured(['ROLE_ADMIN'])
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @MessageMapping("/createTestData")
    protected String  createTestData(String messageSock){
        //webSocketIdentifierService.show()

        long uid = webSocketIdentifierService.GetUserByRequest(messageSock)


        asyncTestdataCreatorService.CreateTestdata(uid,messageSock,false,false)
    }



    //TODO FOR TEST END


    @Transactional
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def uploadFile() {
        def f

        try {
            f = request.getFile('file')
        }catch(e){
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }
        if (f.empty) {
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }

        def stream = f.getInputStream()

        def outrerror = tcgaClassParserService.serviceMethod(stream)

        if(outrerror ) {

            flash.message = message(message:" Error while importing Samples from Uploaded File"+outrerror.message.toString())
            render(view: 'index')
        }else {

            flash.message = message(message:" successful imported Gene Data")
            render(view: 'index')
        }
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @Transactional
    def createContext() {
        def f

        def dataTypes = datasetService.getDataClassNamesAsMaps()



        try {
            f = request.getFile('Contextfile')
        }catch(e){
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }
        if (!f || f.empty) {
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }
        Dataset dataset = null
        ImportProtocol importProtocol = null
        String stringfile = fileService.fileToString(f)
        try{
            importProtocol =contextConstructionService.createContextFromXML(stringfile,springSecurityService.getCurrentUser())
            dataset = importProtocol.getDataset()
        }catch(Exception e){
            if(importProtocol == null){
                importProtocol = new ImportProtocol()
                importProtocol.ImportStart()
                importProtocol.ImportedSuccessful(false)
                importProtocol.ImportEnd()

            }
            flash.message = message(message:" Error while importing from Uploaded File" )
            render(view: 'dataImport', model:[importProtocol: importProtocol,dataTypes:dataTypes])
            return
        }
        if(importProtocol.isSuccessful()){
            def persistedImportProtocol = new PersistedImportProtocol(importProtocol)
            persistedImportProtocol.save()

            flash.message = message(message:" Successesfull created Context")
            render(view: 'dataImport', model:[importProtocol: importProtocol,dataset:dataset,dataTypes:dataTypes])
            return
        }else{

            flash.message = message(message:" Error in Import" )
            render(view: 'dataImport', model:[importProtocol: importProtocol,dataTypes:dataTypes])
            return
        }


    }


    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    @Transactional
    /**
     * This Action Retrives an XML File in the Metadatafile variable and Constructs an Import Info Object with ClinicalInformation attached to it
     */
    def createMetadata() {
        def f

        try {
            f = request.getFile('Metadatafile')
        }catch(e){
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }
        if (!f || f.empty) {
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }
        User u = springSecurityService.getCurrentUser()
        String stringfile = fileService.fileToString(f)
        SourceFile info= fileService.SaveFile(f)
        f= fileService.GetExisting(info)

        ImportProtocol importProtocol
        String filename = info.originalFilename
        assert(f instanceof File )
        importProtocol = new ImportProtocol()

        try{
            if(filename.endsWith("xml")){
                importProtocol =metadataImportService.genericImportAttributesFromXML(filename,stringfile,[:],u,importProtocol) // createContextFromXML(stringfile,springSecurityService.getCurrentUser())
                importProtocol.ImportEnd()
            }else{
                importProtocol =metadataImportService.genericImportAttributesFromCSV(filename,stringfile,[:],u,importProtocol)
                importProtocol.ImportEnd()

            }
        }catch(Exception e){
            print e
            importProtocol.ImportedSuccessful(false)
            importProtocol.ImportEnd()
            def persistedImportProtocol = new PersistedImportProtocol(importProtocol)
            persistedImportProtocol.save()
            flash.message = message(message:" Error while importing from Uploaded File" )
            render(view: 'dataImport', model:[importProtocol: importProtocol])
            return
        }
        if(importProtocol.isSuccessful()){
            flash.message = message(message:" Successesfull created Context")
            render(view: 'dataImport', model:[importProtocol: importProtocol])
            return
        }else{

            flash.message = message(message:" Error in Import" )
            render(view: 'dataImport', model:[importProtocol: importProtocol])
            return
        }


    }

    def createtestdataPrivate(){
        User u = springSecurityService.getCurrentUser()
        testdataCreatorService.CreateTestdata(u.id,"/topic/createTestData",false,false)
    }
    @Secured(['ROLE_ADMIN'])
    def GeneService(){

        geneImportService.UpdateGeneTable(new String("aaaa"))

    }
    @Secured(['ROLE_ADMIN'])
    def GeneOntologieService(){


        geneImportService.GeneOntologyImport(new String("aaaa"),null)
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def createtestdataPublicAnnon(){
        User u = springSecurityService.getCurrentUser()
        testdataCreatorService.CreateTestdata(u.id,"/topic/createTestData",true,true)
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def createtestdataPublicNonAnnon(){
        User u = springSecurityService.getCurrentUser()
        testdataCreatorService.CreateTestdata(u.id,"/topic/createTestData",false,true)
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def createtestdata(){
        User u = springSecurityService.getCurrentUser()
        testdataCreatorService.CreateTestdata(u.id,"/topic/createTestData",true,false)
    }

    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def importRest(String URL){


        def resp = rest.get(URL)




    }


}
