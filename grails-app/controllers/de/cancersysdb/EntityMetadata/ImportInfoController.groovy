package de.cancersysdb.EntityMetadata

import de.cancersysdb.Patient
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured(value=["hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')"])
class ImportInfoController {

    static allowedMethods = [/*save: "POST", update: "PUT", */delete: "DELETE"]
/*
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ImportInfo.list(params), model:[importInfoInstanceCount: ImportInfo.count()]
    }

    def show(ImportInfo importInfoInstance) {
        respond importInfoInstance
    }

    def create() {
        respond new ImportInfo(params)
    }

    @Transactional
    def save(ImportInfo importInfoInstance) {
        if (importInfoInstance == null) {
            notFound()
            return
        }

        if (importInfoInstance.hasErrors()) {
            respond importInfoInstance.errors, view:'create'
            return
        }

        importInfoInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'importInfo.label', default: 'ImportInfo'), importInfoInstance.id])
                redirect importInfoInstance
            }
            '*' { respond importInfoInstance, [status: CREATED] }
        }
    }

    def edit(ImportInfo importInfoInstance) {
        respond importInfoInstance
    }

    @Transactional
    def update(ImportInfo importInfoInstance) {
        if (importInfoInstance == null) {
            notFound()
            return
        }

        if (importInfoInstance.hasErrors()) {
            respond importInfoInstance.errors, view:'edit'
            return
        }

        importInfoInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ImportInfo.label', default: 'ImportInfo'), importInfoInstance.id])
                redirect importInfoInstance
            }
            '*'{ respond importInfoInstance, [status: OK] }
        }
    }
*/
    @Transactional
    def delete(ImportInfo importInfoInstance) {

        if (importInfoInstance == null) {
            notFound()
            return
        }
        Patient p = importInfoInstance.getPatient()
        importInfoInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ImportInfo.label', default: 'ImportInfo'), importInfoInstance.id])
                redirect action: "show", id: p.id,controller: "patient"
            }
            '*'{ render status: NO_CONTENT }
        }



    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'importInfo.label', default: 'ImportInfo'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
