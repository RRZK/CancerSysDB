package de.cancersysdb.TestData

import de.cancersysdb.Dataset
import de.cancersysdb.Patient
import de.cancersysdb.Sample
import de.cancersysdb.Study
import de.cancersysdb.User
import de.cancersysdb.data.DataTranscriptAbundance
import de.cancersysdb.data.DataTranscriptDiffExpr
import de.cancersysdb.data.DataPeak
import de.cancersysdb.data.DataPeakAnnotation
import de.cancersysdb.data.DataVariation
import de.cancersysdb.data.DataVariationAnnotation
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.geneticStandards.TCGAClassObject
import de.cancersysdb.workflow.ConceptualWorkflow
import de.cancersysdb.workflow.ProcessedWorkflow
import grails.transaction.Transactional
import org.apache.commons.lang.RandomStringUtils

@Transactional
class TestdataCreatorService {

    private Random rand
    private GeneMax = 0
    private GeneMin = 0
    String messageSock
    def brokerMessagingTemplate
    static List featureType = ["3PRIME_UTR", "5PRIME_UTR", "AMBIGUOUS", "INTRON"]

    def TestdataCreatorService() {
        rand = new Random()


    }

    private boolean GeneScan() {
        GeneMax = Gene.list(sort: "id", order: "desc", max: 1).get(0).getId()
        log.debug("GeneMax" + GeneMax)
        GeneMin = Gene.list(sort: "id", order: "asc", max: 1).get(0).getId()
        log.debug("GeneMin" + GeneMin)
        true
    }

    /**
     * This Function Generates some Testdatasets, Samples, Sample Groups and Genetic Data.
     * @param uid The ID of User that should own the Data with the Right to edit it
     * @param msock This is the Websocket to report to. If Null nothing will be sent to the Socket
     * @param annon The Flag if the Data is anonymous. Non anonymous Data will only be shown to logged in Users
     * @param shared If Data is not shared its only Visible to the Author
     * @return
     */
    String CreateTestdata(long uid, String mSock, boolean annon, boolean shared) {


        this.messageSock = mSock
        try {

            User Owner = User.get(uid)

            message "Starting Testadata Production"

            if (!Owner) {
                message "There are no User Infos, How did you come so far"
                return
            }

            basicWorkflows()

            message("creating Study")
            TCGAClassObject cancer1 = RandomCancer()

            crateStudy(cancer1, Owner, shared, annon, 5 + rand.nextInt(10))

            message("Study Finished")
            message("Testdata Created")
        } catch (Error e) {

            message e.toString()

        }

    }

    /**
     * Returns Random Cancer from The Cancer Value List
     * @return
     */
    private TCGAClassObject RandomCancer() {
        int numberofCancer = TCGAClassObject.datatypes.indexOf("cancer")
        def temp = TCGAClassObject.findAllByType(numberofCancer)
        Integer randCancerindex = rand.nextInt(temp.size())

        temp.get(randCancerindex)

    }

    /**
     * Returns Random Location from The Location Value List
     * @return
     */
    private TCGAClassObject RandomLocation() {
        int numberofTissue = TCGAClassObject.datatypes.indexOf("tissueType")
        def temp = TCGAClassObject.findAllByType(numberofTissue)
        Integer randLocationindex = rand.nextInt(temp.size())

        temp.get(randLocationindex)

    }

    private void basicWorkflows() {
        ConceptualWorkflow temp

        temp = ConceptualWorkflow.findByType("WGS")
        message "creating Workflows"
        ConceptualWorkflow WGS = null
        if (!temp) {
            WGS = new ConceptualWorkflow(type: ConceptualWorkflow.Types[0], plainDescription: "WGS")
            WGS.save(failOnError: true)
        }


        temp = ConceptualWorkflow.findByType("RNA")
        ConceptualWorkflow RNA = null
        if (!temp) {
            RNA = new ConceptualWorkflow(type: ConceptualWorkflow.Types[1], plainDescription: "RNA")
            RNA.save(failOnError: true)
        }



        temp = ConceptualWorkflow.findByType("ChIP")
        ConceptualWorkflow ChIP = null
        if (!temp) {
            ChIP = new ConceptualWorkflow(type: ConceptualWorkflow.Types[2], plainDescription: "ChIP")
            ChIP.save(failOnError: true)
        }
    }

