package de.cancersysdb.Import

import de.cancersysdb.ImportTools.CSVPreanalyser
import de.cancersysdb.ImportTools.ImportProtocol
import de.cancersysdb.Dataset
import de.cancersysdb.GeneService
import de.cancersysdb.serviceClasses.FiletypeToGeneticStandardMatching
import de.cancersysdb.SourceFile
import de.cancersysdb.User
import de.cancersysdb.geneticStandards.Gene
import de.cancersysdb.serviceClasses.PersistedImportProtocol
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.validation.ConstrainedProperty

import java.lang.reflect.Field

@Transactional
/**
 * This Service Organises The Generic Import of CSV Files to the Database. Therefor the Structure of the CSV File must correspond very close to Database Instance it will be imported to.
 */
class GenericCSVImporterService {
    GrailsApplication grailsApplication
    GeneService geneService
    //    naleid.com/blog/2009/10/01/batch-import-performance-with-grails-and-mysql
    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    static def NAPattern = ~'^(NA|na|n.a|Na|Null|null|NULL)$'

    //These Regex Pattern help to induce the Datatypes from the Importet csv Strings
    static def RegexptoType = [["clazz": Boolean, "Pattern": ~'^((\\+|-)?1|0|\\+|-|(t|T)rue|TRUE|(f|F)alse|FALSE)$'],
                               ["clazz": "Null", "Pattern": NAPattern],
                               ["clazz": Integer, "Pattern": ~'^[\\+]?[0-9]+$'],
                               ["clazz": Long, "Pattern": ~'^[\\+]?[0-9]+$'],
                               ["clazz": Double, "Pattern": ~'^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$'],
                               //Symbols for Human Genes http://www.genenames.org/about/guidelines#genesymbols
                               //and Ensembl and Refseq IDs
                               ["clazz": Gene, "Pattern": ~'^(ENSG|ENST|NM_|XM_|)[0-9]+$'],
                               ["clazz": Gene, "Pattern": ~'^[A-Z0-9]+[A-Z0-9-]?$']
    ]
    static def IgnoredFieldNames = ["version", "id", "dataset"]
    static def DoNotMap = "--Dont Map--"
    static def AutomaticValue = "-Auto_Map-"

    /**
     * This function creates an mapping object of the form input in the HTML-Frontend
     * @param params The params Object
     * @return the mapping Object
     */
    FiletypeToGeneticStandardMatching mappingFromForm(SourceFile file, def params) {
        def temp = file.getImportMapping()
        if (!temp)
            temp = new FiletypeToGeneticStandardMatching();

        //SortedSet<String> fieldsinFile = temp.getFieldsInFile()
        List fields = temp.getFieldsInFile()



        Map mapping = [:]
        Map special = [:]
        params.keySet().findAll {
            it.contains("Mapping_")
        }.each {

            if (!params[it].equals(this.DoNotMap) && !params[it].equals(this.AutomaticValue)) {
                //print params[it]+" "+ fields.indexOf(params[it])

                mapping.put(fields.indexOf(params[it]).toString(), it.substring("Mapping_".length()))
            } else if (params[it].equals(this.AutomaticValue)) {
                special.put(it.substring("Mapping_".length()), this.AutomaticValue)
            }
        }
        //print mapping
        temp.setFieldMapping(mapping)

        temp.setFieldsSpecialValue(special)

        return temp
    }

    /**
     * Performance Glitch see:
     * http://naleid.com/blog/2009/10/01/batch-import-performance-with-grails-and-mysql
     * @return
     */
    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

