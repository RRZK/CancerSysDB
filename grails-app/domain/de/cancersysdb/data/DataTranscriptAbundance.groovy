package de.cancersysdb.data

import de.cancersysdb.Dataset
import de.cancersysdb.geneticStandards.Gene

/**
 * This Class was Dervied from QuickNGS Original Database name is: NGSAbbundance
 *
 */
class DataTranscriptAbundance implements DataEntry {



    static String[] Headings = ["fpkm","fpkmOK","dataset","gene" ]

    //QuickNGS Name fpkm
    Double fpkm
    //QuickNGS Name fpkmOK
    Boolean fpkmOK

    //QuickNGS Name DatasetID
    Dataset dataset
    //QuickNGS Name EnsemblID,Symbol
    Gene gene

    String[] toArrayHeadings() {
        return Headings
    }

    String[] toArray() {

        return [fpkm.toString(), fpkmOK.toString(), dataset.id, gene ? gene.getEnsemblID().toString() : " "]

    }

    static belongsTo = [dataset: Dataset]

    static constraints = {
        fpkmOK nullable: true

    }

    static mapping = {
        dataset cascade: "delete"
        version false
    }

}
