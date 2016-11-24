/*  RefETL.groovy   ETL framework configuration
**
**  01  kdoig       07-May-13   Created
**  02  kdoig       30-Sep-13   Dbload rebuild
*/

import org.petermac.pathos.loader.EtlTransform

println( "ETL Reference Data Configuration Ver 0.997.1")

//
//  Set database and ETL directory based on environment values at config load
//
switch (environment)
{
  case 'test':
    dbname = "mp_test"
    etldir = '.'
    break
  case 'prod':
    dbname = "mp_prod"
    etldir = '.'
    break
  default:
    dbname = "mp_test"
    etldir = '.'
}

//
//  Extract data files to load
//
extract
        {
            indir       = 'Manual'
            include     = '**/ref_*.tsv'
            outdir      = 'Raw'
        }

//
//  Transform raw data into format for loading into Database
//
transform
        {
            def et = new EtlTransform(environment)

            indir   = 'Raw'
            include = '**/*.tsv'
            outdir  = 'Staging'

            ref_clinvar
            {
                def tsv = "ref_clinvar.tsv"
                transform = { et.ref_clinvar( new File( "Raw", tsv), new File( "Staging", tsv))}
            }

            ref_emory
            {
                def tsv = "ref_emory.tsv"
                transform = { et.ref_emory( new File( "Raw", tsv), new File( "Staging", tsv))}
            }

            ref_kconfab
            {
                def tsv = "ref_kconfab.tsv"
                transform = { et.ref_kconfab( new File( "Raw", tsv), new File( "Staging", tsv))}
            }

            ref_bic
            {
                def tsv = "ref_bic.tsv"
                transform = { et.ref_bic( new File( "Raw", tsv), new File( "Staging", tsv))}
            }

            ref_iarc
            {
                def tsv = "ref_iarc.tsv"
                transform = { et.ref_iarc( new File( "Raw", tsv), new File( "Staging", tsv))}
            }
        }


//
//  Tables to load into database
//
load
        {
            indir     = 'Staging'
            createdir = 'Tables'
            tables = [
                    'ref_bic',
                    'ref_cosmic',
                    'ref_cancergenes',
                    'ref_clinvar',
                    'ref_emory',
                    'ref_hgnc_genes',
                    'ref_lrg',
                    'ref_ucscgene',
                    'ref_exon',
                    'ref_iarc',
                    'ref_kconfab'
                    ]
        }

//
//	Post processing after loading
//
postload
        {
            scriptdir = 'PostLoad'

            //  Scripts to be run after data loaded
            //
            scripts = [ ]
        }
