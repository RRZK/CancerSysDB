package de.cancersysdb.Import

import de.cancersysdb.ImportTools.ImportProtocol
import com.google.gson.stream.MalformedJsonException
import de.cancersysdb.contextHandling.CancersysBarcodeConventions
import de.cancersysdb.Dataset
import de.cancersysdb.DatasetService
import de.cancersysdb.Patient
import de.cancersysdb.Sample
import de.cancersysdb.serviceClasses.FiletypeToGeneticStandardMatching
import de.cancersysdb.Study
import de.cancersysdb.User
import de.cancersysdb.geneticStandards.TCGAClassObject
import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib
@Transactional
/**
 * This service manages the import of context data like samples, studies and patients and their diseases.
 */
class ContextConstructionService {
    //TODO Refactor, Manage Exceptions
    //Standardisation Helper to Translate Data....
    static Map KnownAliasses = ["Primary Tumor": "Primary solid Tumor"]
    DatasetService datasetService
    def g = new ValidationTagLib()
    /**
     * Import the File from the JSON file standard
     * @param fileHandle The JSON File Uploaded
     * @param owner The owner(uploader of the File)
     * @return The Import Protocol
     */
    public ImportProtocol createContextFromJSON(String fileHandle, User owner) {

        ImportProtocol importProtocol = new ImportProtocol()
        importProtocol.ImportStart()
        importProtocol.Message("Importing from JSON")
        importProtocol.Message("using class " + this.class.toString())
        JSONElement json = JSON.parse(fileHandle)
        importProtocol.Message("creating Dataset")
        Dataset ds = new Dataset(owner: owner, annon: json.getAt("Annon") ?: true, shared: json.getAt("Shared") ?: true, samples: [])
        def Mapping = json.getAt("Mapping")
        log.debug("Mapping " + Mapping)
        def importMapping = null
        if (Mapping) {
            importMapping = createMappingFromJSON(Mapping, json.getAt("DataType"), importProtocol)

        }
        if (importMapping)
            ds.matching = importMapping

        def originURL = json.getAt("Origin")
        if (originURL)
            ds.originURL = originURL
        def note = json.getAt("Description")
        if (note)
            ds.note = note

        importProtocol.Message("Create Contexts ")
        log.debug("json" + json)

        List contexts = createSamplesFromJson(fileHandle, owner, importProtocol, true, true)
        log.debug("Contexts: " + contexts)
        if (contexts != null) {
            contexts.each {
                ds.addToSamples(it)
            }
            ds.save()
            importProtocol.dataset = ds
        } else {
            ds.discard()
            importProtocol.Message("Failed: Context could not be completely constructed")
            importProtocol.successful = false

        }


        return importProtocol
    }


    public FiletypeToGeneticStandardMatching createMappingFromJSON(JSONObject MappingObject,
                                                                   def TC = null, ImportProtocol ip = null) {
        Class TargetClass
        if (!TC)
            TC = MappingObject.getAt("TargetClass")
        if (TC instanceof Class) {
            TargetClass = TC
        } else
            TargetClass = datasetService.getDataClassForName(TC.toString())

        def authorityName = MappingObject.getAt("authorityName")

        if (!authorityName)
            return null
        def seperator
        if (MappingObject.getAt("Seperator"))
            seperator = MappingObject.getAt("Seperator")
        else
            seperator = MappingObject.getAt("seperator")
        if (!seperator || seperator.toString().length() == 0) {
            if (ip)
                ip.Message("Seperator Missing")
            else
                log.debug("Seperator Missing")
            return null
        }

        FiletypeToGeneticStandardMatching temp
        //FiletypeToGeneticStandardMatching temp = FiletypeToGeneticStandardMatching.findByAuthorityName(authorityName)

        if (!TargetClass)
            return null
        if (ip != null)
            ip.Message("Creating Mapping ")
        else
            log.debug("Creating Mapping ")
        //if(!temp)
        temp = new FiletypeToGeneticStandardMatching(MappingObject)

        if (seperator.toString().equals("tab") || seperator.toString().equals("Tab") || seperator.toString().equals("\\t"))
            temp.setSeperator(" ")
        else
            temp.setSeperator(seperator.toString())

        temp.setTargetClass(TargetClass.simpleName)
        if (!temp.save()) {
            log.debug(temp.errors)
            if (ip != null)
                ip.Message("Failed Creating Mapping !")
            else
                log.debug("Failed Creating Mapping !")
            return null
        } else
            return temp

        return temp

    }


