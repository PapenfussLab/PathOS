package org.petermac.pathos.api

import org.petermac.yaml.YamlCodec
import de.xypron.statistics.MersenneTwister

class PathosImportDataGenerator {

    static List male = ["Aaron", "Adam", "Aiden", "Alex", "Alexander", "Ali", "Angus", "Archer", "Archie", "Arlo", "Ashton",
                        "Austin", "Beau", "Benjamin", "Billy", "Blake", "Charles", "Charlie", "Chase", "Christian", "Connor",
                        "Cooper", "Daniel", "Darcy", "Dylan", "Edward", "Eli", "Elijah", "Ethan", "Felix", "Finn", "Fletcher",
                        "Flynn", "Gabriel", "George", "Hamish", "Harrison", "Harry", "Harvey", "Hayden", "Henry", "Hudson",
                        "Hugo", "Hunter", "Isaac", "Jack", "Jackson", "Jacob", "Jake", "James", "Jasper", "Jaxon", "Jayden",
                        "Jett", "Jordan", "Joseph", "Joshua", "Julian", "Kai", "Lachlan", "Leo", "Levi", "Lewis", "Liam",
                        "Lincoln", "Logan", "Louis", "Luca", "Lucas", "Luke", "Marcus", "Mason", "Matthew", "Max", "Maxwell",
                        "Michael", "Mitchell", "Muhammad", "Nate", "Nathan", "Nathaniel", "Nicholas", "Noah", "Oliver", "Oscar",
                        "Owen", "Patrick", "Riley", "Ryan", "Ryder", "Samuel", "Sebastian", "Spencer", "Theodore", "Thomas",
                        "Toby", "Tyler", "William", "Xavier", "Zachary"]

    static List female = ["Abigail", "Addison", "Aisha", "Alexandra", "Alexis", "Alice", "Alyssa", "Amelia", "Anna",
                        "Annabelle", "Aria", "Ariana", "Audrey", "Aurora", "Ava", "Ayla", "Bella", "Billie", "Charlie",
                        "Charlotte", "Chelsea", "Chloe", "Claire", "Daisy", "Eden", "Eleanor", "Eliza", "Elizabeth", "Ella",
                        "Ellie", "Eloise", "Elsie", "Emilia", "Emily", "Emma", "Eva", "Eve", "Evelyn", "Evie", "Frankie",
                        "Georgia", "Grace", "Hannah", "Harper", "Harriet", "Hazel", "Heidi", "Holly", "Imogen", "Indiana",
                        "Isabel", "Isabella", "Isabelle", "Isla", "Ivy", "Jasmine", "Jessica", "Kiara", "Layla", "Lexi",
                        "Lily", "Lola", "Lucy", "Mackenzie", "Maddison", "Madeleine", "Madeline", "Madison", "Matilda",
                        "Maya", "Mia", "Mila", "Milla", "Molly", "Natalie", "Olive", "Olivia", "Paige", "Penelope", "Phoebe",
                        "Piper", "Poppy", "Rose", "Ruby", "Sadie", "Samantha", "Sarah", "Savannah", "Scarlett", "Sienna",
                        "Sofia", "Sophia", "Sophie", "Stella", "Summer", "Victoria", "Violet", "Willow", "Zara", "Zoe"]

    static List neutral = ["Alexis", "Amari", "Angel", "Ariel", "Armani", "Avery", "Azariah", "Blake", "Briar", "Cameron",
                        "Carter", "Casey", "Charlie", "Dakota", "Dallas", "Dylan", "Eden", "Elliot", "Elliott", "Ellis",
                        "Emerson", "Emery", "Emory", "Finley", "Frankie", "Harley", "Harper", "Hayden", "Hunter", "Jamie",
                        "Jayden", "Jessie", "Jordan", "Jordyn", "Justice", "Kai", "Kamryn", "Karter", "Kayden", "Kendall",
                        "Landry", "Leighton", "Lennon", "Lennox", "Logan", "London", "Lyric", "Marley", "Micah", "Milan",
                        "Morgan", "Oakley", "Parker", "Payton", "Peyton", "Phoenix", "Quinn", "Reagan", "Reese", "Remington",
                        "Remy", "Riley", "River", "Rory", "Rowan", "Royal", "Ryan", "Rylan", "Sage", "Sawyer", "Shiloh",
                        "Skylar", "Skyler", "Sutton", "Tatum", "Taylor", "Zion"]



