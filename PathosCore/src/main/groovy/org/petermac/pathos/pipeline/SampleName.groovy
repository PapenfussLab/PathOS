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
     * Get the base name for a sample, * stripping off replicate numbers if they exist.
     * Note that a tumour-normal sample is a base name, since it's conceivable for a
     * tumour-normal sample to have replicates.
     *
     * @param   sampleName is the name of the sample
     * @return  the base name of the sample.
     */
    static String baseName(String sampleName) {
        if ( ! sampleName ) return null
        sampleName = sampleName.toUpperCase().trim()

        //  replicate samples have a -numeric suffix
        //
        def match = ( sampleName =~ /(.*)-\d+$/ )
        if ( match.count == 1)
        {
            //  return sampleName without -n suffix
            //
            return match[0][1]
        }

        return sampleName
    }

    /**
     * Normalise and decompose a tumour-normal name into it's constituent parts.
     * If the sample is not a tumour-normal sample name, returns null.
     *
     * @param sampleName is the name of the sample.
     * @return the constituent parts of the name, or null if it isn't a tumour-normal name.
     */
    static List<String> decomposeTumourNormal(String sampleName) {
        List<String> parts = sampleName
            .split('--')
            .collect({ String sample ->
                return baseName(sample)
            })

        if (parts.size() == 2) {
            return parts
        }
        return null
    }

    /**
     * Return the implied PatSample name.
     * @param sampleName is the SeqSample name.
     * @return the baseName for atomic samples, or the implied tumour sample name for TN samples.
     */
    static String impliedPatientSampleName(String sampleName) {
        List tn = decomposeTumourNormal(sampleName)
        if (tn) {
            return tn[0]
        }
        return baseName(sampleName)
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


    /**
     * Replace non alphnum chars and '_' in sample name with '-'
     *
     * @param   sample  Raw sample name
     * @return          Sample with whitespace and '_' replaced with '-'
     */
    static String clean( String sample )
    {
        if ( sample )
            return sample.replaceAll( /[\W_]/, '-')

        return null
    }
}
