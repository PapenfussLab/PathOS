-- 		ref_cosmic_transcripts.sql
--
-- 		Cosmic transcripts
--

drop	table if exists ref_cosmic_transcripts;

create	table ref_cosmic_transcripts
as
select	gene,
		count(distinct rc.hgvsc) as cosmicvar,
		rc.accession as transcript
from	ref_cosmic as rc
group
by		rc.gene,
		rc.accession
;

create	index ref_cosmic_transcripts_idx1 on ref_cosmic_transcripts(gene);
create	index ref_cosmic_transcripts_idx2 on ref_cosmic_transcripts(transcript);