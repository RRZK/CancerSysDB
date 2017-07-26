<%@ page import="de.cancersysdb.geneticStandards.Gene; de.cancersysdb.gegenticViz.InteractiveScatterplotViz" %>
<%@ page import="de.cancersysdb.gegenticViz.ScatterplotDot" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName"
           value="${message(code: 'interactiveScatterplot.label', default: 'InteractiveScatterplot')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    %{--
           TODO The Quick and the Dirt
    --}%
    <asset:javascript src="d3.min"/>
    <asset:javascript src="d3toolbox"/>
    <asset:javascript src="Barchart"/>
    <asset:javascript src="Barchart_commons"/>

</head>

<body>
%{--<a href="#show-interactiveScatterplot" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                             default="Skip to content&hellip;"/></a>--}%

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-interactiveScatterplot" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list interactiveScatterplot">

        <g:if test="${interactiveScatterplotVizInstance?.dataset2}">
            <li class="fieldcontain">
                <span id="dataset2-label" class="property-label"><g:message code="interactiveScatterplot.dataset2.label"
                                                                            default="Dataset2"/></span>

                <span class="property-value" aria-labelledby="dataset2-label"><g:link controller="dataset" action="show"
                                                                                      id="${interactiveScatterplotVizInstance?.dataset2?.id}">${interactiveScatterplotVizInstance?.dataset2?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>

        <g:if test="${interactiveScatterplotVizInstance?.dataset1}">
            <li class="fieldcontain">
                <span id="dataset1-label" class="property-label"><g:message code="interactiveScatterplot.dataset1.label"
                                                                            default="Dataset1"/></span>

                <span class="property-value" aria-labelledby="dataset1-label"><g:link controller="dataset" action="show"
                                                                                      id="${interactiveScatterplotVizInstance?.dataset1?.id}">${interactiveScatterplotVizInstance?.dataset1?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>

        <g:if test="${interactiveScatterplotVizInstance?.xAxisDatatype}">
            <li class="fieldcontain">
                <span id="xAxisDatatype-label" class="property-label"><g:message
                        code="interactiveScatterplot.xAxisDatatype.label" default="X Axis Datatype"/></span>

                <span class="property-value" aria-labelledby="xAxisDatatype-label"><g:fieldValue
                        bean="${interactiveScatterplotVizInstance}" field="xAxisDatatype"/></span>

            </li>
        </g:if>

        <g:if test="${interactiveScatterplotVizInstance?.xAxisField}">
            <li class="fieldcontain">
                <span id="xAxisField-label" class="property-label"><g:message
                        code="interactiveScatterplot.xAxisField.label" default="X Axis Field"/></span>

                <span class="property-value" aria-labelledby="xAxisField-label"><g:fieldValue
                        bean="${interactiveScatterplotVizInstance}" field="xAxisField"/></span>

            </li>
        </g:if>

        <g:if test="${interactiveScatterplotVizInstance?.yAxisDatatype}">
            <li class="fieldcontain">
                <span id="yAxisDatatype-label" class="property-label"><g:message
                        code="interactiveScatterplot.yAxisDatatype.label" default="Y Axis Datatype"/></span>

                <span class="property-value" aria-labelledby="yAxisDatatype-label"><g:fieldValue
                        bean="${interactiveScatterplotVizInstance}" field="yAxisDatatype"/></span>

            </li>
        </g:if>

        <g:if test="${interactiveScatterplotVizInstance?.yAxisField}">
            <li class="fieldcontain">
                <span id="yAxisField-label" class="property-label"><g:message
                        code="interactiveScatterplot.yAxisField.label" default="Y Axis Field"/></span>

                <span class="property-value" aria-labelledby="yAxisField-label"><g:fieldValue
                        bean="${interactiveScatterplotVizInstance}" field="yAxisField"/></span>

            </li>
        </g:if>

    </ol>

    <div id="chartScatterplot${uniqueName}" class="chart"></div>
    <g:set var="uniqueName" value="MainVisualisation"></g:set>
    <button value="show" id="buttontohide${uniqueName}"
            onclick="hideUnhide(this.id, 'TableScatterplot${uniqueName}', 'data')">show data</button>

    <table id="TableScatterplot${uniqueName}">
        <thead>
        <tr>
            <th title="The X Axis">
                ${interactiveScatterplotVizInstance?.xAxisField}

            </th>
            <th title="The Y Axis">
                ${interactiveScatterplotVizInstance?.yAxisField}
            </th>

            <th>
                chromosome
            </th>
            <th>
                startPos
            </th>

            <th>
                endPos
            </th>

        </tr>

        </thead>
        <tbody>
        <g:each in="${dots}" var="dot">
            <tr>
                <td>

                    ${dot.xVal}

                </td>
                <td>
                    ${dot.yVal}
                </td>

                <td>
                    ${dot.chromosome}
                </td>
                <td>
                    ${dot.startPos}
                </td>

                <td>
                    ${dot.endPos}
                </td>
            </tr>
        </g:each>

        </tbody>

    </table>

    <script>
        $(function () {
            drawScatterplotfromTable('TableScatterplot${uniqueName}', 'chartScatterplot${uniqueName}', '${createLink(controller: "Gene",action: "getByPosition" )}');
        });
        //$("#TableScatterplot${uniqueName}").hide();
    </script>
    <g:form url="[resource: interactiveScatterplotVizInstance, action: 'delete']" method="DELETE">
        <fieldset class="buttons">
            <g:link class="edit" action="edit" resource="${interactiveScatterplotVizInstance}"><g:message
                    code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete"
                            value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
