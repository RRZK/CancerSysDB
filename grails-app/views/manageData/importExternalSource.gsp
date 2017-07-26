<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 25.02.15
  Time: 11:30
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<head>
    <title></title>
    <meta name="layout" content="main">
</head>
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">

    <h1>Import Data from External Source</h1>

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:if test="${importProtocol}">
        <g:render template="/displayImportStatus" model="[protocol: importProtocol]"></g:render>
    </g:if>

    <g:render template="externalSourceInput"></g:render>

</sec:ifAnyGranted>
</html>