<%@ page import="de.cancersysdb.Study" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'study.label', default: 'Study')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">
<h1>Data available in the CancerSysDB</h1><br/>
<h4>Cohorts available</h4><br/>
%{--<a href="#list-study" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>--}%

<div id="list-study" class="content scaffold-list" role="main">
    <!-- h1><g:message code="default.list.label" args="[entityName]"/></h1 -->
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table width="100%">
        <thead>
        <tr>

            <g:sortableColumn property="description"
                              title="${message(code: 'study.description.label', default: 'Description')}"/>

            <g:sortableColumn property="referenceIdentifier"
                              title="${message(code: 'study.referenceIdentifier.label', default: 'Reference Identifier')}"/>

            <g:sortableColumn property="uRI" title="${message(code: 'study.uRI.label', default: 'U RI')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${studyInstanceList}" status="i" var="studyInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${studyInstance.id}">${fieldValue(bean: studyInstance, field: "description")}</g:link></td>

                <td>${fieldValue(bean: studyInstance, field: "referenceIdentifier")}</td>

                <td>${fieldValue(bean: studyInstance, field: "uRI")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <br/><sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">
      
      <div class="nav" role="navigation">
      <ul>
        <li><g:link class="study" action="index">All Studies</g:link></li>
            %{--
            <li><g:link class="study" action="indexOwn">My Studies</g:link></li>
            --}%
            <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                  args="[entityName]"/></g:link></li>
      </ul>
      </div>
    </sec:ifAnyGranted>


    <div class="pagination">
        <g:paginate total="${studyInstanceCount ?: 0}"/>
    </div>
</div>
</sec:ifAnyGranted>
</body>
</html>
