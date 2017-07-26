package de.cancersysdb.geneticStandards

import de.cancersysdb.GeneticHelpers.GeneticPosition
import de.cancersysdb.GeneContextService
import de.cancersysdb.Import.GeneImportService
import de.cancersysdb.GeneService
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class GeneController {
    /**
     * TODO Comment
     * TODO Remove Unsued Functions
     */
    GeneImportService geneImportService
    GeneService geneService
    GeneContextService geneContextService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        //Rewrite for Transient Fields and Sorts
        if(Gene.transients.contains(params["sort"])){
            if(params["sort"] == "ensemblID")
                params["sort"]="ensembl"
            else if(params["sort"] == "ncbiID"){
                params["sort"]="ncbi"
            }

            else if(params["sort"] == "transcriptOf"){
                params["sort"] = "transcriptionOfGene"
            }

        }

        respond Gene.list(params), model:[geneInstanceCount: Gene.count()]
    }

    def show(Gene geneInstance) {
        if(!geneInstance){
            //Works
            response.sendError(404,"Gene Identified By Key Not Found")
            return
            //respond null, [status: 404, message: 'error404']

        }

        def Contexts = geneContextService.getHumanReadableContextforGene(geneInstance)
        render view: "/gene/show", model: [geneInstance:geneInstance, contexts:Contexts ]


    }
    def getByPosition() {
        //Required parameters!
        if(!params.containsKey("startPos")|| !params.containsKey("endPos")||!params.containsKey("chromosome") ){
            render message:"MissingParameter", status: 404
            return
        }
        GeneticPosition gp = new GeneticPosition( )
        gp.startPos = Integer.parseInt( params.startPos)
        gp.endPos = Integer.parseInt(params.endPos)
        gp.chromosome = params.chromosome


        def genes = geneService.getGenesByGeneticPosition(gp)

        if(genes.size()==0)
            render text: "No Genes in This Position"
        else if(genes.size()==1)
            render template: "/gene/preview", model: [gene:genes.get(0)]
        else{
            String id =""
            genes.each {id+= it.id}
            render template: "/gene/previewCollection", model: [genes:genes,idident:id,originalSpan:gp]
        }
        return
    }

    def showByName(String geneName) {
        def geneInstance = geneService.getGeneByIdentifier(geneName)
        if(geneInstance) {
            show(geneInstance)
            return
        }else {
            flash.message = "Cannot Find Gene with Identifier $geneName"
            redirect(action: "index")
            return
        }
    }


/*    def create() {
        respond new Gene(params)
    }*/
    @Transactional
    @Secured(["ROLE_ADMIN"])
    def uploadFile() {
        def f

        try {
            f = request.getFile('file')
        }catch(e){
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }
        if (f.empty) {
            flash.message = 'file cannot be empty'
            redirect(action: "index")
            return
        }

        def stream
        stream = f.getInputStream()
        def outrerror = geneImportService.importGeneTablefromCSV(stream)

        if(outrerror ) {

            flash.message = message(message:" Error while importing Samples from Uploaded File"+outrerror.toString())
            render(view: 'index')
        }else {

            flash.message = message(message:" Succsessfull imported  Gene Data")
            render(view: 'index')
        }
    }


    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gene.label', default: 'Gene'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}
