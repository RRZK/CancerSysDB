package de.cancersys
/*
import grails.plugin.remotecontrol.RemoteControl
*/
import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.*

@Stepwise
class ReplicationFunctionalSpec extends GebReportingSpec {

    private Random rand = new Random()
    @Shared
    def uniquename
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

    def "Goto the Login"() {

        setup:
        uniquename="a"+rand.nextInt(1000000)
        when:
        to loginPage


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
    def "Open Page and check Healine and Submit Button"() {


        when:
        go()
        sleep(pausa)

        go "manageData/dataImportFromExternalSource"
        sleep(pausa)

        then:
        $("h1").find{ it.text().contains("Import")}
        $("#ExternalResourceSubmit")
        $("#externalSourceDescriptionInstance_description")
        $("#externalSourceDescriptionInstance_descriptionReference")
        $("#externalSourceDescriptionInstance_uRL")
    }
    def "Insert Data to the Form and Submit"() {

        when:

        $("#externalSourceDescriptionInstance_description") << "Myself"
        sleep(pausb)

        $("#externalSourceDescriptionInstance_name") << uniquename
        sleep(pausb)

        $("#externalSourceDescriptionInstance_descriptionReference") << browser.baseUrl
        sleep(pausb)

        $("#externalSourceDescriptionInstance_uRL") << browser.baseUrl
        sleep(pausb)

        $("#ExternalResourceSubmit").click()
        sleep(pausa)

        then:
        $("#externalSourceChoose")
    }

    def "Import Dataset"() {


        when:
        def datasettoImportLink =browser.baseUrl +"dataset/show/1"
        $("form").externalSourceChoose = uniquename
        sleep(pausb)




        $("#RequestResource") <<  datasettoImportLink
        sleep(pausa)

        $("#RequestExternalResource").click()
        sleep(pausa)


        then:
        $("#externalSourceChoose")
    }


}