    static List family = ["Adams", "Ali", "Allen", "Anderson", "Andrews", "Armstrong", "Atkinson", "Bailey", "Baker", "Barker",
                        "Barnes", "Bell", "Bennett", "Berry", "Booth", "Bradley", "Brooks", "Brown", "Butler", "Campbell",
                        "Carr", "Carter", "Chambers", "Chapman", "Clark", "Clarke", "Cole", "Collins", "Cook", "Cooper", "Cox",
                        "Cunningham", "Davies", "Davis", "Dawson", "Dean", "Dixon", "Edwards", "Ellis", "Evans", "Fisher",
                        "Foster", "Fox", "Gardner", "George", "Gibson", "Gill", "Gordon", "Graham", "Grant", "Gray", "Green",
                        "Griffiths", "Hall", "Hamilton", "Harper", "Harris", "Harrison", "Hart", "Harvey", "Hill", "Holmes",
                        "Hudson", "Hughes", "Hunt", "Hunter", "Jackson", "James", "Jenkins", "Johnson", "Johnston", "Jones",
                        "Kaur", "Kelly", "Kennedy", "Khan", "King", "Knight", "Lane", "Lawrence", "Lawson", "Lee", "Lewis",
                        "Lloyd", "Macdonald", "Marshall", "Martin", "Mason", "Matthews", "Mcdonald", "Miller", "Mills",
                        "Mitchell", "Moore", "Morgan", "Morris", "Murphy", "Murray", "Owen", "Palmer", "Parker", "Patel",
                        "Pearce", "Pearson", "Phillips", "Poole", "Powell", "Price", "Reid", "Reynolds", "Richards",
                        "Richardson", "Roberts", "Robertson", "Robinson", "Rogers", "Rose", "Ross", "Russell", "Ryan",
                        "Saunders", "Scott", "Shaw", "Simpson", "Smith", "Spencer", "Stevens", "Stewart", "Stone", "Taylor",
                        "Thomas", "Thompson", "Thomson", "Turner", "Walker", "Walsh", "Ward", "Watson", "Watts", "Webb",
                        "Wells", "West", "White", "Wilkinson", "Williams", "Williamson", "Wilson", "Wood", "Wright", "Young"]

    static List pathology = ["ACT Pathology", "Alfred Hospital", "Alfred Pathology Services", "Auckland Hospital",
                        "Austin Health", "Austin Health Genetics Service", "Austin Pathology - Austin Hospital",
                        "Australian Clinical Labs", "Ballarat Health Services", "Benalla Hospital", "Bendigo Health",
                        "Border Medical Oncology", "Cabrini Hospital", "Cabrini Pathology", "Canberra Hospital",
                        "Canterbury District Health", "Canterbury District Health Board", "Capital Pathology",
                        "Christchurch Hospital", "Concord Repat General Hospital (CSLS)", "Dorevitch Pathology",
                        "Douglas Hanly Moir Pathology", "Eastern Health", "Eastern Health Pathology", "Epworth Eastern Hospital",
                        "Epworth Freemasons Hospital", "Epworth Private Hospital", "Geelong Hospital ",
                        "Goulburn Valley Base Hospital", "Goulburn Valley Hospital", "Healthscope Pathology", "Hobart Pathology",
                        "Hunter Area Pathology", "Hunter Area Pathology Service", "Inst. of Medical & Veterinary Science",
                        "Launceston General Hospital", "Launceston Pathology", "MH-Royal Melbourne Hospital",
                        "Mater Laboratory Services", "Melbourne Health Shared", "Melbourne Health Shared Pathology Svc",
                        "Melbourne Pathology ", "Melbourne Private Hospital", "Middlemore Hospital", "Mildura Base Hospital",
                        "Monash Medical Centre", "Monash Pathology (Monash", "Monash Pathology (Monash Health)",
                        "Nepean Hospital", "North West Pathology", "Northern Hospital", "Northern Tasmanian Pathology Service",
                        "Northpark Private Hospital", "Pacific Laboratory Medicine", "Pacific Laboratory Medicine Service",
                        "Pathology Queensland", "Peninsula Private Hospital", "Peter Mac Clinical Trials (Internal)",
                        "Peter Mac Clinical Trials (external)", "Peter MacCallum Cancer", "Peter MacCallum Cancer Centre",
                        "Peter MacCallum Cancer Centre- Pathology", "Prince Of Wales Hospital", "Private Provider",
                        "Queensland Health Pathology Service", "Queensland Medical Laboratory", "Royal Adelaide Hospital",
                        "Royal Brisbane Hospital", "Royal Childrens Hospital", "Royal Childrens Hospital (Brisbane)",
                        "Royal Darwin Hospital", "Royal Hobart Hospital Pathology", "Royal Hobart Hospital Pathology Services",
                        "Royal North Shore Hospital", "Royal Perth Hospital", "Royal Prince Alfred Hospital", "SA Pathology (FMC)",
                        "SA Pathology (WCH)", "South Eastern Private Hospital", "St George Hospital NSW", "St Georges Hospital",
                        "St John Of God Hospital (Ballarat)", "St John of God Hospital (Geelong)", "St Vincents Hospital (Sydney)",
                        "St Vincents Hospital Melbourne Ltd", "St Vincents Pathology", "St. John of God Pathology",
                        "Sullivan Nicolaides Pathology", "Sydney Adventist Hospital Limited", "Sydney South West Pathology Service",
                        "The Tweed Hospital", "Victorian Clinical Genetics", "Victorian Clinical Genetics Services",
                        "Wellington Hospital", "Western Diagnostic Pathology", "Western NSW Local Health District",
                        "Westmead Hospital", "Wollongong Hospital"]

