package de.cancersysdb.Import

import de.cancersysdb.GeneService
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.geneticStandards.GeneOntologyInfo
import grails.plugins.rest.client.RestBuilder
import grails.validation.ValidationException
import org.codehaus.groovy.grails.commons.GrailsApplication

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import java.util.concurrent.TimeUnit

//TODO Split Into Two Services, The One for Import and Management, the Second For lookup etc.
/**
 * This Service Imports the Gene Standards
 */

class GeneImportService {
    def sessionFactory
    boolean transactional = false
    GrailsApplication grailsApplication
    def runtimeDataSourceService
    GeneService geneService
    def brokerMessagingTemplate
    boolean LockRefresh = false
    //The Following Fields are Important for the Biomart Queries
    def MainIdentifier = "ensembl_gene_id"
    //Gene Core Fields
    def Corefields = [

            "entrezgene",
            "chromosome_name",
            "start_position",
            "end_position",
            "strand",
            "external_gene_name"

    ]

    //RefSeqIdentifiers
    def ref = [
            "entrezgene",
            "refseq_mrna",
            "ccds",
            "ensembl_transcript_id"
    ]
    //Gene Ontology Information
    def geneOntology = [

            "go_id",
            "name_1006"

    ]
    /**
     * CSV Importer for the Gene Table
     * @param stream
     */

    void importGeneTablefromCSV(stream) {

        def error = false
        def valid = true
        def lastError = false

        stream.toCsvReader(['charset': 'UTF-8', 'separatorChar': ',', skipLines: 1]).eachLine { tokens ->
            if (tokens.length != 6) {
                error = true
                return true
            } else {
                //symbol: tokens[4],
                def gene = new Gene(startPos: tokens[1], endPos: tokens[2], chromosome: tokens[3], strand: tokens[5].toString().contains("-") ? "-" : "+")
                gene.setEnsemblID(tokens[0])
                valid = valid && gene.validate()
                if (!valid) {
                    lastError = gene.errors
                    return true
                }
                gene.save()
            }

            return lastError
        }

    }

/**
 * reads in following infos : ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz
 * This is also saved in the Data directory
 * @param stream The Resource for the CSV File
 */
    private void importGeneTablefromGeneInfoCSV(stream, String messageSock) {

        def lastError = false
        Map<String, Gene> reses = GeneFastLookupMap()

        int count = 0
        int lastcount = 0
        //Important 2 = Symbol , 4 = Synonyms, 5 = dbXrefs
        stream.toCsvReader(['charset': 'UTF-8', 'separatorChar': "\t", skipLines: 1]).eachLine { tokens ->

            def symbol = tokens[2]
            List synonyms
            if (!"-".equals(tokens[4]))
                synonyms = tokens[4].toString().tokenize("|")
            else synonyms = []
            def aliastemp = []
            if (!"-".equals(tokens[5]))
                aliastemp = tokens[5].toString().tokenize("|")
            else aliastemp = []

            Map alias = [:]
            def Ensembl = ""
            aliastemp.each {
                it ->
                    int cutpoint = it.toString().indexOf(":")
                    if (cutpoint > 0) {
                        def key = it.toString().substring(0, cutpoint)
                        def id = it.toString().substring(cutpoint + 1)
                        if ("Ensembl".equals(key))
                            Ensembl = id
                        else
                            alias.put(key, id)
                    }
            }
            if (!"".equals(Ensembl)) {

                Gene gene = reses.get(Ensembl)
                if (gene) {
                    gene.setName(symbol)
                    try {
                        gene.save(failOnError: true)
                        count = count + 1
                        if (count % 1000 == 0 && count != lastcount) {
                            message messageSock, "alias" + count
                            lastcount = count
                        }
                    } catch (ValidationException e) {
                        LockRefresh = false
                        message messageSock, "Problems with Saving alias " + symbol + " : " + gene.getEnsemblID()

                    }
                    synonyms.each {

                        gene.setName(it)
                        try {
                            count = count + 1
                            if (count % 10000 == 0) {
                                gene.save(flush: true, failOnError: true)
                                log.debug("Flushing Database")
                            } else
                                gene.save(failOnError: true)

                            if (count % 1000 == 0 && count != lastcount) {
                                message messageSock, "alias" + count
                                lastcount = count
                            }
                        } catch (ValidationException e) {
                            LockRefresh = false
                            message messageSock, "Problems with Saving alias " + it + " : " + gene.getEnsemblID()


                        }


                    }

                    alias.each {
                        key, id ->
                            gene.setName(id)
                            try {
                                gene.save(failOnError: true)
                                count = count + 1
                                if (count % 1000 == 0 && count != lastcount) {
                                    message messageSock, "alias" + count
                                    lastcount = count
                                }
                            } catch (ValidationException e) {
                                LockRefresh = false
                                message messageSock, "Problems with Saving alias " + id + " : " + gene.getEnsemblID()
                            }
                    }
                }
            }
        }

    }

