/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.pipeline

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Normalise sample name to match Detente style name for Patient sample matching
 *
 * User: doig ken
 * Date: 10/02/2014
 * Time: 9:51 AM
 */

class SampleName
{
    /**
     * Normalise sample name to match Detente style name for Patient sample matching
     *
     * @param   sample  Sample name to normalise
     * @return          Sample name for match with Detente Patient records
     */
    static String  normalise( String sampleName )
    {
        String sn = sampleName

        //  Test for Detente style sample name nn[KM]nnnn
        //
        if ( sampleName =~ /^\d\d[mkMK]\d\d\d\d.*/ )
        {
            //  Uppercase and strip off trailing suffix
            //
            sn = sampleName.toUpperCase().substring( 0, 7 )
        }

        return sn
    }

    /**
     * Is sample a positive control sample eg non template control
     *
     * @param   sampleName  Sample name to test
     * @return              true if positive control
     */
    static boolean isPosControl( String sampleName )
    {
        if ( sampleName.toUpperCase() =~ /.*NA12878.*/ )    return true
        if ( sampleName.toUpperCase() =~ /.*CTRL.*/ )       return true
        if ( sampleName.toUpperCase() =~ /.*CONTROL.*/ )    return true

        return false
    }

    /**
     * Is sample a negative control sample eg non template control
     *
     * @param   sampleName  Sample name to test
     * @return              true if negative control
     */
    static boolean isNegControl( String sampleName )
    {
        return (sampleName.toUpperCase() =~ /^NTC.*/)
    }
}