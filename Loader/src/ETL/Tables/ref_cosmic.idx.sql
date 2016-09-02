-- 		ref_indexes.sql
-- 
-- 		Create Indexes for Variant DB
-- 
-- 		kdoig		01		29-Nov-12
-- 

create index ref_cosmic_idx1 on ref_cosmic(gene);
create index ref_cosmic_idx2 on ref_cosmic(hgvsc);
create index ref_cosmic_idx3 on ref_cosmic(hgvsp);
create index ref_cosmic_idx4 on ref_cosmic(cosmicid);
create index ref_cosmic_idx5 on ref_cosmic(pos);
create index ref_cosmic_idx6 on ref_cosmic(site);
create index ref_cosmic_idx7 on ref_cosmic(site_sub);
create index ref_cosmic_idx8 on ref_cosmic(hist);
create index ref_cosmic_idx9 on ref_cosmic(hist_sub);

