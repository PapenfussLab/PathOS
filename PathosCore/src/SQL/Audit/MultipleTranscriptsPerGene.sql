/*	Look for multiple preferrred transcripts in a gene
*/

select	distinct
		ts1.gene,
		ts1.refseq alt_refseq,
		ts2.refseq refseq
from	transcript ts1,
		transcript ts2
where	ts1.gene = ts2.gene
and		ts1.build = 'hg19'	
and		ts2.build = 'hg19'	
and		ts1.preferred = 1
and		ts2.preferred = 1
and		ts1.refseq < ts2.refseq
;