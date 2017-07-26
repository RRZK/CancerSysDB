package de.cancersysdb.Importer

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.ImportTools.ImporterServiceInterface
import de.cancersysdb.Dataset
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import de.cancersysdb.data.DataVariation
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.transaction.Transactional
import grails.validation.ValidationException

@Transactional
/**
 * This Service is an ImporterService and it manages the Import of VCF Files
 */
class VcfImporterService implements ImporterServiceInterface {


    static Map<String, List> MapsTo = ["vcf": ["Variation"]]
    static Map<String, List> FilenamePattern = [:]
    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    ImportProtocol importContent(String format, String Into,
                                 def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds) {
        //If It cant Import this just quit

        if (!(MapsTo.containsKey(format) && MapsTo.get(format).contains(Into))) {
            log.debug("Tutto Kaputto " + format + "  " + Into)
            return null
        }

        if (format.equals("vcf") && Into.equals("Variation"))
            return ImportVCFFileToDataVariation(File, sourceFile.fileName, owner, annon, shared, ip, ds)


    }

    /**
     * Function that Imports VCF Files to Genetic Variation Files.
     * @param File The File Content as String
     * @param Filename The Filename
     * @param owner The User that will be Owner of the Created Data
     * @param annon User Rights
     * @param shared User Rights
     * @return String with Success Message
     */
    ImportProtocol ImportVCFFileToDataVariation(
            def File, String Filename, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds = null) throws ValidationException {

        //Format ClinicalInformation http://faculty.washington.edu/browning/beagle/intro-to-vcf.html
        '''CHROM \tthe chromosome.
            POS \tthe genome coordinate of the first base in the variant. Within a chromosome, VCF records are sorted in order of increasing position.
            ID \ta semicolon-separated list of marker identifiers.
            REF \tthe reference allele expressed as a sequence of one or more A/C/G/T nucleotides (e.g. "A" or "AAC")
            ALT \tthe alternate allele expressed as a sequence of one or more A/C/G/T nucleotides (e.g. "A" or "AAC"). If there is more than one alternate alleles, the field should be a comma-separated list of alternate alleles.
            QUAL \tprobability that the ALT allele is incorrectly specified, expressed on the the phred scale (-10log10(probability)).
            FILTER \tEither "PASS" or a semicolon-separated list of failed quality control filters.
            INFO \tadditional information (no white space, tabs, or semi-colons permitted).
            FORMAT \tcolon-separated list of data subfields reported for each sample. The format fields in the Example are explained below. '''
        '''

        AA ancestral allele
        AC allele count in genotypes, for each ALT allele, in the same order as listed
        AF allele frequency for each ALT allele in the same order as listed: use this when estimated from primary data, not called genotypes
        AN total number of alleles in called genotypes
        BQ RMS base quality at this position
        CIGAR cigar string describing how to align an alternate allele to the reference allele
        DB dbSNP membership
        DP combined depth across samples, e.g. DP=154
        END end position of the variant described in this record (esp. for CNVs)
        H2 membership in hapmap2
        MQ RMS mapping quality, e.g. MQ=52
        MQ0 Number of MAPQ == 0 reads covering this record
        NS Number of samples with data
        SB strand bias at this position
        SOMATIC indicates that the record is a somatic mutation, for cancer genomics
        VALIDATED validated by follow-up experiment


        '''

        org.springframework.validation.Errors Elist
        String errors = "";
        int count = 0
        if (ds == null)
            ds = new Dataset(fileName: Filename, owner: owner, annon: annon, shared: shared)
        Map meta = this.VCFStripMeta(File);

        ds.save(flush: true, failOnError: true)
        ip.setDataset(ds)
        int medlung = 10000
        long timedeltas = 0

        Map indices = [:]
        Map subindices = [:]
        //preAnalysis To Map Fields to The Attribute in the VCF File
        if (meta.containsKey("Testline")) {
            List tokens = meta["Testline"].split('\t')

            def preanalysis = tokens[9].split(":")

            def things = tokens[8].split(":")
            for (int i = 0; i < things.length; i++) {
                switch (things[i]) {
                    case "GT":

                        indices["genotype"] = i
                        break
                    case "AD":
                        if (preanalysis[i].contains(",")) {
                            indices["RefDepth"] = i
                            indices["AltDepth"] = i
                            subindices["RefDepth"] = 0
                            subindices["AltDepth"] = 1
                        } else
                            indices["AltDepth"] = i
                        break

                    case "FREQ":
                        indices["Freq"] = i
                        break
                    default:
                        ip.Message("Ignored the Attribue '" + things[i] + "'")
                }

            }
        }

        //print "Startline " + meta["startline"]
        int linienCount = meta["startline"] as Integer
        File.toCsvReader(['charset': 'UTF-8', 'separatorChar': '\t', skipLines: meta["startline"] as Integer]).eachLine { tokens ->
            if (tokens.length < 9)
                ip.Message("Too Little Fields in this Row for VCF File tokens:'" + tokens + "'")

            int NumberofSamples = tokens.length - 9

            Float qScore
            if (tokens[5].isNumber())
                qScore = new Float(tokens[5])
            else
                qScore = new Float(0.0)


            for (int i = 0; i < NumberofSamples; i++) {


                int startpos = new Integer(tokens[1])

                int endpos = startpos + (tokens[3].size() - 1)
                DataVariation temp = new DataVariation(chromosome: tokens[0], startPos: startpos, endPos: endpos, refAllele: tokens[3], altAllele: tokens[4], qualityScore: qScore, dataset: ds)
                //If the Token is Empty then Skip. For Example when there is Whitespace Errors
                if (tokens[i + 9] == "")
                    continue
                def extratokens = tokens[i + 9].split(":")
                String attTemp = ""
                attTemp = VCFgetAttribute("genotype", indices, subindices, extratokens)
                if (attTemp)
                    temp.setGenotype(attTemp)

                try {
                    attTemp = VCFgetAttribute("DepthIndex", indices, subindices, extratokens)
                    if (attTemp)
                        temp.setTotalDepth(new Integer(attTemp))

                    attTemp = VCFgetAttribute("RefDept", indices, subindices, extratokens)
                    if (attTemp)
                        temp.setRefDepth(new Integer(attTemp))

                    attTemp = VCFgetAttribute("AltDepth", indices, subindices, extratokens)
                    if (attTemp)
                        temp.setAltDepth(new Integer(attTemp))

                    attTemp = VCFgetAttribute("Freq", indices, subindices, extratokens)
                    if (attTemp) {

                        if (attTemp.contains("%")) {
                            attTemp = attTemp.replace("%", "")
                            Float tempfloat = new Float(attTemp) / 100.0f
                            temp.setFreq(new Float(tempfloat))
                        } else {
                            temp.setFreq(new Float(attTemp))
                        }

                    }

                } catch (Exception e) {
                    if (e.class.equals(java.lang.NumberFormatException))
                        ip.Message("There was Unexpected Format in one of the Attributes")

                }
                //TODO Add to datamodell

                if (temp.hasErrors()) {

                    Elist = Elist?.addAllErrors(temp.errors) ?: temp.getErrors()

                    //errors = errors+ "\nValidation Failed For Row $linienCount \n"+tokens.toString()+"\n"

                    //if(Elist.errorCount > 10 )
                    //    throw new ValidationException("Import Failed, more than 10 Errors:\n"+errors, Elist )
                    ip.ImportedFailed(temp)
                    continue
                } else {
                    count++

                    temp.save()
                    ip.ImportedSuccessful(temp)

                }


            }
/*            long endtime = System.currentTimeMillis()
            timedeltas+=endtime-starttime*/
            linienCount = linienCount + 1
            if (linienCount % 100 == 0) cleanUpGorm()

/*            if(linienCount % medlung == 0){
                print linienCount+" processed"
                print timedeltas/medlung
                timedeltas=0

            }*/
        }
        ip.successful = true
        ip.ImportEnd()
        def persistedImportProtocol = new PersistedImportProtocol(ip)
        persistedImportProtocol.save()
        return ip
/*        if(!"".equals(errors)) {

           throw new ValidationException("Import Failed:\n"+errors, Elist )

        }

        return "Succsessful imported $count Genetic Variations"*/

    }

