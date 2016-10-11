CREATE TABLE ref_umdtp53
(
  fileno INT UNSIGNED NOT NULL,
  mutation_position VARCHAR(6) NOT NULL,
  exon VARCHAR(12) NOT NULL,
  codon VARCHAR(3) NOT NULL,
  wt_codon VARCHAR(3) NOT NULL,
  mutant_codon VARCHAR(6) NOT NULL,
  wt_aa VARCHAR(3) NOT NULL,
  mutant_aa VARCHAR(6) NOT NULL,
  protein_variant VARCHAR(8) NOT NULL,
  cdna_variant VARCHAR(32) NOT NULL,
  name VARCHAR(13) NOT NULL,
  atcc VARCHAR(8) NULL,
  variation_type VARCHAR(21) NOT NULL,
  event VARCHAR(14) NOT NULL,
  vartype VARCHAR(8) NOT NULL,
  complexity VARCHAR(3) NOT NULL,
  mutation_type VARCHAR(1) NOT NULL,
  cpg VARCHAR(3) NOT NULL,
  py_py_doublets VARCHAR(22) NULL,
  structure VARCHAR(46) NULL,
  domain VARCHAR(35) NULL,
  ptm VARCHAR(53) NULL,
  origin VARCHAR(33) NOT NULL,
  cancer VARCHAR(43) NULL,
  genetic_background VARCHAR(30) NOT NULL,
  tumor_type VARCHAR(5) NOT NULL,
  tumor_site VARCHAR(4) NOT NULL,
  smoking_status VARCHAR(9) NOT NULL,
  drinking VARCHAR(10) NOT NULL,
  aflatoxin_status VARCHAR(7) NOT NULL,
  radiations VARCHAR(7) NOT NULL,
  asbestos VARCHAR(7) NOT NULL,
  hepatitis_b VARCHAR(7) NOT NULL,
  papilloma VARCHAR(7) NOT NULL,
  pro72_const VARCHAR(7) NOT NULL,
  pro72_tum VARCHAR(7) NOT NULL,
  ref INT UNSIGNED NOT NULL,
  authors TEXT NOT NULL,
  year INT UNSIGNED NOT NULL,
  title VARCHAR(254) NOT NULL,
  journal VARCHAR(50) NOT NULL,
  volume VARCHAR(30) NULL,
  pages VARCHAR(16) NULL,
  medline VARCHAR(30) NULL,
  waf1_pct VARCHAR(5) NOT NULL,
  mdm2_pct VARCHAR(5) NOT NULL,
  bax_pct VARCHAR(5) NOT NULL,
  v14_3_3_s_pct VARCHAR(5) NOT NULL,
  aip_pct VARCHAR(5) NOT NULL,
  gadd45_pct VARCHAR(5) NOT NULL,
  noxa_pct VARCHAR(5) NOT NULL,
  p53r2_pct VARCHAR(5) NOT NULL,
  sample VARCHAR(57) NULL,
  exon_2 VARCHAR(3) NULL,
  exon_3 VARCHAR(3) NULL,
  exon_4 VARCHAR(3) NULL,
  exon_5 VARCHAR(3) NULL,
  exon_6 VARCHAR(3) NULL,
  exon_7 VARCHAR(3) NULL,
  exon_8 VARCHAR(3) NULL,
  exon_9 VARCHAR(3) NULL,
  exon_10 VARCHAR(3) NULL,
  exon_11 VARCHAR(3) NULL,
  pre_screening VARCHAR(3) NULL,
  sscp VARCHAR(3) NULL,
  dhplc VARCHAR(3) NULL,
  dgge VARCHAR(3) NULL,
  ihc VARCHAR(3) NULL,
  yeast_assay VARCHAR(3) NULL,
  other VARCHAR(3) NULL,
  genetic_material VARCHAR(24) NULL,
  method_of_analysis VARCHAR(26) NULL,
  tag_name VARCHAR(20) NOT NULL,
  number_of_records INT UNSIGNED NOT NULL,
  multiple_mut VARCHAR(30) NULL,
  tandem_class VARCHAR(2) NULL,
  publication_occurance INT UNSIGNED NOT NULL,
  unidentified_mutations VARCHAR(30) NULL,
  act_outliers VARCHAR(2) NULL,
  pca_outliers VARCHAR(11) NOT NULL,
  pca_score VARCHAR(2) NOT NULL,
  comment_1_frequency VARCHAR(49) NOT NULL,
  comment_2_duplicates VARCHAR(149) NOT NULL,
  comment_3_outliers VARCHAR(71) NOT NULL,
  comment_4_activity VARCHAR(108) NOT NULL,
  comment_5_event VARCHAR(135) NOT NULL,
  comment_6_splicing VARCHAR(67) NOT NULL
);