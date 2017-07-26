package de.cancersysdb.EntityMetadata

import de.cancersysdb.Patient
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional


/**
 * This Controller devlivers ClinicalInformation
 */
@Transactional(readOnly = true)
@Secured(value=["hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')"])
class ClinicalInformationController {

    static allowedMethods = [/*save: "POST", update: "PUT",*/ delete: "DELETE"]

    @Transactional
    def delete(ClinicalInformation clinicalInformationInstance) {

        if (clinicalInformationInstance == null) {
            notFound()
            return
        }
        Patient p = clinicalInformationInstance.getImportInfo().getPatient()

        clinicalInformationInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ClinicalInformation.label', default: 'ClinicalInformation'), clinicalInformationInstance.id])
                redirect action: "show", id: p.id,controller: "patient"
            }
            '*'{ render status: NO_CONTENT }
        }


    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'clinicalInformation.label', default: 'ClinicalInformation'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