    /**
     * Import CSV file By mapping.
     * @param csvFile The File TO Import
     * @param mapping The mapping object which maps this file to a data-class
     * @return Import Protocol
     */
    ImportProtocol ImportFile(
            def csvFile, FiletypeToGeneticStandardMatching mapping, SourceFile source, User user, boolean Annon, boolean Shared, ImportProtocol ip, Dataset ds = null) {

        ip.ImportStart()
        ip.Message("Using GenericCSVImporterService")

        //No Dataset Create One
        if (!ds)
            ds = new Dataset(owner: user, annon: Annon, shared: Shared, fileName: source.getOriginalFilename())

        ds.save(failOnError: true,flush:true)
        //Protocoll Init
        ip.setDataset(ds)
        ip.setMatching(mapping)
        //TODO This is CopyPasted and MInimal Code Should work, Check if Works
        //TODO Error Handling

        //Name of Class That the Data Should be Attached to
        def className = mapping.getTargetClass()
        //Grails DomainClass Object
        def cla = grailsApplication.domainClasses.find { it.clazz.simpleName == className }

        //The Class
        Class clazz = cla.clazz

        //Start from First Line if There are no Headings
        int start = 0
        if (mapping.headline)
            start = 1

        ip.Message("mapping: "+ mapping)
        Map<String,String> fromTo = mapping.getFieldMapping()
        Map<String,String> specialValue = mapping.getFieldsSpecialValue()

        Set<String> cols = new HashSet<Integer>()
        cols = fromTo.keySet()

        int counter = 0
        int failed = 0
        //////////////////////Look For Automatic Values
        //This is an Exception for Copy Number Values
        String FixedWindowSizeEndPos = null
        Integer WindowSize = -1

        specialValue.each {
            key, val ->

                if (val.equals(this.AutomaticValue)) {

                    if (key.equals("endPos") && clazz.metaClass.hasProperty(clazz, "Autovalue") && clazz.Autovalue.get(key).get("method") == "fixedWindowSize") {
                        FixedWindowSizeEndPos = clazz.Autovalue.get(key).get("fromField")
                        Integer firstval = -1
                        log.debug("Startpreiterating " + key)

                        BufferedReader r = new BufferedReader(new FileReader(csvFile));
                        String line;
                        String lines = ""
                        int star = 2
                        int end = 10
                        for (int ln = 0; (line = r.readLine()) != null && ln <= end; ln++) {
                            if (ln >= star) {
                                lines += line + "\n"
                            }
                        }
                        lines.toCsvReader(['charset': 'UTF-8', 'separatorChar': mapping.seperator, 'skipLines': start, "quoteChar": mapping.enclose]).eachLine {
                            tokens ->
                                if (WindowSize > 0)
                                    return
                                for (col in cols) {
                                    int colint = Integer.parseInt(col)
                                    String fieldName = fromTo.get(col)
                                    if (fieldName.equals(FixedWindowSizeEndPos)) {
                                        Integer vale = new Integer(tokens[colint])

                                        if (firstval == -1) {
                                            firstval = vale
                                        } else {
                                            WindowSize = vale - firstval - 1
                                        }
                                    }

                                }

                        }

                    }

                }

        }
        if (WindowSize > 0)
            ip.Message("determined window size: " + WindowSize)

        ////////////////////////////////////End Special Values
        List NaAbleFields = getNaAbleFields(clazz)
        log.debug("separatorChar "+ mapping.seperator+"  skipLines "+ start+" quoteChar "+ mapping.enclose )
        csvFile.toCsvReader(['charset': 'UTF-8', 'separatorChar': mapping.seperator, 'skipLines': start, "quoteChar": mapping.enclose]).eachLine {
            tokens ->

                Map initMap = [:]

                for (col in cols) {
                    int colint = Integer.parseInt(col)
                    if (colint > tokens.size()) {
                        ip.Message("Line to short ... content" + tokens.toString())
                        continue
                    }

                    String fieldName = fromTo.get(col)
                    //TODO No such Field Exception
                    Field field
                    try{
                        field = clazz.getDeclaredField(fieldName)
                    }catch(Exception e){
                        ip.Message("no such Field: " + fieldName)
                        ip.setSuccessful(false)
                        return ip
                     }

                    if (field.getType().equals(Gene)) {
                        Gene gene = geneService.getGeneByIdentifier(tokens[colint])
                        if (!gene) {
                            //ip.Message("Gene named: "+tokens[colint] + " not found")

                            geneService.saveNonfoundGeneName( tokens[colint], ds)
                        }
                        initMap.put(fieldName, gene)
                    } else if ("endPos".equals(fieldName) && FixedWindowSizeEndPos) {
                        //Nothing jet just dont do the other thing
                    }else if (field.getType().equals(Float)) {
                        if (tokens[colint] =~ NAPattern && fieldName in NaAbleFields) {
                            initMap.put(fieldName, null)
                        } else
                            initMap.put(fieldName, Float.parseFloat(tokens[colint]))
                    }else if (field.getType().equals(Double)) {
                        if (tokens[colint] =~ NAPattern && fieldName in NaAbleFields) {
                            initMap.put(fieldName, null)
                        } else
                            initMap.put(fieldName, Double.parseDouble(tokens[colint]))
                    } else if (fieldName) {

                        if (tokens[colint] =~ NAPattern && fieldName in NaAbleFields) {
                            initMap.put(fieldName, null)
                        } else
                            initMap.put(fieldName, tokens[colint])
                    }
                }
                if (FixedWindowSizeEndPos && WindowSize > 0) {
                    Integer temp = new Integer(initMap.get(FixedWindowSizeEndPos))
                    initMap.put("endPos", temp + WindowSize)
                }

                initMap.put("dataset", ds)

                def clazzInstance = clazz.newInstance(initMap)

                if (clazzInstance.hasErrors() || !clazzInstance.save()) {
                    failed++
                    ip.ImportedFailed(clazzInstance)
                    clazzInstance.discard()
                } else {
                    ip.ImportedSuccessful(clazzInstance)
                }

                counter++
                if(counter %200==0){
                    //log.debug("clean")
                    cleanUpGorm()
                }
        }

        ip.successful = true

        int missedGenes = geneService.uniqueCountNonfoundGeneNamesForDataset(ds)
        ip.Message("Number if Geneidentifiers which could not be unterstood: " + missedGenes)
        if (missedGenes < 10) {
            def stuff = geneService.uniqueNonfoundGeneNamesForDataset(ds)
            ip.Message("Not unterstood gene Identifiers: " + stuff.toString())
        }

        ip.ImportEnd()
        def persistedImportProtocol = new PersistedImportProtocol(ip)
        persistedImportProtocol.save()
        mapping.save(failOnError: true, flush: true)
        return ip

    }

