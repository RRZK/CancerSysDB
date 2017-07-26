<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main">
</head>
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">
    <body>
    <h1>Import Finished</h1>

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <g:render template="importResult" model="[protocol: importProtocol]"></g:render>


    <div id="reportDiv" class="ReportDiv"></div><br/>
</sec:ifAnyGranted>
</html>