/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

//
//	DBMigrate.groovy
//
//	Migrate Path-OS Database from a previously created RDB in GORM structure
//
//	Usage:
//
//	01	ken doig	07-Mar-2014
//	02	ken doig	30-Apr-2016     Added SeqSample type field update and SeqRelation migration
//	03	ken doig	09-Jun-2016     Added TumourNormal SeqRelation migration
//

package org.petermac.pathos.loader

import groovy.time.TimeCategory

import groovy.time.*
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.hibernate.Session
import org.petermac.pathos.curate.*
import org.petermac.util.DbConnect
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.HollyUtil
import org.petermac.util.Tsv
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.petermac.util.RunCommand

import java.nio.file.Files
import java.nio.file.Path

@Log4j
class DBMigrate
{
    //  RDB connection object
    //
    def sql
    def dbl = new DbLoader()
    HGVS hg = null

    static void main( args ) 
    {
        //
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "DBMigrate [options]",
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nMigrate Path-OS Database from RDB\n')

        //	Options to LoadPathOS
        //
        cli.with
        {
            h( longOpt:     'help',		            'this help message' )
            r( longOpt:     'rdb', 	                args: 1, 'RDB Schema to migrate from eg pa_prod' )
            o( longOpt:     'orm', 	                args: 1, required: true, 'ORM Schema to migrate into eg pa_local' )
            t( longOpt:     'table', 	            args: 1, 'Tables to migrate [Seqrun,SeqSample,SeqVariant,SeqRelation]' )
            st( longOpt:     'sampletypes', 	    'Set sample types')
            tn(longOpt:     'tumournormal', 	    args: 1, required: false, 'Tumour Normal file to load' )
            f( longOpt:     'filter', 	            'Filter a seqrun' )
            hs( longOpt:    'samplesholly', 	    'Load Holly information for all samples ' )
            af( longOpt:    'applyfilters', 	    'Set filter flags for all seqvariants explicitly (apply-filters force)' )
            afnf( longOpt:  'applyfiltersnoforce',  'Set filter flags for all seqvariants where filter flags are null (apply-filters no-force)' )
            afoor( longOpt: 'applyoorfilters', 	    'Set OOR filter flags for all seqvariants explicitly' )
            d( longOpt:     'debug',                'Turn on debug logging')
            p(longOpt: 'panelfreqs',             '(re)calculate panel frequencies for all seqvariants in given panel')
            vl( longOpt:     'varlinks',                'v1.3 make varlinks for all cur variants')
        }

        def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            println "--"
            println System.getProperty('java.class.path')
            return
        }

