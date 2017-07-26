<%@ page import="de.cancersysdb.gegenticViz.InteractiveScatterplotViz" %>
<asset:script src="vizhelpters/Scatterplotcreatetools.js"></asset:script>


<g:render template="/viz/chooseDataset"
          model="[name: 'dataset1', activeid: interactiveScatterplotVizInstance?.dataset1?.id, activetype: 'Dataset']"></g:render>

<br/>
<hr/>

<g:render template="/viz/chooseDataset" model="[name: 'dataset2']"></g:render>
<br/>
<hr/>


<div class="fieldcontain ${hasErrors(bean: interactiveScatterplotVizInstance, field: 'xAxisField', 'error')} required">
    <label for="xAxisField">
        <g:message code="interactiveScatterplot.xAxisField.label" default="X Axis Field"/>
        <span class="required-indicator">*</span>
    </label>

    <g:select id="interactiveScatterplot_AxisFieldx" name="xAxisField" from="${[]}" class="many-to-one"
              noSelection="['null': '']" disabled="true"/>

</div>


<div class="fieldcontain ${hasErrors(bean: interactiveScatterplotVizInstance, field: 'yAxisField', 'error')} required">
    <label for="yAxisField">
        <g:message code="interactiveScatterplot.yAxisField.label" default="Y Axis Field"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="interactiveScatterplot_AxisFieldy" name="yAxisField" from="${[]}" class="many-to-one"
              noSelection="['null': '']" disabled="true"/>

</div>

<script>


    $("#dataset1").change(function () {
        fieldDatasetCompleter('dataset', 'interactiveScatterplot_AxisField');
    });
    $("#dataset2").change(function () {
        fieldDatasetCompleter('dataset', 'interactiveScatterplot_AxisField');
    });

</script>