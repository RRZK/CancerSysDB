<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Construct Workflow</title>

</head>

<body>
  <h1>Query the CancerSysDB</h1><br/>
  <h4>${conceptualWorkflow.plainDescription}</h4><br/>
  <p align="justify">${conceptualWorkflow.longDescription}</p>
%{--
<span>create ${conceptualWorkflow.plainDescription}</span>
--}%
	<br/>

    <g:form action="showcaseExec">
        <g:hiddenField name="conceptualWorkflow" value="${conceptualWorkflow.id}"/>
	<table>
        <g:each in="${inputDataFields}" var="din">
	  <tr>
            <div class="fieldcontain required">
	      <td>
                <label for="${din.id}">
                    ${din.getDescription()}:
                </label>
	      </td><td>&nbsp;&nbsp;</td>
	      <td>
            <g:if test="${[Integer.name, String.name, Float.name, Long.name].contains(din.getDataType())}">
                <g:if test="${!predefinedValues.containsKey(din)}">
                    <g:if test="${din.optional}">
                        <g:textField name="${din.id}"/>
                    </g:if>
                    <g:else>
                        <g:textField name="${din.id}" required="" />
                    </g:else>
                </g:if>
                <g:else>
                    <g:set var="predefined" value="${predefinedValues.get(din)}"></g:set>
                    <g:if test="${din.optional}">
                            <g:select name="${din.id}" from="${predefined}" noSelection="${[null:"Not Specified"]}" ></g:select>
                    </g:if>
                    <g:else>
                        <g:select name="${din.id}"  from="${predefined}" value="${predefined.get(0)}"></g:select>
                    </g:else>

                </g:else>

            </g:if>

%{--            <g:elseif test="${din.getDataType().equals(Integer.name)|| din.getDataType().equals(Long.name)}">

                <g:if test="${din.optional}">
                    <g:textField name="${din.id}"  />
                </g:if>
                <g:else>
                    <g:textField name="${din.id}" required="" />
                </g:else>

            </g:elseif>--}%

            <g:elseif test="${din.getDataType().equals("Gene")}">

                <g:render template="chooseGene" model="[id:din.id]"></g:render>
            </g:elseif>
            <g:elseif test="${din.getDataType().equals("List")}">

                <g:select name="seperator-${din.id}"
                          from="${["New Line", ";", ","]}"
                          keys="${["New Line", ";", ","]}"/>


                <g:if test="${optional}">
                    <g:textArea name="value-${din.id}"  />
                </g:if>
                <g:else>
                    <g:textArea name="value-${din.id}" required="" />
                </g:else>

            </g:elseif>
</td>
            </div>
	    </tr>

        </g:each>
	</table>
	<br/>
        <g:actionSubmit value="Submit query" action="showcaseExec"/>

    </g:form>



</body>
</html>



