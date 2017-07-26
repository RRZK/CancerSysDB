package de.cancersysdb.data

import de.cancersysdb.Dataset
import de.cancersysdb.geneticStandards.Gene

/**
 * This is a Very basic table to Put Mutated Genes into.
 * Its independent Measurements for storing very Basic data.
 */
class DataMutatedGene implements DataEntry {
    static String[] Headings = ["dataset", "gene" ]

    //QuickNGS Name EnsemblID,Symbol
    Gene gene

    Dataset dataset

    String[] toArrayHeadings() {

        return Headings

    }

    String[] toArray() {
        return [ dataset.id, gene ? gene.getEnsemblID().toString() : " "]
    }

    static belongsTo = [dataset: Dataset]

    static constraints = {
    }
    static mapping = {
        dataset cascade: "delete"
        version false
    }



}
