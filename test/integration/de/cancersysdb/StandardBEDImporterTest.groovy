package de.cancersysdb

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.data.DataPeak
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.servlet.ModelAndView

/**
 * Created by rkrempel on 07.07.15.
 */
class StandardBEDImporterTest extends GroovyTestCase{

    def fileImportService
    FileService fileService
    def springSecurityService

    void testBEDImporter() {
        def servletContext = ServletContextHolder.servletContext
        def mdc = new ManageDataController()
        mdc.fileImportService = fileImportService
        mdc.fileService =fileService
        mdc.springSecurityService.reauthenticate("User","123User")
        assert mdc.springSecurityService.isLoggedIn()
        mdc.springSecurityService = springSecurityService

        def path = "testData/BED/content.bed"
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
                "file","Peak.bed", "text/plain",text.bytes
        )
/*        def tempfile = new DiskFileItem(new File())
        def file = new CommonsMultipartFile(
                tempfile
        )*/
        mdc.request.addFile file
        DataPeak test = new DataPeak()
        def temp1 = DataPeak.count()

        assert test.class.simpleName.equals("DataPeak")

        mdc.params.dataType = "Peak"
        mdc.importFile()
        assert mdc.response.status == 200
        assert mdc.flash.message != 'file cannot be empty'
        assert mdc.flash.message != 'Define Data Type!'
        assert mdc.flash.message != 'Please Map your Data'

        ModelAndView maw=mdc.modelAndView
        //assert mdc.flash.message == 'Please Map your Data'
        ImportProtocol importProtocol = maw.model.importProtocol

        assert importProtocol.successful
        def temp2 = DataPeak.count()
        def Emp= temp2 - temp1
        assert Emp >0
        assert Emp == 9
        assert importProtocol.getErrorMessages().size()==0
        assert !importProtocol.getSuccessfulImportStats().isEmpty()
        assert importProtocol.getSuccessfulImportStats().containsKey(test.class.simpleName)
        assert Emp == importProtocol.getSuccessfulImportStats().get(test.class.simpleName)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


    }


}