    public List<Sample> createSamplesFromJson(String fileHandle, User owner, ImportProtocol importProtocol, boolean annon = true, boolean shared = true) {
        JSONElement json = JSON.parse(fileHandle)
        log.debug(fileHandle.bytes.toString())
        def FollowsTCGA = true
        if (json.containsKey("TCGALogic"))
            FollowsTCGA = json.getAt("TCGALogic")

            List<Sample> out  = []
            def noPatientContext = json.getAt("NoPatientContext")
            if(noPatientContext)
                return out

            def temps = json.getAt("Contexts")
            if (temps) {
                temps.each { it->
                    if(!it instanceof JSONObject)
                        throw new MalformedJsonException("Context is not described by object but by string!")

                //Get Sample Barcode
                String smpid = null
                if (it.containsKey("SampleBarcode"))
                    smpid = it.getAt("SampleBarcode")
                if (!smpid && it.containsKey("Barcode")) {
                    smpid = it.getAt("Barcode")
                }
/*                    if(!smpid && it.containsKey("PatientBarcode") ){
                        def pbarcode = it.getAt("PatientBarcode")
                        def patient = Patient.findBySourceIdentifier(pbarcode)

                        def cancertype = 	null
                        def tissuelocation = null
                        def tissueType = null

                        if(it.containsKey("TissueLocation")){
                            tissuelocation = TCGAClassObject.findAllByAbbreviation(it.getAt("TissueLocation"))?.get(0) ?: null
                        }
                        if(it.containsKey("Cancertype")){
                            cancertype = TCGAClassObject.findAllByAbbreviation(it.getAt("Cancertype"))?.get(0) ?: null
                        }
                        if(it.containsKey("Cancertype")){
                            tissueType = TCGAClassObject.findAllByAbbreviation(it.getAt("TissueType"))?.get(0) ?: null
                        }

                        if(tissueType && cancertype &&tissuelocation && patient){
                            temp =

                        }

                    }*/

                if (FollowsTCGA)
                    smpid = CancersysBarcodeConventions.BarcodeToSample(smpid)

                def temp = Sample.findBySourceIdentifier(smpid)

                if (temp) {
                    out.add(temp)
                    importProtocol.Message("in Database: " + it.getAt("Barcode"))
                } else {
                    //Create Context by ClinicalInformation
                    //importProtocol.Message(it.getAt("Barcode") + "Not in Database... creating")
                    //Create Sample
                    //smpid Should be right from previous processing


                    Sample sample = createSampleFromJsonContext(smpid, FollowsTCGA, it, json, importProtocol, owner)


                    if (sample && !sample.hasErrors()) {

                        out.add(sample)
                    } else {
                        return null
                    }

                }
            }
        } else {
            importProtocol.Message("No Contexts given")
            log.debug(json)
            return null
        }
        return out
    }

