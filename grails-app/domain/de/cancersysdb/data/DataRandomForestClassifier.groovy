package de.cancersysdb.data

import de.cancersysdb.Dataset

/**
 *This is a Classifier which is Stored as A File.
 */
class DataRandomForestClassifier implements DataEntry, SingleLineDataset, BinaryDataDataset {
    static String[] Headings = ["dataset", "gene" ]

    //QuickNGS Name EnsemblID,Symbol
    byte[] classifierFile

    Dataset dataset

    String[] toArrayHeadings() {
        return Headings
    }

    String[] toArray() {
        return [ dataset.id]
    }

    byte[] getData(){
        return this.getClassifierFile()
    }
    static belongsTo = [dataset: Dataset]

    static constraints = {
        classifierFile(maxSize: 20 * 1024 * 1024) // 20 MBs

    }
    static mapping = {
        dataset cascade: "delete"
        classifierFile sqlType: 'longblob'
        version false
    }



}
