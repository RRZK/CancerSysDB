package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.openqa.selenium.interactions.Actions
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * This test a Full import with Context:
 *
 *
 */
@Stepwise
class ImportWithContextFunctionalSpec extends GebReportingSpec {
    // in ms
    Long pausa = 0
    Long pausb = 0
    Long pausc = 0
    def setup() {
        GrailsApplication grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
        pausa = grailsApplication.config.test.slowFunctional.pausa ?:0
        pausb = grailsApplication.config.test.slowFunctional.pausb ?:0
        pausc = grailsApplication.config.test.slowFunctional.pausc ?:0
    }
    private Random rand = new Random()
    @Shared
    def uniquename
    def "Goto the Login"() {

        setup:
        uniquename="a"+rand.nextInt(1000000)
        when:
        to loginPage
        sleep(pausa)


        then:
        at loginPage
        $("#priMenu > li > a").find{ it.text().contains("Login")}

    }
    def "Log In"() {

        when:
        dologin("User","123User")
        sleep(pausa)



        then:
        $("#priMenu > li > a").find{ it.text().contains("Logout")}

    }
    def "Goto Upload"() {


        when:
        $("#priMenu > li > a").find{ it.text().equals("Data Upload")}.click()

        then:
        $("#secMenu > li > a").find{ it.text().equals("Data Files")}


    }
    def "Goto Upload2"() {


        when:
        $("#secMenu > li > a").find{ it.text().equals("Data Files")}.click()

        then:
        // Important Elements for the next steps
        !$("#contextUploadFileSelector").empty
        !$("#contentUploadFileSelector").empty
        !$("#createcontextButton").empty
        !$("#upload").empty

    }

    def "Upload1"() {


        setup:

        def path = "testData/xml/contentBiospecies.xml"




        when:
        File  f= new File(path);
        then:

        assert f.exists()
        when:
        $("#ContextUpload").Contextfile = f.absolutePath
        //$("#contextUploadFileSelector").Contextfile = f.absolutePath
        sleep(pausa)
        new Actions(driver).moveToElement($("#createcontextButton").firstElement()).perform()
        $("#createcontextButton").displayed
        $("#createcontextButton").click()
        sleep(pausa)

        then:
        $("#contextUploadFileSelector").empty
        $("#createcontextButton").empty


    }
    def "Upload2"() {

        setup:
        def servletContext = ServletContextHolder.servletContext

        def path = "testData/vcf/contentvcf.vcf"

        when:
        File  f= new File(path);

        then:
        assert f.exists()
        assert $("#CSVUpload")
        assert $("#contentUploadFileSelector").getAttribute("name").equals("file")
        when:
        $("#CSVUpload").file = f.absolutePath
        sleep(pausa)


        $("#CSVUpload").dataType = "Variation"
        sleep(pausa)

        $("#upload").click()
        sleep(pausa)

        then:
        $("#CSVImportResult")
        $("#importStatus").text().equals("Import Successful")
        $("#CSVImportResult > span > a").find{it.text().contains("Show") }.getAttribute("href")
    }

    def "Check"() {

        when:

        go $("#CSVImportResult > span > a").find{it.text().contains("Show") }.getAttribute("href")
        sleep(pausa)

        then:
        $("#datatype0").find{it.text().contains("Variation")}
    }

}