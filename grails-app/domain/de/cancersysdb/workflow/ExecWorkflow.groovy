package de.cancersysdb.workflow

/**
 * This Workflow describes a List of commands wich will be executed,  with Input Output description etc.
 */
class ExecWorkflow {
    /**
     * The Name that attribues this class
     */
    String name

    String dataPath
    /**
     * The Description of the Workflow
     */
    String description

    /**
     * The Results this Workflow creates  -> Keys Filenames, Values type
     */
    Map outputFiles
    /**
     * The Data which is input for the Scripts
     */
    List inputData

    /**
     * The Files containted in this Workflow
     */
    List files
    /**
     * The Commands tobe Executed on commandline to finish this workflow
     */
    List excecutionCommands
    /**
     * The conceptual Workdlow this belongsto
     */
    ConceptualWorkflow conceptualWorkflow

    /**
     *  The Commands which should be Executed to test if everything Necesary is installed to start this workflow
     */
    //TODO Use and Check Workflows
    List verificationCommands

    static hasMany = [inputData: WorkflowDataDescription, files: String, excecutionCommands: String, verificationCommands: String, outputFiles: String]
    static belongsTo = [conceptualWorkflow:ConceptualWorkflow]

    static constraints = {

        name nullable: false, unique: true
        dataPath nullable: false, unique: true


    }
}