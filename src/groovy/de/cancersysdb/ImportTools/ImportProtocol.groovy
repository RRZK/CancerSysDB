package de.cancersysdb.ImportTools

import de.cancersysdb.Dataset
import de.cancersysdb.Patient
import de.cancersysdb.serviceClasses.FiletypeToGeneticStandardMatching
import org.apache.commons.logging.LogFactory
import org.springframework.validation.FieldError

/**
 * Class to Modell Import Status, Progress, Accurency and Failures
 */
class ImportProtocol {

    ////Settings
    //Autoreport Adds Messages Automatically and Propergates
    boolean Autoreport = true
    //Print Progess every Nth Dataset
    Integer AutoreportAferEveryNTHDataset = 0
    Long AENthTimeDeltaTemp = 0
    boolean deepErrorDocumentation = true

    //Output Options
    //Console
    boolean toConsole = true
    //WebSockets
    def brokerMessagingTemplate = null
    def messageSock = null
    //Logging
    boolean activateLogging = false
    private static final log = LogFactory.getLog(this)

    //Internals
    List<String> messages = []
    List<String> errorMessages = []
    boolean successful = false
    //Result
    Dataset dataset = null
    Patient patient = null

    String ResultLink = ""

    FiletypeToGeneticStandardMatching matching

    //Statistics
    Long Starttime = 0
    Long Endtime = 0
    Long timeconsumed = 0

    Integer successfulCount = 0
    Integer failedCount = 0
    Map SuccessfulImportStats = [:]
    Map FailImportStats = [:]
    Map FailureReasons = [:]

    ImportProtocol() {

        messages = new ArrayList<String>()
        successful = false

    }
    /**
     * Save Message and eventually Propergate
     * @param Message The Message tobe Saved and Propergated
     * @return The Message
     */
    String Message(String Message) {

        this.Message(Message, true)
        return Message
    }
    /**
     * Save Message and Propergate
     * @param Message Message The Message tobe Saved
     * @param doPropergate Do Additional Outpiuts Console or Websockets
     * @return The Message
     */
    String Message(String Message, boolean doPropergate) {

        messages.add(Message)
        if (doPropergate)
            Propergate(Message)

    }
    /**
     * This Function Manages the Propergation of the Message It can push results to a Websocket to a Terminal or to the Log4JLogging
     * @param Message The Message to Log
     * @return Nothing
     */
    private def Propergate(String Message) {
        if (toConsole)
            log.info(Message)
        //Put to websockets
        if (brokerMessagingTemplate && messageSock)
            brokerMessagingTemplate.convertAndSend messageSock, Message
        if (activateLogging)
            log.info(Message)

    }
    /**
     * Start an Import, set The Start Time for Calulations of Time Needed for Import
     */
    void ImportStart() {
        if (Starttime != 0) {
            this.Message("Import Started twice")
            return
        }

        Starttime = System.currentTimeMillis()

        successfulCount = 0
        failedCount = 0
        SuccessfulImportStats = [:]
        FailImportStats = [:]
        if (Autoreport)
            this.Message("Import Started")
    }
    /**
     * This ends the Report, sets the End Time to Calculate Metrics etc.
     */
    void ImportEnd() {
        if (Endtime != 0) {
            this.Message("import ended twice!")
        }
        Endtime = System.currentTimeMillis()
        timeconsumed = Endtime - Starttime

        if (Autoreport) {
            this.Message("Import End")
            if ((failedCount + successfulCount) > 0) {
                this.Message("processed Lines :" + (failedCount + successfulCount))
                this.Message("Succsessful imported :" + successfulCount)
                this.Message("Failed to import :" + failedCount)
                if (deepErrorDocumentation) {
                    FailureReasons.each {
                        key, val ->
                            this.Message(key + ", occurred Times : " + val)
                    }

                }
                this.Message(timeconsumed / (failedCount + successfulCount) + " ms per line")


            }


        }
    }
    /**
     * Get The last Message from the Messages Stack
     * @return String of the Last Message
     */
    String getLastMessage() {
        if (messages.empty)
            return ""
        else
            return messages.last()

    }
    /**
     * Log the Successufully Imported Dataset, This is Esspecially Interesing if there are More than 1 Type of Datasets Imported
     * @param imported The Successfull Imported Dataset
     */
    void ImportedSuccessful(def imported) {
        def key = imported.class.simpleName
        if (SuccessfulImportStats.containsKey(key))
            SuccessfulImportStats.put(key, SuccessfulImportStats.get(key) + 1)
        else
            SuccessfulImportStats.put(key, 1)

        successfulCount++
        autoreport()

    }
    /**
     * Log the Datasets failed to import, This is wsspecially Interesing if there are More than one Type of Datasets Imported
     * @param imported The Object that was'nt Successfully imported.
     */
    void ImportedFailed(def imported) {

        def key = imported.class.simpleName
        if (FailImportStats.containsKey(key))
            FailImportStats.put(key, FailImportStats.get(key) + 1)
        else
            FailImportStats.put(key, 1)
        failedCount++
        autoreport()

        documentFailure(imported)


    }

    def private autoreport() {
        if (!Autoreport)
            return

        if (AutoreportAferEveryNTHDataset > 0 && failedCount + successfulCount > 0)
            if (failedCount + successfulCount % AutoreportAferEveryNTHDataset == 0) {
                this.Message((failedCount + successfulCount) + " lines processed")
                if (AENthTimeDeltaTemp == 0)
                    AENthTimeDeltaTemp = Starttime

                def curr = System.currentTimeMillis()
                def timedeltas = curr - AENthTimeDeltaTemp
                this.Message(timedeltas / AutoreportAferEveryNTHDataset + " ms per line")
                AENthTimeDeltaTemp = curr

            }


    }
    /**
     * Document the Reasons why an Dataobject wasnt imported successfully
     * @param imported The Data Object
     */
    void documentFailure(def imported) {
        if (!deepErrorDocumentation)
            return

        if (imported.metaClass.respondsTo(imported, "hasErrors") && imported.hasErrors()) {
            String CompiledErrors = imported.errors.getFieldErrors().collect { shortFieldError(it) }.join(",")

            errorMessages.add(CompiledErrors)
            if (activateLogging)
                log.warn(CompiledErrors)
            Integer Num = 0
            if (!FailureReasons.containsKey(CompiledErrors))
                FailureReasons.put(CompiledErrors, Num)
            Num = FailureReasons.get(CompiledErrors) as Integer
            Num = Num + 1
            FailureReasons.put(CompiledErrors, Num)
        } else
            return

    }

    /**
     * This Function Shortens FieldErrors for a Better User Experience
     * @param error FieldError Form an errors
     * @return String with the error String
     */
    String shortFieldError(FieldError error) {

        return "Field " + error.getField() + " rejected Value '" + error.getRejectedValue().toString() + "'"

    }


}