    /**
     * This Function Requests Biomark and Updates the Gene Table in the DAtabase
     * @param messageSock The Socket ID for the Messages
     * @param reses Optional Map of GeneIdentifiers to Genes.
     * @return True if Successful
     */

    void UpdateGeneTable(String messageSock) {
        Map<String, Gene> reses = new HashMap<String, Gene>()
        String resp
        int lastcount = 0
        int count = 0
        String emblbuild
        if (LockRefresh ) {
            message messageSock, "The import is allready running!"
            return
        }
        LockRefresh = true
        List temp = QueryBiomartGenes(messageSock)
        resp = temp[0]
        emblbuild = temp[1] ?: "--"

        //If The Response is very Small The Static local Version is Used!
        if ("".equals(resp) || resp == null) {

            def path = grailsApplication.config.cancersys.config.dataFilepath + "marttemp.txt"
            File f = new File(path);
            if (!f.exists()) {
                throw new FileNotFoundException("File not Found in the Data stuff of App please check if everthing is there or the File Referencing!")


                LockRefresh = false

                return
            }
            resp = f.getText("utf-8")


            log.debug(resp)

/*
        0 -> Ensembl Gene ID
        1 -> Ensembl Transcript ID
        2 -> CCDS ID
        3 -> EntrezGene ID
        4 -> Chromosome Name
        5 -> Gene Start (bp)
        6 -> Gene End (bp)
        7 -> Strand
        8 -> RefSeq mRNA
        9 -> Associated Gene Name
        */
            def OldEndsemble = ""
            def OldEntrez = ""
            Gene transcript = null
            Gene actuGene = null
            count = 0
            Gene.withTransaction {
                resp.toCsvReader(['charset': 'UTF-8', 'separatorChar': '\t', skipLines: 1]).eachLine { tokens ->

                    if (tokens.size() == 10) {
                        if (OldEndsemble == "" || OldEndsemble != tokens[0]) {

                            actuGene = new Gene()
                            if (tokens[0] != "")
                                actuGene.setEnsemblID(tokens[0])
                            if (tokens[3] != "")
                                actuGene.setNcbiID(tokens[3])
                            if (tokens[4] != "")
                                actuGene.setChromosome(tokens[4])
                            if (tokens[5] != "")
                                actuGene.setStartPos(Long.parseLong(tokens[5]))
                            if (tokens[6] != "")
                                actuGene.setEndPos(Long.parseLong(tokens[6]))
                            if (tokens[7] != "")
                                actuGene.setStrand((tokens[7] as String)?.charAt(0) ?: "+")
                            if (tokens[9] != "")
                                actuGene.setName(tokens[9])
                            actuGene.dataVersion = 0
                            if (!actuGene.save()) {

                                message(messageSock, "couldnt Save Gene " + actuGene.toString())
                                actuGene.discard()
                            } else {
                                assert actuGene.isGene()
                                OldEndsemble = tokens[0]
                                if (actuGene.name)
                                    reses.put(tokens[0], actuGene)
                                count = count + 1

                                if (count % 1000 == 0) {
                                    message messageSock, "Genes " + count
                                }
                                if (count % 20000 == 0) {
                                    actuGene.save(flush: true)
                                    def session = sessionFactory.currentSession


                                }
                            }
                        }
                    }
                }
                actuGene.save(flush: true)

                def session = sessionFactory.currentSession
                session.flush()
                message messageSock, "Final Flushing Cache "
            }



            Gene.withTransaction {

                count = 0
                resp.toCsvReader(['charset': 'UTF-8', 'separatorChar': '\t', skipLines: 1]).eachLine { tokens ->
                    if (tokens.size() == 10) {

                        def tempGene = null
                        if (actuGene == null || (!actuGene.getEnsemblID().equals(tokens[0]) || actuGene.getEnsemblID() == null) || (!actuGene.getNcbiID().equals(tokens[1]) || actuGene.getNcbiID() == null))
                            if (actuGene.getEnsemblID() == null) {
                                tempGene = reses[tokens[1]]

                            } else {
                                tempGene = reses[tokens[0]]
                            }
                        else
                            tempGene = actuGene
                        //Create Transcript
                        transcript = new Gene()
                        if (tokens[1] != "")
                            transcript.setEnsemblID(tokens[1])
                        if (tokens[8] != "")
                            transcript.setNcbiID(tokens[8])
                        if (tokens[4] != "")
                            transcript.setChromosome(tokens[4])
                        if (tokens[5] != "")
                            transcript.setStartPos(Long.parseLong(tokens[5]))
                        if (tokens[6] != "")
                            transcript.setEndPos(Long.parseLong(tokens[6]))
                        if (tokens[7] != "")
                            transcript.setStrand((tokens[7] as String)?.charAt(0) ?: "+")

                        transcript.dataVersion = 0
                        transcript.setTranscriptOf(actuGene)

                        if (!transcript.save()) {

                            message(messageSock, "couldnt Save Transcript " + actuGene.toString())

                        }
                        assert transcript.isTranscript()

                        count = count + 1
                        if (count % 20000 == 0) {
                            transcript.save(flush: true)
                            def session = sessionFactory.currentSession
                            session.flush()
                            message messageSock, "Flushing Cache"
                        }
                        if (count % 1000 == 0) {
                            message messageSock, "Transscripts " + count
                        }
                    }

                }
                transcript.save(flush: true)

                def session = sessionFactory.currentSession
                session.flush()
                message messageSock, "Final Flushing Cache "
            }

        } else {
            //  LIFE REQUEST BIOMART!

            /*
            0 -> Ensembl Gene ID
            1 -> EntrezGene ID
            2 -> Chromosome Name
            3 -> Gene Start (bp)
            4 -> Gene End (bp)
            5 -> Strand
            6 -> Associated Gene Name
            */

            def OldEndsemble = ""
            def OldEntrez = ""
            Gene transcript = null
            Gene actuGene = null
            count = 0
            Gene.withTransaction {
                resp.toCsvReader(['charset': 'UTF-8', 'separatorChar': '\t', skipLines: 1]).eachLine { tokens ->

                    if (tokens.size() > 6) {

                        //if (OldEndsemble == "" || OldEndsemble != tokens[0]) {

                        actuGene = new Gene()
                        if (tokens[0] != "")
                            actuGene.setEnsemblID(tokens[0])
                        if (tokens[1] != "")
                            actuGene.setNcbiID(tokens[1])
                        if (tokens[2] != "")
                            actuGene.setChromosome(tokens[2])
                        if (tokens[3] != "")
                            actuGene.setStartPos(Long.parseLong(tokens[3]))
                        if (tokens[4] != "")
                            actuGene.setEndPos(Long.parseLong(tokens[4]))
                        if (tokens[5] != "")
                            actuGene.setStrand((tokens[5] as String)?.charAt(0) ?: "+")
                        if (tokens[6] != "")
                            actuGene.setName(tokens[6])
                        actuGene.dataVersion = 0
                        if (!actuGene.save()) {

                            message(messageSock, "couldnt Save Gene " + actuGene.toString())
                            actuGene.discard()
                        }
                        assert actuGene.isGene()
                        OldEndsemble = tokens[0]
                        if (actuGene.name)
                            reses.put(actuGene.getIdentifier(), actuGene)
                        count = count + 1
                        if (count % 20000 == 0) {
                            actuGene.save(flush: true)
                            def session = sessionFactory.currentSession

                            session.flush()
                            message messageSock, "Flushing Cache "
                        }
                        //}
                    }
                }
                actuGene.save(flush: true)

                def session = sessionFactory.currentSession
                session.flush()
                message messageSock, "Final Flushing Cache "
            }
            // GET THE Transcript Information!
            temp = QueryBiomartTransscripts(messageSock, emblbuild, temp[2])
            //RefSeqIdentifiers
            //"ensembl_gene_id",
            //"entrezgene",
            //"refseq_mrna",
            //"ccds",
            //"ensembl_transcript_id"
            resp = temp[0]

            count = 0
            Gene.withTransaction {
                resp.toCsvReader(['charset': 'UTF-8', 'separatorChar': '\t', skipLines: 1]).eachLine { tokens ->
                    /*
                Ensembl gene:
                "ensembl_gene_id"
                "entrezgene"
                "refseq_mrna"
                "ccds"
                "ensembl_transcript_id"
                */
                    def tempGene = null
                    if (actuGene == null || (!actuGene.getEnsemblID().equals(tokens[0]) || actuGene.getEnsemblID() == null) || (!actuGene.getNcbiID().equals(tokens[1]) || actuGene.getNcbiID() == null))
                        if (actuGene.getEnsemblID() == null) {
                            tempGene = reses[tokens[1]]

                        } else {
                            tempGene = reses[tokens[0]]
                        }
                    else
                        tempGene = actuGene
                    if (tempGene) {
                        assert actuGene != null
                        assert tempGene != null
                        //Create Transcript
                        transcript = new Gene()
                        //Set first to Ensure its a Transcript
                        transcript.setTranscriptOf(tempGene)

                        assert !transcript.isGene()
                        assert transcript.isTranscript()

                        if (tokens[4] != "")
                            transcript.setEnsemblID(tokens[4])
                        if (tokens[2] != "" && tokens[2] != tokens[1])
                            transcript.setNcbiID(tokens[2])

                        transcript.setChromosome(tempGene.getChromosome())
                        transcript.setStartPos(tempGene.getStartPos())
                        transcript.setEndPos(tempGene.getEndPos())
                        transcript.setStrand(tempGene.getStrand())
                        transcript.dataVersion = 0



                        if (!transcript.save()) {
                            message(messageSock, "couldnt Save Transcript " + transcript.toString())
                            message(messageSock, transcript.errors.toString())
                        }
                        assert transcript.isTranscript()

                        count = count + 1
                        if (count % 20000 == 0) {
                            transcript.save(flush: true)
                            def session = sessionFactory.currentSession
                            session.flush()
                            message messageSock, "Flushin Transscripts "
                        }
                        if (count % 1000 == 0) {

                            message messageSock, "Transscripts " + count
                        }
                        actuGene = tempGene
                    }


                }
                transcript.save(flush: true)
                def session = sessionFactory.currentSession
                session.flush()
                message messageSock, "Final Flushin Transscripts "
            }


        }

        message(messageSock, "Finished Saving Genes")
        message messageSock, "Finished all Imports!"
        LockRefresh = false
        true
    }

