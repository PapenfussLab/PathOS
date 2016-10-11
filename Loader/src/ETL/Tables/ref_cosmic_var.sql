CREATE TABLE ref_cosmic
(
  gene VARCHAR(30) NOT NULL,
  strand VARCHAR(1) NULL,
  transcript VARCHAR(30) NOT NULL,
  hgvsc VARCHAR(200) NOT NULL,
  hgvsp VARCHAR(200) NULL,
  muttyp VARCHAR(200) NOT NULL,
  cosmic INT UNSIGNED NOT NULL,
  somatic_sts VARCHAR(200) NOT NULL,
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (id)
);