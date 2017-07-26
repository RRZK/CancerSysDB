# CancerSysDB

## About
The Cancer Systems Biology Database (CancerSysDB) is an information system for integrative analysis of molecular cancer datasets across data types, across cancer types as well as across multiple cancer studies. The public instance contains published data from The Cancer Genome Atlas (TCGA).

Apart from using this public instance, scientists can establish a private instance of the CancerSysDB, link this instance to their own cancer genomics pipelines and thus set up their local infrastructure for data organisation and integrative analysis. This will finally speed up the computational procedures in clinical cancer research and open scientists enhanced perspectives on their data. The system is available for download as a WAR file which can be easily plugged into an existing Tomcat installation on a local server.

The CancerSysDB is a joint effort of the CECAD Bioinformatics Platform at the University of Cologne/Germany, the research group "Computational Biology" at the Institut de Biologie du Developpement, Aix-Marseille University/France, and the Regional Computing Center of the University of Cologne/Germany (RRZK). The project is funded by the German Research Foundation (DFG).

By using data from The Cancer Genome Atlas (TCGA) through the CancerSysDB, you obligate yourself to adhere to the data access policies that apply to the usage of publicly available TCGA data. For details, please refer to https://gdc.cancer.gov/access-data/data-access-policies .


## Files and Folders

[Basic Gene Data](web-app/data/BasisData) which cannot be downloaded from the web.

[Workflows](web-app/data/Workflows) for data processing. To see how they work, please refer to the
[Workflow Tutorials](web-app/data/Workflows/Readme.md).

[Docker](Docker) Files to deploy.

[Test Data](testData) which is used in the automated tests.

[Scripts](web-app/Tools) for uploading data into the web application.

## Installation

### Requirements
Install Grails 2.5.5 - download from https://grails.org/ or install with SDKMan .

A Database: MariaDB/MySQL(Strongly recommended!). GORM Framework SHOULD make it Datbase Independent but this isnt tested.

Run command "grails war" to build a WAR file.

### Modes (Private/Public)

The Database has two Modes:
 
The Private mode (Standard): To do anything (run workflows) you have to be logged in.
 
The Public mode: Here the Database operates as open portal like on https://cancersys.uni-koeln.de/ . Workflows can be run whiout login.

### Docker

Execute the Commands:

    ./InstallDockerCompose.sh
    ./RunDockerCompose.sh

#### Requirements
Run Docker image or Docker Compose to get a test instance. This will create an Docker Enviroment were the App is running on the First and the Database in  annother container.

