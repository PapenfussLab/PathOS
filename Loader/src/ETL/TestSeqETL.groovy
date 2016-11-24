/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

/*  ETL.groovy      ETL framework configuration
**
**  01  kdd         07-may-13   Created
*/

//
//	SeqETL.groovy
//
//	Groovy config file with parameters for controlling the ETL framework database loader
//
//	Usage:  LoadPathOS -c SeqETL.groovy
//
//	04	kdoig	28-feb-2014
//

import org.petermac.pathos.loader.EtlTransform
import org.petermac.util.Locator

println( "TestSeqETL Configuration 0.997.1" )

staticDir   = 'Manual'      //  Static data tables
rawDir      = 'Raw'         //  Raw data from extract phase
stagingDir  = 'Staging'     //  Staging area prior to database load
sqlTableDir = 'Tables'      //  SQL table definitions

//
//  Extract data files to load
//	Default operation is to copy and strip off any '#' comments out of file
//
extract
        {
            Locator loc = Locator.instance
            def samples = loc.samDir                    // root of all sequencing data
            def panels  = loc.panelDir                  // root of all panel data

            indir       = staticDir
            include     = 'mp_*.tsv'
            outdir      = rawDir

            mp_audit
            {
                extractor = 'mergeFiles'
                indir     = samples
                header    = false                       // no header in audit files
                include   = '*/*/*.audit'
            }

//            mp_seqrun
//            {
//                extractor = 'seqrun'
//            }

            mp_alignstats
            {
                extractor = 'mergeFiles'
                indir     = samples
                include   = '*/*/*.stats.tsv'
            }

            mp_amplicon
            {
                extractor = 'mergeFiles'
                indir     = panels
                include   = '*/Amplicon.tsv'
            }

            mp_roi
            {
                extractor = 'mergeFiles'
                indir     = panels
                include   = '*/ROI.tsv'
            }

//            mp_detente
//            {
//                extractor = 'loadDetente'
//                indir     = '/pmc-qmi/Molpathsql'
//            }

            mp_vcf
            {
                extractor  = 'mergeFiles'
                indir      = samples
                include    = '*/*/*.vcf.tsv.mut'
            }

            mp_vep
            {
                extractor = 'mergeFiles'
                indir     = samples
                include   = '*/*/*.vep.tsv.mut'
            }
        }

//
//  Transform raw data into format for loading into Database
//	Default operation is to copy and strip off any '#' comments out of file
//
transform
        {
            def et = new EtlTransform(environment)

            indir   = rawDir
            include = '**/*.tsv'
            outdir  = stagingDir

            mp_vep
            {
				def tsv = "mp_vep.tsv"
                transform = { et.mp_vep( new File( "Raw", tsv), new File( "Staging", tsv))}
            }

            mp_vcf
            {
				def tsv = "mp_vcf.tsv"
                def anv = "mp_annovar.tsv"
                transform = { et.mp_vcf( new File("Raw",tsv), new File(stagingDir,tsv), new File(stagingDir,anv))}
            }

            mp_mutdesc
            {
				def tsv = "mp_mutdesc.tsv"
                transform = { et.mp_mutdesc( new File( "Raw", tsv), new File( "Staging", tsv))}
			}

        }

//
//  Tables to load into database
//
load
        {
            indir     = stagingDir
            createdir = sqlTableDir
            tables = [
                    'mp_alignstats',
                    'mp_annovar',
                    'mp_audit',
                    'mp_batch',
                    'mp_tumourtype',
                    'mp_curated',
                    'mp_detente_tests',
                    'mp_detente',
                    'mp_genedesc',
                    'mp_mutdesc',
                    'mp_seqrun',
                    'mp_vcf',
                    'mp_vep',
                    'mp_amplicon',
                    'mp_roi']
        }

//
//	Post database load stitch up
//
postload
        {
            scriptdir = 'PostLoad'

            //  Scripts to be run after data loaded
            //

            scripts =   []

            if ( environment == 'merge' )
            {
            }

            if ( environment == 'rebuild' )
            {
            }
        }
