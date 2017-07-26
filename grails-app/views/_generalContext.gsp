%{--
This template Renders the GeneralDisplayContext
--}%

<g:if test="${context.InternalType && context.InternalID}">
    <g:link controller="${context.InternalType}" id="${context.InternalID}">
        ${context.InternalType}:${context.InternalID}
    </g:link>
    ${context.Description}
</g:if>
<g:elseif test="${context.URL}">
    <g:if test="${context.Identifier}">
        <a href="${context.URL}" target="_blank">${context.Identifier}</a> :
        ${context.Description}
    </g:if>
    <g:else>
        <a href="${context.URL}" target="_blank">
            ${context.Description}
        </a>

    </g:else>

</g:elseif>

</br>