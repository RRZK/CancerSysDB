package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
/*
import grails.plugin.remotecontrol.RemoteControl
*/
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.omg.CORBA.Environment
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * This test checks:
 *  1. if the Return value in the test enviroment is right
 *  2. if out of Bound Requests are answered with a 404 (not found) error
 *
 *
 */
@Stepwise
class FailureFunctionalSpec extends GebReportingSpec {
    // in ms
    Long pausa = 0
    Long pausb = 0
    Long pausc = 0
    def setup() {
        GrailsApplication grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
        pausa = grailsApplication.config.test.slowFunctional.pausa //?:0
        pausb = grailsApplication.config.test.slowFunctional.pausb //?:0
        pausc = grailsApplication.config.test.slowFunctional.pausc //?:0
    }
    def "Goto the Login"() {

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
    def "Goto call invalid gene"() {

        setup:

        def largenumber = "1000000000000"

        when:

        go "gene/show/"+largenumber
        sleep(pausa)

        then:
        //assert grails.util.Environment == grails.util.Environment.TEST
        assert $("#errorType")
        $("#errorType").find{ it.text().contains("404")}

    }

    def "Goto call invalid Dataset"() {

        setup:

        def largenumber = "1000000000000"

        when:

        go "dataset/show/"+largenumber
        sleep(pausa)
        then:

        $("#errorType").find{ it.text().contains("404")}

    }
    def "Goto call invalid patient"() {

        setup:

        def largenumber = "1000000000000"

        when:

        go "patient/show/"+largenumber
        sleep(pausa)
        then:

        $("#errorType").find{ it.text().contains("404")}

    }
    def "Goto call invalid sample"() {

        setup:

        def largenumber = "1000000000000"

        when:

        go "sample/show/"+largenumber
        sleep(pausa)
        then:

        $("#errorType").find{ it.text().contains("404")}

    }
    def "Goto call invalid study"() {

        setup:

        def largenumber = "1000000000000"

        when:

        go "study/show/"+largenumber
        sleep(pausa)
        then:

        $("#errorType").find{ it.text().contains("404")}

    }

}