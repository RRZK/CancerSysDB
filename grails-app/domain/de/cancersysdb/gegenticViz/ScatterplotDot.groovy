package de.cancersysdb.gegenticViz

class ScatterplotDot {

    //Standard Coordinate Matching by Stuff
    String chromosome
    Integer startPos
    Integer endPos

    //The yExpression the y Expression
    Float xVal
    Float yVal

    static belongsTo = [interactiveScatterplotViz: InteractiveScatterplotViz]

    static constraints = {
    }
}
