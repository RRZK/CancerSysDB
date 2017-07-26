package de.cancersysdb.geneticStandards

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Gene)
class GeneSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "Simple data Tests"() {
        when:
        def g1 = new Gene( name: "RPL10A", ncbiID: "4736", ensemblID:"ENSG00000198755",  chromosome:  "6", startPos: 35468408,endPos: 35470785, strand:"+", dataVersion:"89")
        //g1.setNcbiID( "4736")
       // g1.setEnsemblID("ENSG00000198755")
        g1.save()
        then:
        g1.getEnsembl()
        g1.getEnsemblID()
        g1.getNcbiID()
        g1.ncbiID.equals(g1.getNcbiID())

        g1.errors.allErrors.empty

        g1.isGene()

        when:
        def t1 = new Gene( ensemblID: "ENST00000478340", chromosome: "6", startPos: 35468408,endPos: 35470785, strand:"+",dataVersion:"89")
        t1.setTranscriptOf(g1)
        t1.save()
        then:
        t1.getEnsembl()

        t1.errors.allErrors.empty

        !t1.isGene()

        when:

        def g2 = new Gene( name: "CCDC152",ncbiID: "100129792",ensemblID: "ENSG00000198865", chromosome: "5", startPos: 42756801,endPos: 42802360, strand:"+",dataVersion:"89")
        g2.save()
        def t2 = new Gene( ncbiID: "NM_001134848",ensemblID: "ENST00000361970", chromosome: "5", startPos: 42756801,endPos: 42802360, strand:"+",dataVersion:"89")
        t2.setTranscriptOf(g2)
        t2.save()
        then:
        g2.errors.allErrors.empty
        t2.errors.allErrors.empty
        g2.isGene()
        !t2.isGene()
    }

    void "test something else"() {
        when:
        def g1 = new Gene( name: "RPL10A", ncbiID: "4736", ensemblID:"ENSG00000198755",  chromosome:  "6", startPos: 35468408,endPos: 35470785, strand:"+", dataVersion:"89")
        //g1.setNcbiID( "4736")
        // g1.setEnsemblID("ENSG00000198755")
        g1.save()
        then:
        g1.getEnsembl()==198755
        g1.getEnsemblID().equals("ENSG00000198755")

        g1.getNcbi()==4736
        g1.getNcbiID().equals("4736")

        g1.ncbiID.equals(g1.getNcbiID())
        !g1.isTranscript()
        g1.isGene()

        g1.errors.allErrors.empty

        when:
        def t1 = new Gene( ensemblID: "ENST00000478340", chromosome: "6", startPos: 35468408,endPos: 35470785, strand:"+",dataVersion:"89")
        t1.setTranscriptOf(g1)
        t1.save()
        then:
        t1.getEnsembl()
        !t1.getNcbi()
        t1.getNcbiID() == null
        t1.getEnsemblID().equals("ENST00000478340")
        t1.errors.allErrors.empty
        t1.isTranscript()
        !t1.isGene()
        t1.getTranscriptOf().equals(g1)



    }

    void "Broken Tests!"() {
        when:
        //Fails ... No Mixture of Gene and Transcript Information
        def g1 = new Gene(  ncbiID: "4736",  chromosome:  "asdas6", startPos: 35468408,endPos: 35470785, strand:"+", dataVersion:"89")

        then:
        g1.getNcbiID()
        !g1.setEnsemblID("ENST00000198755")
        g1.save()
        g1.getEnsemblID() == null

        when:
        def t1 = new Gene( ensemblID: "ENST00000478340", chromosome: "6", startPos: 35468408,endPos: 354, strand:"+",dataVersion:"89")
        t1.setTranscriptOf(g1)

        then:

        !t1.setNcbiID("4736")

        !t1.save()


    }
    void "Tests Gene Or Transcript"() {
        when:
        def g1 = new Gene(  ncbiID: "4736",  chromosome:  "asdas6", startPos: 35468408,endPos: 35470785, strand:"+", dataVersion:"89")

        then:
        !g1.setEnsemblID("ENST00000198755")
        g1.save()
        g1.getEnsemblID() == null

        when:
        def t1 = new Gene( )
        t1.setTranscriptOf(g1)

        then:
        t1.isTranscript()
        !t1.isGene()
        when:
        t1.setEnsemblID( "ENST00000478340")
        t1.setChromosome("6")
        t1.setStartPos( 35468408)
        t1.setEndPos(354)
        t1.setStrand('+' as Character)
        t1.setDataVersion(89)
        then:
        !t1.setNcbiID("4736")

        !t1.save()


    }
    void "Tests Transcript extendet"() {
        when:
        def g1 = new Gene(  ncbiID: "4736",  chromosome:  "asdas6", startPos: "35468408",endPos: "35470785", strand:"+", dataVersion:"89")

        then:
        !g1.setEnsemblID("ENST00000198755")
        g1.save()
        g1.getEnsemblID() == null

        when:


        g1 != null

        then:

        def transcript = new Gene( )
        //Set first to Ensure its a Transcript
        transcript.setTranscriptOf(g1)

        assert ! transcript.isGene()
        assert transcript.isTranscript()

        assert transcript.setEnsemblID( "ENST00000198755")

        when:
        transcript.setChromosome( g1.getChromosome())
        transcript.setStartPos( g1.getStartPos())
        transcript.setEndPos( g1.getEndPos())
        transcript.setStrand( g1.getStrand())
        transcript.dataVersion = 0
        then:
        transcript.save()
}
}