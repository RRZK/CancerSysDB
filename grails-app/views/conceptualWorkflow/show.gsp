<%@ page import="de.cancersysdb.workflow.ConceptualWorkflow" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'conceptualWorkflow.label', default: 'ConceptualWorkflow')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
%{--<a href="#show-conceptualWorkflow" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                         default="Skip to content&hellip;"/></a>--}%
%{--		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>--}%
    <h2 data-workflowindentifier="${conceptualWorkflowInstance?.sourceIdentifier}" id="identifierconceptualWorkflow">${conceptualWorkflowInstance.plainDescription}</h2>
<div id="show-conceptualWorkflow" class="content scaffold-show" role="main">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <ol class="property-list conceptualWorkflow">
    %{--
        <g:if test="${conceptualWorkflowInstance?.extSource}">
        <li class="fieldcontain">
            <span id="extSource-label" class="property-label"><g:message code="conceptualWorkflow.extSource.label" default="Ext Source" /></span>

                <span class="property-value" aria-labelledby="extSource-label"><g:link controller="externalSourceDescription" action="show" id="${conceptualWorkflowInstance?.extSource?.id}">${conceptualWorkflowInstance?.extSource?.encodeAsHTML()}</g:link></span>

        </li>
        </g:if>

        <g:if test="${conceptualWorkflowInstance?.uRI}">
        <li class="fieldcontain">
            <span id="uRI-label" class="property-label"><g:message code="conceptualWorkflow.uRI.label" default="U RI" /></span>

                <span class="property-value" aria-labelledby="uRI-label"><g:fieldValue bean="${conceptualWorkflowInstance}" field="uRI"/></span>

        </li>
        </g:if>

        <g:if test="${conceptualWorkflowInstance?.sourceIdentifier}">
        <li class="fieldcontain">
            <span id="sourceIdentifier-label" class="property-label"><g:message code="conceptualWorkflow.sourceIdentifier.label" default="Source Identifier" /></span>

                <span class="property-value" aria-labelledby="sourceIdentifier-label"><g:fieldValue bean="${conceptualWorkflowInstance}" field="sourceIdentifier"/></span>

        </li>
        </g:if>

        <g:if test="${conceptualWorkflowInstance?.execWorkflows}">
        <li class="fieldcontain">
            <span id="execWorkflows-label" class="property-label"><g:message code="conceptualWorkflow.execWorkflows.label" default="Exec Workflows" /></span>

                <g:each in="${conceptualWorkflowInstance.execWorkflows}" var="e">
                <span class="property-value" aria-labelledby="execWorkflows-label"><g:link controller="execWorkflow" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></span>
                </g:each>

        </li>
        </g:if>--}%
        <g:if test="${conceptualWorkflowInstance?.plainDescription}">
            <li class="fieldcontain">
                <span id="plainDescription-label" class="property-label"><g:message
                        code="conceptualWorkflow.plainDescription.label" default="Plain Description"/></span>

                <span class="property-value" aria-labelledby="plainDescription-label"><g:fieldValue
                        bean="${conceptualWorkflowInstance}" field="plainDescription"/></span>

            </li>
        </g:if>
        <g:if test="${conceptualWorkflowInstance?.longDescription}">
            <li class="fieldcontain">
                <span id="longDescription-label" class="property-label"><g:message
                        code="conceptualWorkflow.longDescription.label" default="Long Description"/></span>

                <span class="property-value" aria-labelledby="longDescription-label"><g:fieldValue
                        bean="${conceptualWorkflowInstance}" field="longDescription"/></span>

            </li>
        </g:if>
    <g:link controller="showcase" action="index">Back</g:link><br><br>

<sec:ifAnyGranted roles="ROLE_MANAGER,ROLE_ADMIN">

    <!-- h3>Queries</h3 -->
    <g:each in="${conceptualWorkflowInstance.getExecWorkflows()}" var="execWorkflow">
        <!-- h4>${execWorkflow.description}</h4 -->

        <h3>Input data</h3>
        <g:each in="${execWorkflow.inputData}" var="input">
            <h5>${input.name} to file: ${input.outputName} </h5>
            <i>Parameters:</i><br>

                <g:each in="${input.parametersForQuery}" var="param">
                    <span>
                ${param.key} :

                    ${param.value.name}
                        ${param.value.optional?"optional parameter":"required parameter"}<br>
                    ${param.value.description}<br>
                    Type: ${param.value.dataType}<br>
                    </span><br>
                </g:each>


            <i>Query:</i><br>
            <span style="text-align: justify; font-family:Consolas ">${input.hqlQuery}</span><br><br>



%{--//The Parameters Attached to the Query as Map
Map parametersForQuery

List outputFields
String outputName
String name
List inputData--}%
        </g:each>
        <h3>Commands to execute</h3>
        <g:each in="${execWorkflow.excecutionCommands}" var="com">
            ${com}<br>
        </g:each>

	<br>
        <h3>Output files</h3>
        <g:each in="${execWorkflow.outputFiles}" var="of">
            ${of.key}<br>
        </g:each>
	<br><br>

%{--

    //The Commands tobe Executed on commandline to finish this workflow
    List excecutionCommands
            //The Files containted in this Workflow
    List files
--}%

    </g:each>





    <g:form url="[resource:conceptualWorkflowInstance, action:'delete']" method="DELETE">
        <fieldset class="buttons">
            <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
    </g:form>
</sec:ifAnyGranted>



    %{--
                    <g:if test="${conceptualWorkflowInstance?.type}">
                    <li class="fieldcontain">
                        <span id="type-label" class="property-label"><g:message code="conceptualWorkflow.type.label" default="Type" /></span>

                            <span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${conceptualWorkflowInstance}" field="type"/></span>

                    </li>
                    </g:if>--}%

    </ol>
    %{--			<g:form url="[resource:conceptualWorkflowInstance, action:'delete']" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${conceptualWorkflowInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>--}%
</div>
</body>
</html>
