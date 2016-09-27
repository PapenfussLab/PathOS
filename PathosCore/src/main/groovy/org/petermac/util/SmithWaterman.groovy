/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.pathos.pipeline.Locus
import ssw.Aligner
import ssw.Alignment

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This class calls the Striped Smith Waterman library, a JNI accessible C/C++ library
 * for a fast SW alignment implementation. It uses a machine dependent C++ DLL which is located
 * in a parameterised directory system property 'pipeline.dll' accessed by the Locator class
 *
 * User: Kenneth Doig
 * Date: 03/04/2015
 * Time: 4:57 PM
 */

@Log4j
class SmithWaterman
{
    private static  int[][] score = new int[128][128]

    private Locator loc = Locator.instance

    SmithWaterman()
    {
        try
        {
            //System.loadLibrary("sswjni")

            //  The above class loader works but results in a runtime error when Aligner.align is called
            //  blogs suggest it's because the dynamic library is loaded more than once
            //  This low level call below bypasses this but needs an absolute path to the DLL (dynamic link library)
            //  The dllPath property of Locator class parameterises the absolute path to the directory holding the DLL
            //
            Runtime.getRuntime().load0( GroovyClassLoader.class, loc.dllPath + "libsswjni.jnilib")
        }
        catch ( UnsatisfiedLinkError e)
        {
            log.fatal(String.format("Cannot find libsswjni.jnilib DLL.\n%s", e))
            throw e;
        }

        //  Set up scoring matrix  - the 128 is a magic number for the size of the string ??
        //
        for (int i = 0; i < 128; i++)
        {
            for (int j = 0; j < 128; j++)
            {
                score[i][j] = ( i==j ? 2 : -2 )
            }
        }
    }

    /**
     * Perform SW alignment of qry to ref
     *
     * @param qry   String to search with
     * @param ref   String of reference to be searched
     * @return      Map of best alignment result
     */
    static Map align( String qry, String ref )
    {
        Alignment aln = Aligner.alignSafe( qry.getBytes(), ref.getBytes(), score, 3, 1, true )
        if ( aln == null )
        {
            log.error( "Failed to SW align qry [${qry}] to ref [${ref}]" )
            return [:]
        }

        return [ score: aln.score1, cigar: aln.cigar, refStart: aln.ref_begin1, refEnd: aln.ref_end1, qryStart: aln.read_begin1, qryEnd: aln.read_end1 ]
    }

    /**
     * Format an alignment for printing/debugging
     *
     * @param qry   Query sequence
     * @param ref   Reference sequence
     * @param res   Map of alignment result
     * @return      Map of formatted Strings [ ref: <ref>, align: <alignment symbols>, qry: <alt> ]
     */
    public static Map format( String qry, String ref, Map res )
    {
        int rps    = res.refStart
        int rpe    = res.refEnd
        int qps    = res.qryStart
        int qpe    = res.qryEnd
        def refout = ref
        def qryout = qry

        //  Unpack cigar string
        //
        String pcigar = parseCigar( res.cigar )

        //  Sanity check alignment parameters
        //
        assert pcigar.length() == rpe - rps + 1 + (pcigar =~ /I/).count
        assert pcigar.length() == qpe - qps + 1 + (pcigar =~ /D/).count

        //  insert 'I's into reference seq for each alignment insertion
        //
        refout = padSeq( refout, rps, 'I', pcigar )

        //  insert 'D's into query seq for each alignment deletion
        //
        qryout = padSeq( qryout, qps, 'D', pcigar )

        //  Create a human readable alignment String of '|'s for match, '*'s for mismatch
        //
        Map ma = matchAlign( qryout[qps..-1], refout[rps..-1], pcigar.length())
        String match = ma.match

        //  Left pad query or reference to match start of alignment
        //
        int offset = 0
        if ( rps > qps )
        {
            //  ref starts first, pad out qry String
            //
            offset = rps-qps
            qryout = ' ' * offset + qryout
            offset += qps   // alignment string may not start at beginning of qry
        }
        else
        {
            //  qry starts first, pad out ref String
            offset = qps-rps
            refout = ' ' * offset + refout
            offset += rps   // alignment string may not start at beginning of ref
        }

        //  left pad match String
        //
        match = ' ' * offset + match

        return [ ref: refout, align: match, qry: qryout, dels: (res.cigar =~ /D/).count, ins: (res.cigar =~ /I/).count, snps: ma.snps ]
    }

    /**
     * Unpack a cigar string to its full length
     *
     * @param    cigar  Cigar string eg 9M1I5M
     * @return          Unpacked String eg MMMMMMMMMIMMMMM
     */
    private static String parseCigar( String cigar )
    {
        StringBuffer unpack = new StringBuffer()
        Integer count  = 1
        String  digits = ''
        cigar.eachWithIndex
        {
            String c, int i ->
            switch (c)
            {
                case ~/[MID]/:
                    unpack.append( c * count )
                    digits = ''
                    break
                default:
                    assert c.isInteger()
                    digits = digits + c
                    count = Integer.parseInt(digits)
                    break
            }
        }

        return unpack
    }

