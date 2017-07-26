package de.cancersysdb.gegenticViz

import de.cancersysdb.Dataset
import de.cancersysdb.Patient
import de.cancersysdb.Sample
import de.cancersysdb.StructuralDataService
import de.cancersysdb.Study
import grails.converters.JSON
import viz.VizService

/**
 * This Controller Supplies Basic Information for Creating, Showing Visualisation Types. Connecting Visualisations etc.
 *
 */
class VizController {
    VizService vizService
    StructuralDataService structuralDataService
    /**
     * Shows a List of All Visualisation for Datasets by Type
     *  @params The ID of The Dataset to Show the Visualisations for
     */
    def getExistingVisualisationForDataset() {

        if(!params.dataset){
            respond status: 400 , text:"Specify Dataset Parameter"
            return
        }

        //Todo UserRights

        def ds = Dataset.findById(params.dataset)
        log.debug( ds)
        if(!ds){
            respond status:404, text:"Dataset not Found"
            return
        }
        Map Visblock = vizService.getExisistingVisualisationsInfoblock(ds)


        render template: "/viz/vizList", model:[VizInfos:Visblock], status:200

    }
    /**
     * This is a Function that Returns a Help to  Choose a Dataset or Sample
     * @return A Map which can be Processed By the template views/viz/_chooseDataset.gsp
     */

    def DataSelectSuggest() {
        Map out
/*        if(!params.containsKey("type")){
            render status: 404 , text:"Insufficient Information"
            return
        }*/
        def study = null
        if(params.Study&&params.Study!="null")
            study = Study.get(Long.parseLong(params.Study.substring(0,params.Study.toString().indexOf("_"))))

        def patient = null
        if(params.Patient&&params.Patient!="null")
            patient = Patient.get(Long.parseLong(params.Patient.substring(0,params.Patient.indexOf("_"))))
        def sample = null
        if(params.Sample&&params.Sample!="null")
            sample = Sample.get(Long.parseLong(params.Sample.substring(0,params.Sample.indexOf("_"))))
        def dataset = null
        if(params.Dataset&&params.Dataset!="null")
            dataset = Dataset.get(Long.parseLong(params.Dataset.substring(0,params.Dataset.indexOf("_"))))

        def dsType = null
        if(params.dsType&&params.dsType!="null")
            dsType =params.dsType

        out = structuralDataService.dataSelection(study,patient,sample,dataset,dsType)

        render out as JSON
    }
    def DatasetFromSample() {
        Map out
/*        if(!params.containsKey("type")){
            render status: 404 , text:"Insufficient Information"
            return
        }*/
        def study = null
        if(params.Study&&params.Study!="null")
            study = Study.get(Long.parseLong(params.Study.substring(0,params.Study.toString().indexOf("_"))))
        def patient = null
        if(params.Patient&&params.Patient!="null")
            patient = Patient.get(Long.parseLong(params.Patient.substring(0,params.Patient.indexOf("_"))))
        def sample = null
        if(params.Sample&&params.Sample!="null")
            sample = Sample.get(Long.parseLong(params.Sample.substring(0,params.Sample.indexOf("_"))))

        out = structuralDataService.possibleSamplesSelection(study,patient,sample)

        render out as JSON
    }
}
