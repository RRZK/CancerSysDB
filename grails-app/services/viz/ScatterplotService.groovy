package viz

import de.cancersysdb.GeneticHelpers.GenPosInterface
import de.cancersysdb.GeneticHelpers.GeneticPosition
import de.cancersysdb.Dataset
import de.cancersysdb.DatasetService
import de.cancersysdb.gegenticViz.InteractiveScatterplotViz
import de.cancersysdb.gegenticViz.ScatterplotDot
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

@Transactional
class ScatterplotService {
    DatasetService datasetService
/**
 *
 * @param das1 Dataset ID
 * @param das2 Dataset 2 ID, if empty Everything is Taken from Dataset1
 * @param TypeX The Type of the X-axis Field
 * @param FieldX The Field which acts as Souce for the X-Axis Variable
 * @param TypeY The Type of the Y-axis Field
 * @param FieldY The Field which acts as Souce for the Y-Axis Variable
 * @param skipMissing boolean Skip Mussing Values
 * @return an InteractiveScatterplot Object
 */
    InteractiveScatterplotViz createInteractiveScatterplotMatching(Integer das1 ,Integer das2, String TypeX,String FieldX, String TypeY,String FieldY, boolean skipMissing ) {
        Dataset ds1 = Dataset.findById(das1)
        if(!ds1)
            return null
        Dataset ds2 = Dataset.findById(das2)

        return this.createInteractiveScatterplotMatching(ds1 ,ds2, TypeX,FieldX, TypeY,FieldY, skipMissing)
    }
    InteractiveScatterplotViz createInteractiveScatterplotMatching(Map data ){


        return this.createInteractiveScatterplotMatching( data["dataset1"] as Dataset , data["dataset2"] as Dataset, data["xAxisDatatype"] as String,data["xAxisField"] as String, data["yAxisDatatype"] as String,data["yAxisField"] as String, true )


    }
    InteractiveScatterplotViz createInteractiveScatterplotMatching(Dataset ds1 ,Dataset ds2, String TypeX,String FieldX, String TypeY,String FieldY, boolean skipMissing  ){

        //Validation before Creation

        def Type1 = ds1.datasetService.getDataClassForName( TypeX )
        def Type2 = ds1.datasetService.getDataClassForName(  TypeY )

        //print "Got DS "+Type1 +" "+Type2 +" "+TypeX +" "+TypeY
        //print Type1.properties.find{name ==FieldX}
        def prop1

        ds1.datasetService.getFieldNamesForClass(TypeX)

        prop1 = ds1.datasetService.getFieldNamesForClass(TypeX).find{it -> it == FieldX}

        def prop2
        prop2 =  ds1.datasetService.getFieldNamesForClass(TypeY).find{it -> it == FieldY}
        //Check if everything is there else Break
/*
        if(!Type1|| !Type2 || !prop1|| !prop2){

            print "NOT everything is there else Break"
            return null
        }


        //Check if X-Axis and Y-Axis are the Same, If so Break!
        if(Type1 == Type2 && (ds2.equals(ds1)|| !ds2) && TypeX.equals(TypeY) && FieldX.equals(FieldY) ){
            print "X-Axis and Y-Axis are the Same"
            return null
        }
*/


        //Find if There is an Existing Scatterplot
        InteractiveScatterplotViz iscat = InteractiveScatterplotViz.findByDataset1AndDataset2AndXAxisDatatypeAndXAxisFieldAndYAxisDatatypeAndYAxisField(ds1,ds2, TypeX,FieldX, TypeY,FieldY)
        if(iscat)
            return iscat
        //If There is no Such Scatterplot Allready , Create
        iscat = new InteractiveScatterplotViz(dataset1: ds1,dataset2: ds2, xAxisDatatype: TypeX,xAxisField: FieldX, yAxisDatatype:TypeY ,yAxisField:FieldY   )
        iscat.save(flush: true)
        return iscat
    }

    InteractiveScatterplotViz MatchDatasets(Dataset ds1,Dataset ds2, String TypeX,Class FieldX, String TypeY,String FieldY, boolean skipMissing ) {


        InteractiveScatterplotViz iscat = createInteractiveScatterplotMatching(ds1, ds2, TypeX, FieldX, TypeY, FieldY)
        MatchDatasets(iscat)

    }

    Map getChoicesforDataset(Dataset DatasetId){
        def out = [:]
        def temp = datasetService.getDataTypesInDataset(datasetId)

        temp.each {


        }
    }