    /**
     * Creates a Dataset for a Sample
     * @param Samples The Samples
     * @param Owner The new Owner of the Dataset
     * @param concepts
     * @param shared
     * @param annon
     */
    void createDataset(Collection<Sample> Samples, User Owner, Collection<ConceptualWorkflow> concepts, boolean shared, boolean annon) {

        concepts.each { concept ->
            Dataset ds = new Dataset(samples: Samples, annon: annon, shared: shared, fileName: "  " +
                    "asdas", originURL: "" +
                    "asd", owner: Owner)

            ProcessedWorkflow process = new ProcessedWorkflow(executor: Owner, concept: concept, ds: ds, description: "TEstdata")

            message " creating Datasets"
            ds.save(failOnError: true)
            process.save(failOnError: true)



            if (concept.type.equals("WGS")) {
                message("createVariation")
                (0..100).each {
                    createVariation(ds, Owner, annon, shared)
                }
            } else if (concept.type.equals("RNA")) {
                message("createDiffExpr")

                (0..100).each {
                    createDiffExpr(ds, Owner, annon, shared)
                }

                message("createAbundance")

                (0..100).each {
                    createAbundance(ds, Owner, annon, shared)
                }
                message("createAbundance")
                (0..100).each {
                    createVariation(ds, Owner, annon, shared)
                }
            } else if (concept.type.equals("ChIP")) {
                message("createVariation")
                (0..100).each {
                    createVariation(ds, Owner, annon, shared)
                }

            }

            process.save(failOnError: true)

        }
    }


    String message(String message) {
        log.debug(message)
        if (this.messageSock)
            brokerMessagingTemplate.convertAndSend messageSock, message
        message

    }

    Study crateStudy(TCGAClassObject ctype, User Owner, boolean shared, boolean annon, int Patients) {

        Study stu = new Study()
        int number = this.rand.nextInt()
        String ID = "MockStudy" + number
        List<ConceptualWorkflow> Allworks = []

        Allworks = ConceptualWorkflow.getAll()

        Set<ConceptualWorkflow> Workflows = new HashSet<ConceptualWorkflow>()
        int numworkflows = rand.nextInt(3)
        if (numworkflows == 0)
            numworkflows = 1
        for (int i = 0; i < numworkflows; i++)
            Workflows << Allworks.get(rand.nextInt(numworkflows))



        rand.nextInt()
        stu.setuRI("http://Mock.Study.com/Study" + number)
        stu.setReferenceIdentifier(ID)
        stu.setDescription("This is Study " + ID)
        message "Marker"
        for (int i = 0; i < Patients; i++) {
            message "patient " + i
            def pat = createPatient(ctype, Workflows, Owner, shared, annon)

            stu.addToPatients(pat)
        }
        stu.save(failOnError: true, flush: true)


    }

    /**
     * Create a Patient Dataset
     * @param ctype
     * @param Workflows
     * @param Owner
     * @param shared
     * @param annon
     * @return
     */
    Patient createPatient(TCGAClassObject ctype, Set<ConceptualWorkflow> Workflows, User Owner, boolean shared, boolean annon) {

        //this.rand.nextInt(Sample.cancerTypes.size())
        def location = RandomLocation()
        String filename = ""
        if (!ctype)
            ctype = Sample.cancerTypes[this.rand.nextInt(Sample.cancerTypes.size())]

        filename = this.createRandomString(5)

        Sample Samp1 = new Sample(

                batch: this.createRandomString(5),
                pairEnds: 1,
                label: filename + "_normal",
                tissueType: TCGAClassObject.findByName("Solid Tissue Normal"),
                cancerType: ctype,
                location: location,
                annon: annon,
                shared: shared,
                owner: Owner)

        Samp1.save(failOnError: true)
        //Sick
        Sample Samp2 = new Sample(
                batch: this.createRandomString(5),
                pairEnds: 1,
                label: filename + "can",
                tissueType: TCGAClassObject.findByName("Primary solid Tumor"),
                cancerType: ctype,
                location: location,
                annon: annon,
                shared: shared,
                owner: Owner)

        Sample Samp3 = new Sample(
                batch: this.createRandomString(5),
                pairEnds: 1,
                label: filename + "can",
                tissueType: TCGAClassObject.findByName("Recurrent Solid Tumor"),
                cancerType: ctype,
                location: location,
                annon: annon,
                shared: shared,
                owner: Owner)


        Samp3.save(failOnError: true)
        Patient sampset = new Patient()

        Set a = [Samp1, Samp2, Samp3]




        sampset.setSamples(a)
        sampset.save(failOnError: true)

        createDataset(a, Owner, Workflows, shared, annon)

        return sampset

    }

    DataVariation createVariation(Dataset ds, User u, boolean annon, boolean shared) {

        //Werte für die Wertausprägungen sind gut gewählt
        Map pos = createRandomPosition()




        DataVariation var = new DataVariation(chromosome: pos["chromosome"],
                startPos: pos["start"],
                endPos: pos["end"],
                refAllele: "TODO",
                altAllele: "TODO",
                totalDepth: 0,
                refDepth: 0,
                altDepth: 0,
                genotype: "TODO",
                qualityScore: 255,
                dataset: ds)

        var.save(failOnError: true)
        def temp = createVariationAnnotation(var, pos["gene"])
        return var


    }


