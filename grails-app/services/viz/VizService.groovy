package viz

import de.cancersysdb.Dataset
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication

@Transactional
class VizService {
    GrailsApplication grailsApplication
    def grailsLinkGenerator
    //Everything from the Package GeneticData
    def dataclasses = []
    //Things which are Referenceable By Dataset
    def importantclasses = []


    /**
     *
     * @param ds
     * @return
     */
    Map getExisistingVisualisationsInfoblock(Dataset ds){
        Map out = [:]
        if(dataclasses.isEmpty() || importantclasses.isEmpty())
            getDataClasses()
        log.debug( dataclasses)
        log.debug( importantclasses)
        Map vizType = getVizTypesForDatasetCountMap(ds)
        vizType.each { key,val->

            Map datasetInfo=[:]

            datasetInfo["VizCreateInfo"] = [:]
            datasetInfo["VizCreateInfo"]["VizInfo"] = "description Here"
            datasetInfo["VizCreateInfo"]["CreateLink"]= grailsLinkGenerator.link(controller: key.simpleName, action: "create",params: [dataset1: ds.id])
            datasetInfo["ExisitingViz"] = [:]

            List stuff = []

            datasetInfo["ExisitingViz"]["Count"] =val

            stuff.addAll(key.findAllByDataset1(ds))
            if(key.metaClass.respondsTo(key, "findAllByDataset2"))
                stuff.addAll(key.findAllByDataset2(ds))
            List toAdd = []
            stuff.each {
                Map temp = ["name":it.toString(),"link":grailsLinkGenerator.link(controller: key.simpleName, action: "show",id:it.id )]
                log.debug( temp)

                toAdd.add(temp)


            }

            datasetInfo["ExisitingViz"]["Existing"] =toAdd

            out[key.simpleName]=datasetInfo

        }
        /*
        VizType -> InteractiveScatterplotViz
          VizCreateInfo -> Object
            VizInfo -> Text that Describes Infos
            CreateLink -> The Link to Create a Visualisation
        ExisitingViz -> Visualisations Existing.
          Count -> Number of Visualisations (Number)
          Existing -> Array of Links to Visualisations
        */

        return out


    }

    /**
     * Retrive All the Classes asociated to Special Datasets
     */

    private void getDataClasses(){
        dataclasses = []
        importantclasses = []

        def ic = grailsApplication.domainClasses.findAll { it.clazz.package.name == "de.cancersysdb.gegenticViz" }.clazz
        ic.each {

            dataclasses.addAll(it)
            if(it.simpleName.endsWith("Viz"))
                importantclasses.addAll(it)
        }
    }
    /**
     * Get the Types of Genetic Data Availible for this Dataset as List
     * @param ds The Dataset you want to get the Infos for
     */
    Map getVizTypesForDatasetCountMap(Dataset ds) {
        Map temps = [:]
        if(importantclasses.isEmpty())
            this.getDataClasses()
        for(importantClass in importantclasses ) {
            def temp = importantClass.countByDataset1(ds)

            temps[importantClass]= temp
        }
        return temps
    }
}
