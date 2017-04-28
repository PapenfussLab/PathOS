/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */



package org.petermac.util

/**
 *  Utility to unpack a VCF file into a TSV file
 *
 *  Author:     Ken Doig
 *  Date:       12-Sep-14
 */

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger

@Log4j
class Vcf2Tsv
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'Vcf2Tsv [options] in.vcf out.tsv',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nConvert an VCF variant file to normalised DB format TSV\n')

        cli.with
        {
            h(longOpt: 'help',    'Usage Information',    required: false)
            s(longOpt: 'sample',  'Sample Name', args: 1, required: false)
            r(longOpt: 'seqrun',  'SeqRun Name', args: 1, required: false)
            p(longOpt: 'panel',   'Panel Name',  args: 1, required: false)
            c(longOpt: 'columns', 'File of columns to output', args: 1, required: false)
            d(longOpt: 'debug',   'Turn on debug logging')
        }
        def opt = cli.parse(args)

        if ( ! opt ) return
        List argin = opt.arguments()
        if ( opt.h || argin.size() != 2)
        {
            cli.usage()
            return
        }

        //  Debugging needed
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  O p e n   t h e   f i l e s
        //
        //  VCF input file
        //
        File vcff  = new File(argin[0])
        if ( ! vcff.exists())
        {
            log.error( "Input VCF file ${argin[0]} doesn't exist")
            return
        }

        //  TSV output file
        //
        File tsvf  = new File(argin[1])
        if ( tsvf.exists()) tsvf.delete()

        //  Optional list of columns to output
        //
        File colsf = null
        if ( opt.columns )
        {
            colsf = new File(opt.columns as String)
            if ( ! colsf.exists())
            {
                log.error( "File ${opt.columns} doesn't exist")
                return
            }
        }

        //  Run the program
        //
        log.info("Vcf2Tsv " + args )

        def nlines = vcf2Tsv( vcff, tsvf, opt.sample ?: '', opt.seqrun ?: '', opt.panel ?: '', colsf, true )

        log.info("Done, processed ${nlines} lines")
    }

    /**
     * Convert a VCF file into a TSV file
     *
     * @param vcff      Input VCF File
     * @param tsvf      Output TSV file
     * @param sample    Sample name
     * @param seqrun    Seqrun name
     * @param panel     Panel name
     * @param colsf     Column list to output into TSV
     * @param header    True if header needed
     * @return          no of variants processed
     */
    static Integer vcf2Tsv( File vcff, File tsvf, String sample, String seqrun, String panel, File colsf, Boolean header )
    {
        Vcf vcf = new Vcf( vcff )
        vcf.load()
        Tsv tsv = vcf.unpack()

        if ( sample ) tsv.addColumn( 'sample', sample as String )
        if ( seqrun ) tsv.addColumn( 'seqrun', seqrun as String )
        if ( panel  ) tsv.addColumn( 'panel',  panel  as String )

        //  Read in an optional list of columns to output
        //
        if ( colsf )
        {
            //  Set column names - one per line - optionally with alias after a comma
            //
            List    cols        = []
            List    aliasCols   = []

            setColumnNames( colsf, cols, aliasCols )

            //  Output TSV using column list
            //
            tsv.write( tsvf, cols, aliasCols, header )
        }
        else
            tsv.write( tsvf, header )       //  Output TSV

        return tsv.nrows()
    }

    /**
     * Read in output column names with optional alias
     *
     * @param colsf     File of column names - one per line - with optional alias after a comma
     * @param cols      List of column names to populate
     * @param aliasCols List of aliases to use or column name otherwise
     */
    private static void setColumnNames( File colsf, List cols, List aliasCols )
    {
        List<String> names = colsf.readLines()

        //  clean up columns and apply optional aliases
        //
        for ( col in names )
        {
            List<String> cc = col.split( /,/ )  // use comma as separator between column name and alias

            //  simple column name
            //
            if ( cc.size() == 1)
            {
                cols        << col.trim()
                aliasCols   << col.trim()
            }

            //  column has an alias
            //
            if ( cc.size() == 2)
            {
                cols        << cc[0].trim()
                aliasCols   << cc[1].trim()
            }
        }
    }
}
