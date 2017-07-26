package de.cancersysdb.workflow
/**
 * This Object describes a file which will be exported from the Database for analysis
 */
class WorkflowDataDescription {

    //String baseClass
    //
    /**
     * The HQL Query to describe Data
     */
    String hqlQuery
    /**
     * The Parameters Attached to the Query as Map
     */
    Map parametersForQuery
    /**
     * The Output fields
     */
    List outputFields


    String outputName
    /**
     * The name of this
     */
    String name
    List inputData
    /**
     * The Exec Workflow this belongs to
     */

    ExecWorkflow workflow

    /**
     * DescribesBinaryFile
     */

    Boolean binaryFile=false
    /**
     * The Output CSV has a Header line
     */
    Boolean headers=false

    /**
     * The Put Parameter to File ... made for List inputs
     */
    Boolean parameterToFile=false

    static hasMany = [inputData: WorkflowDataDescription, outputFields: String, parametersForQuery: WorkflowInputParameter]
    static belongsTo = [workflow: ExecWorkflow]

    static mapping = {

        hqlQuery type: 'text'
    }
    static constraints = {
        hqlQuery nullable:true

    }
}
