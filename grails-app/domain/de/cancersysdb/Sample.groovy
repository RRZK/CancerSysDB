package de.cancersysdb

import de.cancersysdb.FrontendTools.StringRepresentationInterface
import de.cancersysdb.ImportTools.ExternalSourceInfoInterface
import de.cancersysdb.serviceClasses.ExternalSourceDescription
import de.cancersysdb.geneticStandards.TCGAClassObject


/**TCGA Mapping ClinicalInformation
 *
 * tissueType->
 * /bio:tcga_bcr/bio:patient/bio:samples/bio:sample/bio:sample_type
 * cancerType->
 * /bio:tcga_bcr/admin:admin/admin:disease_code
 * location->
 * Location wurde noch nicht gefunden Im Beispiel Hatte ich bei Blutkrebs Bone Marrow
 * Label->
 * /bio:tcga_bcr/bio:patient/bio:samples/bio:sample/bio:bcr_sample_barcode/text()
 *
 *
 */

class Sample implements ExternalSourceInfoInterface, StringRepresentationInterface {
    //ExternalSourceInfoInterface
    ExternalSourceDescription extSource
    String sourceIdentifier
    String uRI

    String batch
    Integer pairEnds
    String label

    //TCGA Classification
    TCGAClassObject tissueType
    TCGAClassObject cancerType
    TCGAClassObject location
    /**
     * Patient the Sample belongs to
     */
    Patient patient

    //User rights
    Boolean annon
    Boolean shared
    User owner

    static belongsTo = [patient:Patient]
    static hasMany = [datasets:Dataset]
    static constraints = {
        location nullable: true
        extSource nullable: true
        uRI nullable: true
        sourceIdentifier nullable: true
        location nullable: true
        batch nullable: true
        pairEnds nullable: true

    }
    static def marshallers = ["exchange": { Sample sample ->

        return [
                id        : sample.id,
                class     : sample.getClass().name,
                batch     : sample.batch,
                pairEnds  : sample.pairEnds,
                label     : sample.label,
                tissueType: sample.tissueType,
                cancerType: sample.cancerType,
                location  : sample.location,
                //TODO Remove The Following Bits
                owner     : sample.owner,
                shared    : sample.shared,
                annon     : sample.annon

        ]
    }
    ]

    //This is a Long desription of the Dataset Contain Contextual Inforation. Results in a Looong Description String
    String toContextFreeLongString() {
        this.toString()
    }
    //Context is Represented by something else. It is Distinguishable from others
    String toContextShortIndividualizedString() {
/*        if(label)
            return label
        else*/

        def temp = []

        if (tissueType)
            temp.add(tissueType.abbreviation ?: tissueType.name)
        if (location)
            temp.add(location.abbreviation ?: location.name)
        if (cancerType)
            temp.add(cancerType.abbreviation ?: cancerType.name)
        return temp.join("; ") + " ID:" + this.id
/*        else if(sourceIdentifier)
            return sourceIdentifier + " (" + this.id +")"
        else if(uRI)
            return uRI+ " (" + this.id +")"*/
    }
    //Context is Represented by something else. It described from others
    String toContextShortDeIndividualizedString() {
        this.toString()
    }
    //Technical desription with Class and ID
    String toTechDesriptionString() {
//        def temp = this.toString()
//        ExternalSourceDescription extSource
//        String sourceIdentifier
//        String uRI

    }
    static mapping = {

        sourceIdentifier index:'sourceIdentifier_idx'
    }

}
