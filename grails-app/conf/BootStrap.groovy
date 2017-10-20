import de.cancersysdb.Import.GeneAliasImportService
import de.cancersysdb.Import.GeneImportService
import de.cancersysdb.Role
import de.cancersysdb.Import.TcgaClassParserService
/*
import de.cancersysdb.TestData.TestdataCreatorService
*/
import de.cancersysdb.User
import de.cancersysdb.UserDetail
import de.cancersysdb.UserRole
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.geneticStandards.GeneOntologyInfo
import de.cancersysdb.geneticStandards.GeneSymbolAlias
import de.cancersysdb.geneticStandards.TCGAClassObject
import de.cancersysdb.MarshallingService
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Environment

class BootStrap {
/*
    TestdataCreatorService testdataCreatorService
*/
    TcgaClassParserService tcgaClassParserService
    MarshallingService marshallingService
    GeneImportService geneImportService
    GeneAliasImportService geneAliasImportService
    SpringSecurityService springSecurityService
    def grailsApplication
    def workflowManagementService
    def init = { servletContext ->

        ///////////////////////////MARSHALLER !!! Important
        marshallingService.registerMarshallers()

        File f = new File("./")

        log.info( "Enviroment "+Environment.current + "  Basepath "+ f.getAbsoluteFile()+"\n")



        //grailsApplication.config.cancersys.config.dataFilepat.toString()
        def dataPath = new File(grailsApplication.config.cancersys.config.BasePath.toString())
        if(!dataPath.exists()){
            log.error( "Data Basepath "+grailsApplication.config.cancersys.config.BasePath+" does not Exist"+"\n")
            throw FileNotFoundException()
        }else{
            log.info( "Basepath for Data " + grailsApplication.config.cancersys.config.BasePath+"\n")
        }

        dataPath = new File(grailsApplication.config.cancersys.config.tempFilepath.toString())
        if(!dataPath.exists()){
            log.info( "creating Path for Tempfiles " + dataPath.absolutePath+"\n")
            dataPath.mkdir()
        }
        dataPath = new File(grailsApplication.config.cancersys.config.dataFilepath.toString())
        if(!dataPath.exists()){
            log.info( "creating Path for Data " + dataPath.absolutePath+"\n")
            dataPath.mkdir()
        }



        dataPath = new File(grailsApplication.config.cancersys.config.dataFilepath.toString()+"/workflows/")

        if(!dataPath.exists()){
            log.info( "processed Workflows Basepath "+dataPath.getAbsolutePath()+" does not Exist, creating ..."+"\n")
            dataPath.mkdir()
        }else{
            log.info( "Basepath for processed Workflows " + dataPath.absolutePath+"\n")
        }


        dataPath = new File(grailsApplication.config.cancersys.config.dataFilepath.toString()+"/WorkflowMasters/")

        if(!dataPath.exists()){
            log.info( "WorkflowMasters Basepath "+dataPath.getAbsolutePath()+" does not exist, creating ..."+"\n")
            dataPath.mkdir()
        }else{
            log.info( "Basepath for WorkflowMasters " + dataPath.absolutePath+"\n")
        }


        //Imprt Datastandards if not Availible
        ImportTCGAStandards()
        //Import Genes
        log.info( "Genes in DB "+ Gene.count() +"\n")


        if(Gene.count()==0){
            geneImportService.UpdateGeneTable("")

        }
        if(GeneOntologyInfo.count()==0){
            log.info( "Import Gene Ontology")

            geneImportService.GeneOntologyImport("")

        }
        log.info( "Import Workflows")
        
        workflowManagementService.createWorkflowFromJSON("CologneWorkflow")
        //workflowManagementService.createWorkflowFromJSON("MunichWorkflow")
        //workflowManagementService.createWorkflowFromJSON("MunichWorkflow2")
        workflowManagementService.createWorkflowFromJSON("MunichWorkflowNew")
        workflowManagementService.createWorkflowFromJSON("SurvivalWorkflow")
        workflowManagementService.createWorkflowFromJSON("TherapyWorkflow")
        workflowManagementService.createWorkflowFromJSON("SurvivalWorkflowWithCohort")
        workflowManagementService.createWorkflowFromJSON("WorkflowCooccurance")
        workflowManagementService.createWorkflowFromJSON("WorkflowCNAsize")
        workflowManagementService.createWorkflowFromJSON("WorkflowVartypes")

        if(GeneSymbolAlias.count() ==0)
            geneAliasImportService.ImportGeneAlias()


        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                workflowManagementService.createWorkflowFromJSON("DownloadTest")
                workflowManagementService.createWorkflowFromJSON("ListInputTest")

                createmockData()
                break
            case Environment.TEST:
                workflowManagementService.createWorkflowFromJSON("DownloadTest")
                workflowManagementService.createWorkflowFromJSON("ListInputTest")

                createmockData()
                break
            case Environment.PRODUCTION:
                createAdminIfNonExists()
                break
        }


