package de.cancersysdb.Importer

import de.cancersysdb.Dataset
import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.ImportTools.ImporterServiceInterface
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import de.cancersysdb.data.DataPeak
import de.cancersysdb.data.DataRandomForestClassifier
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.transaction.Transactional
import grails.validation.ValidationException
import org.springframework.validation.Errors

/**
 * This Service is an ImporterService and it manages the Import of BED Files
 */
@Transactional
class RandomTreeClassifierImporterService implements ImporterServiceInterface {
    /**
     * The Setting which this Importer can Import where
     */
    static Map<String, List> MapsTo = ["obj": ["RandomForestClassifier"]]
    /**
     * No Filename Pattern
     */
    static Map<String, List> FilenamePattern = [:]

    ImportProtocol importContent(String format, String Into,
                                 def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds) {
        //If It cant Import this just quit
        if (!(MapsTo.containsKey(format) && MapsTo.get(format).contains(Into)))
            return null
        if (format.equals("obj") && Into.equals("RandomForestClassifier"))
            return ImportRandomForestClassifier(File, sourceFile.fileName, owner, annon, shared, ip, ds)


    }
    /**
     * Function that Import BED Files to Genetic Peak Table
     * @param File The File Content as String
     * @param Filename The Filename
     * @param owner The User that will be Owner of the Created Data
     * @param annon User Rights
     * @param shared User Rights
     * @return String with errors
     */
    ImportProtocol ImportRandomForestClassifier(
            def File, String Filename, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds = null) throws ValidationException {
        String errors = "";
        Errors Elist

        int count = 0
        if (ds == null)
            ds = new Dataset(fileName: Filename, owner: owner, annon: annon, shared: shared)

        ds.save(failOnError: true)
        ip.setDataset(ds)
        ip.Message("Using Import Function: 'ImportBedFileToPeak'")

        DataRandomForestClassifier rfc = new DataRandomForestClassifier(dataset: ds)


        rfc.setClassifierFile(File.bytes)

        if(rfc.save()){

            ip.successful = true
            ip.ImportEnd()
            def persistedImportProtocol = new PersistedImportProtocol(ip)
            persistedImportProtocol.save()

        }else{

            rfc.discard()
            ip.successful = false
            ip.ImportEnd()

        }


        return ip

    }
}
