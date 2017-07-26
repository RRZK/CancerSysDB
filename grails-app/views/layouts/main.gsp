<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>The Cancer Systems Biology Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
    <!-- Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,400,300,600,700' rel='stylesheet' type='text/css'>

%{--
    <!-- Bootstrap core CSS -->
    <!-- link href="css/bootstrap.css" rel="stylesheet" -->
    <!-- asset:stylesheet src="theme_cecad.css"> <!-- rel="stylesheet" -->
    <!-- Custom styles for this template -->
    <!-- link href="css/theme_cecad.css" rel="stylesheet" -->
--}%
<asset:stylesheet src="application.css"/>
<asset:javascript src="application.js"/>
<g:layoutHead/>
</head>

<body role="document" style="padding-bottom: 120px;">
<div class="container" role="main">

    <!-- Header -->
    <div class="microsite-header row">
      <div class="col-sm-12"><asset:image src="CancerSysDB-Header.png" alt="CancerSysDB logo" class="img-responsive"/></div>
    </div>
    <!-- END Header -->

    <!-- Page-Template BEGIN   -->
    <div class="pagetemplate">

      <!-- Content -->
      <div class="row">
          
	<!--Left Column -->
        <div class="col-sm-2 col-xs-12 col-lg-2 subnavi small ">
    <g:meta name="app.cancersys.config.systemType"/>
	  <nav:primary id="priMenu" class="user nav nav-pills nav-stacked" scope="user" custom="true"  >
	    <li class="${active ? 'active ':''}">
	      <p:callTag tag="g:link" attrs="${linkArgs }"><nav:title item="${item}"/></p:callTag>
	      <g:if test="${active}">
		<ul>
		  <nav:secondary scope="user" custom="true" class="subMainMenu" id="secMenu" >
		    <li class="${active ? 'active ':''}"><p:callTag tag="g:link" attrs="${linkArgs }"><nav:title item="${item}"/></p:callTag></li>
		  </nav:secondary>
		</ul>
	      </g:if>
	    </li>
	  </nav:primary>
	</div>
        <!-- End Left Column -->

        <g:if test="${grailsApplication.config.cancersys.config.systemType.equals("public") || (nonews && nonews == true )}">

        <!-- Right Column -->

    <div class="col-xs-12 col-sm-2 col-sm-push-8 col-lg-2 col-lg-push-8 ">
        <div id="NewsStory"> </div>

        <script type="application/javascript">
            $( document ).ready(function() {
                loadlatestNews("${createLink( controller: "newsStory",action: "latest")}")
            });
        </script>
        <hr>
        <g:link controller="newsStory" action="index">show all news</g:link>
    </div>

            <!-- END Right Column -->
            <!-- Middle Column -->
        <div class="col-xs-12 col-sm-8 col-sm-pull-2 col-lg-8 col-lg-pull-2">
	  <g:layoutBody/>
        </div>

        <!-- END Middle Column -->
        </g:if>
    <g:else>
        <!-- Middle Column -->
        <div class="col-xs-12 col-sm-10 col-lg-10">
            <g:layoutBody/>
        </div>

        <!-- END Middle Column -->
    </g:else>
	
      </div>
      <!-- END: Content -->
    
    </div>
    <!-- Page-Template END -->


    <!-- Footer -->
    <!-- Footer-Logos -->
    <div class="container footer-fixed">
      <hr>
      <div class="row footer-logos">
          <div class="  col-sm-5 hidden-xs">
          </div>
          <div class="  col-xs-2 col-sm-1">

              <asset:image src="logos/Rrzk.png" style="max-height: 60px;max-width: 100%; vertical-align:middle; display: block;"/>
          </div>
          <div class="col-xs-3 col-sm-2">
	<asset:image src="logos/logo_cecad.png" style="max-height: 60px;max-width: 100%; vertical-align:middle; display: block;"/>
          </div>
          <div class="col-xs-2 col-sm-1">
	<asset:image src="logos/mpg.png" style="max-height: 60px;max-width: 100%; vertical-align:middle; display: block;"/>
          </div>
          <div class="col-xs-3 col-sm-2">
	<asset:image src="logos/UoC.png" style="max-height: 60px;max-width: 100%; vertical-align:middle; display: block;"/>
          </div>
          <div class="col-xs-2 col-sm-1">
	<asset:image src="logos/funded-by-dfg.jpg" style="max-height: 60px;max-width: 100%; vertical-align:middle; display: block;"/>
          </div>
      </div>
      <!-- /Footer-Logos -->
      <div>&nbsp;</div>
    </div>
    <!-- END: Footer -->

</div>
<!-- END: container -->


<!-- Bootstrap core JavaScript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<!-- script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script -->
<asset:javascript src="bootstrap.js"/>
%{--
<!-- script src="javascripts/jquery.min.js"></script -->
<!-- script src="javascripts/bootstrap.js"></script -->
--}%
</body>
</html>
