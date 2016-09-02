/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

//
//	LoadClarity.groovy
//
//	Load Clarity Database from detente tables in PathOS
//
//	Usage:
//
//	01	kdoig	10-May-2013
//

package org.petermac.clarity

import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.util.DbConnect

@Log4j
class LoadClarity
{
	static void main( args )
	{
		//
		//	Collect and parse command line args
		//
		def cli = new CliBuilder(   usage: "LoadClarity [options]",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nLoad LIMS Database from detente samples in PathOS\n')

		//	Options to Submit
        //
        cli.with
        {
            h( longOpt: 'help',		    'this help message' )
            o( longOpt: 'orm', 	   args: 1, required: true, "ORM  Environment to use (prod|test)" )
            l( longOpt: 'lims',    args: 1, required: true, "LIMS Environment to use (prod|test)" )
            p( longOpt: 'project', args: 1, "LIMS project name [Molpath]" )
            d( longOpt: 'debug',    'Turn on debug logging')
        }
		
		def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  LIMS to use
        //
        def lims = opt.lims ?: "test"
        log.info( "Start Clarity data Load for ${lims} " + args )

        //  ORM DB to use
        //
        def orm = opt.orm

        //  ORM DB to use Todo: deprecated
        //
        def project = opt.project ?: 'Molpath'

        //  Perform data load
        //
		def n = dataload( orm, lims )

        log.info( "Done: loaded ${n} samples from ${orm} into LIMS ${lims}" )
    }

    /**
     * Data loader for Clarity
     *
     * @param orm   Database source of sample data
     * @param lims  Clarity LIMS instance to load
     * @return      No of samples loaded
     */
    static int dataload( String orm, String lims )
	{
        //  Connect to db
        //
        Clarity   cl  = new Clarity( lims )

        //  Extract samples from database
        //
        List<Map> samples  = getDbSamples( orm )
        log.info( "Found samples ${samples.size()}")

        int  nsamples = 0

        for ( sample in samples )
        {
            ++nsamples

            def cs = new ClaritySample( clarity:cl, sample: sample.sample )
            log.info( "Adding sample (${nsamples}) ${sample.sample} into project ${sample.project}")

            //  Add sample to Clarity
            //  Creates project and container if necessary
            //
            cs.add( sample.project, "ImportContainer", sample )
        }

        return nsamples
    }

    /**
     * Extract samples to export to LIMS from DB
     *
     * @param orm   Database to extract records from
     * @return      List of sample Maps
     */
    static List getDbSamples( String orm )
    {
        DbConnect db  = new DbConnect( "mp_web" + orm )
        Sql       sql = db.sql()

        //  SQL to extract all detente samples Todo: this should use the ORM data, not the RDB
        //
        def getsql = '''
            select  distinct
                    md.sample,
                    mt.project,
                    date_format(str_to_date(md.request_date,'%d/%m/%Y'),'%y%m') as project_suffix,
                    md.test_set,
                    md.test_desc,
                    md.patient,
                    date_format(str_to_date(md.dob,'%d/%m/%Y'),'%Y-%m-%d') as dob,
                    md.urn,
                    md.sex,
                    date_format(str_to_date(md.request_date,'%d/%m/%Y'),'%Y-%m-%d') as request_date,
                    date_format(str_to_date(md.collect_date,'%d/%m/%Y'),'%Y-%m-%d') as collect_date,
                    date_format(str_to_date(md.rcvd_date,'%d/%m/%Y'),'%Y-%m-%d')    as rcvd_date,
                    date_format(str_to_date(md.auth_date,'%d/%m/%Y'),'%Y-%m-%d')    as auth_date,
                    md.location,
                    md.requester
            from    mp_detente as md,
                    mp_detente_tests as mt
            where   md.test_set = mt.testset
            and     date_format(str_to_date(md.request_date,'%d/%m/%Y'),'%y%m') not regexp '^0'
            and     md.dob != '00/00/0000'
            and     date_format(str_to_date(md.request_date,'%d/%m/%Y'),'%Y-%m-%d') >= '2013-07-01'
        '''

        log.debug( getsql )

        //  Extract samples from database
        //
        List samples = []

        sql.eachRow( getsql )
        {
            row ->

                //  Test if a new sample
                //
                int idx = samples.findIndexOf { it.sample == row.sample }
                if ( idx == -1 )
                {
                    //  Sample project: add date suffix if needed
                    //
                    String project = row.project
                    if ( project.endsWith('MMYY')) project = project.replace( 'MMYY', row.project_suffix as String)

                    //  New sample
                    //
                    Map sample = [:]
                    sample <<   [
                                    project:        project,
                                    sample:         row.sample,
                                    test_sets:      row.test_set,
                                    test_descs:     row.test_desc,
                                    patient:        row.patient,
                                    dob:            row.dob,
                                    urn:            row.urn,
                                    sex:            row.sex,
                                    request_date:   row.request_date,
                                    collect_date:   row.collect_date,
                                    rcvd_date:      row.rcvd_date,
                                    auth_date:      row.auth_date,
                                    pathlab:        row.location,
                                    requester:      row.requester
                                ]

                    samples << sample
                }
                else
                {
                    //  Update existing sample with additional test_sets
                    //
                    samples[idx].test_sets  += " " + row.test_set
                    samples[idx].test_descs += " " + row.test_desc
                }
        }

        return samples
    }
}

