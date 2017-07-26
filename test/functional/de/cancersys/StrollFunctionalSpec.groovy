package de.cancersys

import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
/*
import grails.plugin.remotecontrol.RemoteControl
*/
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.openqa.selenium.interactions.Actions
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class StrollFunctionalSpec extends GebReportingSpec {
    // in ms
    Long pausa = 0
    Long pausb = 0
    Long pausc = 0

    def setup() {
        GrailsApplication grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
        pausa = grailsApplication.config.test.slowFunctional.pausa ?: 0
        pausb = grailsApplication.config.test.slowFunctional.pausb ?: 0
        pausc = grailsApplication.config.test.slowFunctional.pausc ?: 0
    }

    def "Check All menu Items"() {

        when:
        go ""
        def allErrors = []

        int mains_num = 0
        int mainssize = $("#priMenu > li > a").allElements().size()
        assert mainssize > 0
        while (mains_num < mainssize) {

            def mainz = $("#priMenu > li > a")
            assert mainz.size() > 0

            def main = mainz.allElements()[mains_num]
            assert main
            new Actions(driver).moveToElement(main).perform();
            sleep(pausa)
            main.click()
            sleep(pausa)

            if (!$("#errorType").empty) {
                allErrors.add(main)
            }

            int secs_num = 0
            int secssize = $("#secMenu > li > a").allElements().size()

            while (secs_num < secssize) {

                def secs = $("#secMenu > li > a")
                def sec = secs.allElements()[secs_num]

                new Actions(driver).moveToElement(sec).perform();
                sleep(pausa)
                sec.click()
                sleep(pausa)

                if (!$("#errorType").empty) {
                    allErrors.add(sec)
                }
                secs_num++
            }
            mains_num++
        }


        then:

        assert allErrors.size() == 0

    }

}