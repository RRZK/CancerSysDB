import pycurl , json ,sys, os
import json
import getpass, optparse
import string
import random
from subprocess import Popen, PIPE, STDOUT

"""
global config 
global Globalusername
global Globalpassword
"""
config = dict()

GlobalLinks = [] 
GlobalMessages = [] 
GlobalFailures = []
GlobalUploadedFiles = []
reconnectAfter = 30
AllReports = dict()
machineReadable= False
ignoreContext =False
messages = []
machineReadable = False

def globalLogin(user, password, host):
    execute = "python "+config["UploadScriptLocation"]+" -L -v -u "+user+" -p "+password+" -H "+host
    processRun = execute
    p = Popen(processRun, stdout=PIPE, shell=True)
    stdout, stderr = p.communicate()
    processoutput = stdout
    #printOutput(processoutput,filename)



def globalLogout(host):
    execute = "python "+config["UploadScriptLocation"]+" -v -X -H "+host
    processRun = execute
    p = Popen(processRun, stdout=PIPE, shell=True)
    stdout, stderr = p.communicate()
    processoutput = stdout


def log(toLog):
    if(machineReadable):
        GlobalMessages.append(toLog)
    else:
        print toLog

#Check the Standard Folder where to Look for stuff (Like Biospecimen files)
def checkStandardContext():
    #UploadContexts
    for dirname, dirs, files in os.walk(config["FolderToScan"]):
        #log( dirname)
        for fil in files:
            if(fil.endswith(".xml") and "biospecimen" in fil  ):
                uploadContext(dirname+"/"+fil)

#Check the Standard Folder where to Look for stuff (Like Clinical xml files)
def checkForClinical():
    #UploadClinical
    for dirname, dirs, files in os.walk(config["FolderToScan"]):
        #log( dirname)
        for fil in files:
            if(fil.endswith(".xml") and "clinical" in fil  ):
                log("Uploading Clinical "+ dirname+"/"+fil)
                uploadClinical(dirname+"/"+fil)
def checkForMutation():
    for dirname, dirs, files in os.walk(config["FolderToScan"]):
        #log( dirname)
        for fil in files:
            if(fil.endswith(".maf")):
                upload({"Annon":True,"Shared":True,"DataType":"Variation"},dirname+"/"+fil)


def refresh():
    global config
    global GlobalUploadedFiles
    global reconnectAfter
    if(len(GlobalUploadedFiles) % reconnectAfter ==0):
        print "reconnect"
        globalLogout(config["Host"] )
        globalLogin(Globalusername, Globalpassword, config["Host"] )


#Read config with configuration where to scan and where to upload
def readConfig(conffile):
    global config

    #Mock
    #TODO Read in
    log( "reading Config")
    #print config
    if(conffile ==""):
        log( "default Config")
        #This is the Default config
        config = {
            'Host': 'http://localhost:8080/csys-db-pub/',
            'UploadScriptLocation': './UploadScript.py',
            'FolderToScan' :'../testData/testdatafolder/',
            'FilenamePatternsToTypes':{
                'trimmed.annotated.gene.quantification.txt' : 'TranscriptAbundance' ,
                '.bed':'Peak',
                '.vcf':'Variation',
                '.maf':'Variation'
            }
        }
    else:
        with open(conffile) as data_file:
            config = json.load(data_file)


#    log( config)

    return config


#Checks the folders which contain meta.json files for upload information
def checkFoldersWithMeta():
    global config
    #Iterate Subfolder
    #scan for meta.json
    log( "Walking all Folders")

    #log( os.path.isdir(config["FolderToScan"]))
    directories = []
    #BaseFolder
    if("meta.json" in os.listdir(config["FolderToScan"])):
        directories.append(config["FolderToScan"])
    for dirname, dirs, files in os.walk(config["FolderToScan"]):
        #log( dirname)
        for fil in files:
            if fil == "meta.json":
                directories.append(dirname)
                break
    for direc in directories:
        parseDirectory(direc)
# Dump File For Upload
def dumptempfile(filename, content):

    with open(filename, 'w') as outfile:
        json.dump(content, outfile)
# Remove the Tempfile after Upload
def removeTempfile(filename):
    if(os.path.isfile(filename) ):
        os.remove(filename)
