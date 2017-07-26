package de.cancersysdb

import de.cancersysdb.contextHandling.WebsocketRequestMeta
import grails.transaction.Transactional

@Transactional
/**
 * This Service Manages extra infos for WebSocket Services
 * This Services Manages Websocket IDs which have been given out for longterm operations.
 */
class WebSocketIdentifierService {
    static scope = "singleton"
    //10 Minutes
    static long timeoutValue = 60 * 1000 * 10

    Map<String, WebsocketRequestMeta> MetaSock

    def WebSocketIdentifierService() {

        MetaSock = new HashMap<String, WebsocketRequestMeta>()
    }

    /**
     * Check if there are Websockets Outdated and remove them from Session
     * @return
     */
    def checkforOutdatedSession() {
        Date now = new Date()
        if (MetaSock.isEmpty())
            return
        for (key in MetaSock.keySet()) {
            if (!timeValid(MetaSock.get(key).getCreation(), now))
                MetaSock.remove(key)

        }

    }

    /**
     * Show all Websockets
     * @return Map of Web
     */
    def show() {
        Date now = new Date()
        def keys = MetaSock.keySet()
        if (!keys)
            return
        for (key in keys) {
            //print key +"  "+ MetaSock.get(key).getCreation() + "  "+MetaSock.get(key).user
            if (!timeValid(MetaSock.get(key).getCreation(), now))
                MetaSock.remove(key)

        }

    }

    def SetStuff(String Key, WebsocketRequestMeta wRM) {

        wRM.setCreation(new Date())
        MetaSock.put(Key, wRM)

        //print "  "+Key + "   " + wRM.user

    }

    /**
     * Get The User that has an Websocket Connection
     * @param Request
     * @return
     */

    Long GetUserByRequest(String Request) {

        def temp = MetaSock.get(Request)

        if (!temp)
            return null
        if (!timeValid(temp.getCreation(), new Date()))
            return null
        //Refresh
        temp.setCreation(new Date())

        this.checkforOutdatedSession()
        return temp.getUser()


    }

    /**
     * The time valid for websockets
     * @param earlyer
     * @param later
     * @return
     */
    private boolean timeValid(Date earlyer, Date later) {
        long diff = later.getTime() - earlyer.getTime()
        //print diff.toString()
        if (diff >= timeoutValue)
            return false
        if (diff < timeoutValue)
            return true

    }
}
