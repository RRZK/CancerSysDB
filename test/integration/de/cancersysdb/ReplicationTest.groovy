package de.cancersysdb

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.serviceClasses.ExternalSourceDescription
import de.cancersysdb.data.DataVariation
import grails.converters.JSON
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.json.JSONElement
import org.springframework.web.servlet.ModelAndView

/**
 * Created by rkrempel on 07.07.15.
 */
class ReplicationTest extends GroovyTestCase {

        def fileImportService
        FileService fileService
        def springSecurityService
        GrailsApplication grailsApplication
        private static final log = LogFactory.getLog(this)


        void testReplication() {
                /////////////////THIS TEST IS Made TO FAIL! In THe Self Request will FAIL!
                def servletContext = ServletContextHolder.servletContext
                def uc = new UploadController()
                uc.fileImportService = fileImportService
                uc.fileService = fileService
                uc.springSecurityService.reauthenticate("User", "123User")
                assert uc.springSecurityService.isLoggedIn()
                uc.springSecurityService = springSecurityService
                ///////////////////CREATE DATASET



                uc.params.annon =true

                uc.params.shared =true
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
                /////////////////////////UPLOAD DATA
                def path = "testData/vcf/contentvcf.vcf"
                def content = servletContext.getRealPath(path);
                def contenttemp = GrailsPluginUtils.pluginInfos.find { it.name == 'csys-db-core' || it.name == 'csys-db-pub' }
                if(contenttemp)
                        content = contenttemp.pluginDir.toString()+path
                else
                        content = servletContext.getRealPath(path);


                File  f= new File(content);

                GrailsMockMultipartFile file = new GrailsMockMultipartFile(
                        "file","Original.vcf", "text/plain",f.bytes
                )
                print f.bytes
                uc.request.addFile file
                DataVariation test = new DataVariation()


                assert test.class.simpleName.equals("DataVariation")
                uc.params.dataset = dataset1.id
                uc.params.dataType = "Variation"

                uc.uploadToDataset()

                assert uc.response.text != ""
                assert uc.response.status == 200
                assert uc.flash.message != 'file cannot be empty'
                assert uc.flash.message != 'Define Data Type!'
                assert uc.flash.message != 'Please Map your Data'

                assert uc.response.text != ""
                json = JSON.parse(uc.response.text)

                assert json.getAt("stat") =="successful"
                assert json.getAt("DatasetId")
                Dataset dataset2 = Dataset.get(json.getAt("DatasetId"))
                assert json.getAt("link")
                def link = json.getAt("link")
                assert link.class.equals(String)
                assert link != ""

                assert dataset2
                assert dataset2.equals(dataset1)
                uc.response.reset()
                uc.response.resetBuffer()
                uc.request.clearAttributes()
                uc.request.removeAllParameters()
                ///////////////////REPLICATE!!!!!!!!!!

                //Use Manage Data Controller
                def mdc = new ManageDataController()
                mdc.fileImportService = fileImportService
                mdc.fileService =fileService
                mdc.springSecurityService.reauthenticate("User","123User")
                assert mdc.springSecurityService.isLoggedIn()
                mdc.springSecurityService = springSecurityService

                //Make External Source Descriptiom
                ExternalSourceDescription esd = new ExternalSourceDescription(description: "Myself",name:"adasdasdasdsdgasfgasdfasdfasfvdsfrbtbwerfqw te rgqvrq34tw4rtqwegweth",uRL: grailsApplication.config.grails.serverURL )
                esd.save()

                //Set Params
                mdc.params.externalSourceId =esd.id
                mdc.params.RequestResource =link
                //Fire Controller
                mdc.dataImportFromExternalSource()
                assert mdc.response.status == 200
                ModelAndView maw=mdc.modelAndView
                //assert mdc.flash.message == 'Please Map your Data'
                assert maw.model.importProtocol
                ImportProtocol importProtocol = maw.model.importProtocol
                assert !importProtocol.successful


        }
}