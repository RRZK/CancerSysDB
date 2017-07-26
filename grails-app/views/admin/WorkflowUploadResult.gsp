

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Upload Workflow Result</title>
    <meta name="layout" content="main">

</head>

<body>
<sec:ifAnyGranted roles="ROLE_ADMIN">
    <g:if test="${UploadSuccessful}">
        <h1>Upload Successful</h1>

        <a id="result" href="${result}" >Result</a>

    </g:if>
    <g:else>
        <h1>Upload Failed</h1>

        <g:each in="${errors}" var="error">
            ${error}

        </g:each>

    </g:else>

</sec:ifAnyGranted>
</body>
</html>