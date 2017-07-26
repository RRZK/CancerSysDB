<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>

    <asset:javascript src="jquery"/>
    <asset:javascript src="spring-websocket"/>

    <script type="text/javascript">
        $(function () {
            var socket = new SockJS("${createLink(uri: '/stomp')}");
            var client = Stomp.over(socket);

            <g:each in="${Menus}" var="serveItem">


            $("#" + "${serveItem.controller}").click(function () {
                client.send("/app/${serveItem.controller}", {}, JSON.stringify("${serveItem.socket}"));
            });
            client.connect({}, function () {
                client.subscribe("${serveItem.socket}", function (message) {
                    $("#reportDiv").append(message.body + "<br/>");
                    $("#reportDiv").scrollTop($("#reportDiv").get(0).scrollHeight);
                });
            });

            </g:each>
        });
    </script>


    <title>Cancersys - Import Report</title>
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
    <div class="row"><h2>Functions</h2></div>

    <div>

        <g:each in="${Menus}" var="serveItem">

            <div class="row"><button id="${serveItem.controller}">${serveItem.description}</button></div>

        </g:each>

        <h2>Report</h2>

        <div id="reportDiv" class="ReportDiv"></div>

    </div>

</div>

</body>
</html>
