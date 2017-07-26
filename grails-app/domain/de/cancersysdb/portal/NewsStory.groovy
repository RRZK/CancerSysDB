package de.cancersysdb.portal
/**
 * This Class contains the news Stories which can be entered by the Admin
 */
class NewsStory {
    /**
     * Headline of Newsstory
     */
    String headline
    /**
     * The Story, contains HTML!!
     */
    String story
    /**
     * Creationsdate
     */
    Date dateCreated
    /**
     * Release date
     */
    Date lastUpdated
    static constraints = {


    }
    static mapping = {
        autoTimestamp true
        story type: "text"
    }
}
