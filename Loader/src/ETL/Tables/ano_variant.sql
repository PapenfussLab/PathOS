/*      ano_variant table
**
**      Normalised annotation cache for genomic variants
**
**      01      Ken Doig        01-nov-14       Initial create
**      02      Ken Doig        03-jun-15       Added lookup keys
*/

create table ano_variant
(
        data_source           varchar(50)     not null,         -- eg MUT, VEP, ANV etc
        version               varchar(50)     null,             -- datasource version
        hgvsg                 varchar(500)    not null,         -- genomic variant key
        alias                 varchar(500)    null,             -- un-normalised variant
        hgvsc                 varchar(500)    null,             -- convenience search field
        hgvsp                 varchar(500)    null,             -- convenience search field
        gene                  varchar(50)     null,             -- variant gene
        classification        varchar(50)     null,             -- convenience pathogenicity field
        organism              varchar(100)    null,             -- human, mouse etc
        build                 varchar(100)    null,             -- genome build eg GRCh37
        attr                  varchar(50)     not null,         -- value attribute type
        value		      longtext        null,             -- JSON String encoded attributes
        created               datetime        not null,         -- Creation date
        id                    bigint(20)      not null auto_increment,
        primary key (id)
);
