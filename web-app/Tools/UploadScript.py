import pycurl , json ,sys, os, hashlib
from io import BytesIO
import getpass, optparse

messages = []
machineReadable = False
def log(toLog):
    if(machineReadable):
        messages.append(toLog)
    else:
        print toLog

#Login Token Negotiation
def login(username,password,host):
    out = BytesIO()
    c = pycurl.Curl()
    url=host+'api/login'
    if(Verbose):
        log( "Querying URL:\\n"+ url)
    c.setopt(c.URL, host+'api/login')

    c.setopt(pycurl.HTTPHEADER, ['Accept: application/json',"Content-Type: application/json"])

    data = json.dumps({"username": username, "password": password })
    #debug
    #if(Verbose):
    #    log(data)
    c.setopt(pycurl.POST, 1)
    c.setopt(pycurl.POSTFIELDS, data)
    c.setopt(c.WRITEFUNCTION, out.write)
    #Debug
    #if(Verbose):
    #    c.setopt(pycurl.VERBOSE, 1)


    c.perform()
    # HTTP response code, e.g. 200.
    status= c.getinfo(pycurl.HTTP_CODE);

    if(Verbose):
        log("Status" +str(status))

    if(status == 401):
        log( "Access Denied")
        return False


    if(status != 200):
        log( "Error occurred Status:" + str(status))
        return False

    if(Verbose and status == 200):
        log( "Logged in")

    c.close()
    dictionary = json.loads(out.getvalue())
    Auth_token = dictionary["access_token"]
    return Auth_token
#Upload File
def uploadFile(Auth_token, Host, typ, ds,ufile ):
    out = BytesIO()
    c = pycurl.Curl()

    c.setopt(pycurl.HTTPHEADER, ["X-Auth-Token: "+Auth_token,'Accept: application/json'])

    c.setopt(pycurl.POST, 1)
    c.setopt(c.HTTPPOST, [("file", (c.FORM_FILE, ufile ))])

    url = Host+'upload/uploadToDataset/'+str(typ)+'/'+str(ds)
    if(Verbose):
        log( "Querying URL:\\n"+url)

    c.setopt(c.URL, url)
    #Debug
    #if(Verbose):
    #    c.setopt(pycurl.VERBOSE, 1)

    c.setopt(c.WRITEFUNCTION, out.write)
    c.perform()
    status= c.getinfo(pycurl.HTTP_CODE)
    if(status != 200):
        log( "Error occurred status:" + str(status)+"\\n"+ out.getvalue())


    if(Verbose and status == 200):
        log( "Uploaded File")
    c.close()
    #print out.getvalue()
    temp = out.getvalue()

    #dictionary = json.loads(temp)

    #print dictionary
    return temp

#Logout
def logout(Auth_token, Host):
    out = BytesIO()
    c = pycurl.Curl()

    c.setopt(pycurl.HTTPHEADER, ["X-Auth-Token: "+Auth_token,'Accept: application/json'])
    url =Host+'api/logout'
    if(Verbose):
        log(  "Querying URL:\\n"+url)

    c.setopt(c.URL, Host+'api/logout')
    #if(Verbose):
    #    c.setopt(pycurl.VERBOSE, 1)
    c.perform()
    c.close()

#Create a Dataset as Context

def createDataset(Auth_token, Host, description,samples=None, file=None ):
    out = BytesIO()
    c = pycurl.Curl()

    c.setopt(pycurl.HTTPHEADER, ["X-Auth-Token: "+Auth_token,'Accept: application/json'])

    c.setopt(pycurl.POST, 1)

    if( samples!= None ):
        c.setopt(c.HTTPPOST, [("description", description),("samples", samples)])
    elif(file != None  and description != None):
        c.setopt(c.HTTPPOST, [("file", (c.FORM_FILE, file )),("description", description)])
    elif(file != None  and description == None):
        c.setopt(c.HTTPPOST, [("file", (c.FORM_FILE, file ))])
    else:
        c.setopt(c.HTTPPOST, [("description", description)])
    url =Host+'upload/CreateDataset/'
    if(Verbose):
        log(  "Querying URL:\\n"+url)

    c.setopt(c.URL, Host+'upload/CreateDataset/')
    c.setopt(c.WRITEFUNCTION, out.write)
    #Debug
    #if(Verbose):
    #    c.setopt(pycurl.VERBOSE, 1)

    c.perform()
    status= c.getinfo(pycurl.HTTP_CODE)
    if(status != 200):
        log( "Error occurred status:" + str(status)+"\\n"+  out.getvalue())
        return False

    if(Verbose and status == 200):
        log( "Created Dataset")
    c.close()
    #print out.getvalue()
    dictionary = json.loads(out.getvalue())
    #print dictionary
    Dataset = dictionary["DatasetId"]
    return Dataset


#Upload Context File which describes Patients etc.

def UploadContext(Auth_token, Host, file ):
    out = BytesIO()
    c = pycurl.Curl()

    c.setopt(pycurl.HTTPHEADER, ["X-Auth-Token: "+Auth_token,'Accept: application/json'])

    c.setopt(pycurl.POST, 1)

    c.setopt(c.HTTPPOST, [("file", (c.FORM_FILE, file ))])
    url =Host+'upload/CreateContext/'
    if(Verbose):
        log( "Querying URL:\\n"+ url)

    c.setopt(c.URL, url)
    c.setopt(c.WRITEFUNCTION, out.write)
    #Debug
    #if(Verbose):
    #    c.setopt(pycurl.VERBOSE, 1)

    c.perform()
    status= c.getinfo(pycurl.HTTP_CODE)
    if(status != 200):
        log( "Error occurred status:" + str(status)+"\\n"+ out.getvalue())

    if(Verbose and status == 200):
        log( "Created Context")
    c.close()
    #print out.getvalue()
    temp = out.getvalue()
    #print dictionary
    return temp

