<%@ page import="de.cancersysdb.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'patient.label', default: 'Patient')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
%{--<a href="#show-patient" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>--}%
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">


<h1><g:message code="default.show.label" args="[entityName]"/></h1>
<div id="show-patient" class="content scaffold-show" role="main">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list patient">

        <g:if test="${patientInstance?.sourceIdentifier}">
            <li class="fieldcontain">
                <span id="sourceIdentifier-label" class="property-label"><g:message code="patient.Identifier.label"
                                                                                    default="Identifier"/></span>

                <span class="property-value" aria-labelledby="label-label"><g:fieldValue bean="${patientInstance}"
                                                                                         field="sourceIdentifier"/></span>

            </li>
        </g:if>

        <g:if test="${patientInstance?.samples}">
            <li class="fieldcontain">
                <span id="samples-label" class="property-label"><g:message code="patient.samples.label"
                                                                           default="Samples"/></span>

                <g:each in="${patientInstance.samples}" var="s">
                    <span class="property-value" aria-labelledby="samples-label"><g:link controller="sample"
                                                                                         action="show"
                                                                                         id="${s.id}">${s?.tissueType.toString() + ", " + s?.location.toString() + ", " + s?.cancerType.toString()}</g:link></span>
                </g:each>

            </li>
        </g:if>

    </ol>

    <div id="Clinical Data">
    <h3>Clinical data</h3>
    <g:render template="metadata"/>
    </div>
    %{--TODO Here we have to have a Delete Strategy--}%
    %{--				<g:form url="[resource:patientInstance, action:'delete']" method="DELETE">
                        <fieldset class="buttons">
                            <g:link class="edit" action="edit" resource="${patientInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                            <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                        </fieldset>
                    </g:form>--}%


</div>
<br>
    <div class="nav" role="navigation">
        <ul>

            <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
            <li><g:link class="list" action="index"><g:message code="default.list.label"
                                                               args="[entityName]"/></g:link></li>
            <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                  args="[entityName]"/></g:link></li>
        </ul>
    </div>


</sec:ifAnyGranted>
<sec:ifNotLoggedIn>
    <h1>Forbidden</h1>
    <br>
    <g:link controller="login" action="auth">login?</g:link>
</sec:ifNotLoggedIn>
</body>
</html>
