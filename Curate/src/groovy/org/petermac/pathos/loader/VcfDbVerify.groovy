/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.loader

import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.annotate.*
import org.petermac.pathos.curate.AlignStats
import org.petermac.pathos.curate.SeqSample
import org.petermac.pathos.curate.Seqrun
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.DbConnect
import org.petermac.util.Vcf
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Verify variants from a list of Vcf files against a database
 *
 * Author:  Kenneth Doig / Andrei Seleznev
 * Date:    15-Oct-18
 *
 * 01       30-Jan-19   kdd     Added check for missing VCFs
 */

@Log4j
class VcfDbVerify
{
    //  Annotation object for DB
    //
    static def vds
    static int totvar = 0
    static int toterr = 0
    static int totaln = 0

    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "VcfDbVerify [options] in.vcf ... | [options] --novcf <baseDir>",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nVerify a list of VCF files against a DB\n')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		    'this help message' )
            d( longOpt: 'debug',		'turn on debugging' )
            r( longOpt: 'rdb',          args: 1, required: true, 'RDB to use' )
            e( longOpt: 'errors',       args: 1, 'File name for error records [VcfCheckErr.vcf]' )
            act( longOpt: 'actual',     'Only report variants that are actually expected to be loaded' )
            a( longOpt: 'annotate',     'Only validate MUT/ANV/VEP annotation' )
            qc(longOpt: 'qc',           'Validate AlignStats records' )
            n(      longOpt: 'novcf',   args: 1, 'Look for missing VCF files under this base directory' )
            seqrun( longOpt: 'seqrun',  args: 1, 'seqrun name [extracted from VCF file path]' )
            sample( longOpt: 'sample',  args: 1, 'sample name [extracted from VCF file name]' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return

        List argin = opt.arguments()
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on!" )

        //  Only search for missing VCF files
        //
        if ( opt.novcf )
        {
            if ( ! opt.seqrun )
            {
                log.fatal( "Must supply a --seqrun option with --novcf")
                System.exit(1)
            }

            if ( missingVcfs( opt.novcf, opt.seqrun, opt.sample, opt.rdb ))
            {
                log.fatal( "Missing VCFs in ${opt.novcf}/${opt.seqrun}")
                System.exit(1)
            }
            log.info( "No missing VCFs found")

            return
        }

        //  Open files
        //
        List<File> vcfFiles = []
        for ( inf in argin )
        {
            def infile = new File( inf as String )
            if ( ! infile.exists())
            {
                log.warn( "File ${infile.name} doesn't exist")
                continue
            }

            if ( infile.isFile())
                vcfFiles << infile
        }

        if ( ! vcfFiles)
        {
            log.fatal( "No data files to process")
            System.exit(1)
        }

        //  Open error file and zero it if required
        //
        File errFile = new File("VcfCheckErr.vcf")
        if ( opt.errors )
            errFile = new File( opt.errors as String )
        errFile.delete()
        errFile << (String) Vcf.header( 'VcfDbCheck', [], errFile.name.replaceFirst('.vcf',''))    // create a VCF header for easy fault finding

        //  Perform data load
        //
        log.info( "Start VcfDbCheck " + (argin.size() > 5 ? argin[0..4] + "..." : args))

        //  Process VCFs
        //
        totvar = checkVcfs( opt.seqrun, opt.sample, vcfFiles, opt.rdb, errFile, opt.actual, opt.annotate, opt.qc )

        log.info( "Done: processed ${vcfFiles.size()} files, checked ${totvar} variants, ${toterr} failed ${totvar ? String.format( '%.2f' , (toterr*100)/totvar) : 0} %, Total QC files failed ${totaln}" )

        //  Return an error status if errors seen
        //
        if ( toterr > 0 || totaln > 0 )
        {
            if ( toterr ) log.fatal( "${toterr} Errors found in VCFs")
            if ( totaln ) log.fatal( "${totaln} Errors found in QC files")
            System.exit(1)
        }
    }

    /**
     * Check VCF variants
     *
     * @param seqrun    Seqrun to check (optional)
     * @param sample    Sample to check (optional)
     * @param vcfs      List of VCF Files
     * @param rdb       DB to check
     * @param errFile   Error VCF file
     * @param actual    Check ofr expected errors only
     * @param annotate  Check for annotations
     * @param qc        Check for alignment stats
     * @return          Number of errors found
     */
    static private int checkVcfs( def seqrun, def sample, List<File> vcfs, String rdb, File errFile, boolean actual, boolean annotate, boolean qc )
    {
        def db  = new DbConnect( rdb )
        def sql = db.sql()
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        //  Annotation cache
        //
        vds = new VarDataSource( rdb )

        int nvar = 0
        for ( vcffil in vcfs )
        {
            nvar += checkVcf( seqrun, sample, vcffil, sql, errFile, actual, annotate, qc )
        }

        return nvar
    }

