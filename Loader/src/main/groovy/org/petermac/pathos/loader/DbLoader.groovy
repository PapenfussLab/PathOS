/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */


package org.petermac.pathos.loader

import groovy.util.logging.Log4j
import org.hibernate.Session
import org.petermac.annotate.VarDataSource
import org.petermac.pathos.curate.*
import org.petermac.pathos.pipeline.MakePanel
import org.petermac.pathos.pipeline.SampleName
import org.petermac.util.DateUtil
import org.petermac.util.DbConnect
import java.text.MessageFormat
import java.text.SimpleDateFormat

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Stand alone (Grails-less) class for populating and merging PathOS GORM database
 * Needs Hibernate only
 *
 * User: Ken Doig
 * Date: 08/10/2013
 */

/**
 * Class for populating and refreshing GORM database from RDB tables
 *
 * Author: Ken Doig
 * Date:   13-Sep-2013
 */
@Log4j
class DbLoader
{
    def  sql

    //  Number of records added before a flush is called
    //
    static private final int MAXFLUSH = 2000      // Flush only works with MySQL on update not insert

    /**
     *  Merge latest sequencing data with domain tables
     */
    void mergeETL( String dbase )
    {
        log.info( "Starting Database Merge ${dbase}" )

        def cnt = 0
        def db  = new DbConnect( dbase )
        sql     = db.sql()

        cnt = addUsers()
        log.info( "Added ${cnt} Users")

        cnt = addPatients()
        log.info( "Added ${cnt} Patients")

        cnt = addSamples( true )
        log.info( "Added ${cnt} Patient Samples")

        cnt = addPatAssays( true )
        log.info( "Added ${cnt} Patient Sample Tests")

        cnt = addSeqrun( true )
        log.info( "Added ${cnt} Seqruns")

        cnt = addPanels( true )
        log.info( "Added ${cnt} Panels")

        cnt = addRois( true )
        log.info( "Added ${cnt} ROIs")

        cnt = addSeqSamples( true )
        log.info( "Added ${cnt} SeqSamples")

        int svcnt = LoadSeqVariant.addSeqVariants( sql, dbase )
        log.info( "Added ${svcnt} SeqVariants")

        cnt = addAudits( true )
        log.info( "Added ${cnt} Audit logs")

        cnt = addCnv( true )
        log.info( "Added ${cnt} CNVs")

        cnt = addAlignStats( true )
        log.info( "Added ${cnt} Alignment stats")

        if ( svcnt )
        {
            SeqVariant.withSession
            {
                Session session ->

                    //  Add curated Variant links
                    //
                    def vfs = new VarFilterService()

                    //  Set curated links within a txn
                    //
                    cnt = vfs.applyFilter( session, false )                         //  Filter added SeqVariants
                    log.info( "Set Filter for ${cnt} Variants")
            }
        }

        log.info( "Database Merge Completed.")
    }

    /**
     * Save a domain record and format any errors
     *
     * @param   rec domain record to save
     * @param   flush true if the record should be flushed
     * @return  True if save successful
     */
    static boolean saveRecord( Object rec, boolean flush )
    {
        boolean ok = true
        rec.withTransaction
        {
            status ->

                if ( ! rec.validate())
                {
                    rec?.errors?.allErrors?.each
                    {
                        ok = false
                        log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                    }
                }

                try
                {
                    rec.save( flush: flush )
                }
                catch( Exception ex )
                {
                    ok = false
                    println "Exception in saveRecord() " + ex
                    status.setRollbackOnly()
                }
        }

        return ok
    }

    /**
     * Add the migration user for initial database population
     *
     * @return  Count of rows added
     */
    static int addUsers()
    {
        log.info( 'Adding Users')

        //  Skip existing users
        //
        if ( AuthUser.findByUsername('migration')) return 0

        def mig = new AuthUser(username: "migration", password: "migration",displayName: "Migration",email:'ken.doig@petermac.org')
        saveRecord( mig, true )

        //  Create an Admin role for Migrate user
        //
        def thisRole = new AuthRole(authority: 'ROLE_ADMIN').save( flush: true, failOnError: true)

        //  Link migrate to role
        //
        AuthUserAuthRole.create( mig, thisRole, true )

        return 1
    }

