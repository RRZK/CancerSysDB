package de.cancersysdb.portal

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NewsStoryController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond NewsStory.list(params), model:[newsStoryInstanceCount: NewsStory.count()]
    }

    def show(NewsStory newsStoryInstance) {
        respond newsStoryInstance
    }

    def latest() {
        def c = NewsStory.createCriteria()
        def results = c {
            maxResults(1)
            order("dateCreated", "desc")
        }
        if(results.empty)
            render template: "newsStorySnippet", model: []
        else
            render template: "newsStorySnippet", model: [newsStoryInstance: results.first()]
    }

    @Secured(value=["hasRole('ROLE_ADMIN')"])
    def create() {
        respond new NewsStory(params)
    }

    @Secured(value=["hasRole('ROLE_ADMIN')"])
    @Transactional
    def save(NewsStory newsStoryInstance) {
        if (newsStoryInstance == null) {
            notFound()
            return
        }

        if (newsStoryInstance.hasErrors()) {
            respond newsStoryInstance.errors, view:'create'
            return
        }

        newsStoryInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'newsStory.label', default: 'NewsStory'), newsStoryInstance.id])
                redirect newsStoryInstance
            }
            '*' { respond newsStoryInstance, [status: CREATED] }
        }
    }
    @Secured(value=["hasRole('ROLE_ADMIN')"])
    def edit(NewsStory newsStoryInstance) {
        respond newsStoryInstance
    }

    @Transactional
    @Secured(value=["hasRole('ROLE_ADMIN')"])
    def update(NewsStory newsStoryInstance) {
        if (newsStoryInstance == null) {
            notFound()
            return
        }

        if (newsStoryInstance.hasErrors()) {
            respond newsStoryInstance.errors, view:'edit'
            return
        }

        newsStoryInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'NewsStory.label', default: 'NewsStory'), newsStoryInstance.id])
                redirect newsStoryInstance
            }
            '*'{ respond newsStoryInstance, [status: OK] }
        }
    }
    @Secured(value=["hasRole('ROLE_ADMIN')"])
    @Transactional
    def delete(NewsStory newsStoryInstance) {

        if (newsStoryInstance == null) {
            notFound()
            return
        }

        newsStoryInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'NewsStory.label', default: 'NewsStory'), newsStoryInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'newsStory.label', default: 'NewsStory'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
