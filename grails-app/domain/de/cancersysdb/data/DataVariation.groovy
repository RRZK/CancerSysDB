package de.cancersysdb.data

import de.cancersysdb.GeneticHelpers.GenPosInterface
import de.cancersysdb.Dataset

class DataVariation implements GenPosInterface,DataEntry{

    static String[] Headings = ["chromosome","startPos","endPos", "refAllele", "altAllele","totalDepth", "altDepth","genotype","dataset","qualityScore"]
    static Map GeneticDataContext =[
            "hasAnnotation": true,
            "AnnotationTable":"DataVariationAnnotation",
            "AnnotationForeignKey":"variation"

    ]

    //Postitioning
    //QuickNGS Name Chromosome
    String chromosome
    //QuickNGS Name Start
    Long startPos
    //QuickNGS Name End
    Long endPos
    //QuickNGS Name RefAllele
    String refAllele
    //QuickNGS Name AltAllele
    String altAllele

    //QuickNGS Name TotalDepth
    //Gesamtcoverage Tumor
    Integer totalDepth
    //QuickNGS Name RefDepth
    //Coverage Referenzallel Tumor
    Integer refDepth
    //QuickNGS Name AltDepth
    //Coverage Alternativallel Tumor
    Integer altDepth

    //AdditionalFields
    //Coverage Normal

    Integer totalDepthCtrl
    Integer refDepthCtrl
    Integer altDepthCtrl
    Integer callerID

    //QuickNGS Name Genotype
    String genotype
    //QuickNGS Name QualityScore
    Double qualityScore

    Double freq


    String[] toArrayHeadings() {


        log.debug("Headings: " + Headings)
        return Headings

    }

    String[] toArray() {

        return [chromosome.toString(), startPos.toString(), endPos.toString(), refAllele.toString(), altAllele.toString(), totalDepth.toString(), altDepth.toString(), genotype.toString(), dataset.id.toString()]
    }

    static belongsTo = [dataset: Dataset]
    static hasMany = [annotations:DataVariationAnnotation]
    static mapping = {
        dataset cascade: "delete"
        chromosome index: 'chrom_Index'
        startPos index: 'endPos_Index'
        endPos index: 'startPos_Index'
        version false
        annotations cascade: 'all-delete-orphan'
    }




    static constraints = {
        endPos validator: { val, obj -> if (val < obj.startPos) ["StartBeforeEnd", val] }

        //startPos  max: endPos-1
        totalDepth nullable: true
        refDepth nullable: true
        altDepth nullable: true

        totalDepthCtrl nullable: true
        refDepthCtrl nullable: true
        altDepthCtrl nullable: true
        callerID nullable: true

        //QuickNGS Name Genotype
        genotype nullable: true
        //QuickNGS Name QualityScore
        qualityScore nullable: true
        freq nullable: true

    }
}