    private List QueryBiomartGenes(String messageSock) {
        def rest = new RestBuilder()
        //Query Biomart
        def resp
        def Source = "biomartCentral"
        try {
            resp = rest.get("http://central.biomart.org/martservice?type=registry")
            if (resp.status != 200) {
                resp = rest.get("http://www.ensembl.org/biomart/martservice?type=registry")
                Source = "biomartEnsembl"
            }
        } catch (Exception e) {
            LockRefresh = false
            message(messageSock, "Cant Connect!")
            throw e
            return []
        }
        if (resp.status != 200) {


            LockRefresh = false
            message(messageSock, "Cant Connect to Biomart or Ensembl!")

            return []


        }


        def inputStream
        def builder
        def records
        def xpath
        try {
            inputStream = new ByteArrayInputStream(resp.text.bytes)
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            records = builder.parse(inputStream).documentElement
            xpath = XPathFactory.newInstance().newXPath()
        } catch (Exception e) {
            LockRefresh = false

            message(messageSock, resp.text)
            throw e

        }
        def longstring
        try {
            longstring = xpath.evaluate('//MartRegistry/MartURLLocation[contains(@database,"ensembl_mart")]/@database', records, XPathConstants.STRING)
        } catch (Exception e) {
            LockRefresh = false

            message(messageSock, "The Structure/Format of the Biomart results seems to have changed or there has been an error while Loading and Parsing the File in Genereal")
            throw e

        }

        def temp2 = longstring =~ /ensembl_mart_([0-9]+)/




        String emblbuild = temp2[0][1]

        message messageSock, "Latest Version at Biomart: " + emblbuild.toString()
        def g = Gene.findByDataVersion(emblbuild)

        //message messageSock, g.toString()

        //If the Data for this Emblid is Allready in the Database
        if (g) {
            message messageSock, "Embl up to Date!"
            LockRefresh = false
            return ["", emblbuild]
        }

        def path = grailsApplication.config.cancersys.config.dataFilepath + "tempGenes" + emblbuild + ".cvs"
        log.debug("Using Path for Temporary Files : " + path)
        def file = new File(path)
        def lastmodified = new Date(file.lastModified())
        def now = new Date()

        long diff = now.getTime() - lastmodified.getTime();
        def days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (days < 10) {//file.exists()){
            //Read Cache
            message messageSock, "Tempfile newer than 10 Days using Tempfile for New Download delete " + path
            return [file.text, emblbuild]
        } else {

            //Build Biomart XML Query.

            //Parse the standard Results of the API


            def Query = BuildqueryfromFields(MainIdentifier, Corefields)
            try {
                if (Source == "biomartCentral")
                    resp = rest.get("http://central.biomart.org/martservice/results?query=" + Query + "\n")
                if (Source == "biomartEnsembl")
                    resp = rest.get("http://www.ensembl.org/biomart/martservice?query=" + Query + "\n")
            } catch (Exception e) {
                LockRefresh = false

                message(messageSock, "The Structure/Format of the Biomart results seems to have changed or there has been an error while Loading and Parsing the File in Genereal")
                throw e

            }
            log.debug("http://www.ensembl.org/biomart/martservice?query=" + Query)
            //Cache Result
            def text = resp.text
            file.createNewFile()
            file.write(text)
            [text, emblbuild, Source]
        }
    }

