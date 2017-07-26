package de.cancersysdb.Importer

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.ImportTools.ImporterServiceInterface
import de.cancersysdb.contextHandling.CancersysBarcodeConventions
import de.cancersysdb.Dataset
import de.cancersysdb.DatasetService
import de.cancersysdb.Import.StructuredCSVToDataImporterService
import de.cancersysdb.Sample
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import de.cancersysdb.data.DataVariation
import grails.transaction.Transactional

/**
 * This Service is an ImporterService and it manages the Import of MAF Files
 */
@Transactional
class MafImporterService implements ImporterServiceInterface {

    StructuredCSVToDataImporterService structuredCSVToDataImporterService
    DatasetService datasetService
    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    static Map<String,List> MapsTo = ["maf":["Variation"]]
    static Map<String,List> FilenamePattern=[:]
    ImportProtocol importContent(String format, String Into,
                                 def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds) {
        //If It cant Import this just quit
        log.info("Checking if MAF Importer is right")
        log.error("MAF!!")

        if(!( MapsTo.containsKey(format) && MapsTo.get(format).contains(Into))){
            log.info("NOT USING MAF IMPORTER MAF Importer is right")
            return null

        }

        if(format.equals("maf") && Into.equals("Variation"))
            return ReadIn( File, sourceFile, owner,  annon,  shared,  ip,  ds)


    }

    ImportProtocol ReadIn(def file, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip= null, Dataset ds = null) {
        String note =""
        ip.Message("Start Importing maf "+sourceFile.originalFilename)

        if(ds)
                note =ds.note
            Map mafstaff = mafsplitter( file, ip)

        def keyset = mafstaff.keySet()
        keyset.each { key ->
            def f = mafstaff.get(key)
            StringBuffer txt = new StringBuffer();

            List barcodes = key.split("###")
            //Create Context

            String failing = ""

            Sample samp1 = Sample.findBySourceIdentifier(barcodes[0])
            if (!samp1) {

                failing += "Sample " + barcodes[0] + " Not Found "
            }

            Sample samp2 = Sample.findBySourceIdentifier(barcodes[1])

            if (!samp2) {

                failing += "Sample " + barcodes[1] + " Not Found "

                }
                if(samp1 && samp2){
                    if(!ds)
                        ds =  new Dataset(annon:annon,shared:shared, owner:  owner)
                    ds.fileName = sourceFile.originalFilename
                    ds.note = note
                    ds.addToSamples(samp1)
                    ds.addToSamples(samp2)

                //Todo CheckForDuplicate (ReImport)

                ds.save(failOnError: true)

                f.each { line ->

                    txt.append(line.join(";"))
                    txt.append("\n")

                }
                String text = txt.toString()
                ip.Message("Importing " + samp1.sourceIdentifier + " and " + samp2.sourceIdentifier)
                structuredCSVToDataImporterService.importCSVToGeneticStandard(text, ds, DataVariation.class.simpleName, ip,[Seperator:";",enclose:""])


                    ds =null
                    cleanUpGorm()
                }else
                    ip.Message("Skipped because " +failing)
            }
            return ip
        }

