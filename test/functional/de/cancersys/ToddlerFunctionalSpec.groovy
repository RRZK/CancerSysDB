package de.cancersys

import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Stepwise
import org.apache.commons.lang.RandomStringUtils
/**
 * This tests Simulates Random Input Clicking etc. It just looks that no Internal Errors are Happening
 */
@Stepwise
class ToddlerFunctionalSpec extends GebReportingSpec {
    // in ms
    Long pausa = 1
    Long pausb = 1
    Long pausc = 1
    Integer inner =10
    Integer outer =10

    def setup() {
        GrailsApplication grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
        pausa = grailsApplication.config.test.slowFunctional.pausa ?:0
        pausb = grailsApplication.config.test.slowFunctional.pausb ?:0
        pausc = grailsApplication.config.test.slowFunctional.pausc ?:0
    }
    private Random rand = new Random()
    private Set PagesWithForms =[] as Set
    private List Outerlinks  =[]


    def "ADV Toddling"() {
        setup: "Bootstrapping for Random Walk"
        Boolean nonewlinks =false
        List Errors = []
        Set GoneLinks = [] as Set
        when: "Random Walking"
        for(int outer =0; outer<outer; outer++){
            nonewlinks =false
            go ""
            sleep(pausb)

            def currURL = ""
            def Aktupath = new ArrayList<String>()
            def temp =[]
            for( int i = 0; i< inner && !nonewlinks ; i++){
                //On New Page ?
                temp = []
                temp = $("a").findAll { it.displayed }
                temp += $("input[type=button]").findAll { it.displayed }
                temp += $("input[type=submit]").findAll { it.displayed }

                currURL = browser.driver.currentUrl

                if (!$("form").isEmpty() && $("form").any{it.displayed})
                    PagesWithForms.add(currURL)

                if(temp.isEmpty() ){
                    nonewlinks =true
                    continue
                }
                else {
                    def link
                    int sec = 0
                    boolean goOn = true
                    //SELECT and Link Element from the potential Candidates with given Restrictions!
                    while (goOn) {

                        link = temp.getAt(rand.nextInt(temp.size()));

                        sec = sec + 1
                        if (!link) {
                            goOn = true
                        } else if (link.tag() == "a" && link.@href && !Aktupath.contains(link.@href) && (GoneLinks.contains(link.@href) && rand.nextBoolean())) {
                            goOn = true
                        } else if (Aktupath.contains(link.toString())) {
                            goOn = true
                        } else if (link.@target != "") {
                            goOn = true
                            Outerlinks.add(link.@href)
                        } else
                            goOn = false

                        if (sec > 100) {
                            goOn = false
                            link = null

                        }
                    }

                    if (link == null)
                        break
                    if (link.@href) {

                        Aktupath.add(link.@href)
                        GoneLinks.add(link.@href)
                    }
                    else
                        link.toString()

                    link.click()
                    sleep(pausb)

                }
                if($("ul[class=errors]")){
                    Errors.add(Aktupath)
                    break
                }

            }
        }
        then: "Collected Any Errors?"
        Errors.isEmpty()
        and: "Setup for Spamming Forms"
        int errors =0

        when:"Spamming Found Forms"
        for(int outer =0; outer<10 && outer < PagesWithForms.size()  ; outer++){
            //Goto Form
            PagesWithForms.toSet().each {
                go(it)
                sleep(pausb)

            }
            for(int i =0;i<5;i++){
                def submit

                $("form").findAll{it.displayed}.each {
                    $("input").each {

                        if (it.@type == "submit")
                            submit = it
                        else if (it.@type == "checkbox") {

                            if (rand.nextBoolean())
                                it.click()

                        } else if (it.@type == "button") {
                            if (submit == null)
                                submit = it

                        } else if (it.@type == "text") {

                            it << RandomStringUtils.random(9, true, true)
                            sleep(pausb)

                        }

                    }
                    $("textarea").each {

                        it << RandomStringUtils.random(9, true, true)
                        sleep(pausb)

                    }
                    if (submit) {

                        submit.click()
                        sleep(pausb)

                        if($("ul[class=errors]")){
                            errors =errors+1
                        }
                    }

                }



            }
        }
        then:
        assert errors ==0

    }

}