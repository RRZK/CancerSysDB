package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.openqa.selenium.interactions.Actions
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * This test a Full import with Context:
 *
 *
 */
@Stepwise
class ImportWithContextAndMetadataFunctionalSpec extends GebReportingSpec {

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
    def "Import with Metadata"() {

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
        File  f= new File(path)
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

        path = "testData/clinical/Testdata-clinical.Testproj-A2-A3FX.xml"
        f= new File(path)

        then:
        assert f.exists()
        assert $("#MetaUpload")
        assert !$("#createmetaButton").empty


        when:
        $("#MetaUpload").Metadatafile = f.absolutePath
        sleep(pausa)
        def jqScrollToVisible = 'document.getElementById("createmetaButton").scrollIntoView();'
        js.exec(jqScrollToVisible)
        def mbutton = $("#createmetaButton")
        mbutton.click()
        sleep(pausa)

        then:

        $("#importStatus").text().equals("Import Successful")
        when:

        def patient=   $("#CSVImportResult > span > a").find{it.text().contains("Show") }.getAttribute("href")
        go patient
        then:
        //Check some Values definitly in this thing!
        $(text: "//tcga_bcr/patient/radiations/radiation[1]")
/*        $(text: "//tcga_bcr/patient")
        !$("input",class:"delete")*/
        //TODO Check Delete Function!
        /*   when:

          go "/logout"
         dologin("Manager","123Manager")
          go patient
          then:
          $("input",class:"delete")*/



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