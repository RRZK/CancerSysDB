package de.cancersysdb.geneticStandards

import de.cancersysdb.GeneticHelpers.GenPosInterface
import de.cancersysdb.data.DataGeneticFunction

/**
 * This is a Simple gene or Gene Transcript.
 * The design of the Table was inspired by other Projects like Quick NGS.
 */
//This represents a Gene as a Standardised Object
// It heps to Manage diffrend Identifiers etc.
class Gene implements GenPosInterface {


    static transients = ["geneticType", "ensemblID", "ncbiID"]


    /**
     * Entrez Genes or Refseq Transcripts
     */
    Long ensembl = null
    Long ncbi = null
    /**
     * String Description for The Gene
     */
    String name = null
    //Genetic Position
    Long startPos
    Long endPos
    String chromosome
    Character strand
    //EMbl Data Version
    List<DataGeneticFunction> functions
    Integer dataVersion

    String description
    /**
     * Is a Trancscript of a Gene
     */
    Boolean transcriptionOfGene = false
    /**
     * The Gene this Gene is a Transcription of
     */
    Gene transcriptOf

    /**
     * NCBI infered Transcript -> If True its an "NX_" prefix NCBI Transcript else its an "NM_" Gene Transcript
     */
    Boolean inferedNCBITranscript = false

    //Type Request
    /**
     * Is it a Transcriot or gene
     * @return String  if transcript if its a Transcript else it will return gene
     */
    String getGeneticType() { if (transcriptionOfGene) return "transcript" else return "gene" }
    /**
     * Is it a Transcriot
     * @return true if its a Transcript
     */
    boolean isTranscript() { if (transcriptionOfGene) return true else return false }
    /**
     * Is it a Gene
     * @return true if its a Gene
     */
    boolean isGene() { if (transcriptionOfGene) return false else return true }

    /**
     * Get the Identifier of the Gene. Its Ensembl or NCBI
     * @return The Identifier of the Gene
     */
    String getIdentifier() {

        if (ensembl)
            return this.getEnsemblID()
        else
            return this.getNcbiID()

    }

    String getEnsemblID() {
        if (!ensembl)
            return null
        if (isGene()) {
            return "ENSG" + String.format("%011d", ensembl);
        } else {
            return "ENST" + String.format("%011d", ensembl);

        }
    }


    boolean setEnsemblID(def ensemblindent) {

        if (ensemblindent in String) {
            if (ensemblindent.startsWith("ENSG")) {
                if (transcriptionOfGene)
                    return false
                ensembl = Long.parseLong(ensemblindent.substring(4))

            } else if (ensemblindent.startsWith("ENST")) {
                if ((transcriptionOfGene && ncbi == null) || transcriptionOfGene)
                    transcriptionOfGene = true
                else
                    return false
                ensembl = Long.parseLong(ensemblindent.substring(4))
            }


        } else if (ensemblindent in Integer)
            ensembl = ensemblindent
        else
            return false


    }

    String getNcbiID() {
        if (!ncbi)
            return null
        if (isGene()) {
            return "" + ncbi;
        } else {
            if (inferedNCBITranscript)
                return "XM_" + ncbi;
            else
                return "NM_" + ncbi;
        }

    }

    boolean setNcbiID(def ncbiident) {

        if (ncbiident in String) {
            if (ncbiident.startsWith("NM_")) {
                if ((transcriptionOfGene && ensembl == null) || transcriptionOfGene)
                    transcriptionOfGene = true
                else
                    return false
                ncbi = Long.parseLong(ncbiident.substring(3))
                inferedNCBITranscript = false

            } else if (ncbiident.startsWith("NX_")) {
                if ((transcriptionOfGene && ensembl == null) || transcriptionOfGene)
                    transcriptionOfGene = true
                else
                    return false
                ncbi = Long.parseLong(ncbiident.substring(3))
                inferedNCBITranscript = true
            } else if (ncbiident.isNumber()) {
                if (transcriptionOfGene)
                    return false
                ncbi = Long.parseLong(ncbiident)

            }

        } else if (ncbiident in Integer && !transcriptionOfGene)
            ncbi = ncbiident

        return true

    }

    void setTranscriptOf(Gene instance) {
        if (instance != null)
            this.transcriptionOfGene = true
        transcriptOf = instance

    }
    static hasMany = [dataVersions: String, functions:DataGeneticFunction]

    static constraints = {
        description nullable: true
        name nullable: true
        ensembl nullable: true, validator: { val, obj -> if (val == null && obj.ncbi == null) ["AtLeastOneIdentifierMustBeNotNull", val] }
        ncbi nullable: true
        ensemblID bindable: true
        ncbiID bindable: true

        transcriptOf nullable: true
        endPos validator: { val, obj -> if (val < obj.startPos) ["StartBeforeEnd", val] }
        transcriptionOfGene nullable: false, validator: { val, obj -> if (val && obj.transcriptOf == null) ["TranscriptOrGeneIsContradictive", val] }
    }
    static mapping = {
        chromosome index: 'chrom_Index'
        startPos index: 'endPos_Index'
        endPos index: 'startPos_Index'
        ensembl index: 'EMBL_Index'
        ncbi index: 'NCBI_Index'
        name index: 'Gene_Name_Index'
    }


    static def marshallers = ["standard": { Gene g ->


        // The Fields that Should not be Exportet
        def AntiFields = ["functions"]
        // only Marshall the NOT-specified fields that aren't null
        def map = [:]

        g.domainClass.getPersistentProperties().findAll {
            k ->
                !(k.getName() in AntiFields)
        }.each {
            k ->
                def name = k.getName()
                map.put(name, g."$name")
        }

        map.id = g.id
        map.class = g.getClass().name
        return map
    }


    ]

}
