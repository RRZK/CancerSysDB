package de.cancersysdb.geneticStandards
/**
 * A Simple Gene Ontology informationfragment. It links Information of the Function of a Gene to the Gene
 */
class GeneOntologyInfo {

    /**
     * Gene Ontology ID
     */
    String goID

    /**
     * The String Based Information of Gene Ontology
     */
    String info
    /**
     *  The Gene Object this regeferences to
     */
    Gene gene
    static belongsTo = [gene: Gene]
    static constraints = {
        goID unique: 'gene'
    }
}
