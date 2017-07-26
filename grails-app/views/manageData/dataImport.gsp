<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 25.02.15
  Time: 11:30
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main">
</head>
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">
    <body>
    <h1>Upload data</h1>

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>


    <g:if test="${importProtocol}">
        <g:render template="importResult" model="[protocol: importProtocol]"></g:render>

    </g:if>

    <div id="ImportCSV">
        <g:render template="mangeMatching"
                  model="[matchingCandidates: matchingCandidates, dataTypes: dataTypes, fileName: fileName, fileInfo: fileInfo, matchingType: matchingType, dataset: dataset]"></g:render>
        <g:if test="${!dataset}">
            <g:render template="contextUpload"></g:render>
        </g:if>
    </div>

    <g:render template="metaUpload"></g:render>


    <div id="reportDiv" class="ReportDiv"></div>
</sec:ifAnyGranted>
</html>
