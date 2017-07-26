<%@ page import="de.cancersysdb.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'patient.label', default: 'Patient')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">

<h1>Data available in the CancerSysDB</h1><br/>
<h4>Patients available</h4><br/>
%{--<a href="#list-patient" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>--}%
<div id="list-patient" class="content scaffold-list" role="main">
    <!-- h1><g:message code="default.list.label" args="[entityName]"/></h1 -->
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table width="100%">
        <thead>
        <tr>
            <g:sortableColumn property="id" title="${message(code: 'patient.id.label', default: 'Patient ID')}"/>
            <td><b>Cancer Type</b><td/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${patientInstanceList}" status="i" var="patientInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td><g:link action="show"
                            id="${patientInstance.id}">${patientInstance.sourceIdentifier ?: "Patient " + patientInstance.id}</g:link></td>

                <td><g:if
                        test="${!patientInstance.samples.isEmpty()}">${patientInstance.samples.first().cancerType.toString()}</g:if></td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <br/>
    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="patient" action="index">All Patients</g:link></li>
            %{--
                            <li><g:link class="patient" action="indexOwn">My Patients</g:link></li>
            --}%
            <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                  args="[entityName]"/></g:link></li>
        </ul>
    </div>

    <div class="pagination">
        <g:paginate total="${patientInstanceCount ?: 0}"/>
    </div>
</div>
</sec:ifAnyGranted>
<sec:ifNotLoggedIn>
    <h1>Forbidden</h1>
    <br>
    <g:link controller="login" action="auth">login?</g:link>
</sec:ifNotLoggedIn>
</body>
</html>
