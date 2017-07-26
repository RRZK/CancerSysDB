<%@ page import="de.cancersysdb.geneticStandards.Gene" %>


<div id="GenePreview${gene.id}">
    <span><g:link action="show" id="${gene.id}">${fieldValue(bean: gene, field: "ensemblID")}</g:link></span>
    <span><g:render template="../gPosition" model="[start:gene.startPos,end:gene.endPos,chromosome:gene.chromosome]"></g:render></span>
</div>