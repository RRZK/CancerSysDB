package de.cancersysdb

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.data.DataVariation
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.json.JSONElement
import org.springframework.web.servlet.ModelAndView

/**
 * Created by rkrempel on 07.07.15.
 */
class StandardVCFImporterTest extends GroovyTestCase{

    def fileImportService
    FileService fileService
    def springSecurityService

    //TODO Dialects Einbinden
    void testVCFImporter() {

        def servletContext = ServletContextHolder.servletContext
        def mdc = new ManageDataController()
        mdc.fileImportService = fileImportService
        mdc.fileService =fileService
        mdc.springSecurityService.reauthenticate("User","123User")
        assert mdc.springSecurityService.isLoggedIn()
        mdc.springSecurityService = springSecurityService
        def path = "testData/vcf/contentvcf.vcf"
        def content = servletContext.getRealPath(path);
        def contenttemp = GrailsPluginUtils.pluginInfos.find { it.name == 'csys-db-core' || it.name == 'csys-db-pub' }
        if(contenttemp)
            content = contenttemp.pluginDir.toString()+path
        else
            content = servletContext.getRealPath(path);
        File  f= new File(content);
        assert f.exists()

        def text = f.text
        assert !text.empty


        def file = new GrailsMockMultipartFile(
                "file","Original.vcf", "text/plain",text.bytes
        )
/*        def tempfile = new DiskFileItem(new File())
        def file = new CommonsMultipartFile(
                tempfile
        )*/
        mdc.request.addFile file
        DataVariation test = new DataVariation()
        def temp1 = DataVariation.count()

        assert test.class.simpleName.equals("DataVariation")

        mdc.params.dataType = "Variation"
        mdc.importFile()
        assert mdc.response.status == 200
        assert mdc.flash.message != 'file cannot be empty'
        assert mdc.flash.message != 'Define Data Type!'
        assert mdc.flash.message != 'Please Map your Data'

        ModelAndView maw=mdc.modelAndView
        //assert mdc.flash.message == 'Please Map your Data'
        ImportProtocol importProtocol = maw.model.importProtocol

        assert importProtocol.successful
        def temp2 = DataVariation.count()
        def Emp= temp2 - temp1
        assert Emp >0
        assert Emp ==6
        assert importProtocol.getErrorMessages().size()==0
        assert !importProtocol.getSuccessfulImportStats().isEmpty()
        assert importProtocol.getSuccessfulImportStats().containsKey(test.class.simpleName)
        assert Emp == importProtocol.getSuccessfulImportStats().get(test.class.simpleName)

    }
    void testVCFImporter2() {
        //Same as bevore with other file
        def servletContext = ServletContextHolder.servletContext
        def mdc = new ManageDataController()
        mdc.fileImportService = fileImportService
        mdc.fileService =fileService
        mdc.springSecurityService.reauthenticate("User","123User")
        assert mdc.springSecurityService.isLoggedIn()
        mdc.springSecurityService = springSecurityService
        def path = "testData/vcf/content2vcf.vcf"
        def content = servletContext.getRealPath(path);
        def contenttemp = GrailsPluginUtils.pluginInfos.find { it.name == 'csys-db-core' || it.name == 'csys-db-pub' }
        if(contenttemp)
            content = contenttemp.pluginDir.toString()+path
        else
            content = servletContext.getRealPath(path);
        File  f= new File(content);
        assert f.exists()

        def text = f.text
        assert !text.empty


        def file = new GrailsMockMultipartFile(
                "file","Original.vcf", "text/plain",text.bytes
        )
/*        def tempfile = new DiskFileItem(new File())
        def file = new CommonsMultipartFile(
                tempfile
        )*/
        mdc.request.addFile file
        DataVariation test = new DataVariation()
        def temp1 = DataVariation.count()

        assert test.class.simpleName.equals("DataVariation")

        mdc.params.dataType = "Variation"
        mdc.importFile()
        assert mdc.response.status == 200
        assert mdc.flash.message != 'file cannot be empty'
        assert mdc.flash.message != 'Define Data Type!'
        assert mdc.flash.message != 'Please Map your Data'

        ModelAndView maw=mdc.modelAndView
        //assert mdc.flash.message == 'Please Map your Data'
        ImportProtocol importProtocol = maw.model.importProtocol

        assert importProtocol.successful
        def temp2 = DataVariation.count()
        def Emp= temp2 - temp1
        assert Emp >0
        assert Emp ==8
        assert importProtocol.getErrorMessages().size()==0
        assert !importProtocol.getSuccessfulImportStats().isEmpty()
        assert importProtocol.getSuccessfulImportStats().containsKey(test.class.simpleName)
        assert Emp == importProtocol.getSuccessfulImportStats().get(test.class.simpleName)

    }