    /**
     * Get Attribute Value for described Position in the Metadata.
     * @param key The Field to which the Value Position is Mapped
     * @param indices The Indice of the Attribute Filed
     * @param subindices The Subindice of the Attribute Field
     * @param AttributeTokens The Attribute Tokens
     * @return The String Value of the Attribute if The Attribute is not Mapped Return null
     */
    String VCFgetAttribute(key, indices, subindices, AttributeTokens) {
        if (indices.containsKey(key)) {
            if (subindices.containsKey(key)) {

                return AttributeTokens[indices[key]].split(",").get(subindices(key))
            }

            return AttributeTokens[indices[key]]
        }

        return null
    }

    /**
     * Strip the metadata and of an VCF-File and put it in an associative Array
     * @param file The File to Import
     * @return VCF Map with the Main keys: startline, Healine, Context, Testline
     */
    Map VCFStripMeta(def file) {

        Map out = [:]
        out["startline"] = 0
        out["Healine"] = ""
        out["Context"] = ""
        out["Testline"] = ""
        boolean testlineset = false
        file.eachLine { line, count ->

            if (line.startsWith("##")) {
                out["startline"] = count + 1
                out["Context"] += line

            } else if (line.startsWith("#")) {
                out["Healine"] = line
                out["startline"] = count + 1

            } else if (testlineset)
                return
            else
                out["Testline"] = line


        }

        return out


    }

    /**
     *
     * Performance Glitch see:
     * http://naleid.com/blog/2009/10/01/batch-import-performance-with-grails-and-mysql
     *
     * @return nothing
     */
    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

}
