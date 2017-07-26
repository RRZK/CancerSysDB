<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 21.04.16
  Time: 14:17
--%>


<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Choose Workflow to Construct</title>

</head>

<body>
<sec:ifLoggedIn>
    <h1>Choose Workflow to Construct</h1>

    <g:each in="${conceptualWorkflows}" var="cw">

        <h2>${cw.sourceIdentifier}</h2>
        <span>${cw.plainDescription}</span>
        <g:link action="WorkflowConstruct" params="[showcase: cw.sourceIdentifier]">Create new</g:link>
    </g:each>
</sec:ifLoggedIn>
</body>
</html>