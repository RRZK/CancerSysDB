package de.cancersysdb.data

import de.cancersysdb.Dataset
import de.cancersysdb.geneticStandards.Gene

/**
 * This Table Modells Gene Interactions It makes an Graph Edge Between Genes.
 */

class DataGeneInteraction implements DataEntry  {

    Gene source
    Gene target
    Dataset dataset
    //static belongsTo = [type:DataGeneInteractionGroups]
    static mapping = {
        dataset cascade: "delete"

        version false
    }
}
