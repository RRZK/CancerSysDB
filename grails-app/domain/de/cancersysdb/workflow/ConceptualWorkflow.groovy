package de.cancersysdb.workflow

import de.cancersysdb.ImportTools.ExternalSourceInfoInterface
import de.cancersysdb.serviceClasses.ExternalSourceDescription

/**
 * This is a Workflow that is Conceptually possible . Its The Abstract
 */
class ConceptualWorkflow implements ExternalSourceInfoInterface {
    ExternalSourceDescription extSource
    String sourceIdentifier
    String uRI
    /**
     * Simple and Short description what happens here
     */
    String plainDescription
    /**
     * Long description what this workflow does
     */
    String longDescription
    /**
     * List of Workflows that must be executed
     */
    List execWorkflows
    /**
     * List of File names which determine the order they are displayed in the Result. Put the Names of the Output files which are declared in the exec Workflow in the right order.
     */
    List outputFilesOrder
    //TODO Recheck if Necessary
    static Types = ["WGS", "RNA", "ChIP"]
    static hasMany = [execWorkflows: ExecWorkflow,outputFilesOrder:String]


    static constraints = {
        type: nullable: true
        extSource nullable: true
        uRI nullable: true
        sourceIdentifier nullable: true
        outputFilesOrder nullable: true

    }
    static mapping = {
        longDescription type: 'text'
    }
}
