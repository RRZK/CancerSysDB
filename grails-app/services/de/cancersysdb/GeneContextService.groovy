package de.cancersysdb

import de.cancersysdb.contextHandling.GeneralDisplayContext
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.geneticStandards.GeneOntologyInfo
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

/**
 * This Services Structures and Decorates Data so that it contains Additional Information for the Frontend
 */

@Transactional
class GeneContextService {
    LinkGenerator grailsLinkGenerator

    /**
     * Creates Context for a Gene wich data is in the Database
     * @param gene The Gene that should be contextualized
     * @return A Map of Context Information
     */
    def getHumanReadableContextforGene(Gene gene) {

        def out = [:]

        List<GeneralDisplayContext> Listgo = new ArrayList<GeneralDisplayContext>()
        def geneOntologyStuff = getGeneOntologyContext(gene)
        Map<String, String> temp = new HashMap<String, String>()

        geneOntologyStuff.each { it ->

            def godesc = temp.get(it.goID)
            if (godesc)
                godesc = godesc + ", " + it.info
            else
                godesc = it.info

            temp.put(it.goID, godesc)


        }

        for (goid in temp.keySet()) {
            GeneralDisplayContext context = new GeneralDisplayContext()
            context.setDescription(temp.get(goid))
            context.setIdentifier(goid)
            context.setURL(geneOntologytoLink(goid))
            context.setSourceDescriptor("Gene Ontology")
            Listgo.add(context)
        }
        if (!Listgo.isEmpty())
            out["Gene Ontology"] = Listgo

        def transcripts = Gene.findAllByTranscriptOf(gene)
        List<GeneralDisplayContext> listofTranscripts = new ArrayList<GeneralDisplayContext>()
        transcripts.each { it ->
            GeneralDisplayContext context = new GeneralDisplayContext()
            context.setDescription(it.getIdentifier())
            context.setIdentifier(it.getIdentifier())
            context.setURL(grailsLinkGenerator.link(controller: 'gene', action: 'show', id: it.id, absolute: true))
            context.setSourceDescriptor("Transcript")
            listofTranscripts.add(context)

        }
        if (!listofTranscripts.isEmpty())
            out["known transcripts"] = listofTranscripts
        return out

    }

    /**
     * transform GO Term into a Link wich can be derefered
     * @param GOTerm The GOTerm
     * @return String containing a link to The entry in the Gene Ontology Database
     */
    def geneOntologytoLink(String GOTerm) {

        return "http://www.ebi.ac.uk/QuickGO/GTerm?id=" + GOTerm


    }

    /**
     * Get all Geneontology datasets for gene
     * @param gene The Gene the GO Terms should be retrived for
     * @return List of GO terms for the given gene
     */
    def getGeneOntologyContext(Gene gene) {
        return GeneOntologyInfo.findAllByGene(gene)

    }

}
