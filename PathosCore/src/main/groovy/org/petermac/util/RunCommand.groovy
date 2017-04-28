/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.util

import groovy.util.logging.Log4j

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 15/04/13
 */

@Log4j
class RunCommand
{
    List    listcmd

    RunCommand( List cmd )
    {
        this.listcmd = cmd
    }

    RunCommand( String cmd )
    {
        this.listcmd = ["/bin/bash", "-c", cmd ]
    }

    /**
     * Run a command for this class returning a Map of result
     *
     * @param   bg      True if command is to be run in background
     * @return          Map of result [ stdout: , stderr:, exit: <exit value> ]
     */
    Map runMap( boolean bg = false )
    {
        def sout = new StringBuffer()
        def serr = new StringBuffer()

        //	Run command within bash shell environment
        //
        def proc = listcmd.execute()

        //	Flush output, in background or foreground
        //
        if ( bg )
        {
            //  background: consume output and return, don't wait
            //
            proc.consumeProcessOutput( sout, serr)
        }
        else
        {
            //  foreground: wait for process to finish
            //
            proc.waitForProcessOutput( sout, serr)
        }

        //	Check exit from command
        //
        def exitval = proc.exitValue()

        return [ stdout: sout, stderr: serr, exit: exitval ]
    }

    /**
     * Run a command setup for this Class
     *
     * @param   bg      True if command is to be run in background
     * @return          STDOUT result of command
     */
    String run( boolean bg = false )
    {
        //  Continuation line text
        //
        def cont = "\n..." + " "*10

        log.debug( "Running command: ${listcmd}" )

        //  Run command
        //
        Map m = runMap( bg )

        if ( m.exitval )
        {
            //AES: runMap returns m.exit not m.exitval so this is always fale but i'm leaving this in as not to affect
            //existing code (i think returning null on failure is a poor idea, far better to return stderr+out and exitval
            //regardless, most of the time i'd want my stderr if my exitval is 1). unless KDD you want to fix this?
            log.error( "Command failure: "	+ m.stderr.tokenize("\n").join(cont))
            log.debug( "Command output: "	+ m.stdout.tokenize("\n").join(cont))
            return null
        }

        if( m.stderr ) log.warn(  "Command error output: "	+ m.stderr.tokenize("\n").join(cont))
        if( m.stdout ) log.debug( "Command output: "		+ m.stdout.tokenize("\n").join(cont))
        return m.stdout
    }
}
