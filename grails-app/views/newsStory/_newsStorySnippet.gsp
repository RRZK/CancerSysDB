<g:if test="${newsStoryInstance}">
<h3>${newsStoryInstance?.headline}</h3>

    <span style="font-size: x-small"><g:formatDate date="${newsStoryInstance?.dateCreated}" /></span>


<g:if test="${ newsStoryInstance.lastUpdated &&!newsStoryInstance.lastUpdated.equals(newsStoryInstance.dateCreated)}">
    <br>
    <span style="font-size: x-small"><g:message code="newsStory.lastUpdated.label" default="Updated" />: <g:formatDate date="${newsStoryInstance?.lastUpdated}" /></span>

</g:if>

${raw(newsStoryInstance.story)}


</ol>
</g:if>