/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j
import org.petermac.pathos.api.ApiUtils
import org.petermac.pathos.api.ExportReceiver
import org.petermac.pathos.amqp.AMQPExporter
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created for PathOS
 *
 * Description:
 *
 * Locator class to determine all file locations
 *
 * User: Kenneth Doig
 * Date: 18/10/2013
 * Time: 12:52 PM
 */
@Log4j
class Locator
{
    static final def fs = File.separator

    //  PathOS properties file
    //  Can be overridden by Environment variable "PATHOS_CONFIG" or
    //  by systems property -Dpathos.config=/path_to_pathos.properties
    //
    static String config_file = 'etc/pathos.properties'

    //  Application root directory
    //
    static String pathos_home = '/pathology/NGS/pa_uat'
    static Path home = Paths.get(pathos_home)

    //  Application properties object
    //
    static def prop

    //  Application etc directory
    //
    static String etcDir

    //  Root of pipeline sequencing and samples
    //
    static String samDir

    //  Absolute path to mp-vep.sh script
    //
    static String mpVepPath = "mp-vep.sh"


    //  Tomcat-writable directory for user-initated reruns of pipeline sequencing and samples
    //
    static String tomcatSeqrunDir = '/usr/share/tomcat/pipeline'

    //  Server Instance Molpath or Research
    //
    static String samBase = 'Molpath'


    //  Root of reporting directory
    //
    static String repDir

    //  Root of pubmed pdf directory
    //
    static String pubmedDir

    //  Root of logging directory
    //
    static String logDir

    //  Amplicon panel manifest directory for MiSeq
    //
    static String manifestDir   = '/pipeline/Runs/MiSeq/Indexes/'

    //  Panel directory for PathOS
    //
    static String panelDir      = '/pathology/NGS/Panels/'

    //  Path to default genome
    //
    static String genomePath    = '/data/reference/indexes/human/g1k_v37/picard/human_g1k_v37.fasta'

    //  Path to GATK ET file
    //
    static String gatkET        = '/config/binaries/gatk/maria.doyle_petermac.org.key'

    //  Host for data repository
    //
    static String dataServer    = ""    // Set to empty for default docker URLs, overridden by properties

    //  Full address for JIRA
    //
    static String jiraAddress   = "https://vm-115-146-91-157.melbourne.rc.nectar.org.au"    // defaults to Atlassian NeCTAR instance

    //  Proxy for JIRA
    //
    static String jiraProxy   = ""      // no proxy by default

    //  Username for JIRA
    //
    static String jiraUsername   = ""

    //  Pass for JIRA
    //
    static String jiraPass   = ""

    //  Contact email for sysadmin
    //
    static String sysadminEmail = "christopher.welsh@petermac.org"

    //  Path to DLLs
    //
    static String dllPath       = pathos_home + fs + "DLL" + fs

    //  Use Active Directory to authenticate users, instead of our Grails DB?
    //
    static Boolean useADAuthentication  = true

    //  Path to external Active Directory/LDAP config file for pathos
    //
    static String ADConfigurationFile = '/pathology/NGS/pa_prod/etc/pathos_ldap_conf.groovy'

    //
    static String dbUsername = null

    //  Database Password
    //
    static String dbPassword = null

    //  Database Port (defauly mysql 3306)
    static String dbPort = '3306'

    //  Database Host and Schema
    //
    static String dbHost = null

    static String dbSchema = null

    //  Pathos Environment
    //
    static String pathosEnv = 'noenv'

    //  Mutalyzer host
    //
    static String mutalyzer = 'https://mutalyzer.nl'

    //  CNV viewer URL
    //
    static String cnvViewerUrl = 'http://bioinf-pathos-test:3838/users/jmarkham/cnb'

    //  Anomaly  URL
    //
    static String anomalyUrl = 'http://api-anomaly.ap-southeast-2.elasticbeanstalk.com'

    //  VCF Upload method = either Anomaly or VcfLoader
    //
    static String vcfUploadMethod = 'Anomaly'

    //  Method to reload samples (ie when changing gene mask) - either Anomaly or VcfLoader
    //
    static String sampleReloadMethod = 'VcfLoader'


    static String pathosExportConfig
    static ExportReceiver pathosExport

    static LinkGenerator links


    //  default password for bootstrap pathos test users (bootstrap creates these on on-prod enviornments
    //
    static String defaultTestUserPassword = 'pathos7%^&'
    /**
     * Instance variable for class
     */
    private static final INSTANCE = new Locator()

