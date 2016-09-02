CREATE TABLE mp_vcf
(
  sample VARCHAR(100) NOT NULL,
  seqrun VARCHAR(100) NOT NULL,
  panel  VARCHAR(100) NOT NULL,
  chr varchar(10) NOT NULL,                    -- CHROM
  pos INT UNSIGNED NOT NULL,                   -- POS
  id VARCHAR(50) NOT NULL,                     -- ID
  ref VARCHAR(300) NOT NULL,                   -- REF
  alt VARCHAR(300) NOT NULL,                   -- ALT
  qual VARCHAR(20) NOT NULL,                   -- QUAL
  filter VARCHAR(20) NOT NULL,                 -- FILTER
  adp INT UNSIGNED NOT NULL,                   -- ADP
  wt INT UNSIGNED NOT NULL,                    -- WT
  het INT UNSIGNED NOT NULL,                   -- HET
  hom INT UNSIGNED NOT NULL,                   -- HOM
  nc INT UNSIGNED NOT NULL,                    -- NC
  gt VARCHAR(30) NOT NULL,                     -- GT
  gq INT UNSIGNED NOT NULL,                    -- GQ
  sdp INT UNSIGNED NOT NULL,                   -- SDP
  totalreaddepth INT UNSIGNED NOT NULL,        -- DP
  rd INT UNSIGNED NOT NULL,                    -- RD
  varreaddepth INT UNSIGNED NOT NULL,          -- AD
  freq DOUBLE UNSIGNED NOT NULL,               -- FREQ
  pval VARCHAR(30) NOT NULL,                   -- PVAL
  rbq INT UNSIGNED NOT NULL,                   -- RBQ
  abq INT UNSIGNED NOT NULL,                   -- ABQ
  rdf INT UNSIGNED NOT NULL,                   -- RDF
  rdr INT UNSIGNED NOT NULL,                   -- RDR
  adf INT UNSIGNED NOT NULL,                   -- ADF
  adr INT UNSIGNED NOT NULL,                   -- ADR
  hgvsg        VARCHAR(500) NULL,              -- HGVSg
  hgvsc        VARCHAR(500) NOT NULL,          -- HGVSc
  hgvsp        VARCHAR(500) NOT NULL,          -- HGVSp
  gene         VARCHAR(500) NOT NULL,          -- gene
  lrg          VARCHAR(50)  NOT NULL,          -- lrg
  status       VARCHAR(500) NOT NULL,          -- status
  muterr       VARCHAR(500) NOT NULL,          -- muterr
  numamps      VARCHAR(50)  NULL,              -- numAmps
  amps         VARCHAR(500) NULL,              -- amps
  ampbias      VARCHAR(20)  NULL,              -- ampbias
  homopolymer  VARCHAR(10)  NULL,              -- homopolymer
  varcaller    VARCHAR(50)  NULL,              -- Identified
  ens_variant  VARCHAR(500) NOT NULL           -- Added by EtlTransform
) engine = myisam;
