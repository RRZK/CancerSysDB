<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main">
</head>

<body>
<sec:ifAnyGranted roles="ROLE_ADMIN">
    <g:if test="${result || message == 'Empty result!' }">
        <h1>HQL Success</h1>
        <g:if test="${message }">
        ${message}
        </g:if>

            <h2>query:</h2>

        <div>${query}</div>
        <g:if test="${result }">
            <h2>Result:</h2>
            <g:if test="${result.getClass().equals(Map.class)}">
                <g:each in="${result}" var="thing"></g:each>
                ${thing}
            </g:if>
            <g:else>
                <g:each in="${result}" var="thing">
                    ${thing.toString()}<br/>

                </g:each>

            </g:else>
        </g:if>

    </g:if>
    <g:else>
        <h1>Failed</h1>
        <g:if test="${message }">
            ${message}
        </g:if>

        query: <br/>

        <div>${query}</div>
        error: <br/>

        ${error}
    </g:else>

</sec:ifAnyGranted>
</body>
</html>