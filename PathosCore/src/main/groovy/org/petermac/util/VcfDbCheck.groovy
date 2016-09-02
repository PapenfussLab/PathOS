/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.annotate.*
import org.petermac.pathos.pipeline.HGVS

/**
 * Verify variants from a list of Vcf files against a database
 *
 * Author:  Kenneth Doig
 * Date:    29-Jul-15
 */

@Log4j
class VcfDbCheck
{
    //  Annotation object for DB
    //
    static def vds
    static int totvar = 0
    static int toterr = 0

    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "VcfDbCheck [options] in.vcf ...",
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
            i( longOpt: 'ignore',       'Ignore common Mutalyzer errors/warnings' )
            a( longOpt: 'annotate',     'Only validate MUT/ANV/VEP annotation' )
            seqrun( longOpt: 'seqrun',  args: 1, 'seqrun name [extracted from VCF file path]' )
            sample( longOpt: 'sample',  args: 1, 'sample name [extracted from VCF file name]' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return

        List argin = opt.arguments()
        if ( opt.h || argin.size() < 1 )
        {
            cli.usage()
            return
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on!" )

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
            return
        }

        //  Open error file and zero it if required
        //
        File errFile = new File("VcfCheckErr.vcf")
        if ( opt.errors )
            errFile = new File( opt.errors as String )
        errFile.delete()
        errFile << Vcf.header( errFile.name.replaceFirst('.vcf',''))    // create a VCF header for easy fault finding

        //  Perform data load
        //
        log.info( "Start VcfDbCheck " + (argin.size() > 5 ? argin[0..4] + "..." : args))

        //  Process VCFs
        //
        totvar = checkVcfs( opt.seqrun, opt.sample, vcfFiles, opt.rdb, errFile, opt.ignore, opt.annotate )

        log.info( "Done: processed ${vcfFiles.size()} files, checked ${totvar} variants, ${toterr} failed ${totvar ? String.format( '%.2f' , (toterr*100)/totvar) : 0} %" )

        if ( toterr )
        {
            log.fatal( "${toterr} Errors found in VCFs")
            System.exit(1)
        }
    }

    /**
     * Check VCF variants
     *
     * @param   vcfs    List of VCF Files
     * @param   rdb     RDB to verify variants from
     * @param   errFile Error dump file
     * @return          Number of variants checked
     */
    static private int checkVcfs( def seqrun, def sample, List<File> vcfs, String rdb, File errFile, boolean ignore, boolean annotate )
    {
        def db  = new DbConnect( rdb )
        def sql = db.sql()

        //  Annotation cache
        //
        vds = new VarDataSource( rdb )

        int nvar = 0
        for ( vcffil in vcfs )
        {
            nvar += checkVcf( seqrun, sample, vcffil, sql, errFile, ignore, annotate )
        }

        return nvar
    }

    /**
     * Check VCF variants
     *
     * @param   vcffil  VCF File to check
     * @param   sql     RDB SQL object
     * @param   errFile Error dump file
     * @return          Number of variants checked
     */
    static private int checkVcf( def seqrun, def sample, File vcffil, Sql sql, File errFile, boolean ignore, boolean annotate )
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
        int nerr = 0, nvar = 0

        for ( var in vars )
        {
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
                //  Dont report common Mutalyzer errors
                //
                if (    ignore
                &&      var.muterr
                &&      (var.muterr.contains('No_transcripts_found')
                ||       var.muterr.contains('(GenRecord)')
                ||       var.muterr.contains('(variantchecker)'))) continue

                ++nerr

                //  Report missing variant
                //
                log.error( "Check failed for ${seqrun} ${sample} ${hgvsg} ${var.HGVSc} ${var.muterr} ${var.status}")
                errFile << rowl[nvar-1].join('\t') + '\n'
            }
        }

        toterr += nerr
        log.info( "Checked variants for ${vcffil} ${nerr}/${vars.size()} ${vars.size() ? String.format( '%.2f' , (nerr*100)/vars.size()) : 0} %")
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
}