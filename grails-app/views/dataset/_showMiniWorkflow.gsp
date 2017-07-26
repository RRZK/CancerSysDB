<%@ page import="de.cancersysdb.workflow.ProcessedWorkflow" %>

<g:if test="${processedWorkflowInstance?.description}">
    <li class="fieldcontain">
        <span id="fileName-label" class="property-label"><g:message code="dataset.fileName.label" default="description" /></span>

        <span class="property-value" aria-labelledby="fileName-label"><g:fieldValue bean="${processedWorkflowInstance}" field="description"/></span>

    </li>
</g:if>

%{--<g:if test="${processedWorkflowInstance?.processedWorkflow?.concept?.type}">
    <li class="fieldcontain">
        <span id="originWorkflow-label" class="property-label"><g:message code="dataset.originWorkflow.label" default="original Workflow" /></span>

        <span class="property-value" aria-labelledby="fileName-label">${datasetInstance?.processedWorkflow.concept.type}</span>

    </li>
</g:if>

<g:if test="${processedWorkflowInstance?.originURL}">
    <li class="fieldcontain">
        <span id="originURL-label" class="property-label"><g:message code="dataset.originURL.label" default="Origin URL" /></span>

        <span class="property-value" aria-labelledby="originURL-label"><g:fieldValue bean="${datasetInstance}" field="originURL"/></span>

    </li>
</g:if>--}%

