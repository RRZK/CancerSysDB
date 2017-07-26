<%@ page import="de.cancersysdb.Dataset" %>



<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'annon', 'error')} ">
    <label for="annon">
        <g:message code="dataset.annon.label" default="Annon"/>

    </label>
    <g:checkBox name="annon" value="${datasetInstance?.annon}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'shared', 'error')} ">
    <label for="shared">
        <g:message code="dataset.shared.label" default="Shared"/>

    </label>
    <g:checkBox name="shared" value="${datasetInstance?.shared}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'fileName', 'error')} required">
    <label for="fileName">
        <g:message code="dataset.fileName.label" default="File Name"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="fileName" required="" value="${datasetInstance?.fileName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'originURL', 'error')}">
    <label for="originURL">
        <g:message code="dataset.originURL.label" default="Origin URL"/>
    </label>
    <g:textField name="originURL" value="${datasetInstance?.originURL}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'owner', 'error')} required">
    <label for="owner">
        <g:message code="dataset.owner.label" default="Owner"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="owner" name="owner.id" from="${de.cancersysdb.User.list()}" optionKey="id" required=""
              value="${datasetInstance?.owner?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'samples', 'error')} required">
    <label for="samples">
        <g:message code="dataset.samples.label" default="Samples"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="samples" name="samples.id" from="${de.cancersysdb.Sample.list()}" optionKey="id" required=""
              value="${datasetInstance?.samples?.id}" class="many-to-one"/>

</div>


%{--
<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'timestamp', 'error')} required">
	<label for="timestamp">
		<g:message code="dataset.timestamp.label" default="Timestamp" />
		<span class="required-indicator">*</span>
	</label>

	<g:datePicker name="timestamp" precision="day"  value="${datasetInstance?.timestamp}"  />
--}%

</div>

