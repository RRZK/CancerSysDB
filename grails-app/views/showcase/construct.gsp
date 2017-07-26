
%{--TODO Remove--}%
%{--
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Construct Workflow</title>

</head>

<body>

<h1>create ${execWorkflow.name}</h1>
<span>create ${execWorkflow.description}</span>
<g:form action="showcaseExec">
    <g:hiddenField name="execWorkflow" value="${execWorkflow.id}"/>
    <g:each in="${inputDataFields}" var="din">
    --}%
%{--        TODO Input Widget --}% %{--

        <div class="fieldcontain required">
            <label for="${din.id}">
                ${din.getDescription()}



            <g:if test="${din.getDataType().equals(String.name)}">
                <g:if test="${din.optional}">
                    <g:textField name="${din.id}"/>
                </g:if>
                <g:else>
                    <g:textField name="${din.id}" required=""/>
                </g:else>
            </g:if>
            <g:if test="${din.getDataType().equals(Integer.name)|| din.getDataType().equals(Long.name)}">
                <g:if test="${din.optional}">
                    <g:textField name="${din.id}"/>
                </g:if>
                <g:else>
                    <g:textField name="${din.id}" required=""/>
                </g:else>
            </g:if>



        </div>

    </g:each>
    <g:actionSubmit value="Submit" action="showcaseExec"/>

</g:form>

</body>
</html>

--}%
