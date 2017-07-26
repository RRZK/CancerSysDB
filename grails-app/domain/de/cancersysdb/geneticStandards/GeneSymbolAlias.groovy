package de.cancersysdb.geneticStandards
/**
 * If A Gene has more than One Unique Alias this Class can help looking up those aliases to get Data wich is More complete without accepting False Negatives
 */
class GeneSymbolAlias {
    /**
     * The Alias Name of The Gene as String
      */
    String alias
    /**
     * The Internal gene
     */
    Gene gene


    static constraints = {
        alias unique: true, nullable: false, blank: false
        gene  nullable: false
    }

    static mapping = {
        alias index: 'chrom_Index'

    }

}
