package de.cancersysdb.data

import de.cancersysdb.Dataset
import de.cancersysdb.User
import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.commons.GrailsApplication
import de.andreasschmitt.export.ExportService
import de.cancersysdb.data.SingleLineDataset
import de.cancersysdb.data.BinaryDataDataset
/**
 * This Controller manages the exports of the genetic data from the Database.
 * In contrast to the other data in the database it covers data which is original table-based genetic readings and their direct filters.
 * The controller enables to export with a type from the Package data
 * @author Rasmus Krempel
 */
@Secured("isFullyAuthenticated()")
class ExportController {

    ExportService exportService
    GrailsApplication grailsApplication
    def springSecurityService

    /**
     *
     * @param type The classname from the data package
     * @param ds The dataset id of the associated genetic Data
     * @param format Format of the output
     * @return The formatted genetic data.
     */
    def export(String type, Integer ds,String format) {
        log.debug( "type: "+ type)
        log.debug( "Dataset ID : "+ ds)

        if(!ds){
            render status: "404", text: "Dataset ID not given"
            return
        }
        Dataset dataset = Dataset.findById(ds)


        if( !dataset){
            render status: "404", text: "Dataset not found"
            return
        }

        User u = springSecurityService.getCurrentUser()
        if((u && u.equals(dataset.owner) )|| (dataset.annon && dataset.shared)) {
        }else{
            render status: "401", text: "Permission Denied"
            return
        }
        //Upptercase because all Classes start like this!
        type = type[0].toUpperCase() + type[1..-1]
        def importantclass = grailsApplication.domainClasses.find { it.clazz.package.name == "de.cancersysdb.data" &&  it.clazz.simpleName == type }

        if(!importantclass){
            render status: "404", text: "Datatype not found"
            return
        }

        //print params.format
        if(!format ){
            if( !params.format)
                format = "csv"
            else
                format = params.format
        }
        //Define List of Fields for the Importer
        def fieldstoExport =[]
        def NamestoExport =[:]

        if(SingleLineDataset.isAssignableFrom(importantclass.clazz) &&BinaryDataDataset.isAssignableFrom(importantclass.clazz) ) {
            BinaryDataDataset res = importantclass.clazz.findAllByDataset(dataset).first()


            response.setContentType("application/octet-stream")
            String fname = ""+importantclass.clazz.simpleName +dataset.id +".obj"
            log.debug(fname)

            response.setHeader("Content-disposition", "attachment; filename=$fname")
/*            exportFile( ,response)
            String type, HttpServletResponse response, String filename, String extension, byte[] payload*/
            response.outputStream << new ByteArrayInputStream( res.getData())

            [res.getData()]

        }else {
                importantclass.clazz.declaredFields.each {
                    stuff ->

                        if (!stuff.synthetic && !(stuff.toString().contains("static") || stuff.toString().contains("transient")) && !(stuff.name.equals("errors") || stuff.name.equals("version"))) {

                            fieldstoExport.add(stuff.name)
                            NamestoExport.put(stuff.name, stuff.name)
                        }
                }
                fieldstoExport.remove("dataset")
                NamestoExport.remove("dataset")

                //export as CSV
                response.contentType = grailsApplication.config.grails.mime.types[params.format]

                response.setHeader("Content-disposition", "attachment; filename=$type$ds.${params.extension}")
                def res = importantclass.clazz.findAllByDataset(dataset)

                exportService.export(format, response.outputStream, res, fieldstoExport, NamestoExport, [:], [:])
                log.debug("export result" + res)
                [res]
            }
        }



    /**
     * Export to CSV
     * @param type The classname from the data package
     * @param ds The dataset id of the associated genetic Data
     * @param format Format of the output
     * @return The formatted genetic data.
     */
    def exportCSV() {
        log.debug("CSV Export")
        log.debug("Type= "+ type)
        log.debug("Dataset= "+ ds)

        if(!ds){
            render status: "404", text: "Dataset id not given"
            return
        }
        Dataset dataset = Dataset.findById(ds)


        if( !dataset){
            render status: "404", text: "Dataset not found"
            return
        }

        User u = springSecurityService.getCurrentUser()
        if((u && u.equals(dataset.owner) )|| (dataset.annon && dataset.shared)) {
        }else{
            render status: "401", text: "Permission Denied"
            return
        }
        //Upptercase because all Classes start like this!
        type = type[0].toUpperCase() + type[1..-1]
        def importantclass = grailsApplication.domainClasses.find { it.clazz.package.name == "de.cancersysdb.data" &&  it.clazz.simpleName == type }

        if(!importantclass){
            render status: "404", text: "Datatype not found"
            return
        }
        //print params.format
        if(!format ){
            if( !params.format)
                format = "csv"
            else
                format = params.format
        }
        //Define List of Fields for the Importer
        def fieldstoExport =[]
        def NamestoExport =[:]
        importantclass.clazz.declaredFields.each {
            stuff->

                if(!stuff.synthetic &&! (stuff.toString().contains("static") || stuff.toString().contains("transient")) && !(stuff.name.equals("errors")||stuff.name.equals("version"))){

                    fieldstoExport.add(stuff.name)
                    NamestoExport.put(stuff.name,stuff.name)
                }
        }
        fieldstoExport.remove("dataset")
        NamestoExport.remove("dataset")


        //export as CSV
        response.contentType = grailsApplication.config.grails.mime.types[params.format]

        response.setHeader("Content-disposition", "attachment; filename=$type$ds.${params.extension}")
        def res = importantclass.clazz.findAllByDataset(dataset)

        res= exportService.export(format , response.outputStream,res,fieldstoExport,NamestoExport,[:],[:] )

        [ res ]

    }



}
