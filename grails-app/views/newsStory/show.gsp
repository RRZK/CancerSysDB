
<%@ page import="de.cancersysdb.portal.NewsStory" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'newsStory.label', default: 'NewsStory')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-newsStory" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<sec:ifAnyGranted roles="ROLE_ADMIN">

	<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
</sec:ifAnyGranted>
		<div id="show-newsStory" class="content scaffold-show" role="main">
			<h1>${newsStoryInstance?.headline}</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list newsStory">

				<g:if test="${newsStoryInstance?.dateCreated}">
					<li class="fieldcontain">
						<span id="dateCreated-label" class="property-label"><g:message code="newsStory.dateCreated.label" default="Date Created" /></span>

						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${newsStoryInstance?.dateCreated}" /></span>

					</li>
				</g:if>

				<g:if test="${newsStoryInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="newsStory.lastUpdated.label" default="Last Updated" /></span>

						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${newsStoryInstance?.lastUpdated}" /></span>

				</li>
				</g:if>

				<g:if test="${newsStoryInstance?.story}">
				<li class="fieldcontain">
					<span id="story-label" class="property-label"><g:message code="newsStory.story.label" default="Story" /></span>

						<span class="property-value" aria-labelledby="story-label">${raw(newsStoryInstance.story)}</span>

				</li>
				</g:if>

			</ol>
<sec:ifAnyGranted roles="ROLE_ADMIN">

	<g:form url="[resource:newsStoryInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${newsStoryInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
</sec:ifAnyGranted>
		</div>
	</body>
</html>
