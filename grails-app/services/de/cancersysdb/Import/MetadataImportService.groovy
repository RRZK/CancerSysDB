package de.cancersysdb.Import

import de.cancersysdb.ImportTools.CSVPreanalyser
import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.EntityMetadata.ImportInfo
import de.cancersysdb.EntityMetadata.ClinicalInformation
import de.cancersysdb.Patient
import de.cancersysdb.User
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.transaction.Transactional
import groovy.xml.QName
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib


/**
 * This Class Gets Meta/Clinical Data Information and Adds it to the General Entites like Sample Patient and Study.
 */
@Transactional
class MetadataImportService {
    def g = new ValidationTagLib()
    /*
        private Stack<Node> tags
    */
    //This is the Import ClinicalInformation from the start

    //Bad Tags which are forbidden!
    private List<String> badTags

    /**
     * Takes all Infos in the CSV-File and Appends it to the Patient
     *  Expects Format:
     *  PatientID;Key;Value
     * @param fileHandle The File to Parse
     * @param patient The patient to Append The Data to. Here an CSV Tag
     * @param blockers The Attributes no tobeProcessed Example [ "BadTags":["laml:patient","admin:admin"] ,"TobeImplemented": .... ] because they are allready parsed on other places.
     * @param importProtocol Optional Import Protocoll if not there it willbe Created
     * @return importProtocol The Import Protocoll... Shows how the Import Worked, you know...
     */
    ImportProtocol genericImportAttributesFromCSV(String filename, String fileHandle, Map blockers, User u, ImportProtocol importProtocol = null,annon =false ,shared=false) {
        ImportInfo ii
        badTags =[]
        if (blockers.containsKey("BadTags")) {
/*
            log.debug("Adding Tags to Ignore " + blockers.get("BadTags"))
*/
            badTags.addAll( blockers.get("BadTags"))
        }


        //Import Protocol
        if (importProtocol == null) {
            importProtocol = new ImportProtocol()
            importProtocol.ImportStart()
        }

        CSVPreanalyser csvpre = new CSVPreanalyser()

        csvpre.Preanaylse(fileHandle)

        Map patients = [:]

        fileHandle.toCsvReader(['charset': 'UTF-8', 'separatorChar': csvpre.getSeperator(), 'skipLines': csvpre.isHasHeadline()?1:0, "quoteChar": csvpre.getEnclose()]).eachLine {
            //Collect key, vals by Patient
            if (patients.containsKey(it[0]))
                patients.get(it[0]).put(it[1], it[2])
            else {
                Map val = [:]
                val.put(it[1], it[2])
                patients.put(it[0], val)
            }

        }
        //Iterate Patient step by step
        patients.each {String patientid,Map Values ->
            //Check by Identifier
            Patient patient = Patient.findBySourceIdentifier(patientid)
            if(!patient)
                patient = Patient.findByURI(patientid)
            //Ok Patient exists
            if(patient){
                importProtocol.setPatient(patient)
/*
                log.debug(patient)
                log.debug(Values)
*/

                ii = new ImportInfo(filename: filename, restricted: false, filetype: "csv", patient: patient, owner:u,annon:annon,shared:shared)
                if(ii.save()){
                    importProtocol.ImportedSuccessful(ii)
                    Values.each {key,val->
                        if(!badTags.contains(key)){

                            ClinicalInformation ci = new ClinicalInformation(value: val, location: "-", exactName: key, importInfo: ii)
                            if(ci.save()) {

                                importProtocol.ImportedSuccessful(ci)
                                ii.addToInfos(ci)
                            }else
                                importProtocol.ImportedFailed(ci)

                        }
                    }
                }
                else
                    importProtocol.ImportedFailed(ii)
                ii.save()

            }else{
                importProtocol.Message("cant find Patient with identifier "+ patientid)
            }



        }
        //Bigger than One Because only 1 Import Info is not Enough!
        if(importProtocol.successfulCount>1) {
            importProtocol.setSuccessful(true)
            def persistedImportProtocol = new PersistedImportProtocol(importProtocol)
            persistedImportProtocol.save()
        }else
            importProtocol.setSuccessful(false)
        importProtocol.ImportEnd()
        return importProtocol
    }



