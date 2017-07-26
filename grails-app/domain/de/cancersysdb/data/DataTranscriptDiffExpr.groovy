package de.cancersysdb.data

import de.cancersysdb.Dataset
import de.cancersysdb.geneticStandards.Gene

/**
 * This Class was Dervied from QuickNGS Original Database name is: NGSDiffExpr
 * Should Also match on Prasannas Table:
 *
 *
 */
class DataTranscriptDiffExpr implements DataEntry {
    static String[] Headings = ["controlMean","sampleMean","foldChange","pValue","dataset", "gene" ]
    //FrommoltDB Name ControllMean
    //Prasana Expression_sample_1
    Double controlMean

    //QuickNGS Name SampleMean
    //Prasana Expression_sample_2
    Double sampleMean

    //QuickNGS Name FoldChange
    // Berechnung sampleMean/controlMean
    Double foldChange

    //QuickNGS Name pValue
    Double pValue

    //QuickNGS Name EnsemblID,Symbol
    Gene gene

    Dataset dataset

    String[] toArrayHeadings() {

        return Headings

    }

    String[] toArray() {
        return [controlMean.toString(), sampleMean.toString(), foldChange.toString(), pValue.toString(), dataset.id, gene ? gene.getEnsemblID().toString() : " "]
    }

    static belongsTo = [dataset: Dataset]

    static constraints = {
        pValue nullable:true
    }
    static mapping = {
        dataset cascade: "delete"
        version false
    }



}
