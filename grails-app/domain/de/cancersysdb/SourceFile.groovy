package de.cancersysdb

import de.cancersysdb.serviceClasses.FiletypeToGeneticStandardMatching

/**
 * Describes an Uploaded File. The Class was created for detecting duplicated
 */
class SourceFile {
    Integer contentHash
    String fileName
    String originalFilename
    Dataset dataset
    FiletypeToGeneticStandardMatching importMapping
    Long byteSize
    Date dateCreated
    Date lastUpdated
    static constraints = {
        dataset nullable: true
        importMapping nullable: true
    }


    static mapping = {
        autoTimestamp true
    }

}
