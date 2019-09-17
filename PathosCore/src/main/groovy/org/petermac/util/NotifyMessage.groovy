/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.util

import groovy.sql.Sql
import groovy.util.logging.Log4j

/**
 * Description:
 *
 * Generate a YAML notification message for Mario
 *
 * Author: Ken Doig
 * Date: 25-May-17
 */
@Log4j
class NotifyMessage
{
    static Sql sql = null       // MySQL object

    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'NotifyMessage [options]',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nCreate a YAML message for Mario\n')

        cli.with
                {
                    h(longOpt:   'help',            'Usage Information', required: false)
                    f(longOpt:   'file',            args: 1, required: false, 'File path triggering notification' )
                    s(longOpt:   'status',          args: 1, required: false, 'message status [started]' )
                    q(longOpt:   'quiet',           'Suppress info messages' )
                    sr(longOpt:  'seqrun',          args: 1, required: false, 'Seqrun name' )
                    ss(longOpt:  'sample',          args: 1, required: false, 'Sample name' )
                    srp(longOpt: 'seqrunPattern',   args: 1, required: false, 'Regex pattern for finding seqrun in filepath' )
                    ssp(longOpt: 'samplePattern',   args: 1, required: false, 'Regex pattern for finding sample in filepath' )
                    e(longOpt:   'event',           args: 1, required: false, 'Event type ["file created"]' )
                    d(longOpt:   'desc',            args: 1, required: false, 'Notification description' )
                }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Open DB
        //
        def host   = "localhost"
        def schema = "dbvarys"
        def pass   = "gain-star-formal-bad-1"
//        pass = "pmc1512"
        sql = Sql.newInstance( "jdbc:mysql://${host}:3306/${schema}", "bioinformatics", pass, 'com.mysql.jdbc.Driver' )

        //  extract seqrun/sample names
        //
        Map sampleMap = [ seqrun: opt.seqrun, sample: opt.sample ]
        sampleMap = parsePath( opt.file ?: null, sampleMap, opt.seqrunPattern ?: null, opt.samplePattern ?: null )

        //  Run the program
        //
        if ( ! opt.quiet ) log.info("NotifyMessage " + args )

        Map msg = createMessage( opt.file ?: null, opt.status ?: 'start', opt.event ?: 'file created', opt.desc ?: null, sampleMap )

        println "---"
        println YamlUtil.toYaml(msg)

        //  Add event to database
        //
        createDbEvent( msg )
    }

    /**
     * Compose message a Map
     *
     * @param path      File path
     * @param status    Event status
     * @param event     Event type
     * @param desc      Event description
     * @return          Map of message
     */
    private static Map createMessage( String path, String status, String event, String desc, Map sm )
    {
        File   f      = new File(path)
        String flast  = new Date(f?.lastModified()).format('yyyy/MM/dd HH:mm:ss')
        String now    = new Date().format('yyyy/MM/dd HH:mm:ss')
        Map    params = [ sender: 'Notify', source: [ 'petermac', 'molpath', 'mario', 'notify' ], status: status, 'message-timestamp': now, timestamp: flast, content: [:] ]

        if ( path   ) params.content     << [file:  path  ]
        if ( event  ) params.content     << [event: event ]
        if ( desc   ) params.content     << [description: desc ]
        if ( sm.seqrun ) params.content  << [seqrun: sm.seqrun ]
        if ( sm.sample ) params.content  << [sample: sm.sample ]

        return params
    }

    /**
     * Parse file path to extract Seqrun and/or Sample
     *
     * @param   path    File path
     * @param   sm      Map of parameters
     * @param   srp     Regexp of seqrun in path
     * @param   ssp     Regexp of sample in path
     * @return          Map of seqrun and sample
     */
    private static Map parsePath( String path, Map sm, String srp, String ssp )
    {
        if ( ! path ) return sm

        try
        {
            //  Match seqrun pattern to file
            //
            if ( srp )
            {
                def match = ( path =~ /${srp}/ )
                if ( match.count == 1)
                {
                    sm.seqrun = match[0][1]
                }
                else
                {
                    log.warn( "Couldn't match pattern ${srp} to ${path}" )
                }
            }

            //  Match sample pattern to file
            //
            if ( ssp )
            {
                def match = ( path =~ /${ssp}/ )
                if ( match.count >= 1)
                {
                    sm.sample = match[0][1]
                }
                else
                {
                    log.warn( "Couldn't match pattern ${ssp} to ${path}" )
                }
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to apply patterns to ${path}" + e)
        }

        return sm
    }

    /**
     * Add a row to the database for this event
     *
     * @param   msg Map of message values
     * @return
     *

     sender: 				Notify
     source: 				[petermac, molpath, mario, notify]
     status: 				started
     message-timestamp: 		2017/10/11 16:58:10
     timestamp: 				2017/10/10 05:45:07
     file: 					/pipeline/Runs/NextSeq/171009_NS500817_0227_AHMTKKBGX3/ProjectFolders/Project_Bhargavi-Yellapu/Sample_NA12878
     event: 					BCL2FASTQ
     description: 			New FASTQ
     seqrun: 				171009_NS500817_0227_AHMTKKBGX3
     sample: 				NA12878
     panel:

     */
    private static boolean createDbEvent( Map msg )
    {
        Map con = msg.content

        String sample = con.sample      ? "'${con.sample}'"      : null
        String seqrun = con.seqrun      ? "'${con.seqrun}'"      : null
        String file   = con.file        ? "'${con.file}'"        : null
        String desc   = con.description ? "'${con.description}'" : null
        String sqlins = """
                        insert into event values (
                        '${msg.sender}',
                        '${msg.source}',
                        '${msg.status}',
                        '${msg.'message-timestamp'}',
                        '${msg.timestamp}',
                        ${file},
                        '${con.event}',
                        ${desc},
                        ${seqrun},
                        ${sample},
                        null
                        )"""
//        sqlins = "insert into event values ( 'Notify', '[1,2]', 'start', '2017/10/11 16:58:10', '2017/10/11 16:58:10', 'tmp', 'BCL2FASTQ', 'dummy event', '171003', 'sample', null)"
        boolean ret = sql.execute( sqlins )

        if ( ret )
        {
            log.error( "SQL insert failed for: ${sqlins}" )
        }
        return ret
    }
}

