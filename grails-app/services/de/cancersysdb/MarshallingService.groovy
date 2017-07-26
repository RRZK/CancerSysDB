package de.cancersysdb

import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * This Service Sets The Marshalling Exporters.
 * The actual Marshallers and their configs are defined in the Domain Objects!
 * The Domain Classes can define their own marshallers inline.
 * They have to Define the static marshallers Map.
 *
 * static marshallers = [
 * "exchange":
 * {Closure Containing Marshalling for Context of exchange info returning Map},
 * "standard":
 * {Closure Containing standard Marshalling behavior returning Map},
 *  ....
 * ]
 * for an Example @see de.cancersysdb.Dataset
 * The Map holds String Keys as the Context of Marshalling here for example "exchange".
 * defining a "standard" context will directly insert The Marshallers as Standard.
 * The Values are Closures getting the Object to Serialize as first parameter and a Map to Serialize as Output.
 * http://docs.grails.org/2.5.x/guide/single.html#objectMarshallers
 */
@Transactional
class MarshallingService {

    GrailsApplication grailsApplication
    /**
     * Iterates over Existing Domain Classes and parses the static marshallers Map Gets usually run at Startup
     * @return
     */
    void registerMarshallers() {

        def config = [:]
        //Iterate every Domain Class to see if they got Marshallers Defined
        grailsApplication.domainClasses.each {

            def klass = it.getClazz()
            //Repack Marshaller to create Named Configs at Once
            if (klass.metaClass.hasProperty(klass, "marshallers")) {
                log.debug("Marshaller Class " + it.getClazz())
                //Iterate each static Marshaller Context
                klass.marshallers.each {
                    String k, v ->
                        //If there is no Key for a Marshalling Config It is set and gets a new Map entry for the Class
                        if (!config[k])
                            config[k] = [:]
                        config[k][klass] = v


                }
            }

        }
        //Unwrap The named config to Register all Marshallers for one Config at Once.
        config.each {
            String k, v ->
                //Here the Standard Context is Chosen!
                if(k.equals("standard")){
                    v.each {
                        Klazzz, clos ->

                            JSON.registerObjectMarshaller(Klazzz, clos)

                    }
                }
                else{
                    JSON.createNamedConfig(k, {
                        v.each {
                            Klazzz, clos ->

                                it.registerObjectMarshaller(Klazzz, clos)

                        }
                    })
                }
        }


    }

}
