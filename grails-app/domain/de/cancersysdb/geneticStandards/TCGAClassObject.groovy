package de.cancersysdb.geneticStandards


class TCGAClassObject implements Comparable {
    /**
     * Describes The Types of TCGA Classifications. There are Multiple Controlled Vocabluraries by the TCGA this Class provides a Picture.
     */
    static datatypes = ["cancer", "sampleType", "tissueType"]
    /**
     * The TCGA Code Tables have exactly these Fields:
     */
    String name
    String abbreviation
    String code
    Integer type

    String toString() {
        return name
    }

    int compareTo(obj) {
        int out = this.type.compareTo(obj.type)
        if (out != 0)
            return out

        return this.name.compareTo(obj.name)

    }


    static constraints = {
        abbreviation nullable: true, blank: true
        code nullable: true, blank: true
        name nullable: false, blank: false

    }
    //Test
    static def marshallers = ["exchange": {
        def tcga ->
            return [
                    id          : tcga.id,
                    class       : tcga.getClass().name,
                    name        : tcga.name,
                    abbreviation: tcga.abbreviation,
                    code        : tcga.code,
                    type        : tcga.type
            ]
    }
    ]
}
