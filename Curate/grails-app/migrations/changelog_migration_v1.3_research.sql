#Run this AFTER curate has started


ALTER TABLE vcf_upload DROP FOREIGN KEY `FKC37CFDA7B976AFA2`; #


ALTER TABLE cur_variant DROP INDEX hgvsg_uniq_1456271981333;  #
ALTER TABLE cur_variant DROP INDEX variant;                   #
ALTER TABLE cur_variant DROP INDEX `grp_variant_accession`;   #

ALTER TABLE seq_variant DROP FOREIGN KEY `FK5097A4A57BAB8115`; #

ALTER TABLE seq_variant DROP INDEX `FK5097A4A57BAB8115`;       #

ALTER TABLE seq_variant DROP INDEX `seq_sample_idx`;           #


ALTER TABLE grp_variant DROP COLUMN created_by_id; #
ALTER TABLE grp_variant DROP COLUMN date_created; #
ALTER TABLE grp_variant DROP COLUMN last_updated; #
ALTER TABLE audit MODIFY variant VARCHAR(2000); #needed since we now store a rather long toString in audit's variant

#ALTER TABLE `seq_sample` ADD INDEX `sample_type` (`sample_type`);

ALTER TABLE `cur_variant` ADD UNIQUE `grpvaracc_ccid`(`grp_variant_accession`,`clin_context_id`);

DELETE FROM dblive.user_prefs_columns_shown;
DELETE FROM dblive.user_prefs_columns_hidden;
DELETE FROM dblive.user_prefs;

ALTER TABLE `dblive`.`align_stats` CHANGE COLUMN `sample_stats` `sample_stats` VARCHAR(2000) NULL DEFAULT NULL;

delete	g1.* from	ref_hgnc_genes g1, ref_hgnc_genes g2 where	g1.gene = g2.gene and		g1.id < g2.id;

ALTER TABLE `dblive`.`ref_hgnc_genes` DROP COLUMN `refseq`, DROP INDEX `ref_hgnc_genes_idx2` ;
###this is not made by grails, not sure why
###nullable field for max pm class used for sorting
ALTER TABLE seq_variant ADD COLUMN max_pm_class INT NULL;

#fix sample types
UPDATE seq_sample SET sample_type='Control' WHERE sample_type='CTRL';

UPDATE cur_variant SET grp_variant_accession=hgvsg;
UPDATE cur_variant SET grp_variant_muttyp='SNV';

# Changed RefGene from being a mapped domain to being a proper grails domain
insert	into ref_gene(version,gene,genedesc,hgncid,accession)
  (select	0,rh.gene,max(rh.genedesc),max(rh.hgncid),max(rh.accession)
   from	ref_hgnc_genes rh
   where	rh.gene not regexp '#'
   group
   by		rh.gene);

# Make Seqrun optional fields nullable
ALTER TABLE `dblive`.`seqrun`
CHANGE COLUMN `experiment` `experiment` VARCHAR(255) NULL ,
CHANGE COLUMN `library` `library` VARCHAR(255) NULL ,
CHANGE COLUMN `platform` `platform` VARCHAR(255) NULL ,
CHANGE COLUMN `readlen` `readlen` VARCHAR(255) NULL ,
CHANGE COLUMN `scanner` `scanner` VARCHAR(255) NULL ,
CHANGE COLUMN `sepe` `sepe` VARCHAR(255) NULL ;