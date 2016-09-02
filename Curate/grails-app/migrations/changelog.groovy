databaseChangeLog = {

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-1") {
		createTable(tableName: "keyword") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "keywordPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "keyword", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "pmid", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-2") {
		modifyDataType(columnName: "location", newDataType: "varchar(255)", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-3") {
		dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "location", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-4") {
		modifyDataType(columnName: "panel_name", newDataType: "varchar(255)", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-5") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "panel_name", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-6") {
		modifyDataType(columnName: "sample_name", newDataType: "varchar(255)", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-7") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "sample_name", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-8") {
		addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-9") {
		modifyDataType(columnName: "endpos", newDataType: "varchar(255)", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-10") {
		modifyDataType(columnName: "primerlen1", newDataType: "varchar(255)", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-11") {
		modifyDataType(columnName: "primerlen2", newDataType: "varchar(255)", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-12") {
		modifyDataType(columnName: "startpos", newDataType: "varchar(255)", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-13") {
		addNotNullConstraint(columnDataType: "bigint", columnName: "version", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-14") {
		modifyDataType(columnName: "evidence_justification", newDataType: "varchar(8000)", tableName: "cur_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-15") {
		modifyDataType(columnName: "report_desc", newDataType: "varchar(8000)", tableName: "cur_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-16") {
		modifyDataType(columnName: "justification", newDataType: "varchar(8000)", tableName: "evidence")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-17") {
		modifyDataType(columnName: "path_comments", newDataType: "varchar(3000)", tableName: "pat_sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-18") {
		modifyDataType(columnName: "path_morphology", newDataType: "varchar(3000)", tableName: "pat_sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-19") {
		modifyDataType(columnName: "rep_morphology", newDataType: "varchar(3000)", tableName: "pat_sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-20") {
		modifyDataType(columnName: "slide_comments", newDataType: "varchar(3000)", tableName: "pat_sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-21") {
		modifyDataType(columnName: "exonEnd", newDataType: "varchar(255)", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-22") {
		modifyDataType(columnName: "exonFrame", newDataType: "varchar(255)", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-23") {
		modifyDataType(columnName: "exonStart", newDataType: "varchar(255)", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-24") {
		modifyDataType(columnName: "idx", newDataType: "varchar(255)", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-25") {
		dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "idx", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-26") {
		modifyDataType(columnName: "strand", newDataType: "varchar(255)", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-27") {
		modifyDataType(columnName: "accession", newDataType: "varchar(255)", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-28") {
		dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "accession", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-29") {
		modifyDataType(columnName: "genedesc", newDataType: "varchar(255)", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-30") {
		dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "genedesc", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-31") {
		modifyDataType(columnName: "hgncid", newDataType: "varchar(255)", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-32") {
		dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "hgncid", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-33") {
		modifyDataType(columnName: "refseq", newDataType: "varchar(255)", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-34") {
		dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "refseq", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-35") {
		modifyDataType(columnName: "clinvar_val", newDataType: "varchar(2000)", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-36") {
		modifyDataType(columnName: "cosmic_occurs", newDataType: "varchar(1000)", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-37") {
		modifyDataType(columnName: "domains", newDataType: "varchar(2000)", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-38") {
		modifyDataType(columnName: "ens_variant", newDataType: "varchar(500)", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-39") {
		modifyDataType(columnName: "hgvsc", newDataType: "varchar(500)", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-40") {
		modifyDataType(columnName: "omim_ids", newDataType: "varchar(2000)", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-41") {
		modifyDataType(columnName: "pubmed", newDataType: "varchar(2000)", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-42") {
		dropForeignKeyConstraint(baseTableName: "sample", baseTableSchemaName: "dblive", constraintName: "FKC9C775AAB598E252")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-43") {
		dropForeignKeyConstraint(baseTableName: "sample", baseTableSchemaName: "dblive", constraintName: "FKC9C775AA6EA60A02")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-44") {
		dropForeignKeyConstraint(baseTableName: "sample_test", baseTableSchemaName: "dblive", constraintName: "FK9228207B976AFA2")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-45") {
		dropForeignKeyConstraint(baseTableName: "sample_test", baseTableSchemaName: "dblive", constraintName: "FK9228207152C7A12")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-46") {
		dropForeignKeyConstraint(baseTableName: "seq_sample", baseTableSchemaName: "dblive", constraintName: "FK8199DD8AD7F92583")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-47") {
		dropForeignKeyConstraint(baseTableName: "seq_sample", baseTableSchemaName: "dblive", constraintName: "FK8199DD8A6D80CE01")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-48") {
		dropForeignKeyConstraint(baseTableName: "seq_sample", baseTableSchemaName: "dblive", constraintName: "FK8199DD8A152C7A12")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-49") {
		dropIndex(indexName: "mp_alignstats_idx1", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-50") {
		dropIndex(indexName: "mp_alignstats_idx2", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-51") {
		dropIndex(indexName: "mp_alignstats_idx3", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-52") {
		dropIndex(indexName: "mp_alignstats_idx4", tableName: "align_stats")
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-53") {
		dropIndex(indexName: "amplicon_idx1", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-54") {
		dropIndex(indexName: "amplicon_idx2", tableName: "amplicon")
	}
*/
	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-55") {
		dropIndex(indexName: "ano_variant_idx1", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-56") {
		dropIndex(indexName: "ano_variant_idx2", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-57") {
		dropIndex(indexName: "ano_variant_idx3", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-58") {
		dropIndex(indexName: "ano_variant_idx4", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-59") {
		dropIndex(indexName: "ano_variant_idx5", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-60") {
		dropIndex(indexName: "ano_variant_idx6", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-61") {
		dropIndex(indexName: "ano_variant_idx7", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-62") {
		dropIndex(indexName: "ano_variant_idx8", tableName: "ano_variant")
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-63") {
		dropIndex(indexName: "id", tableName: "db_lock")
	}
*/

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-64") {
		dropIndex(indexName: "table_name", tableName: "meta_table")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-65") {
		dropIndex(indexName: "mp_alamutxml_idx1", tableName: "mp_alamutxml")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-66") {
		dropIndex(indexName: "mp_alamutxml_idx2", tableName: "mp_alamutxml")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-67") {
		dropIndex(indexName: "mp_alamutxml_idx3", tableName: "mp_alamutxml")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-68") {
		dropIndex(indexName: "mp_alignstats_idx1", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-69") {
		dropIndex(indexName: "mp_alignstats_idx2", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-70") {
		dropIndex(indexName: "mp_alignstats_idx3", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-71") {
		dropIndex(indexName: "mp_alignstats_idx4", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-72") {
		dropIndex(indexName: "mp_amplicon_idx1", tableName: "mp_amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-73") {
		dropIndex(indexName: "mp_amplicon_idx2", tableName: "mp_amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-74") {
		dropIndex(indexName: "mp_annovar_idx1", tableName: "mp_annovar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-75") {
		dropIndex(indexName: "mp_batch_idx1", tableName: "mp_batch")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-76") {
		dropIndex(indexName: "mp_batch_idx2", tableName: "mp_batch")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-77") {
		dropIndex(indexName: "mp_detente_idx1", tableName: "mp_detente")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-78") {
		dropIndex(indexName: "mp_isdup_idx1", tableName: "mp_isdup")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-79") {
		dropIndex(indexName: "mp_isdup_idx2", tableName: "mp_isdup")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-80") {
		dropIndex(indexName: "mp_mutalyser_idx1", tableName: "mp_mutalyser")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-81") {
		dropIndex(indexName: "mp_mutcnt_idx1", tableName: "mp_mutcnt")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-82") {
		dropIndex(indexName: "mp_mutcnt_idx2", tableName: "mp_mutcnt")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-83") {
		dropIndex(indexName: "mp_mutdesc_idx1", tableName: "mp_mutdesc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-84") {
		dropIndex(indexName: "mp_mutdesc_idx2", tableName: "mp_mutdesc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-85") {
		dropIndex(indexName: "mp_roi_idx1", tableName: "mp_roi")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-86") {
		dropIndex(indexName: "mp_seqrun_idx1", tableName: "mp_seqrun")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-87") {
		dropIndex(indexName: "mp_seqrun_idx2", tableName: "mp_seqrun")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-88") {
		dropIndex(indexName: "mp_tumourtype_idx1", tableName: "mp_tumourtype")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-89") {
		dropIndex(indexName: "mp_varfreq_idx1", tableName: "mp_varfreq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-90") {
		dropIndex(indexName: "mp_varfreq_idx5", tableName: "mp_varfreq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-91") {
		dropIndex(indexName: "mp_vcf_idx1", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-92") {
		dropIndex(indexName: "mp_vcf_idx2", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-93") {
		dropIndex(indexName: "mp_vcf_idx3", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-94") {
		dropIndex(indexName: "mp_vcf_idx4", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-95") {
		dropIndex(indexName: "mp_vcf_idx5", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-96") {
		dropIndex(indexName: "mp_vcf_idx6", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-97") {
		dropIndex(indexName: "mp_vcf_idx7", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-98") {
		dropIndex(indexName: "panel_refseq_idx1", tableName: "panel_refseq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-99") {
		dropIndex(indexName: "panel_refseq_idx2", tableName: "panel_refseq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-100") {
		dropIndex(indexName: "ref_bic_idx1", tableName: "ref_bic")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-101") {
		dropIndex(indexName: "ref_cancergenes_idx1", tableName: "ref_cancergenes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-102") {
		dropIndex(indexName: "ref_clinvar_idx1", tableName: "ref_clinvar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-103") {
		dropIndex(indexName: "ref_clinvar_idx3", tableName: "ref_clinvar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-104") {
		dropIndex(indexName: "ref_emory_idx1", tableName: "ref_emory")
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-105") {
		dropIndex(indexName: "ref_exon_cnt_idx1", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-106") {
		dropIndex(indexName: "ref_exon_cnt_idx2", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-107") {
		dropIndex(indexName: "ref_exon_idx1", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-108") {
		dropIndex(indexName: "ref_exon_idx2", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-109") {
		dropIndex(indexName: "ref_exon_idx3", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-110") {
		dropIndex(indexName: "ref_hgnc_genes_idx2", tableName: "ref_hgnc_genes")
	}
*/
	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-111") {
		dropIndex(indexName: "ref_iarc_idx1", tableName: "ref_iarc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-112") {
		dropIndex(indexName: "ref_kconfab_idx1", tableName: "ref_kconfab")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-113") {
		dropIndex(indexName: "ref_transcript_idx1", tableName: "ref_transcript")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-114") {
		dropIndex(indexName: "ref_transcript_idx2", tableName: "ref_transcript")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-115") {
		dropIndex(indexName: "ref_transcript_idx3", tableName: "ref_transcript")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-116") {
		dropIndex(indexName: "ref_transcript_idx4", tableName: "ref_transcript")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-117") {
		dropIndex(indexName: "ref_ucscgene_idx1", tableName: "ref_ucscgene")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-118") {
		dropIndex(indexName: "ref_ucscgene_idx2", tableName: "ref_ucscgene")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-119") {
		dropIndex(indexName: "sample", tableName: "sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-120") {
		dropIndex(indexName: "sample_name_variant_idx", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-121") {
		dropIndex(indexName: "variant", tableName: "variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-122") {
		createIndex(indexName: "hgvsg_uniq_1456271981333", tableName: "cur_variant", unique: "true") {
			column(name: "hgvsg")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-123") {
		createIndex(indexName: "chr_idx", tableName: "roi") {
			column(name: "chr")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-124") {
		createIndex(indexName: "endPos_idx", tableName: "roi") {
			column(name: "end_pos")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-125") {
		createIndex(indexName: "panel_idx", tableName: "roi") {
			column(name: "panel_id")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-126") {
		createIndex(indexName: "starpos_idx", tableName: "roi") {
			column(name: "start_pos")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-127") {
		createIndex(indexName: "hgvsg_idx", tableName: "seq_variant") {
			column(name: "hgvsg")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-128") {
		createIndex(indexName: "sample_name_idx", tableName: "seq_variant") {
			column(name: "sample_name")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-129") {
		createIndex(indexName: "variant_idx", tableName: "seq_variant") {
			column(name: "variant")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-130") {
		createIndex(indexName: "seq_sample_id_uniq_1456271981366", tableName: "user_prefs", unique: "true") {
			column(name: "seq_sample_id")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-131") {
		dropColumn(columnName: "panel_group", tableName: "align_stats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-132") {
		dropColumn(columnName: "sample", tableName: "audit")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-133") {
		dropColumn(columnName: "issue_status", tableName: "jira_issue")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-134") {
		dropColumn(columnName: "authorised_by_id", tableName: "seq_sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-135") {
		dropColumn(columnName: "curated_by_id", tableName: "seq_sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-136") {
		dropColumn(columnName: "sample_id", tableName: "seq_sample")
	}



	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-125-2") {
		createIndex(indexName: "align_stats_idx1", tableName: "align_stats") {
			column(name: "sample_name")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-125-1") {
		createIndex(indexName: "align_stats_idx2", tableName: "align_stats") {
			column(name: "seqrun")
		}
	}


	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-125-3") {
		createIndex(indexName: "align_stats_idx3", tableName: "align_stats") {
			column(name: "panel_name")
		}
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-125-4") {
		createIndex(indexName: "align_stats_idx4", tableName: "align_stats") {
			column(name: "amplicon")
		}
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-137") {
		dropColumn(columnName: "panel_list", tableName: "seqrun")
	}
*/
	/*
	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-138") {
		dropTable(tableName: "ano_attr")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-139") {
		dropTable(tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-140") {
		dropTable(tableName: "db_lock")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-141") {
		dropTable(tableName: "haem_report")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-142") {
		dropTable(tableName: "meta_table")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-143") {
		dropTable(tableName: "modvar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-144") {
		dropTable(tableName: "mp_alamutxml")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-145") {
		dropTable(tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-146") {
		dropTable(tableName: "mp_amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-147") {
		dropTable(tableName: "mp_annovar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-148") {
		dropTable(tableName: "mp_as_bu")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-149") {
		dropTable(tableName: "mp_audit")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-150") {
		dropTable(tableName: "mp_batch")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-151") {
		dropTable(tableName: "mp_cnv")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-152") {
		dropTable(tableName: "mp_curated")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-153") {
		dropTable(tableName: "mp_detente")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-154") {
		dropTable(tableName: "mp_detente_tests")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-155") {
		dropTable(tableName: "mp_genedesc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-156") {
		dropTable(tableName: "mp_isdup")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-157") {
		dropTable(tableName: "mp_mutalyser")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-158") {
		dropTable(tableName: "mp_mutcnt")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-159") {
		dropTable(tableName: "mp_mutdesc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-160") {
		dropTable(tableName: "mp_panelcnt")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-161") {
		dropTable(tableName: "mp_roi")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-162") {
		dropTable(tableName: "mp_seqrun")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-163") {
		dropTable(tableName: "mp_tumourtype")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-164") {
		dropTable(tableName: "mp_varfreq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-165") {
		dropTable(tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-166") {
		dropTable(tableName: "panel_group")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-167") {
		dropTable(tableName: "panel_refseq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-168") {
		dropTable(tableName: "ref_bic")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-169") {
		dropTable(tableName: "ref_cancergenes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-170") {
		dropTable(tableName: "ref_clinvar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-171") {
		dropTable(tableName: "ref_emory")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-172") {
		dropTable(tableName: "ref_hgmd")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-173") {
		dropTable(tableName: "ref_hgmdimputed")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-174") {
		dropTable(tableName: "ref_iarc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-175") {
		dropTable(tableName: "ref_kconfab")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-176") {
		dropTable(tableName: "ref_transcript")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-177") {
		dropTable(tableName: "ref_ucscgene")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-178") {
		dropTable(tableName: "sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-179") {
		dropTable(tableName: "sample_test")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-180") {
		dropTable(tableName: "tmp_sin")
	}*/

	changeSet(author: "seleznev andrei (generated)", id: "1456271983038-181") {
		dropTable(tableName: "variant")
	}
}
