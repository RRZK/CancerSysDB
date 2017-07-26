
<%@ page import="de.cancersysdb.portal.NewsStory" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'newsStory.label', default: 'NewsStory')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-newsStory" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
			<h1>News</h1><br>

		<div id="list-newsStory" class="content scaffold-list" role="main">
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table width="100%">
			<thead>
					<tr>
						<g:sortableColumn property="headline" title="${message(code: 'newsStory.headline.label', default: 'Headline')}" />

						<g:sortableColumn property="dateCreated" title="${message(code: 'newsStory.dateCreated.label', default: 'Date Created')}" />

						<g:sortableColumn property="lastUpdated" title="${message(code: 'newsStory.lastUpdated.label', default: 'Last Updated')}" />

					</tr>
				</thead>
				<tbody>
				<g:each in="${newsStoryInstanceList}" status="i" var="newsStoryInstance">

					<tr style="padding: 15px;" class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${newsStoryInstance.id}">${fieldValue(bean: newsStoryInstance, field: "headline")}</g:link></td>
						<td><g:formatDate date="${newsStoryInstance.dateCreated}" /></td>

						<td><g:formatDate date="${newsStoryInstance.lastUpdated}" /></td>


					</tr>
					<tr style="padding: 15px;" class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td style="font-weight: bold ;">News text : </td>

						<td colspan="2" >${raw(newsStoryInstance.story)}</td>

					</tr>
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

						<td colspan="3" >&nbsp;</td>

					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${newsStoryInstanceCount ?: 0}" />
			</div>
			<sec:ifAnyGranted roles="ROLE_ADMIN">

			<div class="nav" role="navigation">
				<ul>
					<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
					</ul>
			</div>
			</sec:ifAnyGranted>

		</div>
	</body>
</html>
