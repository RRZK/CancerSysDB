<g:set var="priofields" value="${["chromosome"]}"></g:set>

<g:each in="${DataStats.get("Fields")}" var="dataf">
    <g:if test="${priofields.contains(dataf.key)}">
        <g:render template="statsForField" model="[datasettype: datasettype, dataf: dataf]"></g:render>
    </g:if>
</g:each>

<g:each in="${DataStats.get("Fields")}" var="dataf">
    <g:if test="${!priofields.contains(dataf.key)}">
        <g:render template="statsForField" model="[datasettype: datasettype, dataf: dataf]"></g:render>
    </g:if>
</g:each>

