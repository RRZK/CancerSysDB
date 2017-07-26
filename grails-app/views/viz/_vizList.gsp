%{--

Variable VizInfos
Depends on the Manageable Data and Follows a Datamodell:

VizType -> InteractiveScatterplotViz
  VizCreateInfo -> Object
    VizInfo -> Text that Describes Infos
    CreateLink -> The Link to Create a Visualisation
  ExisitingViz -> Visualisations Existing.
    Count -> Number of Visualisations (Number)
    Existing -> Array of Links to Visualisations
--}%

<h2>Visualisations</h2>

<g:if test="${VizInfos.isEmpty()}">
    No Visualisations Possible
</g:if>
<g:else>
    <g:each in="${VizInfos}" var="ct">
        <div id='Manage_${ct.key}'>
        <h4>${ct.key}</h4>





            <ol  class="property-list">
                <li class="fieldcontain">
                    <span id="${ct.key}_description">${ct.value["VizCreateInfo"]["VizInfo"]}</span><br/>
                </li>
                <li class="fieldcontain">
                    <a href="${ct.value["VizCreateInfo"]["CreateLink"]}">create</a><br/><br/>
                </li>
                <li class="fieldcontain">
                    <g:if test="${ct.value["ExisitingViz"]["Count"] >0}">
                        <span>Existing Visualisations: ${ct.value["ExisitingViz"]["Count"]}</span><br/>
                    </g:if>
                    <g:else>
                        <span>no existing visualisations</span>
                    </g:else><br/>

                </li>
        <g:each in="${ct.value["ExisitingViz"]["Existing"]}" var="bla">
                <li class="fieldcontain">
                    <a href="${bla["link"]}">${bla["name"]}</a><br/>

                </li>
        </g:each>

            </ol>



        </div>






    </g:each>
</g:else>

