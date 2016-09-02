CREATE TABLE mp_alamutxml
(
  gene VARCHAR(30) NOT NULL,
  assembly VARCHAR(30) NOT NULL,
  pathogenic VARCHAR(30) NOT NULL,
  note VARCHAR(200) NOT NULL,
  vartype VARCHAR(30) NOT NULL,
  pos varchar(30) NOT NULL,
  ref VARCHAR(30) NOT NULL,
  alt VARCHAR(30) NOT NULL,
  hgvsg VARCHAR(200) NOT NULL,
  refseq VARCHAR(30) NOT NULL,
  hgvsc VARCHAR(200) NOT NULL,
  hgvsp VARCHAR(200) NOT NULL,
  variant VARCHAR(200) NOT NULL,
  classtype VARCHAR(30) NOT NULL,
  classlevel VARCHAR(30) NOT NULL,
  udate VARCHAR(30) NULL,
  utime VARCHAR(30) NULL,
  samples TEXT NULL,
  comments TEXT NULL
) engine = myisam;