#This Function calls the upload script with the context
def uploadContext(filename):

    global GlobalUploadedFiles
    if(not os.path.abspath(filename) in GlobalUploadedFiles):
        standard = " -H " + config["Host"] + " -f " +filename+ " -o true -v "
        if(machineReadable):
            standard += " -m "

        execute = "python "+config["UploadScriptLocation"]
        processRun = execute + standard
        p = Popen(processRun, stdout=PIPE, shell=True)
        stdout, stderr = p.communicate()
        processoutput = stdout
        GlobalUploadedFiles.append( os.path.abspath(filename))
        printOutput(processoutput,filename)

def uploadClinical(filename):
    global GlobalUploadedFiles
    if(not os.path.abspath(filename) in GlobalUploadedFiles):
        standard = " -H " + config["Host"] + " -f " +filename+ " -k true -v "
        if(machineReadable):
            standard += " -m "

        execute = "python "+config["UploadScriptLocation"]
        processRun = execute + standard
        p = Popen(processRun, stdout=PIPE, shell=True)
        stdout, stderr = p.communicate()
        processoutput = stdout
        GlobalUploadedFiles.append( os.path.abspath(filename))
        printOutput(processoutput,filename)

#Upload File To Database
def upload(ContextInformation,Filename):

    global GlobalLinks
    global GlobalFailures
    #TODO REMOVE FOR PRODUCTION

    ################THIS IS THE INPUT DESCRIPTION OF THE UPLOAD SCRIPT (just a cheatsheet):
    """
    p = optparse.OptionParser(description='Uploads Data from a Computer directly to the cancersysDB')
    p.add_option('--password', '-p',help="This is the password if not Given as Parameter There will be an input prompt")
    p.add_option('--user', '-u',help="The Username, if not Given as Parameter There will be an input prompt")
    p.add_option('--file', '-f',help="The File to upload")
    p.add_option('--dataset', '-d',help="This is the Dataset ID the Data will be Attached to")
    p.add_option('--Host', '-H',help="The Adress of the CancersysDB")
    p.add_option('--type', '-t',help="This is the Type of Data, like described in the Database, The Input file has to follow its Format")
    p.add_option('--createDataset', '-c',help="If there is no Dataset in the System Please insert Description String within \" \" ")
    p.add_option('--onlyContext', '-o',help="Just Upload Files that describe Samples/Patients/Studies")
    p.add_option('--datasetFromDescription', '-l',help="Create Dataset form a File Describing the Context")
    p.add_option('--SampleSet', '-s',help="If there is no Dataset in the System this Parameter represents the Samples: By Comma Seperated, no whitespace like : DatasetIDs: 1,2,3 or Any ExternalIdentifier: A31,A42,A211")
    p.add_option('--Verbose', '-v',help="Generate Output",action="store_true", dest="verbose",default=False)
    p.add_option('--MachineReadable', '-m',help="Output as Machine-readable Json",action="store_true", dest="machineReadable",default=False)
    p.add_option('--clinicalData', '-k',help="Clinical Data",action="store_true", dest="clinicalData",default=False)
    p.add_option('--OnlyLogin', '-L',help="This Logges in and stores the Auth so no new login is Required. Only Needs User Password Host!",action="store_true", dest="OnlyLogin",default=False)
    p.add_option('--OnlyLogout', '-X',help="If The Login option was used this function logs out and deletes the Stored auth token.  Only Needs User Password Host!",action="store_true", dest="OnlyLogout",default=False)
    """
    #StandardInformation
    global GlobalUploadedFiles
    if(not Filename in GlobalUploadedFiles):
        standard = " -H " + config["Host"]
        #Random String part in Tempfile to avoid tempfile colisions in Uplading one Folder multiple times
        individualChars =  ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(8))
        tempfilename="temporaryfile"+individualChars+".json"
        dumptempfile(tempfilename,ContextInformation)
        basicupload =" -t " +ContextInformation["DataType"]
        fileappend = " -f "+Filename+" -v "
        if(machineReadable):
            standard += " -m "
        if(Filename.endswith("maf")):
            head, tail = os.path.split(Filename)
            context =  " -c  \"created from File "+tail+"\""
        else:
            context = " -l "+tempfilename
        #Dump Contextinformation to File, for upload...
        #execute
        processRun= "python "+config["UploadScriptLocation"] + standard +basicupload+context +fileappend
        #log( processRun)
        p = Popen(processRun, stdout=PIPE, shell=True)
        stdout, stderr = p.communicate()
        processoutput = stdout

        GlobalUploadedFiles.append(Filename)

        #subprocess.call(processRun,stdout=processoutput, shell=True)
        removeTempfile(tempfilename)
        printOutput(processoutput,Filename)


