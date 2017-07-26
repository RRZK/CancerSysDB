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


    <h2>Clinical Data Values for Key ${ClinicalKey}</h2>
        <g:render template="clinicalValues" model="clinicaldescription:clinicaldescription"></g:render>

    </div>
</sec:ifAnyGranted>

</body>
</html>