        //Show config of the Project
/*
        print "Starting in "+grailsApplication.config.cancersys.config.systemType+" mode"
        if(grailsApplication.config.cancersys.config.systemType.toString().toLowerCase().equals("portal")){

            springSecurityService.get
        }
*/



    }
    def destroy = {
    }
    def ImportTCGAStandards(){


        if(!TCGAClassObject.findByType(0)){
            String Study = '''"SNOMED Code", "Study Abbreviation","Study Name"
"91861009","Acute Myeloid Leukemia","LAML"
"92526009","Adrenocortical carcinoma","ACC"
"399326009","Bladder Urothelial Carcinoma","BLCA"
"74532006","Brain Lower Grade Glioma","LGG"
"372137005","Breast invasive carcinoma","BRCA"
"285432005","Cervical squamous cell carcinoma and endocervical adenocarcinoma","CESC"
"70179006","Cholangiocarcinoma","CHOL"
"1701000119104","Colon adenocarcinoma","COAD"
"372138000","Esophageal carcinoma","ESCA"
"393563007","Glioblastoma multiforme","GBM"
"716659002","Head and Neck squamous cell carcinoma","HNSC"
"128667008","Kidney Chromophobe","KICH"
"254915003","Kidney renal clear cell carcinoma","KIRC"
"715561008","Kidney renal papillary cell carcinoma","KIRP"
"93870000","Liver hepatocellular carcinoma","LIHC"
"254626006","Lung adenocarcinoma","LUAD"
"254634000","Lung squamous cell carcinoma","LUSC"
"413990004","Lymphoid Neoplasm Diffuse Large B-cell Lymphoma","DLBC"
"62064005","Mesothelioma","MESO"
"314191009","Ovarian serous cystadenocarcinoma","OV"
"372142002","Pancreatic adenocarcinoma","PAAD"
"302835009","Pheochromocytoma and Paraganglioma","PCPG"
"399490008","Prostate adenocarcinoma","PRAD"
"254582000","Rectum adenocarcinoma","READ"
"424413001","Sarcoma","SARC"
"93655004","Skin Cutaneous Melanoma","SKCM"
"408647009","Stomach adenocarcinoma","STAD"
"713577007","Testicular Germ Cell Tumors","TGCT"
"444231005","Thymoma","THYM"
"448216007","Thyroid carcinoma","THCA"
"702369008","Uterine Carcinosarcoma","UCS"
"92582009","Uterine Corpus Endometrial Carcinoma","UCEC"
"399705007","Uveal Melanoma","UVM"
"277567002","T-cell prolymphocytic leukemia","TPL"'''


            tcgaClassParserService.serviceMethod(Study)

            log.info( "created Study Types")




        }
        if(!TCGAClassObject.findByType(1)) {
            String SampleType = '''"Code","Definition","Short Letter Code"
"01","Primary solid Tumor","TP"
"02","Recurrent Solid Tumor","TR"
"03","Primary Blood Derived Cancer - Peripheral Blood","TB"
"04","Recurrent Blood Derived Cancer - Bone Marrow","TRBM"
"05","Additional - New Primary","TAP"
"06","Metastatic","TM"
"07","Additional Metastatic","TAM"
"08","Human Tumor Original Cells","THOC"
"09","Primary Blood Derived Cancer - Bone Marrow","TBM"
"10","Blood Derived Normal","NB"
"11","Solid Tissue Normal","NT"
"12","Buccal Cell Normal","NBC"
"13","EBV Immortalized Normal","NEBV"
"14","Bone Marrow Normal","NBM"
"20","Control Analyte","CELLC"
"40","Recurrent Blood Derived Cancer - Peripheral Blood","TRB"
"50","Cell Lines","CELL"
"60","Primary Xenograft Tissue","XP"
"61","Cell Line Derived Xenograft Tissue","XCL"'''
            tcgaClassParserService.serviceMethod(SampleType)
            log.info( "created Sample Types")
        }
        if(!TCGAClassObject.findByType(2)) {
            String Tissue = '''"Tissue"
"many"
"brain"
"lung"
"ovary"
"cell line control"e
"breast"
"kidney"
"colon"
"liver"
"lymph"
"blood"
"stomach"
"thyroid"
"bladder"
"endometrial"
"skin"
"rectal"
"cervix"
"prostate"
"head and neck"
"fallopian tube"
'''
            tcgaClassParserService.serviceMethod(Tissue)
            log.info( "created Tissue Types")
        }

    }

    def createmockData(){
        if (!User.count()) {

            def userRole = Role.findOrSaveByAuthority(Role.USER)
            def managerRole = Role.findOrSaveByAuthority(Role.MANAGER)
            def adminRole = Role.findOrSaveByAuthority(Role.ADMIN)
/*
            def spectatorRole = Role.findOrSaveByAuthority(Role.SPECTATOR)
*/
            def user

            user = new User(username: "User"
                    , details: new UserDetail(firstName: "User", lastName: "Test")
                    , password: "123User", eMail: "krempelr@uni-koeln.de", enabled: true).save(failOnError: true)
            UserRole.create user, userRole
/*
            UserRole.create user, spectatorRole
*/

            //testdataCreatorService.CreateTestdata(krempelr,false,true)

            def manager = new User(username: "Manager"
                    , details: new UserDetail(firstName: "Manager", lastName: "Test")
                    , password: "123Manager", eMail: "krempelr@uni-koeln.de", enabled: true).save(failOnError: true)
            UserRole.create manager, userRole
            UserRole.create manager, managerRole
/*
            UserRole.create manager, spectatorRole
*/
            def admin = new User(username: "Admin"
                    , details: new UserDetail(firstName: "Admin", lastName: "Test")
                    , password: "123Admin", eMail: "krempelr@uni-koeln.de", enabled: true).save(failOnError: true)
            UserRole.create admin, userRole
            UserRole.create admin, managerRole
            UserRole.create admin, adminRole
/*
            UserRole.create admin, spectatorRole
*/

        }


    }


    def createAdminIfNonExists(){
        if (!User.count()) {

            def userRole = Role.findOrSaveByAuthority(Role.USER)
            def managerRole = Role.findOrSaveByAuthority(Role.MANAGER)
            def adminRole = Role.findOrSaveByAuthority(Role.ADMIN)
/*
            def spectatorRole = Role.findOrSaveByAuthority(Role.SPECTATOR)
*/

            userRole.save(flush: true,failOnError: true)

            managerRole.save(flush: true,failOnError: true)
            adminRole.save(flush: true,failOnError: true)
            def admin = new User(username: "Admin"
                    , details: new UserDetail(firstName: "Admin", lastName: "Administrator")
                    , password: "AdminPassword", eMail: "", enabled: true).save(failOnError: true,flush: true)
            UserRole.create admin, userRole
            UserRole.create admin, managerRole
            UserRole.create admin, adminRole
/*
            UserRole.create admin, spectatorRole
*/

            admin.setPasswordExpired(true)
            admin.save(failOnError: true,flush: true)


        }
    }
}
