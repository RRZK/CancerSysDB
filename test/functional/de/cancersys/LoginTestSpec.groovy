package de.cancersys
import de.cancersys.pages.loginPage
import geb.spock.GebReportingSpec
import spock.lang.Stepwise


@Stepwise
class LoginTestSpec extends GebReportingSpec {

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
        dologin("User","123User")


        then:
        $("#priMenu > li > a").find{ it.text().contains("Logout")}

    }


}