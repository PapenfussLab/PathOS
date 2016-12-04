/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Manage bpipe stage and pipeline level reporting
 * A shared module for common handling of bpipe events
 *
 *  Global parameters:
 *
 *  SHOW_STAGE_FAIL     Set to 1 to email on stage failure events
 *  MailList            List of email addresses to notify
 *  PipelineType        Subject line text for email filtering
 *  HollyHost           Host for Holly eg bioinf-pathos-test
 *
 *  Ken Doig    18-Aug-16   Initial
 */

import static bpipe.PipelineEvent.*
import java.text.SimpleDateFormat

File    stglog      = new File('stage.log')
Date    pipeStart   = new Date()
boolean header      = false

//  Email event listener
//
[ STAGE_FAILED, STARTED, FINISHED ].each
{
    event ->

        bpipe.EventManager.getInstance().addListener( event )
        {
            eventType, desc, details ->

                //  Ignore Stage failed events ?
                //
                if ( eventType == 'STAGE_FAILED' && ! SHOW_STAGE_FAIL ) return

                // Pipeline failure and finish email - assume we are running this from a
                // directory within a pipeline data repository and the path contains a seqrun identifier
                //
                Map m = getSeqrun( new File('.').absolutePath )
                String seqrun  = m?.seqrun
                String basedir = m?.basedir

                //  Find the pipeline elapsed time
                //
                Double pipeElapsed = ((new Date().time - pipeStart.time) as Double) / 60000.0    // time in minutes

                //  Construct email message and run
                //
                def mailcmd = "echo 'Pipeline Logger: ${PipelineType}: ${eventType} for ${seqrun} in ${basedir}\n${desc} after ${String.format( "%.3f", pipeElapsed)} minutes' | mail -s '#Pipeline Logger: ${PipelineType} ${seqrun} ${eventType} ${desc}' ${MailList.join(' ')}"
                def listcmd = ["/bin/bash", "-c", mailcmd ]
                def sout = new StringBuffer()
                def serr = new StringBuffer()

                //	Run command within bash shell environment
                //
                def proc = listcmd.execute()
                proc.waitForProcessOutput( sout, serr)
                def exitval = proc.exitValue()

                if ( exitval || sout || serr ) println "Error: Output from mail command [${mailcmd}] in mp_event.groovy stdout=${sout} stderr=${serr}"
        }
}

//  Create a realtime stage event log
//
[STARTED, STAGE_STARTED, STAGE_COMPLETED, STAGE_FAILED, FINISHED].each
{
    event ->

        bpipe.EventManager.getInstance().addListener( event )
        {
            eventType, desc, details ->

                def stg = details.stage
                def now = new Date()
                def df  = new SimpleDateFormat('yyMMdd')
                def tf  = new SimpleDateFormat('hh:mm:ss.ss')

                Map flds = [:]

                flds << [type:  eventType as String ]
                flds << [desc:  desc ]

                //  Mine the bpipe objects for useful attributes
                //
                if ( stg )
                {
                    flds << [stage: stg.stageName ]

                    def ctx = stg.context
                    if ( ctx )
                    {
                        if ( ctx.documentation )
                        {
                            def doc = ctx.documentation
                            if ( doc.title  ) flds << [ title:   doc.title ]
                            if ( doc.author ) flds << [ author:  doc.author ]
                            if ( doc.desc   ) flds << [ stgdesc: doc.desc ]
                        }
                        if ( ctx.inputs  instanceof List ) flds << [inputs:  ctx.inputs]
                        if ( ctx.outputs instanceof List ) flds << [outputs: ctx.outputs]

                        if ( ctx?.branch )
                        {
                            flds << [ branch_sr: ctx?.branch?.SEQRUN ]
                            flds << [ branch_ss: ctx?.branch?.SAMPLE ]
                        }

                        //  Collect stage commands
                        //
                        String cmds = ctx.trackedOutputs*.value*.command.join(" ")
                        if ( cmds )
                        {
                            flds << [ commands: cmds ]
                            flds << getSeqrun( cmds )
                        }

                        //  Get command timestamps
                        //
                        Double elap = stg.endDateTimeMs - stg.startDateTimeMs
                        if ( elap )
                        {
                            Double elapsed = elap / 1000.0
                            flds << [ start:   new Date(stg.startDateTimeMs as long) ]
                            flds << [ end:     new Date(stg.endDateTimeMs as long) ]
                            flds << [ elapsed: elapsed ]
                        }
                    }
                }

                //  Output header
                //
                if ( ! header )
                {
                    header = true
                    stglog.delete()
                    pipeStart = now
                    stglog << """##    Created by Bpipe EventManager on ${now}
##
#datestamp\ttimestamp\truntime\tevent\tseqrun\tsample\tstage\telapsed\tdescription\tattributes
"""
                }

                //  Find the pipeline elapsed time
                //
                Double pipeElapsed = ((now.time - pipeStart.time) as Double) / 60000.0    // time in minutes

                //  Output selected attributes of the event stage
                //
                List cols = []
                cols << df.format(now)
                cols << tf.format(now)
                cols << String.format( "%.3f", pipeElapsed)
                cols << flds.type
                cols << flds.branch_sr
                cols << flds.branch_ss
                cols << flds.stage
                cols << flds.elapsed
                cols << flds.desc
                cols << flds.toMapString()

                stglog << cols.join("\t") + "\n"

                //  Only notify Hooly for stage start/finish events
                //
                if ( flds.type in [ 'STAGE_STARTED','STAGE_COMPLETED'] )
                {
                    //  Call Holly notifier, ignore return at the moment
                    //
                    def sample = flds.branch_ss
                    //sample = '15K2858'
                    int ret = NotifyHolly( flds.type, flds.stage, flds.branch_sr , sample )
                }
        }
}

