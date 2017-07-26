package de.cancersysdb.EntityMetadata
/***
 * This class is designed to Save values from an XML field in a flat table.
 * It holds the attribute the tag and the location in the File.
 */
class ClinicalInformation {
    static transients = [
            //This is the most simple name to call the Attribute
            'name',
            //This is the Name with special Identifiers like Namespaces.
            'detailedName']

    /**
     * This is the Name which makes this thing identifiable in contrast to all the other objects in the same location
     */
    String exactName
    /**
     * The Location is the Location in the Document of the Thing
     */
    String location
    /**
     * The Value as String Representation
     */
    String value

    //XMLSpecificFields

    def getDetailedName() {
        if (exactName.endsWith("]") && exactName.contains("["))
            return exactName.subSequence(0, exactName.indexOf("["))
        return exactName
    }

    /**
     * Get the name in the most simple way, no quantifier or namespaces.
     * @return Name of the field in the simplest manner
     */
    def getName() {

        String dn = this.getDetailedName()
        if (dn.contains(":"))
            dn = dn.subSequence(dn.charAt(":", dn.length()))
        return dn

    }

    static belongsTo = [importInfo: ImportInfo]

    static constraints = {
        exactName nullable: false
        value nullable: false
    }
}
