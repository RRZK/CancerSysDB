package de.cancersysdb

import de.cancersysdb.GeneticHelpers.GeneDataStatistics
import de.cancersysdb.data.BinaryDataDataset
import de.cancersysdb.data.SingleLineDataset
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty

/**
 * This Service Manages the Connection between the Structural Data,
 * the Dataset Class and the Payload Data, the Acutal Measurements
 */

@Transactional
class DatasetService {
    GrailsApplication grailsApplication
    /**
     * Everything from the Package Data
     */
    def dataclasses = []

    /**
     * Things which are Referenceable By Dataset
     */
    def importantclasses = []
    static String dataClassPrefix = "Data"
    /**
     * Get the Lines of Genetic Data Availible for this Dataset as Map
     * @param ds The Dataset you want to Collect the Data Quantities of Occurences
     */

    Map getDataCount(Dataset ds) {
        def temps = [:]
        if (importantclasses.isEmpty())
            this.getDataClasses()
        for (importantClass in importantclasses) {
            def temp = importantClass.countByDataset(ds)
            if (temp > 0)
                temps[importantClass.simpleName] = temp
        }
        return temps
    }
    /**
     * Get the Types of Data Availible for this Dataset as List
     * @param ds The Dataset you want to get the Infos for
     */
    List getDataTypesInDataset(Dataset ds) {
        def temps = []
        if (importantclasses.isEmpty())
            this.getDataClasses()
        for (importantClass in importantclasses) {
            def temp = importantClass.findByDataset(ds)
            if (temp)
                temps.add(importantClass.simpleName)
        }
        return temps
    }

    /**
     * Get the Types of Data Availible for this Dataset as List
     * @param ds The Dataset you want to get the Infos for
     */
    String getDataTypeOfDataset(Dataset ds) {
        if (importantclasses.isEmpty())
            this.getDataClasses()
        for (importantClass in importantclasses) {
            def temp = importantClass.findByDataset(ds)
            if (temp)
                return importantClass.simpleName
        }
        return ""
    }

    /**
     * Get the Data Availible for this Dataset as Map
     * @param ds The Dataset you want to Collect the Data for
     * @return Returns a Map Key is the Data Type, The Value are the Data Objects
     */
    Map getData(Dataset ds) {
        def temps = [:]
        if (importantclasses.isEmpty())
            this.getDataClasses()

        for (importantClass in importantclasses) {
            //TODO NOT NICE! Thsi is a real Flaw!
            def temp = importantClass.findAllByDataset(ds, [max: 10000])
            if (temp)
                temps[importantClass.simpleName] = temp
        }
        return temps
    }
    /**
     * Get the Data Availible for this Dataset as Map
     * @param ds The Dataset you want to Collect the Data for
     * @return Returns a Map Key is the Data Type, The Value are the Data Objects
     */
    Map getNData(Dataset ds) {
        def temps = [:]
        if(importantclasses.isEmpty())
            this.getDataClasses()

        for(importantClass in importantclasses ){
            //TODO NOT NICE! This is a real Flaw!
            def Num =importantClass.countByDataset(ds)
            if(Num)
                temps[importantClass.simpleName]= Num
        }
        return temps
    }
    /**
     * Get the Classes for this Dataset
     * @param ds The Dataset you want to Collect the Data for
     * @return List of Important Classes used here
     */
    List getAvailibleData(Dataset ds) {
        def out = []
        if(importantclasses.isEmpty())
            this.getDataClasses()

        for(importantClass in importantclasses ){
            //TODO NOT NICE! This is a real Flaw!
            def Num =importantClass.countByDataset(ds)
            if(Num)
                out.add(importantClass)
        }
        return out
    }

