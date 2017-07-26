package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
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
class ImportWithContextandMappingFunctionalSpec extends GebReportingSpec {
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
        when:"Goto the Login"
        to loginPage
        sleep(pausb)


        then:
        at loginPage
        $("#priMenu > li > a").find{ it.text().contains("Login")}



        when:"Log In"
        dologin("User","123User")
        sleep(pausa)



        then:
        $("#priMenu > li > a").find{ it.text().contains("Logout")}




        when:"Goto Upload"
        $("#priMenu > li > a").find{ it.text().equals("Data Upload")}.click()

        then:
        $("#secMenu > li > a").find{ it.text().equals("Data Files")}




        when:"Goto Upload2"
        $("#secMenu > li > a").find{ it.text().equals("Data Files")}.click()

        then:
        // Important Elements for the next steps
        !$("#contextUploadFileSelector").empty
        !$("#contentUploadFileSelector").empty
        !$("#createcontextButton").empty
        !$("#upload").empty




        then:"Upload1"

        def path = "testData/xml/contentBiospecies.xml"




        when:
        File  f= new File(path);
        then:

        assert f.exists()
        when:
        $("#ContextUpload").Contextfile = f.absolutePath
        sleep(pausa)
        //$("#contextUploadFileSelector").Contextfile = f.absolutePath
        new Actions(driver).moveToElement($("#createcontextButton").firstElement()).perform()

        $("#createcontextButton").click()
        sleep(pausa)
        then:
        $("#contextUploadFileSelector").empty
        $("#createcontextButton").empty



        when:"Upload2"
        path = "testData/csv/contentTOGeneticAbundance.csv"
        f= new File(path);

        then:
        assert f.exists()
        assert $("#CSVUpload")
        assert $("#contentUploadFileSelector").getAttribute("name").equals("file")
        when:
        $("#CSVUpload").file = f.absolutePath
        sleep(pausa)

        $("#CSVUpload").dataType = "TranscriptAbundance"
        sleep(pausa)
        $("#upload").click()
        sleep(pausa)
        then:
        assert $("#CSVUpload") ||$("#importStatus").text().equals("Import Successful")

        when:"Map"
        if( $("#CSVUpload > h2").find{ it.text().equals("File Import Mapping")}){

            GoodSetField("fpkm","fpkm")
            GoodSetField("fpkmOK","fpkmok")

            GoodSetField("gene","gene")

            $("#upload").click()
            sleep(pausa)

        }
        then:
        $("#importStatus").text().equals("Import Successful")
        when:"Goto Result Dataset"
        sleep(pausa)
        sleep(pausa)
        sleep(pausa)
        sleep(pausa)
        go $("a").find{it.text().contains("Show") }.getAttribute("href")
        sleep(pausa)
        then:
        $("#datatype0").find{it.text().contains("Abundance")} || $("#errorType").find{ it.text().contains("401")}||$("#errorType").find{ it.text().contains("403")}

    }


    def void GoodSetField(String name, String value){

        if($("#CSVUpload")."Mapping_$name" != value){

            $("#Mapping_$name").click()
            sleep(pausc)
            $("#CSVUpload")."Mapping_$name" = value
            sleep(pausb)
        }

    }

}