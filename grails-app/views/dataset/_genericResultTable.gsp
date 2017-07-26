<div id="chart${datasettype}" class="chart"></div>

<div style="border:solid #000000 1px;  ">
    <table id="table${datasettype}">
        <thead>
        <tr>
            <g:each in="${Datafragments.get(0).toArrayHeadings()}" var="headName">
                <th>${headName}</th>
            </g:each>
        </tr>
        </thead>
        <tbody>
        <g:each in="${Datafragments}" var="dataf">
            <tr>
                <g:each in="${dataf.toArray()}" var="field">
                    <td>
                        ${field}
                    </td>
                </g:each>

            </tr>

        </g:each>

        </tbody>
    </table>
</div>
<script>
    ChromosomeCountBarChart('table${datasettype}', 'chart${datasettype}');
</script>