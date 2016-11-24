/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */



package org.petermac.pathos.pipeline

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 12/04/13
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */

import groovy.util.logging.Log4j
import org.petermac.pathos.pipeline.HGVS
import org.petermac.util.DbConnect
import org.petermac.util.Tsv

@Log4j
class MakeBed
{
    static def sql = null

    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'MakeBed [options] in.tsv out.bed',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nConvert a variant file to BED format\n')

        cli.with
                {
                    h(longOpt: 'help',      required: false, 'Usage Information' )
                    t(longOpt: 'fileType',  args: 1,  required: true, 'Input file type [genomic,primer,exon,exonref]' )
                    p(longOpt: 'padding',   args: 1, 'Padding size for exon [600bp]' )
                    o(longOpt: 'primer',    'Only output primers' )
                    a(longOpt: 'amplicon',  'Only output amplicons' )
                    r(longOpt: 'rdb',       args: 1, 'RDB to use (eg demo,mp_webtest)' )
                    i(longOpt: 'roi',       args: 1, 'Region of interest TSV file' )
                }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h || opt.arguments().size() != 2)
        {
            cli.usage()
            return
        }

        //  Open DB if needed
        //
        if ( opt.fileType == 'exon' || opt.fileType == 'exonref' )

        {
            if ( opt.rdb )
            {
                def db = new DbConnect( opt.rdb )
                sql = db.sql()
            }
            else
            {
                log.fatal( "Missing RDB option")
                return
            }
        }

        //  Run the program
        //
        log.info("MakeBed " + args )

        //  Extract file names
        //
        List<String> extra = opt.arguments()
        def inFile  = extra[0]
        def bedFile = extra[1]

        def nlines = 0

        if ( opt.fileType == 'genomic' )
            nlines = genomicBed( inFile, bedFile )

        if ( opt.fileType == 'primer' )
            nlines = primerBed( inFile, bedFile, opt.primer, opt.amplicon, opt.roi )

        if ( opt.fileType == 'exon' )
            nlines = exonBed( inFile, bedFile, opt.padding ? opt.padding as int : 0 )

        if ( opt.fileType == 'exonref' )
            nlines = exonRef( inFile, bedFile, opt.padding ? opt.padding as int : 0 )

        log.info("Done, processed ${nlines} lines")
    }

    /**
     * Process a primers files and convert to a BED file
     *
     * @param inname    Primers input file
     * @param outname   BED file for output
     * @param primer    Only output primers
     * @param amplicon  Only output amplicons
     * @return          No of lines converted
     */
    static Integer primerBed( String inname, String outname, boolean primer, boolean amplicon, String roiname )
    {
        def inFile  = new File( inname  )
        if ( ! inFile.exists())
        {
            log.error("Primer file doesn't exist: " + inname)
            return 0
        }

        def bedFile = new File( outname )
        bedFile.delete()

        File roiFile = null
        if ( roiname )
        {
            roiFile = new File( roiname )
            if ( ! roiFile.exists())
            {
                log.error("ROI file doesn't exist: " + roiname)
                return 0
            }
        }

        return makeBed( inFile, bedFile, primer, amplicon, roiFile )
    }

    /**
     * Process a primers files and convert to a BED file
     *
     * @param inname    Primers input file
     * @param outname   BED file for output
     * @param primer    Only output primers
     * @param amplicon  Only output amplicons
     * @return          No of lines converted
     */
    static Integer makeBed( File inFile, File bedFile, boolean primer, boolean amplicon, File roiFile )
    {

        //  Load ROI file if available
        //  ROI format gene,exon,chr,start,finish,description
        //
        Tsv roi = null
        if ( roiFile )
        {
            roi = new Tsv( roiFile)

            def nlines  = roi.load( true )
            if ( ! nlines )
            {
                log.error("Empty ROI file: " + roiFile.absolutePath)
                return 0
            }
            if ( roi?.cols?.size() != 7 )
            {
                log.error("Invalid ROI file, need columns: panel,gene,exon,chr,start,finish,description: " + roiFile.absolutePath)
                return 0
            }
        }

        //  Parse basic fields as TSV file
        //
        def nlines = 0

        inFile.splitEachLine('	')
        {
            line ->
                nlines++

                //  Output as bed file
                //
                //  header format:
                //  track name=AA_somatic_140813 description="Somatic Amplicons 14.08.13" visibility=2 itemRgb="On"
                //

                //  output BED header
                //
                if ( nlines == 1 )
                    bedFile << "track name=${inFile.name} description=\"Generated from ${inFile.name} by MakeBed.groovy\" visibility=2 itemRgb=\"On\"\n"

                if ( line.size() != 4 )
                {
                    log.error( "Invalid number of columns for file ${inFile} [${line.size()}]")
                    return nlines
                }

                //  Parse primer file
                //  10:89624142-89624321	23	23	PTEN_1_1
                //
                def pos      = line[0]
                def prim1    = line[1]
                def prim2    = line[2]
                def name     = line[3]

                Map pmap = HGVS.parseChrPos( pos )

                if ( pmap == [:] || ! prim1.isInteger() || ! prim2.isInteger())
                {
                    log.error( "Invalid format of line [${line}]")
                    return nlines
                }

                //  Bed columns
                //
                Map bedp = [
                            chrom: pmap.chr,
                            name: name.replaceAll(' ','_'),
                            score: 0,
                            strand: '+',
                            rgb: '217,95,14'
                            ]

                if ( name =~ /[Oo]ff_target/ ) bedp.rgb = '255,0,0'

                //  Output both Amplicons and primers
                //
                if ( ! primer && ! amplicon )
                {
                    int chromStart = (pmap.pos as int)-1
                    int chromEnd   = pmap.endpos+2
                    int endBlock = pmap.endpos+1 - pmap.pos - (prim2 as int)

                    bedp <<         [
                            chromStart: chromStart,
                            chromEnd: chromEnd,
                            thickStart: chromStart,
                            thickEnd: chromEnd,
                            blockCount: 2,
                            blockSizes: "${prim1},${prim2}",
                            blockStarts: "0,${endBlock}"
                    ]

                    bedFile << bedLine( bedp ) + "\n"
                }

                //  Output only Amplicons
                //
                if ( amplicon )
                {
                    int chromStart = (pmap.pos as int) + (prim1 as int) - 1
                    int chromEnd   = pmap.endpos - (prim2 as int)
                    int len        = chromEnd - chromStart + 1

                    bedp <<         [
                                    chromStart: chromStart,
                                    chromEnd: chromEnd,
                                    thickStart: chromStart,
                                    thickEnd: chromEnd,
                                    blockCount: 1,
                                    blockSizes: "${len}",
                                    blockStarts: "0"
                                    ]

                    bedFile << bedLine( bedp ) + "\n"
                }

                //  Output only primers
                //
                if ( primer && ! amplicon )
                {
                    //  Primer 1
                    //
                    int chromStart = (pmap.pos as int)-1
                    int chromEnd   = chromStart + (prim1 as int)

                    bedp <<         [
                                    name: name.replaceAll(' ','_') + '_5p',
                                    chromStart: chromStart,
                                    chromEnd: chromEnd,
                                    thickStart: chromStart,
                                    thickEnd: chromEnd,
                                    blockCount: 1,
                                    blockSizes: "${prim1}",
                                    blockStarts: "0"
                                    ]

                    bedFile << bedLine( bedp ) + "\n"

                    //  Primer 2
                    //
                    chromStart = pmap.endpos - (prim2 as int)
                    chromEnd   = pmap.endpos

                    bedp <<         [
                                    name: name.replaceAll(' ','_') + '_3p',
                                    chromStart: chromStart,
                                    chromEnd: chromEnd,
                                    thickStart: chromStart,
                                    thickEnd: chromEnd,
                                    blockCount: 1,
                                    blockSizes: "${prim2}",
                                    blockStarts: "0"
                                    ]

                    bedFile << bedLine( bedp ) + "\n"
                }

        }

        //  Append ROI lines
        //  ROI format gene,exon,chr,start,finish,description
        //
        if ( roi )
        {
            List<List> rows = roi.tsvMap.rows
            for ( List<String> row in rows )
            {
                long    chromStart = row[4] as long
                long    chromEnd   = row[5] as long
                def     strand = '+'
                if ( chromEnd < chromStart )
                {
                    strand = '-'
                    chromStart = row[5] as long
                    chromEnd   = row[4] as long
                }

                //  Set ROI name
                //
                String name = "ROI_" + row[6].replaceAll(' ','_')

                //  Bed columns
                //
                Map bedp = [
                        chrom:      row[3],
                        name:       name,
                        score:      0,
                        strand:     strand,
                        rgb:        '94,217,14',
                        chromStart: chromStart-1,
                        chromEnd:   chromEnd,
                        thickStart: chromStart-1,
                        thickEnd:   chromEnd
                ]

                bedFile << bedLine( bedp ) + "\n"
            }
        }

        return nlines
    }

    /**
     * Construct a BED file line from a Map of parameters
     *
     * @param params    Map of bed parameters
     * @return          Bed line
     */
    static String bedLine( Map p )
    {
        //  Format with flanking primers
        //                chr12     chrom
        //                25380204	chromStart (1 offset)
        //                25380372  chromEnd
        //                KRAS_Ex3  name
        //                0         score
        //                +         strand
        //                25380204  thickStart
        //                25380372  thickEnd - The starting position at which the feature is drawn thickly
        //                          (for example, the start codon in gene displays)
        //                0,255,0   itemRgb
        //                2         blockCount - The number of blocks (exons) in the BED line
        //                24,22     blockSizes - A comma-separated list of the block sizes.
        //                          The number of items in this list should correspond to blockCount.
        //                0,146     blockStarts- A comma-separated list of block starts.
        //                          All of the blockStart positions should be calculated relative to
        //                          chromStart. The number of items in this list should correspond to blockCount.

        List flds = []
        flds.add(p.chrom)
        flds.add(p.chromStart)
        flds.add(p.chromEnd)
        flds.add(p.name)
        flds.add(p.score)
        flds.add(p.strand)
        if ( p.blockCount > 1 )
        {
            flds.add(p.thickStart)
            flds.add(p.thickEnd)
            flds.add(p.rgb)
            flds.add(p.blockCount)
            flds.add(p.blockSizes)
            flds.add(p.blockStarts)
        }

        return flds.join('\t')
    }

    /**
     * Convert genomic coords into a BED file
     *
     * @param inname    Input file with HGVSg in first column
     * @param outname   BED file to output
     * @return          No of lines converted
     */
    static Integer genomicBed( String inname, String outname )
    {
        def inFile  = new File( inname  )
        def bedFile = new File( outname )

        if ( ! inFile.exists())
        {
            log.error("Input file doesn't exist: " + inFile.name)
            return 0
        }

        bedFile.delete()

        //  Parse basic fields as TSV file
        //
        def nlines = 0

        inFile.splitEachLine('	')
        {
            line ->
                nlines++

                //  output BED header
                //
                if ( nlines == 1 )
                    bedFile << "track name=${inFile.name} description=\"Generated from ${inFile.name} by MakeBed.groovy\" visibility=2 itemRgb=\"On\"\n"

                if ( line.size() != 2 )
                {
                    log.error( "Invalid number of columns for file ${inFile} [${line.size()}]")
                    return nlines
                }

                //  Parse genomic HGVS
                //
                def gmap = HGVS.parseHgvsG(line[0])

                //  Bed columns
                //
                List cols = [ gmap.chr, (gmap.pos as int)-1, gmap.endpos, line[1].replaceAll(' ','_'), '+' ]
                bedFile << cols.join("	") + "\n"
         }

        return nlines
    }

    /**
     * Convert a list of genes into a BED file of exons
     *
     * @param inname    Input file of genes
     * @param outname   BED file to output
     * @param padding   Minimum size of exon to output (exon will be padded to this size if needed)
     * @return          No of lines converted
     */
    static Integer exonBed( String inname, String outname, int padding = 0 )
    {
        def inFile  = new File( inname  )
        def bedFile = new File( outname )

        if ( ! inFile.exists())
        {
            log.error("Input file doesn't exist: " + inFile.name)
            return 0
        }

        bedFile.delete()

        //  Parse basic fields as TSV file
        //
        def nlines = 0

        inFile.splitEachLine('	')
        {
            line ->
                nlines++

                //  output BED header
                //
                if ( nlines == 1 )
                    bedFile << "track name=${inFile.name} description=\"Generated from ${inFile.name} by MakeBed.groovy\" visibility=2 itemRgb=\"On\"\n"

                def gene = line[0]

                //  Setup gene query
                //
                def qry = 	"""
                    select	ug.*
                    from	ref_ucscgene ug,
                    		ref_hgnc_genes hg
                    where	ug.gene = ${gene}
                    and		ug.gene = hg.gene
                    and		ug.refseq = hg.refseq
                    """
                def row   = sql.firstRow( qry )

                log.info( "Processing ${gene} ${row?.refseq} exons ${row?.exonCount}" )

                def starts = (row.exonStarts as String).split(',')
                def ends   = (row.exonEnds   as String).split(',')

                //  Process each exon (padding if necessary)
                //
                for ( int exon in 0..row.exonCount-1 )
                {
                    long start = starts[exon] as long
                    long end   = ends[exon]   as long
                    def len   = end - start
                    if ( len < padding )
                    {
                        long mid = (start + end) / 2
                        start = mid - padding / 2
                        end   = mid + padding / 2
                    }

                    //  Bed columns
                    //
                    def displayEx = row.strand == '+' ? exon + 1 : row.exonCount - exon
                    List cols = [ row.chr, start, end, "${gene}_ex${displayEx}", row.strand ]
                    bedFile << cols.join("	") + "\n"
                }
        }

        return nlines
    }

    /**
     * Convert a list of genes into a TSV file of exon parameters
     *
     * @param inname    Input file of genes or 'all'
     * @param outname   TSV file to output suitable for loading into PathOS (eg ref_exon.tsv)
     * @param padding   Extra bases padding around exon if needed
     * @return          No of lines converted
     */
    static Integer exonRef( String inname, String outname, int padding = 0 )
    {
        def inFile  = new File( inname  )
        def refFile = new File( outname )

        if ( ! inFile.exists())
        {
            log.error("Input file doesn't exist: " + inFile.name)
            return 0
        }

        refFile.delete()

        //  Parse basic fields as TSV file
        //
        def nlines = 0

        inFile.splitEachLine('	')
        {
            line ->
                nlines++

                //  output ref header
                //
                if ( nlines == 1 )
                {
                    refFile << "##\tGenerated from ${inFile.name} by MakeBed.groovy\n#"
                    refFile <<  [
                                'gene',
                                'refseq',
                                'exon',
                                'strand',
                                'idx',
                                'exonStart',
                                'exonEnd',
                                'exonFrame'
                                ].join('\t') + "\n"
                }

                def gene = line[0]

                //  Setup gene query
                //
                def qry = 	"""
                            select	ug.*
                            from	ref_ucscgene ug
                            where	ug.gene = ${gene}
                            """
                def rows   = sql.rows( qry )

                for ( row in rows )
                {
                    log.info( "Processing ${gene} ${row?.refseq} exons ${row?.exonCount}" )

                    def starts = (row.exonStarts as String).split(',')
                    def ends   = (row.exonEnds   as String).split(',')
                    def frames = (row.exonFrames as String).split(',')

                    //  Process each exon (padding if necessary)
                    //
                    for ( int exon in 0..row.exonCount-1 )
                    {
                        long start = starts[exon] as long
                        long end   = ends[exon]   as long
                        long frame = frames[exon] as long

                        if ( padding )
                        {
                            start -= padding
                            end   += padding
                        }

                        //  Ref columns (reverse count if on opposite strand)
                        //
                        def displayEx = row.strand == '+' ? exon + 1 : row.exonCount - exon

                        List cols = [
                                    row.gene,
                                    row.refseq,
                                    "ex${displayEx}/${row.exonCount}",
                                    row.strand,
                                    displayEx,
                                    start,
                                    end,
                                    frame
                                    ]

                        refFile << cols.join("\t") + "\n"
                    }
                }
        }

        return nlines
    }
}
