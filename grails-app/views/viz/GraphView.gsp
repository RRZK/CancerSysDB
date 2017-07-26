

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>

    <title>Graph Container</title>


    <asset:javascript src="sigmajs/sigma.js"/>
    <asset:javascript src="sigmajs/plugins/sigma.parsers.gexf.min.js"/>
    <style type="text/css">
    #GraphContainer {
        max-width: 100%;
        height: 700px;
        margin: auto;
    }
    </style>
</head>

<body>
<div id="GraphContainer"></div>

<script>
    sigma.parsers.gexf('${raw(createLink( controller: "Graph", action: graphaction,params: [partitionNumber:partitionNumber,dataset:dataset]))}', {
        container: 'GraphContainer',
        settings: {
            defaultNodeColor: '#AA3F39',
            defaultEdgeColor: '#AA7639',
            edgeColor:"default",
            minArrowSize: 2,
            minNodeSize: 1,
            maxNodeSize:4
        }
    });
</script>
</body>
</html>