    static List testSets = [
                ['19102-3', 'Germline Result Summary'], ['19102-3', 'Result Summary'],
                ['47999-8', 'Result 01 Exon Number'], ['47999-8', 'Result 02 Exon Number'],
                ['47999-8', 'Result 03 Exon Number'], ['47999-8', 'Result 04 Exon Number'],
                ['48004-6', 'Result 01 HGVSc'], ['48004-6', 'Result 02 HGVSc'],
                ['48004-6', 'Result 03 HGVSc'], ['48004-6', 'Result 04 HGVSc'],
                ['48005-3', 'Result 01 HGVSp'], ['48005-3', 'Result 02 HGVSp'],
                ['48005-3', 'Result 03 HGVSp'], ['48005-3', 'Result 04 HGVSp'],
                ['48013-7', 'Result 01 HGVSg'], ['48013-7', 'Result 02 HGVSg'],
                ['48013-7', 'Result 03 HGVSg'], ['48013-7', 'Result 04 HGVSg'],
                ['48018-6', 'Result 01 Variant Gene Name'], ['48018-6', 'Result 02 Variant Gene Name'],
                ['48018-6', 'Result 03 Variant Gene Name'], ['48018-6', 'Result 04 Variant Gene Name'],
                ['51968-6', 'Germline Clinical Interpretation'], ['51968-6', 'Haem Clinical Interpretation'],
                ['51968-6', 'Somatics Clinical Interpretation'], ['53037-8', 'Result 01 Interpretation'],
                ['53037-8', 'Result 02 Interpretation'], ['53037-8', 'Result 03 Interpretation'],
                ['53037-8', 'Result 04 Interpretation'], ['AFEQ01', 'Result 01 Variant Read Frequency'],
                ['AFEQ02', 'Result 02 Variant Allele Frequency'], ['AFEQ03', 'Result 03 Variant Allele Frequency'],
                ['AFEQ04', 'Result 04 Variant Allele Frequency'], ['B598200', 'Breast Ovarian Panel'],
                ['B598201', 'BRCA1 and BRCA2'], ['B598202', 'BRCA1 and BRCA2 (SEQ)'],
                ['B598203', 'BRCA1 and BRCA2 (MLPA)'], ['B598207', 'TP53 (SEQ and MLPA)'],
                ['B598208', 'PTEN (SEQ and MLPA)'], ['B598225', 'Predictive'],
                ['B598226', 'Predictive (SEQ)'], ['B598227', 'Predictive (MLPA)'],
                ['B598228', 'MLH1 Methylation'], ['B598231', 'MEN2A'],
                ['B598232', 'CDH1'], ['B598234', 'SDHB'],
                ['B598271', 'BRCA1/2 PLUS Sub-Panel'], ['B598272', 'BR/OV/PR/PA Sub-Panel'],
                ['B598273', 'Ovarian/CRC Sub-Panel'], ['B598274', 'CRC/Endom Sub-Panel'],
                ['B598275', 'Endocrine Sub-Panel'], ['B598276', 'Polyps Sub-Panel'],
                ['B598277', 'PGL/PCC Sub-Panel'], ['B598278', 'CRC/Polyps Sub-Panel'],
                ['B598279', 'Skin Sub-Panel'], ['B598280', 'Renal Sub-Panel'],
                ['B598281', 'Sarcoma Sub-Panel'], ['B598282', 'FAM Single Gene 01'],
                ['B598283', 'FAM Single Gene 02'], ['B598285', 'Familial One Panel'],
                ['B598286', 'Spectrum One Sub-Panel'], ['B598287', 'Mismatch Repair Sub-Panel'],
                ['B598320', 'Myeloid Panel (Non-MBS)'], ['B598321', 'MPN Panel (Non-MBS)'],
                ['B598324', 'BRAF (Ex 15)'], ['B598325', 'IDH1 (Ex 4)'],
                ['B598326', 'IDH2 (Ex 4)'], ['B598327', 'Myeloid Single Gene 01'],
                ['B598328', 'Myeloid Single Gene 02'], ['B598333', 'Myeloid Panel (NGS)'],
                ['B598340', 'Lymphoid NGS Panel'], ['B598341', 'MYD88 (Ex 5)'],
                ['B598345', 'Lymphoid Single Gene 01'], ['B598400', 'Somatic Panel (NGS)'],
                ['B598403', 'GIST Panel'], ['B598420', 'Comprehensive Cancer Panel - Tier 1'],
                ['B598421', 'Comprehensive Cancer Panel - Tier 2'], ['B73295', 'MBS Item No. 73295 - BRCA1/BRCA2'],
                ['B73314', 'MBS Item No. 73314 - AML/APML/ALL/CML'], ['B73336', 'MBS Item No. 73336 - BRAF Melanoma'],
                ['B73337', 'MBS Item No. 73337 - EGFR Lung'], ['B73338', 'MBS Item No. 73338 - RAS CRC'],
                ['CLASS01', 'Result 01 Classification'], ['CLASS02', 'Result 02 Classification'],
                ['CLASS03', 'Result 03 Classification'], ['CLASS04', 'Result 04 Classification'],
                ['CONS01', 'Result 01 Consequence'], ['CONS02', 'Result 02 Consequence'],
                ['CONS03', 'Result 03 Consequence'], ['CONS04', 'Result 04 Consequence'],
                ['ERC', 'External Result Confirmation-'], ['ERM1', 'External Request MP 1'],
                ['MOL', 'Molecular Pathology Generic registration'], ['NOTEST', 'PMCC Mol Path - Sample Not Tested-'],
                ['PAML', 'Myeloid Panel-'], ['PAMLR1', 't(8;21) RUNX1-RUNX1T1 QL Translocation'],
                ['PASHK', 'Ashkenazi Jewish Breast Cancer Analysis-'], ['PATHDX', 'Pathologist Review'],
                ['PBCL2', 't(14;18) Gene Translocation Analysis-'], ['PBCRL', 't(9;22) BCR-ABL Qual. Analysis-'],
                ['PBCRR1', 't(9;22) BCR-ABL Translocation'], ['PBOC1', 'Breast Ovarian Cancer-'],
                ['PCALR', 'CALR MUTATION ANALYSIS'], ['PCALRR', 'CALR Mutation Result'],
                ['PCEBPA', 'CEBPA Mutation Analysis-'], ['PCEBPR', 'CEBPA Mutation'],
                ['PCHIM', 'Chimerism Analysis - Post-Transplant-'], ['PCHIPR', 'Chimerism Analysis - Pre-Transplant-'],
                ['PCHIS1', 'Chimerism Marker 1 Selected'], ['PCOM1', 'Comprehensive Cancer Panel-'],
                ['PDNABK', 'Extraction Blank DNA-'], ['PDRCP', 'Prostate DRCP Study-'],
                ['PFAM', 'Extended Familial Cancer-'], ['PFLT3', 'FLT3 ITD Mutation Analysis-'],
                ['PFLTH1', 'FLT3-ITD Peak 1'], ['PFLTH2', 'FLT3-ITD Peak 2'],
                ['PFLTH3', 'FLT3-ITD Peak 3'], ['PFLTH4', 'FLT3-ITD Peak 4'],
                ['PFLTHW', 'Wildtype Peak'], ['PFLTS1', 'FLT3-ITD Basepair Size 1'],
                ['PFLTS2', 'FLT3-ITD Basepair Size 2'], ['PFLTS3', 'FLT3-ITD Basepair Size 3'],
                ['PFLTS4', 'FLT3-ITD Basepair Size 4'], ['PFLTSW', 'FLT3 Wildtype Basepair Size'],
                ['PGERM1', 'Familial Single Gene Mutation Analysis-'], ['PGERMC', 'Germline Clinical Recommendation'],
                ['PGERMD', 'Germline Test Disclaimers'], ['PGERMF', 'Germline References'],
                ['PGERMG', 'Germline General Comments'], ['PGERML', 'Germline Test Limitations'],
                ['PGERMM', 'Germline Test Methodology (Details)'], ['PGERMP', 'Germline Test Principle (Summary)'],
                ['PGERMR', 'Germline Report Heading'], ['PGERP1', 'Germline Reportables PDF 1-'],
                ['PGPDF1', 'Germline Report PDF 1'], ['PHAEMC', 'Haem Clinical Recommendation'],
                ['PHAEMD', 'Haem Test Disclaimers'], ['PHAEMF', 'Haem References'],
                ['PHAEMG', 'Haem General Comments'], ['PHAEML', 'Haem Test Limitations'],
                ['PHAEMM', 'Haem Test Methodology (Details)'], ['PHAEMP', 'Haem Test Principle (Summary)'],
                ['PHAEMR', 'Haem Report Heading'], ['PHAEMS', 'Haem Result Summary'],
                ['PHAEP1', 'Molecular Haem Reportables PDF 1-'], ['PHAEP2', 'Molecular Haem Reportables PDF 2-'],
                ['PHISTO', 'Histological Typing:'], ['PHPDF1', 'Molecular Haem Report PDF 1'],
                ['PIGH', 'IgH Gene Rearrangement Analysis-'], ['PIGHR1', 'IgH Gene Rearrangement Analysis:'],
                ['PIGHV', 'IGHV Somatic Mutation Analysis-'], ['PIGVR1', 'IGVH Rearrangement 1 Functionality'],
                ['PINVR1', 't(16;16) CBFB-MYH11 QL Translocation'], ['PJK14', 'JAK2 V617F Mutation Analysis-'],
                ['PJK14R', 'JAK2 Exon 14 V617F Mutation Result'], ['PLYM', 'Lymphoid Panel-'],
                ['PMASR1', 'cKIT D816V Mutation'], ['PMASTO', 'cKIT D816V Mutation Analysis-'],
                ['PMETH', 'Methylation Gene Analysis-'], ['PMMR1', 'MMR Gene Mutation Analysis-'],
                ['PMSI', 'Microsatellite Instability Analysis-'], ['PNOSEE', 'Restricted View Screen-'],
                ['PNPM1', 'NPM1 Mutation Analysis-'], ['PNPM1R', 'NPM1 Mutation'],
                ['PPEGFR', 'Plasma DNA EGFR Mutation Analysis-'], ['PPGER', 'Predictive Gene Variant Reportables'],
                ['PPMLR1', 't(15;17) PMLRARA QL Translocation'], ['PPRED1', 'Predictive Gene Mutation Analysis-'],
                ['PPRED2', 'Predictive Gene Mutation - Confirmatory-,'], ['PQAMHK', 'AML1-ETO ABL Transcript Gene Expression'],
                ['PQAML1', 'RUNX1-RUNX1T1 Gene Expression'], ['PQBCN1', 'B3A2 Transcript Gene Expression'],
                ['PQBCN2', 'B2A2 Transcript Gene Expression'], ['PQBCN3', 'E1A2 Transcript Gene Expression'],
                ['PQBCN4', 'BCR Transcript Gene Expression'], ['PQBCR1', 't(9;22) BCR-ABL Quant. Analysis'],
                ['PQC100', 'QC ASSAY 100bp Av CT'], ['PQC300', 'QC ASSAY 300bp Av CT'],
                ['PQCML', 'BCR-ABL CML tested'], ['PQCSOM', 'QC ASSAY PASS/FAIL'],
                ['PQLAML', 't(8;21) RUNX1-RUNX1T1 Qual. Analysis-'], ['PQLINV', 't(16;16) CBFB-MYH11 Qual. Analysis-'],
                ['PQLPML', 't(15;17) PML-RARA Qual. Analysis-'], ['PQNAML', 't(8;21) RUNX1-RUNX1T1 Quant. Analysis-'],
                ['PQNINV', 't(16;16) CBFB-MYH11 Quant. Analysis-'], ['PQNPM1', 'NPM1 MRD-'],
                ['PQNPMN', '% NPM1 VAF'], ['PQP190', 'BCR-ABL e1a2 (p190) tested'],
                ['PQP210', 'BCR-ABL b2a2 b3a2 (p210) tested'], ['PQPML1', 't(15;17) PML-RARA Quant. Analysis'],
                ['PQPMN1', 'BCR1 Transcript Gene Expression'], ['PQPMN2', 'BCR2 Transcript Gene Expression'],
                ['PQPMN3', 'BCR3 Transcript Gene Expression'], ['PQPMN4', 'ABL Transcript Gene Expression'],
                ['PSANG', 'Additional Sanger Testing-'], ['PSANGR', 'Sanger Mutation Result'],
                ['PSDNA', 'DNA Storage-'], ['PSDNAG', 'DNA Storage General Comments'],
                ['PSEND', 'Specimen Sendaway-'], ['PSENTR', 'Sendaway Test Requested'],
                ['PSOM1', 'Somatic Cancer Panel NGS-'], ['PSOM1P', 'Somatics Test Principle (Summary)'],
                ['PSOM2', 'Somatics Traditional-'], ['PSOM2R', 'Somatics Trad Report Heading'],
                ['PSOMAC', 'Somatics Clinical Recommendation'], ['PSOMAD', 'Somatics Test Disclaimers'],
                ['PSOMAF', 'Somatics References'], ['PSOMAG', 'Somatics General Comments'],
                ['PSOMAL', 'Somatics Test Limitations'], ['PSOMAM', 'Somatics Test Methodology (Details)'],
                ['PSOMAR', 'Somatics Report Heading'], ['PSOMP1', 'Somatics Reportables PDF 1-'],
                ['PSPDF1', 'Somatics Report PDF 1'], ['PSRE1P', 'PDF of Sendaway Report 1'],
                ['PSRE2P', 'PDF of Sendaway Report 2'], ['PSRE3P', 'PDF of Sendaway Report 3'],
                ['PSREP1', 'Sendaway Reports PDF 1-'], ['PSREP2', 'Sendaway Reports PDF 2-'],
                ['PSREP3', 'Sendaway Reports PDF 3-'], ['PSRNA', 'RNA Storage-'],
                ['PSRNAG', 'RNA Storage General Comments'], ['PTCRG', 'TCRG Gene Rearrangement Analysis-'],
                ['PTCRR1', 'TCRG Gene Rearrangement Analysis:'], ['PVARI1', 'Gene Variant Reportables 1-'],
                ['PVSNP', 'VIP SNP Study-'], ['TUMCON', 'Tumour Content %'],
                ['VREP01', 'Variant Result 01 Reportable?'], ['VREP02', 'Variant Result 02 Reportable?'],
                ['VREP03', 'Variant Result 03 Reportable?'], ['VREP04', 'Variant Result 04 Reportable?']]