    //See Ticket #1931
    /**
     * Splits a MAF File and sorts it
     * @param file File to import
     * @param ip Import Protocol to use
     * @return
     */
    private def mafsplitter(def file, ImportProtocol ip) {
        Map<String, List<List>> out = [:]
        ip.Message("Splitting MAF Files")

        def checkbarcodepairs = []

        def existingbarcodepairs = []
        List NEWHEader = ["chromosome", "startPos", "endPos", "refAllele", "altAllele", "totalDepth", "refDepth", "altDepth", "totalDepthCtrl", "refDepthCtrl", "altDepthCtrl", "effect", "refSNP", "gene"]


        boolean processedheadline = false

        List inlist
        Map additionalfields = [:]
        //These Fields are in Addition to the standard maf fields.
        Integer Tumor_Ref_Count_field = null
        Integer Tumor_Alt_Count_field = null
        Integer Norm_Ref_Count_field = null
        Integer Norm_Alt_Count_field = null

        def lastkey = null
        boolean lastkeyskip = false
        file.toCsvReader(['charset': 'UTF-8', 'separatorChar': "\t", 'skipLines': 0]).eachLine {
            tokens ->
                def Alllist


                if (processedheadline) {

                    def barcode1 = CancersysBarcodeConventions.BarcodeToSample(tokens[15])
                    def barcode2 = CancersysBarcodeConventions.BarcodeToSample(tokens[16])
                    def key = barcode1 + "###" + barcode2

                    if (!key.equals(lastkey)) {
                        if (!checkbarcodepairs.contains(key)) {
                            if (Sample.findBySourceIdentifier(barcode1) && Sample.findBySourceIdentifier(barcode2))
                                existingbarcodepairs.add(key)
                            checkbarcodepairs.add(key)
                        } else if (existingbarcodepairs.contains(key))
                            lastkeyskip = false
                        else
                            lastkeyskip = true
                        lastkey = key

                    }

                    if (!lastkeyskip) {
                        inlist = []
                        if (out.containsKey(key))
                            Alllist = out.get(key)
                        else {

                            Alllist = []
                            Alllist.add(NEWHEader)
                            out.put(key, inlist)
                        }

                        //TODO Recheck!!!!!!!!!!!
                        //Chromosome
                        inlist.add(tokens[4])
                        inlist.add(tokens[5])
                        inlist.add(tokens[6])
                        inlist.add(tokens[10])
                        inlist.add(tokens[12])



                        if (Tumor_Ref_Count_field && Tumor_Alt_Count_field) {
                            Integer Tumor_Ref_Count = Integer.parseInt(tokens[Tumor_Ref_Count_field].toString())
                            Integer Tumor_Alt_Count = Integer.parseInt(tokens[Tumor_Alt_Count_field].toString())
                            inlist.add(Tumor_Ref_Count + Tumor_Alt_Count)
                            inlist.add(Tumor_Ref_Count)
                            inlist.add(Tumor_Alt_Count)
                        } else {

                            inlist.add("")
                            inlist.add("")
                            inlist.add("")
                        }

                        if (Norm_Ref_Count_field && Norm_Alt_Count_field) {
                            Integer Match_Norm_Ref_Count = Integer.parseInt(tokens[Norm_Ref_Count_field].toString())
                            Integer Match_Norm_Alt_Count = Integer.parseInt(tokens[Norm_Alt_Count_field].toString())
                            inlist.add(Match_Norm_Ref_Count + Match_Norm_Alt_Count)
                            inlist.add(Match_Norm_Ref_Count)
                            inlist.add(Match_Norm_Alt_Count)
                        } else {

                            inlist.add("")
                            inlist.add("")
                            inlist.add("")
                        }

                        inlist.add(tokens[8])
                        inlist.add(tokens[13])
                        def tempsymbol = tokens[0] ?: "?"
                        inlist.add(tempsymbol + "|" + tokens[1])


                        Alllist.add(inlist)
                        out.put(key, Alllist)
                    }
                }
                //process Headline
                if (!processedheadline && !tokens[0].toString().startsWith("#")) {
                    //The First 32 Fields of MAF are defined The later are optional
                    if (tokens.length > 32) {
                        for (int i = 32; i < tokens.length; i++) {
                            additionalfields.put(tokens[i].toString().toLowerCase(), i)

                        }
                    }
                    Tumor_Ref_Count_field = additionalfields.get("tumor_ref_count") ?: additionalfields.get("tumor_ref_reads")
                    Tumor_Alt_Count_field = additionalfields.get("tumor_alt_count") ?: additionalfields.get("tumor_var_count") ?: additionalfields.get("tumors_var_reads") ?: additionalfields.get("tumor_var_reads")
                    Norm_Ref_Count_field = additionalfields.get("norm_ref_count") ?: additionalfields.get("normal_ref_reads")
                    Norm_Alt_Count_field = additionalfields.get("norm_alt_count") ?: additionalfields.get("normal_var_count") ?: additionalfields.get("norm_alt_reads") ?: additionalfields.get("normal_var_reads")

                    if (Tumor_Ref_Count_field)
                        ip.Message("Importing Tumor reads")

                    if (Norm_Ref_Count_field)
                        ip.Message("Importing Normal reads")
                    processedheadline = true

                }

        }


        ip.Message("Number of discarded Sample pairs" + (checkbarcodepairs.size() - existingbarcodepairs.size()))
        ip.Message("Number of Samplepairs to process" + existingbarcodepairs.size())
        return out
    }
    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
