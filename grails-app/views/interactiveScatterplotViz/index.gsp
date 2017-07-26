<%@ page import="de.cancersysdb.gegenticViz.InteractiveScatterplotViz" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName"
           value="${message(code: 'interactiveScatterplot.label', default: 'InteractiveScatterplot')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
%{--<a href="#list-interactiveScatterplot" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                             default="Skip to content&hellip;"/></a>--}%

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-interactiveScatterplot" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>

            <th><g:message code="interactiveScatterplot.label" default="scatterplot"/></th>

            <th><g:message code="interactiveScatterplot.dataset1.label" default="Dataset1"/></th>

            <th><g:message code="interactiveScatterplot.dataset2.label" default="Dataset2"/></th>

            <g:sortableColumn property="xAxisDatatype"
                              title="${message(code: 'interactiveScatterplot.xAxisDatatype.label', default: 'X Axis Datatype')}"/>

            <g:sortableColumn property="xAxisField"
                              title="${message(code: 'interactiveScatterplot.xAxisField.label', default: 'X Axis Field')}"/>

            <g:sortableColumn property="yAxisDatatype"
                              title="${message(code: 'interactiveScatterplot.yAxisDatatype.label', default: 'Y Axis Datatype')}"/>

            <g:sortableColumn property="yAxisField"
                              title="${message(code: 'interactiveScatterplot.yAxisField.label', default: 'Y Axis Field')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${interactiveScatterplotVizInstanceList}" status="i" var="interactiveScatterplotVizInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${interactiveScatterplotVizInstance.id}">${fieldValue(bean: interactiveScatterplotVizInstance, field: "id")}</g:link></td>

                <td>${fieldValue(bean: interactiveScatterplotVizInstance, field: "dataset1")}</td>
                <td>${fieldValue(bean: interactiveScatterplotVizInstance, field: "dataset2")}</td>

                <td>${fieldValue(bean: interactiveScatterplotVizInstance, field: "xAxisDatatype")}</td>

                <td>${fieldValue(bean: interactiveScatterplotVizInstance, field: "xAxisField")}</td>

                <td>${fieldValue(bean: interactiveScatterplotVizInstance, field: "yAxisDatatype")}</td>

                <td>${fieldValue(bean: interactiveScatterplotVizInstance, field: "yAxisField")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${interactiveScatterplotVizInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
