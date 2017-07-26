<asset:javascript src="libs/select2.full.js"/>
<asset:javascript src="vizhelpers/datachooser.js"/>
<asset:stylesheet src="libs/select2.css"/>
    <div class="fieldcontain" id="${name}StudyChooser" xmlns="http://www.w3.org/1999/html">
        <g:if test="${required}">
            <span class="required-indicator">*</span>
        </g:if>
        <label for="${"Study"+name}">
            <g:message code="viz.chooseDatasetStudy.label" default="Study"/>

        </label>

        <select id="${"Study"+name}"> </select>
    </div>
    <div class="fieldcontain" id="${name}PatientChooser" >

        <label for="${"Patient"+name}">
            <g:message code="viz.chooseDatasetPatient.label" default="Patient"/>
        </label>

        <select id="${"Patient"+name}"> </select>
    </div>

    <div class="fieldcontain" id="${name}SampleChooser" >

        <label for="${"Sample"+name}">
            <g:message code="viz.chooseDatasetSample.label" default="Sample"/>
        </label>


        <select id="${"Sample"+name}"> </select>
    </div>
    <g:if test="${activeid &&activetype && activetype =="Dataset"}">

        <div class="fieldcontain" id="${name}DatasetChooser" >
            <p id="${name}DatasetChooserLabel">${activeid}</p>
            <input type="hidden" name="${name}" id="${name}" value="${activeid}"></input>

        </div>

    </g:if>
    <g:else>
        <div class="fieldcontain" id="${name}DatasetChooser" hidden="hidden" >
            <p id="${name}DatasetChooserLabel"></p>
            <input type="hidden" name="${name}" id="${name}"  value=""></input>
        </div>

    </g:else>

    <script>
        $(function(){
            $("#Study${name}").select2({
            }).on("select2:close",function(){
                completeData('${name}','Study');
            });


            $("#Patient${name}").select2({
            });


            $("#Sample${name}").select2({
            });


            $("#Patient${name}").on("select2:close",function(){
                        completeData('${name}','Patient');
                    }
            );
            $("#Sample${name}").on("select2:close",function(){
                        completeData('${name}','Sample');
                    }

            );

            <g:if test="${activeid &&activetype}">
                completeData('${name}','${activetype}','${activeid+"_"+activetype}');
            </g:if>
            <g:else>
                completeData('${name}','Sample');
            </g:else>

        });

    </script>