    static class Maker {
        private MersenneTwister rng
        private Double compoundFamilyNameProb
        private Double extraGivenNameProb
        private List sexWeights
        private Map nameListMap

        Object oneof(List items) {
            Integer i = items.size() * rng.nextDouble()
            return items[i]
        }

        Object weighted(List items) {
            Double u = rng.nextDouble()
            for (List itm : items) {
                if (u < itm[0]) {
                    return itm[1]
                }
                u -= itm[0]
            }
        }

        List multiNameParts(List names, Double prob) {
            List parts = []
            parts << oneof(names)
            while (rng.nextDouble() < compoundFamilyNameProb) {
                def nm = oneof(names)
                while (nm in parts) {
                    nm = oneof(names)
                }
                parts << nm
            }
            return parts
        }

        String mkName(String gender) {
            String first = multiNameParts(nameListMap[gender], extraGivenNameProb).join(' ')
            String last = multiNameParts(family, compoundFamilyNameProb).join('-')
            return "${first} ${last}"
        }

        String mkSex() {
            return weighted(sexWeights)
        }

        String mkDateOfBirth() {
            Double t = 1980 + 10 * rng.nextGaussian()
            Integer y = Math.floor(t)
            t = 12*(t - y)
            Integer m = Math.floor(t)
            t = 28*(t - m)
            Integer d = Math.floor(t)
            return sprintf('%4d%02d%02d', [y, m, d])
        }

