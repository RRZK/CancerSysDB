package de.cancersysdb.serviceClasses

import de.cancersysdb.Dataset

class FiletypeToGeneticStandardMatching {
    //TODO Split in FileDescription and Mapping Component?
    /**
     * Is There a Headline in the File
     */
    Boolean headline
    /**
     * The Seperator used in the File
     */
    String seperator
/**
 * Teh Char Values are Enclodsed O
  */
    String enclose = ""
    /**
     * The Field in the Types
     */
    List fieldsInFile
    /**
     * Field name with the Types of Data in it
     */
    Map fieldTypes

    //A Pattern that Matches the Filename
    String filenameMatchingPattern

    /**
     * This is a LookupNameFor the Mapping
     */
    String authorityName

    //FieldInFileIdentifier to FieldNameinClass
    Map fieldMapping
    Map fieldsSpecialValue
    /**
     * The Class this Mapping Attribues
     */
    String targetClass

    @Override
    String toString() {

        String out = ""
        if (filenameMatchingPattern)
            out += "Filename Matches Pattern " + filenameMatchingPattern + " \n"
        out += "File has Headline " + headline + "\n"
        out += "Target Class " + targetClass + "\n"
        out += "Fields in File" + "\n"
        fieldsInFile.each {
            it ->
                out += it + "\n"
        }
        out += "Field types" + "\n"
        fieldTypes.each {
            Field, type ->
                out += Field + " -> " + type + "\n"
        }
        out += "Field Mapping" + "\n"
        fieldMapping.each {
            Field, mapped ->
                out += Field + " -> " + mapped + "\n"
        }
        out += "Fields Special Values" + "\n"
        fieldsSpecialValue.each {
            Field, specialValue ->
                out += Field + " -> " + specialValue + "\n"
        }
        return out
    }
    static hasMany = [fieldsInFile: String,datasetsUsedImport:Dataset]

    static constraints = {

        fieldsInFile nullable: false
        fieldTypes nullable: true
        fieldMapping nullable: true
        fieldsSpecialValue nullable: true
        //TODO Regex Validation
        filenameMatchingPattern nullable:true
        authorityName nullable:true
        fieldsInFile cascade: 'all-delete-orphan'
    }


    public boolean matchFilename(String filename) {
        //IF there is no Limitation by one of those Patterns this Function allways returns true
        if (!filenameMatchingPattern)
            return true
        //Remove Parts of Paths
        if (filename.contains("/") || filename.contains("\\")) {
            if (filename.lastIndexOf("/") >= 0)
                filename = filename.substring(filename.lastIndexOf("/"))
            else if (filename.lastIndexOf("\\") >= 0)
                filename = filename.substring(filename.lastIndexOf("/"))
        }

        //Match!
        if (filename =~ filenameMatchingPattern)
            return true
        else
            return false


    }


}
