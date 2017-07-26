<g:select name="Type-${id}"
          from="${["symbol", "ensembl", "ncbi"]}"
          keys="${["symbol", "ensembl", "ncbi"]}"/>


<g:if test="${optional}">
    <g:textField name="identifier-${id}"  />
</g:if>
<g:else>
    <g:textField name="identifier-${id}" required="" />
</g:else>