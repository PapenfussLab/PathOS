/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j

import java.text.SimpleDateFormat

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Log audit records to file
 *
 * User: doig ken
 * Date: 04/10/2013
 * Time: 8:50 AM
 */
@Log4j
class AuditLog
{
    static final def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")   // 24 hour date - ascii sortable

    /**
     * Log and audit message to file
     * 
     * @param out       File to log output eg <sample>.audit
     * @param params    Map of parameters    *  category            // pipeline, lims(clarity), record(detente), curation, database
     *                                          seqrun              // key:
     *                                          variant             // key:
     *                                          sample              // key:
     *                                       *  task                // sequence, align, variant, annotate, register, report, dbload, dbmerge
     *                                       *  complete            // task complete date
     *                                          elapsed             // elapsed time in minutes
     *                                          software            // software package used VarScan, PMAligner, BWA, Clarity, Detente, PathOS
     *                                          version             // software version
     *                                       *  username            // free form text field (conforms to software user formats)
     *                                          description         // free form details eg command args
     */
    static void logAudit( File out, String category, String task, Map params)
    {
        if ( ! category || ! task )
        {
            log.warn( "Missing audit parameters, must have category,task")
            return
        }

        if ( ! (category in [ 'pipeline', 'lims', 'record', 'curation', 'database' ]))
        {
            log.warn( "Invalid audit category [${category}]")
            return
        }

        List flds = []
        flds.add(category)
        flds.add(params.seqrun)
        flds.add(params.variant)
        flds.add(params.sample)
        flds.add(task)
        flds.add(params.complete ? sdf.format(params.complete) : sdf.format(new Date()))
        flds.add(params.elapsed)
        flds.add(params.software)
        flds.add(params.version)
        flds.add(params.username ?: System.getProperty('user.name').trim())
        flds.add(params.description)

        out << flds.join('\t') + '\n'
    }
}
