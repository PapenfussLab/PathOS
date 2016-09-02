/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Class to classify external datasources and strings into PLON 5 class scheme
 *
 * User: doig ken
 * Date: 03/04/2014
 * Time: 3:03 PM
 */
class Classify
{
    /**
     * Map clinvar clinical significance field to C5 scale
     *
     * @param   fld Clinvar field SIG=xxxx;
     * @return  C[15]: or xxxx
     */
    static String clinvar( String fld )
    {
        def sig = null
        def match = ( fld =~ /SIG=([\w\-]+)/ )
        if ( match.count == 1)
        {
            sig = match[0][1]
        }

        switch (sig)
        {
            case 'pathogenic':              sig = 'C5'; break
            case 'probable-pathogenic':     sig = 'C4'; break
            case 'unknown':                 sig = 'C3'; break
            case 'probable-non-pathogenic': sig = 'C2'; break
            case 'non-pathogenic':          sig = 'C1'; break
        }

        return sig
    }

    /**
     * Map lrt db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String lrt( String fld )
    {
        def cat = null

        switch (fld)
        {
            case 'D':   cat = 'C5'; break
            case 'U':   cat = 'C3'; break
            case 'N':   cat = 'C1'; break
        }

        return cat
    }

    /**
     * Map PolyPhen db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String polyphen( String fld )
    {
        def cat = null
        if ( ! fld ) return cat

        if ( fld.startsWith('benign'))            fld = 'B'       // raw category from VEP
        if ( fld.startsWith('possibly_damaging')) fld = 'P'       // raw category from VEP
        if ( fld.startsWith('probably_damaging')) fld = 'D'       // raw category from VEP

        switch (fld)
        {
            case 'D':   cat = 'C5'; break
            case 'P':   cat = 'C4'; break
            case 'B':   cat = 'C1'; break
        }

        return cat
    }

    /**
     * Map mutation taster db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String mutTaste( String fld )
    {
        def cat = null

        switch (fld)
        {
            case 'A':   cat = 'C5'; break
            case 'D':   cat = 'C4'; break
            case 'P':   cat = 'C1'; break
            case 'N':   cat = 'C1'; break
        }

        return cat
    }

    /**
     * Map mutation assessor db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String mutAssess( String fld )
    {
        def cat = null

        switch (fld)
        {
            case 'H':   cat = 'C5'; break
            case 'M':   cat = 'C4'; break
            case 'L':   cat = 'C3'; break
            case 'N':   cat = 'C1'; break
        }

        return cat
    }

    /**
     * Map sift db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String sift( String fld )
    {
        if ( ! fld ) return null

        if ( fld.startsWith('deleterious')) fld = 'D'       // raw category from VEP
        if ( fld.startsWith('tolerated'))   fld = 'T'       // raw category from VEP
        return (fld == 'D' ? 'C5' : (fld == 'T' ? 'C2' : null))
    }

    /**
     * Map fathmm db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String fathmm( String fld )
    {
        return (fld == 'D' ? 'C5' : (fld == 'T' ? 'C2' : null))
    }

    /**
     * Map meta SVM db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String metaSvm( String fld )
    {
        return (fld == 'D' ? 'C5' : (fld == 'T' ? 'C2' : null))
    }

    /**
     * Map meta LR db category to C5 scale
     *
     * @param   fld DB field
     * @return      C[15]:
     */
    static String metaLr( String fld )
    {
        return (fld == 'D' ? 'C5' : (fld == 'T' ? 'C2' : null))
    }
}
