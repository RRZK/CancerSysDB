package de.cancersysdb.data

import de.cancersysdb.Dataset
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(DataCopynumber)
@Mock(Dataset)
class DataCopynumberSpec extends Specification {
    Dataset mokkyDataset
    def setup() {
        def mook = mockFor(Dataset)
        mokkyDataset =mook.createMock()

    }

    def cleanup() {
    }

    void "test Valid 1"() {
        when:"Create a Copynumber dataset"
        def geneticCopynumber = new DataCopynumber(chromosome:"3",startPos:1,endPos:2,copyNumber:10.0,dataset: mokkyDataset)
        geneticCopynumber.validate()

        then:"it Should work just with Copynumber"
        !geneticCopynumber.hasErrors()
    }
    void "test Valid 2"() {
            when: "Create a Copynumber dataset"
            def geneticCopynumber = new DataCopynumber(chromosome: "1", startPos: 1, endPos: 1, copyNumber: 10.0,dataset: mokkyDataset)
            geneticCopynumber.validate()

            then: "it Should work just with Copynumber"
            !geneticCopynumber.hasErrors()
    }
    void "test invalid 2"() {
        when:"Create a Copynumber dataset"
        def geneticCopynumber = new DataCopynumber(chromosome:"1",startPos:5,endPos:1,copyNumber:10.0,dataset: mokkyDataset)
        geneticCopynumber.validate()

        then:"it Should have errors because it Ends before it Starts"
        geneticCopynumber.hasErrors()
    }
}
