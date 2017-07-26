package de.cancersysdb.serviceClasses

import de.cancersysdb.Role
import de.cancersysdb.User
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
@Secured("isFullyAuthenticated()")
@Transactional(readOnly = true)
class PersistedImportProtocolController {
    def springSecurityService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def show(PersistedImportProtocol persistedImportProtocol) {
        User actuUser = springSecurityService.getCurrentUser()
        //def persistedImportProtocolInstance= PersistedImportProtocol.findByDataset(dataset)
        if(persistedImportProtocol.dataset.owner.equals(actuUser) || actuUser.authorities.contains(Role.ADMIN)){

           def NFGs= NotFoundGeneName.findAllByDs(persistedImportProtocol.dataset)

            render view: "show", model: [persistedImportProtocol:persistedImportProtocol,notFoundGeneNames:NFGs]

        }
        else
            respond status: 405

    }

    @Transactional
    def delete(PersistedImportProtocol persistedImportProtocolInstance) {

        if (persistedImportProtocolInstance == null) {
            notFound()
            return
        }

        persistedImportProtocolInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'PersistedImportProtocol.label', default: 'PersistedImportProtocol'), persistedImportProtocolInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'persistedImportProtocol.label', default: 'PersistedImportProtocol'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
