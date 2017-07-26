<%@ page import="de.cancersysdb.Sample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'sample.label', default: 'Sample')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

%{--<a href="#show-sample" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                             default="Skip to content&hellip;"/></a>--}%
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">


    <h1>Sample ${sampleInstance?.sourceIdentifier}</h1>
<div id="show-sample" class="content scaffold-show" role="main">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list sample">

        <g:if test="${sampleInstance?.tissueType}">
            <li class="fieldcontain">
                <span id="tissueType-label" class="property-label"><g:message code="sample.tissueType.label"
                                                                              default="Tissue type"/></span>

                <span class="property-value" aria-labelledby="label-label"><g:fieldValue bean="${sampleInstance}"
                                                                                         field="tissueType"/></span>

            </li>
        </g:if>


        <g:if test="${sampleInstance?.cancerType}">
            <li class="fieldcontain">
                <span id="CancerType-label" class="property-label"><g:message code="sample.cancerType.label"
                                                                              default="Cancer type"/></span>

                <span class="property-value" aria-labelledby="label-label"><g:fieldValue bean="${sampleInstance}"
                                                                                         field="cancerType"/></span>

            </li>
        </g:if>



        <g:if test="${sampleInstance?.label}">
            <li class="fieldcontain">
                <span id="label-label" class="property-label"><g:message code="sample.label.label"
                                                                         default="Label"/></span>

                <span class="property-value" aria-labelledby="label-label"><g:fieldValue bean="${sampleInstance}"
                                                                                         field="label"/></span>

            </li>
        </g:if>

        <g:if test="${sampleInstance?.pairEnds}">
            <li class="fieldcontain">
                <span id="pairEnds-label" class="property-label"><g:message code="sample.pairEnds.label"
                                                                            default="Pair Ends"/></span>

                <span class="property-value" aria-labelledby="pairEnds-label"><g:fieldValue bean="${sampleInstance}"
                                                                                            field="pairEnds"/></span>

            </li>
        </g:if>

        <g:if test="${sampleInstance?.batch}">
            <li class="fieldcontain">
                <span id="batch-label" class="property-label"><g:message code="sample.batch.label"
                                                                         default="Batch"/></span>

                <span class="property-value" aria-labelledby="batch-label"><g:fieldValue bean="${sampleInstance}"
                                                                                         field="batch"/></span>

            </li>
        </g:if>

    %{--				<g:if test="${sampleInstance?.filename1}">
                        <li class="fieldcontain">
                            <span id="filename1-label" class="property-label"><g:message code="sample.filename1.label" default="Filename1" /></span>

                            <span class="property-value" aria-labelledby="filename1-label"><g:fieldValue bean="${sampleInstance}" field="filename1"/></span>

                        </li>
                    </g:if>--}%


        <g:if test="${sampleInstance?.owner}">
            <li class="fieldcontain">
                <span id="owner-label" class="property-label"><g:message code="sample.pairEnds.label"
                                                                         default="Owner"/></span>

                <span class="property-value" aria-labelledby="owner-label">${sampleInstance.owner.username}</span>

            </li>
        </g:if>

        <li class="fieldcontain">
            <h5>Datasets</h5>
            <ul>
        <g:each in="${sampleInstance.datasets}" var="dataset">
         <li><g:link controller="dataset" action="show" id="${dataset.id}"> Dataset ${dataset.id} </g:link> </li>

        </g:each>
            </ul>
        </li>
    </ol>
    <g:if test="${rights["edit"]}">
        <g:form url="[resource: sampleInstance, action: 'delete']" method="DELETE">
            <fieldset class="buttons">
                <g:link class="edit" action="edit" resource="${sampleInstance}"><g:message
                        code="default.button.edit.label" default="Edit"/></g:link>
                <g:actionSubmit class="delete" action="delete"
                                value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
            </fieldset>
        </g:form>
    </g:if>
<br>
<div class="nav" role="navigation">
    <ul>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>

        <g:if test="${rights["edit"]}">
            <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                  args="[entityName]"/></g:link></li>
        </g:if>

    </ul>
</div>

</div>
</sec:ifAnyGranted>
<sec:ifNotLoggedIn>
    <h1>Forbidden</h1>
    <br>
    <g:link controller="login" action="auth">login?</g:link>
</sec:ifNotLoggedIn>
</body>
</html>
