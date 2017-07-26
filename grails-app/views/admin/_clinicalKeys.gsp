

 <table  >
        <caption class="clinicaldata">Names of Clinical Attributes</caption>
        <tr>
            <th>Name</th>
            <th>Values</th>

        </tr>

        <g:each in="${clinicaldescription.Names}"  >

            <tr>
                <td>${it} </td>
                <td><g:link action="clinicalDataValues" params="[ClinicalKey:it]">show values</g:link></td>

            </tr>
        </g:each>
    </table>

<table  >
    <caption class="clinicaldata">Full-Paths of Clinical Attributes</caption>
    <tr>
        <th>Path</th>
        <th>Values</th>

    </tr>

    <g:each in="${clinicaldescription.Paths}"  >

        <tr>
            <td>${it}</td>
            <td><g:link action="clinicalDataValues" params="[ClinicalKey:it]">show values</g:link></td>

        </tr>
    </g:each>
</table>