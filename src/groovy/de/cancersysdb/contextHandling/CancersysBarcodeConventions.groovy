package de.cancersysdb.contextHandling

import java.util.regex.Matcher

/**
 * This is a general Description for the Cancersys Barcode. Its a Superset of the TCGA Barcode
 * In general a Cancersys Barcode consists of a Barcode that matches this pattern: ([0-9A-z_]+)-([0-9A-z_]+)-([0-9A-z_]+)-([0-9A-z_]+)
 * The Matchingclasses Match the things in the TCGA
 * <ProjectName>-<CenterCode>-<PatientNumber>-<SampleCode>
 */
class CancersysBarcodeConventions {
    private static final def StudyPattern = ~/^[A-z0-9_]+/
    private static final def PatientPattern = ~/^[A-z0-9_]+-[A-z0-9_]+-[A-z0-9_]+/
    private static final def SamplePattern = ~/^[A-z0-9_]+-[A-z0-9_]+-[A-z0-9_]+-[A-z0-9_]+/


    /**
     * Find out if the Barcode describes EXACTLY the Cancersys Sample Barcode NOT MORE!
     * @param Barcode
     * @return is EXACTLY a Cancersys Sample Barcode(not more and non less)
     */
    static boolean isSampleBarcode(String Barcode){
        if(Barcode ==~ SamplePattern)
            return true
        return false
    }

    /**
     * Find out if the Barcode contains the Cancersys Sample Barcode NOT MORE!
     * @param Barcode
     * @return contains a Cancersys Sample Barcode
     */
    static boolean containsSampleBarcode(String Barcode){
        if((Barcode =~ SamplePattern).getCount())
            return true
        return false
    }

    /**
     * Find out if the Barcode describes EXACTLY the Cancersys Patient Barcode NOT MORE!
     * @param Barcode
     * @return is EXACTLY a Cancersys Patient Barcode(not more and non less)
     */

    static boolean isPatientBarcode(String Barcode){
        if(Barcode ==~ PatientPattern)
            return true
        return false
    }
    /**
     * Find out if the Barcode contains the Cancersys Patient Barcode NOT MORE!
     * @param Barcode
     * @return contains a Cancersys Patient Barcode
     */
    static boolean containsPatientBarcode(String Barcode){
        if((Barcode =~ PatientPattern).getCount())
            return true
        return false
    }


    /**
     * Find out if the Barcode describes EXACTLY the Cancersys Study Barcode NOT MORE!
     * @param Barcode
     * @return is EXACTLY a Cancersys Study Barcode(not more and non less)
     */
    static boolean isStudyBarcode(String Barcode){
        if(Barcode ==~ StudyPattern)
            return true
        return false
    }

    /**
     * Find out if the Barcode contains the Cancersys Study Barcode NOT MORE!
     * @param Barcode
     * @return contains a Cancersys Study Barcode
     */
    static boolean containsStudyBarcode(String Barcode){
        if((Barcode =~ StudyPattern).getCount())
            return true
        return false
    }
    /**
     * Reduces a Cancersys Barcode to its Sample Else it just Returns the Value
     * @param Barcode Cancersys Barcoe
     * @return String That represents a Cancersys Barcode at Sample LEvel
     */
    static String BarcodeToSample(String Barcode){

        if(containsSampleBarcode(Barcode))
            //Test!
            return (Barcode =~ SamplePattern)[0]
        return Barcode
    }
    /**
     * Reduces a Cancersys Barcode to its Patient Else it just Returns the Value
     * @param Barcode
     * @return String That represents a Cancersys Barcode at Patient LEvel
     */
    static String BarcodeToPatient(String Barcode){

        if(containsPatientBarcode(Barcode))
            return (Barcode =~ PatientPattern)[0]
        return Barcode
    }
    /**
     * Reduces a Cancersys Barcode to its Study Else it just Returns the Value
     * @param Barcode
     * @return String That represents a cancersys barcode at study-level
     */
    static String BarcodeToStudy(String Barcode){

        if(containsStudyBarcode(Barcode))
            return (Barcode =~ StudyPattern)[0]
        return Barcode
    }





}
