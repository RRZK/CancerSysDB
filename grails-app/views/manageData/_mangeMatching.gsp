<g:uploadForm name="CSVUpload" id="CSVUpload" action="importFile">
  <br/><h4>Upload data files</h4>

    <br/>
    <p align="justify">Molecular data on patients in the CancerSysDB can be uploaded in any plain text format. In order to assign the data to a particular patient, you first need to upload a context file for that patient (see below).</p><br/>
    <table><tr><td><b>File:</b></td><td>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>
    <g:if test="${fileName}">
        <input type="text" value="${fileName}" disabled="disabled"/> <br/><br/>
        <g:hiddenField name="ExistingFile" value="${fileInfo}"></g:hiddenField>
    </g:if>
    <g:else>
        <input id="contentUploadFileSelector" type="file" name="file"/><br/><br/>
    </g:else>
    </td></tr>
%{--
    <g:select id="dataType" noSelection="['':'-Choose Import Type-']" name="dataType" from="${dataTypes}" optionKey="key" optionValue="label" value="${userInstance?.active}" /><br/><br/>
--}%
    <tr><td><b>Input as:</b></td><td></td><td>
    <g:if test="${dataTypes}">
        <g:select id="dataType" noSelection="['': '-Choose Import Type-']" name="dataType" from="${dataTypes}"
                  optionKey="key" optionValue="label" value="${matchingType}"/><br/><br/>
    </g:if>
    </td></tr></table>
%{--    <g:elseif test="${matchingType}">
        <input type="text" disabled="disabled" value="${matchingType}"/> <br/><br/>
        <g:hiddenField name="dataType" value="${matchingType}"></g:hiddenField>
    </g:elseif>--}%

    <g:if test="${matchingCandidates}">
        <h2>File Import Mapping</h2>

        <div style="float:none;width:100%;">

            <g:each in="${matchingCandidates}" var="fields">
            %{--TODO Errors--}%
                <div style="float:left;margin-right:10px;height:100px">
                    <label>${fields.key}</label><br/>
                    <g:if test="${fields.value}">
                        <g:select id="${"Mapping_" + fields.key}" name="${"Mapping_" + fields.key}"
                                  from="${fields.value}" value="${fields.value[0]}"/><br/><br/>
                    </g:if>
                    <g:else>
                        no Fitting Value<br/><br/>
                    </g:else>
                </div>

            </g:each>
        </div>
        <g:hiddenField name="Mapping" value="${matchingType}"></g:hiddenField>

    </g:if>

    <g:if test="${dataset}">
        <p>Context: <g:link target="_blank" action="show" controller="dataset"
                            id="${dataset.id}">Dataset ${dataset.id}</g:link></p>
        <g:hiddenField name="dataset" value="${dataset.id}"></g:hiddenField>
    </g:if>




    <g:hiddenField name="Annon" id="AnnonCheckboxHidden" value="false"></g:hiddenField>
    <g:hiddenField name="Shared" id="SharedCheckboxHidden" value="false"></g:hiddenField>
    <g:submitButton name="upload" value="Upload"></g:submitButton>

</g:uploadForm>
