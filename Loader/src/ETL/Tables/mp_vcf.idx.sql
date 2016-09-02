create index mp_vcf_idx1 on mp_vcf(chr);
create index mp_vcf_idx2 on mp_vcf(pos);
create index mp_vcf_idx3 on mp_vcf(hgvsg);
create index mp_vcf_idx4 on mp_vcf(seqrun);
create index mp_vcf_idx5 on mp_vcf(sample);
create index mp_vcf_idx6 on mp_vcf(ens_variant);
create index mp_vcf_idx7 on mp_vcf(seqrun,sample,ens_variant);
