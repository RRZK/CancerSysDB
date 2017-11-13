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

A database: MariaDB/MySQL (strongly recommended). GORM framework should make it database-independent, but this has not been tested.

Run command "grails war" to build a WAR file.

### Modes (private/public)

The database has two modes:
 
The private mode (standard): To do anything (run workflows), you have to be logged in.
 
The public mode: Here, the database operates as an open portal like on https://cancersys.uni-koeln.de/ . Workflows can be run without login.

### Docker

Execute the commands:

    ./InstallDockerCompose.sh
    ./RunDockerCompose.sh

#### Requirements
Run Docker image or Docker Compose to get a test instance. This will create a Docker enviroment where the app is running on the first and the database in another container.

You need to install [docker](https://www.docker.com/) and [docker-compose](https://docs.docker.com/compose/). 

On Ubuntu, you can easily install these with apt: 

    apt-get install docker docker-compose


The process will consume a lot of diskspace (<4GB) and a lot of time and bandwidth.


#### Details

[dockerPrepare.sh](dockerPrepare.sh) Builds the application, downloads the data and prepares it to be parsed by the MariaDB Docker container.

[InstallDockerCompose.sh](InstallDockerCompose.sh) Calls dockerPrepare.sh and builds image for Docker Compose.

[RunDockerCompose.sh](RunDockerCompose.sh) Calls docker-compose.

[docker-compose docker/docker-compose](docker/docker-compose) 

### Manual Build

These are the instructions to integrate and configurate the database to be run in an application container like Tomcat.

#### Configuration (Database)

Before building the application, you can configure:

[Config.groovy](grails-app/conf/Config.groovy)

and

[DataSource.groovy](grails-app/conf/DataSource.groovy)

or use an external config file afterwards. Therefore, you can combine both files into one file named

/etc/opt/grails/csys-db-config.groovy

For an example, see Docker [csys-db-config.groovy](docker/openImage/csys-db-config.groovy)

###### DataSource
Here you can add your database configuration by enviroment. For deployment, the production environment is critical.

The supplied file should give you a clue where to put your database information.

See the [grails Documentation](http://docs.grails.org/2.5.x/guide/single.html#dataSource) for further information.

###### Config

Set the variable 

    cancersys.config.systemType
    
to public to run the database in public mode like https://cancersys.uni-koeln.de/

    cancersys.config.systemType = "public"

Also, the paths where the data is stored can be set

Standard:

        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"

_cancersys.config.tempFilepath_ sets the temporary folder for uploaded files

_cancersys.config.dataFilepath_ sets the path where executed workflows are saved.


#### Auto Build

There is the [build.sh](build.sh) which installs SDKman and Grails and then builds the project WAR file.




### Initial setup

If you start the application in the production enviroment and the data is built from scratch, an *Admin* user is created.

First login is:

    User: Admin
    Password: AdminPassword

After the first login, the password has to be changed! The next step is to upload the data into the database. It is recommended to create a new user for upload.

### Important folders

All data used by the application will go to /srv/cancersys . Please mount this folder to backup local data and workflow results.


## Workflows

As mentioned before, the workflows are in the folder [web-app/data/Workflows](web-app/data/Workflows).

To create your own workflow, look at the [Workflow Tutorials](web-app/data/Workflows/Readme.md)
## Data

Without data, the entire application will not work. 
When starting, the app with clean gene data is downloaded from BioMart.
By default, the public TCGA data is incorporated into the database.

### SQL Dump

There is an SQL dump at

http://bifacility.uni-koeln.de/cancersysdb/cancersysSQLdump.tar.gz

It has an admin user:
    
    User: Admin
    Password: AdminPassword
    
and a user which uploaded the data and is the owner of the data:

    User: User
    Password: UserPassword

### Fresh upload

The structured TCGA data is hosted and ready to import from

http://bifacility.uni-koeln.de/cancersysdb/tcgaForImport.tar.gz .

The data can be uploaded with the scripts in [Scripts](web-app/Tools). You can upload the entire data in the unpacked archive with the upload script and the directory upload script using this command:

     python DirectoryUpload.py -u UploadUser -p Userpassword -H http://localhost:8080/csys-db-pub/ -f /path/tcgaForImport -s UploadScript.py -m 

This will take a lot of time.

If you just want to test the upload functionality,
 
http://bifacility.uni-koeln.de/cancersysdb/tcgaSample.tar.gz

contains a subsample for testing purposes.

### Upload scripts

Directory upload script:

This script uses UploadScript (parameter -s) and has three stages.

* Scanning the directory to upload for XML patient data: https://wiki.nci.nih.gov/display/TCGA/Biospecimen+Core+Resource
    
* Scanning the folders for meta.json files and upload these. If sample and patient identifiers not created in step 1 are used in these files, they will be created from the context information in the meta.json files.

* Scanning all folders for files with the extension .maf and upload them as DataVariation data. In these files, only the lines which contain samples known to the database are processed (see step 1 XML patient data).


#### Upload metadata file description
 
To upload files with the script [DirectoryUpload.py](web-app/Tools/DirectoryUpload.py), the scanned directory has to contain a file describing the context, i.e. patients, samples etc. These files are called _*meta.json*_, *otherwise they will be ignored by the script*! They are in the folder together with the files they describe.


#####Structure
 
The structure of the JSON file consists of two fields:

General: Contains map with information working for all files in this directory.

Special: Exceptions for single files in this folder. The file names are the keys of the underlying map.


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



##### General/special attributes

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
This part defines how the database can be costumized to fit special needs!


### Extending Data Types

Data types in the CancerSysDB are defined by [Grails Domain Classes](http://docs.grails.org/2.5.x/ref/Domain%20Classes/Usage.html).
The data types can be extended without touching other code (except for the [dataset class](grails-app/domain/de/cancersysdb/Dataset.groovy)).

All data types are attached to datasets which

* define which data is associated into a dataset,
* restrict the access,
* define contexts like samples, descriptions and file origins.

All data types are organized in the package _[de.cancersys.data](grails-app/domain/de/cancersysdb/data)_

This package creates a base for classes that hold data.

All classes in this package start with the keyword " _Data_ "

There are three types of classes:

##### General data class

They need to implement the _DataEntryInterface_ . This interface just manages the requirement that a dataset is referenced.

##### Annotation data class

These annotations define, for instance, associations to genes. They can be handled by the generic CSV importer.

##### Group data class

These classes assemble groups of datasets.


#### Data class attributes

Data classes are normal Grails Domain Objects and can be modelled like those.

All normal Java data types are supported (like Long,Integer,Float String etc.). [Gene](grails-app/domain/de/cancersysdb/geneticStandards/Gene.groovy) Data types can be used as references in domain classes. The automatic importers will handle and match thus data. 

#### Optional fields

To add an optional field in a dataset, use the standard GORM constraint _nullable_ . This will make fields optional and will also be evaluated while importing CSV Files.

#### Useful interfaces

There are interfaces which help to normalize functions: 

_GenPosInterface_ defines a position in the genome. 

SingleLineDataset defines that there is just one line in the dataset. 
 
BinaryDataDataset defines that there is Binary Data in the dataset.

###Extending Import capabilities

Imports can be managed in multiple ways since most files in genomics are CSV file formats presenting a lot of variation in the column values. External CSV data can be mapped to the internal data representation. For formats like VCF, special importers must be written.

Single CSV files where the column headers cannot be mapped automatically to the internal database formats must be mapped manually. After choosing an internal destination and a file to upload, a dialog is shown where the file columns can be mapped to internal fields. Optional values can be left out by choosing the value DO NOT MAP.

#### Automated imports

Automated directory imports per directory upload script. Here, a CSV-based description, corresponding to the single file upload can be described in the mapping section of a meta.json upload description.

#### Special importers

From a technical perspective, there are basically two ways to import datasets into the database:

Generic CSV imports: these imports are managed by the class [de.cancersys.Import.GenericCSVImporterService](grails-app/services/de/cancersysdb/Import/GenericCSVImporterService.groovy) which uses a mapping to map the external file to the internal data model. Thus, all the required fields in the internal data model must be present in the external data model.

Special imports for formats like VCF: special importers must be written. Those are managed by the [FileImportService](grails-app/services/de/cancersysdb/Import/FileImportService.groovy). This service manages as the importers implementing [ImporterServiceInterface](src/groovy/de/cancersysdb/ImportTools/ImporterServiceInterface.groovy). Examples are given in the [Package de.cancersys.Importers](grails-app/services/de/cancersysdb/Importer)
<pre><code class="groovy">
/**
 * This interface defines input functions and static variable arrays which define what an ImporterService class can transform to the database!
 */
interface ImporterServiceInterface {
    /**
     * This variable contains data types as keys and target classes as lists.
     * This can be read so the best importer for a task can be found.
     */
    static Map<String,List> MapsTo
    /**
     * This map contains the matching patterns for the maps to defined file types.
     * This can either contain possible file name ends or a lot of regex patterns to match the filename.
     * A regex pattern will always be chosen with higher priority while determining a suitable importerService
     */
    static Map<String,List> FilenamePattern
    /**
     * This is the main function which has to be implemented to parse data into the database
     * @param format The format of the file
     * @param Into The table this parses into
     * @param File The file with content as string or stream
     * @param sourceFile The source file meta description from the database
     * @param owner The owner
     * @param annon Is it anonymzed?
     * @param shared Is it shared?
     * @param ip The import protocoll
     * @param ds The dataset to import the data to
     * @return Import protocol with the success status etc.
     */
    ImportProtocol importContent(String format, String Into,
            def File, SourceFile sourceFile, User owner, boolean annon, boolean shared, ImportProtocol ip, Dataset ds)



}
</pre></code>

##### Importer calling hierarchy

The [FileImportService](grails-app/services/de/cancersysdb/Import/FileImportService.groovy) tries to find a Special Importer for a file name pattern.
If the internal description works, then the importer is used to import the Dataset.
If the import is not successful, the generic CSV importer is used.

# Contact

Priv.-Doz. Dr. Peter Frommolt, CECAD Research Center, University of Cologne, Germany, peter.frommolt@uni-koeln.de
 	 
Dr. Bianca Habermann, Institut de Biologie du Developpement, Aix-Marseille University, France, bianca.habermann@univ-amu.fr
 	 
Prof. Dr. Ulrich Lang, Regional Computing Center, University of Cologne, Germany, lang@uni-koeln.de
