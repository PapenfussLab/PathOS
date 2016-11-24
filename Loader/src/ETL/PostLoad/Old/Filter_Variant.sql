-- 		Filter_Variant.sql
--
-- 		kdd	01		28-Mar-2013	Added Ca2015 filtering
-- 		kdd	02		03-May-2013	Added germline filtering 
-- 		kdd	03		21-May-2013	Excluded suspected artifact variants
-- 		kdd	04		29-May-2013	Included all Alamut C4/C5 class variants and included gmaf < 1%
-- 		kdd	05		30-May-2013	Restructured to flag rejection rule
-- 		kdd	06		12-Jun-2013	Added all Alamut C4/C5 to Filtered
-- 		kdd	07		06-Sep-2013	Rationalised consequence filter
-- 

--
-- 	Initialise filtered and reportable column for all variants
--

update	mp_variants as var
set		var.filtered   = 'N',
		var.reportable = 'N',
		var.filterflag = 'NUL'
;

-- 
-- 		Set no filtering flag for normal, duplicate or non filtered panels
--

update	mp_variants as var
set		var.filterflag = 'NOF'
where	var.tumour = 'N'
or		var.isdup  = 'D'
or		var.panel not in (	'CancerGNA10212011_170_190_Viewermanifest',
								'Germline_v4-2_0603132_manifest',
								'NTC_fix_Germl_v4-2_EGFR_x6_manifest')
; 

--
-- 		Variant read depth >= 30 reads
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',VRD')
where	var.varreaddepth < 30
and		var.filterflag != 'NOF'
;

--
-- 		Variant damaging or deleterious
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',BEN')
where	not (var.poly_class regexp 'damaging' or var.sift_class = 'deleterious' or var.poly_class = '' or var.sift_class = '')
and		var.filterflag != 'NOF'
;

--
-- 		Variant not a polymorphism >= 1% GMAF - Global Minor Allele Frequency
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',GAF')
where	var.gmaf >= 0.01
and		var.filterflag != 'NOF'
;

--
-- 		Variant occurs >= 15% for Germline samples
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',VFR')
where	var.varfreq < 15
and		var.panel in ('Germline_v4-2_0603132_manifest','NTC_fix_Germl_v4-2_EGFR_x6_manifest')
and		var.filterflag != 'NOF'
;

--
-- 		Variant occurs >= 5% for TruSeq samples
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',VFR')
where	var.varfreq < 5
and		var.panel in ('CancerGNA10212011_170_190_Viewermanifest')
and		var.filterflag != 'NOF'
;

--
-- 		Variant occurs <= 850 times in a TruSeq sample
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',MUT')
where	var.mutcnt > 850
and		var.panel in ('CancerGNA10212011_170_190_Viewermanifest')
and		var.filterflag != 'NOF'
;

--
-- 		Exclude non protein changing consequences
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',CON')
where	var.consequence not regexp 'missense_variant|splice_acceptor_variant|splice_donor_variant|splice_region_variant|stop_gained|stop_lost|feature_elongation|feature_truncation|frameshift_variant|inframe_deletion|inframe_insertion|initiator_codon_variant'
and		var.consequence     regexp 'prime_UTR_variant|intron|incomplete_terminal_codon|NMD_transcript|upstream|downstream|synonymous|stop_retained|non_coding'
and		var.filterflag != 'NOF'
;

--
-- 		Exclude "Black list" variants
--

update	mp_variants as var
set		var.filterflag = concat(var.filterflag,',BLK')
where	var.ens_variant in (
		'12_121432116_G/C',
		'12_121432116_G/T',
		'12_121432117_G/-',
		'12_121432124_C/T',
		'12_121432125_C/-',
		'9_80336254_C/A',
		'3_41266037_A/C',
		'3_41266037_A/G')
and		var.filterflag != 'NOF'
;

--
-- 		Set reportable flag if the variant has been filtered AND curated
--

update	mp_variants
set		filterflag = ''
where	filterflag = 'NUL'
;

update	mp_variants
set		filterflag = substring_index(filterflag,'NUL,',-1)
where	filterflag regexp 'NUL'
;

update	mp_variants as var
set		var.reportable		= if( var.curated = 'Y','Y','N'),
		var.filtered		= 'Y'
where	var.filterflag    = ''
;

/*	Report on variant distribution
*
select	sum(if(filterflag regexp 'NOF',1,0)) as "Not Filtered (Panel,Duplicate,non-Tumour)",
		sum(if(filterflag regexp 'VRD',1,0)) as "Low Variant Read Depth",
		sum(if(filterflag regexp 'VFR',1,0)) as "Low Variant Frequency",
		sum(if(filterflag regexp 'BEN',1,0)) as "Benign in-silico",
		sum(if(filterflag regexp 'GAF',1,0)) as "High Global MAF",
		sum(if(filterflag regexp 'MUT',1,0)) as "High Mutation Count",
		sum(if(filterflag regexp 'CON',1,0)) as "No Coding Consequence",
		sum(if(filterflag regexp 'BLK',1,0)) as "Black Listed Variant",
		sum(if(filtered = 'Y' and panel     regexp 'Cancer',1,0)) as "Filtered Somatic",
		sum(if(filtered = 'Y' and panel not regexp 'Cancer',1,0)) as "Filtered Germline",
		sum(if(reportable = 'Y',1,0)) as "Reportable",
		sum(if(curated = 'Y',1,0)) as "Curated"
from	mp_variants
;

/*	End of Filter_Variant.sql	*/
