/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.util

import groovy.util.logging.Log4j
import java.util.zip.GZIPInputStream

/**
 * A class for FASTA and FASTQ file processing
 *
 * Author: Ken Doig
 * Date: 14-Mar-2015
 * Time: 9:59 AM
 */

@Log4j
class Fasta
{
    private File            ffile
    private InputStream     fafis
    private BufferedReader  br
    private boolean         isfastq  = false
    private boolean         isstream = false

    /**
     * Constructor for a disk file
     *
     * @param ffile
     * @param fastq
     */
    Fasta( File ffile, boolean fastq = false )
    {
        this.ffile = ffile
        fafis      = new FileInputStream(ffile)
        isfastq    = fastq

        if ( ! ffile.exists())
        {
            log.warn( "FASTA file doesn't exist: ${ffile}")
        }

        if ( ! fastq )
        {
            Reader decoder    = new InputStreamReader( fafis, "UTF-8")
            br = new BufferedReader(decoder);
        }
        else
        {
            InputStream gzipStream = new GZIPInputStream( fafis );
            Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
            br = new BufferedReader(decoder);
        }
    }

    /**
     * Constructor for zipped STDIN
     *
     * NOTE:
     * Some decompressors (gzip for example) will read all of the data from all1.fq.gz,
     * but others (the java GZipInputStream class for example) will not and will silently
     * finish at the the end of the first concatenated file
     *
     * @param fastq
     */
    Fasta( boolean fastq = false )
    {
        isstream = true
        isfastq  = fastq

        InputStream gzipStream = new GZIPInputStream( System.in );
        Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
        br = new BufferedReader(decoder);
    }

    /**
     * Find read length from first read
     *
     * @return      Length of read
     */
    Integer readLength()
    {
        String read = getRead()?.bases

        return read?.length()
    }

    Map readStats()
    {
        Map read1    = getRead()
        int readSize = read1.head.size() + read1.plus.size() + read1.quals.size() + read1.bases.size() + 4
        return [ readSize: readSize, readLen: read1.bases.size(), fileSize: ffile.size() ]
    }

    /**
     * Load in a FASTA or FASTQ file
     *
     * @return   List of Map entries [head: , bases: ] for FASTA
     * @return   List of Map entries [head: , bases: , quals: ] for FASTQ
     */
    List<Map> load()
    {
        if ( isfastq )
        {
            log.error( "Not supported")
            return []
        }

        return loadFasta()
    }

    /**
     * Load in a FASTA file
     *
     * @return   List of Map entries [head: , bases: ]
     */
    List<Map> loadFasta()
    {
        List   entries = []
        String line, head = '', bases = ''
        try
        {
            //  Loop through rest of file
            //
            while ((line = br.readLine()) != null)
            {
                if ( line.startsWith('>'))
                {
                    if ( bases ) entries << [head:head, bases:bases]

                    head  = line[1..-1]
                    bases = ''
                }
                else
                {
                    bases = bases + line
                }
            }
        }
        catch (IOException io)
        {
            log.error( "IO error reading File ${fafis} ${io}")
            return entries
        }

        //  Save last entry
        //
        if ( bases ) entries << [head:head, bases:bases]

        return entries
    }

    /**
     * Get a new reads from buffered reader
     *
     * @return Map of the entry
     */
    Map getRead()
    {
        String head, plus, bases, quals
        try
        {
            head  = br.readLine()       // @MISEQ1:116:000000000-ABFLD:1:1101:15570:1332 1:N:0:AACCCCTCTAGACCTA
            if ( head == null ) return null
            if ( head[0] != '@' )
            {
                log.fatal( "Unrecognised FASTQ format: ${head}")
                return null
            }

            bases = br.readLine()       // AGTC......
            plus  = br.readLine()       // +
            quals = br.readLine()       // HHIJ......
        }
        catch (IOException io)
        {
            log.error( "IO error reading STDIN ${io}")
            return null
        }

        return [ head: head, plus: plus, bases: bases, quals: quals ]
    }
}