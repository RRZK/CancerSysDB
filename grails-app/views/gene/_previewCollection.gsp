<%@ page import="de.cancersysdb.geneticStandards.Gene" %>

%{--
TODO Variablen einführen ETC
--}%
<div id="GenePreviewContainer${idident}">
    <g:if test="${originalSpan}">All genes on <g:render template="../gPosition"
                                                        model="[start: originalSpan.startPos, end: originalSpan.endPos, chromosome: originalSpan.chromosome]"></g:render></g:if>
    <g:each in="${genes}" var="gene">

        <g:render template="preview" model="[gene: gene]"></g:render>

    </g:each>

</div>