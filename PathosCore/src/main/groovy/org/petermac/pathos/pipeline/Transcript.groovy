/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.pipeline

import groovy.sql.Sql
import groovy.util.logging.Log4j
import org.petermac.util.DbConnect
import org.petermac.util.Locator
import org.petermac.util.Tsv

/**
 * Transcript manipulation utility
 *
 * Author: Ken Doig
 * Date:   26-Aug-2014
 */
@Log4j
class Transcript
{
    //  Maps of genes to preferred and LRG transcripts
    //
    static Map  prefMap    = null
    static Map  lrgMap     = null
    static Map  txToGene   = [:]

    /**
     * Constructor loads in transcript maps
     *
     * @param   preferred  Map of gene -> refseq transcript (without version)
     */
    Transcript( Map preferred )
    {
        prefMap     = preferred
        lrgMap      = [:]
        invertTxMap()
    }

    /**
     * Constructor loads in transcript maps
     *
     * @param   dbname  Database for transcript references
     */
    Transcript( String dbname )
    {
        prefMap     = preferred( dbname )
        lrgMap      = lrg( dbname )
        invertTxMap()
    }

    private static Map invertTxMap()
    {
        for ( gene in prefMap )
        {
            txToGene << [ (gene.value) : gene.key ]
        }
    }

    /**
     * Create a map of preferred gene transcripts from DB
     *
     * @param dbname    DB schema to use
     * @return          Map of transcripts [gene: transcript]
     */
    static Map preferred(String dbname) {
        Map genes = [:]

        //  Lookup data for gene -> refseq transcript mapping
        //
        def qry =   '''
                    select	distinct
                            gene,
                            accession as refseq
                    from	transcript
                    where	build = 'hg19'
                    and     preferred = 1
                    '''

        try {
            DbConnect db  = new DbConnect(dbname)
            Sql sql = db.sql()
            def rs  = sql.rows(qry)

            //  Create Hash Map of genes to transcripts  [ <gene>: <preferred_ts> ]
            //
            for (g in rs) {
                genes << [ (g.gene) : g.refseq ]
            }

            // Clean up the connection
            //
            sql.close()
        } catch (Exception ex) {
            log.fatal( "Exception when trying to load preferred gene transcripts: " + ex )
            System.exit(1)
        }

        return genes
    }

    /**
     * Create a map of LRG gene transcripts from DB
     *
     * @param dbname    RDB database name for transcript table
     * @return          Map of transcripts [lrg: refseq]
     */
    private static Map lrg( String dbname )
    {
        Map lrgs = [:]

        //  Lookup data for lrg -> refseq transcript mapping
        //
        def qry =   '''
                    select	gene,
                            lrg,
                            refseq
                    from	transcript
                    where	build = 'hg19'
                    and     lrg is not null
                    order
                    by      preferred
                    '''

        try
        {
            def db  = new DbConnect( dbname )
            def sql = db.sql()
            def rs  = sql.rows(qry)

            //  Create Hash Map of lrg transcripts to refseq transcripts
            //
            for ( l in rs )
            {
                lrgs << [ (l.lrg) : l.refseq ]
            }
            sql.close()
        }
        catch( Exception ex )
        {
            log.error( "Exception when trying to load HGVS genes " + ex )
        }

        return lrgs
    }

    /**
     * Select the Refseq (NM_nnn.n) and LRG (LRG_nnnt1) transcript from the Mutalyser transcripts
     *
     * @param   mut     Map of variant [ variant: hgsvg, error: msg, transcripts: ts1,ts2... ]
     * @return          Map of select transcript(s) [refseq: NM_nnn.n:c.nnn, lrg: LRG_nnnt1:c.nnn]
     */
    static Map selectTranscript( Map mut )
    {
        List transcripts = mut.transcripts.tokenize(',')
        List tss         = selectRefseq( transcripts )

        log.debug( "Filtering ${tss.size()}\t${tss}")

        if ( ! tss )
        {
            log.warn( "No transcripts found for variant ${mut.variant}")
            return [:]
        }

        //  Select only one transcript, preference is for first non-intergenic variant
        //
        String bestts = tss[0]
        if ( tss.size() > 1 )
        {
            //  Choose the best (coding-wise) transcript
            //
            bestts = selectBest( tss )

            log.warn( "Multiple transcripts found for variant ${mut.variant} filtered ${tss} choosing ${bestts}")
        }

        return [ refseq: bestts, lrg: selectLrg( bestts, lrgMap )]
    }

