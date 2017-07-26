<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 30.08.16
  Time: 10:10
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Clinical Data in Database</title>
    <meta name="layout" content="main">
</head>

<body>
<sec:ifAnyGranted roles="ROLE_ADMIN">

    <h3><g:link action="clinicalDataStats">See Full Stats with Occurences (takes some time)</g:link></h3>
    <h2>Clinical Data Names</h2>
        <g:render template="clinicalKeys" model="clinicaldescription:clinicaldescription"></g:render>

    </div>
</sec:ifAnyGranted>

</body>
</html>