    private List QueryBiomartTransscripts(String messageSock, String emblbuild, String Source) {
        def rest = new RestBuilder()
        def resp
        def path = grailsApplication.config.cancersys.config.dataFilepath + "tempTransscripts" + emblbuild + ".cvs"
        def file = new File(path)
        def lastmodified = new Date(file.lastModified())
        def now = new Date()

        long diff = now.getTime() - lastmodified.getTime();
        def days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (days < 10) {
            return [file.text]
        }

        def Query = BuildqueryfromFields(MainIdentifier, ref)
        try {
            if (Source == "biomartCentral")
                resp = rest.get("http://central.biomart.org/martservice/results?query=" + Query + "\n")
            if (Source == "biomartEnsembl")
                resp = rest.get("http://www.ensembl.org/biomart/martservice?query=" + Query + "\n")
        } catch (Exception e) {
            LockRefresh = false

            message(messageSock, "The Structure/Format of the Biomart results seems to have changed or there has been an error while Loading and Parsing the File in Genereal")
            throw e
            return

        }
        //Cache Result

        log.debug("http://www.ensembl.org/biomart/martservice?query=" + Query + "\n")
        if (!resp)
            return

        def text = resp.text
        file.createNewFile()
        file.write(text)

        [text]
    }


    Map<String, Gene> GeneFastLookupMap() {

        def allGenes = Gene.findAllByTranscriptOf(null)
        Map<String, Gene> out = new HashMap<String, Gene>(allGenes.size())
        allGenes.each { it ->
            out.put(it.getEnsemblID(), it)

        }
        return out
    }