    /**
     * Select a coding transcript, if none then return first
     *
     * @param   tss   List of variants and transcripts
     * @return        First coding transcript
     */
    private static String selectBest( List<String> tss )
    {
        String bestts = null

        for ( ts in tss )
            if ( selectCoding( bestts, ts ))
                bestts = ts

        return bestts
    }

    /**
     * Select the best transcript out of pair
     * Todo: defer decision until merged with more annotation
     * This makes a guess at the most important transcript, should keep all really
     *
     * Use HGVS convention of c.nnnn... is within the gene and
     * c.*nnnn... and c.-nnnn... is 3' and 5' UTR
     *
     * eg NM_014210.3:c.-4849G>A is non-coding 5'UTR downstream
     *    NM_000267.3:c.5205+81C>T is intronic within the gene
     *
     * @param best      The best transcript found so far
     * @param target    New candidate transcript
     * @return          true if target is better than best
     */
    static boolean selectCoding( String best, String target )
    {
        if ( best == null ) return true

        Map<String,String> bmap = HGVS.parseHgvsC( best )
        Map<String,String> tmap = HGVS.parseHgvsC( target )

        //  if best is 5'/3'UTR eg c.[*-]nnnn... and target is genic eg c.nnnn[+-nnn]... then use target
        //
        if ((bmap.pos =~ /^[\*\-]/).count && (tmap.pos =~ /^[0-9]/).count )
            return true

        //  choose smallest base position offset from transcript if both are UTR
        //
        if ((bmap.pos =~ /^[\*\-]/).count && (tmap.pos =~ /^[\*\-]/).count )
        {
            if ( posOffset(bmap.pos) > posOffset(tmap.pos))
                return true
        }

        //  keep best by default
        //
        return false
    }

    /**
     * Extract the absolute number of bases from a genomic position
     *
     * @param pos   Genomic position
     * @return      number of bases
     */
    private static int posOffset( String pos )
    {
        def match = ( pos =~ /^[\*\-](\d+)/ )
        if ( match.count )
        {
            return Integer.parseInt(match[0][1])
        }

        return 0
    }

    /**
     * Find LRG transcript for refseq transcript (if it exists)
     *
     * @param refseq    Refseq mRNA transcript
     * @param lrgs      Map of LRG transcripts
     * @return          LRG transcript of form LRG_nnnt1:c.nnn
     */
    private static String selectLrg( String refseq, Map lrgs )
    {
        def ts = refseq.tokenize(':')
        assert ts.size() == 2, "Couldn't parse refseq transcript ${refseq}"

        if ( ts[0] in lrgs.values())
        {
            def lrg = lrgs.find { it.value == ts[0] }.key
            log.debug( "Found LRG transcript ${lrg} for ${ts[0]}" )
            return lrg + "t1:" + ts[1]
        }

        return ''
    }

    /**
     * Filter a List of HGVSc mRNA transcripts to match the preferred list of gene TSs
     * Todo: this can be simplified now that we have a Transcript table
     *
     * @param   transcripts List of transcripts to filter
     * @param   prefer      Map of preferred transcript for genes
     * @return              List of matching transcripts
     */
    private static List selectRefseq( List transcripts )
    {
        Map version  = [:]      // [base_ts: ts_version]
        Map hgvsc    = [:]      // [base_ts: ts_hgvsc]

        for ( ts in transcripts )
        {
            //  Search for refseq transcripts only eg AA_nnn.n:c\..*
            //
            def match = ( ts =~ /^([A-Z][A-Z]_\d+)\.(\d+):(.*)/ )

            if ( match.count == 1 )
            {
                def base = match[0][1]
                def ver  = match[0][2]
                def var  = match[0][3]

                //  Look for base in preferred TX list
                //
                if ( txToGene[base] )
                {
                    //  have we already seen the base ?
                    //
                    if ( version[base] && version[base] < ver)
                    {
                        //  Keep the latest transcript version
                        //
                        version[base] = ver
                        hgvsc[base]   = var
                    }

                    //  if new, keep it
                    //
                    if ( ! version[base] )
                    {
                        version[base] = ver
                        hgvsc[base]   = var
                    }
                }
                log.debug ("Matching base=$base ver=$ver ts=$ts filter=${version} ")
            }
        }

        //  Return the list of latest transcripts (reassembled from components)
        //
        return version.collect{ it.key + '.' + it.value + ':' + hgvsc[it.key] }
    }
}
