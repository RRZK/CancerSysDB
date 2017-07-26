package de.cancersysdb.geneticStandards

import de.cancersysdb.GeneContextService
import de.cancersysdb.Import.GeneImportService
import grails.test.mixin.*
import spock.lang.*

@TestFor(GeneController)
@Mock(Gene)
class GeneControllerSpec extends Specification {
    def geneServiceMock
    def geneContextService
    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }
    def setup() {
        geneServiceMock = mockFor(GeneImportService)

        controller.geneService = geneServiceMock.createMock()

        geneContextService = mockFor(GeneContextService)
        geneContextService.demand.getHumanReadableContextforGene() { ->
            return [:]
        }
        controller.geneContextService = geneContextService.createMock()

    }
    void "Test the index action returns the correct model"() {

        when: "The index action is executed"

        controller.index()

        then: "The model is correct"
        !model.geneInstanceList
        model.geneInstanceCount == 0
    }




    void "Test that the show action returns the correct model"() {
        when: "The show action is executed with a null domain"
        geneContextService.demand.getHumanReadableContextforGene() { ->
            return [:]
        }
        controller.geneContextService = geneContextService.createMock()
        controller.show(null)


        then: "A 404 error is returned"
        response.status == 404

        when: "A domain instance is passed to the show action"
        populateValidParams(params)
        geneContextService.demand.getHumanReadableContextforGene() { ->
            return [:]
        }
        controller.geneContextService = geneContextService.createMock()
        def gene = new Gene(params)
        controller.show(gene)

        then: "A model is populated containing the domain instance"
        model.geneInstance == gene
    }


}