    /**
     *
     * @param ds Dataset to get the Statistics for
     * @return Map of Statistics
     */
    Map getGetGenticDataStatistics(Dataset ds) {
        def datas = getAvailibleData(ds)
        Map out =[:]


        datas.each{ importantClass->
            def Num =importantClass.countByDataset(ds)

            out.put(importantClass.simpleName,["Count":Num, "IsBinData": SingleLineDataset.isAssignableFrom(importantClass) ,"IsSingleData": BinaryDataDataset.isAssignableFrom(importantClass)])

        }

        return out
    }


    /**
     *
     * @param ds Dataset to get the Statistics for
     * @return Map of Statistics
     */
    Map getGetOldGenticDataStatistics(Dataset ds) {
        def datas = getData(ds)
        Map out = [:]


        datas.each { key, datasets ->

            def kk = this.getDataClassForName(key)

            GeneDataStatistics gs = new GeneDataStatistics()
            def fnt = this.getFieldNamesandTypesForClass(kk)
            gs.setFieldsToTypes(fnt)
            gs.startSession()
            boolean Complete = true

            int counter = 0
            datasets.each {
                it ->
                    gs.putObject(it)
                    counter++
            }
            if (counter == 10000)
                Complete = false
            gs.calculateStatistics()


            out.put(key, ["Fields": gs.getStatisticByField(), "Count": gs.getnCases(), complete: Complete])

        }

        return out
    }

    /**
     * Retrive All the Classes asociated to Special Datasets
     */

    private void getDataClasses() {
        dataclasses = []
        importantclasses = []

        def ic = grailsApplication.domainClasses.findAll { it.clazz.package.name == "de.cancersysdb.data" }.clazz
        for (importantClass in ic) {
            dataclasses.add(importantClass)
            if (grailsApplication.getDomainClass(importantClass.name).getPersistantProperties().find {
                it.name.equals("dataset")
            })
                importantclasses.add(importantClass)
        }
    }

    /**
     * Returns the Class object for a Named Class
     * @param name The Name of The Data Class
     * @return The Class objkect of the Data class
     */
    Class getDataClassForName(String name) {
        if (dataclasses.isEmpty())
            this.getDataClasses()

        if (!name.startsWith("Data"))
            name = "Data" + name
        def temp = dataclasses.find {
            Class it ->
                name.equals(it.simpleName) || name.equals(it.name)

        }

        if (temp)
            return temp
        else
            return null

    }
    /**
     *
     * @param name name of the Domain Class wich should be Retained
     * @return Domain Class of the named thing
     */
    GrailsDomainClass getDomainDataClassForName(String name) {
        return getDomainDataClassForName(getDataClassForName(name))

    }
    /**
     *
     * @param name Class of the Domain Class wich should be Retained
     * @return Domain Class of the named thing
     */
    GrailsDomainClass getDomainDataClassForName(Class claaas) {
        return grailsApplication.getDomainClass(claaas.name)
    }

    /**
     * Returns the AnnotationClass for a Data Annotation Class
     * @param klass This is the Class to get the Annotationclass for
     * @return The Annotationclass
     */
    Class getAnnotationClassFor(klass) {
        if (klass == null)
            return null
        if (klass.class.equals(String.class)) {

            klass = this.getDataClassForName(klass)
        }
        String annoname
        Map ExsistsField = GrailsClassUtils.getStaticPropertyValue(klass, "GeneticDataContext")

        if (ExsistsField && ExsistsField.containsKey("hasAnnotation")) {
            annoname = klass.GeneticDataContext["AnnotationTable"]
        } else {
            return null
        }
        //print "Annotationname : "+ annoname

        def out = getDataClassForName(annoname)
        return out
    }

    /**
     * Get All Fields of Interest as Map From Fields to Types of Fields as Classes
     * @param klass The Class to find the Stuff for
     * @return Map of FieldName(String)-> FieldType(class)
     */

