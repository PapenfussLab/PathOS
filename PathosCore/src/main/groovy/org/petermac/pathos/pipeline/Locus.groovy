/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j
import org.petermac.util.GATK
import org.petermac.util.Locator
import org.petermac.util.RunCommand

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Locus class for manipulating genomic regions
 *
 * User: Ken Doig
 * Date: 6-Aug-2014
 * Time: 8:00 PM
 */

@Log4j
class Locus
{
    String  chr
    private Integer startPos
    private Integer endPos

    Locator loc = Locator.instance

    /**
     * Constructor for chromosome position string chr:start-end
     * @param chrpos
     */
    Locus( String chrpos )
    {
        try
        {
            //  Try chr:pos-pos
            //
            Map pmap = HGVS.parseChrPos( chrpos )
            if ( pmap )
            {
                chr      = pmap.chr
                startPos = pmap.pos    as Integer
                endPos   = pmap.endpos as Integer
            }
            else
            {
                //  Try HGVSg formatting
                //
                pmap = HGVS.parseHgvsG( chrpos )
                if ( pmap )
                {
                    chr      = pmap.chr.replaceFirst( 'chr', '' )
                    startPos = pmap.pos    as Integer
                    endPos   = pmap.endpos as Integer
                }
                else
                {
                    log.error( "Formatting error: ${chrpos}")
                    chr      = ''
                    startPos = null
                    endPos   = null
                }
            }
        }
        catch ( Exception e )
        {
            log.error( "Number formatting error: ${chrpos} ${e}")
            chr      = ''
            startPos = null
            endPos   = null
        }

        orderPos()
    }

    /**
     * Constructor for genomic position
     *
     * @param chr
     * @param startPos
     * @param endPos
     */
    Locus( String chr, String startPos, String endPos )
    {
        try
        {
            this.chr      = chr
            this.startPos = startPos as Integer
            this.endPos   = endPos   as Integer
        }
        catch ( Exception e )
        {
            log.error( "Number formatting error: ${chr}:${startPos}-${endPos} ${e}")
            this.chr      = ''
            this.startPos = null
            this.endPos   = null
        }
        orderPos()
    }

    /**
     * Constructor for genomic position
     *
     * @param chr
     * @param startPos
     * @param endPos
     */
    Locus( String chr, Integer startPos, Integer endPos )
    {
        this.chr      = chr
        this.startPos = startPos
        this.endPos   = endPos
        orderPos()
    }

    /**
     * Order positions if wrong way round
     */
    private orderPos()
    {
        if ( this.endPos < this.startPos)
        {
            def x    = this.endPos
            this.endPos   = this.startPos
            this.startPos = x
        }
    }

    String toString()
    {
        return "${chr}:${startPos}-${endPos}"
    }

    Integer startPos() { return this.startPos }

    Integer endPos()   { return this.endPos }

    /**
     * Test for overlap of Locii
     *
     * @param reg   Region to compare
     * @return      true if overlapping
     */
    boolean overlap( Locus reg )
    {
        if ( this.startPos == null || this.endPos == null ) return false
        if ( reg.startPos  == null || reg.endPos  == null ) return false
        if ( reg.chr != this.chr )                          return false
        if ( this.startPos > reg.endPos )                   return false
        if ( this.endPos < reg.startPos )                   return false

        return true
    }

    /**
     * Test for containment of Locii
     *
     * @param reg   Region to compare
     * @return      true if contained by this locus
     */
    boolean contains( Locus reg )
    {
        if ( this.startPos == null || this.endPos == null ) return false
        if ( reg.startPos  == null || reg.endPos  == null ) return false
        if ( reg.chr != this.chr )                          return false
        if ( this.startPos <= reg.startPos && reg.endPos <= this.endPos ) return true

        return false
    }

    /**
     * Find the bases at this locus
     *
     * @return  String of bases from properties genome
     */
    String bases()
    {
        String bases = new GATK().getBases( toString())

        if ( ! bases )
        {
            log.error( "Couldn't get bases for "+toString())
        }

        return bases
    }

    /**
     * Reverse complement a String of bases
     *
     * @param bases     Bases to revcom
     * @return          Reverse complement bases
     */
    static String revcom( String bases )
    {
        // Get the complement of a DNA sequence
        // Complement table taken from http://arep.med.harvard.edu/labgc/adnan/projects/Utilities/revcomp.html
        //
        def complements = [ A:'T', T:'A', U:'A', G:'C', C:'G', Y:'R', R:'Y', S:'S', W:'W', K:'M', M:'K', B:'V', D:'H', H:'D', V:'B', N:'N' ]

        def comp = bases.toUpperCase().collect
        {
            base ->
                complements[ base ] ?: 'X'
        }.join('')

        return comp.reverse()
    }
}
