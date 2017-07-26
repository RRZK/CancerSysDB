import grails.plugin.springsecurity.SpringSecurityUtils
def loggedIn = { ->
    SpringSecurityUtils.ifAnyGranted('ROLE_USER,ROLE_ADMIN,ROLE_MANGER')

}
def loggedOut = { ->
    !(SpringSecurityUtils.ifAnyGranted('ROLE_USER,ROLE_ADMIN,ROLE_MANGER'))
}
def isAdmin = { ->
    SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
}

def isPortal={
    print grailsApplication
    if(grailsApplication.hasProperty("config"))
        return grailsApplication.config.cancersys.config.systemType.equals("public")
    else
        return false

}

def PortalOrLogin= {
    if(SpringSecurityUtils.ifAnyGranted('ROLE_USER,ROLE_ADMIN,ROLE_MANGER'))
        true
    else{
            if(grailsApplication.hasProperty("config"))
                return grailsApplication.config.cancersys.config.systemType.equals("public")
            else
                return false
    }
}
def PortalAndAdmin= {

    SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') && grailsApplication.hasProperty("config")&& grailsApplication.config.cancersys.config.systemType.equals("public")
}

navigation = {
    user {
/*        "Overview"( uri: "/") */
	"Welcome"(controller: 'showcase', action: 'index', visible:PortalOrLogin) {
/*
            "Browse"(controller: 'showcase', action: 'index')
*/
/*
            "My Workflows"(controller: 'staticContent', action: 'workflowOwn', visible: loggedIn)
*/
        }

        "Data Available"(controller: "study", action: "index", visible: loggedIn){
            "Cohorts"(controller:"study", action: "index, indexOwn, create", visible: loggedIn)
            "Patients"(controller:"patient", action: "index, indexOwn, create", visible: loggedIn)
            "Samples"(controller:"sample",  action: "index, indexOwn, create", visible: loggedIn)
            "Datasets"(controller: 'dataset', action: "index, indexOwn, create", visible: loggedIn)
            "Data Model"( controller: 'staticContent',action: "dataOverview", visible: loggedIn)

        }

        "Data Upload"(controller:'manageData',action:'dataImport', visible: loggedIn  ){
            "Data Files"(controller: 'manageData', action: 'dataImport', visible: loggedIn  )
            "Online Resources"(controller: 'manageData', action: 'dataImportFromExternalSource', visible: loggedIn)
            "UploadTools"(controller: 'Admin', action: 'tools', visible: loggedIn)

            "Import from Biomart"(controller: 'manageData', action: 'GeneImport', visible: isAdmin )
        }

        "About"(controller: 'staticContent', action: 'about', visible:PortalOrLogin){
           "About"(controller: 'staticContent', action: 'about',visible:PortalOrLogin)
           "Contact"(controller: 'staticContent', action: 'contact',visible:PortalOrLogin)

        }
        "Users"(controller:'user',action:'search', visible: isAdmin  ){
            "List Users"(controller: 'user', action: 'search', visible: isAdmin )
            "Add User"(controller: 'user', action: 'create', visible: isAdmin )

        }

        "Queries"(controller:'admin',action:'importWorkflow', visible: isAdmin){
            "Import Query"(controller:'admin', action: 'importWorkflow', visible: isAdmin)
            "HQL Code"(controller:'admin', action: 'testHQL', visible: isAdmin)
            "Clinical Data"(controller: 'admin', action: 'clinicalDataStats', visible: isAdmin)
        }
        "News"(controller:'newsStory',action:'index', visible: isPortal){
            "Create News"(controller:'newsStory', action: 'create', visible: PortalAndAdmin)
        }
        "Logout"(controller:'logout',action:'index', visible: loggedIn )
        "Login"( controller:'login',action:'index', visible: loggedOut )
    }

}
