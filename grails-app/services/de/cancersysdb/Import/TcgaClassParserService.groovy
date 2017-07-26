package de.cancersysdb.Import

import de.cancersysdb.geneticStandards.TCGAClassObject
import grails.transaction.Transactional
import grails.validation.ValidationException

@Transactional
/**
 * This Service Manages the Import of the TCGA Controlled Vocabluraries
 */
class TcgaClassParserService {
    /**
     * This Service Helps to Read in Controlled Vocabularies from the TCGA!
     * @param stream CSV File with the Data Stuff
     * @return Is Sucessfull
     */
    boolean serviceMethod(stream) {

        /**
         * Must Correspond to TCGAClassObject datatypes=["cancer","sampleType","tissueType"]
         */
        def HealineIdentification = []
        def TypeMapping = []

        HealineIdentification[0] = ["SNOMED Code","Study Abbreviation", "Study Name"]
        TypeMapping[0] = ["code","abbreviation", "name"]

        HealineIdentification[1] = ["Code", "Definition", "Short Letter Code"]
        TypeMapping[1] = ["code", "name", "abbreviation"]

        HealineIdentification[2] = ["Tissue"]
        TypeMapping[2] = ["name"]

        boolean Firstline = true;
        Integer ParseType = -1;

        try {
            stream.toCsvReader(['charset': 'UTF-8', 'separatorChar': ',', skipLines: 0]).eachLine { tokens ->
                if (Firstline) {
                    def AllOk = true
                    for (Integer Head = 0; Head != HealineIdentification.size(); Head++) {
                        AllOk = true

                        if (HealineIdentification[Head].size() == tokens.size()) {

                            for (int i = 0; i < HealineIdentification[Head].size(); i++) {
                                if (!HealineIdentification[Head][i].equals(tokens[i])) {
                                    AllOk = false
                                    break
                                }


                            }

                        } else {

                            AllOk = false
                        }
                        if (AllOk) {

                            ParseType = Head
                            break
                        }

                    }

                    if (ParseType < 0) {
                        Firstline = false
                        return
                    } else {
                        eraseByType(ParseType)
                        Firstline = false
                    }
                } else {

                    Map temp = [:]
                    for (int i = 0; i < TypeMapping[ParseType].size(); i++) {


                        temp.put(TypeMapping[ParseType][i], tokens[i])


                    }
                    TCGAClassObject tcgaO = new TCGAClassObject()
                    tcgaO.properties = temp
                    tcgaO.setType(ParseType)
                    tcgaO.save(failOnError: true)

                }

            }
        } catch (ValidationException e) {
            return e.message

        } catch (Exception e) {
            return e.message

        }


    }
    /**
     * Erease ExistingFiles
     * @param type The Type of TGA Controlled Vocab
     * @return Succsess or Not?!
     */
    boolean eraseByType(Integer type) {
        TCGAClassObject.findAllByType(type).each { it.delete() }
    }
}