    /**
     * return var's hgvsg, from normalisation if not set, for varmap if so
     * @param var a varmap
     * @return hgvsg string
     */
    static String varHgvsg(var) {
        def hgvsg = var.HGVSg
        if ( ! hgvsg )
        {
            //  convert to basic HGVSg
            //
            Map m = HGVS.normaliseVcfVar( var.CHROM, var.POS, var.REF, var.ALT )
            hgvsg = m.hgvsg
        }
        return hgvsg
    }

    /***
     * check if the gene a variant belongs to is masked (ie should not be shown)
     * we check both var.gene and vep annotation besttx.gene (if exists)
     *
     * @param var variant Map
     * @param   genemask List
     * @return true if gene masked and should not be shown, false otherwise
     */
    static boolean checkVariantIsMasked(Map var, ArrayList<String> genemask)
    {
        if (!genemask)
        {
            return false
        }

        if (genemask && var.gene in genemask)
        {
            return false
        }

        if (!var.gene)
        {
            //  gene may be in annotation check VEP
            //
            Map valuemap = vds.getValueMap( 'VEP', varHgvsg(var))
            if( valuemap )
            {
                if ( valuemap.besttx?.gene_symbol && valuemap.besttx?.gene_symbol in genemask )
                {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Check VCF variants
     *
     * @param   vcffil  VCF File to check
     * @param   sql     RDB SQL object
     * @param   errFile Error dump file
     * @return          Number of variants checked
     */
    static private int checkVcf( def seqrun, def sample, File vcffil, Sql sql, File errFile, boolean actual, boolean annotate , boolean qc )
    {
        //  Load in VCF file
        //
        assert vcffil.exists()
        Vcf vcf = new Vcf( vcffil )
        if ( ! vcf.load())
        {
            log.error( "No variants found in ${vcffil}" )
            return 0
        }

        //  Get rows for error reporting
        //
        List<List> rowl = vcf.getRows()

        //  Find parent directory (sample name)
        //
        if ( ! sample )
            sample = vcffil.name.replaceFirst('.vcf','')

        //  Find parent of sample dir (seqrun name)
        //
        if ( ! seqrun )
            seqrun = vcffil.getAbsoluteFile()?.getParentFile()?.getParentFile()?.getName()

        if ( ! seqrun || ! sample )
        {
            log.fatal( "Cant find seqrun [${seqrun}] and/or sample [${sample}]")
            return 0
        }
        log.info( "Checking ${seqrun} ${sample}")

        //  Convert VCF rows to Maps
        //
        List<Map> vars = vcf.rowMaps

        //  Loop through vars checking each one
        //
        int nerr = 0, nvar = 0, naln = 0;

        //  Get gene mask
        //
        ArrayList<String> genemask = new  ArrayList<String>()
        String sampleType = '';
        Seqrun.withSession {
            SeqSample ss = SeqSample.findBySampleNameAndSeqrun(sample, Seqrun.findBySeqrun(seqrun))
            genemask = ss?.geneMask()
            sampleType = ss?.sampleType
        }

        //  Check for AlignStats records
        //
        if ( qc )
        {
            AlignStats.withSession {
                int nrows = AlignStats.countBySeqrunAndSampleName( seqrun, sample )
                if ( nrows < 2 && sampleType != 'TumourNormal' )
                {
                    log.debug( "Missing AlignStats records: ${nrows}" )
                    ++naln;
                }
            }
        }

        //  Loop through each variant in VCF
        //
        for ( var in vars )
        {

            //  skip variants that may be excluded by gene mask
            //
            if( checkVariantIsMasked( var, genemask )) {
                continue
            }

            ++nvar


            //  Set the variant to check, first try for VCF info annotation (HGVSg=chrn:g.nnnn)
            //  if that fails do a conversion of the raw variant
            //
            def hgvsg = var.HGVSg
            if ( ! hgvsg )
            {
                //  convert to basic HGVSg
                //
                Map m = HGVS.normaliseVcfVar( var.CHROM, var.POS, var.REF, var.ALT )
                hgvsg = m.hgvsg
            }

            //  Look up variant in DB
            //
            boolean ok = checkVar( sql, hgvsg, seqrun, sample )
            if ( ok )
            {
                //  Check if the variant is annotated
                //
                if ( annotate )
                {
                    boolean hasVEP = false, hasANV = false, hasMUT = false
                    if ( vds.getValueMap( 'MUT', hgvsg )) hasMUT = true
                    if ( vds.getValueMap( 'VEP', hgvsg )) hasVEP = true
                    if ( vds.getValueMap( 'ANV', hgvsg )) hasANV = true

                    log.debug( "Annotation for ${hgvsg} ${hasMUT} ${hasVEP} ${hasANV}" )

                    //  report misssng annotations
                    //
                    if ( ! hasMUT || ! hasVEP || ! hasANV )
                    {
                        log.error( "Missing annotation for ${hgvsg} ${!hasMUT?'!MUT':'   '} ${!hasVEP?'!VEP':'   '} ${!hasANV?'!ANV':'   '}")
                        errFile << rowl[nvar-1].join('\t') + '\n'
                        ++nerr
                    }
                }
            }
            else
            {
                //  Dont report common Mutalyzer errors eg
                //      muterr=No_transcripts_found
                //      muterr=No_transcripts_found_in_mutation_region
                //      muterr=(variantchecker):_X_not_found_at_position_nnnn,_found_X_instead.
                //      muterr=(variantchecker):_Position_nnnn_is_out_of_range.

                if (    actual
                &&      var.muterr
                &&      (var.muterr.contains('No_transcripts_found')
                ||       var.muterr.contains('(GenRecord)')
                ||       var.muterr.contains('not_found_at_position')
                ||       var.muterr.contains('(variantchecker):_Position')))
                {
                    log.debug( "Mutalyzer error with variant ${hgvsg}: ${var.muterr}" )
                    continue
                }

                //  Dont report if VEP doesn't exist
                //
                if ( actual && ! vds.getValueMap( 'VEP', hgvsg ))
                {
                    log.debug( "VEP AnoVariant missing error with variant ${hgvsg}" )
                    continue
                }

                ++nerr

                //  Report missing variant
                //
                log.info( "Check failed for ${seqrun} ${sample} ${hgvsg} ${var.HGVSc} ${var.muterr} ${var.status}")
                errFile << rowl[nvar-1].join('\t') + '\n'
            }
        }

        toterr += nerr
        String msg = "Checked variants   for ${vcffil} ${nerr}/${vars.size()} ${vars.size() ? String.format( '%.2f' , (nerr*100)/vars.size()) : 0} %"
        nerr ? log.error( msg ) : log.info( msg )

        if ( qc )
        {
            totaln += naln
            msg = "Checked alignStats for ${vcffil} ${naln} errors"
            naln ? log.error( msg ) : log.info( msg )
        }

        return nvar
    }

    /**
     * Look for a variant in the DB
     *
     * @param sql       JDBC SQL object
     * @param var       Variant to look for
     * @param seqrun    Seqrun of variant
     * @param sample    Sample of variant
     * @return          true if exists
     */
    static private boolean checkVar( Sql sql, String var, String seqrun, String sample )
    {
        def qry =   """
                    select	sv.hgvsg
                    from	seqrun sr,
                            seq_sample ss,
                            seq_variant sv
                    where	sr.id = ss.seqrun_id
                    and		ss.id = sv.seq_sample_id
                    and		sr.seqrun      = $seqrun
                    and		ss.sample_name = $sample
                    and     sv.hgvsg       = $var
                    """

        def rs  = sql.rows(qry)
        if ( rs.size() > 1 )
            log.error( "Multiple copies [${rs.size()}] of variant ${seqrun} ${sample} ${var}")

        return rs.size() >= 1
    }

    /**
     * Check for missing VCF files
     *
     * @param seqrun    Seqrun to check (optional)
     * @param sample    Sample to check (optional)
     * @param rdb       DB to check
     * @return          OK ?
     */
    static private boolean missingVcfs( String base, String seqrun, def optsample, String rdb )
    {
        def db  = new DbConnect( rdb )
        def sql = db.sql()
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        String sample = optsample ? optsample : ""

        File baseDir = new File( "$base/$seqrun" )
        if ( ! baseDir.exists() || ! baseDir.isDirectory())
        {
            log.fatal( "Missing base directory: ${base}/${seqrun}")
            return true
        }

        log.info( "Checking for VCFs in $baseDir" )

        //  Search for samples for seqrun
        //
        int errs = 0
        Seqrun.withSession {
            List<SeqSample> sss = SeqSample.findAllBySeqrun( Seqrun.findBySeqrun(seqrun))
            log.info( "Found ${sss.size()} samples for Seqrun $seqrun")
            for ( ss in sss )
            {
                String sampleName = ss.sampleName
                if ( sample && sample != sampleName ) continue
                String vcfPath = "${baseDir.absolutePath}/${sampleName}/${sampleName}.vcf"
                File vcf = new File( vcfPath )
                if ( ! vcf.exists() || ! vcf.size())
                {
                    if ( sampleName.startsWith("NTC"))
                    {
                        log.warn( "Missing or empty VCF file at ${vcfPath}")
                    }
                    else
                    {
                        log.error( "Missing or empty VCF file at ${vcfPath}")
                        ++errs
                    }
                }
            }
        }

        return errs > 0
    }
}