#Upload Context File which describes Patients etc.

def UploadClinical(Auth_token, Host, file ):
    out = BytesIO()
    c = pycurl.Curl()

    c.setopt(pycurl.HTTPHEADER, ["X-Auth-Token: "+Auth_token,'Accept: application/json'])

    c.setopt(pycurl.POST, 1)

    c.setopt(c.HTTPPOST, [("file", (c.FORM_FILE, file ))])
    url =Host+'upload/clinicalData/'
    if(Verbose):
        log( "Querying URL:\\n"+ url)

    c.setopt(c.URL, url)
    c.setopt(c.WRITEFUNCTION, out.write)
    #Debug
    #if(Verbose):
    #    c.setopt(pycurl.VERBOSE, 1)

    c.perform()
    status= c.getinfo(pycurl.HTTP_CODE)
    if(status != 200):
        log( "Error occurred status:" + str(status)+"\\n"+ out.getvalue())

    if(Verbose and status == 200):
        log( "Created Clinical Data")
    c.close()
    #print out.getvalue()
    temp = out.getvalue()
    #print dictionary
    return temp

def finish(result,dataset):


    if( not machineReadable):
        print "EndresultData:\\n"
        print result
    else:
        thing =dict()
        try:
            returnVal = json.loads(result)
            thing = {"result":returnVal,"log": messages,"stat":returnVal["stat"]}
        except (ValueError, TypeError, NameError):
            thing = {"result":result,"stat":"??"}

        if(dataset):
            thing["dataset"] = dataset
        print json.dumps(thing)

def main():
    global messages
    global machineReadable

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

    options, arguments = p.parse_args()
    machineReadable = options.machineReadable
    #ONLY Login
    Auth_token =False
    if(not options.Host ):
        log( "Specify Host! Host Parameter not Given!")
        return
    global Verbose
    Verbose = options.verbose
    potentialAuthFile = "."+hashlib.sha224(options.Host).hexdigest()+".login"
    hasStoredLogin =False
    if(os.path.isfile(potentialAuthFile)):
        with open(potentialAuthFile, 'r') as infile:
            Auth_token = infile.read()
        hasStoredLogin = True

    if(options.OnlyLogin ):
        if(not options.user):
            username = raw_input("Enter Username:")
        else:
            username = options.user
        if(not options.password):
            password = getpass.getpass('Enter Password:')
        else:
            password = options.password
        Auth_token = login(username,password, options.Host)

        if Auth_token == False:
            finish('{"stat":405}' )
        else:
            with open(potentialAuthFile, 'w') as outfile:
                outfile.write(Auth_token)
        log("Logged In and Stores authentification "+ options.Host )
        return
    #ONLY LOGOUT

    if(options.OnlyLogout ):
        if(not hasStoredLogin):
            log("There is Not Token to log out")
            return

        logout(Auth_token,options.Host)
        os.remove(potentialAuthFile)
        log("logged out from "+ options.Host)
        return

    if(not options.file):
        log( "Specify File to Upload !")
        return
    if(not os.path.isfile(options.file)):
        log( "File does not Exist!")
        return
    if(not options.dataset and not options.createDataset and not options.datasetFromDescription and not options.onlyContext and not options.clinicalData):
        log( "Use Dataset OR CreateDataset OR datasetFromDescription or clinicalData parameter to make upload.")
        return
    if( options.dataset and options.createDataset):
        log( "Choose Dataset OR CreateDataset parameter, both are too much.")
        return
    if( options.SampleSet and options.dataset):
        log( "Sample Identifiers are irrelevant when referencing an existing Dataset, will be Ignored.")

    if(not options.type and not options.onlyContext and not options.clinicalData):
        print options.type
        log( "Specify Type this file represents ! Type Parameter not Given!")
        return
    if( options.onlyContext and options.createDataset ):
        log( "Conflicting Parameters onlyContext and datasetFromDescription!")
        return
    if( options.onlyContext and options.createDataset  ):
        log( "Conflicting Parameters onlyContext and createDataset!")
        return
    if(options.clinicalData and options.onlyContext ):
        log( "Conflicting Parameters clinicalData and onlyContext!")
        return
    if(not options.user and not Auth_token):
        username = raw_input("Enter Username:")
    elif(not Auth_token):
        username = options.user
    if(not options.password and not Auth_token):
        password = getpass.getpass('Enter Password:')
    elif(not Auth_token):
        password = options.password



    if(options.verbose):
        log("Verbose Output set!")
    dataset = None
    if(options.dataset):
        dataset = {"DatasetId":options.dataset}
    if(not Auth_token):
        Auth_token = login(username,password, options.Host)


    if Auth_token == False:
        finish('{"stat":405}',dataset )


    if(options.onlyContext):
        result = UploadContext(Auth_token, options.Host, options.file)
    elif(options.clinicalData):
        result = UploadClinical(Auth_token, options.Host, options.file)
    else:
        if(options.createDataset or options.datasetFromDescription):
            dataset = createDataset(Auth_token, options.Host,options.createDataset,options.SampleSet,options.datasetFromDescription)
            if(not dataset == False):
                result = uploadFile(Auth_token, options.Host,options.type,dataset, options.file)
            else:
                result='{"stat":400}'
    finish(result,dataset)
    logout(Auth_token, options.Host)


    '''
    print "finished " + result["stat"]

    if(result.has_key("link")):
        print "Result: " + result["link"]

    if(result["stat"]=="successful"):
        return 0
    '''

main()
