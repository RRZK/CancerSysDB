<!DOCTYPE html>
%{--
TODO too much static Styles -> Refactor
--}%
<html>
<head>
    <meta name="layout" content="main"/>
    <title>CancerSysDB - Contact</title>
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
    <h1>Contact</h1><br/>

  <table>
    <tr>
      <td><asset:image src="logo_cecad.png" height="60px"/></td>
      <td>&nbsp; &nbsp; &nbsp; &nbsp;</td>
      <td>
        <b>Priv.-Doz. Dr. Peter Frommolt</b><br/>
        CECAD Research Center<br/>
	University of Cologne<br/>
        <a href="mailto:&#112;&#101;&#116;&#101;&#114;&#046;&#102;&#114;&#111;&#109;&#109;&#111;&#108;&#116;&#064;&#117;&#110;&#105;&#045;&#107;&#111;&#101;&#108;&#110;&#046;&#100;&#101;">&#112;&#101;&#116;&#101;&#114;&#046;&#102;&#114;&#111;&#109;&#109;&#111;&#108;&#116;&#064;&#117;&#110;&#105;&#045;&#107;&#111;&#101;&#108;&#110;&#046;&#100;&#101;</a>
      </td>
    </tr>

    <tr><td>&nbsp;</td><td>&nbsp;</td></tr>

    <tr>
      <td><asset:image src="ibdm.png" height="60px"/></td>
      <td>&nbsp;</td>
      <td>
        <b>Dr. Bianca Habermann</b><br/>
        Institut de Biologie du Developpement<br/>
	Aix-Marseille University, France<br/>
	<a href="&#109;&#097;&#105;&#108;&#116;&#111;&#058;&#098;&#105;&#097;&#110;&#099;&#097;&#046;&#104;&#097;&#098;&#101;&#114;&#109;&#097;&#110;&#110;&#064;&#117;&#110;&#105;&#118;&#045;&#097;&#109;&#117;&#046;&#102;&#114;">&#098;&#105;&#097;&#110;&#099;&#097;&#046;&#104;&#097;&#098;&#101;&#114;&#109;&#097;&#110;&#110;&#064;&#117;&#110;&#105;&#118;&#045;&#097;&#109;&#117;&#046;&#102;&#114;</a><br/>
      </td>
    </tr>

    <tr><td>&nbsp;</td><td>&nbsp;</td></tr>

    <tr>
      <td><asset:image src="zaik_rrzk.jpg" width="60px"/></td>
      <td>&nbsp;</td>
      <td>
        <b>Prof. Dr. Ulrich Lang</b><br/>
        Regional Computing Center<br/>
	University of Cologne, Germany<br/>
	<a href="mailto:&#108;&#097;&#110;&#103;&#064;&#117;&#110;&#105;&#045;&#107;&#111;&#101;&#108;&#110;&#046;&#100;&#101;">&#108;&#097;&#110;&#103;&#064;&#117;&#110;&#105;&#045;&#107;&#111;&#101;&#108;&#110;&#046;&#100;&#101;</a>
      </td>
    </tr>
    
    </table>


</div>

</body>
</html>
