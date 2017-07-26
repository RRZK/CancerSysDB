import sys, json , os,  shutil
from subprocess import Popen, PIPE, STDOUT
import time
import getpass, optparse
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from decimal import *

#########Globals

lastModified = time.time()
UploadScriptLocation =None
DirectoryScriptLocation = None
Host= None
User = None
Password = None
NewPath = None
TempPath = None
OldPath = None
NoModificationsforSeconds = 30.0

##########Move Data to Temporary Folder
def copyData():
    #Move all Data to Tempdata
    print "copy Data"

    global NewPath
    global TempPath
    for filename in os.listdir(NewPath):
        shutil.move(NewPath+"/"+filename,TempPath)

##########Upload Temporary Folder
def uploadData():
    global UploadScriptLocation
    global DirectoryScriptLocation
    global User
    global Password
    global Host
    global NewPath
    global TempPath
    print "Upload Data from folder " +TempPath
    toExecute = "python "+str(DirectoryScriptLocation)+" -m -u "+str(User)+" -p "+str(Password)+" -H "+str(Host)+" -f "+str(TempPath)+" -s "+str(UploadScriptLocation)
    p = Popen(toExecute, stdout=PIPE, shell=True)
    stdout, stderr = p.communicate()
    processoutput = stdout
    return processoutput
    #Upload Stuff

##########Stash Uploaded Data
def stashUploaded(processoutput):

    global TempPath
    global OldPath
    subfolder = str(time.time())

    finalDestination = OldPath+"/"+subfolder
    print "storing uploaded Data "+ finalDestination
    os.makedirs(finalDestination)
    for filename in os.listdir(TempPath):
        shutil.move(TempPath+"/"+filename,finalDestination)
    #Write Report Json File
    if(processoutput and not os.path.isfile(finalDestination+"/report.json")):
        f = open(finalDestination+"/report.json", 'w')
        f.write( processoutput)
        f.close()

############This is the Event Handler
#http://brunorocha.org/python/watching-a-directory-for-file-changes-with-python.html
class CsysAutoUploadHandler(FileSystemEventHandler):

    def process(self, event):
        global lastModified
        """
        event.event_type
            'modified' | 'created' | 'moved' | 'deleted'
        event.is_directory
            True | False
        event.src_path
            path/to/observed/file
        """
        print event.src_path, event.event_type  # print now only for degug

        lastModified =time.time()
    def on_modified(self, event):
        self.process(event)
    def on_created(self, event):
        self.process(event)


