<g:set var="uniqueName" value="${datasettype + datasettype}"></g:set>

<div id="chart${uniqueName}" class="chart"></div>

<g:set var="Mappe" value="${field.value}"></g:set>
<button value="show" id="buttontohide${uniqueName}"
        onclick="hideUnhide(this.id, 'table${uniqueName}', 'data')">show data</button>

<div id="table${uniqueName}" hidden>
    <table>
        <thead>
        <tr>

            <th>value</th>
            <th>occurence</th>
        </tr>
        </thead>

        <g:each in="${Mappe}" var="token">
            <tr>
                <td>${token.key}</td>
                <td>${token.value}</td>
            </tr>
        </g:each>
    </table>
</div>

<script>
    CountBarChart("table${uniqueName}", 'chart${uniqueName}', "value", "occurence");
</script>