    Map getFieldNamesandTypesForClass(klass) {

        if (klass == null)
            return null
        if (klass.class.equals(String.class)) {

            klass = this.getDataClassForName(klass)
        }
        Map out = [:]
        grailsApplication.getDomainClass(klass.name).getPersistantProperties().each {
            k ->
                def name = k.getName()
                out.put(name, k.getType())
        }
        return out
    }
    /**
     * Get All Fields of Interest of a class as List of String
     * @param klass
     * @return
     */

    List<String> getFieldNamesForClass(klass) {
        if (klass == null)
            return null
        if (klass.class.equals(String.class)) {

            klass = this.getDataClassForName(klass)
        }
        List out = []
        grailsApplication.getDomainClass(klass.name).getPersistantProperties().each {
            k ->
                def name = k.getName()
                out.add(name)
        }
        return out
    }
    /**
     * Get All required Fields of a class as List of String
     * @param klass Class to get the fields for
     * @return List of String containing the required Fields.
     */

    List<String> getRequiredFieldNamesForClass(klass) {
        if (klass == null)
            return null
        List fields = getFieldNamesForClass(klass)

        if (fields.empty)
            return null
        List<String> out = []
        klass.constraints.each { String key, ConstrainedProperty value ->
            if (key in fields) {

                if (value.appliedConstraints.find {
                    (it.class.simpleName == "NullableConstraint") && (it.nullable == true)
                }) {

                } else
                    out.add(key)
            }
        }
        return out
    }
    /**
     * Returns the field-name in the annotation dataset which is used to Connect the Annotation to the Core-Dataset
     * @param geneticDataClass The class of The core-Dataset
     * @param annoklass The Annotation Class
     * @return Returns the string identifier for the Field in The Annotation Class
     */
    String getFieldLinkFieldForAnnotation(geneticDataClass, annoklass) {

        if (geneticDataClass.GeneticDataContext["AnnotationTable"].equals(annoklass.simpleName))
            return geneticDataClass.GeneticDataContext["AnnotationForeignKey"]
        else
            return null
    }

    /**
     * Returns a List of Strings wich Returns A List of All  Data Classes
     * @return List of All Dataset class without the Prefix "Genetic"
     */
    List<String> getDataClassNames() {

        if (importantclasses.isEmpty())
            this.getDataClasses()
        def out = importantclasses.collect {

            it.simpleName.substring(dataClassPrefix.length())

        }
        return out
    }
    /**
     * Returns a List of Maps with of all genetic data classes, as string
     * @return List of All Genetic dataset Class without the prefix "Genetic"
     */
    List<Map> getDataClassNamesAsMaps() {
        List<Map> out = []
        if (importantclasses.isEmpty())
            this.getDataClasses()

        importantclasses.each {
            it ->
                out.add(["key": it.simpleName.substring(dataClassPrefix.length()), "label": it.simpleName.substring(dataClassPrefix.length())])
        }

        return out
    }

    /**
     * Produces a Map of Datatypes and Lists
     * @param ds
     * @return
     */
    Map getNumericDataFieldsByTypeByDataset(Dataset ds, boolean fullnames = false) {
        Map out = [:]

        def Datatypes = this.getDataTypesInDataset(ds)
        Datatypes.each { dataype ->
            def dt = getDataClassForName(dataype)
            //if(GenPosInterface.isAssignableFrom(dt)){
            Map fieldsandTypes = this.getFieldNamesandTypesForClass(dt)

            out[ShortenNameOfClass(dataype)] = []
            fieldsandTypes.each {
                key, value ->
                    if (Number.class.isAssignableFrom(value)) {
                        if (fullnames)
                            out[dataype].add(key)
                        else
                            out[ShortenNameOfClass(dataype)].add(key)
                    }
            }
            //}

        }
        return out

    }

    /**
     * Shorten the Name of the given Class name
     * @param toShorten The name that should be Shortend.
     * @return the short name
     */
    String ShortenNameOfClass(String toShorten) {

        String out = toShorten.substring(dataClassPrefix.length())

        return out
    }

}
