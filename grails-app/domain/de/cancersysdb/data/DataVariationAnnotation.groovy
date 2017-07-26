package de.cancersysdb.data

import de.cancersysdb.geneticStandards.Gene

class DataVariationAnnotation {
    /**
     * TODO English please
     */

    //QuickNGS Name Effect -> example harmful or not?
    //Lage relativ zum kodierenden Bereich des Gens
    String effect
    //QuickNGS Name FunctionalClass
    //Auswirkung auf das Protein
    String functionalClass
    //QuickNGS Name AAChange
    /**
     * Change in the Protein this Variation causes.(Aminosäureaustausch)
     */
    String aaChange
    //QuickNGS Name BioType
    //Art des Gens
    String bioType
    //Name des Gens -> Über Verbindung zum Gen ??? Nachfragen
    //Symbol
    //RefSNP: ID der Variante, falls diese bereits bekannt ist
    String refSNP
    /**
     * Minor Allele Frequency
     */
    Double maf
    /**
     * prediction of pathogenicity
     */
    String prediction

    /**
     * Gene this line is ascribed to
     */
    Gene gene


    static belongsTo = [variation: DataVariation]

    static constraints = {

        functionalClass nullable: true
        aaChange nullable: true
        bioType nullable: true
        refSNP nullable: true
        prediction nullable: true
        maf nullable: true

        gene nullable: true

    }
    static mapping = {
        variation cascade: "delete"
    }
}
