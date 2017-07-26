<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main">
</head>

<body>

<h1>Tools</h1><br>

This pages describes external tools which can be used for the database.


<h4 style="margin-top: 20px">Upload tools</h4>
The upload tools enable batch uploads, for example from HPC clusters which are executing complex analyses.<br><br>

<h5>File upload script</h5>
Python-based command line tool to upload structured data to the database.<br>
Please call <span style="font-family: 'Courier New, monospace';font-weight: lighter;background-color: #C3C3C3">python UploadScript.py -h</span> for command description.<br>
<g:link action="toolsDownload" params='[filename:"UploadScript.py"]'>Download</g:link>

<br><br>

<h5>Directory upload script</h5>
Python-based command line tool to upload entire directories to the database.<span style="font-weight: bold"> Requires the file upload script.</span>
<br>
Please call <span style="font-family: 'Courier New, monospace';font-weight: lighter;background-color: #C3C3C3">python DirectoryUpload.py -h</span> for command description.
<br>
<g:link action="toolsDownload" params='[filename:"DirectoryUpload.py"]'>Download</g:link>

<br><br>
<h5>Upload watchdog</h5>
Python-based command line tool to automatically upload new data to the database.<span style="font-weight: bold"> Requires file upload script and directory upload script.</span>
<br>
Please call <span style="font-family: 'Courier New, monospace';font-weight: lighter;background-color: #C3C3C3">python CancersysUploadWatchdog.py -h</span> for command description.
<br>
<g:link action="toolsDownload" params='[filename:"CancersysUploadWatchdog.py"]'>Download</g:link>


</body>
</html>
