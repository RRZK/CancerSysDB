<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Showcases</title>

</head>

<body>
<div class="showStuffContainer">
    <table width="100%">
            <h1>Welcome to the CancerSysDB!</h1><br/>
          <span>To get started, please select any of the predefined queries to the CancerSysDB. You do not need to create a login. </span>
          <span>Any feedback or requests for additional custom queries is accepted by <a href="mailto:cecad-bioinfo@uni-koeln.de">e-mail</a>.</span>
<br><br>
            <h3>Select a predefined query</h3>
            <table width="100%" id="workflowList">

            <g:each in="${conceptualWorkflows}" var="cw">
                <tr>
                    <td style="padding: 2px">${cw.plainDescription}</td>
                    <td style="padding: 2px"><g:link class="create" action="WorkflowConstruct"
                                                     params="[showcase: cw.sourceIdentifier]"
                                                     title="${cw.longDescription}">run</g:link></td>
                    <td style="padding: 2px"><g:link controller="conceptualWorkflow" action="show"
                                                     id="${cw.id}">details</g:link></td>
                <tr/>
            </g:each>
        </table>
        <br/><br/>
        <g:if test="${!ps.keySet().empty}">
            <h4>Results of previous queries</h4><br/>

            <g:each in="${conceptualWorkflows}" var="cw">
                <g:if test="${ps.containsKey(cw.id) && !ps.get(cw.id).empty}">
                    <h5>${cw.plainDescription}</h5>
                    <table width="100%">
                        <thead>
                        <tr>
                            <th><b>Date</b></th>
                            <th><b>Input parameters</b></th>
                            <th><b>Actions</b></th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:if test="${ps.containsKey(cw.id)}">
                            <g:each in="${ps.get(cw.id)}" var="pw">
                                <tr>

                                    <td><g:formatDate format="dd.MM.yyyy - hh:mm"
                                                      date="${pw.start}"/>%{--<g:formatDate format="hh:mm" date="${pw.start}"/>--}%</td>
                                    <td>${pw.showInputParameters()}</td>
                                    <td><g:link action="showProcessedWorkflow"
                                                params="[processedWorkflow: pw.uuid]">Show</g:link>
                                    %{--
                                              <g:link action="FunctionNotImplementedYet" params="">Delete</g:link>
                                    --}%
                                    </td>
                                </tr>
                            </g:each>
                        </g:if>

                        </tbody>
                    </table>
                </g:if>
            </g:each>
        </g:if>
</div>

</body>
</html>
