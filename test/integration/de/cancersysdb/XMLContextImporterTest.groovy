package de.cancersysdb

import de.cancersysdb.ImportTools.ImportProtocol
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.servlet.ModelAndView

/**
 * Created by rkrempel on 07.07.15.
 */
class XMLContextImporterTest extends GroovyTestCase{

    def fileImportService
    FileService fileService
    def springSecurityService
    def contextConstructionService

    //TODO Dialects Einbinden
    void testXMLContextimporter() {
        def servletContext = ServletContextHolder.servletContext




        def mdc = new ManageDataController()
        mdc.fileImportService = fileImportService
        mdc.fileService =fileService
        mdc.springSecurityService.reauthenticate("User","123User")
        assert mdc.springSecurityService.isLoggedIn()
        mdc.springSecurityService = springSecurityService
        mdc.contextConstructionService = contextConstructionService

        def path = "testData/xml/contentBiospecies.xml"
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
                "Contextfile","Original.cml", "text/plain",text.bytes
        )
/*        def tempfile = new DiskFileItem(new File())
        def file = new CommonsMultipartFile(
                tempfile
        )*/
        mdc.request.addFile(file)
        Dataset test = new Dataset()
        def temp1 = Dataset.count()

        assert test.class.simpleName.equals("Dataset")

        mdc.createContext()
        assert mdc.response.status == 200


        ModelAndView maw=mdc.modelAndView
        //assert mdc.flash.message == 'Please Map your Data'
        ImportProtocol importProtocol = maw.model.importProtocol

        assert importProtocol.isSuccessful()
        def temp2 = Dataset.count()
        def Emp= temp2 - temp1
        assert Emp >0
        assert Emp ==1
        assert importProtocol.getErrorMessages().size()==0



    }

    void testXMLContextimporter_NotEnoughInfos() {
        def servletContext = ServletContextHolder.servletContext
        def mdc = new ManageDataController()
        mdc.fileImportService = fileImportService
        mdc.fileService =fileService
        mdc.springSecurityService.reauthenticate("User","123User")
        assert mdc.springSecurityService.isLoggedIn()
        mdc.springSecurityService = springSecurityService
        mdc.contextConstructionService = contextConstructionService

        def path = "testData/xml/NotEnoughInfosBiospecies.xml"
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
                "Contextfile","Original.xml", "text/plain",text.bytes
        )
/*        def tempfile = new DiskFileItem(new File())
        def file = new CommonsMultipartFile(
                tempfile
        )*/
        mdc.request.addFile(file)
        Dataset test = new Dataset()
        def temp1 = Dataset.count()

        assert test.class.simpleName.equals("Dataset")

        mdc.createContext()
        assert mdc.response.status == 200


        ModelAndView maw=mdc.modelAndView
        //assert mdc.flash.message == 'Please Map your Data'
        ImportProtocol importProtocol = maw.model.importProtocol
        assert importProtocol
        assert !importProtocol.isSuccessful()
/*        def temp2 = Dataset.count()
        def Emp= temp2 - temp1
        assert Emp ==0*/

    }
    void testXMLContextimporter_contentbroken() {
        def servletContext = ServletContextHolder.servletContext
        def mdc = new ManageDataController()
        mdc.fileImportService = fileImportService
        mdc.fileService =fileService
        mdc.springSecurityService.reauthenticate("User","123User")
        assert mdc.springSecurityService.isLoggedIn()
        mdc.springSecurityService = springSecurityService
        mdc.contextConstructionService = contextConstructionService

        def path = "testData/xml/brokenBiospecies.xml"
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
                "Contextfile","Original.xml", "text/plain",text.bytes
        )
/*        def tempfile = new DiskFileItem(new File())
        def file = new CommonsMultipartFile(
                tempfile
        )*/
        mdc.request.addFile(file)
        Dataset test = new Dataset()
        def temp1 = Dataset.count()

        assert test.class.simpleName.equals("Dataset")

        mdc.createContext()
        assert mdc.response.status == 200


        ModelAndView maw=mdc.modelAndView
        //assert mdc.flash.message == 'Please Map your Data'
        ImportProtocol importProtocol = maw.model.importProtocol

        assert !importProtocol.isSuccessful()
        def temp2 = Dataset.count()
        def Emp= temp2 - temp1
        assert Emp ==0
    }

}
