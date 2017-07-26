package de.cancersysdb

import grails.gorm.DetachedCriteria
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
@Secured("isFullyAuthenticated()")
class SampleController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def springSecurityService


    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def callingController
        if(!params.callingController)
            callingController = "index"
        else
            callingController = params.callingController


        User actuUser = springSecurityService.getCurrentUser()
        def visSamplesQuery
        if(!params.justMine) {
            if(!springSecurityService.loggedIn) {
                visSamplesQuery = Sample.where {

                   annon == true && shared == true

                }

            }else{
                visSamplesQuery = Sample.where {

                    (owner == actuUser) || (owner != actuUser && annon == false && shared == true) || (owner != actuUser && annon == true && shared == true)
                }
            }
        }else{
            visSamplesQuery = Sample.where {

                (owner == actuUser)
            }
        }

        def sort  = params.sort ? params.sort : "label";

        def order  = params.order ? params.order : "asc";

        def samps = visSamplesQuery.list(max:params.max,offset:params.offset, sort:sort, order:order  )
        def size = visSamplesQuery.size()
        render( view: "index", model: [ sampleInstanceList:samps
                                        ,sampleSize:size, callingController:callingController] )

    }

    def indexByDesease(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        params.ByDesease = true;
        params.callingController = "indexByDesease"
        index()
    }


    def indexOwn(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        params.justMine = true;
        params.callingController = "indexOwn"
        index()
    }


    @Transactional
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def uploadFile() {

        def f

        try {
            f = request.getFile('file')
        }catch(e){
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }
        if (f.empty) {
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }

        def stream =  f.getInputStream()
        def newSamples = []
        def error =false
        def valid =true
        //TODO to Service
        stream.toCsvReader(['charset':'UTF-8','separatorChar':';',skipLines:1]).eachLine{tokens ->
            if(tokens.length!=5) {
                error =true
                return
            }else {
                def ns =new Sample(batch:tokens[2],pairEnds: tokens[3].toInteger(),label:tokens[4], owner:springSecurityService.getCurrentUser(),annon:params.Annon ?: true,shared:params.Shared ?: false)
                valid= valid && ns.validate()
                newSamples.add(ns)
            }
        }
        if(error || !valid ) {
            newSamples.each { it.discard() }
            flash.message = message(message:" Error while importing Samples from Uploaded File")
            render(view: 'index')
        }else {
            for (samp in newSamples) {
                samp.save( flush:true)
            }
            def importedSize = newSamples.size()
            flash.message = message(message:" Succsessfull imported $importedSize Samples")
            render(view: 'index')
        }
    }
    def show(Sample sampleInstance) {
        if(!sampleInstance || sampleInstance == null){
            response.sendError(404,"not Found")
            return
        }
        def cu= springSecurityService.getCurrentUser()
        def rights = [:]
        if(cu && sampleInstance.owner == cu)
            rights["edit"] = true
        else
            rights["edit"] = false

        respond sampleInstance, model:[ rights:rights ]
    }

    def create() {
        respond new Sample(params)
    }

    @Transactional
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def save(Sample sampleInstance) {
        if (sampleInstance == null) {
            notFound()
            return
        }

        if (sampleInstance.hasErrors()) {
            respond sampleInstance.errors, view:'create'
            return
        }

        sampleInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'sample.label', default: 'Sample'), sampleInstance.id])
                redirect sampleInstance
            }
            '*' {                 flash.message = message(code: 'default.created.message', args: [message(code: 'sample.label', default: 'Sample'), sampleInstance.id])
                redirect sampleInstance }
        }
    }
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def edit(Sample sampleInstance) {
        respond sampleInstance
    }

    @Transactional
    @Secured(["ROLE_USER","ROLE_MANAGER","ROLE_ADMIN"])
    def update(Sample sampleInstance) {
        if (sampleInstance == null) {
            notFound()
            return
        }

        if (sampleInstance.hasErrors()) {
            respond sampleInstance.errors, view:'edit'
            return
        }

        sampleInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Sample.label', default: 'Sample'), sampleInstance.id])
                redirect sampleInstance
            }
            '*'{ redirect view: "show", sampleInstance, [status: OK] }
        }
    }

    @Transactional
    @Secured(["ROLE_MANAGER","ROLE_ADMIN"])
    def delete(Sample sampleInstance) {

        if (sampleInstance == null) {
            notFound()
            return
        }

        sampleInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Sample.label', default: 'Sample'), sampleInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'sample.label', default: 'Sample'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
