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

import org.petermac.util.Locator
import org.springframework.security.access.method.P

//  Application locator
//
def loc  = Locator.instance



grails.project.groupId = appName   // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
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
    xml:           ['text/xml', 'application/xml'],
    excel:         'application/vnd.ms-excel',             // added for export - kdd
    pdf:           'application/pdf',                      // added for export - kdd
    rtf:           'application/rtf'                       // added for export - kdd
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
//
grails.resources.adhoc.patterns = ['/dist/images/*', '/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.adhoc.includes = ['/dist/images/**','/images/**', '/css/**', '/js/**', '/plugins/**']

//  kdd: Overrides default theme for the skin of the JqGrid plugin: lots more at http://jqueryui.com/themeroller/
//
grails.resources.modules =
    {
        overrides   {
            'jquery-theme'  {
                resource id:'theme', url: '/css/jquery-ui-1.11.0.custom/jquery-ui.theme.min.css'  // cupertino
            }
        }
    }

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
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

//  Default name and location of log file set by logDir pathos_properties
//  default = <pathos_home>/log
//
def curate_log  = loc.logDir + loc.fs + 'Curate.log'



//----END AD----//
environments
{
    pa_stage
    {

        grails.logging.jul.usebridge = true
        grails.mail.port = com.icegreen.greenmail.util.ServerSetupTest.SMTP.port
        //grails.serverURL = "http://localhost:8080/PathOS"
        //Enable for LDAP login:
        //grails.config.locations = [  "file:/pathology/NGS/PathOS/etc/pathos_ldap_conf.groovy"  ]

    }
    pa_local
    {

        grails.logging.jul.usebridge = true
        grails.mail.port = com.icegreen.greenmail.util.ServerSetupTest.SMTP.port
        //grails.serverURL = "http://localhost:8080/PathOS"
        //grails.mail.host = 'mail.petermac.org'
        //grails.mail.port = '25'

        grails.gsp.enable.reload = true
    }
    pa_prod
    {
        //no uname or pass: SMTP mail.petermac.org for future mail //
        grails.mail.host = 'mail.petermac.org'
        grails.mail.port = '25'
    }
    pa_uat
    {
        //Enable for LDAP login:
        //grails.config.locations = [  "file:/pathology/NGS/PathOS/etc/pathos_ldap_conf.groovy"  ]
        // no uname or pass: SMTP mail.petermac.org for future mail //
        grails.mail.host = 'mail.petermac.org'
        grails.mail.port = '25'
    }

}

if ( loc.useADAuthentication == true ) {
    // This loads a config file with LDAP properties and tells us to use LDAP for spring auth provider
    // note that loc.useADAuthentication is true by default in Locator.groovy if unset in the pa_xxx.properties file

    grails.config.locations = [  "file:${loc.ADConfigurationFile}" ]

} else {
    // This specifically tells us to NOT use LDAP and to use standard db auth instead
    //
    grails.plugin.springsecurity.providerNames = [ 'daoAuthenticationProvider' ]

}

// log4j configuration
//
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    // console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    appenders
    {
        appenders
        {
            rollingFile name:           'PathOSFile',
                        maxFileSize:    '10MB',
                        file:           curate_log,
                        layout:         pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
        }
        root
        {
            info 'stdout', 'PathOSFile'
        }
    }

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
//weakssl.trustedhosts = ['vm-115-146-91-157.melbourne.rc.nectar.org.au'] //our Atlassian stuff lives here, and the cert is self-signed. need this for Jira's REST API to work. //DEPRECATED we have a real cert

// Added by the Spring Security Core plugin
//
grails.plugin.springsecurity.useSecurityEventListener = true

grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.petermac.pathos.curate.AuthUser'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.petermac.pathos.curate.AuthUserAuthRole'
grails.plugin.springsecurity.authority.className = 'org.petermac.pathos.curate.AuthRole'

grails.plugin.springsecurity.rememberMe.tokenValiditySeconds = 1 // Make sure Remember Me does not work
grails.plugin.springsecurity.logout.postOnly = false  //disable postOnly logout
//grails.plugin.springsecurity.ui.password.validationRegex = '^.*(?=.*\\d)(?=.*[a-zA-Z]).*$' //dont force special char

//Spring Security UI emails:
//
grails.plugin.springsecurity.ui.forgotPassword.emailFrom = 'pathOS.do.not.reply@petermac.org'
grails.plugin.springsecurity.ui.forgotPassword.emailSubject = 'PathOS Password Reset'
grails.plugin.springsecurity.ui.forgotPassword.emailBody = """
Hi ${user.displayName},<br/>
<br/>
You (or someone pretending to be you) requested that your password be reset.<br/>
<br/>
If you didn't make this request then ignore the email; no changes have been made.<br/>
<br/>
If you did make the request, then click <a href="$url">here</a> to reset your password.
"""

//  Set static permission rules
//
grails.plugin.springsecurity.controllerAnnotations.staticRules =
        [
            //  Infrastructure web files
            //
            '/index':                       ['permitAll'],
            '/index.gsp':                   ['permitAll'],
            '/js/**':                       ['permitAll'],
            '/css/**':                      ['permitAll'],
            '/fonts/**':                    ['permitAll'],
            '/**/js/**':                    ['permitAll'],
            '/**/css/**':                   ['permitAll'],
            '/**/fonts/**':                 ['permitAll'],
            '/**/images/**':                ['permitAll'],
            '/**/favicon.ico':              ['permitAll'],
            '/dist/**':                     ['permitAll'],
            '/hotfix.js':                   ['permitAll'],
            '/hotfix.css':                  ['permitAll'],
             '/igv/**':                      ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'], //['permitAll'],
            '/circos/**':                   ['permitAll'],
            '/igvSession/**':                   ['permitAll'],  // dynamic igv xml session generator



            //  Security and Administration
            //
            '/greenmail/**':                ['ROLE_ADMIN','ROLE_DEV'],
            '/user/show/**':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/user/**':                     ['ROLE_ADMIN','ROLE_DEV'],
            '/role/create':                 ['denyAll'],
            '/role/delete/**':              ['denyAll'],
            '/role/**':                     ['ROLE_ADMIN','ROLE_DEV'],
            '/securityInfo/**':             ['ROLE_ADMIN','ROLE_DEV'],
            '/registrationCode/**':         ['ROLE_ADMIN','ROLE_DEV'],
            '/register':                    ['denyAll'],
            '/register/register':           ['denyAll'],
            '/register/**':                 ['denyAll'],
            '/admin/**':                    ['ROLE_ADMIN','ROLE_DEV'],
            '/*/filter':                    ['permitAll'],

            // Utility pages
            //
            '/payload/**':                  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/filtertemplate/**':           ['ROLE_ADMIN','ROLE_DEV'],
            '/preferences/**':              ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/vcfUpload/*':                 ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/search/**':                   ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/search/reindex':              ['permitAll'],

            '/tag/create/**':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
            '/tag/save/**':                 ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
            '/tag/edit/**':                 ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
            '/tag/update/**':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
            '/tag/delete/**':               ['ROLE_ADMIN','ROLE_DEV'],
            '/tag/list/**':                 ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/tag/variantRows/**':          ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB',],
            '/tag/**':                      ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_LAB'],

            //  Domain tables
            //
            '/patsample/show/**':           ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/patsample/list':              ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/patsample/updatecomment':     ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],

            '/patient/find':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/patient/show':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/patient/list':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],

            '/audit/show':                  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/audit/list':                  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],

            '/drug/create':                 ['ROLE_ADMIN','ROLE_DEV'],
            '/drug/save':                   ['ROLE_ADMIN','ROLE_DEV'],
            '/drug/edit':                   ['ROLE_ADMIN','ROLE_DEV'],
            '/drug/update':                 ['ROLE_ADMIN','ROLE_DEV'],
            '/drug/delete':                 ['ROLE_ADMIN','ROLE_DEV'],
            '/drug/**':                     ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/trial/create':                 ['ROLE_ADMIN','ROLE_DEV'],
            '/trial/save':                   ['ROLE_ADMIN','ROLE_DEV'],
            '/trial/edit':                   ['ROLE_ADMIN','ROLE_DEV'],
            '/trial/update':                 ['ROLE_ADMIN','ROLE_DEV'],
            '/trial/delete':                 ['ROLE_ADMIN','ROLE_DEV'],
            '/trial/**':                     ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/roi/**':                      ['ROLE_ADMIN','ROLE_DEV'],

            '/panel/show':                  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/panel/list':                  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/panel/fetchAllData':          ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/panel/*':                     ['ROLE_DEV'],

            '/patassay/show':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/patassay/list':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],

            '/labassay/show':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/labassay/list':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/labassay/edit':               ['ROLE_ADMIN','ROLE_DEV'],
            '/labassay/delete':             ['ROLE_ADMIN','ROLE_DEV'],
            '/labassay/create':             ['ROLE_ADMIN','ROLE_DEV'],
            '/labassay/**':                 ['ROLE_ADMIN','ROLE_DEV'],

            '/seqrun/create':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/seqrun/save':                 ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/seqrun/edit':                 ['ROLE_ADMIN','ROLE_DEV'],
            '/seqrun/update':               ['ROLE_ADMIN','ROLE_DEV'],
            '/seqrun/delete':               ['ROLE_ADMIN','ROLE_DEV'],
            '/seqrun/authoriseRun/**':      ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/seqrun/**':                   ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

// Nobody should be allowed to touch these. We're locking them out in case the scaffold creates them:
            '/seqsample/create':           ['ROLE_ADMIN','ROLE_DEV'],
            '/seqsample/save':              ['ROLE_ADMIN','ROLE_DEV'],
            '/seqsample/edit':              ['ROLE_ADMIN','ROLE_DEV'],
            '/seqsample/update':            ['ROLE_ADMIN','ROLE_DEV'],
            '/seqsample/delete':           ['ROLE_ADMIN','ROLE_DEV'],

            '/seqSample/show':              ['ROLE_DEV','ROLE_ADMIN'],
            '/seqSample/rerunSample':       ['ROLE_UNMASKER','ROLE_DEV'],
            '/seqsample/editGeneMask':      ['ROLE_UNMASKER','ROLE_DEV'],
            '/seqsample/updateGeneMask':    ['ROLE_UNMASKER','ROLE_DEV'],
            '/seqsample/**':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

// Temporarily lazy allowing all under /seqsamplereport/**, for PATHOS-2269 mockup
// We should probably lock this down later to certain users and certain actions
// DKGM 13-April-2017
            '/seqsamplereport/**':          ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/seqvariant/create':           ['ROLE_ADMIN','ROLE_DEV'],
            '/seqvariant/save':             ['ROLE_ADMIN','ROLE_DEV'],
            '/seqvariant/edit':             ['ROLE_ADMIN','ROLE_DEV'],
            '/seqvariant/update':           ['ROLE_ADMIN','ROLE_DEV'],
            '/seqvariant/delete':           ['ROLE_ADMIN','ROLE_DEV'],
            '/seqVariant/revokeReview':     ['ROLE_ADMIN','ROLE_DEV'],
            '/seqVariant/authoriseReview':  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_LAB'],
            '/seqvariant/**':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/curvariant/create/**':            ['ROLE_ADMIN','ROLE_DEV'],
            '/curvariant/save/**':              ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
            '/curvariant/edit/**':              ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
            '/curvariant/update/**':            ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
            '/curvariant/delete/**':            ['ROLE_ADMIN','ROLE_DEV'],
            '/curvariant/list/**':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/curvariant/variantRows/**':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/curvariant/**':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_LAB'],

            '/evidence':                    ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],
            '/evidence/**':                 ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],

            '/grpVariant/**':               ['ROLE_ADMIN','ROLE_DEV'],

            '/pubmed/**':  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            //  Reference tables
            //
            '/refBic/**':                   ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refCosmic/**':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refKconfab/**':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refIarc/**':                  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refClinvar/**':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refEmory/**':                 ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refExon/create/**':           ['denyAll'],
            '/refExon/delete/**':           ['denyAll'],
            '/refExon/edit/**':             ['denyAll'],
            '/refExon/**':                  ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],


            '/refGene/genedesc':            ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refGene/list':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refGene/show':                ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/refGene/**':                  ['ROLE_ADMIN', 'ROLE_DEV'],
            '/refGene/create':              ['denyAll'],

            '/civicVariant/**':             ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],
            '/civicClinicalEvidence/**':    ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/transcript/**':               ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/annoVariant/**':              ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/icdO/list':                   ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB','ROLE_VIEWER'],

            '/clinContext/**':              ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR','ROLE_EXPERT','ROLE_LAB'],

            '/seqRelation/**':              ['ROLE_ADMIN','ROLE_DEV','ROLE_CURATOR'],
        ]