    void testVCFGenericUploadImporter() {
        def servletContext = ServletContextHolder.servletContext

        def uc = new UploadController()
        uc.fileImportService = fileImportService
        uc.fileService = fileService
        uc.springSecurityService.reauthenticate("User", "123User")
        assert uc.springSecurityService.isLoggedIn()
        uc.springSecurityService = springSecurityService
        ///////////////////CREATE DATASET



        uc.params.annon =true

        uc.params.shared =false
        uc.params.description = "This is a Test tube Test!"

        uc.CreateDataset( )

        assert uc.response.status == 200
        JSONElement json = JSON.parse(uc.response.text)
        assert json.getAt("stat") =="successful"
        assert json.getAt("DatasetId")
        Dataset dataset1 = Dataset.get(json.getAt("DatasetId"))
        assert dataset1
        uc.response.reset()
        uc.response.resetBuffer()
        uc.request.clearAttributes()
        uc.request.removeAllParameters()
        uc.params.remove("annon")
        uc.params.remove("shared")
        uc.params.remove("description")
        /////////////////////////UPLOAD DATA


        def uc2 = new UploadController()
        uc2.fileImportService = fileImportService
        uc2.fileService = fileService
        uc2.springSecurityService.reauthenticate("User", "123User")
        assert uc2.springSecurityService.isLoggedIn()
        uc2.springSecurityService = springSecurityService
        /////////////////////////First Error - No Dataset Given
        def path = "testData/vcf/contentvcf.vcf"
        def content = servletContext.getRealPath(path);
        def contenttemp = GrailsPluginUtils.pluginInfos.find { it.name == 'csys-db-core' || it.name == 'csys-db-pub' }
        if(contenttemp)
            content = contenttemp.pluginDir.toString()+path
        else
            content = servletContext.getRealPath(path);
        File  f= new File(content);
        assert f.exists()

        def text = f.text
        assert !text.empty
        def file = new GrailsMockMultipartFile(
                "file","Original.vcf", "text/plain",text.bytes
        )

        uc2.request.addFile file
        DataVariation test = new DataVariation()


        assert test.class.simpleName.equals("DataVariation")



        uc2.params.dataType = "Variation"
        uc2.params.dataset = null
        uc2.uploadToDataset()

        assert uc2.response.status != 200
        assert uc2.response.text != ""
        uc2.response.reset()
        uc2.response.resetBuffer()
        uc2.request.clearAttributes()
        uc2.request.removeAllParameters()
        uc2.params.remove("dataset")
        uc2.params.remove("dataType")
        assert uc2.response.text == ""


        def uc3 = new UploadController()
        uc3.fileImportService = fileImportService
        uc3.fileService = fileService
        uc3.springSecurityService.reauthenticate("User", "123User")
        assert uc3.springSecurityService.isLoggedIn()
        uc3.springSecurityService = springSecurityService
    //////////With Dataset




        file = new GrailsMockMultipartFile(
                "file","Original.vcf", "text/plain",text.bytes
        )

        uc3.request.addFile file
        test = new DataVariation()


        assert test.class.simpleName.equals("DataVariation")
        uc3.params.dataset = dataset1.id
        uc3.params.dataType = "Variation"
        uc3.uploadToDataset()


        assert uc3.response.status == 200
        assert uc3.flash.message != 'file cannot be empty'
        assert uc3.flash.message != 'Define Data Type!'
        assert uc3.flash.message != 'Please Map your Data'

        assert uc3.response.text != ""
        json = JSON.parse(uc.response.text)

        assert json.getAt("stat") =="successful"
        assert json.getAt("DatasetId")
        Dataset dataset2 = Dataset.get(json.getAt("DatasetId"))

        assert dataset2
        assert dataset2.equals(dataset1)


    }


}
