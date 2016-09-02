/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.Mutalyzer

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the class for Invitae TSV annotation
 * See http://clinvitae.invitae.com
 *
 * 01   Ken Doig            3-Jun-2015      Initial
 */
@Log4j
class InvitaeVarDataSource extends VarDataSource
{
    static def code = DS.Invitae.code()         // Datasocurce abbreviation for RDB

    InvitaeVarDataSource( String rdb )
    {
        super( rdb )
    }

    /**
     * Add a list of HGVSg variants into cache after calling Mutalyzer
     *
     * @param vars  List of TSV rowMap variants
     * @return      Number of variants added (note: some may be added twice as aliases)
     */
    static int addToCache( List<Map> vars, File errfile, boolean useCache = true )
    {
        def jb   = new JsonBuilder()
        int nadd = 0

        //  Generate an error header
        //
        if ( errfile )
        {
            List header = vars[0].keySet() as String[]
            header << "Error"
            errfile << header.join('\t') + '\n'
        }

        //  Look for valid rows with a HGVSc refseq transcript
        //
        int nerr = 0
        List<Map> validVars = []
        for ( var in vars )
        {
            //  Do we have a Refseq transcript ?
            //
            String hgvs = var.Nucleotide_Change
            if ( hgvs.startsWith('NM_') || ( hgvs.startsWith('NC_') && hgvs.contains(':g.')))
            {
                var.hgvsc = hgvs
                validVars << var
                continue
            }

            //  dump variant as an error
            //
            var.error = "No refseq transcript"
            if ( errfile ) errfile << var.values().join('\t') + '\n'
             ++nerr
        }

        log.info( "Number of variants ok/reject  ${validVars.size()}/${nerr}" )

        //  Convert all the HGVSc to HGVSg for caching
        //
        List<Map> hgvss = Mutalyzer.batchPositionConverter( validVars.hgvsc )

        //  Filter out the ones that converted OK
        //
        int i = -1   // index to HGVSc transcripts
        List<Map> cacheVars = []
        for ( hgvs in hgvss )
        {
            i++     // move index along

            if ( hgvs.error )
            {
                validVars[i].remove('hgvsc')   //  clean out added parameter to give raw error raw
                validVars[i].error = "Mutalyzer error ${hgvs.error}"
                if ( errfile ) errfile << validVars[i].values().join('\t') + '\n'
                log.warn( "Couldn't convert ${hgvs.variant} to HGVSg ${hgvs.error}")
                continue
            }

            //  cDNA variant ?
            //
            if ( hgvs.variant.startsWith('NM_'))
            {
                validVars[i].hgvsg = HGVS.normalise(hgvs.hgvsg)
                validVars[i].hgvsc = HGVS.normalise(hgvs.variant)
            }

            //   Genomic variant ?
            //
            if ( hgvs.variant.startsWith('NC_'))
            {
                validVars[i].hgvsg = HGVS.normalise(hgvs.hgvsg)
                validVars[i].hgvsc = ''
            }

            if ( ! validVars[i].hgvsg || validVars[i].hgvsc == null || ! validVars[i].hgvsg?.startsWith('chr') || ! validVars[i].hgvsc?.startsWith('NM_'))
                log.warn( "Error normalising ${validVars[i].Nucleotide_Change} hgvsg=${validVars[i].hgvsg} hgvsc=${validVars[i].hgvsc}" )

            cacheVars << validVars[i]
        }

        log.info( "Number of HGVS variants to cache ${cacheVars.size()}" )

        //  Add variants into cache
        //
        for ( var in cacheVars )
        {
            jb(var)

            Map params =    [
                            data_source:        code + '-' + var.Source,
                            hgvsg:              var.hgvsg,
                            attr:               'mut_map',
                            value:              jb.toString(),
                            version:            'beta',
                            gene:               var.Gene,
                            hgvsc:              var.hgvsc,
                            hgvsp:              var.Protein_Change,
                            classification:     var.Reported_Classification
                            ]

            List ids = saveValueMap( params )
            if ( ! ids ) log.warn( "Couldn't add ${params}")
            nadd += ids.size()
        }

        return nadd
    }

    /**
     * Remove duplicate vars Todo: need to collapse duplicates ?
     *
     * @param   vars    List of var Maps
     * @return          Uniq List
     */
    static List<Map> uniqVars( List<Map> vars )
    {
        Map found = [:]
        List<Map> uniq = []
        for ( var in vars )
        {
            String key = var.Nucleotide_Change + var.Source
            if ( ! found[key])
            {
                found[key] = var
                uniq << var
            }
        }

        double uniqpct = (vars.size() != 0 ? uniq.size() / vars.size() : 0)
        log.info( "Total vars ${vars.size()} Unique vars ${uniq.size()} Percent " + String.format("%.2f%%",uniqpct*100))

        return uniq
    }

    /**
     * Delete all Invitae cache records
     *
     * @return
     */
    static boolean deleteAll()
    {
        return deleteAll( code )
    }
}