/**
 * Notify Holly of an event
 *
 * @param event     Bpipe event
 * @param stage     Bpipe stage
 * @param seqrun    Seqrun of stage
 * @param sample    Sample of stage
 * @return          0 if OK, exitval otherwise
 */
int NotifyHolly( String event, String stage, String seqrun , String sample )
{
    //  Only notify if we have a sample
    //
    if ( ! sample ) return 0

    if ( stage == "mp_align"      ) return NotifyUrl( event, sample, "Status_Align" )
    if ( stage == "mp_runCanary"  ) return NotifyUrl( event, sample, "Status_Canary" )
    if ( stage == "mp_loadPathOS" ) return NotifyUrl( event, sample, "Status_Pathos" )

    return 0
}

/**
 * Notify using a URL and curl
 *
 * @param event
 * @param sample
 * @param status
 * @return
 */
int NotifyUrl( String event, String sample, String status )
{
    //println( "INFO: in NotifyUrl at ${event} ${sample} ${status}")

    def end = ''
    if ( event == 'STAGE_COMPLETED' ) end = '_end'

    def urlcmd  = "curl --fail 'http://${HollyHost}/Holly/workflowStatus/setStatus?sampleNames=${sample}&status=${status}${end}'"
    def listcmd = ["/bin/bash", "-c", urlcmd ]
    def sout = new StringBuffer()
    def serr = new StringBuffer()

    //	Run command within bash shell environment
    //
    def proc = listcmd.execute()
    proc.waitForProcessOutput( sout, serr)
    def exitval = proc.exitValue()

    if ( exitval ) println "Error: Exit=${exitval} Command [${urlcmd}] in mp_event.groovy stdout=${sout} stderr=${serr}"

    return exitval
}

/**
 * Find Seqrun/Sample from filenames embedded in command
 * Note: this assumes pipeline is being run wthin the pipeline data repository eg /pathology/NGS/Samples/<base>/<seqrun>/<sample>
 * Todo: Allow the user to decide how to determine seqrun/sample
 *
 * @param   cmd  Command to parse
 * @return       Map [ seqrun: sample: basedir: ]
 */
static Map getSeqrun( String cmd )
{
    def match = ( cmd =~ /\/pathology\/NGS\/Samples\/([^\/]+)\/([^\/]+)\/([^\/]+)\// )
    if ( match.count >= 1)
    {
        String basedir = match[0][1]
        String seqrun  = match[0][2]
        String sample  = match[0][3]

        if ( sample.startsWith( 'RunPipe')) sample = ''
        return [seqrun: seqrun, sample: sample, basedir: basedir ]
    }

    return [:]
}
