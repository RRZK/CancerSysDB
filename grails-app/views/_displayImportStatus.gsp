<%@ page import="de.cancersysdb.ImportTools.ImportProtocol" %>

<div id="ImportProtocol">
    <g:if test="{protocol.successful}">
        <h2>Import Successful</h2>
    </g:if>
    <g:else>
        <h2>Import Failed</h2>
    </g:else>
    <ol class="property-list sample">

        <li class="fieldcontain">
            <span id="successfulCount-label" class="property-label">Successful Imported</span>

            <span class="property-value" aria-labelledby="label-label">${protocol.successfulCount}</span>

        </li>
        <li class="fieldcontain">
            <span id="failedCount-label" class="property-label">Failed to Import</span>

            <span class="property-value" aria-labelledby="label-label">${protocol.failedCount}</span>

        </li>

    </ol>


    <g:if test="${protocol.errorMessages.size() > 0}">
        <h3>Errors</h3>

        <g:each in="${protocol.errorMessages}" var="emessage" status="i">

            <span id="Importprotocol_ErrorMessage${i}">${emessage}</span><br/>

        </g:each>
    </g:if>

    <h3>Messages</h3>
    <g:each in="${protocol.messages}" var="message" status="i">

        <span id="Importprotocol_Message${i}">${message}</span><br/>

    </g:each>

</div>