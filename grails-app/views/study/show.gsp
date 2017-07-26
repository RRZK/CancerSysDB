<%@ page import="de.cancersysdb.Study" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'study.label', default: 'Study')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
%{--<a href="#show-study" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                            default="Skip to content&hellip;"/></a>--}%
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">


    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
<div id="show-study" class="content scaffold-show" role="main">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list study">

        <g:if test="${studyInstance?.description}">
            <li class="fieldcontain">
                <span id="description-label" class="property-label"><g:message code="study.description.label"
                                                                               default="Description"/></span>

                <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${studyInstance}"
                                                                                               field="description"/></span>

            </li>
        </g:if>
        <g:if test="${studyInstance?.referenceIdentifier}">
            <li class="fieldcontain">
                <span id="referenceIdentifier-label" class="property-label"><g:message
                        code="study.referenceIdentifier.label" default="Reference Identifier"/></span>

                <span class="property-value" aria-labelledby="referenceIdentifier-label"><g:fieldValue
                        bean="${studyInstance}" field="referenceIdentifier"/></span>

            </li>
        </g:if>

        <g:if test="${studyInstance?.uRI}">
            <li class="fieldcontain">
                <span id="uRI-label" class="property-label"><g:message code="study.uRI.label" default="URL"/></span>

                <span class="property-value" aria-labelledby="uRI-label"><g:fieldValue bean="${studyInstance}"
                                                                                       field="uRI"/></span>

            </li>
        </g:if>
        <g:if test="${stats}">
            <h4>Statistics</h4>
            <li class="fieldcontain">
                <span id="PatientCount-label" class="property-label"><g:message code="study.PatientCount.label"
                                                                                default="Number of patients"/></span>

                <span class="property-value" aria-labelledby="PatientCount-label">${stats["numPatients"]}</span>

            </li>
            <li class="fieldcontain">
                <span id="SampleCount-label" class="property-label"><g:message code="study.SampleCount.label"
                                                                               default="independent samples"/></span>

                <span class="property-value" aria-labelledby="SampleCount-label">${stats["numSamples"]}</span>

            </li>
            <h4>Cancer types</h4>
            <g:each in="${stats["cancerTypes"]}" var="cancer">
                <li class="fieldcontain">
                    <span id="${cancer.key}-label" class="property-label">${cancer.key}</span>
                    <span class="property-value" aria-labelledby="${cancer.key}-label">${cancer.value}</span>

                </li>

            </g:each>
            <h4>Data</h4>

            <li class="fieldcontain">
                <span id="DatasetCount-label" class="property-label"><g:message code="study.DatasetCount.label"
                                                                                default="Number of associated datasets"/></span>

                <span class="property-value" aria-labelledby="SampleCount-label">${stats["numDatasets"]}</span>

            </li>
            <h4>Dataset types</h4>
            <g:each in="${stats["numDatasetTypes"]}" var="dsType">

                <li class="fieldcontain">
                    <span id="${dsType.key}-label" class="property-label">${dsType.key}</span>

                    <span class="property-value" aria-labelledby="${dsType.key}-label">${dsType.value}</span>

                </li>

            </g:each>

        </g:if>



        <g:if test="${studyInstance?.patients}">
            <h4>Context</h4>
            <li class="fieldcontain">
                <span id="patients-label" class="property-label"><g:message code="study.patients.label"
                                                                            default="Patients"/></span>
                <span class="property-value" aria-labelledby="patients-label">
                    <g:each in="${studyInstance.patients.sort()}" var="p" status="ith">
                        <g:link controller="patient" action="show" id="${p.id}">${p?.id}</g:link><g:if
                            test="${ith < studyInstance.patients.size() - 1}">,</g:if>
                    </g:each>

                </span>
            </li>
        </g:if>
    </ol>
    %{--TODO Here we have to have a Delete Strategy--}%
    %{--			<g:form url="[resource:studyInstance, action:'delete']" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${studyInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>--}%
</div>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

</sec:ifAnyGranted>
</body>
</html>
