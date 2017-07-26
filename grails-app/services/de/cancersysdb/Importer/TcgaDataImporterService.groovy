package de.cancersysdb.Importer

import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.ImportTools.ImporterServiceInterface
import de.cancersysdb.Dataset
import de.cancersysdb.Import.GenericCSVImporterService
import de.cancersysdb.serviceClasses.FiletypeToGeneticStandardMatching
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import de.cancersysdb.data.DataTranscriptAbundance
import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class TcgaDataImporterService implements ImporterServiceInterface {

    static Map<String, List> MapsTo = ["TCGAtagq": ["TranscriptAbundance"]]
    static Map<String, List> FilenamePattern = ["TCGAtagq": [/^.*\.trimmed\.annotated\.gene\.quantification\.txt$/]]
    GenericCSVImporterService genericCSVImporterService

    ImportProtocol importContent(String format, String Into,
                                 def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds) {
        //If It cant Import this just quit
        if (!(MapsTo.containsKey(format) && MapsTo.get(format).contains(Into)))
            return null
        if (format.equals("TCGAtagq") && Into.equals("TranscriptAbundance"))
            return ImportTCGAtagqToTranscriptAbundance(File, sourceFile, owner, annon, shared, ip, ds)


    }

    def ImportTCGAtagqToTranscriptAbundance(
            def file, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip = null, Dataset ds = null) {


        FiletypeToGeneticStandardMatching temp = FiletypeToGeneticStandardMatching.findByAuthorityName("TCGAtagqTOTranscriptAbundance")
        //TODO This is Ugly! Move this to a general Adress
        if (!temp) {
            def Content = JSON.parse(
                    """{"headline":true,
                        "authorityName":"TCGAtagqTOTranscriptAbundance",
                       "fieldsInFile": ["gene","raw_counts","median_length_normalized","RPKM"],
                       "fieldTypes":{"gene":"Gene","raw_counts":"Integer","median_length_normalized":"Float","RPKM":"Float"},
                       "fieldMapping":{"0":"gene","3":"fpkm"}
                       }""")

            temp = new FiletypeToGeneticStandardMatching(Content)
            temp.setTargetClass(DataTranscriptAbundance.class.getSimpleName())
            temp.setSeperator("\t")
            if (!temp.save()) {
                log.debug(temp.errors)
                ip.successful = false
                ip.Message("Error occurred")
                return ip
            }
        }

        ip = genericCSVImporterService.ImportFile(file, temp, sourceFile, owner, annon, shared, ip, ds)
        return ip

    }

}
