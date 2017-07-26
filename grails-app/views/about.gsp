<!DOCTYPE html>
%{--
TODO too much static Styles -> Refactor
--}%
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Cancersys - About</title>
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

<div>
  <h1>About the CancerSysDB</h1><br/>

        <p align="justify">
            The Cancer Systems Biology Database (CancerSysDB) is an information
            system for integrative data analysis of molecular cancer datasets across
            data types, across cancer types as well as across multiple cancer
            studies. The public instance contains published data from The Cancer
            Genome Atlas (TCGA).</p>

        <p align="justify">
            Apart from using this public instance, scientists can establish a
            private instance of the CancerSysDB, link this instance to their own
            cancer genomics pipelines and thus set up their local infrastructure for
            data organisation and integrative analysis. This will finally speed up
            the computational procedures in clinical cancer research and open
            scientists enhanced perspectives on their data. The system is available
            for download as a WAR file which can be easily plugged into an existing
            Tomcat installation on a local server.
        </p>

        <p align="justify">
            The CancerSysDB is a joint effort of the CECAD Bioinformatics Platform
            at the University of Cologne/Germany, the research group "Computational Biology" at
            the Institut de Biologie du Developpement, Aix-Marseille University/France, and the
            Regional Computing Center of the University of Cologne/Germany (RRZK). The
            project is funded by the German Research Foundation (DFG).
        </p>

  <p align="justify"><b>By using data from <a href="https://cancergenome.nih.gov" target="_blank">The Cancer Genome Atlas (TCGA)</a> through the CancerSysDB, you obligate yourself to adhere to the <a href="https://gdc.cancer.gov/access-data/data-access-policies" target="_blank">data access policies</a> that apply to the usage of publicly available TCGA data.</b></p>


%{--    <asset:image src="logos/Rrzk.png" height="60"/>
    <asset:image src="logos/logo_cecad.png" height="60"/>
    <asset:image src="logos/mpg.png" height="60"/>
    --}%%{--
        <asset:image src="logos/MPI-biochem.gif" height="60" />
    --}%%{--
    <asset:image src="logos/UoC.png" height="60"/>
    <asset:image src="logos/funded-by-dfg.jpg" height="60"/>--}%

</div>


%{--
    <a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div id="status" role="complementary">
        <h1>Application Status</h1>
        <ul>
            <li>App version: <g:meta name="app.version"/></li>
            <li>Grails version: <g:meta name="app.grails.version"/></li>
            <li>Groovy version: ${GroovySystem.getVersion()}</li>
            <li>JVM version: ${System.getProperty('java.version')}</li>
            <li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
            <li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
            <li>Domains: ${grailsApplication.domainClasses.size()}</li>
            <li>Services: ${grailsApplication.serviceClasses.size()}</li>
            <li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
        </ul>
        <h1>Installed Plugins</h1>
        <ul>
            <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
                <li>${plugin.name} - ${plugin.version}</li>
            </g:each>
        </ul>
    </div>
    <div id="page-body" role="main">
        <h1>Welcome to Grails</h1>
        <p>Congratulations, you have successfully started your first Grails application! At the moment
           this is the default page, feel free to modify it to either redirect to a controller or display whatever
           content you may choose. Below is a list of controllers that are currently deployed in this application,
           click on each to execute its default action:</p>

        <div id="controller-list" role="navigation">
            <h2>Available Controllers:</h2>
            <ul>
                <g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
                    <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
                </g:each>
            </ul>
        </div>
    </div>
--}%</body>
</html>