You need to install [docker](https://www.docker.com/) and [docker-compose](https://docs.docker.com/compose/). 

On Ubuntu you can easily install it with apt: 

    apt-get install docker docker-compose


The process will consume alot (<4GB) diskspace and alot of time and Bandwith.


#### Details

[dockerPrepare.sh](dockerPrepare.sh) Builds the Application, Downloads the Data and preparse it tobe parsed by the mariadb Docker Container

[InstallDockerCompose.sh](InstallDockerCompose.sh)  calles dockerPrepare.sh and builds image for docker compose.

[RunDockerCompose.sh](RunDockerCompose.sh)  calls docker-compose

[docker-compose docker/docker-compose](docker/docker-compose) 

### Manual Build

These are the Instructions to integrate and Configurate the Database tobe run in an Application container like tomcat.

#### Configuration (Database)

Before building the Application you can configure:

[Config.groovy](grails-app/conf/Config.groovy)

and

[DataSource.groovy](grails-app/conf/DataSource.groovy)

or use an External Config file afterwards: Therefor you can Combine both Files into one file with the Name :

/etc/opt/grails/csys-db-config.groovy

for an example see docker [csys-db-config.groovy](docker/openImage/csys-db-config.groovy)

###### DataSource
Here you can add your Database Configuration by Enviroment. For Deployment the procution enviroment is critical.

The supplied File should give you a Clue where to put your Database Information.

See the [grails Documentation](http://docs.grails.org/2.5.x/guide/single.html#dataSource) for futher Information.

###### Config

Set the Variable 

    cancersys.config.systemType
    
to Public to Run the Database in Public mode like https://cancersys.uni-koeln.de/

    cancersys.config.systemType = "public"

Also the Paths where the Data is Stored can be set

Standard:

        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"

_cancersys.config.tempFilepath_ sets the Temporary folder for Uploaded Files

_cancersys.config.dataFilepath_ sets the path where executed Workflows are Saved.


#### Auto Build

There is the [build.sh](build.sh) which installs SDKman and grails an then builds the project war file.




### Initial setup

If you start the application in the production enviroment and the data is built from scratch, an *Admin* user is created.

First login is:

    User: Admin
    Password: AdminPassword

After the first login, the password has to be changed! The next step is to upload the data to the database. It is recommended to create a new user for upload.

### Important folders

All data used by the application will go to /srv/cancersys . Please mount this folder to backup local data and workflow results.


## Workflows

As mentioned before, the workflows are in the folder [web-app/data/Workflows](web-app/data/Workflows).

To create your own workflow look at the [Workflow Tutorials](web-app/data/Workflows/Readme.md)
## Data

Without Data the Project does not work. 
When starting the App with an Clean Gene Data is downloaded from biomart.
By Standard the Public TCGA Data is incorporated into the Database.

### SQL Dump

There is an SQL Dump at

http://bifacility.uni-koeln.de/cancersysdb/cancersysSQLdump.tar.gz

It has an admin User:
    
    User: Admin
    Password: AdminPassword
    
and a User which uploaded the Data and is owner of the Data:

    User: User
    Password: UserPassword

### Fresh upload

The structured TCGA data is hosted and ready to import from

http://bifacility.uni-koeln.de/cancersysdb/tcgaForImport.tar.gz .

The data can be uploaded with the scripts in [Scripts](web-app/Tools). You can upload the entire data in the unpacked archive with the upload script and the directory upload script using this command:

     python DirectoryUpload.py -u UploadUser -p Userpassword -H http://localhost:8080/csys-db-pub/ -f /path/tcgaForImport -s UploadScript.py -m 

This will take a lot of time.

if you just want to test  Upload functionality
 
http://bifacility.uni-koeln.de/cancersysdb/tcgaSample.tar.gz

contains a Subsample for Testing reasons.

### Upload scripts

Directory upload script:

This script uses UploadScript (parameter -s) and has three stages.

* Scanning the directory to upload for XML patient data: https://wiki.nci.nih.gov/display/TCGA/Biospecimen+Core+Resource
    
* Scanning the folders for meta.json files and upload these. If sample and patient identifiers not created in step 1 are used in these files, they will be created from the context information in the meta.json files.

* Scanning all folders for files with the extension .maf and upload them as DataVariation data. In the files, only the lines which contain samples known to the database are processed (see step 1 XML patient data).


#### Upload metadata file description
 
To upload files with the script [DirectoryUpload.py](web-app/Tools/DirectoryUpload.py), the scanned directory has to contain a file describing the context, i.e. patients, samples etc. These files are called _*meta.json*_, *otherwise they will be ignored by the script*! They are in the folder together with the files they describe.


#####Structure
 
The structure of the JSON file consists of two fields:

General: contains map with information working for all files in this directory

Special: exceptions for single files in this folder. The file names are the keys of the underlying map.


<pre>
{
    "General":
    {
        GeneralAttribues...
        "Contexts":
        [
            {ContextAttributes}
        ]
        Mapping:{....}

        }

    "Special":{
        SpecialAttributes...
        "Filename":{
            ContextDescription...
            "Contexts":[
                {ContextDescription}
            ]
        }

    }
}

</pre>



##### General/special attribues


|Attribute|Description|
|----|----|
|DataProvider| Name of the organization providing the data|
|Origin|The web address where the data is downloaded from|
|URI|The exact “Unified Resource Identifier” (URI) of the data|
|Description| A description of the dataset|
|DataType| The target where to put the data|
|TCGALogic| Boolean which indicates whether the data follows TCGA standard (inherent barcode logic)|
|ContextsPath|Path with “biospecies.xml” files or similar files which describe the context. This parameter is only useful if the clinical data is OUTSIDE the upload folder if you use the directory upload script|
|NoPatientContext| Boolean - no sample or patient context is attached|
|NoGeneralContexts| Boolean - in a special context, it rules out that the general "contexts" are not attached.|
|Mapping| This complex field is used for the import into the database, see Mapping|

##### Contexts 

Array of objects, describing samples to be linked to the dataset. If the explicit information identifier already exists, all data will be ignored and the existing data will be used.


|Attribute|Description|
|----|----|
|SampleBarcode|Identifies a sample|
|PatientBarcode|Identifies a patient|
|Pseudonyme|Pseudonyme of the patient|
|TCGALogic| Follows TCGA logic|
|Cancertype| The cancer type following the TCGA short letter codes. Required if sample does not exist in the database|
|TissueType|The sample type following the TCGA short letter codes. Required if sample does not exist in the database|
|location|The location the sample was taken from TCGA short letter codes. Required if sample does not exist in the database|
|Study|Identifier of the study (NOT the description of the File). This can be described in the description section|

##### Mapping

Mapping describes the mapping of the columns in the CSV file to import to the database. It can be left out or blank if no mapping information is needed. If the database has an importer which can handle the file types, no mapping is needed. 

|Attribute|Description|
|----|----|
|authorityName| Choose yourself, should be unique|
|fieldsInFile| Headlines in the CSV|
|fieldTypes|  Map/dict key = Column headline names to column types|
|fieldMapping| Map/dict key = Number if the column starts with 0, value=Name of database field|
|Seperator| Character used to separate columns in data to import|
|Enclosed| Character used to enclose fields in data to import|


##### Examples

###### Genetic Function Genes

In this folder, there is a CSV file which describes generic functions of genes. It is added to the genetic function table. The fields in the file are mapped into the database. There is no explicit patient or sample connections since it is abstract information.
<pre><code class="json">
{
  "General": {
    "DataProvider": "LMU Munich",
    "Origin": "",
    "Description": "genes associated to mitochondria",
    "DataType": "GeneticFunction",
    "Annon": true,
    "NoPatientContext": true,
    "Mapping": {
      "headline": true,
      "authorityName": "MunichMitoGenefunctionsToVariation",
      "fieldsInFile": ["Gene_Function", "Process", "ID"],
      "fieldTypes": {
        "ID": "Gene",
        "Process": "String",
        "Gene_Function": "String"
      },
      "fieldMapping": {
        "0": "gene_Function",
        "1": "process",
        "2": "gene"
      },
      "Seperator": "\t"
    }
  }
}

</pre></code>

## Extending the Database
This part defines how the Database can be costumized to fit special Needs!


### Extending Data Types


Dataypes in Cancersys are defined by [Grails Domain Classes](http://docs.grails.org/2.5.x/ref/Domain%20Classes/Usage.html).
The Datatypes can be extended without touching other code (except for the [dataset class](grails-app/domain/de/cancersysdb/Dataset.groovy)).

All Data types are Attached to Datasets which

* Define which data is asociated into a dataSET
* Restrict the access
* Define Contexts like Samples, Descriptions and File Origins.

All Data Types are Organized in the Package _[de.cancersys.data](grails-app/domain/de/cancersysdb/data)_

This Package Creates a Base for Classes That Hold Data This Data.

All classes in this Package start with the Keyword " _Data_ "

There are three Types of Classes:

##### general Dataclass

They need to Implement the _DataEntryInterface_ This interface just manages the Requirement that a Dataset is referenced.

##### Annotation Dataclass

These Annotations define asociations to genes for example. They can be handled by the generic CSV importer.

##### Group Dataclass

These Classes Assembe groups of Datasets.


#### Dataclass Attributes

Dataclasses are Normal Grails Domain Objects. They can be Modelled like those.

All normal Java Datatypes are supported(like Long,Integer,Float String etc.). [Gene](grails-app/domain/de/cancersysdb/geneticStandards/Gene.groovy) Datatype can be used as References in Domain classes. The Automatic Importers will Handle and Match thus Data. 

#### Optional Fields

To add an Optional Field in a Dataset, use the standard GORM Constraint _nullable_ . This will make Fields optional and will also be evaluated while importing CSV Files.

#### Useful Interfaces

There are Interfaces which help to Normalize Functions: 

_GenPosInterface_ defines a Position in the Genome. 

SingleLineDataset defines that there is just one Line in the Dataset. 
 
BinaryDataDataset defines that there is Binary Data in the Dataset.

###Extending Import capabilities

Imports can be Managed in multiple ways,
Since the Most Files in Genertics are CSV File Formats which present alot of Variation in the Column Values. External CSV Data can be Mapped to The Internal Datarepresentation. For Formats like vcf etc, special Importers must be written.


Single csv files where the Column headers cant be Mapped automatically to the Internal Database Formats must be mapped manualy. After Choosing an Internal Destination and a file to Upload, a Dialog is shown where the Files columns can be Mapped to Internal Fields. Optionolal Values canbe left out by choosing the Value, DO NOT MAP

#### Automated Imports

Automated Directory Imports per directory Upload script. here a CSV Based description, correspontig to the Single File Upload can be described in the mapping section of a meta.json Upload Description.

#### Special importers

From a technical Perspective there are basically two ways to Import datasets into the Database:

Generic CSV Imports These Imports are Managed by the Class [de.cancersys.Import.GenericCSVImporterService](grails-app/services/de/cancersysdb/Import/GenericCSVImporterService.groovy) . This class uses a Mapping to Map the External File to the Internal Datamodell. Therefore all the required Fields in the Internal Datamodell must be present in the External Datamodell.

Special Imports For Formats like vcf etc, special Importers must be written. Those are Managed by the [FileImportService](grails-app/services/de/cancersysdb/Import/FileImportService.groovy). This Services Manages as the Importers implementing [ImporterServiceInterface](src/groovy/de/cancersysdb/ImportTools/ImporterServiceInterface.groovy). Examples are found in the [Package de.cancersys.Importers](grails-app/services/de/cancersysdb/Importer)
<pre><code class="groovy">
/**
 * This Interface Defines Input Functions and Static Variable Arrays Which Define what an ImporterService Class can transform to the Database!
 */
interface ImporterServiceInterface {
    /**
     * This Variable Contains Datatypes as Keys and Target Classes as Lists.
     * This can be read so The best importer for a Task can be found.
     */
    static Map<String,List> MapsTo
    /**
     * This Map Contains the Matiching Patterns for the Maps to Defined File Types.
     * This can either contain Possible File Name Ends or a Lot of Regex Patterns to match The Filename
     * A Regexpattern will allways be choosen with Higher Priority while determiting an suitable importerService
     */
    static Map<String,List> FilenamePattern
    /**
     * This is the Main Function which hastobe Implemented to Parse Stuff into the Database
     * @param format The Format of The File
     * @param Into The Table this Parses into
     * @param File The file with Content as String or Stream
     * @param sourceFile The Source file Meta Description form the Database
     * @param owner The Owner
     * @param annon Is it Annonmyzed
     * @param shared is it Shared
     * @param ip The Import Protocoll
     * @param ds The Dataset to import the Stuff to
     * @return Import protocoll with the Successstatus etc
     */
    ImportProtocol importContent(String format, String Into,
            def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds)



}
</pre></code>

##### Importer Calling Hierarchy

The [FileImportService](grails-app/services/de/cancersysdb/Import/FileImportService.groovy) tries to find a Special Importer for a File name pattern.
If The internal Description works then the Importer is used to Import the Dataset.
If the Import is not Successful then the Generic CSV Importer is Used.

# Contact

Priv.-Doz. Dr. Peter Frommolt, CECAD Research Center, University of Cologne, Germany, peter.frommolt@uni-koeln.de
 	 
Dr. Bianca Habermann, Institut de Biologie du Developpement, Aix-Marseille University, France, bianca.habermann@univ-amu.fr
 	 
Prof. Dr. Ulrich Lang, Regional Computing Center, University of Cologne, Germany, lang@uni-koeln.de
