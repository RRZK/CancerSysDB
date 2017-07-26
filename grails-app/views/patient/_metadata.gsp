<g:each in="${importInfos}" var="Import">
    <div style="width: 100%" id="ImportedInfos${Import.id}" >
    <h4>${Import.filename}</h4>
    <g:if test="${editableImportInfos.contains(Import)}">
        <g:set var="mayEdit" value="${true}"></g:set>
    </g:if>
    <g:else>
        <g:set var="mayEdit" value="${false}"></g:set>

    </g:else>
        <table style="width: 100%">
            <caption>Access rights</caption>
        <thead>
        <tr>
            <th>Attribute</th>
            <th>Value</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td title="This Data is Anonymized, this means it should be availible to anyone and does not pose any critical information about the patient. ">
                anonymized
            </td>
            <td>
                ${Import.annon}
            </td>
        </tr>
        <tr>
            <td title="This Data is shared with other users logged in into the Database">
                shared
            </td>
            <td>
                ${Import.shared}
            </td>
        </tr>
        <tr>
            <td title="The owner of the data, if not shared or anonymized this data is only visible to the owner or the admins.">
                Owner
            </td>
            <td>
                ${Import.owner.username}
            </td>
        </tr>
        <g:if test="${mayEdit}">

            <tr>
            <td title="Delete whole Batch of Clinical data">
                delete
            </td>
            <td>
                <g:form url="[id:Import.id, action:'delete', controller:'ImportInfo']" method="DELETE">
                    <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></g:form>

            </td>
        </tr>

        </g:if>
        </tbody>
        </table>



        <table style="width: 100%">
        <caption>Clinial data</caption>
        <thead>
        <tr>
            <th>Name</th>
            <th>Value</th>
            <th>Context</th>
    <g:if test="${mayEdit}">
        <th>Delete</th>

    </g:if>
        </tr>
        </thead>
        <tbody>
        <g:each in="${Import.infos}" var="Clinical">

            <tr id="${Clinical.id}">
                <td>${Clinical.exactName}</td>
                <td>${Clinical.value}</td>

                <td>${Clinical.location}</td>
                <g:if test="${mayEdit}">

                    <td><g:form url="[id:Clinical.id, action:'delete', controller:'ClinicalInformation']" method="DELETE">
                        <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></g:form>
                    </td>
                </g:if>
            </tr>

        </g:each>
        </tbody>
    </table>
    </div>
</g:each>