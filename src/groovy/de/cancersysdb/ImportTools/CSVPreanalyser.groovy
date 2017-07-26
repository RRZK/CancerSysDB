package de.cancersysdb.ImportTools

import org.apache.commons.logging.LogFactory

/**
 * Helper Class that manages to Preanalyse CSV Files to determine howto parse the File
 *
 */
class CSVPreanalyser {
    static def possibleSeperators = [",", "\t", ";", "|", " "]
    static def possibleEnclose = ['"', "'"]
    String enclose = ""
    String ExampleLines
    String Seperator
    String headline
    boolean hasHeadline
    //The Field in the CSV File
    List Fields

    private static final log = LogFactory.getLog(this)
    /**
     * Analyse the CSV Data and check what attributes it has
     * @param csvData The CSV Data its iterated with new line
     * @return
     */
    CSVPreanalyser(Map KnownStuff){
        if(KnownStuff){
            if(KnownStuff.containsKey("Seperator"))
                Seperator=KnownStuff.get("Seperator")
            if(KnownStuff.containsKey("enclose"))
                enclose=KnownStuff.get("enclose")
            if(KnownStuff.containsKey("headline"))
                headline=KnownStuff.get("headline")
        }
    }

    boolean Preanaylse(csvData) {
        if (!csvData)
            log.warn("csvData input Empty !")
        ExampleLines = this.TestlinesfromCSVFile(csvData)
        if(headline==null)
            headline = this.parseHeadline(ExampleLines)
        if(Seperator==null)
            Seperator = this.determineSeperator(ExampleLines,headline)
        if(enclose==null)
            enclose = this.determineEnclose(ExampleLines)

        Fields = this.parseFields(headline)
        if (headline && Fields)
            hasHeadline = true
        else
            hasHeadline = false

    }

    /**
     * Parse the Headline
     * @param ExampleLines The Example lines
     * @return Headline
     */
    String parseHeadline(String ExampleLines) {

        int Endofline = ExampleLines.indexOf('\n')
        if (Endofline < 0)
            log.error("No Headline/Line Found for " + ExampleLines)
        String out = ExampleLines.substring(0, Endofline)
        return out

    }

    List<String> parseFields(String headline) {
        return headline.replace('"', '').split(Seperator)
    }

    static String determineEnclose(csvData) {

        Map Occs = [:]
        Map lastOccs = [:]
        for (posEnc in possibleEnclose) {
            Occs.put(posEnc, 0)
            lastOccs.put(posEnc, 0)
        }

        boolean first = true
        csvData.eachLine {
            String line ->

                for (posEnc in possibleEnclose) {

                    def menge = line.findAll(posEnc).size()
                    if (!first) {
                        if (menge > 1 && (menge % 2) == 0 && lastOccs.get(posEnc) == menge)
                        //The More Fields there are the better it will be Weighted
                            Occs.put(posEnc, Occs.get(posEnc) + 1)
                        else
                            Occs.put(posEnc, Occs.get(posEnc) - 1)
                    } else
                        first = false
                    lastOccs.put(posEnc, menge)
                }


        }

        def maxvalue = 0
        def maxenc = ''
        Occs.each {
            key, value ->
                //print key +"  "+value
                if (value > maxvalue) {
                    maxvalue = value
                    maxenc = key
                }
        }

        return maxenc
    }
    /**
     * Determine which Seperator is Used in the CSV file
     * @param csvData The Data of which to extract the comma from
     * @return The Separator
     */
    static String determineSeperator(csvData,headline = null) {

        Map Occs = [:]
        Map lastOccs = [:]
        List posseps= []
        //Seperator must Occure in the Healine
        if(headline){
            possibleSeperators.each {
                if(headline.contains(it))
                    posseps.add(it)
            }
        }else
            posseps.addAll( possibleSeperators)

        for (posSep in posseps) {
            Occs.put(posSep, 0)
            lastOccs.put(posSep, 0)
        }

        boolean first = true
        csvData.eachLine {
            line ->

                for (posSep in posseps) {

                    def menge = line.tokenize(posSep).size()
                    if (!first) {
                        if (menge > 1 && lastOccs.get(posSep) == menge)
                        //The More Fields there are the better it will be Weighted
                            Occs.put(posSep, Occs.get(posSep) + 1)
                        else
                            Occs.put(posSep, Occs.get(posSep) - 1)
                    } else
                        first = false
                    lastOccs.put(posSep, menge)
                }

        }

        def maxvalue = 0
        def maxSep = ";"
        Occs.each {
            key, value ->
                //print key +"  "+value
                if (value > maxvalue) {
                    maxvalue = value
                    maxSep = key
                }
        }

        return maxSep
    }

/**
 * Gets a Sample of Max 1000 Lines form File
 * @param csvData The Complete CSV Data
 * @return Set of Lines which represents a Sample of the CSV-File
 */

    static String TestlinesfromCSVFile(csvData) {
        def lineCount = 0
        def actuLines = 0
        def totalLineCount = 0
        String newString = ""
        csvData.eachLine {

            totalLineCount++

        }
        int mod
        if (totalLineCount < 1000)
            mod = 1
        else
            mod = totalLineCount / 1000

        csvData.eachLine {
            line ->

                lineCount++
                if (lineCount < 3 || lineCount % mod == 0) {
                    newString += line + "\n"
                    actuLines++
                }

        }
        if (newString.empty)
            log.warn("Sample Empty " + actuLines + " lines processed")
        //print "AktualLines"+actuLines
        return newString
    }

    //TODO
/*    boolean AnaylseHeadline() {


        firstLine = false
        matching.headline = true
        for (reftotype in RegexptoType) {
            //Is Headline if Yes Skip
            if (!matching.headline)
                break
            for (a in tokens) {
                if (a ==~ reftotype.Pattern) {
                    matching.headline = false

                    break
                }
            }
        }
        for (int i = 0; i < tokens.size(); i++) {
            names.add(tokens.getAt(i))
        }
    }*/
    String print(){
        StringBuffer out = new StringBuffer()
        out.append("enclose " +enclose +"\n")
        out.append("Seperator " +Seperator+"\n")
        out.append("headline " +headline+"\n")
        out.append("hasHeadline " +hasHeadline+"\n")
        out.append("Fields " +Fields.toString()+"\n")
        return out.toString()
    }

}
