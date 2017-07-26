<%@ page import="de.cancersysdb.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'patient.label', default: 'Patient')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
%{--<a href="#edit-patient" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>--}%
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="edit-patient" class="content scaffold-edit" role="main">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${patientInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${patientInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form url="[resource: patientInstance, action: 'update']" method="PUT">
        <g:hiddenField name="version" value="${patientInstance?.version}"/>
        <fieldset class="form">
            <g:render template="form"/>
        </fieldset>
        <fieldset class="buttons">
            <g:actionSubmit class="save" action="update"
                            value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        </fieldset>
    </g:form>
</div>
</sec:ifAnyGranted>
<sec:ifNotLoggedIn>
    <h1>Forbidden</h1>
    <br>
    <g:link controller="login" action="auth">login?</g:link>
</sec:ifNotLoggedIn>
</body>
</html>
