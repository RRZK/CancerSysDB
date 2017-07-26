package de.cancersysdb.serviceClasses

import de.cancersysdb.Dataset

/**
 * This import-helper saves all the unrecognized Gene Identifiers which occured in Uploaded Files
 *
 * Example File Peaks.csv was uploaded to Dataset 1.
 * Peaks.csv contained The Information for Gene dsfi22 which is Unknown to the Database.
 * The information for this unidentifyable gene was discarded.
 * These Discareded Gene information is stored here.
 */
class NotFoundGeneName {
    /**
     * The Gene Name which could not be identified by The Application
     */
    String searchedGeneName
    /**
     * The Dataset where information concerning this gene Name was tried to upload to
     */
    Dataset ds
    static belongsTo= [ds:Dataset]
    static constraints = {
    }
    static mapping = {
        ds cascade: "delete"
    }

}