    /**
     * Add all Patients from Detente
     *
     * @return  Count of rows added
     */
    int addPatients()
    {
        log.info( 'Adding Patients')

        int cnt    = 0              // number of rows added
        int rowcnt = 0              // row number

        //  External patients may not have a DOB
        //  allow '00/00/0000' for DOB but exclude patients with numerals in the name or the word 'control'
        //
        def qry = 	'''
                    select	distinct
                            md.patient,
                            md.sex,
                            md.urn,
                            md.dob
                    from  	mp_detente as md
                    where   (md.dob != '00/00/0000' or (md.dob = '00/00/0000' and patient not regexp '[0-9]' and patient not regexp 'control'))
                    and     md.dob is not null
                    '''

        def rows = sql.rows(qry)

        //  Dummy patient DOB
        //
        def sdf = new SimpleDateFormat("dd/MM/yyyy")

        for ( row in rows )
        {
            ++rowcnt

            //	Lookup Patient
            //
            def pat = Patient.findByUrn( row.urn )

            //  Skip existing patients
            //
            if ( pat ) continue

            //  Convert Detente dates
            //
            Date dob = DateUtil.dateParse( sdf, row.dob )
            if ( ! dob )
            {
                log.error( "Invalid date of birth [${row.dob}] for patient ${row.patient}")
                continue
            }

            //	Create patient as domain class
            //
            pat = new Patient(	fullName:   row.patient,
                                sex:        row.sex,
                                dob:        dob,
                                urn:        row.urn
                                )

            if ( saveRecord( pat, cnt % MAXFLUSH == 0)) ++cnt
        }

        return cnt
    }

    /**
     * Add the patient samples from detente
     *
     * @return  Count of rows added
     */
    int addSamples( boolean merge )
    {
        log.info( 'Adding Patient Samples')

        int cnt    = 0              // number of rows added
        int rowcnt = 0              // row number

        def qry = 	'''
                    select	distinct
                            md.sample,
                            ifnull(md.location,'none') as pathlab,
                            md.urn,
                            md.requester,
                            md.collect_date,
                            md.rcvd_date,
                            md.request_date,
                            tt.tumourtype,
                            tt.formalstage,
                            tt.tumourstage
                    from	mp_detente as md
                    left
                    join	mp_tumourtype as tt
                    on		md.sample = tt.sample
                    where	md.request_date != ''
                    and     (md.dob != '00/00/0000' or (md.dob = '00/00/0000' and patient not regexp '[0-9]' and patient not regexp 'control'))
                    and     md.dob is not null
                    '''

        def sams = sql.rows(qry)

        //	Find default owner
        //
        AuthUser user = AuthUser.findByUsername( "migration" )
        assert user.getUsername() == "migration"

        //  Date parsing format
        //
        def sdf = new SimpleDateFormat("dd/MM/yyyy")

        //	Loop through each row set and add to hibernate
        //
        for ( row in sams )
        {
            ++rowcnt

            //	Lookup Patient
            //
            def pat = Patient.findByUrn( row.urn )
            if ( ! pat )
            {
                log.warn( "Row: ${rowcnt} Couldn't find Patient [${row.urn}] for PatSample [${row.sample}]")
                continue
            }

            //  Skip existing sample
            //
            def sam = PatSample.findBySample( row.sample )
            if ( merge && sam ) continue

            def collect_date = null
            def rcvd_date    = null
            def request_date = null
            if ( row.collect_date ) collect_date = DateUtil.dateParse( sdf, row.collect_date )
            if ( row.rcvd_date    ) rcvd_date    = DateUtil.dateParse( sdf, row.rcvd_date )
            if ( row.request_date ) request_date = DateUtil.dateParse( sdf, row.request_date )

            //	Create sample as domain class
            //
            sam = new PatSample(sample:         row.sample,
                                patient:        pat,
                                owner:	        user,
                                collectDate:    collect_date,
                                rcvdDate:       rcvd_date,
                                requestDate:    request_date,
                                requester:      row.requester,
                                pathlab:        row.pathlab,
                                tumourType:     row.tumourtype,
                                stage:          row.tumourstage,
                                formalStage:    row.formalstage
            )

            if( saveRecord( sam, cnt % MAXFLUSH == 0)) ++cnt
        }

        return cnt
    }


