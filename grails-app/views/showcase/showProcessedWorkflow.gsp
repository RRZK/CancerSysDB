<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 28.04.16
  Time: 11:08
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:set var="nonews" value="${true}"></g:set>
    <meta name="layout" content="main"/>
    <title>Computed Process</title>
%{--
    <script src="https://d3js.org/d3.v4.min.js"></script>
--}%
    <asset:javascript src="d3.js"/>

<asset:javascript src="d3toolbox.js"/>
<asset:javascript src="d3table.js"/>
    <script src="https://d3js.org/d3-dsv.v1.min.js"></script>
</head>

<body>


%{--ExternalSourceDescription extSource
String sourceIdentifier
String uRI
User executor
String description
Date start
ConceptualWorkflow concept
String resultFileLocation
String callerParameters
// Key is General Filename, Value ActualFilename
Map outputFiles--}%


    <h2><g:fieldValue bean="${processedWorkflow}"
                      field="description"/></h2>
    <g:if test="${processedWorkflow.concept.outputFilesOrder}">
        <g:set var="outputFieldkey" value="${processedWorkflow.concept.outputFilesOrder}"></g:set>

    </g:if>
    <g:else>
        <g:set var="outputFieldkey" value="${processedWorkflow.getOutputFiles().keySet()}"></g:set>

    </g:else>
    <g:each in="${outputFieldkey}" status="counter" var="key">
        <g:set var="value" value="${processedWorkflow.getOutputFiles().get(key)}"></g:set>
%{--
        ${variable.key  } ${variable.value}
--}%
        <div id="ResultFileContainer${counter}" style="margin-top: 5rem">

        <g:if test="${value.endsWith("html")}">
            <iframe frameborder="0" style="width:1000px;height:650px;-ms-zoom: 0.9; -moz-transform: scale(0.9); -moz-transform-origin: 0 0; -o-transform: scale(0.9); -o-transform-origin: 0 0; -webkit-transform: scale(0.9); -webkit-transform-origin: 0 0;"
                    src="${createLink(absolute: true, controller: "showcase", action: " ") + "/displayResultfile/" + processedWorkflow.uuid + "/" + key}"></iframe>
            <a href="${createLink(absolute: true, controller: "showcase", action: " ") + "/displayResultfile/" + processedWorkflow.uuid + "/" + key}"
               target="_blank">Open in Window</a>
        </g:if>
        <g:elseif test="${value.endsWith("htmlsnippet")}">

            <div id="ShowHTMLSnippet${counter}">

            </div>

            <script>
                $(document).ready(function () {
                    var file = "${raw(createLink(action: "ShowresultFile", params: [filename:key, Uuid:processedWorkflow.uuid ]))}";
                    $.ajax({
                        url : file,
                        success : function(data) {
                            $("#ShowHTMLSnippet${counter}").append( data );
                        }
                });
                });
            </script>

        </g:elseif>
        <g:elseif test="${value.endsWith("svg")}">

            <img src="${createLink(action: "ShowresultFile", params: [filename: key, Uuid: processedWorkflow.uuid])}"
                 alt="SVG with Result" width="600">

            <br>
            <a href="${createLink(action: "ShowresultFile", params: [filename: key, Uuid: processedWorkflow.uuid])}">Download Graphic</a>

        </g:elseif>
        <g:elseif test="${value.endsWith("csv")}">

            <div class="resultTableContainer" id="ShowTable${counter}">

            </div>

            <script>
                $(document).ready(function () {
                    var file = "${raw(createLink(action: "ShowresultFile", params: [filename:key, Uuid:processedWorkflow.uuid ]))}";
                    d3.text(file, function (data) {
                        //TODO ETC
                        var seperator = "\t";

                        if(data[0].match(/\t/g).length ==data[1].match(/\t/g).length == data[2].match(/\t/g).length )
                            seperator = "\t";
                        else if(data[0].match(/,/g).length ==data[1].match(/,/g).length == data[2].match(/,/g).length )
                            seperator = ",";
                        else if(data[0].match(/;/g).length ==data[1].match(/;/g).length == data[2].match(/;/g).length )
                            seperator = ";";
                        //TODO .....

                        var parsedCSV = d3.dsvFormat(seperator).parseRows(data);
                        d3TableBuilder(parsedCSV.slice(1), parsedCSV[0], "ShowTable${counter}")

                    });
                });
            </script>
            <a href="${createLink(action: "ShowresultFile", params: [filename: key, Uuid: processedWorkflow.uuid])}">Download Table</a>

        </g:elseif>
        <g:elseif test="${value.endsWith("pdf")}">

            <a href="${createLink(action: "ShowresultFile", params: [filename: key, Uuid: processedWorkflow.uuid])}">Download PDF</a>
        </g:elseif>
        <g:elseif test="${value.endsWith("js")}">
        </g:elseif>
            <g:elseif test="${value.endsWith("pic")}">
                <a href="${createLink(action: "ShowresultFile", params: [filename: key, Uuid: processedWorkflow.uuid])}" > <img src="${createLink(action: "ShowresultFile", params: [filename: key, Uuid: processedWorkflow.uuid])}"/></a>
            </g:elseif>
        <g:else>

            <a href="${createLink(action: "ShowresultFile", params: [filename: key, Uuid: processedWorkflow.uuid])}">Download</a>
        </g:else>


        </div>
    </g:each>
<ol class="property-list dataset" style="margin:0px;padding:0px;">

    <li class="fieldcontain" >



        <span id="description-label" class="property-label" style="text-align: left"><g:message code="processedWorkflow.description.label"
                                                                       default="Description"/></span>

        <span class="property-value" aria-labelledby="description-label" ><g:fieldValue bean="${processedWorkflow}"
                                                                                       field="description"/></span>

    </li>
    <li class="fieldcontain" >
        <span id="start-label" class="property-label" style="text-align: left"><g:message code="processedWorkflow.start.label"
                                                                 default="Created"/></span>

        <span class="property-value" aria-labelledby="start-label" ><g:fieldValue bean="${processedWorkflow}"
                                                                                 field="start"/></span>
    </li>

    <g:if test="${processedWorkflow.executor}">
        <li class="fieldcontain">



            <span id="Link-label" class="property-label" style="text-align: left"><g:message code="processedWorkflow.link.label"
                                                                    default="Shareable Link"/></span>

            <span class="property-value" aria-labelledby="Link-label" >The query result will not be referenced by us. If you want to share the result, use the <a href="${createLink(controller: "showcase", action: "showProcessedWorkflow",id:processedWorkflow.uuid)}" alt="This link can by you, it wont be shared by us.">link</a></span>


        </li>

    </g:if>

    <g:else>
        <li class="fieldcontain">

            <span>This Query result is protected and only visible to you and the Admin.</span>

        </li>

    </g:else>

    <h2 style="margin-top:3rem">Parameters</h2>
    <table class="resultTableContainer" style="width: 100%">

        <tr>
            <th>parameter</th>
            <th>Value</th>
        </tr>

        <g:each in="${parameters}" var="date">

            <tr>
                <td>${date.key}</td>
                <g:if test="${date.value instanceof de.cancersysdb.geneticStandards.Gene}">
                    <td><g:link controller="gene" action="show"
                                id="${date.value.id}">${date.value.getIdentifier()}</g:link></td>
                </g:if>
                <g:else><td>${date.value }</td></g:else>
            </tr>

        </g:each>
    </table>
</ol>

</body>
</html>