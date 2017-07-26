package de.cancersysdb.EntityMetadata

import de.cancersysdb.Controll.CsysProtectionInterface
import de.cancersysdb.ImportTools.ExternalSourceInfoInterface
import de.cancersysdb.Patient
import de.cancersysdb.User
import de.cancersysdb.serviceClasses.ExternalSourceDescription

/**
 * This class will give a General Handle to the Information Like Clinical Information. It Manages Clinical Information from a Single file
 */
class ImportInfo implements ExternalSourceInfoInterface,CsysProtectionInterface {

    String filename
    String filetype
    //TODO Implement FileHasingIdentification
    //Project project
    /**
     * Is this information Annonymous, CAN it be shown to anyone
     */
    Boolean annon
    /**
     * Is this information Annonymous, SHOULD it be shown to anyone. If false this dataset is only visible to the owner!
     */
    Boolean shared
    /**
     * The Owner
     */
    User owner
    ExternalSourceDescription extSource
    String sourceIdentifier
    String uRI

    static belongsTo = [patient: Patient]
    static hasMany = [infos: ClinicalInformation]
    static constraints = {
        extSource nullable: true
        sourceIdentifier nullable: true
        uRI nullable: true
    }
}
