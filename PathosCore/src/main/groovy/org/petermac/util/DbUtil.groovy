/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Utility class for database operations
 *
 * User: Ken Doig
 * Date: 28-May-2015
 */

@Log4j
class DbUtil
{
    static def sql = null

    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'DbUtil [options]',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nDelete seqrun/sample/variant data records\n')

        cli.with
                {
                    h(longOpt: 'help',      required: false, 'Usage Information' )
                    q(longOpt: 'seqrun',    args: 1,  required: false, 'Seqrun to operate on' )
                    s(longOpt: 'sample',    args: 1,  'Sample to operate on' )
                    r(longOpt: 'rdb',       args: 1,  required: true,  'RDB to use (eg pa_local,pa_prod)' )
                    t(longOpt: 'type',      args: 1,  required: false, 'operation eg DELETEVARIANT,DELETESAMPLE,DELETESEQRUN' )
                    l(longOpt: 'lock',      args: 1,  required: false, 'operation eg set,clear,status' )
                    d( longOpt: 'debug',    'Turn on debug logging')
                }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h || opt.arguments().size() != 0)
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  Open DB
        //
        if ( opt.rdb )
        {
            def db = new DbConnect( opt.rdb )
            sql = db.sql()
        }
        else
        {
            log.fatal( "Missing RDB option")
            return
        }

        //  Run the program
        //
        log.info("DbUtil " + args )

        int nrows = 0

        if ( opt.type && ! opt.seqrun )
        {
            log.fatal( "Missing Seqrun option")
            return
        }

        //  Delete all Variants for a Seqrun and Sample
        //
        if ( opt.type == 'DELETEVARIANT')
        {
            if ( ! opt.sample )
            {
                log.fatal( "Missing Sample option")
                return
            }
            nrows = deleteSampleVariant( opt.seqrun, opt.sample )
        }

        //  Delete an entire Sample and Variants
        //
        if ( opt.type == 'DELETESAMPLE')
        {
            if ( ! opt.sample )
            {
                log.fatal( "Missing Sample option")
                return
            }
            nrows += deleteSampleVariant( opt.seqrun, opt.sample )
            nrows += deleteSample( opt.seqrun, opt.sample )
        }

        //  Delete an entire Seqrun, Samples and Variants
        //
        if ( opt.type == 'DELETESEQRUN')
        {
            List<String> samples = getSamples( opt.seqrun )
            for ( sample in samples )
            {
                nrows += deleteSampleVariant( opt.seqrun, sample )
                nrows += deleteSample( opt.seqrun, sample )
            }
            nrows += deleteSeqrun( opt.seqrun )
        }

        if ( opt.lock )
        {
            nrows = 1
            def dbl = new DbLock( opt.rdb, 5 )

            //  Show lock status
            //
            if ( opt.lock == 'status' )
                log.info( "DB Lock status ${dbl.hasLock()}")

            //  Set lock to current time
            //
            if ( opt.lock == 'set' )
                log.info( "DB Lock set    ${dbl.setLock()}")

            //  Create dblock table
            //
            if ( opt.lock == 'create' )
                log.info( "DB Lock created ${dbl.createLock()}")

            //  Clear lock
            //
            if ( opt.lock == 'clear' )
                log.info( "DB Lock clear  ${dbl.clearLock()}")

        }

        log.info("Done, processed ${nrows} rows")
    }

    /**
     * Get a list of samples for a run
     *
     * @param   seqrun  Run to query
     * @return          List of samples for run
     */
    static List<String> getSamples( String seqrun )
    {
        List sam = []

        //  Setup get samples command
        //
        def sel = 	"""
                    select	sa.sample_name as sample
                    from	seq_sample  as sa,
                            seqrun      as sr
                    where	sr.seqrun = ${seqrun}
                    and		sr.id = sa.seqrun_id
                    """

        sql.eachRow( sel )
        {
            sam << it['sample']
        }

        log.info( "Found samples ${sam}")

        return sam
    }

    /**
     * Delete variants for a sample
     *
     * @param seqrun    Seqrun to use
     * @param sample    Sample to use
     * @return
     */
    static Integer deleteSampleVariant( String seqrun, String sample )
    {
        Integer nrows = 0

        log.info "Processing ${seqrun} ${sample}"

        //  Setup delete cnv command
        //
        def del = 	"""
                    delete	sv.*
                    from	seq_cnv as sv,
                            seq_sample  as sa,
                            seqrun      as sr
                    where	sr.seqrun      = ${seqrun}
                    and		sa.sample_name = ${sample}
                    and		sr.id = sa.seqrun_id
                    and		sa.id = sv.seq_sample_id
                    """
        sql.execute( del )

        //  Delete tag links, else we get foreign key errors
        //
        def delTags = """
                      delete
                      from  seq_variant_tag
                      where seq_variant_tags_id IN
                        (SELECT sv.id from seq_variant as sv,
                            seq_sample  as sa,
                            seqrun      as sr
                    where sr.seqrun = ${seqrun}
                    and sa.sample_name = ${sample}
                    and sr.id = sa.seqrun_id
                    and sa.id = sv.seq_sample_id)
                       """
        sql.execute(delTags)

        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Deleted Seqvariant Tag linking table rows = ${nrows}"
                }


        //  Before deleting variants, clear Originating IDs
        //
        def orig =  """
                    update cur_variant
                    set originating_id=NULL
                    where originating_id IN
                    (SELECT sv.id FROM seq_variant as sv,
                            seq_sample  as sa,
                            seqrun      as sr
                            where	sr.seqrun = ${seqrun}
                            and		sa.sample_name = ${sample}
                            and		sr.id = sa.seqrun_id
                            and		sa.id = sv.seq_sample_id)
                    """
        sql.execute( orig )
        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Cleared CurVariant Originating fields = ${nrows}"
                }



        //  Setup delete variants command
        //
        del = 	    """
                    delete	sv.*
                    from	seq_variant as sv,
                            seq_sample  as sa,
                            seqrun      as sr
                    where	sr.seqrun = ${seqrun}
                    and		sa.sample_name = ${sample}
                    and		sr.id = sa.seqrun_id
                    and		sa.id = sv.seq_sample_id;
                    """
        sql.execute( del )

        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Deleted SeqVariant rows = ${nrows}"
                }

        return nrows
    }

    /**
     * Delete a sample
     *
     * @param seqrun    Seqrun to use
     * @param sample    Sample to use
     * @return
     */
    static Integer deleteSample( String seqrun, String sample )
    {
        Integer nrows = 0
       
        log.info "Processing ${seqrun} ${sample}"

        //  Setup delete sample report command
        //
        def del = 	"""
                    delete	ssr.*
                    from	seq_sample        as ss,
                            seq_sample_report as ssr,
                            seqrun            as sr
                    where	sr.seqrun = ${seqrun}
                    and		ss.sample_name = ${sample}
                    and		sr.id = ss.seqrun_id
                    and     ssr.seq_sample_id = ss.id
                    """
        sql.execute( del )

        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Deleted SeqSampleReport rows = ${nrows}"
                }

        //  Setup delete sample relation command
        //
        del = 	"""
                    delete	ssrel.*
                    from	seq_sample        as ss,
                            seq_sample_relations as ssrel,
                            seqrun            as sr
                    where	sr.seqrun = ${seqrun}
                    and		ss.sample_name = ${sample}
                    and		sr.id = ss.seqrun_id
                    and     ssrel.seq_sample_id = ss.id
                    """
        sql.execute( del )

        sql.eachRow( "select row_count()" )
                {
                    nrows += it['row_count()'] as Integer
                    log.info "Deleted SeqSampleRelation rows = ${nrows}"
                }


        //  Delete tag links, else we get foreign key errors
        //
        def delTags = """
                      delete
                      from  seq_sample_tag
                      where seq_sample_tags_id IN
                        (SELECT sa.id FROM seq_sample AS sa, seqrun AS sr where	sr.seqrun = ${seqrun}
                        and	sa.sample_name = ${sample}
                        and sr.id = sa.seqrun_id)
                       """
        sql.execute(delTags)

        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Deleted Seqsample Tag linking table rows = ${nrows}"
                }

        //  Delete alignstats records for this sample
        //
        def delStats = """
                        delete
                        from align_stats
                        where seqrun=${seqrun} and sample_name=${sample}
                        """
        sql.execute(delStats)

        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Deleted Seqsample Tag linking table rows = ${nrows}"
                }

        //  Setup delete sample command
        //
        del = 	    """
                    delete	sa.*
                    from	seq_sample  as sa,
                            seqrun      as sr
                    where	sr.seqrun = ${seqrun}
                    and		sa.sample_name = ${sample}
                    and		sr.id = sa.seqrun_id
                    """
        sql.execute( del )

        sql.eachRow( "select row_count()" )
                {
                    nrows += it['row_count()'] as Integer
                    log.info "Deleted SeqSample rows = ${nrows}"
                }

        return nrows
    }

    /**
     * Delete a seqrun
     *
     * @param seqrun    Seqrun to use
     * @return
     */
    static Integer deleteSeqrun( String seqrun )
    {
        Integer nrows = 0

        log.info "Processing ${seqrun}"

        //  First delete tag links, else we get foreign key errors
        //
        def delTags = """
                      delete
                      from  seqrun_tag
                      where seqrun_tags_id IN (SELECT id FROM seqrun WHERE seqrun=${seqrun})
                       """
        sql.execute(delTags)

        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Deleted Seqrun Tag linking table rows = ${nrows}"
                }

        //  Setup delete seqrun command
        //
        def del = 	"""
                    delete
                    from	seqrun
                    where	seqrun = ${seqrun}
                    """
        sql.execute( del )

        sql.eachRow( "select row_count()" )
                {
                    nrows = it['row_count()'] as Integer
                    log.info "Deleted Seqrun rows = ${nrows}"
                }

        return nrows
    }
}
