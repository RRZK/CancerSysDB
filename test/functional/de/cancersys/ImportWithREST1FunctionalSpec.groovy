package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * This test a Full import with Context:
 *
 *
 */
@Stepwise
class ImportWithREST1FunctionalSpec extends GebReportingSpec {
    // in ms
    Long pausa = 0
    Long pausb = 0
    Long pausc = 0
    String thisUrl
    def setup() {
        GrailsApplication grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
        pausa = grailsApplication.config.test.slowFunctional.pausa ?:0
        pausb = grailsApplication.config.test.slowFunctional.pausb ?:0
        pausc = grailsApplication.config.test.slowFunctional.pausc ?:0
    }
    @Shared
    def path = "web-app/Tools/UploadScript.py"
    def description = "testData/xml/contentBiospecies.xml"
    def data ="testData/vcf/contentvcf.vcf"
    def data2 ="testData/trimmedannotatedgenequantification/test1.trimmed.annotated.gene.quantification.txt"
    def user = "User"
    def pw = "123User"
    def result = ""
    def "Testfiles exist ?"(){
        when:
        def f = new File(path)

        then:
        assert f.exists()
        when:
         f = new File(description)

        then:
        assert f.exists()
        when:
         f = new File(data)

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
        def f = new File(path)
        def Scriptp =f.getAbsoluteFile()
        def process = "python $Scriptp -h".execute()
        process.waitFor()
        then:

        process.exitValue() == 0
        process.text.startsWith("Usage:")
    }
    def "Script Upload One Variation from VCF"() {


        setup:"All the File Stuff must be Setup"
        def f = new File(path)
        def fde = new File(description)
        def fda = new File(data)
        def Scriptp =f.getAbsoluteFile()
        def context = fde.getAbsoluteFile()
        def data = fda.getAbsoluteFile()

        when:"Command is executed"
        def text =new StringBuilder()
        def error =new StringBuilder()


        //thisUrl = browser.baseUrl
//        def command =  "python $Scriptp -u $user -p $pw -f $data -t Variation -l $context -H http://localhost:8090/${grails.util.Metadata.current.getApplicationName()}/"
        def command =  "python $Scriptp -u $user -p $pw -f $data -t Variation -l $context -H $browser.baseUrl"

        def process = command.execute()
        def out =process.getOutputStream()

        process.waitForProcessOutput(text, error)
        then:"Test Results"

        process.exitValue() == 0

        when:
        //String endstatement = "EndresultData:"
        def Jsontext = text.toString().subSequence(text.indexOf("{"),text.length())

        def json = JSON.parse(Jsontext)

        then:
        json.containsKey("stat")
        json.get("stat").equals("successful")

        when:
        json.containsKey("link")
        result= json.get("link").toString()
        then:
        result.startsWith("http")

        when:
        to loginPage
        then:
        at loginPage
        when:
        dologin(user, pw)

        then:

        $("#priMenu > li > a").find{ it.text().contains("Logout")}


        when:"Goto the Page of the Result"

        go result
        sleep(pausa)

        then:"Check if there are Errors"
        !( $("#errorType") && $("#errorType").text().trim() in ["401","403","404","451"])

    }
    def "Script Upload TranscriptAbundance from trimmed annotated gene quantification "() {


        setup:"All the File Stuff must be Setup"
        def f = new File(path)
        def fde = new File(description)
        def fda = new File(data2)
        def Scriptp =f.getAbsoluteFile()
        def context = fde.getAbsoluteFile()
        def data = fda.getAbsoluteFile()

        when:"Command is executed"
        def text =new StringBuilder()
        def error =new StringBuilder()
        def command =  "python $Scriptp -u $user -p $pw -f $data -t TranscriptAbundance -l $context -H $browser.baseUrl"
        def process = command.execute()
        def out =process.getOutputStream()

        process.waitForProcessOutput(text, error)
        then:"Test Results"

        process.exitValue() == 0
        when:
        def Jsontext = text.toString().subSequence(text.indexOf("{"),text.length())

        def json = JSON.parse(Jsontext)
/*        String endstatement = "EndresultData:"
        def json = JSON.parse(text.toString().subSequence(text.indexOf(endstatement)+endstatement.length(),text.length()))*/
        then:
        json.containsKey("stat")
        json.get("stat").equals("successful")

        when:
        json.containsKey("link")
        result= json.get("link").toString()
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

        !( $("#errorType") && $("#errorType").text().trim() in ["401","403","404","451"])
    }
}