    /**
     * This Function takes a Context and derives a Sample and possibly a Study and a Patient out of it.
     * @param smpid The ID of the Sample we know
     * @param FollowsTCGA Boolean if the Barcodes etc follow the TCGA Scheme
     * @param it The Special Infos for this Context.
     * @param json The General JSON File for general Options
     * @return Sample which is created from context info
     */
    private Sample createSampleFromJsonContext(String smpid, boolean FollowsTCGA, JSONObject it, JSONObject json, ImportProtocol importProtocol, User owner) {
        Sample smpl = null
        def patient
        log.debug("CREATION!")
        //Find Patient
        if (smpid) {

            if (FollowsTCGA)
                patient = Patient.findBySourceIdentifier(CancersysBarcodeConventions.BarcodeToPatient(smpid))
            else
                patient = Patient.findBySourceIdentifier(smpid)
        } else {
            smpid = it.getAt("PatientBarcode")
            patient = Patient.findBySourceIdentifier(it.getAt("PatientBarcode"))

        }
        //If Patient Does not Exist
        if (!patient && smpid) {
            importProtocol.Message("Patient Not found Will be created!")
            def studyid = null
            if (FollowsTCGA)
                studyid = CancersysBarcodeConventions.BarcodeToStudy(smpid) + "_" + json.getAt("Cancertype")
            else
                studyid = json.getAt("Study")
            Study study = null
            log.debug("ID of the Study " + studyid)
            if (studyid) {
                //Search Study

                study = Study.findBySourceIdentifierOrDescriptionOrReferenceIdentifier(studyid, studyid, studyid)
                if (!study) {
                    //Create Study
                    importProtocol.Message("Study " + studyid + " Will be created!")
                    study = new Study(description: studyid, referenceIdentifier: studyid)

                    if (!study.save(flush: true)) {
                        log.debug(study.errors)
                        importProtocol.Message("could not create Study " + it.getAt("Study"))
                        return null
                    } else
                        importProtocol.Message("created Study " + study)

                } else
                    importProtocol.Message("Study " + studyid + " found !")


            }
            if (FollowsTCGA)
                patient = new Patient(sourceIdentifier: CancersysBarcodeConventions.BarcodeToPatient(smpid), study: study)
            else
                patient = new Patient(sourceIdentifier: smpid, study: study)
            if (!patient.save(flush: true)) {

                log.debug(patient.errors)
                importProtocol.Message("could not create Patient " + smpid)
                return null

            } else
                importProtocol.Message("created Patient " + smpid)
        }
        //If we finally found the patient, we will create the sample
        if (patient) {
            Map parama = [:]
            parama.put("patient", patient)
            if (it.getAt("Cancertype") ?: json.getAt("Cancertype"))
                parama.put("cancerType", getTCGAClassObject(it.getAt("Cancertype") ?: json.getAt("Cancertype")))
            if (it.getAt("TissueType") ?: json.getAt("TissueType"))
                parama.put("tissueType", getTCGAClassObject(it.getAt("TissueType") ?: json.getAt("TissueType")))
            if (it.getAt("TissueLocation") ?: json.getAt("TissueLocation"))
                parama.put("location", getTCGAClassObject(it.getAt("TissueLocation") ?: json.getAt("TissueLocation")))
            if (it.getAt("Label") ?: json.getAt("Label"))
                parama.put("location", getTCGAClassObject(it.getAt("Label") ?: json.getAt("Label")))


            def samps = Sample.findAllWhere(parama)

            importProtocol.Message("")
            if (samps.empty) {
                Map parama2 = [annon: it.getAt("annon") ?: json.getAt("annon") ?: true, shared: it.getAt("shared") ?: json.getAt("shared") ?: true, owner: owner]
                if (it.getAt("Label") ?: json.getAt("Label") && !FollowsTCGA) {
                    parama2.put("label", it.getAt("Label") ?: json.getAt("Label"))
                } else if (FollowsTCGA && CancersysBarcodeConventions.isSampleBarcode(it.getAt("Label") ?: json.getAt("Label")))
                    parama2.put("label", it.getAt("Label") ?: json.getAt("Label"))
                else
                    parama2.put("label", smpid)

                parama.putAll(parama2)
                smpl = new Sample(parama)
                smpl.setSourceIdentifier(smpid)

            } else if (samps.size() > 1) {
                importProtocol.Message("Too Much Sample Candidates ")
                return null


            } else {

                return samps.first()
            }
        }

        if (smpl && smpl.save(flush: true)) {
            importProtocol.Message("created Sample " + smpid)
            return smpl
        } else {

            importProtocol.Message("Failed to Create Sample " + smpid)
            log.debug(smpl.errors)

            smpl.discard()
            return null
        }


    }

    /**
     * This Function should construct a Dataset Context from an XML File
     * The Main focus lies on the Metadata that is supplied with the TCGA Data.
     * @param fileHandle The File Handle to Parse
     * @return Returns the Contextualized Dataset which is ready to precive the Data for Attachment
     */
    public ImportProtocol createContextFromXML(String fileHandle, User owner, annon = true, shared = true) {
        ImportProtocol importProtocol = new ImportProtocol()
        importProtocol.ImportStart()
        //Map namespaces = [] as Map
        Dataset ds = new Dataset(owner: owner, annon: annon, shared: shared, samples: [])



        List<Sample> temp = createSamplesFromXML(fileHandle, owner, importProtocol)
        temp.each {
            ds.addToSamples(it)
        }
        //Save Dataset Samples
        if (ds.save(flush: true)) {
            importProtocol.ImportedSuccessful(ds)
            importProtocol.setSuccessful(true)
            importProtocol.setDataset(ds)
            log.debug("attached Samples" + ds.getSamples().size())
            return importProtocol
        } else if (ds.hasErrors(flush: true)) {
            importProtocol.Message( importProtocol.Message(ds.errors.fieldErrors.collect{ g.message(error:it)}.toString(), true))


            importProtocol.setSuccessful(false)
            ds.discard()
            log.debug("attached Samples" + ds.getSamples().size())

            return importProtocol

        }

    }

