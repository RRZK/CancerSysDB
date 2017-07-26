package de.cancersysdb.ImportTools

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class CSVPreanalyserSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test Datainput1"() {
        setup:
        String test = "a;b;c\none;1;1.0\ntwo;2;2.0\nthree;3;3.0\n"
        CSVPreanalyser cp = new CSVPreanalyser( )
        when:
        cp.Preanaylse(test)
        then:
        cp.hasHeadline
        cp.getSeperator().equals(";")
        cp.Fields.get(0).equals("a")
        cp.Fields.get(1).equals("b")
        cp.Fields.get(2).equals("c")

    }
    void "test Datainput2"() {
        setup:
        String test = "Akka,bekka,chekka\none;two,1,1.0\ntwo;three,2,2.0\nthree;one,3,3.0\n"
        CSVPreanalyser cp = new CSVPreanalyser( )
        when:
        cp.Preanaylse(test)
        then:
        cp.hasHeadline
        cp.getSeperator().equals(",")
        cp.Fields.get(0).equals("Akka")
        cp.Fields.get(1).equals("bekka")
        cp.Fields.get(2).equals("chekka")
    }

    void "test Datainput Real1"() {
        setup:
        String test =  "id;version;chromosome;dataset_id;end_pos;fpkm;fpkmok;gene_id;start_pos;strand\n" +
                "1;0;5;11;4339;31324000;1;8037;811;NA\n" +
                "2;0;20;11;21180;21547100;1;37954;20415;NA\n" +
                "3;0;19;11;83;20201400;1;18910;22;NA\n" +
                "4;0;4;11;1626;38421000;1;46439;59;NA\n"
        CSVPreanalyser cp = new CSVPreanalyser( )
        when:
        cp.Preanaylse(test)
        then:
        cp.hasHeadline
        cp.getHeadline().equals("id;version;chromosome;dataset_id;end_pos;fpkm;fpkmok;gene_id;start_pos;strand")
        cp.Fields.size()==10
        cp.getSeperator().equals(";")
        cp.Fields.get(0).equals("id")
        cp.Fields.get(2).equals("chromosome")
        cp.Fields.get(9).equals("strand")
    }

}
