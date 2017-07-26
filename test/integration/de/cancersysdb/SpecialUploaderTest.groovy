package de.cancersysdb

import de.cancersysdb.data.DataTranscriptAbundance
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.json.JSONElement

/**
 * Created by rkrempel on 07.07.15.
 */
class SpecialUploaderTest extends GroovyTestCase {

        def fileImportService
        FileService fileService
        def springSecurityService



        //This Thing is Scheisse....
        void testUpload() {
                def servletContext = ServletContextHolder.servletContext

                def uc = new UploadController()
                uc.fileImportService = fileImportService
                uc.fileService = fileService
                uc.springSecurityService.reauthenticate("User", "123User")
                assert uc.springSecurityService.isLoggedIn()
                uc.springSecurityService = springSecurityService
                //Create Dataset
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



                def path = "testData/csv/contentTOGeneticAbundance2.csv"
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
                        "file", "abu.csv", "text/plain", text.bytes
                )

                uc.request.addFile(file)
                def temp1 = DataTranscriptAbundance.count()

                assert DataTranscriptAbundance.class.simpleName.equals("DataTranscriptAbundance")

                uc.params.dataType = "DataTranscriptAbundance"
                uc.params.dataset= dataset1.id


                uc.uploadToDataset()


                assert uc.response.status == 200



        }

        void testCreationOfDataset() {

                def uc = new UploadController()
                uc.fileImportService = fileImportService
                uc.fileService = fileService


                uc.springSecurityService.reauthenticate("User", "123User")
                assert uc.springSecurityService.isLoggedIn()
                uc.springSecurityService = springSecurityService
                uc.params.put("annon", true)
                uc.params.put("shared", true)
                uc.params.put("description", "Upĺoaded Test Stuff")
                uc.params.put("samples", "1,2,3,4,5")
                def tempcount = Dataset.count()

                uc.CreateDataset()
                assert uc.response.status == 200
                assert tempcount<Dataset.count()

        }

}