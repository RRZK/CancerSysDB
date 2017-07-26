package de.cancersysdb.GeneticHelpers

/**
 * Calculates simple Statistics for Single Datasets
 */
class GeneDataStatistics {
    Map fieldsToTypes = [:]
    Map StatisticByField = [:]
    Map nullFieldCount
    //Number Statistics
    Map accFieldCount
    Map avgFieldCount
    Map avgFieldCountwithValue
    Map distinctValuesField

    Map insertsMap
    Integer nCases


    def startSession() {
        nullFieldCount = [:]
        insertsMap = [:]
        accFieldCount = [:]
        distinctValuesField = [:]
        fieldsToTypes.each {
            field, fieldType ->
                insertsMap.put(field, new Integer(0))
                nullFieldCount.put(field, new Integer(0))
                if (Number.class.isAssignableFrom(fieldType)) {
                    accFieldCount.put(field, new Double(0))
                }
                if (fieldType.equals(String.class))
                    distinctValuesField.put(field, [:])

        }

    }
    /**
     * Calculates Statistics form the Collected Data
     */
    void calculateStatistics() {
        avgFieldCount = [:]
        avgFieldCountwithValue = [:]
        Set usedFields = fieldsToTypes.keySet()
        //Remove not Completely Filled Columns
        Integer max = 0
        insertsMap.each {
            k, Integer v ->
                if (v > max)
                    max = v
        }
        //print "max"+ max
        //print insertsMap
        //Remove all Values, that are not Completely inserted out.
        nCases = max
        insertsMap.each {
            k, v ->
                if (v != max) {

                    accFieldCount.remove(k)
                    nullFieldCount.remove(k)
                    distinctValuesField.remove(k)
                    usedFields.remove(k)
                }

        }
        StatisticByField = [:]

        usedFields.each {
            fie ->
                StatisticByField.put(fie, [:])

        }
        //print accFieldCount
        accFieldCount.each {
            k, v ->

                Map allmap = StatisticByField.get(k)
                def nullfieldcount = nullFieldCount.get(k)
                def avg = 0
                def avgWV = 0
                if (v != 0) {
                    avg = v / max
                    avgWV = v / (max - nullfieldcount)
                }

                avgFieldCount.put(k, avg)
                avgFieldCountwithValue.put(k, avgWV)

                allmap.put("avarage value", avg)
                if (avgWV != avg)
                    allmap.put("avarage of existing values", avgWV)
                StatisticByField.put(k, allmap)


        }
        //print accFieldCount
        //MapOfMaps
        distinctValuesField.each {
            key, Maps ->

                if (Maps.keySet().size() > max / 10) {
                    Map allmap = StatisticByField.get(key)
                    allmap.put("Values by count", Maps)
                    StatisticByField.put(key, allmap)
                }


        }
        def toremove = []
        for (a in StatisticByField.keySet()) {
            if (StatisticByField.get(a).isEmpty())
                toremove.add(a)
        }
        toremove.each { a -> StatisticByField.remove(a) }

/*        print distinctValuesField
        print accFieldCount
        print avgFieldCount*/


    }

    /**
     *
     * @param field
     * @param token
     */
    void addValueToField(field, String token) {
        Map temp = distinctValuesField.get(field)
        def count = temp.get(token)
        if (count == null) {
            count = new Integer(0)
        }
        count++
        temp.put(token, count)
        distinctValuesField.put(field, temp)


    }

    /**
     * ObjectBased Input and Calculation
     * @param ob The Object To take into Statistics
     * @return nuddin
     */
    void putObject(Object ob) {

        fieldsToTypes.keySet().each {
            field ->
                putField(field, ob."$field")

        }

    }
    /**
     * ObjectBased Input and Calculation
     * @param ob The Object To take into Statistics
     * @return nuddin
     */
    private void singleField(field, List) {

        List.each {
            val ->
                putField(field, val)

        }

    }

    /**
     * Add Values just for one Field
     * @param field
     * @param value
     */
    void putField(String field, value) {
        def temp
        def newval = insertsMap.get(field) + 1
        insertsMap.put(field, newval)
        if (value == null) {
            temp = nullFieldCount.get(field)
            temp++
            nullFieldCount.put(field, temp)
        } else if (accFieldCount.containsKey(field)) {
            temp = accFieldCount.get(field)
            temp = temp + value

            accFieldCount.put(field, temp)
        } else if (distinctValuesField.containsKey(field)) {

            addValueToField(field, value)
        }


    }


}