    /**
     * Add the patient samples tests from detente
     *
     * @return  Count of rows added
     */
    int addPatAssays(boolean merge )
    {
        log.info( 'Adding Patient Sample Tests')

        int cnt    = 0              // number of rows added
        int rowcnt = 0              // row number

        def qry = 	'''
                    select	distinct
                            md.sample,
                            md.auth_date,
                            md.test_set,
                            md.test_desc,
                            mt.genes
                    from	mp_detente as md
                    join	mp_detente_tests as mt
                    on   	md.test_set = mt.testset
                    where	md.request_date != ''
                    and     (md.dob != '00/00/0000' or (md.dob = '00/00/0000' and patient not regexp '[0-9]' and patient not regexp 'control'))
                    and     md.dob is not null
                    '''

        def sts = sql.rows(qry)

        //  Date parsing format
        //
        def sdf = new SimpleDateFormat("dd/MM/yyyy")

        //	Loop through each row set and add to hibernate
        //
        for ( row in sts )
        {
            ++rowcnt

            //	Lookup Patient Sample
            //
            def sam = PatSample.findBySample( row.sample )
            if ( ! sam )
            {
                log.warn( "Row: ${rowcnt} Couldn't find PatSample [${row.sample}] for Test [${row.test_set}]")
                continue
            }

            //  Skip existing sample test
            //
            def st = PatAssay.findByPatSampleAndTestSet( sam, row.test_set )
            if ( merge && st ) continue

            def auth_date = null
            if ( row.auth_date ) auth_date = DateUtil.dateParse( sdf, row.auth_date )

            //	Create sample as domain class
            //
            st  = new PatAssay(	patSample:         sam,
                                    authDate:       auth_date,
                                    testSet:        (row.test_set).trim(),
                                    testName:       (row.test_desc).trim(),
                                    genes:          row.genes
            )

            if( saveRecord( st, cnt % MAXFLUSH == 0)) ++cnt
        }

        return cnt
    }

    /**
     * Add the sequencing runs
     *
     * @return  Count of rows added
     */
    int addSeqrun( boolean merge )
    {
        log.info( 'Adding Sequencing Runs')

        int cnt    = 0              // number of rows added

        def qry = 	'''
                    select  distinct
                            seqrun,
                            platform,
                            sepe,
                            library,
                            experiment,
                            scanner,
                            readlen
                    from    mp_seqrun
                    '''

        def rows  = sql.rows(qry)

        for ( row in rows )
        {
            //  Check if it exists
            //
            if ( merge && Seqrun.findBySeqrun( row.seqrun )) continue

            //	Convert Seqrun date prefix (YYMMDD) to Date object
            //
            def sdf = new SimpleDateFormat("yyMMdd")
            String seqrun = row.seqrun
            Date runDate = new Date()
            if ( seqrun =~ /\d{6}/ ) runDate = DateUtil.dateParse( sdf, seqrun[0..5] )

            //	Create Seqrun as domain class
            //
            def sr = new Seqrun(	seqrun:		row.seqrun,
                                    platform:   row.platform,
                                    sepe:       row.sepe,
                                    library:    row.library,
                                    runDate:	runDate,
                                    experiment: row.experiment,
                                    scanner:    row.scanner,
                                    readlen:    row.readlen
                                )

            if ( saveRecord( sr, true)) ++cnt

            //  Report any new Seqruns added
            //
            if ( merge ) log.info( "Added Seqrun ${row.seqrun}")
        }

        return cnt
    }