    /**
     * Get required fields of a domain class
     * @param clazzInstance domain class the required fields should be determined
     * @return List of Fieldnames which are required
     */
    List<String> getRequiredFields(def clazzInstance) {
        List<String> Required = []

        clazzInstance.constraints.each { String key, ConstrainedProperty value ->

            if (value.appliedConstraints.find {
                (it.class.simpleName == "NullableConstraint") && (it.nullable == true)
            }) {

                if (value.appliedConstraints.find {
                    it.class.simpleName == "RequiredForConstraint"
                }) {

                    //atLeastOneRequired = true
                    Required.add(key)
                }
            } else if (!(key in ["application", "dateCreated", "lastUpdated"])) {
                Required.add(key)
            }
        }
        //atLeastOneRequired
        return Required
    }

    /**
     * Get Required the fieleds that are nullable and that do not contain strings. This is important to Interpret "NA" fields and similiar for CSV import
     * @param clazzInstance The domain class the fields should be determined
     * @return List of fieldnames which can be nulled and arn't strings
     */
    List<String> getNaAbleFields(def clazzInstance) {
        List required = getRequiredFields(clazzInstance)
        List checkNA = []

        def dc = grailsApplication.getDomainClass(clazzInstance.name)
        dc.propertyMap.each {
            key, value ->
                if (!(key in required) && !(key in ["application", "dateCreated", "lastUpdated"]) && !(key in IgnoredFieldNames) && !value.equals(String))
                    checkNA.add(key)

        }

        return checkNA
    }

    /**
     * Prepares a matiching that holds the options for parsing of the file.
     * @param clazzToMapTo The class the data should be included to
     * @param csvData data of the CSV-File which should be mapped
     * @param info The Sourcefile which holds other infos about the CSV-File
     * @return Map of Fields(Key) To the candidate fields(Values) from the CSV-File
     */
    Map CreateMatchingCandidates(Class clazzToMapTo, File csvData, SourceFile info) {

        FiletypeToGeneticStandardMatching matching = new FiletypeToGeneticStandardMatching()
        CSVPreanalyser csvpre = new CSVPreanalyser()
        // Keys are the Domain Class Fields and their Classes
        Map mappedStuff = [:]
        //Seperator Handling
        def seperator
        def enclose
        //Process the Domain class that should be Mapped
        def domainclass = grailsApplication.getDomainClass(clazzToMapTo.name)
        //The Testdata which is used to determine the Struct of the CSV File
        String data
        //
        def typedCandidates
        //The Names of the Columns in the CSV File
        List<String> names = []

        matching.setTargetClass(clazzToMapTo.simpleName)

        //Extract test Lines for Analysis
        csvpre.Preanaylse(csvData)

        //Determine Seperator
        if (matching.seperator)
            seperator = matching.seperator
        else
            seperator = csvpre.getSeperator()           //seperator = determineSeperator(testdata)
        matching.setSeperator(seperator)
        //Determine etc
        enclose = csvpre.getEnclose()
        matching.setSeperator(seperator)
        matching.setEnclose(enclose)
        domainclass.propertyMap.each {
            key, value ->
                if (!IgnoredFieldNames.contains(key)) {
                    mappedStuff.put(value.name, value.type)

                }
        }

        //Retrive Required Fields
        def reqFields = getRequiredFields(domainclass)
        //Get Alle Possible Candidates For the Fields and Their Occurences
        ArrayList Occs = infereCandidatesForFields(csvData, seperator, matching, names)

        //////////////////SIMPLIFIED VERSION for Possible Debug. Same Result diffrend Sorting
        //Get the Final Mapping of Field in Database (Key) to List of Possible Columns in CSV File

/*        typedCandidates = [:]
        mappedStuff.each{ DatabaseField, DatabaseFieldClass ->
            def things=[]
            Occs.eachWithIndex{
                def entry, int listfieldIndex ->
                    entry.each { listfieldclass, listfieldFitNumber ->

                        if(listfieldclass.equals(DatabaseFieldClass)){
                            things.add(names.get(listfieldIndex))

                        }
                    }
            }
            typedCandidates.put(DatabaseField,things)

        }
        print typedCandidates
        matching.fieldsInFile=[]
        names.each {
            matching.fieldsInFile.add(it)
        }
        print typedCandidates
        */
        ////////////////////More Complex version with Complex Sorted Results!

        typedCandidates = createFinalMatchingWithRanking(matching, names, mappedStuff, clazzToMapTo, Occs)

        //Add Blank Field For non Required Fields
        //Add the Do not Map Option if a Field is not Requied in the Dataset Type

        typedCandidates.each {
            key, value ->
                if (!(key in reqFields))
                    value.add(this.DoNotMap)

                if ("endPos".equals(key)) {
                    value.add(this.AutomaticValue)
                }
        }
        matching.fieldTypes = [:]

        //Save the Matching
        matching.save(failOnError: true)
        //Save the Matching to the File
        info.setImportMapping(matching)
        info.save(failOnError: true)
        //Return the Typed Candidates
        return typedCandidates

    }

