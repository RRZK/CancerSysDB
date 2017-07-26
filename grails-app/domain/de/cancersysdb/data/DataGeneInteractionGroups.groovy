package de.cancersysdb.data

import de.cancersysdb.Dataset

/**
 * This Table Sets the type of gene interactions.
 * It helps to generate and manage a Graph of Gene Interactions
 */
class DataGeneInteractionGroups implements DataEntry {

    String interactionDescription

    URI dataSource
    Dataset dataset
    //static hasMany = [interactions:DataGeneInteraction]
    static belongsTo = [dataset: Dataset]

    static mapping = {
        dataset cascade: "delete"

        version false
    }
}
