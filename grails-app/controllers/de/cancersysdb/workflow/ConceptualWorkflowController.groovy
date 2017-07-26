package de.cancersysdb.workflow

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional
class ConceptualWorkflowController {
    def springSecurityService
    WorkflowManagementService workflowManagementService
    static allowedMethods = [save: "POST", update: "PUT"]
/*
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ConceptualWorkflow.list(params), model:[conceptualWorkflowInstanceCount: ConceptualWorkflow.count()]
    }*/

    def show(ConceptualWorkflow conceptualWorkflowInstance) {
        respond conceptualWorkflowInstance
    }

/*    def create() {
        respond new ConceptualWorkflow(params)
    }

    @Transactional
    def save(ConceptualWorkflow conceptualWorkflowInstance) {
        if (conceptualWorkflowInstance == null) {
            notFound()
            return
        }

        if (conceptualWorkflowInstance.hasErrors()) {
            respond conceptualWorkflowInstance.errors, view:'create'
            return
        }

        conceptualWorkflowInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'conceptualWorkflow.label', default: 'ConceptualWorkflow'), conceptualWorkflowInstance.id])
                redirect conceptualWorkflowInstance
            }
            '*' { respond conceptualWorkflowInstance, [status: CREATED] }
        }
    }

    def edit(ConceptualWorkflow conceptualWorkflowInstance) {
        respond conceptualWorkflowInstance
    }

    @Transactional
    def update(ConceptualWorkflow conceptualWorkflowInstance) {
        if (conceptualWorkflowInstance == null) {
            notFound()
            return
        }

        if (conceptualWorkflowInstance.hasErrors()) {
            respond conceptualWorkflowInstance.errors, view:'edit'
            return
        }

        conceptualWorkflowInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ConceptualWorkflow.label', default: 'ConceptualWorkflow'), conceptualWorkflowInstance.id])
                redirect conceptualWorkflowInstance
            }
            '*'{ respond conceptualWorkflowInstance, [status: OK] }
        }
    }
    */
    @Transactional
    @Secured(["ROLE_MANAGER","ROLE_ADMIN"])
    def delete(ConceptualWorkflow conceptualWorkflowInstance) {


        if (conceptualWorkflowInstance == null) {
            notFound()
            return
        }

        workflowManagementService.delteConceptualWorkflow(conceptualWorkflowInstance)

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'ConceptualWorkflow.label', default: 'ConceptualWorkflow'), conceptualWorkflowInstance.id])
                    redirect controller: "showcase", action:"index", method:"GET"

                }
                '*'{ render status: NO_CONTENT }
            }

    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'conceptualWorkflow.label', default: 'ConceptualWorkflow'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
