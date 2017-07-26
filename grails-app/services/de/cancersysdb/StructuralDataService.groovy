package de.cancersysdb

import de.cancersysdb.FrontendTools.StringRepresentationInterface
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass


/**
 * This Service Manages Structural Data and Data access for Special Searches.
 */
@Transactional
class StructuralDataService {
    def springSecurityService
    GrailsApplication grailsApplication
    DatasetService datasetService

    /**
     * Returns a map of possibly choosen Studies datasets and Patients where accessable Data is availible to the User
     * @param stud The Study as Filter criteria
     * @param pat The Patient as Filter criteria
     * @return A Map with Owned and Possible Data.
     */
    Map possibleSamplesSelection(Study stud, Patient pat, Sample samp, Dataset dataset) {
        User user = springSecurityService.getCurrentUser()
        //Output Data structure
        Map out = [:]
        out["Studies"] = [:]
        out["Patients"] = [:]
        out["Samples"] = [:]
        def ownedSamples
        def accessableSamples
        ownedSamples = Sample.findAllByOwner(user)

        accessableSamples = Sample.findAllByAnnonOrShared(true, true)
        accessableSamples.removeAll(ownedSamples)


        out["Studies"]["Own"] = []
        out["Studies"]["Accessable"] = convertToSelectBoxValues(Study.getAll())



        if (pat) {


            Set SampleTemp1 = [] as Set
            Set SampleTemp2 = [] as Set
            ownedSamples.each { SampleTemp1.add(it.patient) }
            accessableSamples.each { SampleTemp2.add(it.patient) }



            SampleTemp2.removeAll(SampleTemp1)

            out["Patients"]["Own"] = convertToSelectBoxValues(SampleTemp1)
            out["Patients"]["Accessable"] = convertToSelectBoxValues(SampleTemp2)


            SampleTemp1.clear()
            SampleTemp2.clear()
            ownedSamples.each { if (it.patient.id.equals(pat.id)) SampleTemp1.add(it) }
            accessableSamples.each { if (it.patient.id.equals(pat.id)) SampleTemp2.add(it) }


            out["Samples"]["Own"] = convertToSelectBoxValues(SampleTemp1)
            out["Samples"]["Accessable"] = convertToSelectBoxValues(SampleTemp2)


            out["Samples"]["Active"] = ""

            out["Patients"]["Active"] = convertToSelectBoxValue(pat)


            out["Studies"]["Active"] = convertToSelectBoxValue(pat.study)

        } else if (stud) {

            Set SampleTemp1 = [] as Set
            Set SampleTemp2 = [] as Set

            Set Patienttem1 = [] as Set
            Set Patienttem2 = [] as Set

            Set patientsIDs = [] as Set

            stud.getPatients().each { patientsIDs.add(it.id) }

            ownedSamples.each {
                Patient tempat = it.patient
                if (patientsIDs.contains(tempat.id)) {
                    SampleTemp1.add(it)
                    Patienttem1.add(it.patient)
                }
            }
            accessableSamples.each {
                if (patientsIDs.contains(it.patient.id)) {
                    SampleTemp2.add(it)
                    Patienttem2.add(it.patient)
                }
            }


            SampleTemp2.removeAll(SampleTemp1)
            out["Samples"]["Own"] = convertToSelectBoxValues(SampleTemp1)
            out["Samples"]["Accessable"] = convertToSelectBoxValues(SampleTemp2)
            Patienttem2.removeAll(Patienttem1)
            out["Patients"]["Own"] = convertToSelectBoxValues(Patienttem1)
            out["Patients"]["Accessable"] = convertToSelectBoxValues(Patienttem2)


            SampleTemp1.clear()
            SampleTemp2.clear()

            out["Samples"]["Active"] = ""


            out["Patients"]["Active"] = ""

            out["Studies"]["Active"] = convertToSelectBoxValue(stud)

        } else {
            Set SampleTemp1 = [] as Set
            Set SampleTemp2 = [] as Set

            Set Patienttem1 = [] as Set
            Set Patienttem2 = [] as Set
            ownedSamples.each {

                SampleTemp1.add(it)
                Patienttem1.add(it.patient)

            }
            accessableSamples.each {

                SampleTemp2.add(it)
                Patienttem2.add(it.patient)

            }



            SampleTemp2.removeAll(SampleTemp1)
            out["Samples"]["Own"] = convertToSelectBoxValues(SampleTemp1)
            out["Samples"]["Accessable"] = convertToSelectBoxValues(SampleTemp2)
            Patienttem2.removeAll(Patienttem1)
            out["Patients"]["Own"] = convertToSelectBoxValues(Patienttem1)
            out["Patients"]["Accessable"] = convertToSelectBoxValues(Patienttem2)

            if (samp) {
                out["Samples"]["Active"] = convertToSelectBoxValue(samp)
                pat = samp.patient
                out["Patients"]["Active"] = convertToSelectBoxValue(pat)
                stud = pat.study
                out["Studies"]["Active"] = convertToSelectBoxValue(stud)

            } else if (dataset) {
                samp = dataset.getSamples().first()
                out["Samples"]["Active"] = convertToSelectBoxValue(samp)
                pat = samp.patient
                out["Patients"]["Active"] = convertToSelectBoxValue(pat)
                stud = pat.study
                out["Studies"]["Active"] = convertToSelectBoxValue(stud)

            } else {
                out["Samples"]["Active"] = ""
                out["Patients"]["Active"] = ""
                out["Studies"]["Active"] = ""

            }

        }
        return out
    }

