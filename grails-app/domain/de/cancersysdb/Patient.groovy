package de.cancersysdb

import de.cancersysdb.EntityMetadata.ImportInfo
import de.cancersysdb.FrontendTools.StringRepresentationInterface
import de.cancersysdb.ImportTools.ExternalSourceInfoInterface
import de.cancersysdb.serviceClasses.ExternalSourceDescription

/**
 * This class Represents the Patients an Manages Samples and Clinical Data
 */
/**
 * TCGA Mapping
 *
 * The Patient is Part of the Clinical/Biocspecimen XML
 * the XML is Described in http://tcga-data.nci.nih.gov/docs/xsd/BCR/tcga.nci/bcr/xml/biospecimen/2.6/TCGA_BCR.Biospecimen.xsd
 *
 * Identifier for Upload
 * "/laml:tcga_bcr/laml:patient/shared:bcr_patient_barcode/text()"
 *
 * See ALSO
 * tumor_tissue_site Bis jetzt anscheinend nicht Im Kontrollierten Vokabular
 * /laml:tcga_bcr/laml:patient/shared:tumor_tissue_site/text()
 */

class Patient implements ExternalSourceInfoInterface, StringRepresentationInterface {
    //ExternalSourceInfoInterface
    ExternalSourceDescription extSource
    String sourceIdentifier
    String uRI

    /**
     * The Study this patient belongs to
     */
    Study study

    static hasMany = [samples: Sample, importInfos: ImportInfo]
    static belongsTo = [study: Study]


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
        toString()
    }

    /**
     * Context is Represented by something else. It is Distinguishable from others
     * @return
     */
    String toContextShortIndividualizedString() {
        if (sourceIdentifier)
            return sourceIdentifier + " ID:" + this.id
        else if (uRI)
            return uRI + " ID:" + this.id

    }
    /**
     * Context is Represented by something else. It described from others
     * @return
     */
    String toContextShortDeIndividualizedString() {
        toString()
    }
    /**
     * Technical desription with Class and ID
     * @return
     */
    String toTechDesriptionString() {
        toString()
    }

    static mapping = {

        sourceIdentifier index:'sourceIdentifier_idx'
    }
}
