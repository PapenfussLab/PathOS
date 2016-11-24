#synthax:
#ALTER TABLE tbl_magazine_issue
#DROP FOREIGN KEY FK_tbl_magazine_issue_mst_users

ALTER TABLE grp_variant DROP FOREIGN KEY FK2955932BC6CECA17;
ALTER TABLE tag_links DROP FOREIGN KEY FK7C35D6D45A3B441D;
ALTER TABLE vcf_upload DROP FOREIGN KEY FKC37CFDA7B976AFA2;
DROP INDEX hgvsg_uniq_1456271981333 ON cur_variant;
DROP INDEX variant ON cur_variant;

ALTER TABLE grp_variant DROP COLUMN created_by_id;
ALTER TABLE grp_variant DROP COLUMN date_created;
ALTER TABLE grp_variant DROP COLUMN last_updated;
ALTER TABLE pat_sample DROP COLUMN mut_context;
ALTER TABLE audit MODIFY variant VARCHAR(999);