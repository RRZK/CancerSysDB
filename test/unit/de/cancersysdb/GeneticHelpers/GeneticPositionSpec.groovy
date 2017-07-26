package de.cancersysdb.GeneticHelpers

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class GeneticPositionSpec extends Specification {
    List<GeneticPosition> Gp=[]

    def setup() {
        Gp[0] = new  GeneticPosition( chromosome: "1",startPos: 1, endPos: 1)
        Gp[1] = new  GeneticPosition( chromosome: "1",startPos: 1, endPos: 2)
        Gp[2] = new  GeneticPosition( chromosome: "1",startPos: 2, endPos: 2)
        Gp[3] = new  GeneticPosition( chromosome: "1",startPos: 1, endPos: 5)
        Gp[4] = new  GeneticPosition( chromosome: "1",startPos: 3, endPos: 5)
        Gp[5] = new  GeneticPosition( chromosome: "X",startPos: 1, endPos: 100)
    }

    def cleanup() {
    }
    void "test isIntersecting"() {

        boolean temp
        when: "G1 to G2"
        temp = GeneticPosition.isIntersecting(Gp[1],Gp[2])
        then: "True"
        temp
        when: "G1 to G3"
        temp = GeneticPosition.isIntersecting(Gp[1],Gp[3])
        then: "True"
        temp
        when: "G1 to G4"
        temp = GeneticPosition.isIntersecting(Gp[1],Gp[4])
        then: "False"
        !temp
        when: "G1 to G5"
        temp = GeneticPosition.isIntersecting(Gp[1],Gp[5])
        then: "False"
        !temp

        when: "Gp4 to G5"
        temp = GeneticPosition.isIntersecting(Gp[4],Gp[5])
        then: "False"
        !temp
    }
    void "test Intersection"() {

        GeneticPosition temp
        when: "G1 to G2"
        temp = GeneticPosition.Intersection(Gp[1],Gp[2])
        then: "True"
        temp.startPos ==2
        temp.endPos ==2
        when: "G1 to G3"
        temp = GeneticPosition.Intersection(Gp[1],Gp[3])
        then: "True"
        temp.startPos ==1
        temp.endPos ==2
        when: "G1 to G4"
        temp = GeneticPosition.Intersection(Gp[1],Gp[4])
        then: "NULL"
        temp == null
        when: "G1 to G5"
        temp = GeneticPosition.Intersection(Gp[1],Gp[5])
        then: "NULL"
        temp == null

        when: "G3 to G4"
        temp = GeneticPosition.Intersection(Gp[4],Gp[5])
        then: "NULL"
        temp ==null

    }



    void "test CalcPositionFrames"() {

        Collection temp
        when: "G1 to G2"
        temp = GeneticPosition.CalcPositionFrames(Gp)
        then:
        temp.size() == 2
        when: "G1 to G3"
        temp = GeneticPosition.CalcPositionFrames([Gp[1],Gp[3]])
        then:
        temp.size() ==1
        temp[0].startPos ==1
        temp[0].endPos ==5


        when: "G1 to G4"
        temp = GeneticPosition.CalcPositionFrames([Gp[1],Gp[4]])
        then:
        temp.size() ==1
        temp[0].startPos ==1
        temp[0].endPos ==5
        when: "G2 to G4"
        temp = GeneticPosition.CalcPositionFrames([Gp[2],Gp[4]])
        then:
        temp.size() ==1
        temp[0].startPos ==2
        temp[0].endPos ==5

    }

}
