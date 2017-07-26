package de.cancersysdb

import de.cancersysdb.GeneticHelpers.GeneticPosition
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.geneticStandards.GeneSymbolAlias
import de.cancersysdb.serviceClasses.NotFoundGeneName
import grails.transaction.Transactional

/**
 * Service Class wich offers diffrend functions for Genes
 */
class GeneService {
    Map symboltoGene = [:]

    /**
     * Takes identifier and tries to resolve the best Gene-Datatype
     * @param Identifier an Identifier for the Gene
     * @return The Gene Instance of the Searched gene
     */
    Gene getGeneByIdentifier(String Identifier, boolean NoRedirects = false) {
        //This is the Case from the TCGA Files where the Symbol is at Start and the Its The Somatic Annotations
        if (Identifier.contains("|"))
            Identifier = Identifier.substring(0, Identifier.lastIndexOf("|"))
        Gene gene
        //Ensembl IDs
        if (Identifier.startsWith("ENS") || Identifier.startsWith("Ens") || Identifier.startsWith("ens")) {
            Long temp = this.extractEnsemblLongVal(Identifier)
            //long Genenow = System.currentTimeMillis();

            gene = Gene.findByEnsembl(temp)
            //log.debug("GeneRetrival ENSEMB"+ (System.currentTimeMillis() - Genenow) + " ms")

            if (gene)
                return gene
        }

        if (Identifier.startsWith("NM_") || Identifier.startsWith("XM_")) {
            //long Genenow = System.currentTimeMillis();

            gene = Gene.findByNcbi(extractNCBILongVal(Identifier))
            //log.debug("GeneRetrival NCBI"+ (System.currentTimeMillis() - Genenow) + " ms")

            if (gene)
                return gene
        }

        if (Identifier =~ /^[0-9]+$/) {
            Long temp = Long.parseLong(Identifier)
            //long Genenow = System.currentTimeMillis();

            gene = Gene.findByNcbi(temp)
            //log.debug("GeneRetrival NCBI"+ (System.currentTimeMillis() - Genenow) + " ms")

        }
        if (gene)
            return gene
        //long Genenow = System.currentTimeMillis();

        gene = Gene.findByName(Identifier)

        //log.debug("GeneRetrival Name"+ (System.currentTimeMillis() - Genenow) + " ms")
        //print Identifier
        if (gene)
            return gene

        //Gene GeneSymbolAlias
        if(!NoRedirects){
            gene = GeneSymbolAlias.findByAlias(Identifier)?.getGene()

            //log.debug("GeneRetrival Name"+ (System.currentTimeMillis() - Genenow) + " ms")
            //print Identifier
            if (gene)
                return gene
        }
        return null
    }

    Long extractEnsemblLongVal(String Identifier) {
        Long temp

        try {
            if (Identifier.startsWith("ENS") || Identifier.startsWith("Ens") || Identifier.startsWith("ens")) {

                temp = Long.parseLong(Identifier.substring(4).trim())
            } else
                temp = Long.parseLong(Identifier.trim())
            //print Identifier + " -> " + Identifier.substring(4)
        } catch (NumberFormatException e) {
            return null
        }
        return temp
    }

    Long extractNCBILongVal(String Identifier) {
        Long temp
        try {
            if (Identifier.startsWith("NM_") || Identifier.startsWith("XM_")) {
                temp = Long.parseLong(Identifier.substring(3).trim())
            } else
                temp = Long.parseLong(Identifier.trim())

        } catch (NumberFormatException e) {
            return null
        }
        return temp
    }

    Gene getGeneByIdentifierAndType(String Identifier, String Type) {
        List temp
        switch (Type) {
            case "ncbi":
                temp = Gene.findAllByNcbiAndTranscriptionOfGene(this.extractNCBILongVal(Identifier), false)
                break;
            case "symbol":
                temp = Gene.findAllByNameAndTranscriptionOfGene(Identifier, false)
                break;
            case "ensembl":
                temp = Gene.findAllByEnsemblAndTranscriptionOfGene(this.extractEnsemblLongVal(Identifier), false)
                break;
        }

        if (temp.size() >= 1)
            return temp.get(0)
        else
            log.debug("ERROR " + temp.size())
        return null
    }

    /**
     * Gets all Genes in a Position Span
     *
     * @param GeneticPosition where all the stuff should be collected for
     * @return Gene Instances on this Span of Positions
     */
    List<Gene> getGenesByGeneticPosition(GeneticPosition gp) {

        //Gene.findAllByChromosomeAndStartPos  gStartPosAndEndPos(gp.chromosome,gp.startPos,gp.endPos,[ sort: "startPos", order: "asc"])
        def results = Gene.withCriteria {

            eq("chromosome", gp.chromosome)
            not {
                or {
                    lt("endPos", gp.startPos,)
                    gt("startPos", gp.endPos)
                }
            }


            and {
                order("chromosome", "asc")
                order("startPos", "asc")
                order("endPos", "asc")
            }
        }
        return results
    }

    /**
     * This function Saves Gene Identifiers to the Database wich could where not found in th Basic Dataset
     * @param genename The Genename which could not be identified
     * @param ds The Dataset into wich the Gene should have been entered
     */
    @Transactional
    void saveNonfoundGeneName(String genename, Dataset ds) {

        NotFoundGeneName nfg = new NotFoundGeneName(searchedGeneName: genename, ds: ds)
        //nfg.save()
       if (!nfg.save())
            print nfg.getErrors()

    }

    /**
     * Return the number of Unique Gene Identifiers wich could not be Identified in the Import Process of an Dataset
     * @param ds The Dataset for which to get the number of unquie Identifiers wich could no be understood
     * @return number of Unique Gene Identifiers wich could not be Identified
     */
    Integer uniqueCountNonfoundGeneNamesForDataset(Dataset ds) {

        def result = NotFoundGeneName.withCriteria {
            eq("ds", ds)
            projections {
                countDistinct("searchedGeneName")
            }
        }
        result.first()

    }

    /**
     * Return a List of Unique Gene Identifiers wich could not be Identified in the Import Process of an Dataset
     * @param ds The Dataset for which to get the List of unique Identifiers which could no be understood
     * @return List of Unique Gene Identifiers which could not be Identified
     */
    List<String> uniqueNonfoundGeneNamesForDataset(Dataset ds) {

        def result = NotFoundGeneName.withCriteria {
            eq("ds", ds)
            projections {
                distinct("searchedGeneName")
            }
        }
        result

    }

}