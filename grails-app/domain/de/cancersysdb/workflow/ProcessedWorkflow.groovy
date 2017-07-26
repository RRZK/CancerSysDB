package de.cancersysdb.workflow

import de.cancersysdb.ImportTools.ExternalSourceInfoInterface
import de.cancersysdb.serviceClasses.ExternalSourceDescription
import de.cancersysdb.User
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * This is a Finished Workflow Its Persisted.
 */
class ProcessedWorkflow implements ExternalSourceInfoInterface {
    //ExternalSourceInfoInterface
    ExternalSourceDescription extSource
    String sourceIdentifier
    String uRI


    /**
     * User who executed the Script
     */
    User executor
    /**
     * The description
     */
    String description
    /**
     * Start of Import
     */
    Date start
    /**
     * The Conceptional Workflow this runned workflow is based on
     */
    ConceptualWorkflow concept
    /**
     * The Path of the Files
     */
    String resultFileLocation
    /**
     * Parameters
     */
    String callerParameters
    /**
     * Parameters, Unique
     */
    String uniqueCallerParameters
    /**
     * The output files : Key is General Filename, Value ActualFilename
     */
    Map outputFiles
    //TODO Implement shared behavior
    /**
     * Shared?
     */
    Boolean shared = true
    /**
     * This is the Random Id to make the result callable from the outside without beeing guessable
     */
    String uuid = UUID.randomUUID().toString()

    static hasMany = [outputFiles: String]

    def showInputParameters(){
        try{
        def prams = JSON.parse( this.getUniqueCallerParameters())
        def oprams =[]
        log.debug("Hello is it you Im looking for "+ prams)
        prams.each{ key,val->

            if(val && val != ""){

                if(val instanceof JSONObject)
                {
                    if(val.get("class").endsWith("Gene"))
                        oprams.add(key+":"+val.get("name"))
                }else if(val instanceof JSONArray){
                    String out = val.toString()
                    if(out.size()>20)
                        out = val.toString().subSequence(0,17)+"..."
                    oprams.add(key+":"+out)
                } else {
                    if(val instanceof String)
                        oprams.add(key+":"+val)
                    else
                        oprams.add(key+":"+val.get("name"))
                }
            }
        }

        return oprams.join(", ")
        } catch(Exception e){

            log.error(e.message)
            return "Parameter cant be displayed"

        }
    }


    static constraints = {

        extSource nullable: true
        uRI nullable: true
        sourceIdentifier nullable: true
        concept nullable: true
        executor nullable: true
        uuid nullable: false, unique: true
    }

    static mapping = {

        callerParameters type: 'text'

        uniqueCallerParameters type: 'text'
    }

    static def marshallers = ["exchange":
                                      { ProcessedWorkflow pw ->

                                          return [
                                                  id         : pw.id,
                                                  class      : pw.getClass().name,
                                                  executor   : pw.executor,
                                                  concept    : pw.concept,
                                                  description: pw.description
                                          ]
                                      }]
}