    /**
     * Add the panels
     *
     * @return  Count of rows added
     */
    int addPanels( boolean merge )
    {
        log.info( 'Adding Panels')

        int cnt    = 0              // number of rows added

        def qry = 	'''
                    select  distinct
                            panel
                    from    mp_seqrun
                    '''

        def rows = sql.rows(qry)

        def fs = new VarFilterService()

        for ( row in rows )
        {
            //  Check if it exists
            //
            if ( merge && Panel.findByManifest( row.panel )) continue

            //  Map a panel into a filter group for filtering
            //
            Map fg = fs.getFilterGroup( row.panel )

            //  Create Amplicon file
            //
            if ( ! new MakePanel().runAmplicon( row.panel, 'Amplicon.tsv' ))
            {
                log.error( "Create Amplicon file failed: Amplicon.tsv")
            }

            //  Create BED file for IGV
            //
            if ( ! new MakePanel().runBed( row.panel, 'Amplicon.bed' ))
            {
                log.error( "Create Amplicon BED file failed: Amplicon.bed")
            }

            //	Create panel as domain class
            //
            def pnl = new Panel(    manifest:		fg.manifest,
                                    panelGroup:	    fg.filterGroup,
                                    description:    fg.description
                                )

            if ( saveRecord( pnl, true)) ++cnt

            //  Report any new Panels added
            //
            if ( merge ) log.info( "Added panel ${row.panel}")
        }

        return cnt
    }

    /**
     * Add the Regions of Interest (ROIs)
     *
     * @return  Count of rows added
     */
    int addRois( boolean merge )
    {
        log.info( 'Adding ROIs')

        int cnt    = 0              // number of rows added

        def qry = 	'''
                    select  distinct
                            panel,
                            gene,
                            exon,
                            chr,
                            startpos,
                            endpos,
                            name
                    from    mp_roi
                    '''

        def rows = sql.rows(qry)

        for ( row in rows )
        {
            //  Find Panel parent
            //
            def pnl = Panel.findByManifest( row.panel )
            if ( ! pnl )
            {
                log.error( "ROI [${row.name}] without matching Panel ${row.panel}")
                continue
            }

            //  Check if it exists
            //
            if ( merge && Roi.findByPanelAndName( pnl, row.name )) continue

            //  enforce ordering of position
            //
            def startpos =  row.startpos
            def endpos   =  row.endpos
            if ( startpos > endpos )
            {
                startpos =  row.endpos
                endpos   =  row.startpos
            }

            //	Create ROI as domain class
            //
            def roi = new Roi(    panel:		pnl,
                                  manifestName:	row.panel,
                                  name:         row.name,
                                  gene:         row.gene,
                                  exon:         row.exon,
                                  chr:          row.chr,
                                  startPos:     startpos,
                                  endPos:       endpos
                                )

            if ( saveRecord( roi, true)) ++cnt
        }

        return cnt
    }