        String mkDateAgo() {
            Double t = rng.nextGaussian()
            if (t > 0) {
                t = -t
            }
            int dt = 50*t
            Calendar now = Calendar.getInstance()
            now.add(Calendar.DAY_OF_YEAR, dt)
            Integer y = now.get(Calendar.YEAR)
            Integer m = now.get(Calendar.MONTH)
            Integer d = now.get(Calendar.DAY_OF_MONTH)
            return sprintf('%4d%02d%02d', [y, m, d])
        }

        String mkDrName() {
            if (rng.nextDouble() > 0.5) {
                return "Dr ${oneof(male)[0]} ${multiNameParts(family, compoundFamilyNameProb).join('-')}"
            } else {
                return "Dr ${oneof(female)[0]} ${multiNameParts(family, compoundFamilyNameProb).join('-')}"
            }
        }

        String mkPathLab() {
            return oneof(pathology)
        }

        String mkDigits(Integer n) {
            List ds = []
            for (int i = 0; i < n; ++i) {
                ds << oneof(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'])
            }
            return ds.join('')
        }

        String mkURN() {
            List parts = []
            for (int i = 0; i < 8; ++i) {
                parts << oneof(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'])
            }
            return parts.join('')
        }

        Map mkPatient() {
            Map m = [:]
            m['urn'] = mkURN()
            m['sex'] = mkSex()
            m['name'] = mkName(m['sex'])
            m['dob'] = mkDateOfBirth()
            return ['patient':m]
        }

        Map mkSample(String urn) {
            List ds = [mkDateAgo(), mkDateAgo()].sort()
            String smpl = "0800${mkDigits(4)}"
            Map m = [:]
            m['urn'] = urn
            m['sample'] = smpl
            m['requestDate'] = ds[0]
            m['rcvdDate'] = ds[1]
            m['requester'] = mkDrName()
            m['pathlab'] = mkPathLab()
            return ['patSample':m]
        }

        Map mkAssay(String sample) {
            def assay = oneof(testSets)
            Map m = [:]
            m['patSample'] = sample
            m['testSet'] = assay[0]
            m['testName'] = assay[1]
            return ['patAssay':m]
        }

        Map mkCreateOrUpdate() {
            List itms = []

            Map p = mkPatient()
            itms << p

            String urn = p['patient']['urn']
            while (true) {
                Map s = mkSample(urn)
                itms << s
                String smpl = s['patSample']['sample']
                while (true) {
                    Map a = mkAssay(smpl)
                    itms << a
                    if (rng.nextDouble() < 0.2) {
                        break
                    }
                }
                if (rng.nextDouble() < 0.9) {
                    break
                }
            }
            return ['patient':['createOrUpdate':itms]]
        }

        Maker() {
            rng = new MersenneTwister()
            compoundFamilyNameProb = 0.2
            extraGivenNameProb = 0.2

            sexWeights = [
                [0.45, 'M'],
                [0.45, 'F'],
                [0.025, 'A'],
                [0.025, 'I'],
                [0.025, 'O'],
                [0.025, 'U']
            ]

            nameListMap = [:]
            nameListMap['M'] = male
            nameListMap['F'] = female
            nameListMap['A'] = neutral
            nameListMap['I'] = neutral
            nameListMap['O'] = neutral
            nameListMap['U'] = neutral
        }
    }

    static void main(args) {
        def m = new Maker()
        def yaml = new YamlCodec()
        for (int i = 0; i < 100; ++i) {
            println yaml.dump(m.mkCreateOrUpdate())
        }
    }
}
