<br/>
<br/>
<h4>Upload context files</h4>
<p align="justify">A context file is typically an XML sheet which provides comprehensive information on a patient in a study, in particular, clinical information which can be used to annotate the molecular data available for the respective patient. To provide such data to the CancerSysDB, the system follows the specification of The Cancer Genome Atlas (TCGA) for these data.</p>
<g:if test="${context}">
  <p>Chosen Context is Dataset: <g:link controller="Dataset" action="show" id="${context.id}"></g:link></p>
</g:if>
<g:else>
    <g:uploadForm name="ContextUpload" id="ContextUpload" action="createContext">

        <!-- br style="clear: left;"/ -->
        <input id="AnnonCheckbox" type="checkbox" name="Annon" value="false"/>
	<b>Data available to others</b> (i.e. visible to logged in people except yourself)<br>
        <input id="SharedCheckbox" type="checkbox" name="Shared" value="false"/>
	<b>Make available</b> (i.e. other people than yourself can see and use the data)<br/>
	<br/>
        <input id="contextUploadFileSelector" type="file" name="Contextfile"/><br/><br/>
        <g:submitButton name="createcontext" value="Upload" id="createcontextButton"></g:submitButton>
        <script>

            $(document).ready(function () {

                if ($("#AnnonCheckboxHidden")) {
                    $("#AnnonCheckbox").change(function () {
                        if ($(this).is(":checked")) {
                            $("#AnnonCheckboxHidden").val(true);
                        } else {
                            $("#AnnonCheckboxHidden").val(false);
                        }
                    });
                }
                if ($("#SharedCheckboxHidden")) {
                    $("#SharedCheckbox").change(function () {
                        if ($(this).is(":checked")) {
                            $("#SharedCheckboxHidden").val(true);
                        } else {
                            $("#SharedCheckboxHidden").val(false);
                        }
                    });
                }
            });
        </script>
    </g:uploadForm>
</g:else>
