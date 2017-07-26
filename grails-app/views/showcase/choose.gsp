<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 21.04.16
  Time: 14:17
--%>
%{--TODO DELETE ?--}%
%{--

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Choose Workflow to Construct</title>

</head>

<body>

<h1>Choose Workflow to Construct</h1>

<g:each in="${execWorkflows}" var="ew">

    <h2>${ew.name}</h2>
    <span>${ew.description}</span>
    <g:link action="showcaseConstruct" params="[showcase: ew.name]">Create new</g:link>
</g:each>

</body>
</html>--}%
