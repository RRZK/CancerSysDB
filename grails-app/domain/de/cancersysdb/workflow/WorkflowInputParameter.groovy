package de.cancersysdb.workflow
/**
 * This is an Parameter for a Workflow. The datatype describes what it stands for
 */
class WorkflowInputParameter {
    /**
     * Description of the Parameter so users understand what this parameter is about
     */
    String description
    /**
     * Name of the Parameter
     */
    String name
    /**
     * Datatype Can either be an Java basic Datatype or a Gene
     */
    String dataType
    /**
     * Is optional and can be skipped on Input
     */
    Boolean optional
    /**
     * The field is Restricted to Pre-Chosen Values
     */
    String predefinedValuesQuery

    WorkflowInputParameter workflowInputParameter
    static belongsTo = [workflowInputParameter:WorkflowInputParameter]
    static constraints = {
        predefinedValuesQuery blank: true, nullable: true
    }
}
