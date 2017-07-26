<%@ page import="de.cancersysdb.Patient" %>



<div class="fieldcontain ${hasErrors(bean: patientInstance, field: 'samples', 'error')} ">
    <label for="samples">
        <g:message code="patient.samples.label" default="Samples"/>

    </label>
    <g:select name="samples" from="${de.cancersysdb.Sample.list()}" multiple="multiple" optionKey="id" size="5"
              value="${patientInstance?.samples*.id}" class="many-to-many"/>

</div>

