/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.annotate

import groovy.util.logging.Log4j
import org.petermac.util.DbConnect
import org.petermac.util.Vcf
import org.petermac.util.Vcf

/**
 * Created for PathOS.
 *
 * Description:
 *
 * This is the base class for variant annotation data sources. A data source can be a a basic TSV
 * file with a header and data columns and optional meta data describing the columns. A more
 * complex data source may involve an extract or generate process followed by a loader phase.
 *
 * User: Ken Doig
 * Date: 23-Nov-2014
 */

@Log4j
class VarDataSource extends DataSource
{
    VarDataSource( String rdb )
    {
        super( rdb )
    }

    /**
     * Load a VCF file from disk
     *
     * @param   infile  Input file name
     * @return          Vcf object of file
     */
    static Vcf loadVcf( String infile )
    {
        File inf = new File(infile)
        if ( ! inf.exists())
        {
            log.error( "File [${infile}] doesn't exist")
            return null
        }

        Vcf vcf = new Vcf( )
        vcf.load( true )
        return vcf
    }
}
