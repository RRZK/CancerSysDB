package de.cancersysdb.gegenticViz

import de.cancersysdb.Dataset

/**
 * This class is created to mange visualisation specific coordinates for a scatterplot
 * This class manages the refined data to be visualized, so it acts as an Anchor for the actually displayed values
 */
class InteractiveScatterplotViz {


    /**
     * The Dataset this Visualisation Visualizes
     */
    Dataset dataset1
    /**
     *  This is the Optional Second Dataset Source of The Y value.
     */
    Dataset dataset2

    // XAxis
    //This is Domain Class the xAxis Data comes from
    String xAxisDatatype
    //This is the Field in the Domain Class which acts as Value for the xAxis
    String xAxisField
    // YAxis
    //This is Domain Class the yAxis Data comes from
    String yAxisDatatype
    //This is the Field in the Domain Class which acts as Value for the yAxis
    String yAxisField


    static constraints = {
        dataset2 nullable: true
        yAxisField validator: { val, obj -> if (obj.dataset2 == null && obj.xAxisField.equals(val) && obj.xAxisDatatype.equals(obj.yAxisDatatype)) return ["validation.interactiveScatterplotViz.yAxisDatatype.fieldsTheSame"] }
    }
}
