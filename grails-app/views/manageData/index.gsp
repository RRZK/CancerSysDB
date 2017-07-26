<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<sec:ifAnyGranted roles="ROLE_USER,ROLE_MANAGER,ROLE_ADMIN">
    <head>
        <asset:javascript src="js/jquery-ui-1.10.3.custom.js"/>
        <asset:stylesheet src="themes/ui-lightness/jquery-ui-1.10.3.custom.css"/>
        <script type="text/javascript">
            $(document).ready(function () {
                $("#ImportCSV").dialog({
                    autoOpen: false,
                    width: 400,
                    height: 250,
                    buttons: [{
                        text: "OK", click: function () {
                            $("#CSVUpload").submit();
                        }
                    }]
                });
                $("#openCSVImport").on("click", function () {
                    $("#ImportCSV").dialog("open");
                });
            });
        </script>
        <meta name="layout" content="main">
        <title>Import Menu</title>
    </head>

    <body>

    <h1>Import Menu</h1>

    <p><g:link controller="manageData" action="testData">Create mock Data</g:link></p>

    <p><g:link controller="manageData" action="dataImport">Import files</g:link></p>

    <p><g:link controller="manageData" action="dataImportFromExternalSource">Import from foreign resource</g:link></p>


    <sec:ifAllGranted roles="ROLE_ADMIN">
        <h1>Administration Menu</h1>

        <p><g:link controller="manageData" action="GeneImport">Import from Biomart</g:link></p>
    </sec:ifAllGranted>

    %{--
    <p><a id="openCSVImport" class="create" href="#"><g:message message="Import From CSV"  /></a></p>
    --}%
    </body>

</sec:ifAnyGranted>
</html>