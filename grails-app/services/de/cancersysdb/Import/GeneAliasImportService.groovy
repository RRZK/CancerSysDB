package de.cancersysdb.Import

import de.cancersysdb.GeneService
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.geneticStandards.GeneSymbolAlias
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication

@Transactional
class GeneAliasImportService {
    GeneService geneService
    GrailsApplication grailsApplication
    /**
     * Pareses Gene Alias CSV With two fields Gene;Aliaa
     * @param GeneAliasCSV is a String or an Object which can by Itrated by the CSV Iteration tool If Null the Local File wille be used
     * @return
     */
    def ImportGeneAlias(def GeneAliasCSV = null) {

        //Load Lokal Table
        if (GeneAliasCSV == null) {
            log.debug("No Parameter given using Apps own stuff")
            def GeneAliasInfoPath = grailsApplication.parentContext.getResource("").file.toString() + "/data/BasisData/geneAlias.csv"
            File f = new File(GeneAliasInfoPath)
            if (f.exists())
                GeneAliasCSV = f.getText("utf-8")
            else
                throw new FileNotFoundException("Internal Data Error /data/BasisData/geneAlias.csv not part of this webapp")

        }
        //Counting Stuff
        def lines = 0
        def done = 0
        def successful = 0
        def faultyAliases= 0
        def FailedToCreate= 0
        //AList Collection Doublicated Aliases which will be Removed at the End
        List DoublicateAliases=[]

        //Remove Everything

        GeneSymbolAlias.findAll().each { it.delete(flush: true, failOnError: true) }

        GeneAliasCSV.toCsvReader(['charset': 'UTF-8', 'separatorChar': ';', skipLines: 1]).eachLine { tokens ->

            //We Try to Alias a single Entity in the Gene Table.
            lines++
            def genes = Gene.findAllByName(tokens[0])
            boolean thereisAGeneInTheResults = false
            if(genes.size() > 1)
                thereisAGeneInTheResults= genes.any {it.isGene()}
            genes.each { Gene gene ->
                done++
                //This Should Fail. If an suggested Alias allready Attributes a Gene!
                def AliasFalseCheck = Gene.findAllByName(tokens[1])
                // Check if Anything is in there And if Its just one Result OR the Result is a Gene and not one of its Transcripts!
                if (AliasFalseCheck == null || AliasFalseCheck.empty && (genes.size() == 1 || (genes.size() > 1 && !gene.isTranscript() && thereisAGeneInTheResults ))) {

                    def gsa = new GeneSymbolAlias(gene: gene, alias: tokens[1])
                    gsa.save()

                    if (gsa.hasErrors()){
                        log.error("Gene Alias " + tokens[1] + " produced Errors: " + gsa.errors)
                        gsa.discard()
                        //There is probaply only one Reason for Errors: The Unique Constraint on the Alias String. So Every Alias is Collected and Late Attached to the Stuff
                        DoublicateAliases.add(tokens[1])
                        FailedToCreate++
                        //TODO if Alias in Non Unquie Delete the allready Existing Aliases in the Database
                    }else
                        successful++
                //Handle The Other cases and produce an Error if there are Errors
                } else if (genes.size() > 1 && !gene.isTranscript() && thereisAGeneInTheResults)
                    log.debug("Skipping Transcriprts in Favor of Gene")
                else {
                    AliasFalseCheck.each { it ->
                        //Explainable Failures
                        if (it.transcriptOf.equals(gene))
                            log.debug("Transcript Alias found " + it.name + "transcript Of " + gene.name)
                        else if (gene.transcriptOf.equals(it))
                            log.debug("Transcript Alias found " + gene.name + "transcript Of " + it.name)
                        //Unexplainable Failures
                        else{
                            log.error("Gene Alias " + tokens[1] + " Should name " + gene.name + " " + gene.ncbiID + " " + gene.ensemblID + " but allready attributes Gene: " + it.ncbiID + " " + it.ensemblID)
                            faultyAliases++
                        }
                    }
                }
            }
            if (genes.empty)
                log.error("Could not Find Gene with Name :" + tokens[0])
        }

        successful = successful - DoublicateAliases.size()
        FailedToCreate = FailedToCreate + DoublicateAliases.size()
        //Delete Aliases Used multiple times
        DoublicateAliases.each {String it->
            GeneSymbolAlias toDelete
            GeneSymbolAlias.findByAlias(it)
            if(toDelete)
                toDelete.delete()
        }
        log.debug("Lines Processed "+lines)
        log.debug("Successful Importet Genealiases: " + successful + " of potentally " + done)
        log.debug(" Unexplainable error on "+faultyAliases)
        log.debug("Failed to Create "+FailedToCreate+ "Propably Doubliate non Unique Aliases")


    }

    String printGeneAliasListAsCSVCandidate(){
        StringBuilder preOut = new StringBuilder()

        preOut.append("AliasName;GeneName;GeneNCBI;GeneEnsembl\n")
        GeneSymbolAlias.getAll().each { GeneSymbolAlias it ->
            preOut.append(it.alias+";"+it.gene.name+";"+it.gene.ncbiID+";"+it.gene.ensemblID +"\n" )

        }

        return preOut.toString()


    }


}