    DataVariationAnnotation createVariationAnnotation(DataVariation gv, Gene gene) {


        def temp = new DataVariationAnnotation(
                variation: gv,
                effect: "TODO",
                functionalClass: "Todo",
                aaChange: "TODO",
                bioType: "TODO",
                gene: gene)
        temp.save(failOnError: true)

        return temp

    }


    DataTranscriptDiffExpr createDiffExpr(Dataset ds, User u, boolean annon, boolean shared) {

        //Werte für die Wertausprägungen sind gut gewählt

        float temp = rand.nextFloat() * (800000.0 - 0.0) + 0.0

        float FoldChange = rand.nextFloat() * 3.0
        float temp2 = temp * FoldChange
        Gene gene = getRandomGene()
        DataTranscriptDiffExpr diff = new DataTranscriptDiffExpr(controlMean: temp, sampleMean: temp * temp2, pValue: this.createRandomPValue(), dataset: ds, foldChange: FoldChange, Gene: gene)

        diff.save(failOnError: true)
        return diff


    }

    Gene getRandomGene() {
        if (GeneMin == 0 && GeneMax == 0 || GeneMax - GeneMin <= 0)
            GeneScan()
        Gene out = null

        while (!out) {


            def diff = GeneMax - GeneMin

            def temp = GeneMin + rand.nextInt(diff.intValue())

            if (temp == 0)
                temp = 1

            out = Gene.get(temp)
        }

        return out

    }

    DataTranscriptAbundance createAbundance(Dataset ds, User u, boolean annon, boolean shared) {

        //Werte für die Wertausprägungen sind gut gewählt
        Map pos = createRandomPosition()

        String Strand = "NA"
        DataTranscriptAbundance Abbu = new DataTranscriptAbundance(chromosome: pos["chromosome"], startPos: pos["start"], endPos: pos["end"], strand: Strand, fpkm: createRandomFPKM(), fpkmOK: true, dataset: ds, gene: pos["gene"])

        Abbu.save(failOnError: true)
        return Abbu


    }


    DataPeak createPeak(Dataset ds, User u, boolean annon, boolean shared) {

        //Werte für die Wertausprägungen sind gut gewählt

        Map pos = createRandomPosition()

        DataPeak peak = new DataPeak(chromosome: pos["chromosome"], startPos: pos["start"], endPos: pos["end"], foldEnrichment: createRandomFoldEnrich(), pValue: createRandomPValue(), dataset: ds)

        peak.save(failOnError: true)
        def temp = createPeakAnnotation(peak, pos["gene"])
        return peak


    }

    DataPeakAnnotation createPeakAnnotation(DataPeak gp, Gene gene) {

        Integer ftypeint = rand.nextInt(featureType.size())
        String ftype = featureType[ftypeint]
        def temp = new DataPeakAnnotation(peak: gp, featureType: ftype, gene: gene)
        temp.save(failOnError: true)
        return temp

    }

    String createRandomChromosome() {

        Integer Chrom = rand.nextInt(23) + 1

        if (Chrom == 23)
            return "X"
        if (Chrom == 24) {
            if (rand.nextBoolean())
                return "Y"
            else
                return "X"
        }
        return Chrom.toString()
    }

    Map createRandomPosition() {

        return createRandomPosition(createRandomChromosome())

    }

    String createRandomFolder(int depth) {
        String concat = ""
        for (int i = 0; i < depth; i++) {
            concat << createRandomString(i + 2) + "/"
        }
        return concat
    }

    String createRandomString(int len) {


        return RandomStringUtils.randomAlphabetic(len)
    }

    Map createRandomPosition(String chromosome) {
        Map out = [:]
        Gene gen = getRandomGene()

        Integer thing = gen.endPos - gen.startPos


        Integer length = rand.nextInt(thing)
        if (length == 0)
            length = 1
        Integer start = rand.nextInt(thing - length)



        out["chromosome"] = chromosome
        out["start"] = start
        out["length"] = length
        out["end"] = start + length
        out["gene"] = gen

        if (start >= (start + length)) {
            log.debug("error" + out)
        }

        return out
    }


    float createRandomFoldEnrich() {

        rand.nextFloat() * (78600.0 - 1.0) + 1.0

    }

    float createRandomFPKM() {

        rand.nextFloat() * (46000000.0 - 0.0) + 0.0

    }

    float createRandomPValue() {

        rand.nextFloat() * (1.0 - 0.0) + 0.0

    }

}
