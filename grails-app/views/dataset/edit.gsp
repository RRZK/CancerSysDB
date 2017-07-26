<%@ page import="de.cancersysdb.Dataset" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'dataset.label', default: 'Dataset')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">

<a href="#edit-dataset" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>


    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
<div id="edit-dataset" class="content scaffold-edit" role="main">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${datasetInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${datasetInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form url="[resource: datasetInstance, action: 'update']" method="PUT">
        <g:hiddenField name="version" value="${datasetInstance?.version}"/>
        <fieldset class="form">
            <g:render template="form"/>
        </fieldset>
	<br>
        <fieldset class="buttons">
            <g:actionSubmit class="save" action="update"
                            value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        </fieldset>
    </g:form>
<br>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>
<br>
</div>
</sec:ifAnyGranted>
</body>
</html>
