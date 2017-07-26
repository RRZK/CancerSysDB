package de.cancersysdb.Importer

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.ImportTools.ImporterServiceInterface
import de.cancersysdb.Dataset
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import de.cancersysdb.data.DataPeak
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.transaction.Transactional
import grails.validation.ValidationException
import org.springframework.validation.Errors

/**
 * This Service is an ImporterService and it manages the Import of BED Files
 */
@Transactional
class BedImporterService implements ImporterServiceInterface {
    /**
     * The Setting which this Importer can Import where
     */
    static Map<String, List> MapsTo = ["bed": ["Peak"]]
    /**
     * No Filename Pattern
     */
    static Map<String, List> FilenamePattern = [:]

    ImportProtocol importContent(String format, String Into,
                                 def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds) {
        //If It cant Import this just quit
        if (!(MapsTo.containsKey(format) && MapsTo.get(format).contains(Into)))
            return null
        if (format.equals("bed") && Into.equals("Peak"))
            return ImportBedFileToGeneticPeak(File, sourceFile.fileName, owner, annon, shared, ip, ds)


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
    ImportProtocol ImportBedFileToGeneticPeak(
            def File, String Filename, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds = null) throws ValidationException {
        String errors = "";
        Errors Elist

        int count = 0
        if (ds == null)
            ds = new Dataset(fileName: Filename, owner: owner, annon: annon, shared: shared)

        ds.save(failOnError: true)
        ip.setDataset(ds)
        ip.Message("Using Import Function: 'ImportBedFileToPeak'")
        int linienCount = 0
        File.toCsvReader(['charset': 'UTF-8', 'separatorChar': '\t', skipLines: 0]).eachLine { tokens ->
            String Chrom = ""
            if (tokens[0].toString().startsWith("chr"))
                Chrom = tokens[0].toString().replace("chr", "")
            else
                Chrom = tokens[0]

            DataPeak temp = new DataPeak(chromosome: Chrom, startPos: tokens[1], endPos: tokens[2], foldEnrichment: 0.0, pValue: 0.0, dataset: ds)
            temp.save(flush: true)
            if (temp.hasErrors()) {
                Elist = Elist?.addAllErrors(temp.errors) ?: temp.getErrors()
                ip.ImportedFailed(temp)
                errors = errors + "\nValidationFailed For Row $linienCount\n" + tokens + "\n"
                if (Elist.errorCount > 10)
                    throw new ValidationException("Import Failed, more than 10 Errors:\n" + errors, Elist)

            } else {
                ip.ImportedSuccessful(temp)
                count++


            }

            linienCount = linienCount + 1
        }
        ip.successful = true
        ip.ImportEnd()
        def persistedImportProtocol = new PersistedImportProtocol(ip)
        persistedImportProtocol.save()
        return ip

    }
}
