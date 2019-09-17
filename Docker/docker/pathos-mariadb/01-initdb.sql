CREATE SCHEMA IF NOT EXISTS dblive;
CREATE USER IF NOT EXISTS 'bioinformatics'@'%' IDENTIFIED BY 'pathos';
GRANT ALL ON dblive.* TO 'bioinformatics'@'%';
