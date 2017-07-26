package de.cancersysdb.data

import de.cancersysdb.Dataset
import de.cancersysdb.GeneticHelpers.GenPosInterface
/**
 * This Class was Dervied from FrommoltDB Original Database name is: NGSPeaks
 */
class DataPeak implements GenPosInterface , DataEntry{

    static String[] Headings = ["chromosome", "startPos", "endPos", "foldEnrichment", "pValue"]
    static Map GeneticDataContext = [
            "hasAnnotation"       : true,
            "AnnotationTable"     : "GenetricPeakAnnotation",
            "AnnotationForeignKey": "peak"
    ]

    //QuickNGS Name Chromosome
    String chromosome
    //QuickNGS Name PeakStart
    Long startPos
    //QuickNGS Name PeakEnd
    Long endPos

    //QuickNGS Name FoldEnrichment
    Double foldEnrichment
    //QuickNGS Name pValue
    Double pValue

    String[] toArrayHeadings() {
        return Headings
    }

    String[] toArray() {

        return [chromosome.toString(), startPos.toString(), endPos.toString(), foldEnrichment.toString(), pValue.toString()]

    }
    static belongsTo = [dataset: Dataset]
    static hasMany = [annotations: DataPeakAnnotation]
    static constraints = {
        pValue nullable: true
        foldEnrichment nullable: true
        endPos validator: { val, obj -> if (val < obj.startPos) ["StartBeforeEnd", val] }
    }
    static mapping = {
        dataset cascade: "delete"
        chromosome index: 'chrom_Index'
        startPos index: 'endPos_Index'
        endPos index: 'startPos_Index'

        annotations cascade: 'all-delete-orphan'
        version false

    }
}
