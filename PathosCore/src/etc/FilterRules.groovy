/*  FilterRules.groovy      Path-OS filtering rules configuration
**
**  01  Ken Doig            10-Sep-13   Created
**  02  Andrew Fellowes     07-Oct-13   Changed AF to 8%, added Germline blacklist, refined consequences
**	03	Ken Doig			24-Apr-14	Restructured to map manifests to filter groups, added wildcard mappings
**  04  Ken Doig            16-May-14   Added RD Germline halopex panel
**  05  Ken Doig            05-Jun-14   Added 'NM_000314:c.802-4_802-3del' to Germline blacklist
**  06  Ken Doig            23-Jun-14   Added Haem panel group
**  07  Ken Doig            08-Aug-14   Added MP Research and Development panel
**  08  Ken Doig            10-Aug-14   Added version numbers to black lists
**  09  Ken Doig            24-Sep-14   Changed VarPanelPct threshholds
**  10  Ken Doig            24-Oct-14   Changed black lists to genomic coords (v0.995a)
**  11  Ken Doig            02-Feb-15   Added to germline black list to reflect Normalised HGVSg variants
**  12  Andrei Seleznev     26-Feb-15   Moved varDepth from top level of rules{} into each panelGroup{}
**  13  Ken Doig            10-Apr-15   Added Dawson Lab Panels
**  14  Ken Doig            25-May-15   Added a Somatic black list var
**  15  Ken Doig            24-Jul-15   Added MDS_panelval_07.07.15 as Dawson lab panel
**  16  Ken Doig            11-Aug-15   Added Super panels correctly and TruSeq_CAT panels
**  17  Ken Doig            19-Aug-15   Reallocated panel groups
**  17  Ken Doig            19-Aug-15   Made 3/5_prime_UTR_variant benign
*/


println( "Filter Configuration Ver 1.2.5")