        log.info("Running DBMigrate: " + args )

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)



        //  Set RDB to load from
        //
        def rdb
        def db
        if (opt.rdb) {
            rdb = opt.rdb
            db = new DbConnect(rdb)
            if (!db.valid()) {
                log.fatal("Unknown RDB schema [${rdb}]")
                return
            }
        }

        //  Set ORM to load into
        //
        def orm = opt.orm
        db = new DbConnect(orm)
        if ( ! db.valid(true))
        {
            log.fatal( "Unknown ORM schema [${orm}]")
            return
        }

        //  Tumour Normal file processing
        //
        if ( opt.tumournormal )
        {
            File tn = new File( opt.tumournormal as String )
            if ( ! tn.exists() )
            {
                log.fatal( "Tumour Normal file doesn't exists ${tn}")
                System.exit(1)
            }

            Tsv tndata = new Tsv( tn )
            int n = tndata.load( true )
            if ( n < 1 )
            {
                log.fatal( "Tumour Normal file doesn't contain data ${tn} lines=${n}")
                System.exit(1)
            }

            //  Load data into database
            //
            n = loadTumourNormal( tndata, orm )
            if ( n < 1 )
            {
                log.fatal( "Tumour Normal file didn't load data ${tn} lines=${n}")
                System.exit(1)
            }

            log.info( "Loaded Tumour Normal file ${tn} lines=${n}")
            System.exit(0)
        }

        //  Filter a given seqrun
        //
        String tables = opt.table ?: ''
        if ( opt.filter )
        {
            tables = 'Filter'
        }

        if ( opt.applyfilters )
        {
            tables = 'ApplyFilts'
        }

        if ( opt.applyfiltersnoforce )
        {
            tables = 'ApplyNullFilts'
        }

        if ( opt.applyoorfilters )
        {
            tables = 'ApplyOorFilts'
        }

        if ( opt.samplesholly )
        {
            tables = 'SamplesHolly'
        }

        if ( opt.sampletypes )
        {
            tables = 'SetSampleTypes'
        }

        if ( opt.panelfreqs )
        {
            tables = 'CalculatePanelFrequencies'
        }

        if  ( opt.varlinks )
        {
            tables = 'ClinContextGrpVariant'
        }
        //  Perform data load
        //
        new DBMigrate().migrate( rdb, orm, tables )

        log.info( "Done: DB Migrated" )
    }

    /**
     * Main DB migrator
     *
     * @param rdb       RDB schema from which to load RDB tables
     * @param orm       ORM schema into which to load/merge ORM tables
     * @param tables    Table list to migrate
     */
    void migrate( String rdb, String orm, String tablestr )
    {
        List tables = tablestr.split(',')

        log.info( "Performing migrate from RDB ${rdb} to ORM ${orm} on Tables ${tables}")

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //


        def db  = new DbConnect( orm )
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        //  Connect to RDB
        //
        db  = new DbConnect( rdb )
        sql = db.sql()
        hg  = new HGVS(rdb)

        //  User migration
        //
        if ( tables.contains('User'))
            migrateUsers( rdb )

        //  Audit migration
        //
        if ( tables.contains('Audit'))
            migrateAudits( rdb )

        //  Seqrun migration
        //
        if ( tables.contains('Seqrun'))
            migrateSeqruns( rdb )

        //  SeqSample migration
        //
        if ( tables.contains('SeqSample'))
            migrateSeqSamples( rdb )

        //  SeqVariant migration
        //
        if ( tables.contains('SeqVariant'))
            migrateSeqVariants( rdb )

        //  SeqVariant filtering
        //
        if ( tables.contains('Filter'))
            filter( orm )

        //  SeqVariant filtering
        //
        if ( tables.contains('ApplyFilts'))
            applyFilters( orm, true  )

        //  SeqVariant filtering
        //
        if ( tables.contains('ApplyNullFilts'))
            applyFilters( orm, false  )

        //  SeqVariant filtering
        //
        if ( tables.contains('ApplyOorFilts'))
            applyOorFilters( orm  )

        //  Load Patient pathology data from Holly
        //
        if ( tables.contains('SamplesHolly'))
            loadAllHollyPatSamples( orm  )  //change to TRUE to re-load samples, not just (try to) load where no holly data exists yet

        //  Set SeqSample relationships
        //
        if ( tables.contains('SeqRelation'))
            setSampleRelations( orm  )


        //  Variant copying
        //
        if ( tables.contains('CurVariant'))
            copyCurVariants( rdb )

        if ( tables.contains('ClinContextGrpVariant'))
            migrateToClinContextModel( orm  )


        //  SeqVariant filtering
        //
        if ( tables.contains('SetSampleTypes'))
            setSampleTypes( orm  )


        if ( tables.contains('CalculatePanelFrequencies'))
            calcPanelFrequencies( orm  )


        log.info( "Finished migrate")
    }


    /**
     * Migrate all Users
     *
     * @return  Count of modified records
     */
    int migrateUsers( String rdb )
    {
     //   println "DDD"
        def rowcnt = 0
        def modcnt = 0

        log.info( "User migration")

        def qry =   """
                    select  id,username,
                            display_name as displayName,
                            email,
                            password
                    from    auth_user
                    """

        def rows = sql.rows( qry.toString())

        AuthUser.withTransaction
                {
                    for ( row in rows )
                    {
                        ++rowcnt
                        String msg = "Row: ${rowcnt} Processing AuthUser [${row.displayName}]"
                        log.info( msg )

                        //  Deal with AuthUser details: either create new, or update if exists
                        //
                        def thisUser = AuthUser.findByUsername( row.username )
                        if  ( ! thisUser )
                        {
                            //  Add a new User

                            def newUsername = row.username
                            def newEmail = row.email
                            def newDisplayname = row.displayName
                            def newPass = row.password

                            AuthUser thisNewUser = new AuthUser(username: newUsername, password: newPass, displayName: newDisplayname,email:newEmail).save(flush: true,failOnError:true)

                            def thisNewUserId = thisNewUser.getId()
                            thisUser = thisNewUser
                        }
                        else
                        {
                            //  Existing User
                            //  TODO from 0.995 to 0.996 we need to be updating AuthUsers and inserting AuthUserAuthRoles and AuthRoles as well
                            //  but for now we don't have existing AuthUsers so let's not fuss about it
                            def newUsername = row.username
                            def newEmail = row.email
                            def newDisplayname = row.displayName
                            def newPass = row.password
                            thisUser.setUsername(newUsername)
                            thisUser.setEmail(row.email)
                            thisUser.setDisplayName(newDisplayname)
                            thisUser.setPassword(newPass)
                        }

                        //Deal with roles
                        def roleqry =   """
                                        select  auth_role.authority
                                        from    auth_role
                                        inner
                                        join    auth_user_auth_role as ur
                                        on      ur.auth_role_id=auth_role.id
                                        where   ur.auth_user_id='${row.id}'
                                        """

                        def rolerows = sql.rows( roleqry.toString())

                        for (role in rolerows) {

                            def authority = role.authority
                            def thisUsersRole = AuthRole.findByAuthority(authority) as AuthRole //get the AuthRole from our DB if exists
                            if ( ! thisUsersRole ) { //make a new role if we found no matching role in our DB
                                thisUsersRole = new AuthRole(authority: authority).save(flush: true,failOnError:true) //.save(flush: true,failOnError:false)
                                dbl.saveRecord( thisUsersRole , true )
                            }

                            //make user-role link if need be
                            def existingUr = AuthUserAuthRole.findByAuthUserAndAuthRole(thisUser,thisUsersRole)
                            if (!existingUr) {
                                AuthUserAuthRole.create(thisUser, thisUsersRole, true) //could be here? check if exists?
                            }

                            //deletr any roles than are now unset
                            def theseUrs =  AuthUserAuthRole.findAllByAuthUser(thisUser)
                            for (thisUr in theseUrs) {
                                if (!(thisUr.authRole.authority  in rolerows.authority)) {
                                    thisUr.delete()
                                }
                            }
                        }
                        ++modcnt
                    }

                    log.info( "Updated ${modcnt} Users")
                    return modcnt
                }
    }


    /**
     * Migrate pipeline Audit records
     *
     * @return  Count of modified records
     */
    int migrateAudits( String rdb )
    {
        def rowcnt = 0
        def modcnt = 0

        log.info( "Audit migration")

        def qry =   """
                    select  category,
                            seqrun,
                            variant,
                            sample,
                            task,
                            complete,
                            elapsed,
                            software,
                            sw_version as swVersion,
                            username,
                            description
                    from	audit
                    where   category != 'pipeline'
                    """

        def rows = sql.rows( qry.toString())

        Audit.withTransaction
        {
            for ( row in rows )
            {
                if ( ++rowcnt % 100 == 0 )
                {
                    String msg = "Row: ${rowcnt} Processing Audit [${row.description}]"
                    log.info( msg )
                }

                //  Add a new Audit
                //
                def aud = new Audit( row as Map )

                if ( dbl.saveRecord( aud, true)) ++modcnt
            }

            log.info( "Updated ${modcnt} Audits")
            return modcnt
        }
    }


    /**
     * Migrate all modifiable data from Seqrun objects
     *
     * @return  Count of modified records
     */
    int migrateSeqruns( String rdb )
    {
        def rowcnt = 0
        def modcnt = 0

        log.info( "Seqrun migration")

        def qry =   """
                    select	sr.seqrun,
                    		us.username,
                    		sr.authorised_flag,   -- always 1
                    		sr.passfail_flag,
                    		sr.qc_comment
                    from	seqrun as sr,
                    		auth_user as us
                    where	sr.authorised_flag = 1
                    and		sr.authorised_id = us.id
                    """

        def rows = sql.rows( qry.toString())

        Seqrun.withTransaction
        {
            for ( row in rows )
            {
                ++rowcnt

                String msg = "Row: ${rowcnt} Processing Seqrun [${row.seqrun}]"
                log.info( msg )

                //  Check if Seqrun exists
                //
                def sr = Seqrun.findBySeqrun( row.seqrun )
                if ( ! sr )
                {

                    log.warn( "Couldn't update Seqrun [${row.seqrun}]")
                    continue
                }

                if ( sr.authorisedFlag == row.authorised_flag )
                {
                    log.warn( "Record already updated Seqrun [${row.seqrun}]")
                    continue
                }

                def us = AuthUser.findByUsername( row.username )
                if ( ! us )
                {
                    log.warn( "Couldnt find authorised user Seqrun [${row.seqrun}:${row.username}]")
                    continue
                }

                //  Set QC attributes
                //
                sr.authorisedFlag = row.authorised_flag
                sr.passfailFlag   = row.passfail_flag
                sr.qcComment      = row.qc_comment
                sr.authorised     = us
                ++modcnt
            }

            log.info( "Updated ${modcnt} Seqrun")
            return modcnt
        }
    }

    /**
     * Migrate all modifiable data from SeqSample objects
     *
     * @return  Count of modified records
     */
    int migrateSeqSamples( String rdb )
    {
        def rowcnt = 0
        def modcnt = 0

        log.info( "SeqSample migration")

        def qry =   """
                    select	sr.seqrun,
                            ss.sample_name as sample,
                            auth.username as authUser,
                            cur.username  as curUser,
                            qc.username   as qcUser,
                            ss.authorised_qc_flag,
                            ss.passfail_flag,
                            ss.qc_comment
                    from	seq_sample as ss
                    left
                    join    seqrun as sr
                    on      sr.id = ss.seqrun_id
                    left
                    join    auth_user as auth
                    on      auth.id = ss.authorised_by_id
                    left
                    join    auth_user as cur
                    on      cur.id = ss.curated_by_id
                    left
                    join    auth_user as qc
                    on      qc.id = ss.authorised_qc_id
                    where	(ss.authorised_by_id is not null or ss.curated_by_id is not null or ss.authorised_qc_id is not null)
                    """

        def rows = sql.rows( qry.toString())

        SeqSample.withTransaction
        {
            for ( row in rows )
            {
                if ( ++rowcnt % 100 == 0 )
                {
                    String msg = "Row: ${rowcnt} Processing SeqSample [${row.sample}]"
                    log.info( msg )
                }

                //	Lookup Seqrun
                //
                def seqr = Seqrun.findBySeqrun( row.seqrun )
                if ( ! seqr )
                {
                    log.warn( "Row: ${rowcnt} Couldn't find Seqrun [${row.seqrun}]")
                    continue
                }

                //  Check if SeqSample exists
                //
                def ss = SeqSample.findBySeqrunAndSampleName( seqr, row.sample )
                if ( ! ss )
                {
                    log.warn( "Couldn't find SeqSample [${row.sample}]")
                    continue
                }

                def authUser = row.authUser ? AuthUser.findByUsername( row.authUser ) : null
                def curUser  = row.curUser  ? AuthUser.findByUsername( row.curUser )  : null
                def qcUser   = row.qcUser   ? AuthUser.findByUsername( row.qcUser )   : null
                if ( ! authUser && ! curUser && ! qcUser )
                {
                    //log.warn( "Couldnt find any users in SeqSample [${row.sample}:${row.login}]")
                    log.warn( "Couldnt find any users in SeqSample [${row.sample}]")   //we get an exception if row.login doesn't exist
                    continue
                }

                //  Set QC attributes
                //
                ss.authorisedQcFlag = row.authorised_qc_flag
                ss.passfailFlag     = row.passfail_flag
                ss.qcComment        = row.qc_comment
                ss.finalReviewBy    = authUser
                ss.firstReviewBy    = curUser
                ss.authorisedQc     = qcUser
                ++modcnt
            }

            log.info( "Updated ${modcnt} SeqSample")

            return modcnt
        }
    }

    /**
     * Migrate all modifiable data from SeqVariants objects
     *
     * @return  Count of modified records
     */
    int migrateSeqVariants( rdb )
    {
        def rowcnt = 0
        def modcnt = 0

        //  Hard wired mapping from sequenced variant HGVSg to Mutalyzed variant
        //
        Map hgg =   [
                    'chr11:g.32417922_32417923insAG' : 'chr11:g.32417919_32417920dup',
                    'chr13:g.28608290_28608291insCCTCATTATCAACGTAGAAGT' : 'chr13:g.28608276_28608277insATCAACGTAGAAGTCCTCATT',
                    'chr1:g.115256492_115256493insCTCATGTATTGGTCTCTCATGGCGCCTGTC' : 'chr1:g.115256485_115256514dup',
                    'chr7:g.140453135_140453136insACTGTAGCTAGACCAAAATCACTC':'chr7:g.140453134_140453157dup',
                    'chr7:g.140453157_140453158insTCACTGTAGCTAGACCAAAATCAC':'chr7:g.140453134_140453157dup',
                    'chr7:g.55242466_55242477del':'chr7:g.55242467_55242478del',
                    'chr7:g.55249010_55249011insCCAGCGTGG':'chr7:g.55249002_55249010dup',
                    'chr7:g.55249013_55249014insGCGTGGACA':'chr7:g.55249005_55249013dup',
                    'chr7:g.55249021_55249022insCCCCAC':'chr7:g.55249016_55249021dup',
                    'chr7:g.55249025_55249026insACAACCCCCACGTGT':'chr7:g.55249011_55249025dup'
                    ]

        log.info( "SeqVariant migration")

        def qry =   """
                    select	sr.seqrun,
                    		ss.sample_name as sample,
                    		sv.hgvsg as variant,
                    		sv.reportable			-- always 1
                    from	seq_sample as ss,
                    		seqrun as sr,
                    		seq_variant as sv
                    where	sr.id = ss.seqrun_id
                    and		ss.id = sv.seq_sample_id
                    and		sv.reportable = 1
                    """

        def       rows = sql.rows( qry.toString())
        SeqSample runs = null               //  Last SeqSample processed

        SeqVariant.withTransaction
        {
            for ( row in rows )
            {
                ++rowcnt

                String msg = "Row: ${rowcnt} Processing SeqVariant [${row.seqrun}:${row.sample}:${row.variant}]"

                //  Skip lookups if the same run/sample
                //
                if ( runs?.sampleName != row.sample )
                {
                    //	Lookup Seqrun
                    //
                    def seqr = Seqrun.findBySeqrun( row.seqrun )
                    if ( ! seqr )
                    {
                        log.error( "Row: ${rowcnt} Couldn't find Seqrun [${row.seqrun}] Couldn't add SeqVariant [${row.variant}]")
                        continue
                    }

                    //	Lookup SeqSample
                    //
                    runs = SeqSample.findBySeqrunAndSampleName( seqr, row.sample )
                    if ( ! runs )
                    {
                        log.error( "Row: ${rowcnt} Couldn't find SeqSample [${row.seqrun}:${row.sample}] Couldn't add SeqVariant [${row.variant}]")
                        continue
                    }
                }

                //  Check if SeqVariant exists: look for specific SeqSample object and variant string
                //
                String var = row.variant
                if ( hgg[var] )
                {
                    log.warn( "Mapping ${var} to ${hgg[var]}")
                    var = hgg[var]
                }

                def sv = SeqVariant.findBySeqSampleAndVariant( runs, var )

                //  No variant found - skip
                //
                if ( ! sv )
                {
                    log.error( "Couldn't update SeqVariant [${var}]")
                    continue
                }

                /*if ( sv.reportable == row.reportable )
                {
                    log.error( "Record already updated SeqVariant [${var}]")
                    continue
                }*/

                //  Set reportable flag
                //
                sv.reportable = row.reportable
                ++modcnt
            }

            //run mass update of ROI flags
            //
            def fs = new VarFilterService()
            /*int roiUpdates = fs.updateRoiFlags()

            modcnt += roiUpdates*/

            log.info( "Updated ${modcnt} SeqVariants")

            return modcnt
        }
    }


    /**
     * Migrate all modified CurVariant objects
     *
     * @return  Count of modified records
     */
    int copyCurVariants( rdb )
    {
        def rowcnt = 0
        def modcnt = 0

        log.info( "Variant migration")

//        Map<String,String> genets = geneToTranscripts( rdb )
//        log.info( "Found ${genets.size()} genes with transcripts")

      def qry =   """
                    select  distinct
                            var.alamut_class as alamutClass,
                            var.authorised_flag as authorisedFlag,
                            var.chr,
                            var.pos,
                            var.consequence,
                            var.cosmic,
                            var.date_created as dateCreated,
                            var.dbsnp,
                            var.ens_variant,
                            var.exon,
                            var.gene,
                            var.gene_pathway,
                            var.gene_process,
                            var.gene_type,
                            var.hgvsc,
                            var.hgvsp,
                            sv.hgvsg,
                            sv.hgvsg as variant,
                            var.last_updated as lastUpdated,
                            var.pm_class as pmClass,
                            var.polyphen_cat as polyphenCat,
                            var.report_desc as reportDesc,
                            var.sift_cat as siftCat,
                            evidence_benign_alone_gmaf,
                            evidence_benign_alone_healthy,
                            evidence_benign_strong_case,
                            evidence_benign_strong_coseg,
                            evidence_benign_strong_function,
                            evidence_benign_support_insilico,
                            evidence_benign_support_lsdb,
                            evidence_benign_support_path,
                            evidence_benign_support_spectrum,
                            evidence_benign_support_variable,
                            evidence_evidence_class,
                            evidence_justification,
                            evidence_path_alone_known,
                            evidence_path_alone_truncating,
                            evidence_path_strong_case,
                            evidence_path_strong_coseg,
                            evidence_path_strong_function,
                            evidence_path_support_coseg,
                            evidence_path_support_gene,
                            evidence_path_support_gmaf,
                            evidence_path_support_hotspot,
                            evidence_path_support_indel,
                            evidence_path_support_insilico,
                            evidence_path_support_lsdb,
                            evidence_path_support_novel_missense,
                            evidence_path_support_spectrum,
							ausr.username as alogin,
							cusr.username as clogin
                    from	variant as var
                    join    seq_variant as sv
                    on      sv.variant = var.variant
			        left
				    join	auth_user as ausr
					on		ausr.id = var.authorised_id
 					left
					join	auth_user as cusr
					on		cusr.id = var.classified_id  """ /*
                    where	var.classified_id is not null
                    or      var.authorised_id is not null
                    """*/



        def       rows = sql.rows( qry.toString())

        CurVariant.withTransaction
        {
            for ( row in rows )
            {
                ++rowcnt

                String msg = "Row: ${rowcnt} Processing Variant [${row.variant}]"
                log.info( msg )

                //  Check if Variant exists: look for specific SeqSample object and variant string
                //
                def var = CurVariant.findByVariant( row.variant )
                if ( var )
                {
                    log.error( "Variant already exists [${row.variant}]")
                    continue
                }

                //  Populate evidence
                //
                def evd = new Evidence()

                evd.benignAloneGmaf         = row.evidence_benign_alone_gmaf
                evd.benignAloneHealthy      = row.evidence_benign_alone_healthy
                evd.benignStrongCase        = row.evidence_benign_strong_case
                evd.benignStrongCoseg       = row.evidence_benign_strong_coseg
                evd.benignStrongFunction    = row.evidence_benign_strong_function
                evd.benignSupportInsilico   = row.evidence_benign_support_insilico
                evd.benignSupportLsdb       = row.evidence_benign_support_lsdb
                evd.benignSupportPath       = row.evidence_benign_support_path
                evd.benignSupportSpectrum   = row.evidence_benign_support_spectrum
                evd.benignSupportVariable   = row.evidence_benign_support_variable
                evd.evidenceClass           = row.evidence_evidence_class
                evd.justification           = row.evidence_justification
                evd.pathAloneKnown          = row.evidence_path_alone_known
                evd.pathAloneTruncating     = row.evidence_path_alone_truncating
                evd.pathStrongCase          = row.evidence_path_strong_case
                evd.pathStrongCoseg         = row.evidence_path_strong_coseg
                evd.pathStrongFunction      = row.evidence_path_strong_function
                evd.pathSupportCoseg        = row.evidence_path_support_coseg
                evd.pathSupportGene         = row.evidence_path_support_gene
                evd.pathSupportGmaf         = row.evidence_path_support_gmaf
                evd.pathSupportHotspot      = row.evidence_path_support_hotspot
                evd.pathSupportIndel        = row.evidence_path_support_indel
                evd.pathSupportInsilico     = row.evidence_path_support_insilico
                evd.pathSupportLsdb         = row.evidence_path_support_lsdb
                evd.pathSupportNovelMissense= row.evidence_path_support_novel_missense
                evd.pathSupportSpectrum     = row.evidence_path_support_spectrum

                //  Set curation Users
                //
                def authUser = row.alogin ? AuthUser.findByUsername( row.alogin ) : null
                def clsUser  = row.clogin ? AuthUser.findByUsername( row.clogin ) : null
                if ( ! authUser && ! clsUser )
                {
                    log.error( "Couldnt find users in Variant [${row.variant}]")
                    //continue
                }

                row.remove('evidence_benign_alone_gmaf')
                row.remove('evidence_benign_alone_gmaf')
                row.remove('evidence_benign_alone_healthy')
                row.remove('evidence_benign_strong_case')
                row.remove('evidence_benign_strong_coseg')
                row.remove('evidence_benign_strong_function')
                row.remove('evidence_benign_support_insilico')
                row.remove('evidence_benign_support_lsdb')
                row.remove('evidence_benign_support_path')
                row.remove('evidence_benign_support_spectrum')
                row.remove('evidence_benign_support_variable')
                row.remove('evidence_evidence_class')
                row.remove('evidence_justification')
                row.remove('evidence_path_alone_known')
                row.remove('evidence_path_alone_truncating')
                row.remove('evidence_path_strong_case')
                row.remove('evidence_path_strong_coseg')
                row.remove('evidence_path_strong_function')
                row.remove('evidence_path_support_coseg')
                row.remove('evidence_path_support_gene')
                row.remove('evidence_path_support_gmaf')
                row.remove('evidence_path_support_hotspot')
                row.remove('evidence_path_support_indel')
                row.remove('evidence_path_support_insilico')
                row.remove('evidence_path_support_lsdb')
                row.remove('evidence_path_support_novel_missense')
                row.remove('evidence_path_support_spectrum')
                row.remove('alogin')
                row.remove('clogin')
                row.remove('oldhgvsc')

                //	Copy Variant as domain class
                //
                var = new CurVariant( row as Map )

                var.evidence = evd
                if (authUser)
                   var.authorised = authUser

                if (clsUser)
                   var.classified = clsUser

                //  Save variant
                //
                dbl.saveRecord( var, false )
                ++modcnt
            }

            log.info( "Updated ${modcnt} Variants")

            return modcnt
        }
    }

    /**
     * Filter a given seqrun
     *
     * @return  Count of modified records
     */
    static int filter( String orm )
    {
        def cnt = 0
        def vfs = new VarFilterService()
//        def report

        log.info( "Run filter for ORM ${orm}")

        SeqVariant.withSession
        {
            Session session ->
                println "calling apply filter..."
                cnt = vfs.applyFilter( session, false )
                log.info( "Set Filter for ${cnt} Variants")
        }
//        report.flat.prettyPrint()

        log.info( "Updated ${cnt} rows")
        return cnt
    }

    /**
     * Filter a given seqrun
     * all: do all, not just ROI
     * @return  Count of modified records
     */
    static int applyFilters( String orm, force = true  )
    {
        def cnt = 0
        def vfs = new VarFilterService()
//        def report

        log.info( "Run filter for ORM ${orm}")
        //cnt = vfs.updateRoiFlags(false)


        if (force) {

            //cnt = vfs.updateRoiFlags(false) not needed as this is done by applyFIlter

            SeqVariant.withSession
                    {
                        Session session ->

                            cnt = vfs.applyFilter(session, true)
                            log.info("Set Filter for ${cnt} Variants")
                    }
//        report.flat.prettyPrint()
        }
        else {
            SeqVariant.withSession
                    {
                        Session session ->

                            cnt = vfs.applyFilter(session, false)
                            log.info("Set Filter for ${cnt} Variants")
                    }
        }
        log.info( "Updated ${cnt} rows")
        return cnt
    }


