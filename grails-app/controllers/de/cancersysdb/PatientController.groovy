package de.cancersysdb

import de.cancersysdb.EntityMetadata.ImportInfo
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
@Secured("isFullyAuthenticated()")
@Transactional(readOnly = true)
class PatientController {
    def springSecurityService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Patient.list(params), model: [patientInstanceCount: Patient.count(), patientInstanceList:Patient.list(params)]
    }

    def show(Patient patientInstance) {
        User u = springSecurityService.getCurrentUser()
        if(!patientInstance)
            response.sendError(404)
        def ii = patientInstance.getImportInfos()
        List importInfos = []
        List editableImportInfos = []
        ii.each { ImportInfo it->
            if( it.owner.equals(u) || u.getAuthorities().any() { it.authority == Role.ADMIN}){
                importInfos.add(it)
                editableImportInfos.add(it)
            }
            else if(it.annon|| it.shared)
                importInfos.add(it)
        }
        render view: "show", model:[ patientInstance:patientInstance,importInfos:importInfos,editableImportInfos:editableImportInfos ]
    }

    def create() {
        respond new Patient(params)
    }

    @Transactional
    def save(Patient patientInstance) {
        if (patientInstance == null) {
            notFound()
            return
        }

        if (patientInstance.hasErrors()) {
            respond patientInstance.errors, view: 'create'
            return
        }

        patientInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'patient.label', default: 'Patient'), patientInstance.id])
                redirect patientInstance
            }
            '*' { respond patientInstance, [status: CREATED] }
        }
    }

    def edit(Patient patientInstance) {
        respond patientInstance
    }

    @Transactional
    def update(Patient patientInstance) {
        if (patientInstance == null) {
            notFound()
            return
        }

        if (patientInstance.hasErrors()) {
            respond patientInstance.errors, view: 'edit'
            return
        }

        patientInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Patient.label', default: 'Patient'), patientInstance.id])
                redirect patientInstance
            }
            '*' { respond patientInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Patient patientInstance) {

        if (patientInstance == null) {
            notFound()
            return
        }

        patientInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Patient.label', default: 'Patient'), patientInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'patient.label', default: 'Patient'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