//
//  Rule parameters to flag technical artifacts and biologically uninteresting variants
//
//  Consumed by org.petermac.pathos.curate.Filter.groovy
//
rules
{
    //	T e c h n i c a l    f i l t e r i n g
    //  ======================================

    //	No global rules defined

    //	B i o l o g i c a l    f i l t e r i n g
    //  ========================================

    //	Global minor allele frequency
    //
    gmaf      = 1			// gmaf 1%

    //	CON flag not set if ANY of these are present
    //
    inclCons  = [
                'missense_variant',
                'splice_acceptor_variant',
                'splice_donor_variant',
                'splice_region_variant',
                'stop_gained',
                'stop_lost',
                'frameshift_variant',
                'inframe_deletion',
                'inframe_insertion',
                'initiator_codon_variant',
                'coding_sequence_variant'
                ]

    //  CON flag is set if ANY of these are present and NONE of the the above are set
    //
    exclCons  = [
                'intron',
                'incomplete_terminal_codon',
                'NMD_transcript',
                'upstream',
                'downstream',
                'synonymous',
                'stop_retained',
                'non_coding',
                '5_prime_UTR_variant',
                '3_prime_UTR_variant'
                ]

    //	A s s a y    s p e c i f i c   p a n e l   p a r a m e t e r s
    //	==============================================================

    filters
    {
        //	I l l u m i n a   T r u S e q    P a n e l
        //	==========================================

        'MP ADS Somatic Production'
        {
            description = 'Illumina TruSeq panels paired for different strands'

            //	Specific manifests in Filter group
            //
            manifests =	[
                        'TruSeq_CAT_Manifest_CoreAdd_A',
                        'TruSeq_CAT_Manifest_CoreAdd_B',
                        'TruSeq_CAT_Manifest_Full_Panel_A',
                        'TruSeq_CAT_Manifest_Full_Panel_B',
                        'TruSeq_CAT_Manifest_Full_Panel_A',
                        'TruSeq_CAT_Manifest_TC0055678-CAT_Core_A',
                        'TruSeq_CAT_Manifest_TC0056453-CAT_Core_B'
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'TruSeq_CAT_.*'

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff   = 20

            //	Black list of variants
            //
            blackList =	[
                        'chr12:g.121432116G>C',	            //	NM_000545.5:c.863G>C
                        'chr12:g.121432116G>T',	            //	NM_000545.5:c.863G>T
                        'chr12:g.121432117del',	            //	NM_000545.5:c.864del
                        'chr12:g.121432124C>T',	            //	NM_000545.5:c.871C>T
                        'chr12:g.121432125del',	            //	NM_000545.5:c.872del
                        'chr3:g.41266037A>G',	            //	NM_001098209.1:c.34A>G
                        'chr3:g.41266037A>C',	            //	NM_001098209.1:c.34A>C
                        'chr9:g.80336254C>A',	            //	NM_002072.3:c.1065G>T
                        'chr2:g.212578392_212578393del',	//	NM_001042599.1:c.884-20_884-19del
                        'chr2:g.212578393_212578394insA',	//	NM_001042599.1:c.884-21_884-20insT
                        'chr2:g.212578393del',	            //	NM_001042599.1:c.884-20del
                         ]
        }

        'MP TSACP Production'
        {
            description = 'Illumina TruSeq panel for Cancer 2015 samples'

            //	Specific manifests in Filter group
            //
            manifests =	[
                        'CancerGNA10212011_170_190_Viewermanifest'
                        ]

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff   = 20

            //	Black list of variants
            //
            blackList =	[
                        'chr12:g.121432116G>C',	            //	NM_000545.5:c.863G>C
                        'chr12:g.121432116G>T',	            //	NM_000545.5:c.863G>T
                        'chr12:g.121432117del',	            //	NM_000545.5:c.864del
                        'chr12:g.121432124C>T',	            //	NM_000545.5:c.871C>T
                        'chr12:g.121432125del',	            //	NM_000545.5:c.872del
                        'chr3:g.41266037A>G',	            //	NM_001098209.1:c.34A>G
                        'chr3:g.41266037A>C',	            //	NM_001098209.1:c.34A>C
                        'chr9:g.80336254C>A',	            //	NM_002072.3:c.1065G>T
                        'chr2:g.212578392_212578393del',	//	NM_001042599.1:c.884-20_884-19del
                        'chr2:g.212578393_212578394insA',	//	NM_001042599.1:c.884-21_884-20insT
                        'chr2:g.212578393del',	            //	NM_001042599.1:c.884-20del
                         ]
        }

        'MP ILM Control Manifest'
        {
            description = 'Illumina TruSeq control panels'

            //	Specific manifests in Filter group
            //
            manifests =	[
                        'TruSeq_Custom_Amplicon_Control_Manifest_ACP1',      // TruSeq control panel
                        'truseq_custom_amplicon_control_manifest_acp3'       // TruSeq control panel
                        ]

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff   = 20

            //	Black list of variants
            //
            blackList =	[]
        }

        //	S o m a t i c   P a n e l s
        //	===========================

        'MP FLD Somatic Production'
        {
            description = 'Production somatic panel targeting approx. 20 genes'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'Somatic_Manifest_v3.4_17_07_2014'
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Somatic_Panel_Manifest.*'

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff  = 20

            //	Black list of variants
            //
            blackList = [
                        'chr17:g.37884059A>G',
                        'chr8:g.38271186T>G'
                        ]
        }

        'MP FLD Somatic Development'
        {
            description = 'Development somatic panel targeting approx. 20 genes'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'p53_amplicon_primers_manifest',
                        'Somatic_fix_R19_200313_manifest',
                        'somatic_new_Pools_13082015'
                        ]

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff  = 20

            //	Black list of variants
            //
            blackList =	['chr17:g.37884059A>G']
        }

        //	M y e l o i d   P a n e l s
        //	===========================
        //
        'MP FLD Myeloid Production'
        {
            description = 	'Production haem myeloid panels'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Myeloid_.*'


            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff  = 20

            //	Black list of variants
            //
            blackList =	[
                        'chr4:g.106156300_106156302del',
                        'chr4:g.106193919G>T',
                        'chr4:g.106196310_106196312del',
                        'chr5:g.170837525_170837526del',
                        'chr5:g.170837526del',
                        'chr7:g.148543694_148543695del',
                        'chr7:g.148543694del',
                        'chr9:g.5073690_5073691del',
                        'chr9:g.5073691del',
                        'chr9:g.5073691dup',
                        'chr20:g.31022449del',
                        'chr20:g.31024359T>C',
                        'chr20:g.31024609G>A'
                        ]
        }

        'MP FLD Myeloid Development'
        {
            description = 	'Development haem myeloid panels'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'FGFR_CALR',
                        'haem_fix',
                        'mol_haem_fix_031013',
                        'MRD_v1',
                        'MRD_v1.1_CEBPA'
                        ]

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff  = 20

            //	Black list of variants
            //
            blackList =	[
                        'chr4:g.106156300_106156302del',
                        'chr4:g.106193919G>T',
                        'chr4:g.106196310_106196312del',
                        'chr5:g.170837525_170837526del',
                        'chr5:g.170837526del',
                        'chr7:g.148543694_148543695del',
                        'chr7:g.148543694del',
                        'chr9:g.5073690_5073691del',
                        'chr9:g.5073691del',
                        'chr9:g.5073691dup',
                        'chr20:g.31022449del',
                        'chr20:g.31024359T>C',
                        'chr20:g.31024609G>A'
                        ]
        }

        //	G e r m l i n e   P a n e l
        //	===========================

        'MP FLD Germline Production'
        {
            description = 'Production germline panel for BRCA1 and 2, TP53 and PTEN'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'Germline_1164AAP12O1_manifest',
                        'Germline_v3_070213_manifest',
                        'Germline_v4-2_0603132_manifest',
                        'Germline_v4-2_0603132_with_off_targ_manifest',
                        'Germline_v4-5_100613_with_off_targ_manifest',
                        'Germline_v4-6_040913_with_off_target_manifest',
                        'Germline_v4-8_071013_with_off_target_manifest',
                        'Germline_v5_010813_with_off_target_manifest'
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Germline_.*'


            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 15

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff  = 20

            //	Black list of variants
            //
            blackList =	[
                        'chr10:g.89725304del',	            // NM_000314.4:c.*75del
                        'chr10:g.89725303_89725304del',	    // NM_000314.4:c.*74_*75del
                        'chr10:g.89720647_89720648del',	    // NM_000314.4:c.802-4_802-3del
                        'chr10:g.89720648_89720649insT',	// NM_000314.4:c.802-3dup
                        'chr10:g.89720648del',	            // NM_000314.4:c.802-3del
                        'chr13:g.32890572G>A',	            // NM_000059.3:c.-26G>A
                        'chr13:g.32907546_32907547insT',	// NM_000059.3:c.1909+22dup
                        'chr13:g.32907546del',	            // NM_000059.3:c.1909+22del
                        'chr13:g.32907546dup',			    // NM_000059.3:c.1909+22dup		BRCA2
                        'chr10:g.89725304dup',			    // NM_000314.4:c.*75dup			PTEN
                        'chr10:g.89720648dup',			    // NM_000314.4:c.802-3dup		PTEN
                        'chr13:g.32893207del',			    // NM_000059.3:c.68-7del		BRCA2
                        'chr13:g.32913055A>G',			    // NM_000059.3:c.4563A>G		BRCA2
                        'chr13:g.32915005G>C',			    // NM_000059.3:c.6513G>C		BRCA2
                        'chr13:g.32929387T>C',			    // LRG_293t1:c.7397T>C          BRCA2 ESP=97.77%
                        'chr10:g.89720749C>T',			    // NM_000314.4:c.900C>T			PTEN
                        'chr10:g.89720791A>G',			    // NM_000314.4:c.942A>G			PTEN
                        ]

        }

        //	P r o d u c t i o n   l y m p h o i d   P a n e l s
        //	===============================================
        //
        'MP FLD Lymphoid Production'
        {
            description = 	'Production haem lymphoid panels'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'MRD_v3_LYMPHOID'
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Lymphoid_.*'

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff  = 20

            //	Black list of variants
            //
            blackList =	[
                        ]
        }

        //	M o l p a t h   R & D   P a n e l s
        //	===================================

        'MP Development'
        {
            description = 'Molpath research, testing and development panels'

            //	Specific manifests in Filter group
            //
            manifests =	[
                        'TruSightTumor-FPA-Manifest',
                        'TruSightTumor-FPB-Manifest',
                        'cll_panel_29.08.14',
                        'Qiagen_Comprehensive_4_9_2014',
                        'Qiagen_Colorectal_4_9_2014',
                        '00100-1361547029_Amplicons'
                        ]

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 5

            //	Maximum percent of a variant within a panel
            //
            varPanelPct = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff   = 20

            //	Black list of variants
            //
            blackList =	[]
        }

        'MP FLD Germline Development'
        {
            description = 'Molpath research, testing and development panels'

            //	Specific manifests in Filter group
            //
            manifests =	[
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'BRCA12_.*'

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct   = 5

            //	Maximum percent of a variant within a panel
            //
            varPanelPct = 35

            //	Minimum no of reads for an amplicon variant to be present
            //
            ampCutoff   = 20

            //	Black list of variants
            //
            blackList =	[]
        }

        //	H a l o p l e x   S o m a t i c   P a n e l
        //	===========================================

        'RD Somatic Haloplex'
        {
            description = 	'R&D Panel for evaluating Haloplex targeted capture'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'halo-loi-16552-1371711646_amplicons',
                        'Melanoma_mutation_Load_Fluidigm_070514'
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Somatic_Halo_Manifest.*'

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct    = 8

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Black list of variants
            //
            blackList =	[
                        ]
        }

        //	H a l o p l e x   G e r m l i n e   P a n e l
        //	=============================================

        'RD Germline Haloplex'
        {
            description = 	'R&D Panel for germline Haloplex targeted capture'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'Haloplex_AOCS_v2_04818-1371122271_Amplicons'
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Somatic_Halo_Manifest.*'

            //	Miniumum variant read depth
            //
            varDepth  = 50

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct    = 20

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Black list of variants
            //
            blackList =	[
                        ]
        }

        //	C o m p r e h e n s i v e   C a n c e r    P a n e l
        //	====================================================

        'MP CCP Somatic Assay'
        {
            description = 'Comprehensive Cancer Panel for multi-gene capture'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'CCP_NSC_v0.1',            // Nimblegen SeqCap
                        'CCP_SSL_v0.1'             // Agilent SureSelect
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Pathology_hyb_CCP.*'

            //	Miniumum variant read depth
            //
            varDepth  = 10

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct    = 3

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Black list of variants
            //
            blackList =	[
                        ]
        }

        //	H y b r i d    C a p t u r e    P a n e l s
        //	===========================================

        'MP Germline Capture Assay'
        {
            description = 'Hybrid Germline Panel for multi-gene capture'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Pathology_hyb_FRCP.*'

            //	Miniumum variant read depth
            //
            varDepth  = 10

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct    = 3

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Black list of variants
            //
            blackList =	[
                        ]
        }

        'MP DNA Repair Capture Assay'
        {
            description = 'Hybrid DNA Repair Panel for multi-gene capture'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Pathology_hyb_DRCP.*'

            //	Miniumum variant read depth
            //
            varDepth  = 10

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct    = 3

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Black list of variants
            //
            blackList =	[
                        ]
        }

        'MP Pan Haem Capture Assay'
        {
            description = 'Hybrid Pan Haem Panel for multi-gene capture'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'Pathology_hyb_PHCP.*'

            //	Miniumum variant read depth
            //
            varDepth  = 10

            //	Minimum read depth
            //
            readDepth = 100

            //	Minimum allele frequency (percent)
            //
            allelePct    = 3

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Black list of variants
            //
            blackList =	[
                        ]
        }

        //	D a w s o n   L a b o r a t o r y    P a n e l s
        //	================================================

        'RD DL'
        {
            description = 'Dawson Lab Panel for ctDNA'

            //	Specific manifests in Filter group - Todo: deprecated
            //
            manifests =	[
                        'DL_NBG_v3.0',
                        'MDS_panelval_07.07.15'
                        ]

            //	Pattern match for wildcard matching manifests
            //
            manifestPattern = 'DL_.*'

            //	Miniumum variant read depth
            //
            varDepth  = 10

            //	Minimum read depth
            //
            readDepth = 40

            //	Minimum allele frequency (percent)
            //
            allelePct    = 20

            //	Maximum percent of a variant within a panel
            //
            varPanelPct  = 35

            //	Black list of variants
            //
            blackList =	[
                        ]
        }
    }
}

