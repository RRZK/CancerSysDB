<%@ page import="de.cancersysdb.Sample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'sample.label', default: 'Sample')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<h1>Data available in the CancerSysDB</h1><br/>
<h4>Samples available</h4><br/>
%{--
<a href="#list-sample" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>--}%
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">

<div id="list-sample" class="content scaffold-list" role="main">
    <g:if test="${"indexOwn".equals(callingController)}">
        <h1><g:message message="My Samples"/></h1>
    </g:if>
    <g:else>
      <!-- h1><g:message message="Accessable Samples"/></h1 -->
    </g:else>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table width="100%">
        <thead>
        <tr>
            <g:sortableColumn property="id" title="${message(code: 'sample.id.label', default: 'Sample ID')}"/>


            <g:sortableColumn property="label" title="${message(code: 'sample.label.label', default: 'Label')}"/>
            <g:sortableColumn property="cancerType"
                              title="${message(code: 'sample.cancerType.label', default: 'Cancer Type')}"/>
            <g:sortableColumn property="tissueType"
                              title="${message(code: 'sample.tissueType.label', default: 'Tissue Type')}"/>
            %{--				<g:sortableColumn property="filename1" title="${message(code: 'sample.filename1.label', default: 'Filename1')}" />

                            <g:sortableColumn property="filename2" title="${message(code: 'sample.filename2.label', default: 'Filename2')}" />--}%
            <!-- g:sortableColumn property="pairEnds"
                              title="${message(code: 'sample.pairEnds.label', default: 'Pair Ends')}"/ -->
            <g:sortableColumn property="owner" title="${message(code: 'sample.owner.label', default: 'Owner')}"/>
            <g:sortableColumn property="shared" title="${message(code: 'sample.shared.label', default: 'Shared')}"/>
            <g:sortableColumn property="annon" title="${message(code: 'sample.annon.label', default: 'Anonymized')}"/>

        </tr>
        </thead>

        <tbody>
        <g:each in="${sampleInstanceList}" status="i" var="sampleInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${sampleInstance.id}">${fieldValue(bean: sampleInstance, field: "id")}</g:link></td>


                <td>${fieldValue(bean: sampleInstance, field: "label")}</td>

                %{--					<td>${fieldValue(bean: sampleInstance, field: "filename1")}</td>

                                    <td>${fieldValue(bean: sampleInstance, field: "filename2")}</td>--}%

                <td>${fieldValue(bean: sampleInstance, field: "cancerType")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "tissueType")}, ${fieldValue(bean: sampleInstance, field: "location")}</td>
                <!-- td>${fieldValue(bean: sampleInstance, field: "pairEnds")}</td -->

                <td>${fieldValue(bean: sampleInstance.owner, field: "username")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "shared")}</td>

                <td>${fieldValue(bean: sampleInstance, field: "annon")}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="sample" action="index">All samples</g:link></li>
            <li><g:link class="sample" action="indexOwn">My samples</g:link></li>
            <li><g:link class="create" action="create">New sample</g:link></li>

        </ul>
    </div>

    <div class="pagination">
        <g:paginate total="${sampleSize ?: 0}" action="${callingController}" controller="sample"/>
    </div>
</div>
</sec:ifAnyGranted>
<sec:ifNotLoggedIn>
    <h1>Forbidden</h1>
    <br>
    <g:link controller="login" action="auth">login?</g:link>
</sec:ifNotLoggedIn>
<asset:deferredScripts/>
</body>
</html>
