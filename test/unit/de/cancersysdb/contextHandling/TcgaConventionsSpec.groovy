package de.cancersysdb.contextHandling

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class TcgaConventionsSpec extends Specification  {
    String test

    def setup() {
    }

    def cleanup() {
    }
    void "test Zero"() {
        when:
        test = "TCGA"
        then:
        TcgaConventions.isStudyBarcode(test)
        TcgaConventions.containsStudyBarcode(test)
        !TcgaConventions.isPatientBarcode(test)
        !TcgaConventions.containsPatientBarcode(test)
        !TcgaConventions.isSampleBarcode(test)
        !TcgaConventions.containsSampleBarcode(test)

        TcgaConventions.BarcodeToStudy(test) == "TCGA"
    }
    void "test One"() {
        when:
        test = "TCGA-01"
        then:
        !TcgaConventions.isStudyBarcode(test)
        TcgaConventions.containsStudyBarcode(test)
        !TcgaConventions.isPatientBarcode(test)
        !TcgaConventions.containsPatientBarcode(test)
        !TcgaConventions.isSampleBarcode(test)
        !TcgaConventions.containsSampleBarcode(test)

        TcgaConventions.BarcodeToStudy(test) == "TCGA"
    }

    void "test Two"() {
        when:
        test = "TCGA-01-01AX"
        then:
        !TcgaConventions.isStudyBarcode(test)
        TcgaConventions.containsStudyBarcode(test)
        TcgaConventions.isPatientBarcode(test)
        TcgaConventions.containsPatientBarcode(test)
        !TcgaConventions.isSampleBarcode(test)
        !TcgaConventions.containsSampleBarcode(test)
        TcgaConventions.BarcodeToStudy(test) == "TCGA"
        TcgaConventions.BarcodeToPatient(test) == "TCGA-01-01AX"

    }
    void "test Three"() {

        when:
        test = "TCGA-01-01AX-01a"

        then:
        !TcgaConventions.isStudyBarcode(test)
        TcgaConventions.containsStudyBarcode(test)
        !TcgaConventions.isPatientBarcode(test)
        TcgaConventions.containsPatientBarcode(test)
        TcgaConventions.isSampleBarcode(test)
        TcgaConventions.containsSampleBarcode(test)
        TcgaConventions.BarcodeToStudy(test) == "TCGA"
        TcgaConventions.BarcodeToPatient(test) == "TCGA-01-01AX"
        TcgaConventions.BarcodeToSample(test) == "TCGA-01-01AX-01a"

    }
    void "test Four"() {
        when:
        test = "TCGA-01-01AX-01a-08c-112"
        then:
        !TcgaConventions.isStudyBarcode(test)
        TcgaConventions.containsStudyBarcode(test)
        !TcgaConventions.isPatientBarcode(test)
        TcgaConventions.containsPatientBarcode(test)
        !TcgaConventions.isSampleBarcode(test)
        TcgaConventions.containsSampleBarcode(test)
        TcgaConventions.BarcodeToStudy(test) == "TCGA"
        TcgaConventions.BarcodeToPatient(test) == "TCGA-01-01AX"
        TcgaConventions.BarcodeToSample(test) == "TCGA-01-01AX-01a"

    }
    void "test Five"() {
        when:
        test = "TCGA-01-01AX-01-08-112"
        then:
        !TcgaConventions.isStudyBarcode(test)
        TcgaConventions.containsStudyBarcode(test)
        !TcgaConventions.isPatientBarcode(test)
        TcgaConventions.containsPatientBarcode(test)
        !TcgaConventions.isSampleBarcode(test)
        TcgaConventions.containsSampleBarcode(test)
        TcgaConventions.BarcodeToStudy(test) == "TCGA"
        TcgaConventions.BarcodeToPatient(test) == "TCGA-01-01AX"
        TcgaConventions.BarcodeToSample(test) == "TCGA-01-01AX-01"

    }

}
