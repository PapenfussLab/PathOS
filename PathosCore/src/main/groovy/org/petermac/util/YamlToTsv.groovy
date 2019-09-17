/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.util

import groovy.util.logging.Log4j
import org.yaml.snakeyaml.Yaml

/**
 * Description:
 *
 * YAML processing utilities
 *
 * Author: Ken Doig
 * Date: 30-mar-17
 */
@Log4j
class YamlToTsv
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'YamlToTsv [options] in.yaml',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nConvert YAML file to TSV\n')

        cli.with
                {
                    h(longOpt: 'help', 'Usage Information', required: false)
                    f(longOpt: 'format', args: 1, required: true, 'List of fields to output plus optional header' )
                }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Load in format file
        //
        def ftsv = null
        if ( opt.format )
        {
            def fmtfile = new File( opt.format as String )

            if ( ! fmtfile.exists())
            {
                log.error("Format file doesn't exist: " + fmtfile.name)
                return
            }

            ftsv = new Tsv( fmtfile )
            int nline = ftsv.load( true )
            if ( ! nline )
            {
                log.error("Format file has no lines" )
                return
            }
        }
        List<Map> formats = ftsv.getRowMaps()

        //  Get YAML file
        //
        List<String> extra = opt.arguments()
        if ( ! extra )
        {
            cli.usage()
            return
        }

        def yfile = new File( extra[0] as String )
        if ( ! yfile.exists())
        {
            log.error("YAML file doesn't exist: " + yfile.name)
            return
        }

        //  Run the program
        //
        log.info("YamlToTsv " + args )
        convert( yfile, formats )
    }

    public static void convert( File yfile, List<Map> formats )
    {
        List<Map> records = YamlUtil.load( yfile )

        println "${(formats.header).join('\t')}"

        for ( rec in (records[0].trials as List))
        {
            for ( fmt in formats )
            {
                for ( loc in rec.locations )
                {
                    if ( loc?.geo?.lat )
                    {
                        String title = rec?.briefTitle
                        String name  = loc?.name
                        println "${loc?.geo?.lat}\t${loc?.geo?.lon}\t${name?.take(63)}\t${loc?."email"}\t${loc?."status"}\t${title?.take(63)}\t${rec?.id}"
                    }
                }
            }
        }
    }
}

