
<%@ page import="de.cancersysdb.serviceClasses.PersistedImportProtocol" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'persistedImportProtocol.label', default: 'PersistedImportProtocol')}" />
		<title>Import Protocol for Dataset ${persistedImportProtocol?.id}</title>
	</head>
	<body>
		<a href="#show-persistedImportProtocol" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

		<div id="show-persistedImportProtocol" class="content scaffold-show" role="main">
			<h1>Import-Protocol for <g:link action="show" controller="dataset" id="${persistedImportProtocol?.id}">Dataset ${persistedImportProtocol?.dataset.id}</g:link> </h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list persistedImportProtocol">
			
				<g:if test="${persistedImportProtocol?.dataset}">
				<li class="fieldcontain">
					<span id="dataset-label" class="property-label"><g:message code="persistedImportProtocol.dataset.label" default="Dataset" /></span>
					
						<span class="property-value" aria-labelledby="dataset-label"><g:link controller="dataset" action="show" id="${persistedImportProtocol?.dataset?.id}">Dataset ${persistedImportProtocol?.dataset.id}</g:link></span>
					
				</li>
				</g:if>
				<g:if test="${persistedImportProtocol?.start}">
					<li class="fieldcontain">
						<span id="start-label" class="property-label"><g:message code="persistedImportProtocol.start.label" default="Start" /></span>

						<span class="property-value" aria-labelledby="start-label"><g:formatDate date="${persistedImportProtocol?.start}" /></span>

					</li>
				</g:if>
				<g:if test="${persistedImportProtocol?.end}">
				<li class="fieldcontain">
					<span id="end-label" class="property-label"><g:message code="persistedImportProtocol.end.label" default="End" /></span>
					
						<span class="property-value" aria-labelledby="end-label"><g:formatDate date="${persistedImportProtocol?.end}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${persistedImportProtocol?.messages&& !persistedImportProtocol.messages.empty}">
				<li class="fieldcontain">
					<span id="messages-label" class="property-label"><g:message code="persistedImportProtocol.messages.label" default="Messages" /></span>

					<g:each in="${persistedImportProtocol.messages}" var="msg">
						<span class="property-value" >${msg}</span>
					</g:each>
				</li>
				</g:if>
				<g:if test="${notFoundGeneNames && !notFoundGeneNames.empty}">
					<li class="fieldcontain">
						<span id="messages-label" class="property-label"><g:message code="persistedImportProtocol.messages.label" default="Gene Identifiers that could not be found on Import" /></span>

						<g:each in="${notFoundGeneNames}" var="nfg">
							<span class="property-value" >${nfg.searchedGeneName}</span>
						</g:each>
					</li>
				</g:if>


			</ol>
%{--			<g:form url="[resource:persistedImportProtocol, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>--}%

		</div>
	</body>
</html>
