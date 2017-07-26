package de.cancersysdb.gegenticViz

import de.cancersysdb.Dataset
import grails.converters.JSON
import viz.ScatterplotService

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = false)
class InteractiveScatterplotVizController {

    ScatterplotService scatterplotService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    /**
     * Experimental Stuff Delete Form Production
     * @return
     */
    def Test() {

        InteractiveScatterplotViz out = scatterplotService.createInteractiveScatterplotMatching(120,301,"DataCopynumber","copyNumber","DataVariation","freq", )
        scatterplotService.MatchDatasets(out)

        respond out
    }
    def GetFieldsForDatasets() {
        def out = [:]
        if(!params.containsKey("dataset1")){
            render status: 404 , text:"Insufficient Information"
            return
        }
        if(params.dataset1.contains("_"))
            params.dataset1 = params.dataset1.substring(0,params.dataset1.indexOf("_"))
        if(params.dataset2.contains("_"))
            params.dataset2 = params.dataset2.substring(0,params.dataset2.indexOf("_"))
        Dataset ds1 = Dataset.get(Long.parseLong(params.dataset1))
        boolean data2 = false
        Dataset ds2 =null
        if(params.containsKey("dataset2") &&params.dataset2 != ""){
            ds2 = Dataset.get(Long.parseLong(params.dataset2))
            data2 =true
        }

        out["dataset1"] = ds1.datasetService.getNumericDataFieldsByTypeByDataset(ds1)
        if(data2){
            out["dataset2"] = ds2.datasetService.getNumericDataFieldsByTypeByDataset(ds2)
        }
        render out as JSON
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond InteractiveScatterplotViz.list(params), model:[interactiveScatterplotVizInstanceCount: InteractiveScatterplotViz.count()]
    }

    def show(InteractiveScatterplotViz interactiveScatterplotVizInstance) {


/*
        int out = ScatterplotDot.countByInteractiveScatterplot( interactiveScatterplotVizInstance)
*/



        def dots =  ScatterplotDot.findAllByInteractiveScatterplotViz(interactiveScatterplotVizInstance)
        render view: "show", model:[dots:dots,interactiveScatterplotVizInstance:interactiveScatterplotVizInstance]
    }

    def create() {
        respond new InteractiveScatterplotViz(params)
    }



    @Transactional
    def save() {

        def initMap = [:] as Map
        //if(!params.containsKey("dataset1")||!params.containsKey("yAxisField") ||!params.containsKey("xAxisField") )

        //if(!params.get("yAxisField") ||!params.containsKey("xAxisField") )

        if(params.dataset1.contains("_")){
            params.dataset1 = params.dataset1.substring(0,params.dataset1.indexOf("_"))

            params.dataset1 = Dataset.get(params.dataset1)
        }
        if(params.dataset2.contains("_")) {
            params.dataset2 = params.dataset2.substring(0, params.dataset2.indexOf("_"))

            params.dataset2 = Dataset.get(params.dataset2)
        }
        List fields1 = params.get("xAxisField").split("_")
        List fields2 = params.get("yAxisField").split("_")
        //The X iss Allways with Dataset 1 and The Y Axis allways from Dataset 2
        if(fields1.get(0) == "dataset2" && fields2.get(0) == "dataset1"){
            List temp = fields1
            fields1 = fields2
            fields2 = temp

        }
        // If just One Dataset is Used Remove the Other
        if(fields1.get(0) ==  fields2.get(0)){
            //print "Just One Dataset"
            //If Its the Second Dataset just move everythin to Dataset1
            if(fields2.get(0) == "dataset2"){
                //print "Movin to the First Dataset"
                fields1[0] = "dataset1"

                fields2[0] = "dataset1"
                params.dataset1 = params.dataset2
            }
            params.dataset2 = null

        }


        Dataset ds1 = Dataset.get(params.dataset1.id)

        initMap["dataset1"] = ds1

        if(params.dataset2 && params.dataset2.id && params.dataset2.id != "null"){
            Dataset ds2 = Dataset.get(params.dataset2.id)
            initMap["dataset2"] = ds2
        }
        initMap["xAxisDatatype"] = ds1.datasetService.getDataClassForName(fields1.get(1)).simpleName
        initMap["yAxisDatatype"] = ds1.datasetService.getDataClassForName(fields2.get(1)).simpleName
        initMap["xAxisField"] = fields1.get(2)
        initMap["yAxisField"] = fields2.get(2)

        InteractiveScatterplotViz interactiveScatterplotVizInstance = scatterplotService.createInteractiveScatterplotMatching(initMap["dataset1"] as Dataset, initMap["dataset2"] as Dataset, initMap["xAxisDatatype"] as String,initMap["xAxisField"] as String, initMap["yAxisDatatype"] as String, initMap["yAxisField"] as String, true )
        //print interactiveScatterplotVizInstance
        if(interactiveScatterplotVizInstance && !interactiveScatterplotVizInstance.hasErrors())
            scatterplotService.MatchDatasets(interactiveScatterplotVizInstance)
        else {
            respond interactiveScatterplotVizInstance, view:'create'

            return

        }
        if (interactiveScatterplotVizInstance == null) {
            notFound()
            return
        }

        if (interactiveScatterplotVizInstance.hasErrors()) {
            respond interactiveScatterplotVizInstance.errors, view:'create'
            return
        }


        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'interactiveScatterplotViz.label', default: 'InteractiveScatterplotViz'), interactiveScatterplotVizInstance.id])
                redirect  interactiveScatterplotVizInstance
            }
            '*' { respond interactiveScatterplotVizInstance, [status: CREATED] }
        }
    }



    @Transactional
    def delete(InteractiveScatterplotViz interactiveScatterplotVizInstance) {

        if (interactiveScatterplotVizInstance == null) {
            notFound()
            return
        }

        interactiveScatterplotVizInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'InteractiveScatterplotViz.label', default: 'InteractiveScatterplotViz'), interactiveScatterplotVizInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'interactiveScatterplotViz.label', default: 'InteractiveScatterplotViz'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
