CREATE TABLE mp_batch
(
  seqrun			VARCHAR(50) NOT NULL,
  sample			VARCHAR(50) NOT NULL,
  isca2015			VARCHAR(3) NOT NULL,
  tumour			VARCHAR(7) NOT NULL,
  dnaconc			DOUBLE UNSIGNED NULL,
  extract_date		        VARCHAR(20)
);
