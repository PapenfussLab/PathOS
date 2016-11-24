/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

/**
 * Created for PathOS.
 *
 * Description:
 *
 * HouseKeeping class for daily PathOS chores
 *
 * User: doig ken
 * Date: 18/10/2013
 * Time: 12:52 PM
 */

import groovy.util.logging.Log4j
import org.petermac.util.Mailer
import groovy.json.*
import org.petermac.util.Locator
import java.util.Properties;
import org.petermac.util.RunCommand

@Log4j
class HouseKeeping
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */

    //people to email if Pathos is down
    static final notifyIfDown = ['andrei.seleznev@petermac.org','ken.doig@petermac.org','david.ma@petermac.org','christopher.welsh@petermac.org']

    static void main(args)
    {


        def cli = new CliBuilder(
                usage: 'HouseKeeping [options]',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nPerform daily PathOS house keeeping chores\n')

        cli.with
                {
                    h(longOpt: 'help',  'Usage Information', required: false)
                    b(longOpt: 'backup',  args: 1, 'backup this schema to standard backup directory')
                    c(longOpt: 'checkdir', args: 0, 'check directory file status')
                    a(longOpt: 'archive', args: 1, 'archive this schema to standard backup directory')
                    f(longOpt: 'filtercheck', args: 1, 'check if filter flags are set correctly')
                    d(longOpt: 'debug', args:0, 'run in debug mode')
                    j(longOpt: 'jira', args:0, 'make jira issue on failure')
                    i(longOpt: 'isup', args:1, 'check if URL is up - will send warning email to PathOS developers unless debug is on')
                }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Run the program
        //
        def start = System.currentTimeMillis()


        logCmdInfo(args)    //log.info with arguments


        if ( opt.backup  ) backup( opt.backup )
        if ( opt.archive ) archive( opt.archive )

        if  ( opt.isup ) {
            boolean email = false
            if  (opt.debug)  email = true
            checkifup(opt.isup,email)
        }

        if ( opt.checkdir  ) {
            List argin = opt.arguments()
            if ( argin.size() < 1 )
            {
                cli.usage()
                return
            }
                checkdir(argin, opt.debug, opt.jira)
        }
        if ( opt.filtercheck  ) filtercheck( opt.filtercheck,  opt.debug, opt.jira)

        Integer elapsed = (System.currentTimeMillis() - start) / (60 * 1000)    // minutes elapsed

        log.info("Done in ${elapsed} minute(s).")
    }

    static void logCmdInfo(args) {
        def argList = args.toList()

        def maxsize = 20
        if (argList.size() > maxsize) {
            String argString = argList[0..maxsize].toString().replace("[", "").replace("]", "").trim()
            log.info("HouseKeeping: " + argString + "... " )
        } else {
            String argString = argList.toString().replace("[", "").replace("]", "").trim()
                log.info("HouseKeeping: " + argString )
        }
        //  log.info("HouseKeeping " + args )
    }

    /**
     * Check a URL to see if the web server is up
     *
     * @param environment   PathOS DB instance default: pa_prod
     */
    static void checkifup(String url, boolean notify = false) {
        //list of addresses to mail in case PathOS is down
        //
        println "URL " + url
        URL u
        try {
             u = new URL(url)
        } catch (java.net.MalformedURLException e) {
            println "Malformed URL Exception. Have you specified protocol e.g. ('http://')?"
            System.exit(1)
        }

        //connect to appropriate pathos URL
        //make a HEAD request and see if we get a response
        //
        HttpURLConnection huc = (HttpURLConnection)u.openConnection();

        huc.setRequestMethod("HEAD");

        try {
            huc.connect();

            if ((huc.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                //System.exit(0)  //exit silently with success
            } else {
                def reason = "Connection not OK: Response code " + huc.getResponseCode()

                if(notify) {
                    sentIsDownEmail(url, reason, notifyIfDown)
                }

                println reason

                System.exit(1)
            }
        }   catch ( java.net.ConnectException e) {
            def reason = "Unable to connect (got ConnectException)"

            if(notify) {
                sentIsDownEmail(url, reason, notifyIfDown)
            }

            println reason
            System.exit(1)
        }
    }

    /**
     *  Send a "pathos is down" email to anybody who cares
     *
     * @param env
     * @param reason
     * @param addresses
     */
    static void sentIsDownEmail(String env, String reason, ArrayList addresses) {
        for (toAddress in addresses) {


            String head = "[Housekeeping] Pathos ${env} is down"
            String msgBody = "Housekeeping IsUp checked Pathos ${env} and could not connect. Reason: ${reason}"
            String mailout = Mailer.commandLineSendMail(toAddress,head,msgBody)
            if(mailout) {
                println "HouseKeeping: mail to ${toAddress} failed"
                println "Stdout: ${mailout}"
            }
        }

    }

    /**
     * calls DirChecker check a directory tree and its samples for missing BAMs, IGVs etc
     * this is a Grails rewrite of the CheckDir.sh script (/pathology/NGS/Samples/Admin/CheckDir.sh)
     *
     * @param directories
     * @param debug
     * @param makeJira
     */
    static void checkdir( List directories, boolean debug, boolean makeJira) {
        HashMap allErrors = new HashMap()
        def checker = new DirChecker()

        def validation

        def dircount = 0
        for ( directory in directories )
        {
            if ( debug ) { println "Checking ${directory}" }
            def thisdir = new File(directory)
            if (thisdir.isDirectory()) {
                dircount = dircount + 1
                validation = checker.validateSampleDir(directory)   //we have no wildcard - just validate one dir
                allErrors.putAll(validation)
            }
            else {
                if ( debug ) {
                    System.err.println "Found non-directory in list and skipped it: ${directory}"
                }
            }
        }

        printValidationResults(allErrors,debug,makeJira)

        def errorscount = 0
        allErrors.each{     //count errors
            key,value ->
                for (errorstring in value) {
                    errorscount = errorscount + 1
                }
        }

        System.err.println("HouseKeeping Directory Checker checked ${dircount} dirs and found ${errorscount} errors")

    }

    /**
     * Print out validation results
     *
     * @param validation
     * @param debug
     * @param makeJira
     */
    static void printValidationResults(validation, debug, makeJira) {
        String valout = ''

        validation.each{
            key,value ->
                valout = valout + "${key}" + "\t"
                for (errorstring in value) {
                    valout = valout+ "\t" + " ${errorstring} "
                }

                valout = valout + "\n"
        }

        if(!validation.isEmpty()) {
                      //debug mode: print output

            println valout


            if (makeJira) { //if we have some validaiton failures, make an issue in JIRA for them if we've set the flag
                println "Errors found. Making JIRA issue..."
                def jnotifier = new JiraNotifier()
                def response = jnotifier.createJiraIssue("PathOS Housekeeping CheckDir errors", valout, "Bug")

                if (response) {
                    if (response.containsKey('errors')) {
                        println "Error creating issue! Response:"
                        println response
                    }

                    if (response.containsKey('id') && response.containsKey('key')) {
                        println "Issue created. Issue ${response['id']} ${response['key']} "

                        //assign it to pathos ops now
                        int newIssueId = response['id'] as int //cast to int
                        jnotifier.assignJiraIssue('pathos.user', newIssueId)
                    }
                }
            }
        }
    }

    /**
     * Check filter flags for all seqvariants to see if they're consistent
     */
    static void filtercheck(rdb,debug,jira) {
        def checker = new FilterflagChecker()
        checker.checkFilterFlags(rdb,debug)

        //todo make a jira issue if jira is set. low priority.
    }

    /**
     * Backup database schema
     *
     * @param dbname    Schema to backup - all tables
     */
    static void backup( String dbname )
    {
        //  snapshot the database schema
        //
        def db = new MysqlCommand( dbname )
        String bufile = db.snapshot( Locator.backupDir( dbname ))

        //  Gzip the snapshot
        //
        def cmd = "gzip -q ${bufile}"
        new RunCommand( cmd ).run()
    }

    /**
     * Archive database backups
     *
     * @param dbname    Schema to Archive
     */
    static void archive( String dbname )
    {
        DbConnect db = new DbConnect( dbname )
        def budir    = new File( Locator.backupDir( dbname ))
        def archdir  = new File( budir, 'Archive' )

        if ( budir.exists())
        {
            budir.eachFileMatch( ~/snapshot_.*.gz/ )
            {
                if ( it.name =~ /.*1[29]00.gz/ )
                {
                    def cmd = "cp -v ${it.path} ${archdir.path}"
                    log.info( "Archiving ${it.name} "+ cmd)
                    new RunCommand( cmd ).run()
                }
                log.info( "Deleting ${it.name}")
                it.delete()
            }
        }
    }
}
