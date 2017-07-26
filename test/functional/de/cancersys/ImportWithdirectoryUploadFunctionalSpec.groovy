package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * This Test
 *
 *
 */
@Stepwise
class ImportWithdirectoryUploadFunctionalSpec extends GebReportingSpec {
    // in must be Implemented
    Long pausa = 0
    Long pausb = 0
    Long pausc = 0
    def setup() {
        GrailsApplication grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
        pausa = grailsApplication.config.test.slowFunctional.pausa ?:0
        pausb = grailsApplication.config.test.slowFunctional.pausb ?:0
        pausc = grailsApplication.config.test.slowFunctional.pausc ?:0
    }
    @Shared
    def execscript = "web-app/Tools/UploadScript.py"
    def pathToScan = "testData/testdatafolder"
    def user = "User"
    def pw = "123User"
    def result = ""
    def "Testfiles exist ?"(){
        when:
        def f = new File(execscript)

        then:
        assert f.exists()
        when:
         f = new File(pathToScan)

        then:
        assert f.exists()



    }
    def "Script Runnable"() {

        when:
        def process = "python --version".execute()
        process.waitFor()
        then:
        process.exitValue() == 0


    }

    def "Start Script for Running"() {

        when:
        def f = new File(execscript)
        def Scriptp =f.getAbsoluteFile()
        def process = "python $Scriptp -h".execute()
        process.waitFor()
        then:

        process.exitValue() == 0
        process.text.startsWith("Usage:")
    }

    def "Script upload Context "() {


        setup:"All the File Stuff must be Setup"
        def f = new File(execscript)
        def fde = new File(pathToScan)
        def Scriptp =f.getAbsoluteFile()

        when:"Command is executed"
        def text =new StringBuilder()
        def error =new StringBuilder()
        def command =  "python $Scriptp -u $user -p $pw -f testdata/testdatafolder/ -H http://localhost:8080/${grails.util.Metadata.current.getApplicationName()}/"
        def process = command.execute()
        def out =process.getOutputStream()

        process.waitForProcessOutput(text, error)
        then:"Test Results"

        process.exitValue() == 0
        !text.toString().contains("errors")



        //TODO Delete all imported Data...
/*


        when:
        result= text.substring( text.indexOf("Result: ") +  "Result: ".length())
        result = result.trim()
        then:
        result.startsWith("http")
        when:
        if(!$("#priMenu > li > a").find{ it.text().contains("Logout")}){
            to loginPage
            dologin(user, pw)
        }

        then:
        $("#priMenu > li > a").find{ it.text().contains("Logout")}
        when:"Goto the Page of the Result"

        go result
        sleep(pausa)

        then:"Check if there are Errors"

        !( $("#errorType") && $("#errorType").text().trim() in ["401","403","404","451"])*/
    }
}