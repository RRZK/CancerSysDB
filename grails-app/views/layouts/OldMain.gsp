<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>CancerSysDB</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
    <g:layoutHead/>
</head>

<body>
<asset:image src="csyslogo.png" height="100"/>



%{--<div>  <sec:ifLoggedIn><sec:username/> - <a href="logout" >logout</a></sec:ifLoggedIn></div>--}%
<div class="row">

    %{--<nav:menu id="mainMenu" class="" scope="user" >

    </nav:menu>--}%
    <nav>
        <nav:primary id="priMenu" class="" scope="user">

        </nav:primary>

        <nav:secondary id="secMenu" class="" scope="user">

        </nav:secondary>
    </nav>
</div>
<g:layoutBody/>


<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
<footer class="footer">Footer</footer>

</body>
</html>