    /**
     * This Method creates a Ranking of Fields in the File
     * @param matching The matching domain object which should hold the information
     * @param names The names of the columns
     * @param mappedStuff
     * @param clazzToMapTo  class the information should be mapped to
     * @param Occs occurrences of the datatype a value in a column can get
     * @return The Hash Map keys fieleds ind the database values -> Lists of fields in the file to be imported.
     */
    private LinkedHashMap createFinalMatchingWithRanking(FiletypeToGeneticStandardMatching matching, ArrayList<String> names, Map mappedStuff, Class clazzToMapTo, ArrayList Occs) {
        def typedCandidates
        List<Map> MatchHeadline = new ArrayList<Map>(names.size())



        for (i in 0..names.size())
            MatchHeadline[i] = [:]
        //Whith Headline in Import File

        if (matching.headline) {
            names.eachWithIndex {
                String entry, int i ->
                    def keys = mappedStuff.keySet()
                    keys.each {
                        it ->
                            int score = MatchHeadlines(entry, it)

                            def temp = MatchHeadline[i] ?: [:]

                            temp.put(it, score)
                            MatchHeadline[i] = temp

                    }
            }
            typedCandidates = typeCandidates(clazzToMapTo, mappedStuff, Occs, names, MatchHeadline)


            names.each {
                matching.addToFieldsInFile(it)
            }
            //Without Headline in Import File
        } else {
            names.clear()
            //Set Names for Fields with Field Description
            Occs.eachWithIndex {
                val, index ->
                    names.add("Field" + (index + 1))
            }
            typedCandidates = typeCandidates(clazzToMapTo, mappedStuff, Occs, names)
            names.each {
                matching.addToFieldsInFile(it)
            }
            matching.headline = false
        }
        typedCandidates
    }

    /**
     * Determine with the Regular Expressions which kind of Value is in a Field
     * @param csvData The Data to Infere the Possible Filed Types from
     * @param seperator The Seperator of csvData
     * @param matching The Matching
     * @param names The Names of the Headlines
     * @return Returns an Array how Often an Column in the CSV File fitted the Data Requirements [[Class String : 98, class int: 50  ]] etc
     */
    private ArrayList infereCandidatesForFields(File csvData, String seperator, matching, names) {
        boolean firstLine = true
        def Occs = []
        csvData.toCsvReader(['charset': 'UTF-8', 'separatorChar': seperator, quoteChar: matching.enclose]).eachLine {
            tokens ->
                //Determine if this is a Headline
                if (firstLine) {
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
                    //Process the Rest NOT the headline
                } else {
                    //Process each Token and Count the Occurences of Patterns on Fields
                    tokens.eachWithIndex { a, i ->

                        def temp = Occs[i] ?: [:]
                        def name = String.class
                        boolean atleastOneMatched = false
                        for (reftotype in RegexptoType) {

                            if (a ==~ reftotype.Pattern) {

                                name = reftotype.clazz

                                if (temp[name])
                                    temp[name] = temp[name] + 1
                                else
                                    temp[name] = 1
                                Occs[i] = temp

                                atleastOneMatched = true
                            }
                        }
                        if (!atleastOneMatched) {
                            if (temp[name])
                                temp[name] = temp[name] + 1
                            else
                                temp[name] = 1
                            Occs[i] = temp


                        }

                    }
                }
        }
        Occs
    }
    /**
     * Creates String Errors for the Mapping Candidates
     * @param typedCandidates The Candidates and The Values
     * @return A Set of things that Describe the Problems
     */
    String errorCommentTypedCandidates(Map typedCandidates) {

        String out = ""
        int reqCounter = 0
        Set All = new HashSet()
        typedCandidates.each {
            key, value ->
                if (value.size() == 0)
                    out += "Cant Map Field $key No Suitable Field in Input File\n"
                else if (!value.contains(DoNotMap))
                    reqCounter++
                All.addAll(value)

        }
        if (All.contains(DoNotMap))
            All.remove(DoNotMap)

        if (All.size() < reqCounter)
            out += "Not Enough Fields in Input File to Fill all Required Fields\n"

        return out;
    }