    InteractiveScatterplotViz MatchDatasets(InteractiveScatterplotViz iscat, boolean skipMissing =true ){

        //TODO Calculate Cardinality of Data
        Integer Card1
        Integer Card2
        Dataset ds1 = iscat.dataset1
        Dataset ds2
        //print "started"
        if(iscat.dataset2)
            ds2 = iscat.dataset2
        else
            ds2 = ds1

        def TypeX = datasetService.getDomainDataClassForName(iscat.getxAxisDatatype()) //grailsApplication.domainClasses.find { it.clazz.simpleName == iscat.getxAxisDatatype() }
        def TypeY = datasetService.getDomainDataClassForName(iscat.getyAxisDatatype())//grailsApplication.domainClasses.find { it.clazz.simpleName == iscat.getyAxisDatatype() }
        String fieldx = iscat.getxAxisField()
        String fieldy = iscat.getyAxisField()
        Card1 = TypeX.clazz.countByDataset(ds1)
        Card2 = TypeY.clazz.countByDataset(ds2)
        log.debug( Card1 + "  "+Card2)

        int counter =0
        int outcount =0
        int RequestWindow = 10000
        //TODO Start Matching, Line by Line Base = lower Cardinality
        //FlippFlopp
        def cardBigger
        def cardSmaller
        def TypeOne
        def TypeTwo
        def data1
        def data2
        def axis1
        def axis2
        def FieldOne
        def FieldTwo
        if(Card1 > Card2) {
            cardBigger = Card2
            cardSmaller = Card1
            TypeOne = TypeY
            TypeTwo= TypeX
            data1 = ds2
            data2 = ds1
            axis1 = "yVal"
            axis2 = "xVal"
            FieldOne = fieldy
            FieldTwo = fieldx

        }else{
            cardBigger = Card1
            cardSmaller = Card2
            TypeOne = TypeX
            TypeTwo= TypeY
            data1 = ds1
            data2 = ds2
            axis1 = "xVal"
            axis2 = "yVal"
            FieldOne = fieldx
            FieldTwo = fieldy

        }

            //MemoryComsuming
            int i=0
            def crit = new DetachedCriteria(TypeOne.clazz).build{
                eq("dataset", data1)
                isNotNull(FieldOne)
                and{
                    order("chromosome", "asc")
                    order("startPos", "asc")
                    order("endPos", "asc")
                }
            }

            while((i*RequestWindow) < cardSmaller){
                    def result = crit.list(max: RequestWindow,offset:RequestWindow*i)
                    //print "OHONEEE "+result.size()
                    def positionFrames = GeneticPosition.CalcPositionFrames(result)
                    List temps = []
                    positionFrames.each {
/*                        print result.first().chromosome +" "+ result.first().startPos +" "+ result.first().endPos
                        print result.last().chromosome +" "+ result.last().startPos +" "+ result.last().endPos
                        print it.chromosome +" "+ it.startPos +" "+ it.endPos
                        print "-------------"*/
                        temps.addAll( MatchThingsByOverlap(it, TypeTwo.clazz,data2,FieldTwo))
                    }
                    def oldj =0

                    result.each{
                        //Y Axis
                        it->
                            //Sorted Heuristics
                            int j =oldj

                            boolean FirstEnd = false
                            for(;j< temps.size();j++){
                                //X Axis
                                def that = temps.get(j)
                                if( !FirstEnd && that.getEndPos() < it.getStartPos()  ){
                                    oldj =j
                                    continue
                                }else
                                    FirstEnd =true

                                if(it.getEndPos() < that.getStartPos() ){
                                    break
                                }
                                GeneticPosition overlap = GeneticPosition.Intersection(it,that)
                                    if(overlap){
                                        ScatterplotDot spc = new ScatterplotDot(chromosome: overlap.getChromosome(),startPos: overlap.getStartPos(),endPos: overlap.getEndPos(),"$axis1": it."$FieldOne","$axis2": that."$FieldTwo" ,interactiveScatterplotViz:iscat)

                                        spc.save()
                                        if(spc.hasErrors())
                                            log.debug( spc.errors)
                                        //print spc.validate()
                                        counter++
                                        if(counter % 10000 ==0)
                                            log.info( "Done: " +counter)
                                    }
/*                                    if(outcount % 10000 == 0)
                                        print "Compared " +outcount +" Matched "+counter*/

                            }
                    }
                    i+=1
            }



        log.debug( "Counter : "+ counter)

    }

    List MatchThingsByOverlap(GenPosInterface Type1, Class target,Dataset ds,String Field){
        //Make Matching Arguments Here

        def results

/*            def query = target.where {
                dataset == ds.getId() && chromosome == Type1.chromosome && !(Type1.startPos> endPos ||Type1.endPos< startPos)

            }
            results = query.list(max:70)*/
            results  = target.withCriteria {
                eq("dataset",  ds)
                isNotNull(Field)
                and{
                    eq("chromosome", Type1.chromosome)
                    not {
                        or {
                            lt("endPos", Type1.startPos, )
                            gt( "startPos",Type1.endPos)
                            }
                        }
                }

                and{
                    order("chromosome", "asc")
                    order("startPos", "asc")
                    order("endPos", "asc")
                    }
                }
        return results
        //Special If Length if In is 1


    }




}