    /**
     * Import The GeneOntologyDatabase from Biomart
     * @param messageSock The socket ID for the messages
     * @param reses optional map of GeneIdentifiers to Genes.
     * @return True if successful
     */
    boolean GeneOntologyImport(String messageSock, HashMap<String, Gene> reses = null) {
        if (LockRefresh) {
            message messageSock, "The import is allready running!"
            return
        }


        message(messageSock,"Statring retrival of Gene Ontology")
        LockRefresh = true
        def Query, resp

        int count = 0
        int EmptyCount = 0
        int NonEmptyCount = 0

        int NotSaveableCount = 0
        int SaveableCount = 0

        def rest = new RestBuilder()

        //Loading The Genes into Memory to Fast add the Gene Ontology Files
        if (reses == null || reses.isEmpty()) {
            reses = GeneFastLookupMap()
        }

        def path = grailsApplication.config.cancersys.config.dataFilepath + "tempGOS.cvs"
        def text = ""
        ClearGeneOntologyInfo(messageSock)
        def file = new File(path)
        if (file.exists()) {
            //Read Cache
            message messageSock, "using Tempfile if for New Download delete " + path
            text = file.text
        } else {


            message(messageSock, "QueryBiomart")
            Query = BuildqueryfromFields(MainIdentifier, geneOntology)
            try {

                resp = rest.get("http://central.biomart.org/martservice/results?query=" + Query + "\n")
                if (resp.status != 200) {
                    log.debug("Ensembl")
                    resp = rest.get("http://www.ensembl.org/biomart/martservice?query=" + Query + "\n")
                }
            } catch (Exception e) {

                LockRefresh = false
                message(messageSock, "Cant query Biomart!")
                throw e
            }

            def gos = new ArrayList<GeneOntologyInfo>(reses.size())
            message messageSock, "Building Data"
            text = resp.text
            file.createNewFile()
            file.write(text)

        }
        GeneOntologyInfo go = null
        GeneOntologyInfo.withTransaction {
            text.toCsvReader(['charset': 'UTF-8', 'separatorChar': '\t', skipLines: 1]).eachLine { tokens ->

                if (tokens.size() < 3 || tokens[0] == null || tokens[1] == null || tokens[2] == null || !tokens[0] || !tokens[1] || !tokens[2]) {
                    EmptyCount++

                } else {
                    NonEmptyCount++


                    Integer len = tokens[2].size()
                    if (len > 255)
                        tokens[2] = tokens[2].substring(0, 254)
                    def tmpGene = reses.get(tokens[0])
                    if (!tmpGene) {
                        tmpGene = geneService.getGeneByIdentifier(tokens[0])

                    }

                    if (tmpGene) {


                        go = new GeneOntologyInfo(goID: tokens[1], info: tokens[2], gene: tmpGene)
                        try {
                            if (count % 10000 == 0) {
                                go.save(flush: true)
                                log.debug("Flushing Session")
                                def session = sessionFactory.currentSession

                                session.flush()
                                session.clear()
                                //propertyInstanceMap.get().clear()
                            } else
                                go.save()

                        } catch (Exception e) {
                            NotSaveableCount++
                            if (NotSaveableCount > 10000) {
                                LockRefresh = false
                                throw e
                            }
                        }
                        SaveableCount++
                        count = count + 1

                        if (NonEmptyCount % 1000 == 0) {
                            message messageSock, "Gos" + NonEmptyCount


                        }
                    } else
                        message messageSock, "Error finding Gene for Import : " + tokens[0]
                }


            }
            go.save(flush: true)
            log.debug("Flushing Session")
            def session = sessionFactory.currentSession

            session.flush()
            session.clear()

        }
        message messageSock, "Import Finished"
        message messageSock, "Saved " + SaveableCount
        message messageSock, "Not Saved " + NotSaveableCount
        message messageSock, "Importet datasets : " + SaveableCount
        LockRefresh = false
        true

    }

