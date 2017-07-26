// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.assets.includes = ["**/*.csv"]
grails.config.locations = [
        "file:/var/security/auth/${appName}.groovy",
        "file:${userHome}/.grails/${appName}.groovy",
        "file:/etc/opt/grails/${appName}.groovy",
        "file:/etc/opt/grails/csys-public.groovy",
        "file:/etc/opt/grails/cancersysDB.groovy",
        "file:C:/etc/csys-db-pub.groovy","file:/etc/opt/grails/csys-db-pub.groovy"]
grails.mime.use.accept.header = true
// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true

    }
    test{
        grails.logging.jul.usebridge = true

    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j.main = {
/*
    appenders {
        file name:'file', file:'/var/logs/cancersys.log'
    }
    root {
        info 'stdout'
    }
*/
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}


    error 'org.codehaus.groovy.grails.web.servlet',        // controllers
            'org.codehaus.groovy.grails.web.pages',          // GSP
            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
            'org.codehaus.groovy.grails.commons',            // core / classloading
            'org.codehaus.groovy.grails.plugins',            // plugins
            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate',
            'de.cancersysdb'
    environments {
        development {
            all  'de.cancersysdb',
                    'grails.app.services',
                    'grails.app.domain',
                    'grails.app.controllers'
/*SQL Debug
            debug 'org.hibernate.SQL'
            trace 'org.hibernate.type'*/

            warn 'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
                    'org.codehaus.groovy.grails.web.servlet',    // controllers
                    'org.codehaus.groovy.grails.web.sitemesh',       // layouts
                    'org.codehaus.groovy.grails.web.pages'          // GSP

        }
        production{
            info    'de.cancersysdb',
                    'grails.app.services',
                    'grails.app.domain',
                    'grails.app.controllers',
                    'grails.app.BootStrap',
                    'de.cancersysdb.ImportTools',
                    'de.cancersysdb.import',
                    'de.cancersysdb.importer'

        }
    }

}


// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'de.cancersysdb.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'de.cancersysdb.UserRole'
grails.plugin.springsecurity.authority.className = 'de.cancersysdb.Role'
grails.plugin.springsecurity.rest.token.storage.useGrailsCache = true
grails.plugin.springsecurity.rest.token.storage.grailsCacheName	= 'tokenCache'
grails.plugin.springsecurity.rest.token.validation.useBearerToken = false
grails.plugin.springsecurity.rest.token.validation.headerName = 'X-Auth-Token'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [

	'/assets/**':                     ['permitAll'],
	'/**/js/**':                      ['permitAll'],
	'/**/css/**':                     ['permitAll'],
	'/**/images/**':                  ['permitAll'],
	'/**/favicon.ico':                ['permitAll'],
    '/user/**':                       ['permitAll'],
    //'/**':                            ['permitAll'],

    '/':                              ['permitAll'],

    '/upload/**':                      ['ROLE_USER'],
    '/**':                            ['IS_AUTHENTICATED_ANONYMOUSLY'],

]

grails.plugin.springsecurity.logout.postOnly = false

grails.plugin.springsecurity.filterChain.chainMap = [

        '/api/**':  'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter',  // Stateless chain
        '/upload/**':  'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter',  // Stateless chain
        '/**':      'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'                                          // Traditional chain
]
grails.plugin.springsecurity.failureHandler.exceptionMappings = [
        'org.springframework.security.authentication.LockedException':
                '/user/accountLocked',
        'org.springframework.security.authentication.DisabledException':
                '/user/accountDisabled',
        'org.springframework.security.authentication.AccountExpiredException':
                '/user/accountExpired',
        'org.springframework.security.authentication.CredentialsExpiredException':
                '/user/passwordExpired'
]

grails.plugin.springsecurity.apf.storeLastUsername = true
//////////////////////////////Cancersys Specific

environments {
    development {
        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"

    }
    test{
        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"
    }
    production {
        cancersys.config.BasePath = "/srv/cancersys/"
        cancersys.config.tempFilepath = "/srv/cancersys/ImportedFiles/"
        cancersys.config.dataFilepath = "/srv/cancersys/Data/"
    }
}



test{
    slowFunctional{
        pausa =1
        pausb =1
        pausc =1
    }
}

//By default start in private mode. For Public mode change in external config file

cancersys.config.systemType = "private"
