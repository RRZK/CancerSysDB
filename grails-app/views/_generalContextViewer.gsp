<g:each in="${contexts}" var="ct">
    <h3>${ct.key}</h3>
    <g:each in="${ct.value}" var="context">

        <g:render template="/generalContext" model="[context: context]"/>

    </g:each>
    <hr/>
</g:each>