package de.cancersysdb.contextHandling

/**
 * Created by rkrempel on 29.03.16.
 * This is a Tool that supports Static Functions for TCGA Normalisation
 * https://wiki.nci.nih.gov/display/TCGA/TCGA+barcode
 */
class TcgaConventions extends CancersysBarcodeConventions{

    private static final def StudyPattern = ~/^TCGA/
    private static final def PatientPattern = ~/^TCGA-[0-9]{2}-[A-z0-9]{4}/
    private static final def SamplePattern = ~/^TCGA-[0-9]{2}-[A-z0-9]{4}-[0-9]{2}[A-z]{0,1}/
/*
    *//**
     * Reduces a TCGA Barcode to its Sample Else it just Returns the Value
     * @param Barcode TCGA Barcoe
     * @return String That represents a TCGA Barcode at Sample LEvel
     *//*
    static String BarcodeToSample(String Barcode){

        if(containsSampleBarcode(Barcode))
            return Barcode.substring(0,15)
        return Barcode
    }



    *//**
     * Reduces a TCGA Barcode to its Patient Else it just Returns the Value
     * @param Barcode
     * @return String That represents a TCGA Barcode at Patient LEvel
     *//*
    static String BarcodeToPatient(String Barcode){

        if(containsPatientBarcode(Barcode))
            return Barcode.substring(0,12)
        return Barcode
    }
    *//**
     * Find out if the Barcode describes EXACTLY the TCGA Patient Barcode NOT MORE!
     * @param Barcode
     * @return is EXACTLY a TCGA Patient Barcode(not more and non less)
     *//*

    static boolean isPatientBarcode(String Barcode){
        if(Barcode ==~ /^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}$/)
            return true
        return false
    }

    *//**
     * Find out if the Barcode describes EXACTLY the TCGA Sample Barcode NOT MORE!
     * @param Barcode
     * @return is EXACTLY a TCGA Sample Barcode(not more and non less)
     *//*
    static boolean isSampleBarcode(String Barcode){
        if(Barcode ==~ /^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}-[0-9]{2}[A-z]{0,1}$/)
            return true
        return false
    }
    *//**
     * Find out if the Barcode contains the TCGA Patient Barcode NOT MORE!
     * @param Barcode
     * @return contains a TCGA Patient Barcode
     *//*
    static boolean containsPatientBarcode(String Barcode){
        if(Barcode ==~ /^TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}.+/)
            return true
        return false
    }
    *//**
     * Find out if the Barcode contains the TCGA Sample Barcode NOT MORE!
     * @param Barcode
     * @return contains a TCGA Sample Barcode
     *//*
    static boolean containsSampleBarcode(String Barcode){
        if(Barcode ==~ /TCGA-[A-Z0-9]{2}-[A-Z0-9]{4}-[0-9]{2}[A-z]{0,1}.+/)
            return true
        return false
    }


    *//**
     * Reduces a TCGA Barcode to its Study Else it just Returns the Value
     * @param Barcode
     * @return String That represents a TCGA Barcode at Study LEvel
     *//*
    static String BarcodeToStudy(String Barcode){

        if(containsStudyBarcode(Barcode))
            return Barcode.substring(0,4)
        return Barcode
    }

    *//**
     * Find out if the Barcode describes EXACTLY the TCGA Study Barcode NOT MORE!
     * @param Barcode
     * @return is EXACTLY a TCGA Study Barcode(not more and non less)
     *//*
    static boolean isStudyBarcode(String Barcode){
        if(Barcode =="TCGA")
            return true
        return false
    }

    *//**
     * Find out if the Barcode contains the TCGA Study Barcode NOT MORE!
     * @param Barcode
     * @return contains a TCGA Study Barcode
     *//*
    static boolean containsStudyBarcode(String Barcode){
        if(Barcode ==~ /^TCGA.+/)
            return true
        return false
    }*/

}
