package de.cancersysdb

import de.cancersysdb.TestData.TestdataCreatorService
import de.cancersysdb.geneticStandards.Gene
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Mock([User, Dataset, Gene])
@TestFor(TestdataCreatorService)
class TestdataCreatorServiceSpec extends Specification {
    def setup() {
    }

    def cleanup() {
    }

    void "test Random Chromosome"() {
        when: "Random Chromosome is created"
        def chrom =  service.createRandomChromosome()


        then:
        chrom != ""
        chrom.length()<100

    }
/*    void "test Random Position"() {
        when: "Random Position is created"
        def pos =  service.createRandomPosition()

        then:
        pos["Start"] > pos["end"]
        pos["chromosome"]!= ""
        pos["start"] >=0
        pos["length"] >0
        pos["end"] == pos["start"] + pos["length"]
        pos["gene"] != ""



    }*/


}
