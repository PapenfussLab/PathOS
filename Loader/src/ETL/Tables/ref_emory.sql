CREATE TABLE ref_emory
(
  egl_id INT UNSIGNED NOT NULL,
  gene VARCHAR(30) NOT NULL,
  exon VARCHAR(30) NOT NULL,
  egl_variant_id INT UNSIGNED NOT NULL,
  egl_variant VARCHAR(200) NOT NULL,
  egl_protein VARCHAR(30) NULL,
  egl_classication VARCHAR(30) NULL,
  egl_classification_date VARCHAR(30) NOT NULL,
  variant VARCHAR(200) NOT NULL,
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (id)
);
