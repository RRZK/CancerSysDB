<%@ page import="de.cancersysdb.portal.NewsStory" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'newsStory.label', default: 'NewsStory')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
	<ckeditor:resources/>
<sec:ifAnyGranted roles="ROLE_ADMIN">

	<a href="#edit-newsStory" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
		<div id="edit-newsStory" class="content scaffold-edit" role="main">
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${newsStoryInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${newsStoryInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form url="[resource:newsStoryInstance, action:'update']" method="PUT" >
				<g:hiddenField name="version" value="${newsStoryInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<br>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				</fieldset>
				<br>
			</g:form>
		</div>
</sec:ifAnyGranted>

	</body>
</html>