    /**
     * Takes all Infos in the XMLFile and Appends it to the Patient
     * @param fileHandle The File to Parse
     * @param patient The patient to Append The Data to
     * @param blockers The Attributes no tobeProcessed Example [ "BadTags":["laml:patient","admin:admin"] ,"TobeImplemented": .... ] because they are allready parsed on other places.
     * @param importProtocol Optional Import Protocoll if not there it willbe Created
     * @return importProtocol The Import Protocoll... Shows how the Import Worked, you know...
     */
    ImportProtocol genericImportAttributesFromXML(String filename, String fileHandle, Map blockers, User u, ImportProtocol importProtocol = null,annon =false ,shared=false) {
        //Oldu,ip, params.annon?:true, params.shared?:true
        //    ImportProtocol genericImportAttributesFromXML(String fileHandle, Patient patient, Map blockers, ImportProtocol importProtocol = null) {
        ImportInfo ii
        if (blockers.containsKey("BadTags")) {
            log.debug("Adding Tags to Ignore " + blockers.get("BadTags"))
            badTags = blockers.get("BadTags")
        }
        //Import Protocol
        if (importProtocol == null) {
            importProtocol = new ImportProtocol()
            importProtocol.ImportStart()
        }
        //Map namespaces = [] as Map
        XmlParser parser = new XmlParser(false, true)

        //Init XML parser
        Node content
        try {
            content = parser.parseText(fileHandle)

        } catch (Exception e) {
            String Message = "File Import Failed. XML Malformed?"
            log.debug(Message)
            importProtocol.Message(Message)

            importProtocol.setSuccessful(false)
            importProtocol.ImportEnd()
            //ds.discard()
            return importProtocol
        }
        def patientString = getUserIdFromData(fileHandle)
        if(!patientString){
            importProtocol.setSuccessful(false)
            importProtocol.Message("couldnt find the Patient Identifier in the XML file! ")
            return importProtocol
        }
        log.debug("Patient String " + patientString)
        def p = Patient.findBySourceIdentifier(patientString)

        if(!p){
            importProtocol.setSuccessful(false)

            importProtocol.Message("couldnt finld Patient with Identifier " + patientString)

            return importProtocol
        }

        //TODO Zugriffsrechte
        ii = new ImportInfo(filename: filename, restricted: false, filetype: "xml", patient: p,owner:u,annon:annon,shared:shared)
        importProtocol.setPatient(p)

        if (ii.save(flush: true)) {
            importProtocol.ImportedSuccessful(ii)
        } else {
            importProtocol.ImportedFailed(ii)
        }

        if(!ii.hasErrors()){
            String tagname = localTagname(content.name())
            String previouspath = "//" + tagname
            Const(content, previouspath, ii, importProtocol)
        }else{
            importProtocol.Message( importProtocol.Message(ii.errors.fieldErrors.collect{ g.message(error:it)}.toString()))

        }

        if(importProtocol.successfulCount>1)
            importProtocol.setSuccessful(true)
        else
            importProtocol.setSuccessful(false)
        return importProtocol

    }

    protected String getUserIdFromData(String fileHandle) {
        def response = new XmlSlurper().parseText(fileHandle)
        def result = response.'**'.find { node -> node.name() == "bcr_patient_barcode" }.text()
        log.debug(result)
        result
    }


    /**
     * Iterate the XML Tree and extract all Values and put them to the Clinical Data
     * @param content The Content Node which comes from the Parsed XML A Node Element
     * @param path The Xpath context of the Base Element
     * @param ii The Import Info which the Information Snippet is attached to
     * @param importProtocol The Import Protocoll
     */

    protected void Const(Node content, String path, ImportInfo ii, ImportProtocol importProtocol) {
        LinkedList<Map> q = new LinkedList<Map>()
        q.push([node: content, path: path])
        Map MaxCountTags = [:]
        def toProcess
        for (; q.size()>0;) {
            toProcess = q.last
            q.removeLast()
            Node n = toProcess.get("node")
            String ppath = toProcess.get("path")
            //Every Node here Contains more than Text

            MaxCountTags= maxCountTags(n)

            assert(n instanceof Node)
            assert(n.children().size()>0)
            Map CountTags=[:]
            n.children().each { child ->

                assert (child instanceof Node)

                Node chi = child

                String tagname = localTagname(child.name())
                String fName = createXpathFieldName(tagname,CountTags,MaxCountTags)

                if (chi.children().size() <= 1 && chi.text() && chi.text() != "") {
                    //TODO Dirty Mysql Workarround
                    String value
                    if(chi.text().size()> 255)
                        value = chi.text().subSequence(0,253)
                    else
                        value = chi.text()
                    ClinicalInformation ci = new ClinicalInformation(value:value, location: ppath, exactName: fName, importInfo: ii)

                    if (ci.save()) {
                        importProtocol.ImportedSuccessful(ci)
                        ii.addToInfos(ci)
                    } else {
                        importProtocol.ImportedFailed(ci)
                    }

                } else if (chi.children().size() > 1) {

                    q.addFirst( [node: chi, path: ppath + "/" + fName])


                } /*else
                    log.debug("discarded " + chi)*/

            }



            }



        }

    protected Map maxCountTags(Node n) {
        Map MaxCountTags = [:]
        n.children().each { node ->
            if (node instanceof Node) {
                def tname = node.name()
                String tagname = localTagname(tname)

                if (MaxCountTags.containsKey(tagname))
                    MaxCountTags.put(tagname, MaxCountTags.get(tagname) + 1)
                else
                    MaxCountTags.put(tagname, 1)
            } else
                log.debug("Node is " + node.class)


        }
        return MaxCountTags
    }







protected String createXpathFieldName( String tagname, Map CountTags,MaxCountTags) {

    Integer myval
    if (CountTags.containsKey(tagname)) {
        myval = CountTags.get(tagname) + 1
    } else {
        myval = 1
    }
    CountTags.put(tagname, myval)

    String fName = ""
    def getmaxval = MaxCountTags.get(tagname)
    if (getmaxval > 1)
        fName = tagname + "[" + myval + "]"
    else
        fName = tagname
    fName
}
/**
     * The Grails Parser is very unprecise what Name a Node has. This Function extracts the local Name and ignores the Namespace.
     * @param tname The Name Object given by Node.name() function of Groovy XML parser. It determines the type and gets the local name
     * @return String with local tagname whitout Namespace
     */
    protected String localTagname(tname) {
        String tagname
        if (tname instanceof QName) {
            QName qName = (QName) tname
            tagname = qName.getLocalPart()
            //log.debug("QName Tagname : " + tagname)
        } else {
            //log.debug("NotQName " + tname.class.toString())
            tagname = tname.toString()
            //log.debug("tagname " + tagname)
        }
        tagname
    }

}
