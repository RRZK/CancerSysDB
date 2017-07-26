<%@ page import="de.cancersysdb.serviceClasses.ExternalSourceDescription" %>

<br>
<g:form controller="manageData" action="dataImportFromExternalSource" id="dataImportFromExternalSource_form">
    <g:select name="externalSourceChoose" id="externalSourceChoose" from="${externalSources}"
              value="${externalSourceDescriptionInstance?.name ?: "Nothing"}" optionKey="name" optionValue="name"
              onchange="externalSource_ChosenReload()" noSelection="['Nothing': '-Create New Source-']"/>

    <g:javascript>
$(document).ready(function() {externalSource_ChosenReload();
});

        function externalSource_ChosenReload(){
            var temp = $("#externalSourceChoose");
            var qString = temp.val();
            var url = "${raw(createLink(controller: 'manageData', action: 'getExternalSourceByName'))}";
            if(qString != "Nothing"){
                $.ajax({
                    url:url,
                    dataType: 'json',
                    data: {
                        externalSourceName: qString
                    },
                   success: function(data) {
                        for(var key in data){
                            if(key !="class" ){

                                val = data[key];
                                var tempo;
                                var element = $( 'input[id="externalSourceDescriptionInstance_' +key+'"]');

                                tempo =  element.val( val );
                                element.val( val );
                                element.prop( "disabled", true );
                                if(key == "id")
                                    $("#externalSourceId").val(val)

                            }
                        }
                        var submiter = $("#ExternalResourceSubmit");
                        submiter.hide();
                        $("#RequestResourceContainer").show();

                        $("#RequestResource").prop( "disabled", false );
                        $("#RequestExternalResource").show();
                        $("#RequestExternalResource").prop( "disabled", false );

                    },
                    error: function(request, status, error) {
                    }

                });
            }else{

                var tempa = $( "input[id^='externalSourceDescriptionInstance_']");
                tempa.val( " " );

                tempa.prop( "disabled", false );
                $("#ExternalResourceSubmit").show();
                $("#RequestResourceContainer").hide();
                $("#RequestResource").prop( "disabled", true );
                $("#RequestExternalResource").hide();
                $("#RequestExternalResource").prop( "disabled", true );

        }
    }
    </g:javascript>

<br><br>
    <div class="fieldcontain ${hasErrors(bean: externalSourceDescriptionInstance, field: 'name', 'error')} ">
        <label for="externalSourceDescriptionInstance.name">
            <g:message code="externalSourceDescription.name.label" default="Name"/>

        </label>
        <g:textField name="externalSourceDescriptionInstance.name" id="externalSourceDescriptionInstance_name"
                     required="" value="${externalSourceDescriptionInstance?.name}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: externalSourceDescriptionInstance, field: 'uRL', 'error')} ">
        <label for="externalSourceDescriptionInstance.uRL">
            <g:message code="externalSourceDescription.uRL.label" default="URL"/>

        </label>
        <g:textField name="externalSourceDescriptionInstance.uRL" id="externalSourceDescriptionInstance_uRL" required=""
                     value="${externalSourceDescriptionInstance?.uRL}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: externalSourceDescriptionInstance, field: 'description', 'error')} ">
        <label for="externalSourceDescriptionInstance.description">
            <g:message code="externalSourceDescription.description.label" default="Description"/>

        </label>
        <g:textField name="externalSourceDescriptionInstance.description"
                     id="externalSourceDescriptionInstance_description" required=""
                     value="${externalSourceDescriptionInstance?.description}"/>

    </div>

    <div class="fieldcontain ${hasErrors(bean: externalSourceDescriptionInstance, field: 'descriptionReference', 'error')} ">
        <label for="externalSourceDescriptionInstance.descriptionReference">
            <g:message code="externalSourceDescription.descriptionReference.label" default="Source"/>

        </label>
        <g:textField name="externalSourceDescriptionInstance.descriptionReference"
                     id="externalSourceDescriptionInstance_descriptionReference" required=""
                     value="${externalSourceDescriptionInstance?.descriptionReference}"/>

    </div>

<br><br>
    <g:submitButton id="ExternalResourceSubmit" name="Create external resource"></g:submitButton>
</g:form>
<br/><br/>
<g:form controller="manageData" action="dataImportFromExternalSource">

    <g:hiddenField name="externalSourceId" id="externalSourceId" value=""></g:hiddenField>

    <div class="fieldcontain ${hasErrors(bean: RequestResource, field: 'RequestResource', 'error')}  "
         id="RequestResourceContainer" hidden="hidden">
        <label for="RequestResource">
            Dataset url

        </label>
        <g:textField name="RequestResource" id="RequestResource" value="${RequestResource}"/>
    </div>
    <g:submitButton id="RequestExternalResource" name="RequestExternalResource" value="import resource"
                    disabled="disabled"></g:submitButton>

</g:form>
