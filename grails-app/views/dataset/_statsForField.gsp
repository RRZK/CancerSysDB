<h4>${dataf.key}</h4>

<div>
    <ol class="property-list dataset">
        <g:each in="${dataf.value}" var="field">
            <g:if test="${field.key.equals("Values by count")}">
                <g:render template="occurenceTableWithChart"
                          model="[field: field, datasettype: datasettype]"></g:render>
            </g:if>
            <g:else>
                <li class="fieldcontain">
                    <p:label>${field.key}</p:label> ${field.value}
                </li>
            </g:else>

        </g:each>
    </ol>
</div>