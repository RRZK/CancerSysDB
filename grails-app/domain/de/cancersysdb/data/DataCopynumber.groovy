package de.cancersysdb.data

import de.cancersysdb.GeneticHelpers.GenPosInterface
import de.cancersysdb.Dataset

/**
 * This is a windows based approach the windowsize may vary between.
 */

class DataCopynumber implements GenPosInterface,DataEntry {

    ////////Required Fields
    /**
     * Chromosome to which this copy number Field is asocitated
     */
    String chromosome
    /**
     * The Start Position of the read
     */
    Long startPos
    /**
     * The Endposition of the read
     */
    Long endPos
    /**
     * Copy Number of the read
     */
    Double copyNumber
    Integer numProbes
    //////////Optional Fields
    Double gcPercent
    static belongsTo = [dataset: Dataset]

    static mapping = {
        dataset cascade: "delete"
        chromosome index: 'chrom_Index'
        startPos index: 'endPos_Index'
        endPos index: 'startPos_Index'
        version false
    }

    static constraints = {
        endPos validator: { val, obj -> if (val < obj.startPos) ["StartBeforeEnd", val] }
        gcPercent nullable: true
    }
    static def Autovalue = [
            endPos: [method: "fixedWindowSize", fromField: "startPos"]
    ]
}