def main():
    global lastModified
    global UploadScriptLocation
    global DirectoryScriptLocation
    global NewPath
    global TempPath
    global OldPath
    global User
    global Password
    global NoModificationsforSeconds
    global Host

    ##########DEFINE Parameters for script #######

    p = optparse.OptionParser(description='This is a Watchdog script to monitor File System structures and Upload data to the CancersysDB')
    p.add_option('--password', '-p',help="The password of the User which uploads Stuff")
    p.add_option('--user', '-u',help="The username of the User which uploads Stuff")
    p.add_option('--Host', '-H',help="The Adress of the CancersysDB (Overwrites configfile Option)")

    p.add_option('--FolderToWatch', '-f',help="The Folder to use for continous systematic uploads, auto creates  sub folders 'new', 'temp' and 'old'. If they do not exist.")

    p.add_option('--WatchFolder', '-w',help="The Folder with new data. Without FolderToWatch use t and d Parameter.")
    p.add_option('--TempFolder', '-t',help="The Folder to temporary move the upload Data to. Without FolderToWatch use w and d Parameter.")
    p.add_option('--DoneFolder', '-o',help="The Folder to put The Done Uploaded. Without FolderToWatch use w and t Parameter.")

    p.add_option('--UploadScriptLocation', '-s',help="The location of the Uploadscript, if not given Assumes UploadScript.py in the working directory.")
    p.add_option('--DirectoryScriptLocation', '-d',help="The location of the Directory Uploadscript, if not given Assumes DirectoryUpload.py in the working directory.")
    p.add_option('--InactiveTillTransfer', '-i',help="The Seconds the new Folder should have been unmodified til everything is prepared and uploaded Standard 30 seconds")
    options, arguments = p.parse_args()

    ###########Check Parameters

    if(not options.FolderToWatch and not(options.WatchFolder and options.TempFolder and options.DoneFolder)):
        print "Failed Parameter FolderToWatch not set or WatchFolder and TempFolder and DoneFolder not set"
        return
    if(not options.password ):
        print "Failed Parameter password not set"
        return
    Password = options.password
    if(not options.user ):
        print "Failed Parameter user not set"
        return
    User = options.user
    if(not options.Host ):
        print "Failed Parameter Host not set"
        return

    Host = options.Host

    if(not options.UploadScriptLocation):
        if(not os.path.isfile("./UploadScript.py")):
            print "Upload script could not be found in ./UploadScript.py please specify by parameter UploadScriptLocation"
            return
        else:
            UploadScriptLocation = "./UploadScript.py"
    else:
        if(not os.path.isfile(options.UploadScriptLocation)):
            print "Upload script could not be found in " +options.UploadScriptLocation
            return
        else:
            UploadScriptLocation =  options.DirectoryScriptLocation

    if(not options.DirectoryScriptLocation):
        if(not os.path.isfile("./DirectoryUpload.py")):
            print "Upload script could not be found in ./DirectoryUpload.py, please specify by parameter DirectoryScriptLocation"
            return
        else:
            DirectoryScriptLocation = "./DirectoryUpload.py"
    else:
        os.path.isfile(options.DirectoryScriptLocation)
        if(not os.path.isfile(options.DirectoryScriptLocation)):
            print "Upload script could not be found in " +options.DirectoryScriptLocation
            return
        else:
            DirectoryScriptLocation =  options.DirectoryScriptLocation

    if(options.InactiveTillTransfer):
        temp =  float(options.InactiveTillTransfer)
        if(type(temp) is float):
            NoModificationsforSeconds = temp
        else:
            print "could not Understand InactiveTillTransfer Using Standard Value: "+ str(NoModificationsforSeconds)

    ###############Create and Check Directories

    if(options.FolderToWatch):
        NewPath = options.FolderToWatch +"/new"
        TempPath =  options.FolderToWatch +"/temp"
        OldPath =  options.FolderToWatch +"/old"

    if(options.WatchFolder):
        NewPath = options.WatchFolder
        if(not os.path.isdir(NewPath)):
            print "Path "+ NewPath + "Does not Exist"
            return
    else:
        if(not os.path.isdir(NewPath)):
            os.makedirs(NewPath)
    if(options.TempFolder):
        TempPath =  options.TempFolder
        if(not os.path.isdir(TempPath)):
            print "Path "+ TempPath + "Does not Exist"
            return
    else:
        if(not os.path.isdir(TempPath)):
            os.makedirs(TempPath)


    if(options.DoneFolder):
        OldPath =  options.DoneFolder
        if(not os.path.isdir(OldPath)):
            print "Path "+ OldPath + "Does not Exist"
            return
    else:
        if(not os.path.isdir(OldPath)):
            os.makedirs(OldPath)

    ########EventHandler
    event_handler = CsysAutoUploadHandler()
    observer = Observer()
    observer.schedule(event_handler, NewPath, recursive=True)

    observer.start()
    #########Watchdog Loop
    try:
        while True:
            time.sleep(1)
            timediffrence = None
            ########Watch the Time Difference:
            if(not lastModified == None ):
                timediffrence =  time.time()-lastModified
                print "Counting "+str(timediffrence)
            if( not lastModified == None and timediffrence> NoModificationsforSeconds):
                print "Uploading"
                i=0
                for filename in os.listdir(NewPath):
                    i+=1
                if(i>0):
                    copyData()
                    #Copying Data is a Change event
                    lastModified=None
                    report = uploadData()
                    stashUploaded(report)
                lastModified=None
    except KeyboardInterrupt:
        observer.stop()
    observer.join()

main()

