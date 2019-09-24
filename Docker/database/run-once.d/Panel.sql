-- MySQL dump 10.16  Distrib 10.2.12-MariaDB, for osx10.11 (x86_64)
--
-- Host: localhost    Database: dblive
-- ------------------------------------------------------
-- Server version	10.2.12-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `panel`
--

DROP TABLE IF EXISTS `panel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `panel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `manifest` varchar(255) NOT NULL,
  `panel_group` varchar(255) NOT NULL,
  `skip_gene_mask` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `manifest` (`manifest`)
) ENGINE=InnoDB AUTO_INCREMENT=403 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `panel`
--

LOCK TABLES `panel` WRITE;
/*!40000 ALTER TABLE `panel` DISABLE KEYS */;
INSERT INTO `panel` VALUES (7,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_v4-2_0603132_manifest','Germline Amplicon',NULL),(8,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_v3_070213_manifest','Germline Amplicon',NULL),(9,0,'Research Panel','p53_amplicon_primers_manifest','MP FLD Somatic Development',NULL),(11,0,'R&D Panel for germline Haloplex targeted capture','Haloplex_AOCS_v2_04818-1371122271_Amplicons','RD Somatic Haloplex',NULL),(13,0,'Research Panel','Qiagen_Comprehensive_4_9_2014','MP Development',NULL),(14,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_v4-8_071013_with_off_target_manifest','Germline Amplicon',NULL),(15,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_CAT_Manifest_TC0056453-CAT_Core_B','MP ADS Somatic Production',NULL),(16,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_CAT_Manifest_TC0055678-CAT_Core_A','MP ADS Somatic Production',NULL),(17,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v3.5.2_22_1_2015','Somatic Amplicon',NULL),(18,0,'Production haem myeloid panels','Myeloid_v4.1','Haem Myeloid Amplicon',''),(22,0,'Production haem myeloid panels','Myeloid_Panel_Manifest_v5.2_12_2_2015','Haem Myeloid Amplicon',''),(23,0,'Research Panel','Qiagen_Colorectal_4_9_2014','MP Development',NULL),(24,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v3.5.1_5_12_2014','Somatic Amplicon',NULL),(27,0,'Molpath research, testing and development panels','cll_panel_29.08.14','MP Development',NULL),(28,0,'Production haem myeloid panels','Myeloid_Panel_Manifest_v5.0_16_1_2015','Haem Myeloid Amplicon',''),(29,0,'Production haem myeloid panels','Myeloid_Panel_Manifest_v5.1_4_2_2015','Haem Myeloid Amplicon',''),(30,0,'Production haem myeloid panels','Myeloid_V5.3_1','Haem Myeloid Amplicon',''),(32,0,'Illumina TruSeq panel for Cancer 2015 samples','CancerGNA10212011_170_190_Viewermanifest','Somatic Amplicon',NULL),(33,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_Custom_Amplicon_Control_Manifest_ACP1','MP ILM Control Manifest',NULL),(38,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_1164AAP12O1_manifest','Germline Amplicon',NULL),(40,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2.1_manifest','MP FLD Germline Development',NULL),(41,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2.4_manifest','MP FLD Germline Development',NULL),(42,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2.5_manifest','MP FLD Germline Development',NULL),(43,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2.6_manifest','MP FLD Germline Development',NULL),(47,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2.10_manifest','MP FLD Germline Development',NULL),(48,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2.2_manifest','MP FLD Germline Development',NULL),(50,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2_2.5_manifest','MP FLD Germline Development',NULL),(51,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2_2.6_manifest','MP FLD Germline Development',NULL),(52,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2_2.1_manifest','MP FLD Germline Development',NULL),(53,0,'Research Panel','BRCA12_single_BRCA1_MCPV2_62r_2_2.3_manifest','MP FLD Germline Development',NULL),(56,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_6r_2.1_manifest','MP FLD Germline Development',NULL),(57,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_6r_2.3_manifest','MP FLD Germline Development',NULL),(58,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_6r_2.5_manifest','MP FLD Germline Development',NULL),(59,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_6r_2.7_manifest','MP FLD Germline Development',NULL),(60,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_6r_2.9_manifest','MP FLD Germline Development',NULL),(64,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_6r_2.2_manifest','MP FLD Germline Development',NULL),(70,0,'Research Panel','BRCA12_single_CDH1_ex1_5r_1_manifest','MP FLD Germline Development',NULL),(71,0,'Research Panel','BRCA12_single_CDH1_ex16_3r_1_manifest','MP FLD Germline Development',NULL),(72,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_75r_2.1_manifest','MP FLD Germline Development',NULL),(73,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_75r_2.2_manifest','MP FLD Germline Development',NULL),(76,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_75r_2.5_manifest','MP FLD Germline Development',NULL),(77,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_75r_2.6_manifest','MP FLD Germline Development',NULL),(79,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_75r_2.8_manifest','MP FLD Germline Development',NULL),(83,0,'Research Panel','BRCA12_single_MLH1ex_12_1r_1_manifest','MP FLD Germline Development',NULL),(84,0,'Research Panel','BRCA12_single_MSH2_ex2_5r_1_manifest','MP FLD Germline Development',NULL),(86,0,'Research Panel','BRCA12_single_MSH2_ex5_3r_1_manifest','MP FLD Germline Development',NULL),(88,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_77r_2.1_manifest','MP FLD Germline Development',NULL),(90,0,'Research Panel','BRCA12_single_BRCA2_MCPV2_77r_2.5_manifest','MP FLD Germline Development',NULL),(210,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_v4-2_0603132_with_off_targ_manifest','Germline Amplicon',NULL),(277,0,'Research Panel','Somatic_fix_R19_200313_manifest','Somatic Amplicon',NULL),(294,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_v4-5_100613_with_off_targ_manifest','Germline Amplicon',NULL),(295,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_CAT_Manifest_TC0012307-CAT','MP ADS Somatic Production',NULL),(297,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_v5_010813_with_off_target_manifest','Germline Amplicon',NULL),(299,0,'Molpath research, testing and development panels','TruSightTumor-FPA-Manifest','MP Development',NULL),(300,0,'Molpath research, testing and development panels','TruSightTumor-FPB-Manifest','MP Development',NULL),(303,0,'Research Panel','00100-1361547029_Amplicons','MP Development',NULL),(305,0,'Research Panel','mol_heam_fix_031013','MP FLD Myeloid Development',''),(308,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v3.0_6_11_2013','Somatic Amplicon',NULL),(309,0,'Production germline panel for BRCA1 and 2, TP53 and PTEN','Germline_v4-6_040913_with_off_target_manifest','Germline Amplicon',NULL),(310,0,'Research Panel','haem_Fix','MP FLD Myeloid Development',NULL),(311,0,'Research Panel','FGFR_CALR','MP FLD Myeloid Development',NULL),(312,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v3.2_11_12_2013','Somatic Amplicon',NULL),(313,0,'Production haem lymphoid panels','Lymphoid_v1','Haem Lymphoid Amplicon',''),(315,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v3.3_23_1_2014','Somatic Amplicon',NULL),(316,0,'Production haem lymphoid panels','Lymphoid_v2','Haem Lymphoid Amplicon',''),(317,0,'Production haem myeloid panels','Myeloid_v1','Haem Myeloid Amplicon',''),(318,0,'Production haem lymphoid panels','Lymphoid_v3','Haem Lymphoid Amplicon',''),(320,0,'Production haem myeloid panels','Myeloid_v2','Haem Myeloid Amplicon',''),(321,0,'Production haem lymphoid panels','Lymphoid_v3.1','Haem Lymphoid Amplicon',''),(325,0,'Production haem myeloid panels','Myeloid_v3.2','Haem Myeloid Amplicon',''),(328,0,'Production haem myeloid panels','Myeloid_v3','Haem Myeloid Amplicon',''),(329,0,'Production somatic panel targeting approx. 20 genes','Somatic_Manifest_v3.4_17_07_2014','Somatic Amplicon',NULL),(330,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v3.4_17_7_2014','Somatic Amplicon',NULL),(331,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v3.5_31_7_2014','Somatic Amplicon',NULL),(332,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_CAT_Manifest_Full_Panel_A','MP ADS Somatic Production',NULL),(333,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_CAT_Manifest_Full_Panel_B','MP ADS Somatic Production',NULL),(334,0,'Production haem lymphoid panels','Lymphoid_v4','Haem Lymphoid Amplicon',''),(337,0,'Production haem myeloid panels','Myeloid_v4','Haem Myeloid Amplicon',''),(338,0,'Production haem lymphoid panels','Lymphoid_v4.05','Haem Lymphoid Amplicon',''),(339,0,'Production haem myeloid panels','Myeloid_v5.4','Haem Myeloid Amplicon',''),(342,0,'Production haem lymphoid panels','Lymphoid_Panel_v5.0','Haem Lymphoid Amplicon',''),(343,0,'Production haem myeloid panels','Myeloid_v5.4_1','Haem Myeloid Amplicon',''),(345,0,'Production haem lymphoid panels','Lymphoid_Panel_v5.1','Haem Lymphoid Amplicon',''),(349,0,'Research Panel','MRD_v1','MP FLD Myeloid Production',''),(351,0,'Comprehensive Cancer Panel for ~500 gene capture','CCP_NSC_v0.1','MP CCP Somatic Assay',NULL),(352,0,'Comprehensive Cancer Panel for ~500 gene capture','CCP_SSL_v0.1','MP CCP Somatic Assay',NULL),(353,0,'Research Panel','MRD_v1.1_CEBPA','MP FLD Myeloid Production',''),(355,0,'Research Panel','truseq_custom_amplicon_control_manifest_acp3','MP ILM Control Manifest',NULL),(356,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_CAT_Manifest_CoreAdd_B','MP ADS Somatic Production',NULL),(357,0,'Illumina TruSeq panel for Cancer 2015 samples','TruSeq_CAT_Manifest_CoreAdd_A','Somatic Amplicon',NULL),(358,0,'Dawson Lab Panel for ctDNA','DL_NBG_v3.0','RD DL',NULL),(359,0,'Research Panel','somatic_new_Pools_13082015','Somatic Amplicon',NULL),(360,0,'Illumina TruSeq panels paired for different strands','TruSeq_CAT_Manifest_TC0056455-CAT_Plus_B','MP ADS Somatic Production',NULL),(361,0,'Illumina TruSeq panels paired for different strands','TruSeq_CAT_Manifest_TC0055675-CAT_Plus_A','MP ADS Somatic Production',NULL),(362,0,'Research Panel','MRD_v2','MP FLD Myeloid Production',''),(365,0,'Production haem lymphoid panels','MRD_v3_LYMPHOID','MP FLD Myeloid Production',''),(368,0,'Production somatic panel targeting approx. 20 genes','Somatic_Panel_Manifest_v4_23_10_2015','Somatic Amplicon',NULL),(369,0,'Research Panel','MRD_v4','MP FLD Myeloid Production',''),(370,0,'Production haem myeloid panels','Myeloid_v5.4_1_MRD','Haem Myeloid Amplicon',''),(371,0,'Production haem myeloid panels','Myeloid_v5.4_1_CEBPA_N','Haem Myeloid Amplicon',''),(372,0,'Production haem lymphoid panels','Lymphoid_Panel_v5.2','Haem Lymphoid Amplicon',''),(373,0,'Research Panel','MRD_v4.1','MP FLD Myeloid Production',''),(374,0,'Research Panel','MRD_v4.2','MP FLD Myeloid Production',''),(375,0,'Research Panel','MRD_v4.3','MP FLD Myeloid Production',''),(376,0,'Research Panel','MRD_v4.4','MP FLD Myeloid Production',''),(377,0,'Research Panel','MRD_v4.5','MP FLD Myeloid Production',''),(379,0,'Comprehensive Cancer Panel for multi-gene capture','Pathology_hyb_CCP_2','Somatic Hybrid',NULL),(380,0,'Hybrid DNA Repair Panel for multi-gene capture','Pathology_hyb_DRCP_1','DNA Repair Hybrid',NULL),(381,0,'Production haem lymphoid panels','Lymphoid_Panel_v5.3','Haem Lymphoid Amplicon',''),(382,0,'Comprehensive Cancer Panel for multi-gene capture','Pathology_hyb_CCP_1','MP CCP Somatic Assay',NULL),(383,0,'Comprehensive Cancer Panel for multi-gene capture','Pathology_hyb_CCP_2_dev','MP CCP Somatic Assay',NULL),(384,0,'Hybrid Germline Panel for multi-gene capture','Pathology_hyb_FRCP_1.2','Germline Hybrid',NULL),(385,0,'Hybrid Germline Panel for multi-gene capture','Pathology_hyb_FRCP_1.1','MP Germline Capture Assay',NULL),(386,0,'Comprehensive Cancer Panel for multi-gene capture','Pathology_hyb_PHCP_1.1','Haem Hybrid',''),(387,0,'Research Panel','MRD_v4.6','MP FLD Myeloid Production',''),(389,0,'Research Panel','AgilentSureSelect SUPER','Research',NULL),(390,0,'Research Panel','PanHaem_v1.1','Research',''),(391,0,'Research Panel','Familial_Cancer_Risk_v1.2','Research',NULL),(392,0,'Production haem lymphoid panels','Lymphoid_Panel_v6.1','Haem Lymphoid Amplicon',''),(393,0,'Production haem lymphoid panels','Lymphoid_Panel_v6.2','Haem Lymphoid Amplicon',''),(394,0,'Research Panel','TSP_solid_tumour','Research',NULL),(395,0,'Hybrid Pan Haem Panel for multi-gene capture','Pathology_hyb_PHCP_2','Haem Hybrid',''),(396,0,'Research Panel','Human(HG19)','Research',NULL),(397,0,'Production haem lymphoid panels','Lymphoid_Panel_v6.3','Haem Lymphoid Amplicon',''),(398,0,'Production haem lymphoid panels','Lymphoid_Panel_v6.4','Haem Lymphoid Amplicon',''),(399,0,'Production haem lymphoid panels','Lymphoid_Panel_v6.5','Haem Lymphoid Amplicon',''),(400,0,'Research Panel','vespa10','Research',NULL),(401,0,'Research Panel','MRD_v4.7','Research',NULL),(402,0,'Traceback Panel Agilent Sureselect','Agilent-SureSelectXT-HS-IUMI','Research','\0');
/*!40000 ALTER TABLE `panel` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-13 14:16:17