    /**
     * Match two Headlines if they could mean the same . The Result is an Integer Value whicht expresses the "sameness"
     * @param one The Frist Headline
     * @param two The Second Headline
     * @return An Int that shows the Similarity of Strings
     */
    int MatchHeadlines(String one, String two) {
        def oneElements = []
        def twoElements = []
        one.tokenize().each {
            that1 ->
                that1.split("(?<=[a-z])(?=[A-Z])").each {
                    that2 ->
                        that2.tokenize("_").each {
                            that3 ->
                                oneElements.add(that3.toLowerCase())
                        }

                }

        }

        two.tokenize().each {
            that1 ->
                that1.split("(?<=[a-z])(?=[A-Z])").each {
                    that2 ->
                        that2.tokenize("_").each {
                            that3 ->
                                twoElements.add(that3.toLowerCase())
                        }

                }

        }
        int score = 0

        oneElements.each {
            a ->
                twoElements.each {
                    b ->
                        if (a.equals(b))
                            score += 10
                        else {
                            if (a.length() > b.length()) {
                                if (a.contains(b))
                                    score += 2
                            } else {
                                if (b.contains(a))
                                    score += 2
                            }
                        }
                }

        }
        return score

    }

    /**
     * Creates a Suggestion list from The Parameters
     * @param mappedStuff The Stuff that is Mapped
     * @param Occs List of Classes with their Occurences With
     * @param names The Names of the Fields of The CSV File
     * @return Map Keys are the Fields of the Domainclass , Values are Lists of Columns in the CSV File
     */
    Map typeCandidates(Class tomapto, Map mappedStuff, List Occs, List<String> names, List<Map> aa = null) {
        Map TypedCandidates = [:]
        def reqFields = getRequiredFields(tomapto)


        mappedStuff.each {
                //databaseFieldName: Name of the Field in the Database, databaseFieldClass: class of the Field in the Database
            databaseFieldName, databaseFieldClass ->
                TypedCandidates.put(databaseFieldName, [:])
                Occs.eachWithIndex {
                        //v2: Map of Matching Types with Occurences as Values, Index: is the Index in Occs
                    v2, index ->
                        v2.each {
                                //ClassCandidate: Class the found classes, CandidateMatches: the occurences of the Class
                            ClassCandidate, CandidateMatches ->

                                if (databaseFieldClass.equals(ClassCandidate) || databaseFieldClass.equals(String.class) || (ClassCandidate == "Null" && !(databaseFieldName in reqFields))) {
                                    int score = 0
                                    if (CandidateMatches > 999)
                                        score = 4
                                    else if (CandidateMatches > 980)
                                        score = 3
                                    else if (CandidateMatches > 700)
                                        score = 2
                                    else if (CandidateMatches > 500)
                                        score = 1
                                    if (ClassCandidate == "Null" && CandidateMatches > 20) {
                                        if (!(databaseFieldName in reqFields))
                                            score += 3
                                        else
                                            score = 0
                                    }
                                    if (aa) {
                                        def temp = aa.getAt(index)
                                        if (temp) {
                                            temp.each {
                                                key, val ->
                                                    if (key.equals(databaseFieldName))
                                                        score += val
                                            }
                                        }

                                    }

                                    if (databaseFieldName && names.getAt(index))
                                        TypedCandidates.get(databaseFieldName).put(names.getAt(index), score)

                                }
                        }

                }

        }
        def out = [:]
        TypedCandidates.each {
            key, value ->

                def temp = value.sort { a, b -> b.value <=> a.value }.keySet();
                def list = []
                list.addAll(temp)
                out.put(key, list)

        }
        return out
    }

}