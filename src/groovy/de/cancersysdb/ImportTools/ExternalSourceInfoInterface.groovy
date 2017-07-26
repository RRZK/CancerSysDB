package de.cancersysdb.ImportTools

import de.cancersysdb.serviceClasses.ExternalSourceDescription

/**
 * This Interface describes External Sources for Data.
 * Important is the URI and The Name of the Source.
 */
interface ExternalSourceInfoInterface {
    ExternalSourceDescription extSource
    String sourceIdentifier
    String uRI

}