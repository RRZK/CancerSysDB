package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import spock.lang.Stepwise


@Stepwise

class ImportWorkflowFunctionalSpec extends GebReportingSpec  {

        def "Goto the Login"() {

            setup:
            when:
            to loginPage


            then:
            at loginPage
            $("#priMenu > li > a").find{ it.text().contains("Login")}

        }
        def "Log In"() {

            when:
            dologin("Admin", "123Admin")

            then:

            $("#priMenu > li > a").find { it.text().contains("Logout") }
        }

    def "Deleting Workflow"() {
        when:
        go "showcase/index"
        then:
        $("#workflowList >tbody > tr > td:nth-child(3) > a")
        when:
        List workflows = $("#workflowList >tbody > tr > td:nth-child(3) > a").collect { it.getAttribute("href") }
        def wfs = workflows.size()
        workflows.each {

            go it
            def theElement
            if ($("#identifierconceptualWorkflow").attr("data-workflowindentifier").equals("CologneWorkflow")) {
/*
                new Actions(driver).moveToElement($(".delete").firstElement()).perform()
*/              WebElement element = $(".delete").firstElement()
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element)
                Thread.sleep(500)
                $(".delete").first().displayed

                withConfirm(true) { $(".delete").click() }
            } else
                $('h2 > a').each {
                    if(it.text().equals("back"))
                    theElement = it
                }
            if(theElement)
                theElement.click()

        }
        workflows = $("#workflowList >tbody > tr > td:nth-child(3) > a").collect { it.getAttribute("href") }
        def wfs2 = workflows.size()


        then:
        wfs2 < wfs
        $("#workflowList >tbody > tr > td:nth-child(3) > a")
    }
    def "Importing Workflow"() {
            when:
            go "admin/importWorkflow"
            then:
            $("#metaFileUploadSelector")
            $("#ZipFileUploadSelector")
            when:
            def pathJson = "web-app/data/Workflows/CologneWorkflow.json"
            def pathZip = "web-app/data/Workflows/CologneWorkflow.zip"

            File  fJson= new File(pathJson)
            File  fZip= new File(pathZip)

            then:

            assert fZip.exists()
            assert fJson.exists()

            when:



            $("#UploadWorkflow").Metadatafile = fJson.absolutePath
            $("#UploadWorkflow").ZipFile =  fZip.absolutePath
            $("#createWorkflow").click()
            waitFor {
                !$("h1").empty
            }
        then:

            $("h1").text().equals("Upload Successful")
    }

    def "Checking Results"() {

        when:

            $('#result').click()
        then:
            $("#identifierconceptualWorkflow").attr("data-workflowindentifier").equals("CologneWorkflow")


        }

    }

