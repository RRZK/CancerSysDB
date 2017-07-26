package de.cancersysdb

import de.cancersysdb.data.DataVariation
import de.cancersysdb.data.DataVariationAnnotation
import grails.test.spock.IntegrationSpec

class DatasetServiceIntegrationSpec extends IntegrationSpec {
    DatasetService datasetService
    def setup() {
    }

    def cleanup() {
    }

    void "test Annotation Recognition"() {
        setup:
        DataVariation temp = new DataVariation()
        DataVariationAnnotation tempAnnotation = new DataVariationAnnotation()
        when:
        def AnnotationClass = datasetService.getAnnotationClassFor(temp.class)
        def tempClass = datasetService.getDataClassForName(temp.class.simpleName)

        then:
        AnnotationClass.equals(tempAnnotation.class)
        tempClass.equals(temp.class)

    }
    void "test simple Field Recognition"() {
        setup:
        DataVariation temp = new DataVariation()
        when:
        def fields = datasetService.getFieldNamesForClass(temp.class.simpleName)
        then:
        fields
        fields.size()>0
        fields.contains("chromosome")
        fields.contains("startPos")
        fields.contains("endPos")

    }



}
