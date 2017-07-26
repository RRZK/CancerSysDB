package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Action
import org.openqa.selenium.interactions.Actions
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * This test a Full import with Context:
 *
 *
 */
@Stepwise
class ImportWithContextandMapping2FunctionalSpec extends GebReportingSpec {

    private Random rand = new Random()

    @Shared
    // in ms
    def pausa = 0
    def pausb = 0
    def pausc = 0
    def setup() {
        GrailsApplication grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
        pausa = grailsApplication.config.test.slowFunctional.pausa ?:0
        pausb = grailsApplication.config.test.slowFunctional.pausb ?:0
        pausc = grailsApplication.config.test.slowFunctional.pausc ?:0
    }
    def uniquename
    def "Import with mapping"() {

        setup:
        uniquename="a"+rand.nextInt(1000000)
        when:
        to loginPage
        sleep(pausa)


        then:
        at loginPage
        $("#priMenu > li > a").find{ it.text().contains("Login")}



        when:"Log In"
        dologin("User","123User")
        sleep(pausa)



        then:
        $("#priMenu > li > a").find{ it.text().contains("Logout")}





        when:"Goto Upload"
        //Action Examples
        /*        new Actions(driver).moveToElement($("#priMenu > li > a[href*=about]").firstElement()).perform()
        sleep(pausa)
        new Actions(driver).moveToElement($("#priMenu > li > a[href*=dataOverview]").firstElement()).perform()
        sleep(pausa)*/



        new Actions(driver).moveToElement($("#priMenu > li > a[href*=manageData]").firstElement()).click().perform();

        sleep(pausa)
        then:
        $("#secMenu > li > a").find{ it.text().equals("Data Files")}





        when:
        $("#secMenu > li > a").find{ it.text().equals("Data Files")}.click()
        sleep(pausa)
        then:
        // Important Elements for the next steps
        !$("#contextUploadFileSelector").empty
        !$("#contentUploadFileSelector").empty
        !$("#createcontextButton").empty
        !$("#upload").empty





        when:
        def path = "testData/xml/contentBiospecies.xml"
        File  f= new File(path);
        then:

        assert f.exists()
        when:
        $("#Contextfile").click()
        sleep(pausb)
        new Actions(driver).moveToElement($("#contextUploadFileSelector").firstElement()).perform();
        $("#ContextUpload").Contextfile = f.absolutePath
        sleep(pausb)

        $("#createcontextButton").click()
        sleep(pausa)

        then:
        $("#contextUploadFileSelector").empty
        $("#createcontextButton").empty



        when:

        path = "testData/csv/DataVariationtest.csv"

        f= new File(path);

        then:
        assert f.exists()
        assert $("#CSVUpload")
        assert $("#contentUploadFileSelector").getAttribute("name").equals("file")
        when:
        $("#CSVUpload").file = f.absolutePath
        sleep(pausa)

        SetField("dataType","Variation")


        $("#upload").click()
        sleep(pausa)

        then:
        assert $("#CSVUpload") ||$("#importStatus").text().equals("Import Successful")


        when:

        GoodSetField("totalDepthCtrl", "--Dont Map--")

        GoodSetField("refDepthCtrl", "--Dont Map--")

        GoodSetField("altDepthCtrl", "--Dont Map--")

        GoodSetField("chromosome", "Kromo")


        GoodSetField("startPos", "Start")


        GoodSetField("endPos", "Ente")

        GoodSetField("refAllele", "rA")

        GoodSetField("altAllele", "aA")

        GoodSetField("totalDepth", "tD")

        GoodSetField("refDepth", "refD")

        GoodSetField("altDepth", "aD")

        GoodSetField("callerID", "--Dont Map--")

        GoodSetField("genotype", "genO")

        GoodSetField("qualityScore", "qualiteit")

        GoodSetField("freq", "--Dont Map--")

        sleep(pausa)
        sleep(pausa)
        $("#upload").click()
        sleep(pausa)


        then:
        $("#CSVImportResult > h2").text().equals("Import Successful")
        when:"Goto Result Dataset"

        go $("a").find{it.text().contains("Show") }.getAttribute("href")
        sleep(pausa)
        then:
        $("#datatype0").find{it.text().contains("Variation")} || $("#errorType").find{ it.text().contains("401")}||$("#errorType").find{ it.text().contains("403")}

    }
    def void GoodSetField(String name, String value){

        if($("#CSVUpload")."Mapping_$name" != value){

            $("#Mapping_$name").click()
            sleep(pausc)
            $("#CSVUpload")."Mapping_$name" = value
            sleep(pausb)
        }

    }

    def void SetField(String name, String value){

        if($("#CSVUpload")."$name" != value){

            $("#$name").click()
            sleep(pausc)
            $("#CSVUpload")."$name" = value
            sleep(pausb)
        }

    }

}