    /**
     * Pad a sequence matching cigar String
     *
     * @param seq     Sequence to pad out with '-'
     * @param cigar   Unpacked Cigar string
     * @param padChar Character to pad
     * @return        Padded String
     */
    private static String padSeq( String seq, int ptr, String padChar, String cigar )
    {
        StringBuffer outseq = new StringBuffer(seq)

        cigar.eachWithIndex
        {
            String c, int i ->

                //  If we see a padChar, insert a '-'
                //
                if ( c == padChar )
                {
                    outseq.insert(ptr+i,'-')
                }
        }

        return outseq
    }

    /**
     * Generate match string characters
     *
     * @param q     Query String
     * @param r     Ref String
     * @return      A String of '|' for match '*' for mismatch
     */
    private static Map matchAlign( String q, String r, int len )
    {
        int snps = 0
        StringBuffer match = new StringBuffer()
        r.eachWithIndex
        {
            String c, int i ->
            if ( i < len )
            {
                if ( c == q[i] )
                {
                    match.append('|')
                }
                else
                {
                    if ( c != '-' && q[i] != '-' ) ++snps
                    match.append('*')
                }
            }
        }

        return [ match: match, snps: snps ]
    }

    /**
     * Merge a pair of aligned reads
     *
     * @param   fmt     Formatted alignment fmt = [ ref: ref, align: match, qry: qry ]
     * @return          Merged sequence
     */
    public static String mergePair( Map fmt )
    {
        StringBuffer merged = new StringBuffer()
        int len = Math.max( fmt.ref.length() as int, fmt.qry.length() as int)
        String ref = fmt.ref.padRight(len)
        String aln = fmt.align.padRight(len)
        String alt = fmt.qry.padRight(len)

        for ( p in 0..len-1 )
        {
            //  Note we may have both a ref and an alt but with no alignment marker
            //
            if ( aln[p] == '*' || (ref[p] != ' ' && alt[p] != ' ' && ref[p] != alt[p]))
                merged.append('N')      //  mismatch
            else if ( ref[p] != ' ')
                merged.append(ref[p])   //  use reference first
            else if ( alt[p] != ' ')
                merged.append(alt[p])   //  then try alt
        }

        return merged
    }

    /**
     * Find all variants in an alignment. Return position relative to start of reference sequence
     *
     * @param   fmt     Formatted alignment fmt = [ ref: ref, align: match, qry: qry ]
     * @return          List of variant Maps [pos:, ref:, alt: ]
     */
    public static List variants( Map fmt )
    {
        List vars   = []    // List of SNPs and indels found

        //  Set delta between alignment coords and genomic coords
        //  count the number of spaces at the start of ref string
        //
        int  offset = 0
        while ( fmt.ref[offset] == ' ' ) ++offset

        offset = -offset

        //  Look for an altered alignment position marked with '*'
        //
        int  ptr = fmt.align.indexOf('*')

        String  ref
        String  alt
        int reflen = fmt.ref.length()
        int altlen = fmt.qry.length()

        //  Loop through all '*' positions (mismatches) of the alignment
        //
        while ( ptr != -1 )
        {
            ref = fmt.ref[ptr]
            alt = fmt.qry[ptr]

            //  process a run of reference insertions
            //  look ahead to see if we still have a reference insert
            //
            while ( ref == '-' && (ptr+1) < reflen && fmt.ref[ptr+1] == '-' )
            {
                ++ptr
                alt = alt + fmt.qry[ptr]
            }

            //  process a run of reference deletions
            //  look ahead to see if we still have a reference delete
            //
            while ( alt == '-' && (ptr+1) < altlen && fmt.qry[ptr+1] == '-' )
            {
                ++ptr
                ref = ref + fmt.ref[ptr]
            }

            //  Adjust genomic coordinates to allow for any reference deletions
            //
            if ( ref == '-' ) offset -= alt.size()

            //  Adjust position to match genomic coords of reference sequence
            //
            int pos = ptr+offset
            if ( alt == '-' )
                pos -= ref.size() - 1

            //  Add map of variant to List
            //
            vars << newVar( pos, ref, alt, fmt.ref )

            ptr = fmt.align.indexOf('*', ptr+1)    // move along to next mutation
        }

        return vars
    }

