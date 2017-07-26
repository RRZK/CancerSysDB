<g:each in="${description.MainClasses}" var="Mc">
    <h4>${Mc}</h4>
    <table  >
      <caption class="geneticdata"><i>Table</i> ${Mc}</caption>
      <tr>
        <th>Fieldname</th>
        <th>Fieldtype</th>
        <th>Required</th>
      </tr>

    <g:each in="${description.Fields.get(Mc)}" >
        <g:if test="${it[1].equals("Dataset")}">
            <g:set var="Klass" value="structuraldata"/>
        </g:if>
        <g:elseif test="${it[1].equals("Gene")}">
            <g:set var="Klass" value="genedata"/>
        </g:elseif>
        <g:else >
            <g:set var="Klass" value=""/>
        </g:else>
        <tr class="${Klass}">
            <td>${it.get(0)}</td>
            <td>${it[1]}</td>
            <td>${it[2]}</td>
        </tr>
    </g:each>
    </table>
    <g:if test="${description.Annotations.containsKey(Mc)}">
        <br>
        <p align="justify">This table has an annotation table providing extra informations for the records in the table.</p>
        <br>
        <table>
            <caption class="geneticdata"><i>Annotation Table</i> ${description.Annotations.get(Mc)}</caption>
        <tr>
            <th>Fieldname</th>
            <th>Fieldtype</th>
            <th>Required</th>
        </tr>

            <g:each in="${description.Fields.get(description.Annotations.get(Mc))}" var="linb" >
                <g:if test="${linb[1].equals("Dataset")}">
                    <g:set var="Klass" value="structuraldata"/>
                </g:if>
                <g:elseif test="${linb[1].equals("Gene")}">
                    <g:set var="Klass" value="genedata"/>
                </g:elseif>
                <g:elseif test="${linb[1].equals(Mc)}">
                    <g:set var="Klass" value="geneticdata"/>
                </g:elseif>
                <g:else >
                    <g:set var="Klass" value=""/>
                </g:else>
                    <tr class="${Klass}">
                        <td>${linb[0]}</td>
                        <td>${linb[1]}</td>
                        <td>${linb[2]}</td>
                    </tr>

            </g:each>
        </table>
    </g:if>
    <hr>
    <br>
</g:each>

