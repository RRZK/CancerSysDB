package de.cancersysdb.contextHandling

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

import java.util.regex.Matcher

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class CancersysBarcodeConventionsSpec extends Specification  {
    String test
    def setup() {
    }

    def cleanup() {
    }




    void "TCGA Zero"() {

        when:
        test = "TCGA"
        then:
        CancersysBarcodeConventions.isStudyBarcode(test)
        CancersysBarcodeConventions.containsStudyBarcode(test)
        !CancersysBarcodeConventions.isPatientBarcode(test)
        !CancersysBarcodeConventions.containsPatientBarcode(test)
        !CancersysBarcodeConventions.isSampleBarcode(test)
        !CancersysBarcodeConventions.containsSampleBarcode(test)

        CancersysBarcodeConventions.BarcodeToStudy(test) == "TCGA"
    }
    void "TCGA One"() {

        when:
        test = "TCGA-01"
        then:
        !CancersysBarcodeConventions.isStudyBarcode(test)
        CancersysBarcodeConventions.containsStudyBarcode(test)
        !CancersysBarcodeConventions.isPatientBarcode(test)
        !CancersysBarcodeConventions.containsPatientBarcode(test)
        !CancersysBarcodeConventions.isSampleBarcode(test)
        !CancersysBarcodeConventions.containsSampleBarcode(test)

        CancersysBarcodeConventions.BarcodeToStudy(test) == "TCGA"

        CancersysBarcodeConventions.BarcodeToStudy(test) != "TCGA-01"
    }

    void "TCGA Two"() {
        when:
        test = "TCGA-01-01AX"

        then:
        !CancersysBarcodeConventions.isStudyBarcode(test)
        CancersysBarcodeConventions.containsStudyBarcode(test)
        CancersysBarcodeConventions.isPatientBarcode(test)
        CancersysBarcodeConventions.containsPatientBarcode(test)
        !CancersysBarcodeConventions.isSampleBarcode(test)
        !CancersysBarcodeConventions.containsSampleBarcode(test)
        CancersysBarcodeConventions.BarcodeToStudy(test) == "TCGA"
        CancersysBarcodeConventions.BarcodeToPatient(test) == "TCGA-01-01AX"

    }
    void "TCGA Three"() {

        when:
        test = "TCGA-01-01AX-01a"
        then:
        !CancersysBarcodeConventions.isStudyBarcode(test)
        CancersysBarcodeConventions.containsStudyBarcode(test)
        !CancersysBarcodeConventions.isPatientBarcode(test)
        CancersysBarcodeConventions.containsPatientBarcode(test)
        CancersysBarcodeConventions.isSampleBarcode(test)
        CancersysBarcodeConventions.containsSampleBarcode(test)
        CancersysBarcodeConventions.BarcodeToSample(test) == "TCGA-01-01AX-01a"
        CancersysBarcodeConventions.BarcodeToPatient(test) == "TCGA-01-01AX"

        CancersysBarcodeConventions.BarcodeToStudy(test) == "TCGA"


    }
    void "TCGA Four"() {

        when:
        test = "TCGA-01-01AX-01a-112"
        then:
        !CancersysBarcodeConventions.isStudyBarcode(test)
        CancersysBarcodeConventions.containsStudyBarcode(test)
        !CancersysBarcodeConventions.isPatientBarcode(test)
        CancersysBarcodeConventions.containsPatientBarcode(test)
        !CancersysBarcodeConventions.isSampleBarcode(test)
        CancersysBarcodeConventions.containsSampleBarcode(test)

        CancersysBarcodeConventions.BarcodeToStudy(test) == "TCGA"
        CancersysBarcodeConventions.BarcodeToPatient(test) == "TCGA-01-01AX"
        CancersysBarcodeConventions.BarcodeToSample(test) == "TCGA-01-01AX-01a"
        CancersysBarcodeConventions.BarcodeToSample(test) != "asd-sss-1a"


    }
    void "Costum1 Zero"() {

        when:
        test = "OESOPH"
        then:
        CancersysBarcodeConventions.isStudyBarcode(test)
        CancersysBarcodeConventions.containsStudyBarcode(test)
        !CancersysBarcodeConventions.isPatientBarcode(test)
        !CancersysBarcodeConventions.containsPatientBarcode(test)
        !CancersysBarcodeConventions.isSampleBarcode(test)
        !CancersysBarcodeConventions.containsSampleBarcode(test)

        CancersysBarcodeConventions.BarcodeToStudy(test) == "OESOPH"


    }
    void "Costum1 one"() {

        when:
        test = "OESOPH-UKK-3607-01A"
        then:
        !CancersysBarcodeConventions.isStudyBarcode(test)
        CancersysBarcodeConventions.containsStudyBarcode(test)
        !CancersysBarcodeConventions.isPatientBarcode(test)
        CancersysBarcodeConventions.containsPatientBarcode(test)
        CancersysBarcodeConventions.isSampleBarcode(test)
        CancersysBarcodeConventions.containsSampleBarcode(test)

        CancersysBarcodeConventions.BarcodeToStudy(test) == "OESOPH"
        CancersysBarcodeConventions.BarcodeToPatient(test) == "OESOPH-UKK-3607"
        CancersysBarcodeConventions.BarcodeToSample(test) == "OESOPH-UKK-3607-01A"
        CancersysBarcodeConventions.BarcodeToSample(test) != "asd-sss-1a"


    }



}