    /**
     * Performance Glitch see:
     * http://naleid.com/blog/2009/10/01/batch-import-performance-with-grails-and-mysql
     * @return
     */
/*    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }*/

    private void ClearGeneOntologyInfo(String messageSock) {
        if (GeneOntologyInfo.count() > 0) {
            message messageSock, "Cleaning old Gene Ontology Infos"
            GeneOntologyInfo.executeUpdate("delete GeneOntologyInfo b ")
        }
    }

    /**
     * Type Message
     * @param messageSock
     * @param message
     */
    void message(String messageSock, String message) {

        brokerMessagingTemplate.convertAndSend messageSock,   message
        log.info(  message)
    }

    /**
     * Build Query to Biomart
     * @param MainIdentifier The Main Identifier, Usualy the EnsemblId
     * @param fields a list of the Fields from the Biomart Database that should be Retrived
     * @return Returns a Query XML object as String
     */
    private String BuildqueryfromFields(String MainIdentifier, List fields) {
        String queryFields = "\"<Attribute name=\"" + MainIdentifier + "\"/>\""


        for (field in fields) {

            queryFields += "<Attribute name=\"" + field + "\"/>"

        }

        def QueryOptions = "<Query client=\"true\" processor=\"TSV\" limit=\"-1\" header=\"1\">"

        def Query = "<!DOCTYPE Query>" +
                QueryOptions +
                "<Dataset name=\"hsapiens_gene_ensembl\" config=\"gene_ensembl_config\">" +
                queryFields +
                "</Dataset>" +
                "</Query>"

        return Query

    }


}