    /**
     * Accessor method for Locator
     *
     * @return  A singleton instance of Locator
     */
    static Locator getInstance()
    {
        return INSTANCE
    }

    /**
     * Private constructor for Locator - only called once
     */
    private Locator()
    {
        println "Constructing a locator"
        init("For utilities, don't pass in a context string")
    }

    /**
     * Initialise the locator instance!
     */
    public static init(String context)
    {
        println "Initialising the locator"
        println "The context is... ${context}"

//  We have to set pathos_home, based on the context...


        //  Find application properties
        //
        pathos_home = System.getenv('PATHOS_HOME') ?: pathos_home
        pathos_home = System.getProperty('pathos.home') ?: pathos_home

// todo: /Haem and /Trace context is hardcoded, this should be env agnostic
        if(context == "/Haem") {
            pathos_home = "/pathology/NGS/pa_haem"
        } else if(context == "/Trace" || context == "/Traceback") {
            pathos_home = "/pathology/NGS/pa_trace"
        }

        home = Paths.get(pathos_home).toAbsolutePath()

        config_file = System.getenv('PATHOS_CONFIG') ?: config_file
        config_file = System.getProperty('pathos.config') ?: config_file

// todo: /Haem and /Trace context is hardcoded, this should be env agnostic
        if(context == "/Haem") {
            config_file = "/pathology/NGS/pa_haem/etc/pathos.properties"
        } else if(context == "/Trace" || context == "/Traceback") {
            config_file = "/pathology/NGS/pa_trace/etc/pathos.properties"
        }

        //Path config_path = home.resolve(Paths.get(config_file))
        Path config_path = Paths.get(config_file)

        File chf = new File(config_path.toUri())
        if (!chf.exists())
        {
            System.err.println "PathOS Configuration File doesn't exist ${chf.absolutePath}"
            System.err.println "PATHOS_HOME=${System.getenv('PATHOS_HOME')}"
            System.err.println "PATHOS_CONFIG=${System.getenv('PATHOS_CONFIG')}"
            System.err.println "pathos.config=${System.getProperty('pathos.config')}"
            System.err.println "Exiting..."
            System.exit(1)
        }

        //  Load application properties
        //
        prop = new Properties()
        def fis = new FileInputStream(chf)
        def is  = new BufferedInputStream(fis)
        prop.load(is)
        is.close()

        //  Set application home
        //
        if (prop.getProperty('pathos.home')) {
            pathos_home = prop.getProperty('pathos.home')
            home = Paths.get(pathos_home).toAbsolutePath()
        }
        System.err.println("Using PathOS Configuration File ${chf.absolutePath}")
        System.err.println("PathOS Home ${pathos_home}")
        File homeDir = new File(home.toUri())
        if (!homeDir.directory)
        {
            System.err.println("PathOS Home is not a directory.")
            System.err.println("Exiting...")
            System.exit(1)
        }

        //  Set etc dir
        //
        etcDir = pathos_home + fs + "etc" + fs

        //  Set reporting dir
        //
        repDir = pathos_home + fs + "Report" + fs
        if ( prop.getProperty('pipeline.reports')) repDir = prop.getProperty('pipeline.reports')

        //  Set pubmed pdf dir
        //
        pubmedDir = pathos_home + fs + "Pubmed" + fs
        if ( prop.getProperty('pubmed.pdfs')) pubmedDir = prop.getProperty('pubmed.pdfs')

        //  Set logging dir
        //
        logDir = pathos_home + fs + "log" + fs
        if ( prop.getProperty('pipeline.logging')) logDir = prop.getProperty('pipeline.logging')

        //  Set Samples root dir
        //
        samDir = new File(pathos_home).parent + fs + "Molpath" + fs
        if ( prop.getProperty('pipeline.samples')) samDir = prop.getProperty('pipeline.samples')

        //  Set Instance
        //
        def path = samDir.tokenize('/')
        samBase = path[-1]
//        assert samBase in ['Molpath','Research','PipeCleaner','Testing']


        //  Set panel manifest directory
        //
        if ( prop.getProperty('tomcat.seqrun.directory')) tomcatSeqrunDir = prop.getProperty('tomcat.seqrun.directory')

        //  Set panel manifest directory
        //
        if ( prop.getProperty('pipeline.manifests')) manifestDir = prop.getProperty('pipeline.manifests')

        // Mutalyser Host for the pipeline
        //
        if ( prop.getProperty('pipeline.mutalyzer')) mutalyzer = prop.getProperty('pipeline.mutalyzer')

        //  Set default genome path
        //
        if ( prop.getProperty('genome.path')) genomePath = prop.getProperty('genome.path')

        //  Set GATK ET file path
        //
        if ( prop.getProperty('gatk.etpath')) gatkET = prop.getProperty('gatk.etpath')

        //  Set PathOS panel root directory
        //
        if ( prop.getProperty('pipeline.panels')) panelDir = prop.getProperty('pipeline.panels')

        //  Set PathOS DLL path
        //
        if ( prop.getProperty('pipeline.dll')) dllPath = prop.getProperty('pipeline.dll')

        //  Set Jira API IP address
        //
        if ( prop.getProperty('jira.address')) jiraAddress = prop.getProperty('jira.address')

        //  Set Jira Proxy
        //
        if ( prop.getProperty('jira.proxy')) jiraProxy = prop.getProperty('jira.proxy')

        //  Set Jira username & pass
        //
        if ( prop.getProperty('jira.username')) jiraUsername = prop.getProperty('jira.username')

        if ( prop.getProperty('jira.pass')) jiraPass = prop.getProperty('jira.pass')

        if ( prop.getProperty('sysadmin.email')) sysadminEmail = prop.getProperty('sysadmin.email')

        //  Set Data Repository Server
        //
        if ( prop.getProperty('pipeline.dataserver')) dataServer = prop.getProperty('pipeline.dataserver')

        //  Are we using AD for Curate auth?
        //
        if ( prop.getProperty('use.ad.authentication')) useADAuthentication = prop.getProperty('use.ad.authentication').toBoolean()

        //  Location of AD config file?
        //
        if ( prop.getProperty('ad.configuration.path')) ADConfigurationFile = prop.getProperty('ad.configuration.path')

        if ( prop.getProperty('default.test.user.password')) defaultTestUserPassword = prop.getProperty('default.test.user.password')


        //  Set DB paramters
        //
        dbUsername = prop.getProperty('db.username')
        dbPassword = prop.getProperty('db.password')
        dbSchema   = prop.getProperty('db.schema')
        dbHost     = prop.getProperty('db.host')
        if(prop.getProperty('db.port'))             dbPort     = prop.getProperty('db.port')
        pathosEnv  = prop.getProperty('pathos.env')

        // Set the export config, and load it if necessary
        //
        pathosExportConfig = prop.getProperty('pathos.export.config')
        if (pathosExportConfig) {
            try {
                pathosExport = ApiUtils.loadExporter(pathosExportConfig, home)
            } catch (Exception e) {
                System.err.println("Caught Exception loading ${pathosExportConfig}")
                System.err.println("${e.getMessage()}")
            }
        }

        //  Set CNV viewer URL
        //
        if ( prop.getProperty('cnv.viewer.url')) cnvViewerUrl = prop.getProperty('cnv.viewer.url')

        //  Set anomaly URL
        //
        if ( prop.getProperty('anomaly.url')) anomalyUrl = prop.getProperty('anomaly.url')

        //  Set path to mp-vep script
        //
        if ( prop.getProperty('mp.vep.path')) mpVepPath = prop.getProperty('mp.vep.path')


        //  Set CNV viewer URL
        //
        if ( prop.getProperty('vcf.upload.method')) vcfUploadMethod = prop.getProperty('vcf.upload.method')

        //  Set CNV viewer URL
        //
        if ( prop.getProperty('sample.reload.method')) sampleReloadMethod = prop.getProperty('sample.reload.method')

        // Initialize the link generator.
        if (prop.getProperty('external.links.config')) {
            println "external.links.config = `${prop.getProperty('external.links.config')}`"
            links = new LinkGenerator(prop.getProperty('external.links.config'))
        }
    }

    /**
     * Location of all database backups
     *
     * @param suffix    Optional directory to append
     * @return          Full path to directory
     */
    static String backupDir( String suffix = '' )
    {
        def budir = pathos_home + fs + 'Backup' + fs
        if ( prop.getProperty('db.backup')) budir = prop.getProperty('db.backup')

        assert new File(budir).exists(), "Backup directory should exist"
        if ( suffix )
        {
            budir += suffix + fs

            //  Create sub dir if needed
            //
            def sub = new File( budir )
            if ( ! sub.exists()) sub.mkdir()
        }

        return budir
    }
}
