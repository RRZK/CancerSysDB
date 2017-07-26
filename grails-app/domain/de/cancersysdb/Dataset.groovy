package de.cancersysdb

import de.cancersysdb.Controll.CsysProtectionInterface
import de.cancersysdb.FrontendTools.StringRepresentationInterface
import de.cancersysdb.ImportTools.ExternalSourceInfoInterface
import de.cancersysdb.data.DataCopynumber
import de.cancersysdb.data.DataGeneInteraction
import de.cancersysdb.data.DataGeneInteractionGroups
import de.cancersysdb.data.DataGeneticFunction
import de.cancersysdb.data.DataMutatedGene
import de.cancersysdb.data.DataPeak
import de.cancersysdb.data.DataTranscriptAbundance
import de.cancersysdb.data.DataTranscriptDiffExpr
import de.cancersysdb.data.DataVariation
import de.cancersysdb.serviceClasses.ExternalSourceDescription
import de.cancersysdb.serviceClasses.FiletypeToGeneticStandardMatching
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import de.cancersysdb.workflow.ProcessedWorkflow

/**
 * The Dataset is the Core Class for Data. It manages all Datatypes from Package de.cancersysdb.data .
 * It manages Dataaccess
 * It Manages the Commetions to the Samples and Patients and Stuides
 */

class Dataset implements ExternalSourceInfoInterface, StringRepresentationInterface,CsysProtectionInterface {
    //Services
    transient def datasetService

    ExternalSourceDescription extSource
    String sourceIdentifier
    String uRI
    //Standard Userrights Stuff
    //Is this Sample Annonymous?
    Boolean annon
    //If False this Dataset is only Visible to The Owner
    Boolean shared

    /**
     * The Owner
     */
    User owner
    /**
     * The Local Filename of The Dataset
     */
    String fileName
    /**
     * Shortcut if Dataset Samples belong to a singe Sample
     */
    Patient singlePatient

    /**
     * Thing that enables to reference to an Original Datasource
     */
    String originURL
    /**
     * Note which describes what this Dataset is about
     */
    String note
    //
    /**
     * This is the Matching used for Import. This Mapping will be used if Generic CSV Files are uploaded to the Dataset.
     */
    FiletypeToGeneticStandardMatching matching
    /**
     * processedWorkflow
     */

    //TODO Really its not Implemented
    ProcessedWorkflow processedWorkflow
    static hasMany=[samples:Sample,
                    dataCopynumber:DataCopynumber,
                    dataGeneInteraction:DataGeneInteraction,
                    dataGeneInteractionGroups:DataGeneInteractionGroups,
                    dataGeneticFunction:DataGeneticFunction,
                    dataPeak:DataPeak,
                    dataTranscriptAbundance:DataTranscriptAbundance,
                    dataTranscriptDiffExpr:DataTranscriptDiffExpr,
                    dataVariation:DataVariation,
                    dataMutatedGene:DataMutatedGene

    ]


    static belongsTo =  [Sample]

    static constraints = {
        originURL blank: true, nullable: true
        processedWorkflow nullable: true
        extSource nullable: true
        fileName nullable: true
        uRI nullable: true
        sourceIdentifier nullable: true
        samples nullable: true
        originURL nullable: true
        singlePatient nullable: true
        note nullable: true
        matching nullable: true
    }

    static mapping = {

        note type: 'text'
        sourceIdentifier index:'sourceIdentifier_idx'
        dataCopynumber cascade: 'all-delete-orphan'
        dataGeneInteraction cascade: 'all-delete-orphan'
        dataGeneInteractionGroups cascade: 'all-delete-orphan'
        dataGeneticFunction cascade: 'all-delete-orphan'
        dataPeak cascade: 'all-delete-orphan'
        dataTranscriptAbundance cascade: 'all-delete-orphan'
        dataTranscriptDiffExpr cascade: 'all-delete-orphan'
        dataVariation cascade: 'all-delete-orphan'
        dataMutatedGene cascade: 'all-delete-orphan'
    }

    /**
     * Check if the Dataset belongs to a Singe Patient
     * @return Singepat
     */
    def beforeValidate() {
        Patient Singepat = null
        boolean singlepatient = false
        samples.each {
            Patient pat = it.getPatient()
            if (it && Singepat == null && !singlepatient) {
                singlepatient = true
                Singepat = pat

            } else if (Singepat != null && !Singepat.id.equals(pat.id))
                singlepatient = false

        }
        if (singlepatient && Singepat != null)
            singlePatient = Singepat
        else
            singlePatient = null
    }

    /**
     * Delete all Pending stuff by hand?!?!
     * @return Singepat
     */
    def beforeDelete() {
        samples.clear()
        samples.each {Sample it ->
            it.removeFromDatasets(this)
                    it.save flush:true

        }


    }
    /**
     * Map of Marshallung behavior in Contexts @see de.cancersysdb.MarshallingService
     */
    static def marshallers = ["exchange": { Dataset ds ->

        //Get the Backlinked Stuff from the Data Package.
        def temps = ds.datasetService.getDataCount(ds)
        // The Fields that Should not be Exportet
        def AntiFields = []
        // only Marshall the NOT-specified fields that aren't null
        def map = [:]

        ds.domainClass.getPersistentProperties().findAll {
            k ->
                !(k.getName() in AntiFields)
        }.each {
            k ->
                def name = k.getName()
                map.put(name, ds."$name")
        }

        map.id = ds.id
        map.class = ds.getClass().name
        map.datas = temps
        return map
    }


    ]

    //This is a Long desription of the Dataset Contain Contextual Inforation. Results in a Looong Description String
    String toContextFreeLongString() {
        this.toString()
    }
    //Context is Represented by something else. It is Distinguishable from others
    String toContextShortIndividualizedString() {
        this.id + " Dataset"
    }
    //Context is Represented by something else. It described from others
    String toContextShortDeIndividualizedString() {
        this.toString()
    }
    //Technical desription with Class and ID
    String toTechDesriptionString() {

        this.toString()
    }

}
