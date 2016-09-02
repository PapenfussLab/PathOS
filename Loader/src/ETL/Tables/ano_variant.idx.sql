create index ano_variant_idx1 on ano_variant(hgvsg);
create index ano_variant_idx2 on ano_variant(data_source);
create index ano_variant_idx3 on ano_variant(alias);
create index ano_variant_idx4 on ano_variant(hgvsc);
create index ano_variant_idx5 on ano_variant(hgvsp);
create index ano_variant_idx6 on ano_variant(gene);
create index ano_variant_idx7 on ano_variant(classification);
create unique index ano_variant_idx8 on ano_variant(data_source,hgvsg,organism,build);