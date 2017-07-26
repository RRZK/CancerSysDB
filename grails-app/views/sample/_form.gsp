<%@ page import="de.cancersysdb.Sample" %>



<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'batch', 'error')} required">
    <label for="batch">
        <g:message code="sample.batch.label" default="Batch"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="batch" required="" value="${sampleInstance?.batch}"/>

</div>
%{--
<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'filename1', 'error')} required">
	<label for="filename1">
		<g:message code="sample.filename1.label" default="Filename1" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="filename1" required="" value="${sampleInstance?.filename1}"/>

</div>--}%


<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'label', 'error')} required">
    <label for="label">
        <g:message code="sample.label.label" default="Label"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="label" required="" value="${sampleInstance?.label}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: sampleInstance, field: 'pairEnds', 'error')} required">
    <label for="pairEnds">
        <g:message code="sample.pairEnds.label" default="Pair Ends"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field name="pairEnds" type="number" value="${sampleInstance.pairEnds}" required=""/>

</div>