    /**
     *
     * @param toConvert
     * @return
     */
    private def convertToSelectBoxValues(Collection<StringRepresentationInterface> toConvert) {
        def out = []
        toConvert.each {
            out.add(convertToSelectBoxValue((StringRepresentationInterface) it))
        }
        return out

    }

    /**
     *
     * @param toConvert
     * @return
     */
    private Map convertToSelectBoxValue(StringRepresentationInterface toConvert) {
        return ["id": infereID(toConvert), "text": toConvert.toContextShortIndividualizedString()]
    }

    /**
     *
     * @param d
     * @return
     */
    private String infereID(d) {
        if (!d)
            return ""

        String classname = d.getClass().simpleName
        if (classname.contains("\$\$"))
            classname = classname.substring(0, classname.indexOf("\$\$") - 1)

        return d.id + "_" + classname

    }

    List<Dataset> possibleDatasetsSelection(Sample samp, String className) {
        List<Dataset> out = []
        User user = springSecurityService.getCurrentUser()


        def crit = Dataset.createCriteria()
        def res = crit {
            and {
                samples {
                    idEq(samp.id)
                }
                or {
                    eq("owner", user)
                    and {

                        eq("annon", true)

                        eq("shared", true)

                    }
                }
            }
        }

        GrailsDomainClass appClass = null
        if (className != null) {
            if (!className.startsWith("Data"))
                className = "Data" + className

            appClass = grailsApplication.getDomainClasses().find {
                it.packageName.equals("data") && it.fullName.endsWith(className)
            }
        }
        if (appClass) {

            res.each {
                it ->
                    def tempo = appClass.findByDataset(it)
                    if (tempo)
                        out.add(it)
            }
        } else {

            out.addAll(res)

        }

        return out

    }

    Map dataSelection(Study stud, Patient pat, Sample samp, Dataset data, String className) {
        def out = possibleSamplesSelection(stud, pat, samp, data)

        if (samp && !data) {
            List<Dataset> aaa = possibleDatasetsSelection(samp, className)

            if (aaa.size() == 0) {

                out["Datasets"] = []

            } else {

                out["Datasets"] = convertToSelectBoxValues(aaa)

            }


        } else if (data) {

            out["Datasets"] = convertToSelectBoxValues([data])

        }
        out
    }
    /**
     * Get Information from the Studies Things Accumulated
     * @param study The Study the Context should be Analyzed and processed to Statistics
     * @return A Key Value Map of Maps Construct to store the Statistics
     */
    def StudyStats(Study study) {
        def out = [:]

        def patients = study.getPatients()
        out["numPatients"] = patients.size()
        out["numSamples"] = 0
        out["cancerTypes"] = [:]
        //Number of Datasets for a Study
        def dsets = Dataset.executeQuery(
                ' select  distinct  d  from Dataset d join d.samples s join s.patient p join p.study st where st = :stu',
                [stu: study])



        out["numDatasets"] = dsets.size()
        out["numDatasetTypes"] = [:]
        dsets.each { Dataset d ->
            String type = datasetService.getDataTypeOfDataset(d)

            if (out["numDatasetTypes"][type] == null)
                out["numDatasetTypes"][type] = 1
            else
                out["numDatasetTypes"][type] = out["numDatasetTypes"][type] + 1


        }



        patients.each { patient ->
            def cancerType = ""
            patient.samples.each {
                sample ->
                    out["numSamples"] = out["numSamples"] + 1
                    if (sample.cancerType)
                        cancerType = sample.cancerType

            }
            if (out["cancerTypes"][cancerType] == null)
                out["cancerTypes"][cancerType] = 1
            else
                out["cancerTypes"][cancerType] = out["cancerTypes"][cancerType] + 1

        }
        return out

    }

}

