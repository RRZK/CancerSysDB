<%@ page import="de.cancersysdb.geneticStandards.Gene" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:if test="${geneInstance.isGene()}">
        <g:set var="entityName" value="${message(code: 'gene.labelGene', default: 'Gene')}"/>
    </g:if>
    <g:elseif test="${geneInstance.isTranscript()}">
        <g:set var="entityName" value="${message(code: 'gene.labelGene', default: 'Transcript')}"/>
    </g:elseif>
    <title><g:message code="default.show.label" args="[entityName]"/>${entityName} - ${geneInstance.ensemblID}</title>
</head>

<body>
%{--<a href="#show-gene" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                           default="Skip to content&hellip;"/></a>--}%

<div class="nav" role="navigation">
    <ul>
        %{--<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--}%
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-gene" class="content scaffold-show" role="main">
    <h1>${entityName} - ${geneInstance.ensemblID}</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list gene">
        <g:if test="${geneInstance?.name}">
            <li class="fieldcontain">
                <span id="name-label" class="property-label"><g:message code="gene.name.label" default="symbol"/></span>

                <span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${geneInstance}"
                                                                                        field="name"/></span>

            </li>
        </g:if>
        <g:if test="${geneInstance?.ensemblID}">
            <li class="fieldcontain">
                <span id="ensemblID-label" class="property-label"><g:message code="gene.ensemblID.label"
                                                                             default="Ensembl ID"/></span>

                <span class="property-value" aria-labelledby="ensemblID-label"><g:fieldValue bean="${geneInstance}"
                                                                                             field="ensemblID"/></span>

            </li>
        </g:if>

        <g:if test="${geneInstance?.ncbiID}">
            <li class="fieldcontain">
                <span id="ncbiID-label" class="property-label"><g:message code="gene.ncbiID.label"
                                                                          default="NCBI ID"/></span>

                <span class="property-value" aria-labelledby="ncbiID-label"><g:fieldValue bean="${geneInstance}"
                                                                                          field="ncbiID"/></span>

            </li>
        </g:if>
        <g:if test="${geneInstance.isTranscript()}">
            <li class="fieldcontain">
                <span id="transcriptOf-label" class="property-label"><g:message code="gene.TranscriptOf.label"
                                                                                default="transcript of"/></span>

                <span class="property-value" aria-labelledby="transcriptOf-label"><g:link action="show"
                                                                                          id="${geneInstance.transcriptOf.id}">${geneInstance.transcriptOf.toString()}</g:link></span>

            </li>
        </g:if>

        <g:if test="${geneInstance?.chromosome && geneInstance?.startPos && geneInstance?.endPos && geneInstance?.strand}">
            <li class="fieldcontain">
                <span id="position-label" class="property-label"><g:message code="gene.position.label"
                                                                            default="position"/></span>

                <span class="property-value" aria-labelledby="symbol-label"><g:render template="/gPosition"
                                                                                      model="[chromosome: geneInstance.chromosome, start: geneInstance.startPos, end: geneInstance.endPos, strand: geneInstance.strand]"/></span>

            </li>
        </g:if>

        <g:if test="${geneInstance?.description}">
            <li class="fieldcontain">
                <span id="description-label" class="property-label"><g:message code="gene.description.label"
                                                                               default="description"/></span>

                <span class="property-value" aria-labelledby="symbol-label"><g:fieldValue bean="${geneInstance}"
                                                                                          field="description"/></span>

            </li>
        </g:if>





    %{--				<g:if test="${geneInstance?.uri}">
                    <li class="fieldcontain">
                        <span id="uri-label" class="property-label"><g:message code="gene.uri.label" default="Uri" /></span>

                            <span class="property-value" aria-labelledby="uri-label"><g:fieldValue bean="${geneInstance}" field="uri"/></span>

                    </li>
                    </g:if>--}%
        <g:if test="${contexts}">
            <g:render template="/generalContextViewer" model="[contexts: contexts]"/>
        </g:if>

    </ol>

</div>
</body>
</html>
