package de.cancersysdb

import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.commons.GrailsClass

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.converters.JSON
import grails.converters.XML
@Transactional(readOnly = true)
@Secured("isFullyAuthenticated()")
class DatasetController {
    /**
     * TODO Comment
     * TODO Remove Unsued Functions
     */

    def springSecurityService
    def marshallingService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        def callingController
        if(!params.callingController)
            callingController = "index"
        else
            callingController = params.callingController


        User actuUser = springSecurityService.getCurrentUser()
        def visDatasetsQuery
        if(!params.justMine) {

            if(!springSecurityService.loggedIn) {
                visDatasetsQuery = Dataset.where {

                    annon ==true && shared ==true

                }

            }else {
                visDatasetsQuery = Dataset.where {

                    (owner == actuUser) || (owner != actuUser && annon == false && shared == true) || (owner != actuUser && annon == true && shared == true)
                }
            }
        }else{
            visDatasetsQuery = Dataset.where {

                (owner == actuUser)
            }
        }

        def sort  = params.sort ? params.sort : "id"

        def order  = params.order ? params.order : "asc"

        def datas = visDatasetsQuery.list(max:params.max,offset:params.offset, sort:sort, order:order  )
        def StatisticsList = []

        datas.each {
            dat ->
              StatisticsList.add(dat.datasetService.getDataTypesInDataset(dat))
        }

        def size = visDatasetsQuery.size()
        //render( view: "index", model: [ datasetInstanceList:datas ,datasetSize:size, callingController:callingController,statisticsList:StatisticsList] )
        render( view: "index", model: [ datasetInstanceList:datas ,datasetSize:size, callingController:callingController,statisticsList:StatisticsList] )
        //respond Sample.list(params), model:[sampleInstanceCount: Sample.count()]


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
    def show(Dataset datasetInstance) {
        if(!datasetInstance || datasetInstance == null){
            response.sendError(404,"not Found")
            return
        }

        User cu= springSecurityService.getCurrentUser()
        boolean access

        if( (cu && datasetInstance.owner.equals(cu))|| (datasetInstance.annon && datasetInstance.shared))
            access =true
        else
            access =false


        withFormat {
            html{
                if(!access && cu)
                    response.sendError( 403,'Insufficient Rights')
                if(!access && !cu)
                    response.sendError( 401, 'Ressource protected, login?')
                def DataStatistics = datasetInstance.datasetService.getGetGenticDataStatistics(datasetInstance)
                def importProtocol = PersistedImportProtocol.findByDataset(datasetInstance)

                def rights = [:]
                if( cu && (datasetInstance.owner.equals(cu) ||cu.authorities.any { it.authority == "ROLE_ADMIN" }))
                    rights["edit"] = true
                else
                    rights["edit"] = false
                respond datasetInstance, model:[rights: rights, DataStatistics: DataStatistics, importProtocol: importProtocol]
            }
            json {
                //CustomObjectMarshallers.register()
                //marshallingService.updateMarshallers()
                if(access){
                    JSON.use('exchange')

                    respond datasetInstance as JSON

                }else{
                    if(!access && cu)
                        response.sendError( 403,  'Insufficient Rights')
                    if(!access && !cu)
                        response.sendError(401,  'Ressource protected, login?')
                }
            }
            xml {
                if(access){
                    respond datasetInstance as XML

                }else{
                    if(!access && cu)
                        response.sendError( 403, 'Insufficient Rights')
                    if(!access && !cu)
                        response.sendError( 401, 'Ressource protected, login?')
                }

            }
        }

    }

    def create() {
        respond new Dataset(params)
    }

    @Transactional
    def save(Dataset datasetInstance) {
        if (datasetInstance == null) {
            notFound()
            return
        }

        if (datasetInstance.hasErrors()) {
            respond datasetInstance.errors, view: 'create'
            return
        }

        datasetInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'dataset.label', default: 'Dataset'), datasetInstance.id])
                redirect datasetInstance
            }
            '*' { respond datasetInstance, [status: CREATED] }
        }
    }

    def edit(Dataset datasetInstance) {
        respond datasetInstance
    }

    @Transactional
    def update(Dataset datasetInstance) {
        if (datasetInstance == null) {
            notFound()
            return
        }

        if (datasetInstance.hasErrors()) {
            respond datasetInstance.errors, view: 'edit'
            return
        }

        datasetInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'dataset.label', default: 'Dataset'), datasetInstance.id])
                redirect datasetInstance
            }
            '*' { respond datasetInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Dataset datasetInstance) {

        if (datasetInstance == null) {
            notFound()
            return
        }
/*        Dataset.hasMany.each{ key,value ->
            if(!key.equals("Samples") &&  !value.simpleName.equals("Sample"))
                datasetInstance."$key".each{ GrailsClass it->
                    if(it.hasProperty("annotations") && it.annotations){it.annotations.each{anno.delete flush:true}
                    }
                    it.delete flush: true
                }


        }*/
        datasetInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'dataset.label', default: 'Dataset'), datasetInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dataset.label', default: 'Dataset'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
