CREATE TABLE ref_cancergenes
(
  gene VARCHAR(20) NOT NULL,
  description TEXT NOT NULL,
  num_mut INT UNSIGNED NOT NULL,
  oncogene_score INT UNSIGNED NOT NULL,
  tsg_score INT UNSIGNED NOT NULL,
  gene_type VARCHAR(20) NOT NULL,
  pathway VARCHAR(100) NOT NULL,
  process VARCHAR(30) NOT NULL
);
