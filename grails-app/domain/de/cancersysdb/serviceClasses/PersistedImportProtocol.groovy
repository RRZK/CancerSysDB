package de.cancersysdb.serviceClasses

import de.cancersysdb.Dataset
import de.cancersysdb.ImportTools.ImportProtocol

/**
 * This is a Persisted version of the import logger
 * It is saved after an import is Successfully finished
 */
class PersistedImportProtocol {

    def PersistedImportProtocol(ImportProtocol ip){

        dataset = ip.getDataset()
        start = new Date(ip.getStarttime())
        end = new Date(ip.getEndtime())
        messages = []
        ip.messages.each {
            if(it.size()> 70){
                //Split messages in Smaller pieces
                int steps= it.size()/70 +1
                int rest=0
                boolean end=false
                for(int i = 0;i<steps&& !end;i++){
                    String sub = it.subSequence(i*70-rest,i+1*70)
                    if(i<70)
                        end =true
                    if(!end){
                        rest=  sub.lastIndexOf(" ")-sub.size()

                        messages.add(sub.subSequence(0,sub.lastIndexOf(" ")))
                    }else
                        messages.add(sub)

                }

            }else
                messages.add(it)

        }
    }
    /**
     * Messages on Import
     */
    List messages
    /**
     * Starttime of Import
     */
    Date start
    /**
     * End time of Impoer
     */
    Date end
    /**
     * The Dataset this information belong to
     */
    Dataset dataset
    static belongsTo = [dataset: Dataset]
    static hasMany = [
            messages: String
    ]
    static constraints = {
    }
    static mapping = {
        dataset cascade: "delete"
    }

}
