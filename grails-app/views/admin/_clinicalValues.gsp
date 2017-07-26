

 <table  >
        <caption class="clinical">Occurences of Values</caption>
        <tr>
            <th>Name</th>
            <th>Occurence</th>

        </tr>

        <g:each in="${clinicaldescription}">

            <tr >
                <td>${it[0]}</td>

                <td>${it[1]}</td>
            </tr>
        </g:each>
    </table>