#Print output globaly
def printOutput(processoutput,Filename):
    global GlobalLinks 
    global GlobalFailures
    global GlobalMessages
    global AllReports

    pattern = "EndresultData:\n"
    poss = processoutput.find(pattern)
    ######Extract Results of Output
    if(poss < 0):
        pattern = "{"
        poss = processoutput.find(pattern)
        if(poss < 0):
            log( "couldnt find Output for upload "+Filename)
            log( processoutput)
            return
            #log( processoutput[poss+len(pattern):len(processoutput)])
        try:
            Jayson = json.loads(processoutput[poss:len(processoutput)])
        except (ValueError, TypeError, NameError):
            Jayson = {"FaultyStringOutput":processoutput,"stat":"failed" }
    else:
        #log( processoutput[poss+len(pattern):len(processoutput)])
        try:
            Jayson = json.loads(processoutput[poss+len(pattern):len(processoutput)])
        except (ValueError, TypeError, NameError):
            Jayson = {"FaultyStringOutput":processoutput,"stat":"failed" }

    
    if(Jayson["stat"]=="successful" ):
        if(Jayson.has_key("link")):
            GlobalLinks.append(Jayson["link"])
        if(Jayson.has_key("messages")):
            GlobalMessages.append(Jayson["messages"])
    else:
        output =Filename 
        if(Jayson.has_key("messages")):
            output+=Jayson["messages"]
        GlobalFailures.append(output)
    AllReports[Filename] = Jayson





def parseDirectory(directory):
    global ignoreContext
    log( "Uploading Directory : "+directory)
    try:
        metainfo = parseMetajson(directory)
        #Create Standard context object
        StandardMeta = metainfo["General"]

        #Exceptions
        if (metainfo.has_key("Special")):
            filenameExceptions = metainfo["Special"].keys()
        else:
            filenameExceptions = []

        #Iterate all Files in Folder not as Keys in Special
        if (StandardMeta.has_key("ContextsPath") and not ignoreContext):

            for dirname, dirs, files in os.walk(directory+"/"+str(StandardMeta["ContextsPath"])):
                log( "Uploading Context information "+ str(len(files)))
                for f in files:
                    if( "clinical" in f):
                        uploadClinical( directory+"/"+StandardMeta["ContextsPath"]+"/" + f)
                    elif( "biospecimen" in f):
                        uploadContext( directory+"/"+StandardMeta["ContextsPath"]+"/" + f)

        for dirname, dirs, files in os.walk(directory):
            log( "Uploading Files "+ str(len(files)-1))
            for f in files:
                if(not f.endswith("meta.json")):
                    if(f in filenameExceptions):
                        specialinfo = metainfo["Special"][f]
                        info =mergeSpecialandStandard(parseMetajson(directory)["General"],specialinfo)
                        upload(info,dirname+"/"+f)
                    else:
                        upload(StandardMeta,dirname+"/"+f)
    except Exception as e:
        log("Stuff went wrong in Folder "+ directory +", exception "+str(e))


    #process Special Values
    #iterate all Special keys
    #copy standard config and Add Special Config for File
def mergeSpecialandStandard(Standardinfo,Specialinfo):
    for key in Standardinfo:
        if(Standardinfo[key] == "true"):
            Standardinfo[key] = True
        if(Standardinfo[key] == "false"):
            Standardinfo[key] = False
        if  (key == "Contexts" and isinstance(Standardinfo["Contexts"], dict)):
            #print "There is aDict"
            Standardinfo["Contexts"] = [Standardinfo["Contexts"]]
        if  (key == "Contexts" and not isinstance(Standardinfo["Contexts"], list)):
            Standardinfo["Contexts"] = []

    out = Standardinfo
