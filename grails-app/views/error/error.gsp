<%@ page import="grails.util.Environment" %>
<!DOCTYPE html>
<html>
<head>
    <title><g:if env="development">Grails Runtime Exception</g:if><g:else>Error</g:else></title>
    <meta name="layout" content="main">
    <g:if env="development"><asset:stylesheet src="errors.css"/></g:if>
</head>

<body>

<g:if env="development">
    <g:renderException exception="${exception}"/>
    <div id="errorMessage">
        <g:if test="${msg}">
            ${msg}
        </g:if>
        <g:else>
            <li>An error has occurred</li>
        </g:else>
    </div>

    <div id="errorType">
        ${code}
    </div>
</g:if>

<g:if env="test">
    <div id="errorMessage">
        <g:if test="${msg}">
            ${msg}
        </g:if>
        <g:else>
            <li>An error has occurred</li>
        </g:else>
    </div>

    <div id="errorType">
        ${code}
    </div>

</g:if>



<ul class="errors">
    <g:if test="${msg}">
        ${msg}
    </g:if>
    <g:else>
        <li>An error has occurred</li>
    </g:else>
</ul>

</body>
</html>
