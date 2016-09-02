CREATE TABLE mp_audit
(
  category			VARCHAR(50) NOT NULL,
  seqrun			VARCHAR(50),
  variant 			VARCHAR(50),
  sample			VARCHAR(50),
  task			        VARCHAR(50),
  complete			VARCHAR(50),
  elapsed			int,
  software			VARCHAR(50),
  version                       VARCHAR(50),
  username			VARCHAR(50),
  description			VARCHAR(300)
);
