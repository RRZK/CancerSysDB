<br/>
<br/>
<h4>Upload clinical meta data</h4>
<p align="justiy">In order to assign clinical data to a particular patient n the CancerSysDB, the system expects these information to be specified xml or csv format. The XML format follows the  <a href="https://wiki.nci.nih.gov/display/TCGA/Clinical+Data+Overview">TCGA clinical-data description</a>. The csv-file format needs a Patient, key and Value Column. The patient column should contain the Patient Barcode/ID the Key and Value can be choosen at will.</p>
<g:uploadForm name="MetaUpload" id="MetaUpload" action="createMetadata">

    <input id="metaUploadFileSelector" type="file" name="Metadatafile"/><br/><br/>
    <g:submitButton name="createmeta" id="createmetaButton" value="Upload"></g:submitButton>

</g:uploadForm>
