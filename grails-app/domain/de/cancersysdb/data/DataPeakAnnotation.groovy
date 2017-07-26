package de.cancersysdb.data

import de.cancersysdb.geneticStandards.Gene

/**
 * This Class was Dervied from QuickNGS Original Database name is: NGSPeaks
 */
class DataPeakAnnotation {

    //QuickNGS Name FeatureType
    String featureType
    //QuickNGS Name EnsemblID,Symbol
    Gene gene

    DataPeak peak


    static belongsTo = [peak: DataPeak]


    static constraints = {
    }
    static mapping = {
        peak cascade: "delete"

        version false

    }


}