    /**
     * Find all complex variants in an alignment. Return position relative to start of reference sequence
     *
     * @param   fmt     Formatted alignment fmt = [ ref: ref, align: match, qry: qry ]
     * @param   maxmut  Maximum size of MNP
     * @param   maxmut  Maximum size of inter mutation gap
     * @return          Variant Map [pos:, ref:, alt:, complex: true ]
     */
    public static Map complex( Map fmt, int maxmut, int interMutGap )
    {
        //  Look for an altered alignment position marked with '*'
        //
        int  ptr = fmt.align.indexOf('*')
        if ( ptr == -1 ) return [:]              // no mutations, exit

        //  Set start of complex mutations
        //  include a common base at the start
        //
        def startpos = ptr - 1
        def endpos   = ptr

        //  Set delta between alignment coords and genomic coords
        //  count the number of spaces at the start of ref string
        //
        int  offset = 0
        while ( fmt.ref[offset] == ' ' )
            ++offset

        //  Loop through all '*' positions (mismatches) of the alignment
        //
        while ( ptr != -1 )
        {
            //  Only deal with upto maxmut MNPs
            //
            if ((ptr - startpos) > maxmut ) break


            //  If the inter mutation gap is too big, reset the search
            //
            def img = ptr - endpos
            if ( img > interMutGap )
            {
                if ((startpos + 1) == endpos )
                {
                    startpos = ptr - 1      // start search again from here
                }
                else
                {
                    break   // we're done, found the first MNP in the read
                }
            }

            endpos = ptr                            // save last position so far

            ptr = fmt.align.indexOf('*', ptr+1)     // move along to next mutation
        }

        //  Exit if we only have one mutation
        //
        if ((startpos + 1) == endpos)
            return [:]

        //  Construct a new MNP variant and add to list of variants
        //
        String ref = fmt.ref[startpos..endpos]
        String alt = fmt.qry[startpos..endpos]
        ref = ref.replaceAll('-','')      // remove spacer bases
        alt = alt.replaceAll('-','')      // remove spacer bases

        //  Ignore invalid MNPs, too long, too short or containing Ns
        //
        if (    ref.contains('N')
        ||      alt.contains('N')
        ||      ref.length() > maxmut
        ||      alt.length() > maxmut
        ||      ref.length() == 0
        ||      alt.length() == 0 )
        {
            return [:]
        }

        //  Check we have a common base
        //
        if ( ref[0] != alt[0] )
        {
            log.error( "Complex mut without common start base ref:${ref} alt:${alt}")
            return [:]
        }

        //  Construct and return complex mut
        //
        Map mut =   [
                    pos:        startpos - offset,
                    ref:        ref,
                    alt:        alt,
                    complex:    true
                    ]

        return mut
    }

    /**
     * Create a new variant Map
     * @param pos
     * @param ref
     * @param alt
     * @param refseq
     * @return        Map of variant [ pos: <pos relative to ref>, ref: <ref allele>, alt: <alternative allele>]
     */
    private static Map newVar( int pos, String ref, String alt, String refseq )
    {
        //  remove any non-base gaps from formatted reference
        //
        String refs = refseq.replaceAll( /[ \-]/, '' )

        //  ref deletion: anchor with current ref base
        //
        if ( ref == '-' )
        {
            ref = refs[pos]
            alt = ref + alt
        }

        //  ref insertion: anchor with preceding ref base
        //
        if ( alt == '-' )
        {
            --pos
            alt = refs[pos]
            ref = alt + ref
        }

        return [pos: pos, ref: ref, alt: alt]
    }

    /**
     * Main for testing
     *
     * @param args
     */
    public static void main( String[] args )
    {
        def cli = new CliBuilder(
                usage: 'SmithWaterman [options] <qryseq>|file <refseq>|file',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nRun a Smith Waterman Alignment on two sequences\n')

        cli.with
                {
                    h(longOpt: 'help',          'Usage Information')
                    d(longOpt: 'debug',  args:0, 'run in debug mode')
                    r(longOpt: 'revcom', args:0, 'reverse complement the query string')
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

        //  Run SW alignment
        //
        SmithWaterman sw = new SmithWaterman()

        //  Set up list of arguments
        //
        List<String> qrylst = [ argin[0] ]
        List<String> reflst = [ argin[1] ]

        //  Check for files in arguments
        //
        def qryf = new File(argin[0])
        def reff = new File(argin[1])
        if ( qryf.exists()) qrylst = qryf.readLines()
        if ( reff.exists()) reflst = reff.readLines()

        for ( qry in qrylst )
        {
            //  Reverse complement query string if needed
            //
            if ( opt.revcom ) qry = Locus.revcom( qry )

            for ( ref in reflst )
            {
                Map res = sw.align(  qry, ref)
                Map fmt = sw.format( qry, ref, res )

                println "${res} snp=${fmt.snps} ins=${fmt.ins} dels=${fmt.dels}\nref: ${fmt.ref}\n     ${fmt.align}\nqry: ${fmt.qry} ${opt.revcom ? 'Reverse Complement' : '' }"
            }
        }
    }
}
