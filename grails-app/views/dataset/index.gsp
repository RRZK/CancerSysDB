<%@ page import="de.cancersysdb.Dataset" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'dataset.label', default: 'Dataset')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<h1>Data available in the CancerSysDB</h1><br/>
<h4>Datasets available</h4><br/>
%{--
		<a href="#list-dataset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
--}%
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">
<div id="list-dataset" class="content scaffold-list" role="main">
    <g:if test="${"indexOwn".equals(callingController)}">
        <!-- h1><g:message message="My Datasets"/></h1 -->
    </g:if>
    <g:else>
        <!-- h1><g:message message="Accessable Datasets"/></h1 -->
    </g:else>


    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table width="100%">
        <thead>
        <tr>

            <g:sortableColumn property="id" title="${message(code: 'dataset.fileName.label', default: 'Dataset ID')}"/>

            %{--
                                    <th>Content</th>
            --}%
            <th><g:message code="dataset.samples.label" default="Samples"/></th>
            <g:sortableColumn property="singlePatient"
                              title="${message(code: 'dataset.singlePatient.label', default: "Patient")}"/>
            <th><g:message code="dataset.owner.label" default="Owner"/></th>


            <g:sortableColumn property="shared" title="${message(code: 'dataset.shared.label', default: 'Shared')}"/>
            <g:sortableColumn property="annon" title="${message(code: 'dataset.annon.label', default: 'Anonymized')}"/>

        </tr>
        </thead>
        <tbody>

        <g:each status="i" in="${datasetInstanceList}" var="datasetInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${datasetInstance.id}">${statisticsList.get(i).join(",")} id: ${fieldValue(bean: datasetInstance, field: "id")}</g:link></td>
                %{--						<td><g:link action="show" id="${datasetInstance.id}">${fieldValue(bean: datasetInstance, field: "fileName")}</g:link></td>
                                        --}%
                %{--
                                        <td>${statisticsList.get(i).join(",")}</td>
                --}%
                <td>
                    <g:if test="${datasetInstance.samples.size() < 4}">
                        <g:each in="${datasetInstance.samples}" var="sample" status="j">
                            <g:link controller="sample" action="show"
                                    id="${sample.id}">${sample.tissueType.abbreviation}</g:link>
                            <g:if test="${j < datasetInstance.samples.size() - 1}">,</g:if>
                        </g:each>
                    </g:if>
                    <g:else>
                        ${datasetInstance.samples.size()} Samples
                    </g:else>

                </td>
                <td>
                    ${datasetInstance.singlePatient?.toContextShortIndividualizedString() ?: "-"}
                    %{--
                    <g:link action="show">${datasetInstance.singlePatient?.toContextShortIndividualizedString() ?: "-"}</g:link>
                    --}%
                </td>
                <td>${fieldValue(bean: datasetInstance.owner, field: "username")}</td>

                <td>${fieldValue(bean: datasetInstance, field: "shared")}</td>
                <td>${fieldValue(bean: datasetInstance, field: "annon")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>


    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="dataset" action="index">All datasets</g:link></li>
            <li><g:link class="dataset" action="indexOwn">My datasets</g:link></li>
            <li><g:link class="create" action="create">New dataset
<!-- g:message code="default.new.label"
                                                                  args="[entityName]"/ -->
	    </g:link></li>

        </ul>
    </div>
</sec:ifAnyGranted>

    <div class="pagination">
        <g:paginate total="${datasetSize ?: 0}"/>
    </div>
</div>
</body>
</html>
