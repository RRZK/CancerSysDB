<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Data</title>
    <asset:javascript src="d3toolbox.js"/>
    <asset:javascript src="d3.js"/>
    <script>

        /**
         *
         * This Class Incorporates the Datamodel and creates an Clickable NavigationObject
         *
         * This script needs a File Called DataModell.svg
         * There are ElementGroups with the IDs of the Datamodell
         * */


        function DataModelNavigation() {
            d3.xml("${assetPath(src:'DataModell.svg')}",
                "image/svg+xml", function (xml) {

                    //The Element where The SVG Should be Integrated to
                    var main_chart_div = d3.select("#dtmdl");
                    //Extracting the SVG Content and Pushing it to the Element to Inegrate
                    IntegrateSVGFromFileToPage(xml, main_chart_div);

                    var svgNode = main_chart_div.select("svg");


                    //Add the Links to The Named Sections inside the SVG!

                    var innerSVG = svgNode.select("#StudiesGroups");

                    innerSVG.style("cursor", "pointer").on("click", function () {
                        window.location.href = '${createLink( controller:"study", action:"index")}';
                    });

                    innerSVG = svgNode.select("#PatientsGroups");

                    innerSVG.style("cursor", "pointer").on("click", function () {
                        window.location.href = '${createLink( controller:"patient", action:"index")}';
                    })
                    //This is a Prototype for an Mouse Over color Change May be Implemented in the Future
                    /*
                     .on("mouseover", function(d) {
                     d3.select(this).transition()
                     .ease('cubic-out')
                     .duration('200')
                     .style("fill", "red");
                     })
                     .on("mouseout", function(d) {
                     d3.select(this).transition()
                     .ease('cubic-out')
                     .duration('200')
                     .style("fill", "#fff8ee");
                     })*/
                    ;

                    innerSVG = svgNode.select("#SamplesGroups");

                    innerSVG.style("cursor", "pointer").on("click", function () {
                        window.location.href = '${createLink( controller:"sample", action:"index")}';
                    });

                    innerSVG = svgNode.select("#DatasetsGroups");

                    innerSVG.style("cursor", "pointer").on("click", function () {
                        window.location.href = '${createLink( controller:"dataset", action:"index")}';
                    });

                });
        }
    </script>

    <style type="text/css" media="screen">
    #status {
        background-color: #eee;
        border: .2em solid #fff;
        margin: 2em 2em 1em;
        padding: 1em;
        width: 12em;
        float: left;
        -moz-box-shadow: 0px 0px 1.25em #ccc;
        -webkit-box-shadow: 0px 0px 1.25em #ccc;
        box-shadow: 0px 0px 1.25em #ccc;
        -moz-border-radius: 0.6em;
        -webkit-border-radius: 0.6em;
        border-radius: 0.6em;
    }

    .ie6 #status {
        display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
    }

    #status ul {
        font-size: 0.9em;
        list-style-type: none;
        margin-bottom: 0.6em;
        padding: 0;
    }

    #status li {
        line-height: 1.3;
    }

    #status h1 {
        text-transform: uppercase;
        font-size: 1.1em;
        margin: 0 0 0.3em;
    }

    #page-body {
        margin: 2em 1em 1.25em 18em;
    }

    h2 {
        margin-top: 1em;
        margin-bottom: 0.3em;
        font-size: 1em;
    }

    p {
        line-height: 1.5;
        margin: 0.25em 0;
    }

    #controller-list ul {
        list-style-position: inside;
    }

    #controller-list li {
        line-height: 1.3;
        list-style-position: inside;
        margin: 0.25em 0;
    }

    @media screen and (max-width: 480px) {
        #status {
            display: none;
        }

        #page-body {
            margin: 0 1em 1em;
        }

        #page-body h1 {
            margin-top: 0;
        }
    }
    </style>
</head>

<body>

<h1>Data available in the CancerSysDB</h1><br/>
<h4>Basic setup of the CancerSysDB data model</h4><br/>
<p align="justify">The data model of the CancerSysDB is basically composed of the concept of studies, patients, samples and data sets.<p>
<div id="dtmdl">
</div>
<script>
    DataModelNavigation();

</script>
<p align="justify">Every patient is assigned to exactly one study in the CancerSysDB. Several samples can be obtained from the same patient, e.g. a tumor and a non-tumor sample or several specimens taken at different stages of a patient's disease or after different therapeutical attempts.</p>
<p align="justify">A dataset can be derived from only one sample (e.g. for an analysis of the tumor's gene expression) as well as from a combination of samples (e.g. a tumor and a non-tumor samples for the analysis of somatic genomic alterations).</p>

%{--
<p><g:link controller="study" action="index">Studies</g:link> </p>
<p><g:link controller="dataset" action="index">Datasets</g:link></p>
<p><g:link controller="patient" action="index">Patients</g:link> </p>
<p><g:link controller="sample" action="index">Samples</g:link> </p>
--}%

</body>

</html>
