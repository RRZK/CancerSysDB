package de.cancersysdb.FrontendTools

/**
 * This is an Interface for diffrend String representations
 */
interface StringRepresentationInterface {


    //This is a Long desription of the Dataset Contain Contextual Inforation. Results in a Long Description String
    String toContextFreeLongString()
    //Context is Represented by something else. It is Distinguishable from others
    String toContextShortIndividualizedString()
    //Context is Represented by something else. It described from others
    String toContextShortDeIndividualizedString()
    //Technical desription with Class and ID
    String toTechDesriptionString()


}
