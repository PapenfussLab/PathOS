databaseChangeLog = {
/*
	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-1") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "endpos", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-2") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "primerlen1", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-3") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "primerlen2", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-4") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "startpos", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-5") {
		modifyDataType(columnName: "date", newDataType: "datetime", tableName: "pubmed")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-6") {
		addNotNullConstraint(columnDataType: "datetime", columnName: "date", tableName: "pubmed")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-7") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "exonEnd", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-8") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "exonFrame", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-9") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "exonStart", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-10") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "strand", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-11") {
		modifyDataType(columnName: "genedesc", newDataType: "varchar(255)", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-12") {
		addNotNullConstraint(columnDataType: "varchar(500)", columnName: "ens_variant", tableName: "seq_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-13") {
		addNotNullConstraint(columnDataType: "varchar(500)", columnName: "hgvsc", tableName: "seq_variant")
	}
*/
	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-14") {
		modifyDataType(columnName: "description", newDataType: "varchar(8000)", tableName: "tag")
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-15") {
		addNotNullConstraint(columnDataType: "bigint", columnName: "seqrun_id", tableName: "vcf_upload")
	}
*/
	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-16") {
		dropForeignKeyConstraint(baseTableName: "grp_variant", baseTableSchemaName: "dblive", constraintName: "FK2955932BC6CECA17")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-17") {
		dropForeignKeyConstraint(baseTableName: "tag_links", baseTableSchemaName: "dblive", constraintName: "FK7C35D6D45A3B441D")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-18") {
		dropForeignKeyConstraint(baseTableName: "vcf_upload", baseTableSchemaName: "dblive", constraintName: "FKC37CFDA7B976AFA2")
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-19") {
		dropIndex(indexName: "amplicon_idx1", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-20") {
		dropIndex(indexName: "amplicon_idx2", tableName: "amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-21") {
		dropIndex(indexName: "ano_variant_idx1", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-22") {
		dropIndex(indexName: "ano_variant_idx2", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-23") {
		dropIndex(indexName: "ano_variant_idx3", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-24") {
		dropIndex(indexName: "ano_variant_idx4", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-25") {
		dropIndex(indexName: "ano_variant_idx5", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-26") {
		dropIndex(indexName: "ano_variant_idx6", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-27") {
		dropIndex(indexName: "ano_variant_idx7", tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-28") {
		dropIndex(indexName: "ano_variant_idx8", tableName: "ano_variant")
	}*/

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-29") {
		dropIndex(indexName: "hgvsg_uniq_1456271981333", tableName: "cur_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-30") {
		dropIndex(indexName: "variant", tableName: "cur_variant")
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-31") {
		dropIndex(indexName: "id", tableName: "db_lock")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-32") {
		dropIndex(indexName: "mp_alignstats_idx1", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-33") {
		dropIndex(indexName: "mp_alignstats_idx2", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-34") {
		dropIndex(indexName: "mp_alignstats_idx3", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-35") {
		dropIndex(indexName: "mp_alignstats_idx4", tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-36") {
		dropIndex(indexName: "mp_batch_idx1", tableName: "mp_batch")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-37") {
		dropIndex(indexName: "mp_batch_idx2", tableName: "mp_batch")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-38") {
		dropIndex(indexName: "mp_detente_idx1", tableName: "mp_detente")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-39") {
		dropIndex(indexName: "mp_roi_idx1", tableName: "mp_roi")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-40") {
		dropIndex(indexName: "mp_seqrun_idx1", tableName: "mp_seqrun")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-41") {
		dropIndex(indexName: "mp_seqrun_idx2", tableName: "mp_seqrun")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-42") {
		dropIndex(indexName: "mp_tumourtype_idx1", tableName: "mp_tumourtype")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-43") {
		dropIndex(indexName: "mp_vcf_idx1", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-44") {
		dropIndex(indexName: "mp_vcf_idx2", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-45") {
		dropIndex(indexName: "mp_vcf_idx3", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-46") {
		dropIndex(indexName: "mp_vcf_idx4", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-47") {
		dropIndex(indexName: "mp_vcf_idx5", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-48") {
		dropIndex(indexName: "mp_vcf_idx6", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-49") {
		dropIndex(indexName: "mp_vcf_idx7", tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-50") {
		dropIndex(indexName: "ref_exon_cnt_idx1", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-51") {
		dropIndex(indexName: "ref_exon_cnt_idx2", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-52") {
		dropIndex(indexName: "ref_exon_idx1", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-53") {
		dropIndex(indexName: "ref_exon_idx2", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-54") {
		dropIndex(indexName: "ref_exon_idx3", tableName: "ref_exon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-55") {
		dropIndex(indexName: "ref_hgnc_genes_idx2", tableName: "ref_hgnc_genes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-56") {
		dropIndex(indexName: "FKC9C775AA6EA60A02", tableName: "sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-57") {
		dropIndex(indexName: "FKC9C775AAB598E252", tableName: "sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-58") {
		dropIndex(indexName: "FK9228207152C7A12", tableName: "sample_test")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-59") {
		dropIndex(indexName: "FK9228207B976AFA2", tableName: "sample_test")
	}*/

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-60") {
		dropColumn(columnName: "created_by_id", tableName: "grp_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-61") {
		dropColumn(columnName: "date_created", tableName: "grp_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-62") {
		dropColumn(columnName: "last_updated", tableName: "grp_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-63") {
		dropColumn(columnName: "mut_context", tableName: "pat_sample")
	}
/*
	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-64") {
		dropColumn(columnName: "lims_work_flow", tableName: "seq_sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-65") {
		dropColumn(columnName: "panel_list", tableName: "seqrun")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-66") {
		dropColumn(columnName: "panel_id", tableName: "vcf_upload")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-67") {
		dropColumn(columnName: "queue", tableName: "vcf_upload")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-68") {
		dropColumn(columnName: "report_file_path", tableName: "vcf_upload")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-69") {
		dropColumn(columnName: "sample_name", tableName: "vcf_upload")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-70") {
		dropTable(tableName: "ano_attr")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-71") {
		dropTable(tableName: "ano_variant")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-72") {
		dropTable(tableName: "db_lock")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-73") {
		dropTable(tableName: "haem_report")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-74") {
		dropTable(tableName: "meta_table")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-75") {
		dropTable(tableName: "modvar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-76") {
		dropTable(tableName: "mp_alamutxml")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-77") {
		dropTable(tableName: "mp_alignstats")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-78") {
		dropTable(tableName: "mp_amplicon")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-79") {
		dropTable(tableName: "mp_annovar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-80") {
		dropTable(tableName: "mp_as_bu")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-81") {
		dropTable(tableName: "mp_audit")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-82") {
		dropTable(tableName: "mp_batch")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-83") {
		dropTable(tableName: "mp_cnv")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-84") {
		dropTable(tableName: "mp_curated")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-85") {
		dropTable(tableName: "mp_detente")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-86") {
		dropTable(tableName: "mp_detente_tests")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-87") {
		dropTable(tableName: "mp_genedesc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-88") {
		dropTable(tableName: "mp_isdup")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-89") {
		dropTable(tableName: "mp_mutalyser")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-90") {
		dropTable(tableName: "mp_mutcnt")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-91") {
		dropTable(tableName: "mp_mutdesc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-92") {
		dropTable(tableName: "mp_panelcnt")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-93") {
		dropTable(tableName: "mp_roi")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-94") {
		dropTable(tableName: "mp_seqrun")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-95") {
		dropTable(tableName: "mp_tumournormal")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-96") {
		dropTable(tableName: "mp_tumourtype")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-97") {
		dropTable(tableName: "mp_varfreq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-98") {
		dropTable(tableName: "mp_vcf")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-99") {
		dropTable(tableName: "panel_group")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-100") {
		dropTable(tableName: "panel_refseq")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-101") {
		dropTable(tableName: "ref_bic")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-102") {
		dropTable(tableName: "ref_cancergenes")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-103") {
		dropTable(tableName: "ref_clinvar")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-104") {
		dropTable(tableName: "ref_emory")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-105") {
		dropTable(tableName: "ref_hgmd")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-106") {
		dropTable(tableName: "ref_hgmdimputed")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-107") {
		dropTable(tableName: "ref_iarc")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-108") {
		dropTable(tableName: "ref_kconfab")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-109") {
		dropTable(tableName: "ref_transcript")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-110") {
		dropTable(tableName: "ref_ucscgene")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-111") {
		dropTable(tableName: "sample")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-112") {
		dropTable(tableName: "sample_test")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-113") {
		dropTable(tableName: "sv_report")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-114") {
		dropTable(tableName: "tag_links")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-115") {
		dropTable(tableName: "tmp_sin")
	}

	changeSet(author: "seleznev andrei (generated)", id: "1479267294173-116") {
		dropTable(tableName: "tmp_testvar")
	}*/
}
