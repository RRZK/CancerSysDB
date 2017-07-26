

 <table  >
        <caption class="clinicaldata">Names and Occurences of Clinical Attributes</caption>
        <tr>
            <th>Name</th>
            <th>Occurences</th>
            <th>Values</th>

        </tr>

        <g:each in="${clinicaldescription.Names}"  >

            <tr >
                <td>${it[0]}</td>
                <td>${it[1]}</td>
                <td><g:link action="clinicalDataValues" params="[ClinicalKey:it]">show values</g:link></td>


            </tr>
        </g:each>
    </table>

<table  >
    <caption class="clinicaldata">Full-Paths and Occurences of Clinical Attributes</caption>
    <tr>
        <th>Path</th>
        <th>Occurences</th>
        <th>Values</th>

    </tr>

    <g:each in="${clinicaldescription.Paths}"  >

        <tr>
            <td>${it[0]}</td>
            <td>${it[1]}</td>
            <td><g:link action="clinicalDataValues" params="[ClinicalKey:it]">show values</g:link></td>
        </tr>
    </g:each>
</table>