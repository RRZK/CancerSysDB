<%@ page import="de.cancersysdb.Dataset" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'dataset.label', default: 'Dataset')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
%{--
		<a href="#show-dataset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
--}%

<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">

		  <h1>Data available in the CancerSysDB</h1><br/>
		<div id="show-dataset" class="content scaffold-show" role="main">
			<h4><g:message code="default.show.label" args="[entityName]" /></h4>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list dataset">
%{--				<g:if test="${datasetInstance?.fileName}">
				<li class="fieldcontain">
					<span id="fileName-label" class="property-label"><g:message code="dataset.fileName.label" default="File Name" /></span>

						<span class="property-value" aria-labelledby="fileName-label"><g:fieldValue bean="${datasetInstance}" field="fileName"/></span>

				</li>
				</g:if>--}%

        <g:if test="${datasetInstance?.processedWorkflow?.concept?.type}">
            <li class="fieldcontain">
                <span id="originWorkflow-label" class="property-label"><g:message code="dataset.originWorkflow.label"
                                                                                  default="original Workflow"/></span>

						<span class="property-value" aria-labelledby="originWorkflow-label">${datasetInstance?.processedWorkflow.concept.type}</span>

					</li>
				</g:if>
			
				<g:if test="${datasetInstance?.originURL}">
				<li class="fieldcontain">
					<span id="originURL-label" class="property-label"><g:message code="dataset.originURL.label" default="Origin URL" /></span>
					
						<span class="property-value" aria-labelledby="originURL-label"><g:fieldValue bean="${datasetInstance}" field="originURL"/></span>
					
				</li>
				</g:if>
				<g:if test="${datasetInstance?.note}">
					<li class="fieldcontain">
					  <span id="note-label" class="property-label"><g:message code="dataset.note.label" default="note" /></span>

					  <span class="property-value" aria-labelledby="note-label"><g:fieldValue bean="${datasetInstance}" field="note"/></span>

					</li>
				</g:if>
				<g:if test="${datasetInstance?.fileName}">
					<li class="fieldcontain">
						<span id="fileName-label" class="property-label"><g:message code="dataset.fileName.label" default="note" /></span>

						<span class="property-value" aria-labelledby="fileName-label"><g:fieldValue bean="${datasetInstance}" field="fileName"/></span>

					</li>
				</g:if>

				<g:if test="${datasetInstance?.owner}">
				<li class="fieldcontain">
					<span id="owner-label" class="property-label"><g:message code="dataset.owner.label" default="Owner" /></span>
					
						<span class="property-value" aria-labelledby="owner-label"><g:link controller="user" action="show" id="${datasetInstance?.owner?.id}">${datasetInstance?.owner?.username.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>

				<g:if test="${datasetInstance?.samples}">
				<li class="fieldcontain">
					<span id="samples-label" class="property-label"><g:message code="dataset.samples.label" default="Samples" /></span>
					
						<span class="property-value" aria-labelledby="samples-label">
					<g:each in="${datasetInstance.samples}" var="sample" status="i" >

                        <g:link controller="sample" action="show"
                                id="${sample.id}">${sample.toContextShortIndividualizedString()}</g:link>
                        <g:if test="${i < datasetInstance.samples.size() - 1}">,</g:if>
                    </g:each>
                </span>
            </li>



        </g:if>



				<g:if test="${importProtocol}">
					<li class="fieldcontain">
						<g:link action="show" controller="persistedImportProtocol" id="${importProtocol.id}"><g:message code="dataset.importProtocol.label" default="importProtocol" /></g:link>
					</li>
				</g:if>
        <g:if test="${datasetInstance?.processedWorkflow}">
            <li class="fieldcontain">
                <h3>Worflow</h3>

                <g:render template="showMiniWorkflow"
                          model="[processedWorkflowInstance: datasetInstance.processedWorkflow]"></g:render>
            </li>
        </g:if>

    </ol>

%{--    <div class="fieldcontain" id="VizManageWidget"
         data-callrest="${createLink(controller: "viz", action: "getExistingVisualisationForDataset", params: [dataset: datasetInstance.id])}">

    </div>
    <br/>--}%

    <h4>Data</h4>

    <g:each status="i"  in="${DataStatistics}" var="datasets" >
        <g:set var="stat" value="${datasets.value}"/>
		<g:if test="${datasets.value.IsBinData && datasets.value.IsSingleData}">
			<a href="${createLink(controller: "export", action: datasets.key, id: datasetInstance.id)}">Download File here</a>
		</g:if>
		<g:else>
        <div>
			<p align="justify">This is a <b id="datatype${i}">${datasets.key?.encodeAsHTML()}</b> dataset with ${stat.get("Count")} lines. Please click <a href="${createLink(controller: "export", action: datasets.key, id: datasetInstance.id)}">here</a> to export the data as a CSV file.</p><p>&nbsp;</p>
        </div>
		</g:else>
    %{--<g:render template="genericResultTable" model="[Datafragments:datasets,datasettype:DataStatistics.key]"/>--}%
        <g:render template="datasetStatistics" model="[DataStats: stat, datasettype: datasets.key]"/>

    </g:each>

<br>
	<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<g:if test="${rights["edit"]}" >
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</g:if>
			</ul>
		</div>
<br>

    <g:if test="${rights["edit"]}">
        <g:form url="[resource: datasetInstance, action: 'delete']" method="DELETE">
            <fieldset class="buttons">
                <g:link class="edit" action="edit" resource="${datasetInstance}"><g:message
                        code="default.button.edit.label" default="Edit"/></g:link>
                <g:actionSubmit class="delete" action="delete"
                                value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
            </fieldset>
        </g:form>
    </g:if>
</div>
</sec:ifAnyGranted>
%{--
<script>
    var Call = $("#VizManageWidget").attr("data-callrest");
    $.ajax(Call, {
        success: function (htmlinlay) {
            $("#VizManageWidget").html(htmlinlay)
        }
    });
</script>--}%
</body>
</html>
