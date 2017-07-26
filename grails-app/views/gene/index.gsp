<%@ page import="de.cancersysdb.geneticStandards.Gene" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'gene.label', default: 'Gene')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>

%{--
	<div hidden="hidden" id="ImportCSV" ><g:uploadForm name="CSVUpload" action="uploadFile"><input type="file" name="file"/></g:uploadForm>  </div>
--}%
<asset:javascript src="js/jquery-ui-1.10.3.custom.js"/>
<asset:stylesheet src="themes/ui-lightness/jquery-ui-1.10.3.custom.css"/>

%{--<a href="#list-gene" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                           default="Skip to content&hellip;"/></a>--}%
<sec:ifAnyGranted roles="ROLE_ADMIN">
    <div class="nav" role="navigation">
        <ul>

            <li><a id="openCSVImport" class="create" href="#"><g:message message="Import From CSV"/></a></li>

        </ul>
    </div>
</sec:ifAnyGranted>


%{--<a href="#list-gene" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>--}%
%{--<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
    </ul>
</div>--}%

<div>

    Search by Gene
    <g:form controller="gene" action="showByName" method="GET"><g:field type="text" name="geneName"/> <input
            type="submit"/></g:form>

</div>

<div id="list-gene" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="ensemblID"
                              title="${message(code: 'gene.ensemblID.label', default: 'Ensembl ID')}"/>
            <g:sortableColumn property="ncbiID" title="${message(code: 'gene.ncbiId.label', default: 'NCBI ID')}"/>
            <g:sortableColumn property="name" title="${message(code: 'gene.ncbiId.label', default: 'Name')}"/>
            <g:sortableColumn property="transcriptionOfGene"
                              title="${message(code: 'gene.Type.label', default: 'Type')}"/>
            <g:sortableColumn property="chromosome"
                              title="${message(code: 'gene.position.label', default: 'Chromosome')}"/>
            <g:sortableColumn property="startPos" title="${message(code: 'gene.start.label', default: 'start')}"/>
            <g:sortableColumn property="endPos" title="${message(code: 'gene.end.label', default: 'end')}"/>
            <g:sortableColumn property="strand" title="${message(code: 'gene.strand.label', default: 'strand')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${geneInstanceList}" status="i" var="geneInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${geneInstance.id}">${fieldValue(bean: geneInstance, field: "ensemblID")}</g:link></td>
                <td><g:link action="show"
                            id="${geneInstance.id}">${fieldValue(bean: geneInstance, field: "ncbiID")}</g:link></td>
                <td><g:link action="show"
                            id="${geneInstance.id}">${fieldValue(bean: geneInstance, field: "name")}</g:link></td>
                <td>${geneInstance.getGeneticType()}</td>
                <td colspan="4" style="text-align:left"><g:render template="/gPosition"
                                                                  model="[chromosome: geneInstance.chromosome, start: geneInstance.startPos, end: geneInstance.endPos, strand: geneInstance.strand]"></g:render></td>
                %{--						<td>${fieldValue(bean: geneInstance, field: "chromosome")}</td>
                                        <td>${fieldValue(bean: geneInstance, field: "start")}</td>
                                        <td>${fieldValue(bean: geneInstance, field: "end")}</td>
                                        <td>${fieldValue(bean: geneInstance, field: "strand")}</td>--}%

            </tr>
        </g:each>
        <td>
    </table>

    <div class="pagination">
        <g:paginate total="${geneInstanceCount ?: 0}"/>
    </div>
</div>
<asset:deferredScripts/>
</body>
</html>
