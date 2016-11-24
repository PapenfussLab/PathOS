/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

import groovy.time.TimeDuration

/**
 * Create Audit log of pipeline commands from Bpipe XML file
 *
 * Author:  Ken Doig
 * Date:    6-Aug-2016
 */

import groovy.util.logging.Log4j
import groovy.time.TimeCategory
import org.petermac.util.Locator
import java.text.SimpleDateFormat

@Log4j
class PipeLogger
{
    static List<Map> stats = []

    //static def loc = Locator.instance      // Locator class for file locations

    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
            usage: 'PipeLogger [options] in.xml out.tsv',
            header: '\nAvailable options (use -h for help):\n',
            footer: '\nCreate pipeleine audit file for loading into PathOS\n')

        cli.with
        {
            h(longOpt: 'help',      required: false, 'Usage Information' )
        }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h || opt.arguments().size() != 2)
        {
            cli.usage()
            return
        }

        //  Run the program
        //
        log.info("PipeLogger " + args )

        //  Extract file names
        //
        List<String> extra = opt.arguments()
        def bpXml  = extra[0]
        def audit  = extra[1]

        //  Open files
        //
        File xmlFile = new File( bpXml )
        if ( ! xmlFile )
        {
            log.fatal( "XML file ${xmlFile} doesn't exist")
            System.exit(1)
        }
        File auditFile = new File( audit )
        if ( auditFile.exists())
        {
            log.warn( "File ${audit} already exists, deleting")
            auditFile.delete()
        }

        //  Create audit log file
        //
        boolean ok = runLogger( xmlFile, auditFile )
        if ( ! ok )
        {
            log.fatal("Failed, processing XML ${xmlFile} into ${auditFile}")
            System.exit(1)
        }

        log.info("Done, processed XML ${xmlFile} into ${auditFile}")
    }

    /**
     * Parse the XML and generate a TSV file
     *
     * @param   xmlFile     Bpipe XML to parse
     * @param   auditFile   Output TSV file
     * @return              True if OK
     */
    static boolean runLogger( File xmlFile, File auditFile )
    {
        List<Map> cmds = parseXml( xmlFile )

        //  Output commands as TSV file
        //
        setAuditHeader( xmlFile, auditFile )

        for ( cmd in cmds )
        {
            getStats( cmd )
            auditFile << fmtAudit( cmd )
        }

        //  Output stage stats (by duration descending)
        //
        def sorted = stats.sort { -it.duration }
        for ( stat in stats )
            println stat

        return true
    }

    static List parseXml( File inf )
    {
        List   cmdlist = []
        String seqrun  = ''

        //  Unpack bpipe command XML
        //
        def parser = new XmlParser()
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Node node = parser.parseText( inf.getText())

        //  Get top level run attributes
        //
        String succeeded = node.succeeded.text()
        String start     = node.startDateTime.text()
        String end       = node.endDateTime.text()
        NodeList cmds    = node.commands.command

        //  Calculate total pipeline run time
        //
        def sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")            // 2016-08-05 15:36:11
        Date sd = sdf.parse(start)
        Date ed = sdf.parse(end)
        TimeDuration diff = TimeCategory.minus( ed, sd )
        Double duration = diff.hours * 60 + diff.minutes + diff.seconds / 60.0
        String exitCode = ( succeeded == 'true' ? 0 : 1 )

        Map runMap = [stage: 'Seqrun', branch: 'root', start: sd, end: ed, exit: exitCode, duration: duration]

        log.info "Run ${succeeded} ${start} ${end} cmds=${cmds.size()} " +
        "duration: ${diff.days} days ${String.format( "%02d", diff.hours)}:${String.format( "%02d", diff.minutes)}:${String.format( "%02d", diff.seconds)}"

        for ( cmd in cmds )
        {
            //  Create a map of command meta data
            //
            Map cmdMap = [:]
            cmdMap << [stage:   cmd.stage.text()]
            cmdMap << [branch:  cmd.branch.text()]
            cmdMap << [exit:    cmd.exitCode.text()]
            cmdMap << [content: cmd.content.text()]

            //  Get command start/finish timestamps
            //
            sd = sdf.parse(cmd.start.text())
            ed = sdf.parse(cmd.end.text())
            diff = TimeCategory.minus( ed, sd )
            cmdMap << [start:   sd]
            cmdMap << [end:     ed]

            //  Calculate duration of command
            //
            duration = diff.hours * 60 + diff.minutes + diff.seconds / 60.0
            if ( diff.days != 0 )
                duration = -1
            cmdMap << [duration:duration]

            //  Find Seqrun/Sample from command
            //
            Map ss = getSeqrun( cmd.content.text())
            if ( ss )
            {
                seqrun = ss.seqrun
                cmdMap << [seqrun: seqrun]
                cmdMap << [sample: ss.sample]
            }

            cmdlist << cmdMap
        }

        //  Add entire run as an entry
        //
        runMap.seqrun = seqrun
        cmdlist << runMap

        return cmdlist
    }

    /**
     *
     * @return
     */
    static String fmtAudit( Map cmd )
    {
        List cols = []

        cols << 'pipeline'         //        category
        cols << cmd.seqrun         //        seqrun
        cols << ''                 //        variant
        cols << cmd.sample         //        sample
        cols << cmd.stage          //        task
        cols << cmd.end            //        complete
        cols << cmd.duration       //        elapsed
        cols << 'bpipe'            //        software
        cols << '0.9.8.6_rc2'      //        version
        cols << 'bioinf'           //        username
        cols << cmd.content        //        description

        return cols.join('\t') + '\n'
    }

    /**
     * Generate header for output file
     *
     * @param inf       Input  File
     * @param outf      Output File
     */
    static void setAuditHeader( File inf, File outf )
    {
        def now = new Date()

        outf << """##    Created by PipeLogger on ${now} from ${inf.absolutePath}
##
#category\tseqrun\tvariant\tsample\ttask\tcomplete\telapsed\tsoftware\tversion\tusername\tdescription\t
"""
    }

    /**
     * Summarise stage statistics
     *
     * @param cmd   Command meta data
     */
    static void getStats( Map cmd )
    {
        Map stage = stats.find { it.stage == cmd.stage }
        if ( ! stage )
        {
            stage = [stage: cmd.stage, count: 0, duration: 0.0, nodurn: 0]
            stats << stage
        }

        ++stage.count
        if ( cmd.duration != -1 )
            stage.duration += cmd.duration
        else
            ++stage.nodurn
    }

    /**
     * Find Seqrun/Sample from filenames embedded in command
     *
     * @param   cmd  Command to parse
     * @return       Map [ seqrun: sample: ]
     */
    static Map getSeqrun( String cmd )
    {
        def match = ( cmd =~ /\/pathology\/NGS\/Samples\/[^\/]+\/([^\/]+)\/([^\/]+)\// )
        if ( match.count >= 1)
        {
            String seqrun = match[0][1]
            String sample = match[0][2]

            if ( sample.startsWith( 'RunPipe')) sample = ''
            return [seqrun: seqrun, sample: sample ]
        }

        return [:]
    }
}
