package de.cancersysdb.data

import de.cancersysdb.Dataset
import de.cancersysdb.geneticStandards.Gene

/**
 * This is from Prasanna DB Table function
 *
 * it shows the Function of a Gene.
 */
class DataGeneticFunction implements DataEntry {
    static String[] Headings =["gene" ,"gene_Function","process","uri"]
    // From Prasanna Table Gene_function
    String gene_Function
    //From Prasanna Field Process
    String process
    // From Prasanna Field Link
    // References the Origin of the Function Can Support Visualisations
    String uri
    //Gene_id
    Gene gene
    Dataset dataset

    String[] toArrayHeadings() {
        return Headings
    }

    String[] toArray() {

        return [gene ? gene.getEnsemblID() : " ", gene_Function, process.toString(), uri.toString()]

    }
    static belongsTo= [dataset:Dataset]

    static constraints = {
        process nullable: true
        uri nullable: true

    }

    static mapping = {
        dataset cascade: "delete"
        gene cascade: "delete"

        version false

    }

}
