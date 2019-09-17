/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.pathos.pipeline.MutalyzerUtil
import org.petermac.util.Tsv
import org.petermac.util.YamlUtil

/**
 * Extract a unique list of variants from a list of Vcf files (or a TSV file)
 * Annotate and add these variants to a the DataSource annotation cache
 *
 * Author:  Kenneth Doig
 * Date:    14-Dec-14
 */

@Log4j
class Annotator
{
    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "Annotator [options] in.dat ...",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nConvert a list of annotation files to cached variants\n')

        //	Options to command
        //
        cli.with
        {
            h(  longOpt: 'help',		'this help message' )
            d(  longOpt: 'debug',		'turn on debugging' )
            s(  longOpt: 'datasource',   args: 1, required: true, 'comma separated list of datasources to use for annotation eg mutalyzer,vep,annovar,myvariant' )
            r(  longOpt: 'rdb',          args: 1, required: true, 'RDB to use' )
            e(  longOpt: 'errors',       args: 1, 'File name for error records' )
            o(  longOpt: 'output',       args: 1, 'File name for dumping YAML records' )
            mut(longOpt: 'mutalyzer',    args: 1, 'Mutalyzer annotation server host [https://mutalyzer.nl]' )
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

        //  Validate data sources
        //
        List dss = (opt.datasource as String).tokenize(',')

        //  Open files
        //
        List<File> vcfFiles = []
        for ( inf in argin )
        {
            def infile = new File( inf as String )
            if ( ! infile.exists())
            {
                log.fatal( "File ${infile.name} doesn't exist")
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
        File errFile = null
        if ( opt.errors )
        {
            errFile = new File( opt.errors as String )
            errFile.delete()
        }

        //  Perform data load
        //
        log.info( "Start Annotator " + (args.size() > 10 ? args[0..10] + "..." : args))

        //  Process VCFs
        //
        int nmut = new Annotator().annotateVcf( vcfFiles, opt.rdb, dss, errFile, opt.mutalyzer ?: 'https://mutalyzer.nl', opt.output ?: null )

        log.info( "Done: processed ${vcfFiles.size()} files, annotated ${nmut} variants." )
    }

    /**
     * Annotate VCF variants
     *
     * @param   vcfs    List of VCF Files
     * @param   sql     RDB to cache variants in
     * @param   dss     DataSource List
     * @param   errFile File for errors
     * @param   mutHost Mutalyzer host
     * @param   output  Dumpfile name
     * @return          Number of variants output
     */
    int annotateVcf( List<File> vcfs, String rdb, List dss, File errFile, String mutHost, String output )
    {
        int nv = 0

        //  dump annotations and exit
        //
        if ( output )
        {
            return outputYaml( vcfs, rdb, dss, output )
        }

        //  Add all data sources to cache
        //
        for ( String ds in dss )
        {
            switch (ds.toLowerCase())
            {
                case 'mutalyzer':
                    List vars = uniqVars( vcfs, 'mutalyzer' )

                    def mds = new MutVarDataSource( rdb, mutHost )
                    nv      = mds.addToCache( vars )

                    log.info( "Added ${nv} new vars to Mutalyzer cache out of ${vars.size()}")
                    break
                case 'annovar':
                    List vars = uniqVars( vcfs, 'annovar' )

                    def ads = new AnoVarDataSource( rdb )
                    nv      = ads.addToCache( vars )

                    log.info( "Added ${nv} new vars to Annovar cache out of ${vars.size()}")
                    break
                case 'vep':
                    List vars = uniqVars( vcfs, 'vep' )

                    def vds = new VepVarDataSource( rdb )
                    nv      = vds.addToCache( vars )

                    log.info( "Added ${nv} new vars to VEP cache out of ${vars.size()}")
                    break
                case 'myvariant':
                    List vars = uniqVars( vcfs, 'myvariant' )

                    def mvds = new MyVariantVarDataSource( rdb )
                    nv       = mvds.addToCache( vars )

                    log.info( "Added ${nv} new vars to MYV cache out of ${vars.size()}")
                    break
                case 'iarc':
                    def  ids  = new IarcDataSource( rdb )
                    Tsv itsv  = new Tsv( vcfs[0] )
                    itsv.load( true )
                    List<Map> vars = itsv.rowMaps
                    nv        = ids.addToCache( vars )

                    log.info( "Added ${nv} new vars to IARC cache out of ${vars.size()}")
                    break
                case 'invitae':
                    def  ids  = new InvitaeVarDataSource( rdb )

                    //  Load in TSV file
                    //
                    Tsv itsv  = new Tsv( vcfs[0] )
                    itsv.load( true )
                    List<Map> vars = itsv.rowMaps

                    //  Remove duplicates
                    //
                    vars = ids.uniqVars( vars )

                    //  Clear cache
                    //
                    if ( vars ) ids.deleteAll()

                    //  Add to cache
                    //
                    List<Map> chunkVars
                    while ( chunkVars = chunk( vars, 10000, "Getting variants chunk" ))
                    {
                        nv += ids.addToCache( chunkVars, errFile )
                    }

                    log.info( "Added ${nv} new vars to Invitae cache out of ${vars.size()}")
                    break
                default:
                    log.error( "Unknown datasource ${ds}" )
                    break
            }
        }

        return nv
    }

    int chidx = 0       // index to chunk method

    /**
     * Break a List into chunks for processing
     *
     * @param   l       List to partition
     * @param   size    Chunk size
     * @param   msg     Optional message to log
     * @return
     */
    private List chunk( List l, int size, String msg = '' )
    {
        int start = chidx * size
        int end   = Math.min((++chidx) * size-1, l.size()-1)
        if ( start > end ) return []

        if ( msg ) log.info( "${msg} records[${start}-${end}]")

        return l[(start)..(end)]
    }

    /**
     * Extract all unique VCF variants from a list of VCF files
     *
     * @param   vcfs      List of VCF Files
     * @param   ds        Datasource type
     * @return            List of HGVSg variants
     */
    private static List<String>  uniqVars( List<File> vcfs, String ds )
    {
        int  totalvars      = 0
        List<String> vars   = []

        //  Loop through VCF files
        //
        for ( vcf in vcfs )
        {
            assert vcf.exists()

            List v = []
            switch (ds)
            {
                case 'mutalyzer':
                    v = MutalyzerUtil.vcfVariants( vcf )
                    break
                case 'annovar':
                    v = MutalyzerUtil.vcfVariants( vcf, ds )
                    break
                case 'vep':
                    v = MutalyzerUtil.vcfVariants( vcf, ds )
                    break
                case 'myvariant':
                    v = MutalyzerUtil.vcfVariants( vcf, 'hgvsg' )
                    break
                default:
                    log.error( "Unknown datasource ${ds}" )
                    break
            }

            totalvars += v.size()
            vars << v
        }

        //  Collapse and deduplicate variants
        //
        vars = vars.flatten().unique()
        double uniq = (totalvars != 0 ? vars.size() / totalvars : 0)
        log.info( "Total vars ${totalvars} Unique vars ${vars.size()} Percent " + String.format("%.2f%%",uniq*100))

        return vars
    }

    private static int outputYaml( List<File> vcfs, String rdb, List dss, String output  )
    {
        int nv = 0
        def  mds     = new DataSource( rdb )
        List vars    = uniqVars( vcfs, 'mutalyzer' )
        List<Map> vmaps =[], vms

        for ( ds in dss )
        {
            switch (ds)
            {
                case 'mutalyzer':
                    vms = mds.getValueMaps( 'MUT', vars )
                    nv += vms.size()
                    log.info( "Found ${vms.size()} variants in ${ds} cache")
                    vmaps << [ datasource: ds, database: rdb, variants: vms ]
                    break
                case 'vep':
                    vms = mds.getValueMaps( 'VEP', vars )
                    nv += vms.size()
                    log.info( "Found ${vms.size()} variants in ${ds} cache")
                    vmaps << [ datasource: ds, database: rdb, variants: vms ]
                    break
                case 'annovar':
                    vms = mds.getValueMaps( 'ANV', vars )
                    nv += vms.size()
                    log.info( "Found ${vms.size()} variants in ${ds} cache")
                    vmaps << [ datasource: ds, database: rdb, variants: vms ]
                    break
                case 'myvariant':
                    vms = mds.getValueMaps( 'MYV', vars )
                    nv += vms.size()
                    log.info( "Found ${vms.size()} variants in ${ds} cache")
                    vmaps << [ datasource: ds, database: rdb, variants: vms ]
                    break
                default:
                    log.error( "Unknown datasource ${ds}" )
                    break
            }
        }

        //  Collapse and deduplicate variants
        //
        vmaps = vmaps.flatten()
        log.info( "Found ${nv} variants in ${vmaps.size()} data sources")

        //  Dump output in YAML format
        //
        File outf = new File(output)
        outf.delete()
        YamlUtil.dump( outf, vmaps )

        return vmaps.size()
    }
}