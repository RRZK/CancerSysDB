<div id="CSVImportResult">
    <g:if test="${protocol.successful}">
        <h2 id="importStatus">Import Successful</h2>
    </g:if><g:else>
    <h2 id="importStatus">Import Failed</h2>
</g:else>
    <g:if test="${protocol.matching}">
        <span>Mapping of CSV File to ${protocol.matching.targetClass.toString()} Successful</span><br/>

    </g:if>
    <span>Import Finshed</span><br/>
    <g:if test="${protocol.dataset?.id}">
        <span><g:link controller="dataset" action="show"
                      id="${protocol.dataset.id.toString()}">Show dataset</g:link></span><br/>
    </g:if>
    <g:if test="${protocol.patient?.id}">
        <span><g:link controller="patient" action="show"
                      id="${protocol.patient.id.toString()}">Show patient</g:link></span><br/>
    </g:if>

    <g:each in="${protocol.messages}" var="message">

        <span>${message}</span><br/>

    </g:each>

</div>
