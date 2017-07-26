package de.cancersysdb.serviceClasses
/**
 * This Class Describes External References.
 * It holdes information about the Source of Data.
 */
class ExternalSourceDescription {
    String name
    String uRL
    String description
    String descriptionReference

    static constraints = {
        name unique: true
        uRL url: true
        description nullable: true
        descriptionReference url: true, nullable: true
    }
}
