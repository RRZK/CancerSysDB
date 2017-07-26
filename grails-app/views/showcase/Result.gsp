<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 25.04.16
  Time: 17:34
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>

    <title>Process succesful</title>
</head>

<body>

<g:each in="${processedWorkflow.getOutputFiles()}" var="variable">

    <img src="${createLink(action: "ShowresultFile", params: [filename: variable.key, Uuid: processedWorkflow.Uuid])}"
         alt="SVG with Result" width="600">
</g:each>

</body>
</html>