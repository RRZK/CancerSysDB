package de.cancersysdb.gegenticViz


import grails.test.mixin.*
import spock.lang.*

@TestFor(InteractiveScatterplotVizController)
@Mock(InteractiveScatterplotViz)
class InteractiveScatterplotVizControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        params["dataset1.id"] = '1'

    }

    void "Test the index action returns the correct model"() {

        when: "The index action is executed"
        controller.index()

        then: "The model is correct"
        !model.interactiveScatterplotVizInstanceList
        model.interactiveScatterplotVizInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when: "The create action is executed"
        controller.create()

        then: "The model is correctly created"
        model.interactiveScatterplotVizInstance != null
    }

/*    void "Test the save action correctly persists an instance"() {

        when: "The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def interactiveScatterplotViz = new InteractiveScatterplotViz()
        interactiveScatterplotViz.validate()
        controller.save(interactiveScatterplotViz)

        then: "The create view is rendered again with the correct model"
        model.interactiveScatterplotVizInstance != null
        view == 'create'

        when: "The save action is executed with a valid instance"
        response.reset()
        populateValidParams(params)
        interactiveScatterplotViz = new InteractiveScatterplotViz(params)

        controller.save(interactiveScatterplotViz)

        then: "A redirect is issued to the show action"
        response.redirectedUrl == '/interactiveScatterplotViz/show/1'
        controller.flash.message != null
        InteractiveScatterplotViz.count() == 1
    }*/

    void "Test that the show action returns the correct model"() {
        when: "The show action is executed with a null domain"
        controller.show(null)

        then: "A 404 error is returned"
        response.status == 404


    }


/*
    void "Test that the delete action deletes an instance if it exists"() {
        when: "The delete action is called for a null instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(null)

        then: "A 404 is returned"
        response.redirectedUrl == '/interactiveScatterplotViz/index'
        flash.message != null


    }*/
}