//tdo use http://bioinf-pathos/Holly/pathologist/getPathologistSample?sampleId=15K2862
    /**
     * Filter a given seqrun
     * all: do all, not just ROI
     * @return  Count of modified records
     */
    static int applyOorFilters( String orm  )
    {
        def cnt = 0
        def vfs = new VarFilterService()
//        def report

        log.info( "Run filter for ORM ${orm}")
        println "DISABLED. This is deprecated and is no longer in use. Mass OOR updates can still be achieved through apply filters force-all (-af)."
        //cnt = vfs.updateRoiFlags(false)


        log.info( "Updated ${cnt} rows")
        return cnt
    }

    /**
     * Returns true if Sample was found in Holly and loaded, False otherwise
     * @param sampleName
     */
    static boolean loadHollyPatSample(rdb, String sampleName) {
        def debug = true

        def holly = new HollyUtil()

        def hollySample = holly.getSample(sampleName)

        PatSample.withTransaction {

            PatSample ps = PatSample.findBySample(sampleName)

            //check for success
            if (hollySample) {
                if (debug) {
                    println "Updating " + ps.sample
                }
                ps.hAndE = hollySample.hAndE
                ps.slideComments = hollySample.slideComments
                ps.slideTech = hollySample.slideTech
                ps.retSite = hollySample.retSite
                ps.repMorphology = hollySample.repMorphology
                ps.methylGreen = hollySample.methylGreen
                ps.pathMorphology = hollySample.pathMorphology
                ps.tumourPct = hollySample.tumourPct
                ps.pathComments = hollySample.pathComments
                ps.pathologist = hollySample.pathologist
                ps.hollyLastUpdated = hollySample.lastUpdated

            }
        }

        if (hollySample) {  return true  }
        return false

    }

    /**
     *
     * @param force
     */
    void loadAllHollyPatSamples( rdb )
    {
        def qry =  "select * from pat_sample"

        def  rows = sql.rows( qry.toString() )

        def rowcnt = 0

        for ( row in rows )
        {
            def res = loadHollyPatSample(rdb, row.sample)
            if (res)
            {
                ++rowcnt    //only increment if holly had sample
            }
        }

        println "Updated ${rowcnt} PatSamples with data from Holly"
    }

    /**
     * mass update function
     * set panel frequencies for all seqvariants
     * @param orm
     */
    static void calcPanelFrequencies( String orm ) {
        //check if the panel freq updated table exists: if not , run the setup from scratch function

        def db  = new DbConnect( orm )
        def sql = db.sql()


        SeqVariant.withTransaction {
            def timeStart
            def timeStop
            def v
            def allPanels = Panel.findAll()
            def updated = 0
            def vf = new VarFilterService()
            timeStart = new Date()
            for (p in allPanels) {
               updated += vf.setPanelFrequenciesForVariantsInPanel(p)
            }
            timeStop = new Date()
            println "Did " + updated + " in " + TimeCategory.minus(timeStop, timeStart)
        }


    }



    /**
     * Set CTL or NTC sample types
     *
     * @param orm   DBname eg pa_local
     */
    static void setSampleTypes( String orm )
    {
        log.info( "Setting SampleTypes for ${orm}")


        //  Set Control and NTC sample types
        //
        int count = 0
        SeqSample.withTransaction
                {
                    def allSeqSamples = SeqSample.getAll()
                    for (ss in allSeqSamples) {
                        String sampleType = null

                        if (ss.sampleName.startsWith("NTC")) {
                            sampleType = "NTC"
                        } else if (ss.sampleName.startsWith("CTRL") || ss.sampleName.startsWith("CONTROL") || ss.sampleName.startsWith("NA12878") || ss.sampleName.startsWith("NA19240")) {
                            sampleType = "CTRL"
                        }

                        if (sampleType) {
                            ss.setSampleType(sampleType)
                            count++
                        }
                    }
                }
        log.info( "Set ${count} sample types")
    }


    /**
     * Apply SeqSample relationships for GORM tables
     *
     * @param orm   DBname eg pa_local
     */
    static void setSampleRelations( String orm )
    {
        log.info( "Setting SeqRelation table for ${orm}")

        //  Drop all relations, then drop the SeqRelation object
        //
        SeqRelation.withTransaction
        {
            RelationService.dropAllRelations()
        }

        //  Add SeqSample replicate relations
        //
        Seqrun.withTransaction
        {
            int cnt = RelationService.assignReplicateRelation( Seqrun.findAll(), false)
            log.info("Assigned Replicate to ${cnt} samples")
        }

        //  Add SeqSample duplicate relations
        //
        PatSample.withTransaction
        {
            int cnt = RelationService.assignDuplicateRelation( PatSample.findAll(), false)
            log.info( "Assigned Duplicate to ${cnt} samples")
        }
    }


    /**
     * Load in Tumour Normal relationships from a TSV file
     *
     * @param tndata    Tsv of seqrun and samples
     * @param orm       Gorm DB to populate
     * @return          Number of valid lines
     */
    static int loadTumourNormal( Tsv tndata, String orm )
    {
        List<Map>   tnmaps  = tndata.rowMaps
        int         nlines  = 0

        log.info( "Loading ${tnmaps.size()} lines from ${tndata} into ORM ${orm}")

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        def db  = new DbConnect( orm )
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        //  Remove existing TN relationships
        //
        SeqRelation.withTransaction
                {
                    RelationService.dropAllRelations( 'TumourNormal' )
                }

        //  Process each TN pair
        //
        for ( tnmap in tnmaps )
        {
            if ( addTumourNormal( tnmap ))
                ++nlines
        }

        return nlines
    }

    /**
     * Process a TN pair and add a relationship to DB
     *
     * @param   tn  Map of Tumour/Normal seqrun/sample
     * @return      true if loaded OK
     */
    static boolean addTumourNormal( Map tn )
    {
        boolean res = false

        if ( ! tn.normal || ! tn.tumour ) return res

        Seqrun.withTransaction
        {
            def tsr = Seqrun.findBySeqrun( tn.tumourseqrun as String )
            def nsr = Seqrun.findBySeqrun( tn.normalseqrun as String )

            if ( ! tsr || ! nsr )
            {
                log.error( "Missing Seqrun ${tn}")
                return res
            }

            def tss  = SeqSample.findBySampleNameAndSeqrun( tn.tumour, tsr )
            def nss  = SeqSample.findBySampleNameAndSeqrun( tn.normal, nsr )
            def tnss = SeqSample.findBySampleNameAndSeqrun( tn.tumour + 'N', nsr )

            if ( ! tss || ! nss )
            {
                log.error( "Missing SeqSample ${tn}")
                return res
            }

            //  Add TN relationship to samples
            //
            res = addTNRelation( tss, nss, tnss )
        }

        return res
    }

    /**
     * Add a TN Relation for samples
     *
     * @param   tss     Tumour SeqSample
     * @param   nss     Normal SeqSample
     * @param   tnss    Optional TumourNormal SeqSample
     * @return          true if added OK
     */
    static boolean addTNRelation( SeqSample tss, SeqSample nss, SeqSample tnss = null )
    {
        def tnrel = new SeqRelation( relation: 'TumourNormal', base: tss.sampleName ).save()

        tss.sampleType = 'Tumour'
        nss.sampleType = 'Normal'
        tss.addToRelations( tnrel )
        nss.addToRelations( tnrel )
        if ( tnss )
        {
            tnss.sampleType = 'TumourNormal'
            tnss.addToRelations( tnrel )
            tnss.save( true )
        }

        tss.save( true )
        nss.save( true )

        return true
    }

     boolean migrateToClinContextModel( String orm ) {
        //populate var links


         def timeStart = new Date()
        //some code you want to time
        if (! VarLink.findAll() ) { //only if we have no varlinks..
            def insQry = """
                INSERT INTO var_link (seq_variant_id,cur_variant_id,preferred,originating,version)
                SELECT sv.id, cv.id, true, false, 0 FROM seq_variant AS sv INNER JOIN cur_variant AS cv ON cv.id=sv.curated_id
                WHERE sv.curated_id IS NOT NULL
                """

            def res = sql.execute(insQry)
        } else {
            println "VarLinks exist in database. Refusing."
            return false
        }
         def timeStop = new Date()
         TimeDuration duration = TimeCategory.minus(timeStop, timeStart)
         println "Created VarLinks in: " + duration

         timeStart = new Date()
        //populate GrpVariants
        CurVariant.withTransaction {

            def allCv = CurVariant.findAll()
            for(cv in allCv) {
                cv.grpVariant =  new GrpVariant(accession:cv.hgvsg,muttyp:'SNV')
                cv.save()
            }
        }

         timeStop = new Date()
         duration = TimeCategory.minus(timeStop, timeStart)
         println "Crated GrpVariants for all CurVariants in " + duration


        return true

    }


}

