<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName"
           value="${message(code: 'interactiveScatterplot.label', default: 'InteractiveScatterplot')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
    <asset:javascript src="d3.min.js"/>
    <asset:javascript src="d3toolbox.js"/>
    <asset:javascript src="Barchart.js"/>
    <asset:javascript src="Barchart_commons.js"/>
    <asset:javascript src="vizhelpers/Scatterplotcreatetools.js"/>
</head>

<body>
%{--<a href="#create-interactiveScatterplot" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                               default="Skip to content&hellip;"/></a>--}%

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="create-interactiveScatterplot" class="content scaffold-create" role="main">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${interactiveScatterplotVizInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${interactiveScatterplotVizInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form url="[resource: interactiveScatterplotVizInstance, action: 'save']">
        <fieldset class="form">
            <g:render template="form"/>
        </fieldset>
        <fieldset class="buttons">
            <g:submitButton name="create" class="save"
                            value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