    public List<Sample> createSamplesFromXML(String fileHandle, User owner, ImportProtocol importProtocol, boolean annon = true, boolean shared = true) {
        List<Sample> out = []
        XmlParser parser = new XmlParser(false, true)

        //parser
        def content
        try {
            content = parser.parseText(fileHandle)

        } catch (Exception e) {
            importProtocol.Message("File Import Failed. XML Malformed?")
            importProtocol.setSuccessful(false)
            importProtocol.ImportEnd()
            //ds.discard()
            return importProtocol

        }
        //TODO Check namesspaces ->
        // xmlns:bio="http://tcga.nci/bcr/xml/biospecimen/2.6"
        // xmlns:admin="http://tcga.nci/bcr/xml/administration/2.6"
        // xmlns:shared="http://tcga.nci/bcr/xml/clinical/shared/2.6"

        //Create Study
        //print path
        def test
        test = content."admin:admin"."admin:project_code" + "_" + content."admin:admin"."admin:disease_code"

        Study study = Study.findByReferenceIdentifier(test.text())

        if (!study) {
            study = new Study(sourceIdentifier: test.text(), description: test.text(), referenceIdentifier: test.text())
            if (study.save(flush: true))

                importProtocol.ImportedSuccessful(study)

            else {
                importProtocol.ImportedFailed(study)

                importProtocol.Message("File Import Failed. XML Malformed?")
                importProtocol.setSuccessful(false)
                importProtocol.ImportEnd()
                study.discard()
                return null

            }


        } else {
            importProtocol.Message("Study " + test.text() + " allready in Database", false)
        }


        def cancerType = getTCGAObj(content."admin:admin"."admin:disease_code".text())
        //Create Patients
        def patients = content.'**'."bio:patient"
        patients.each {
            pati ->
                def patientIdentifier = CancersysBarcodeConventions.BarcodeToPatient(pati."shared:bcr_patient_barcode".text())
                boolean patientnew = false
                Patient patient = Patient.findBySourceIdentifier(patientIdentifier)
                if (patient) {
                    importProtocol.Message("Patient " + patientIdentifier + " allready in Database", false)
                } else {
                    patient = new Patient(sourceIdentifier: patientIdentifier, study: study)
                    patientnew = true
                    if (patient.save(flush: true)) {

                        importProtocol.ImportedSuccessful(patient)
                        log.debug("Patient saved")
                    } else
                        importProtocol.ImportedFailed(patient)
                }

                //Create Samples
                pati."bio:samples"."bio:sample".each {
                    samp ->

                        def barcode = CancersysBarcodeConventions.BarcodeToSample(samp."bio:bcr_sample_barcode".text())
                        barcode = CancersysBarcodeConventions.BarcodeToSample(barcode)
                        log.debug("barcode: " + barcode)
                        Sample sample = Sample.findBySourceIdentifier(barcode)

                        if (!sample) {
                            sample = new Sample()
                            sample.setSourceIdentifier(barcode)
                            sample.setLabel(barcode)
                            sample.setOwner(owner)
                            sample.setShared(shared)
                            sample.setAnnon(annon)
                            sample.setPatient(patient)
                            def sampletype = getTCGAObj(samp."bio:sample_type".text())
                            if(!sampletype)
                                sampletype = getTCGAObj(samp."bio:sample_type_id".text())

                            if (sampletype)
                                sample.setTissueType(sampletype)



                            if (cancerType)
                                sample.setCancerType(cancerType)

                            if (sample.save(flush: true) && !sample.hasErrors()) {
                                patient.addToSamples(sample)

                                out.add(sample)
                                importProtocol.ImportedSuccessful(sample)

                            } else {
                                //log.debug("Problems " + sample.errors.collect{ g.message(error:it)})
                                importProtocol.Message("Sample "+ barcode +" " + sample.errors.fieldErrors.collect{ g.message(error:it)})

                                importProtocol.ImportedFailed(sample)
                                sample.discard()

                            }
                        } else {
                            importProtocol.Message("Sample " + barcode + " allready in Database",false)
                            out.add(sample)

                        }
                }

                if (study && patient) {
                    study.addToPatients(patient)
                    patient.setStudy(study)
                    study.save(flush: true)
                    patient.save(flush: true)


                }

        }
        return out
    }
    /**
     * Retrive TCGA Controlled Vocabulary
     * @param identifier Athe Identifier tobe Checked
     * @return TCGAClassObject that fits the Identifier
     */
    private TCGAClassObject getTCGAObj(String identifier) {
        //There are some Coding Issues with the TCGA Standard. Sometome the Data does not fit. Like they say in Germany "What does not fit is made fitting!"(Was nicht passt wird passend gemacht.)
        if (KnownAliasses.get(identifier))
            identifier = KnownAliasses.get(identifier)

        TCGAClassObject out = TCGAClassObject.findByNameIlike(identifier)
        if (out)
            return out
        out = TCGAClassObject.findByAbbreviationIlike(identifier)
        if (out)
            return out
        out = TCGAClassObject.findByCodeIlike(identifier)
        if (out)
            return out
        else
            return null


    }

    private TCGAClassObject getTCGAClassObject(String identifier) {
        TCGAClassObject out = null
        if (identifier == null)
            return out

        out = TCGAClassObject.findByNameOrAbbreviationIlike(identifier, identifier)
        return out

    }


}