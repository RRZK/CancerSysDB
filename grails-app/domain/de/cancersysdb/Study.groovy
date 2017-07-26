package de.cancersysdb

import de.cancersysdb.FrontendTools.StringRepresentationInterface
import de.cancersysdb.ImportTools.ExternalSourceInfoInterface
import de.cancersysdb.serviceClasses.ExternalSourceDescription

/**
 * The Study to Which a Patient etc belongs to
 */
class Study implements ExternalSourceInfoInterface, StringRepresentationInterface {
    /**
     * TODO to English
     */
    ExternalSourceDescription extSource
    String sourceIdentifier
    //A Place where to Find more Infos about the Study

    String uRI
/**
 * TCGA Mapping
 *
 * The Patient is Part of the Clinical XML
 * Die Wichtigen Daten für die Studien müssten sich Hier Befinden:
 *
 * db:open("genomewustledu_clinicalTCGA-AB-2803","genome.wustl.edu_clinical.TCGA-AB-2803.xml")/laml:tcga_bcr/admin:admin
 * Da muss noch geschaut werden wie die identifier zusammengestellt wurden.
 *
 * Identifier for Upload
 * admin:disease_code
 *
 */
    /**
     *     The Description of the Study
     */
    String description

    /**
     *  A referenceable identifier
     */
    String referenceIdentifier


    static hasMany = [patients: Patient]

    static constraints = {
        extSource nullable: true
        uRI nullable: true
        sourceIdentifier nullable: true
    }
    /**
     * This is a Long desription of the Dataset Contain Contextual Inforation. Results in a Looong Description String
     * @return
     */
    String toContextFreeLongString() {
        this.toString()
    }

    /**
     * Context is Represented by something else. It is Distinguishable from others
     * @return
     */
    String toContextShortIndividualizedString() {
        referenceIdentifier
    }

    /**
     * Context is Represented by something else. It described from others
     * @return
     */
    String toContextShortDeIndividualizedString() {
        this.toString()
    }
    /**
     * Technical desription with Class and ID
     * @return
     */
    String toTechDesriptionString() {
        this.toString()

    }
    static mapping = {

        sourceIdentifier index:'sourceIdentifier_idx'
    }
}
