/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import org.petermac.pathos.pipeline.UrlLink

class TestDomain
{
    String  pos    = "7:140453142"
    String  seqrun = "120823_M00139_0012_A000000000-A1H8J"
    String  sample = "11M0444"
    String  igvURL = "http://localhost:60151/load?file=http://bioinf-ensembl.petermac.org.au/Pathology/${seqrun}/${sample}/IGV_Session.xml&amp;locus=${pos}"
    String  cosmicURL = UrlLink.cosmic("") //"http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=571"
    String  reportDocURL  = '' // UrlLink.reportDoc( "/pathology/NGS/PathOS", "12M1839")
    String  reportHtmlURL = '' //UrlLink.reportHtml("/pathology/NGS/PathOS","12M1839")
    String  dbsnpURL  = "http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=7688609"
    String  pubmedURL  = "http://www.ncbi.nlm.nih.gov/pubmed/7485115"
    String  alamutURL  = "http://localhost:10000/show?request=${pos}"
    String  ucscURL  = "http://genome.ucsc.edu/cgi-bin/hgTracks?position=chr${pos}"

    static constraints =
        {
            pos()
            sample()
            seqrun()
            igvURL(    url: false, nullable: true)
            cosmicURL( url: true,  nullable: true)
            dbsnpURL(  url: true,  nullable: true)
            reportDocURL( urL: false)
            reportHtmlURL( url: false )
            pubmedURL( urL: true)
            alamutURL( urL: true)
            ucscURL( urL: true)
        }
}
