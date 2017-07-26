package de.cancersysdb.Import

import de.cancersysdb.ImportTools.CSVPreanalyser
import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.Dataset
import de.cancersysdb.GeneService
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.transaction.Transactional

import java.util.regex.Pattern

/**
 * This is a general CSV Importer, working on CSV Files and similiar Standards
 */
@Transactional
class StructuredCSVToDataImporterService {
    GeneService geneService
    static Pattern RefalleleValidationPattern = ~'^[ACGTtcgaN]+$'
    static Pattern AltalleleValidationPattern = ~'^[ACGTtcgaN.,]+$'
    static Pattern ChromosomeValidationPattern = ~'^[0-9XY]{1,2}$'
    static Pattern PositionValidationPattern = ~'^[0-9]+$'
    static Map AlternativeFieldNames = ["start": "startPos", "end": "endPos", "end_pos": "endPos", "start_pos": "startPos", "chr": "chromosome", "chrom": "chromosome", "aAChange": "aaChange", "rpkm": "fpkm"]
    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
    /**
     * Import CSVFile from geneticStandard
     * @param text This is String or a Stream with the CSV Content to Import
     * @param ds Dataset Object which this Data is Appended to
     * @param geneticDataClass The Class name of the Genetic Dataset to Import
     * @param csvOptions Give Options of the CSV File as Map Willbe Given to the CSV Preanalyzer to minimize Workload
     * @return The Errorresponse
     */
    def importCSVToGeneticStandard(text, Dataset ds, String geneticDataClass, ImportProtocol ip, Map csvOptions =[:]) {

        //////PRE ANALYSIS PHASE

        CSVPreanalyser csvpre = new CSVPreanalyser()
        //Preanalyse the CSV File
        csvpre.Preanaylse(text)

        print csvpre.print()
        //Parse Headings
        List cols = csvpre.Fields
        //FAIL IF NO HEADINGS SUBMITTED!
        if (cols.empty) {
            ip.Message("Headings of Import File not as Expected!")
            ip.successful = false
            log.error("Headings of Import File not as Expected!")
            return ip
        }

        //Refactor the Columns Lowercase first AND replace Sysnonyms
        for (int i = 0; i < cols.size(); i++) {
            if (cols[i] == null || cols[i].length() < 2)
                continue
            def fname = cols[i][0].toLowerCase() + cols[i].substring(1)

            if (fname in AlternativeFieldNames.keySet()) {
                ip.Message("Automatic reqcognized fieldname: " + cols[i] + " Name in input: " + AlternativeFieldNames.get(fname))
                cols[i] = AlternativeFieldNames.get(fname)
            } else
                cols[i] = fname

        }



        def klass = ds.datasetService.getDataClassForName(geneticDataClass)
        if (klass == null) {
            ip.Message("Could not find Class for name: '" + geneticDataClass + "'")
            ip.successful = false
            ip.ImportEnd()
            return ip
        }

        List<String> req = ds.datasetService.getRequiredFieldNamesForClass(klass)

        if (!req || req.empty) {
            ip.Message("Not All required Fields could be Found\n")
            ip.successful = false
            ip.ImportEnd()
            return ip

        }

        if (req != null) {
            boolean allrequiredThere = true
            //Dataset allready set
            List<String> testcols = ["dataset"]
            testcols.addAll(cols)

            req.each {
                it ->

                    if (!(it in testcols)) {
                        ip.Message("Required Field " + it + " could not be found!\n")
                        allrequiredThere = false
                    }
            }
            if (!allrequiredThere) {
                ip.Message("Not All required Fields could be Identified, use costum Mapping Imports\n")
                ip.successful = false
                ip.ImportEnd()
                return ip

            }


        }

        ip.Message("Import to Class " + klass + "  " + geneticDataClass)

        //Check if there is an Annotation class for the Imported Data
        def annoklass = ds.datasetService.getAnnotationClassFor(klass)
        List<String> AnnoFields = []
        def Linkfield
        //Check if there are enough fields to create Annotations
        if (annoklass) {
            boolean allrequiredThere = true
            List<String> testcols = ["dataset"]
            testcols.addAll(cols)

            Linkfield = ds.datasetService.getFieldLinkFieldForAnnotation(klass, annoklass)
            List AnnoReqFields = ds.datasetService.getRequiredFieldNamesForClass(annoklass)
            testcols.add(Linkfield)
            AnnoReqFields.each {
                it ->
                    if (!(it in testcols)) {
                        allrequiredThere = false
                    }
            }
            //Fincal check if all required it there for the Annotationclass
            if (!allrequiredThere) {
                annoklass = null
            } else {
                AnnoFields = ds.datasetService.getFieldNamesForClass(annoklass)
                ip.Message("Importing Annotations")
            }

        }
        List Last = [cols.size()]

        def LastEntity
        int countdatasets=0
        int countAnnotations =0
        int countFaileddatasets=0
        int countFailedAnnotations =0
        long index = 0
        try{
            text.toCsvReader(['charset': 'UTF-8', 'separatorChar':csvpre.getSeperator(),'skipLines':1,"quoteChar":csvpre.enclose]).eachLine {
                tokens ->

                    Map initMap = [:]
                    Map annoInitMap = [:]
                    boolean isLikeLast = true
                    //Missmatching number of cols
                    if (cols.size() != tokens.size()) {
/*                        print "is -> " +tokens
                        print "should be -> "+cols*/
                        throw new Exception("Headings of Files do not match the Fields!")
                    }

                    for (int i = 0; i < cols.size(); i++) {
                        def valueToInsert = tokens[i]
                        String fieldname = cols[i]
                        //Ignore Suggestions for IDs
                        if (fieldname.equals("id"))
                            continue

                        if (annoklass && fieldname in AnnoFields) {

                            if(annoklass.getDeclaredField(fieldname).getType().equals(Gene)){
                                long Genenow = System.currentTimeMillis();
                                Gene gene = geneService.getGeneByIdentifier(valueToInsert)
                                //log.debug("FetchGene"+ (System.currentTimeMillis() - Genenow) + " ms")
                                if(gene)
                                    valueToInsert = gene
                                else {
                                    geneService.saveNonfoundGeneName(valueToInsert, ds)
                                }

                            }

                            annoInitMap.put(fieldname, valueToInsert)

                        } else {
                            //Special Treatment for Genes

                            if (klass.getDeclaredField(fieldname).getType().equals(Gene)) {

                                Gene gene = geneService.getGeneByIdentifier(valueToInsert)

                                if (gene)
                                    valueToInsert = gene
                                else {
                                    geneService.saveNonfoundGeneName(valueToInsert, ds)
                                }

                            }
                            //Map the Columns directly to the Fileds(Works for Synchrnous Databases)
                            initMap.put(fieldname, valueToInsert)
                            if (annoklass && !Last[i].equals(valueToInsert))
                                isLikeLast = false
                            Last[i] = valueToInsert
                        }
                    }
                    initMap.put("dataset", ds)

                    def annoclazzInstance
                    def clazzInstance


                    if (annoklass && isLikeLast)
                        clazzInstance = LastEntity
                    else {


                        clazzInstance = klass.newInstance(initMap)

                        if (clazzInstance.hasErrors() || !clazzInstance.save()) {
                            ip.ImportedFailed(clazzInstance)
                            clazzInstance.discard()
                            countFaileddatasets++

                        } else {
                            ip.ImportedSuccessful(clazzInstance)
                            countdatasets++
                        }


                    }
                    if (annoklass) {
                        annoInitMap.put(Linkfield, clazzInstance)
                        annoclazzInstance = annoklass.newInstance(annoInitMap)
                        if (annoclazzInstance.hasErrors() || !annoclazzInstance.save()) {
                            ip.ImportedFailed(annoclazzInstance)
                            annoclazzInstance.discard()
                            countFailedAnnotations++
                        } else {
                            ip.ImportedSuccessful(annoclazzInstance)
                            countAnnotations++
                        }


                    }
                    LastEntity = clazzInstance
                    index +=1
                    if (index % 200 == 0) cleanUpGorm()
            }
            ip.successful = true
            ip.setAutoreport(false)
            if (countAnnotations != 0 && countFailedAnnotations != 0) {
                ip.Message("Import Completed Datasets: " + countdatasets + " Annotations: " + countAnnotations)
                if (countFailedAnnotations || countFaileddatasets)
                    ip.Message("Import Failed Datasets: " + countFaileddatasets + " Annotations: " + countFailedAnnotations)
                else
                    ip.Message("No Failed Imports")

            } else {
                ip.Message("Import Completed Datasets: " + countdatasets)
                if (countFaileddatasets)
                    ip.Message("Import Failed Datasets: " + countFaileddatasets)
                else
                    ip.Message("No Failed Imports")
            }
            def tempmakro = geneService.uniqueCountNonfoundGeneNamesForDataset(ds)
            if (tempmakro)
                ip.Message("Number of Geneidentifiers not Found: " + tempmakro)


        } catch (e) {
            ip.Message("Failed while reading CSV")
            ip.setAutoreport(true)
            ip.Message("Error Message: " + e.message)
            ip.successful = false

        }
        if(ip.isSuccessful()){
            def persistedImportProtocol = new PersistedImportProtocol(ip)
            persistedImportProtocol.save()
        }
        ip.ImportEnd()

        return ip


    }


    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