    /**
     * Add the sequenced samples
     *
     * @return  Count of rows added
     */
    int addSeqSamples( boolean merge )
    {
        log.info( 'Adding Sequenced Samples')

        int  cnt    = 0              // number of rows added
        int  rowcnt = 0              // row number
        List<String>    srs = []     // List of Seqrun    for this load
        List<String>    pss = []     // List of PatSample for this load

        def qry = 	'''
                    select	distinct
                    		sr.seqrun,
                    		sr.sample,
                    		sr.panel,
                    		sr.analysis,
                            sr.username,
                            sr.useremail,
                            sr.laneno
                    from	mp_seqrun as sr
    		        '''

        def rows  = sql.rows(qry)

        for ( row in rows )
        {
            ++rowcnt
            //	Lookup seqrun
            //
            def seqrun = Seqrun.findBySeqrun( row.seqrun )
            if ( ! seqrun )
            {
                log.warn( "Row: ${rowcnt} Couldn't find seqrun [${row.seqrun}]")
                continue
            }

            //  Check if SeqSample exists
            //
            if ( merge && SeqSample.findBySampleNameAndSeqrun( row.sample, seqrun )) continue

            //	Lookup sample
            //
            def patSample = PatSample.findBySample( SampleName.normalise(row.sample))
            if ( ! patSample )
            {
                log.debug( "Row: ${rowcnt} Couldn't find sample [${row.sample}]")
            }

            //	Lookup panel
            //
            def panel = Panel.findByManifest( row.panel )
            if ( ! panel )
            {
                log.warn( "Row: ${rowcnt} Couldn't find panel [${row.panel}]" )
            }

            //  Save a list of patient samples and seqruns for adding duplicates and replicates
            //
            if ( patSample ) pss << patSample.sample
            srs << seqrun.seqrun

            //  Figure out sample type
            //  NTC: "NTC" prefix, Control: "CTRL", "CONTROL", "NA12878","NA19240" prefixes
            //
            String sampleType = null
            if (row.sample.startsWith("NTC")) {
                sampleType = "NTC"
            } else if (row.sample.startsWith("CTRL") || row.sample.startsWith("CONTROL") || row.sample.startsWith("NA12878") || row.sample.startsWith("NA19240")) {
                sampleType = "Control"
            }

            //	Create RunSample as domain class
            //
            def ss = new SeqSample(	seqrun:		seqrun,
                                    patSample:	patSample,
                                    panel:		panel,
                                    sampleName:	row.sample,
                                    analysis:	row.analysis,
                                    userName:	row.username,
                                    userEmail:	row.useremail,
                                    laneNo:	    row.laneno,
                                    sampleType: sampleType
                                    )

            if ( saveRecord( ss, cnt % MAXFLUSH == 0)) ++cnt
        }

        //  Add SeqSample duplicate relations for each Patient Sample
        //
        PatSample.withTransaction
        {
            status ->

                List<PatSample> patsamples = []     // List of PatSamples for this load

                for ( ps in pss.unique())
                    patsamples << PatSample.findBySample(ps)

                if ( patsamples )
                {
                    int c = RelationService.assignDuplicateRelation( patsamples , true )
                    log.info( "Assigned Duplicate to ${c} samples")
                }
        }

        //  Add SeqSample replicate relations for each Seqrun
        //
        Seqrun.withTransaction
        {
            status ->

                List<Seqrun> seqruns = []     // List of Seqrun    for this load

                for ( sr in srs.unique())
                    seqruns << Seqrun.findBySeqrun(sr)

                if ( seqruns )
                {
                    int c = RelationService.assignReplicateRelation( seqruns, true )
                    log.info("Assigned Replicate to ${c} samples")
                }
        }

        return cnt
    }


    /**
     * Add the Copy Number Variants
     *
     * @return  Count of rows added
     */
    int addCnv( boolean merge )
    {
        log.info( 'Adding CNVs')

        int cnt    = 0              // number of rows added
        int rowcnt = 0              // row number

        def qry = 	'''
                    select	*
                    from    mp_cnv
    		        '''

        def rows  = sql.rows(qry)

        for ( row in rows )
        {
            ++rowcnt

            //	Lookup seqrun
            //
            def seqrun = Seqrun.findBySeqrun( row.seqrun )
            if ( ! seqrun )
            {
                log.warn( "Row: ${rowcnt} Couldn't find seqrun [${row.seqrun}]")
                continue
            }

            //  Check if SeqSample exists
            //
            row.seqSample = SeqSample.findBySampleNameAndSeqrun( row.sample, seqrun )
            if ( ! row.seqSample )
            {
                log.warn( "Row: ${rowcnt} Couldn't find seqSample [${row.seqrun}:${row.sample}]")
                continue
            }

            //  Check if SeqCnv exists
            //
            def svs = SeqCnv.withCriteria
                        {
                            eq( 'seqSample', row.seqSample )
                            eq( 'chr',       row.chr )
                            eq( 'startpos',  row.startpos )
                            eq( 'endpos',    row.endpos )
                        }

            if ( merge && svs ) continue

            //  Remove extra parameters
            //
            row.remove( 'seqrun' )
            row.remove( 'sample' )

            //	Create CNV as domain class
            //
            def sc = new SeqCnv( row as Map )

            if ( saveRecord( sc, cnt % MAXFLUSH == 0)) ++cnt
        }

        Seqrun.findAll()
        return cnt
    }

