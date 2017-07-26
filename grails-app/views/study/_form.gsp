<%@ page import="de.cancersysdb.Study" %>

<div class="fieldcontain ${hasErrors(bean: studyInstance, field: 'description', 'error')} required">
    <label for="description">
        <g:message code="study.description.label" default="Description"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="description" required="" value="${studyInstance?.description}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: studyInstance, field: 'patients', 'error')} ">
    <label for="patients">
        <g:message code="study.patients.label" default="Patients"/>

    </label>

</div>

<div class="fieldcontain ${hasErrors(bean: studyInstance, field: 'referenceIdentifier', 'error')} required">
    <label for="referenceIdentifier">
        <g:message code="study.referenceIdentifier.label" default="Reference Identifier"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="referenceIdentifier" required="" value="${studyInstance?.referenceIdentifier}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: studyInstance, field: 'uRI', 'error')} required">
    <label for="uRI">
        <g:message code="study.uRI.label" default="U RI"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="uRI" required="" value="${studyInstance?.uRI}"/>

</div>

