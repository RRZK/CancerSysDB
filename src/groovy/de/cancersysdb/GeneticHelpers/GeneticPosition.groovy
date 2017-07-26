package de.cancersysdb.GeneticHelpers
/**
 * Simple Genetic Object which ca calculate Intersections etc
 */
class GeneticPosition implements GenPosInterface {

    Long startPos
    Long endPos
    String chromosome

    /**
     * Get Intersection between two Position
     * @param a
     * @param b
     * @return
     */
    static GeneticPosition Intersection(GenPosInterface a ,  GenPosInterface b ){
        GeneticPosition out = new GeneticPosition()
        if(!a.getChromosome().equals(b.getChromosome()))
            return null
        out.chromosome = a.getChromosome()
        if(a.getStartPos() > b.getEndPos() ||b.getStartPos() > a.getEndPos() )
            return null

        if(a.getStartPos()> b.getStartPos() )
            out.startPos = a.getStartPos()
        else
            out.startPos = b.getStartPos()


        if(a.getEndPos()> b.getEndPos() )
            out.endPos = b.getEndPos()
        else
            out.endPos = a.getEndPos()

        return out
    }
    /**
     * Get The Combining area between Positions
     * @param Genposses
     * @return
     */
    static GeneticPosition CalcPositionFrame(Collection <GenPosInterface> Genposses ){
        def start
        def end
        GeneticPosition out = new GeneticPosition()
        out.chromosome = Genposses.getAt(0).chromosome
        boolean invalid =false

        Genposses.each{
            out.chromosome = it.chromosome
            if(it.getEndPos() > end )
                end = it.getEndPos()
            if(start< it.getStartPos())
                start = it.getStartPos()
            if(out.chromosome != it.chromosome)
                invalid=true


        }
        if(invalid)
            return null
        else{
            out.setStartPos(start)
            out.setEndPos(end)
        }
        return out
    }
    /**
     * Calulate Frames for each Chromosome
     * @param GenPosis
     * @return
     */
    static Collection<GeneticPosition> CalcPositionFrames( GenPosis){
        Map<String,GeneticPosition> outs = [:]

        GenPosis.each{
            it->
                if(!it)
                    return
                GenPosInterface tempchrom
            if(outs.containsKey(it.getChromosome())){
                tempchrom = outs.get(it.getChromosome())

            }else{
                tempchrom = new GeneticPosition()
                tempchrom.setChromosome(it.getChromosome())
                tempchrom.setStartPos(it.getStartPos())
                tempchrom.setEndPos(it.getEndPos())
                outs.put(tempchrom.getChromosome(),tempchrom)

            }

            if(it.getEndPos() > tempchrom.getEndPos() )
                tempchrom.setEndPos( it.getEndPos())
            if(it.getStartPos() < tempchrom.startPos)
                tempchrom.setStartPos( it.getStartPos())

        }
        return outs.values()
    }

    /**
     * Boolean Function that replys True if Tow Genpositions Intersect
     * @param a The First Position
     * @param b The Section Position
     * @return true if the Positions intersect, fals if they do not.
     */
    static boolean isIntersecting(GenPosInterface a ,  GenPosInterface b ){
        if(!a.getChromosome().equals(b.getChromosome()))
            return false
        if(a.getEndPos()< b.getStartPos() || a.getStartPos() > b.getEndPos())
            return false
        return true
    }

}
