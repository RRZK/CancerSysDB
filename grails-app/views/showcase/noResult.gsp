<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 25.04.16
  Time: 17:34
--%>

<%@ page import="de.cancersysdb.geneticStandards.Gene" contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <title>Execution failed</title>
    <meta name="layout" content="main"/>
</head>

<body>

<h1>Execution of Workflow "${workflow.sourceIdentifier}" Failed</h1>

<br> <b>${ErrorMessage}</b><br>

<g:link controller="showcase" action="WorkflowConstruct"
        params="['showcase': workflow.sourceIdentifier]">retry</g:link>


<sec:ifAnyGranted roles="ROLE_MANAGER,ROLE_ADMIN">
    <h2>Internal Error-Message</h2>

    ${raw(InternalErrormessage.toString().replace("\n","<br>"))}

</sec:ifAnyGranted>


<h2>Parameters</h2>
<table>

    <tr>
        <th>parameter</th>
        <th>Value</th>
    </tr>

    <g:each in="${inputData}" var="date">

        <tr>
            <td>${date.key}</td>
            <g:if test="${date.value instanceof de.cancersysdb.geneticStandards.Gene}">
                <td><g:link controller="gene" action="show"
                            id="${date.value.id}">${date.value.getIdentifier()}</g:link></td>
            </g:if>
            <g:else>${date.value}</g:else>
        </tr>

    </g:each>
</table>

</body>
</html>