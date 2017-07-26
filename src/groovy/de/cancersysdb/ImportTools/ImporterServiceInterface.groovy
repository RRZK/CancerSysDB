package de.cancersysdb.ImportTools

import de.cancersysdb.Dataset
import de.cancersysdb.SourceFile
import de.cancersysdb.User

/**
 * This Interface Defines Input Functions and Static Variable Arrays Which Define what an ImporterService Class can transform to the Database!
 */
interface ImporterServiceInterface {
    /**
     * This Variable Contains Datatypes as Keys and Target Classes as Lists.
     * This can be read so The best importer for a Task can be found.
     */
    static Map<String, List> MapsTo
    /**
     * This Map Contains the Matiching Patterns for the Maps to Defined File Types.
     * This can either contain Possible File Name Ends or a Lot of Regex Patterns to match The Filename
     * A Regexpattern will allways be choosen with Higher Priority while determiting an suitable importerService
     */
    static Map<String, List> FilenamePattern
    /**
     * This is the Main Function which hastobe Implemented to Parse Stuff into the Database
     * @param format The Format of The File
     * @param Into The Table this Parses into
     * @param File The file with Content as String or Stream
     * @param sourceFile The Source file Meta Description form the Database
     * @param owner The Owner
     * @param annon Is it Annonmyzed
     * @param shared is it Shared
     * @param ip The Import Protocoll
     * @param ds The Dataset to import the Stuff to
     * @return Import protocoll with the Successstatus etc
     */
    ImportProtocol importContent(String format, String Into,
                                 def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds)


}