##      comment shows new Informations
##    print "--------new-------"
##    print "--------Standard-------"
##    print out
    if "NoGeneralContexts" in Specialinfo:
        Specialinfo["Contexts"] = []
    if  isinstance(Specialinfo["Contexts"], dict):
        #print "There is aDict"
        Specialinfo["Contexts"] = [Specialinfo["Contexts"]]
    if  not isinstance(Specialinfo["Contexts"], list):
        #print "There NO List"
        Specialinfo["Contexts"] = []

    for key in Specialinfo:
        if(key == "Contexts"):
            out[key].extend(Specialinfo[key])
        elif(key == "NoGeneralContexts"):
            None
        else:
            out[key] =Specialinfo[key]
        if(Specialinfo[key] == "true"):
            Specialinfo[key] = True
        if(Specialinfo[key] == "false"):
            Specialinfo[key] = False
##    print "-------Final------"
##    print out
    return out


#Parse the meta.json file
def parseMetajson(directory):
    #Read
    with open(directory+"/meta.json") as data_file:    
        data = json.load(data_file)
    return data
#Create the final report
def finalReport():
    global GlobalLinks 
    global GlobalFailures
    global GlobalMessages
    global machineReadable
    global AllReports
    if(machineReadable):
        out = {"Reports":AllReports,"failed":GlobalFailures,"log":GlobalMessages,"ResultLinks":GlobalLinks}
        print json.dumps(out,indent=4, sort_keys=True)
    else:
        print "failed: "
        for fail in GlobalFailures:
            print fail
    
        print "infos: "
        for mess in GlobalMessages:
            print mess
        
        print "ResultLinks: "
    
        for link in GlobalLinks:
            print link



def main():
    global Globalusername
    global Globalpassword
    global machineReadable
    global ignoreContext
    ##########DEFINE Parameters for script #######

    p = optparse.OptionParser(description='Uploads Data from a Computer directly to the cancersysDB')
    p.add_option('--password', '-p',help="This is the password if not Given as Parameter There will be an input prompt")
    p.add_option('--user', '-u',help="The Username, if not Given as Parameter There will be an input prompt")
    p.add_option('--generalconfig', '-c',help="A json file containing the general config OPTIONAL")
    p.add_option('--Host', '-H',help="The Adress of the CancersysDB (Overwrites configfile Option)")
    p.add_option('--FolderToScan', '-f',help="The Folder to scan and systematicaly upload")
    p.add_option('--UploadScriptLocation', '-s',help="The location of the Uploadscript (Usually in the same folder as this script)")
    p.add_option('--MachineReadable', '-m',help="Output as machine readable Json",action="store_true", dest="machineReadable",default=False)
    p.add_option('--IgnoreContext', '-i',help="IgnoreContextFiles, this option if all Context(Patients,Studies, Samples) are Allready in the Database",action="store_true", dest="ignoreContext",default=False)

    ##########Config START #######
    options, arguments = p.parse_args()
    machineReadable = options.machineReadable
    if(not options.user):
        Globalusername = raw_input("Enter Username:")
    else:
        Globalusername = options.user
    if(not options.password):
        Globalpassword = getpass.getpass('Enter Password:')
    else:
        Globalpassword = options.password

    if( options.generalconfig):
        configObject = readConfig(options.generalconfig)
    else:
        configObject = readConfig("")

    if(options.Host):
        configObject["Host"] = options.Host
    
    if(options.FolderToScan):
        configObject["FolderToScan"] = options.FolderToScan

    if(options.UploadScriptLocation):
        configObject["UploadScriptLocation"] = options.UploadScriptLocation

    ignoreContext = options.ignoreContext
    ##########Config END #######


    #########Globallogin

    globalLogin(Globalusername, Globalpassword, options.Host)

    #First Step get XML files with Description of Patients and their Samples etc.



    if(options.ignoreContext):
        log("Ignoring Context Files")
    else:
        log("Uploading Contexts")
        checkStandardContext()
    #Check the Folders with files to Upload , they are containing the meta.json files
    globalLogout(options.Host)
    globalLogin(Globalusername, Globalpassword, options.Host)
    log("Uploading Folders")
    checkFoldersWithMeta()

    globalLogout(options.Host)
    globalLogin(Globalusername, Globalpassword, options.Host)

    log("Uploading Clinical")
    checkForClinical()
    globalLogout(options.Host)
    globalLogin(Globalusername, Globalpassword, options.Host)
    #Searches the Upload folder for MAF mutation Files to Upload.
    log("Uploading Mutation Data")
    checkForMutation()
    globalLogout(options.Host)
    log("Writing Report ")
    finalReport()

main()