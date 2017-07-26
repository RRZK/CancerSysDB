
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Query Upload</title>
    <meta name="layout" content="main">

</head>

<body>
<sec:ifAnyGranted roles="ROLE_ADMIN">
    <g:uploadForm name="UploadWorkflow" id="UploadWorkflow" action="uploadWorkflow">
        <h1>Upload custom query</h1><br>
        <label for="metaFileUploadSelector">Query description as a JSON file</label>
        <input id="metaFileUploadSelector" type="file"  accept=".json" name="Metadatafile"/><br/><br/>


        <label for="ZipFileUploadSelector">Zip archive containing all files for query execution</label>

        <input id="ZipFileUploadSelector" type="file" accept=".zip"  name="ZipFile"/><br/><br/>

        <g:submitButton name="createworkflow" id="createWorkflow" value="Upload Query"/>

    </g:uploadForm>
</sec:ifAnyGranted>

</body>
</html>