    /**
     * Return first COSMnnnn Cosmic ID found in vars
     *
     * @param vars
     * @return
     */
    private static String getCosmic( Object vars )
    {
        if ( ! vars ) return ''

        def m = ( vars as String =~ /COSM(\d+)/ )
        if ( m.count ) return m[0][1]
        return ''
    }

    /**
     * Return first rsnnnn dbSNP ID found in vars
     *
     * @param vars
     * @return
     */
    private static String getDbsnp( Object vars )
    {
        if ( ! vars ) return ''

        def m = ( vars as String =~ /rs(\d+)/ )
        if ( m.count ) return m[0][1]
        return ''
    }

    /**
     * Add the audit logs
     *
     * @return  Count of rows added
     */
    int addAudits( boolean merge )
    {
        log.info( 'Adding Audit Logs')

        int cnt    = 0              // number of rows added

        def qry = 	'''
                    select  category,
                            seqrun,
                            variant,
                            sample,
                            task,
                            complete,
                            elapsed,
                            software,
                            version as swVersion,
                            username,
                            description
                    from    mp_audit
                    '''

        def rows  = sql.rows(qry)

        for ( row in rows )
        {
            //  Check date
            //
            if ( ! row.complete )
            {
                log.debug( "Invalid complete date for ${row.seqrun}:${row.sample} []" )
                continue
            }

            //  Parse data
            //
            def sdf      = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            def complete = DateUtil.dateParse( sdf, row.complete )

            //  Check if it exists
            //
            if ( merge && Audit.findByTaskAndComplete( row.task, complete )) continue

            //	Create Audit as domain class
            //
            row.complete = complete             // override DB version with formatted date
            def aud = new Audit( row as Map )

            if ( saveRecord( aud, true)) ++cnt
        }

        return cnt
    }

    /**
     * Add the statistics generated from amplicon alignment
     *
     * @return  Count of rows added
     */
    int addAlignStats( boolean merge )
    {
        log.info( 'Adding Alignment Stats')

        int cnt    = 0              // number of rows added
        int rowcnt = 0              // number of rows read

        //  Count the total number of records to retrieve
        //
        def qry  = 'select count(*) as norows from mp_alignstats'
        def rows = sql.rows(qry)
        int recs = rows[0].norows as int
        int page = 0
        int psiz = 100000
        log.info( "AlignStats records : ${recs}")

        //  Chunk through the records in chunks of ${psiz} records
        //  Needed to avoid memory exhaustion
        //
        while ( page < recs )
        {
            qry =   """
                    select    seqrun,
                              sample_name as sampleName,
                              panel_name  as panelName,
                              amplicon,
                              location,
                              readsout,
                              totreads,
                              unmapped,
                              goodamp,
                              sample_stats as sampleStats
                    from      mp_alignstats
                    limit     ${page},${psiz}
                    """

            page += psiz
            log.info( "AlignStats retrieved: ${page}")

            rows = sql.rows(qry)

            //  Loop through the records retrieved and save in ORM
            //
            for ( row in rows )
            {
                ++rowcnt

                //  Ignore record if already exists
                //
                if ( merge )
                {
                    //  Search for seqrun/sample/amplicon
                    //
                    def amps = AlignStats.withCriteria
                    {
                        eq( 'seqrun',       row.seqrun     )
                        eq( 'sampleName',   row.sampleName )
                        eq( 'amplicon',     row.amplicon   )
                    }

                    //  Skip if already exists
                    //
                    if ( amps.size()) continue
                }

                //	Create Stats as domain class
                //
                def alns = new AlignStats( row as Map )

                if ( saveRecord( alns, true)) ++cnt
            }
        }
        return cnt
    }
}
