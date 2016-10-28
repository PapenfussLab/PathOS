import grails.util.GrailsUtil
import org.petermac.pathos.curate.*
import org.petermac.pathos.curate.AuthRole
import org.petermac.pathos.curate.AuthUser
import org.petermac.pathos.curate.AuthUserAuthRole
import org.petermac.util.Locator
import java.text.SimpleDateFormat

class BootStrap
{
    def grailsApplication
    def loc  = Locator.instance
    /**
     * Create initial Roles and Users for bootstrapping development
     */
    void makeBaseSpringRoles() {
        AuthRole thisRole
        thisRole = AuthRole.findByAuthority("ROLE_ADMIN") as AuthRole
        if (thisRole == null)
        { //make a new role if we found nothing in the DB
            thisRole = new AuthRole(authority: 'ROLE_ADMIN').save(flush: true,failOnError:true)
        }

        thisRole = AuthRole.findByAuthority("ROLE_CURATOR") as AuthRole
        if (thisRole == null)
        {
            //make a new role if we found nothing in the DB
            thisRole = new AuthRole(authority: 'ROLE_CURATOR').save(flush: true,failOnError:true)
        }

        thisRole = AuthRole.findByAuthority("ROLE_VIEWER") as AuthRole
        if (thisRole == null)
        {
            //make a new role if we found nothing in the DB
            thisRole = new AuthRole(authority: 'ROLE_VIEWER').save(flush: true,failOnError:true)
        }

        thisRole = AuthRole.findByAuthority("ROLE_LAB") as AuthRole
        if (thisRole == null)
        {
            //make a new role if we found nothing in the DB
            thisRole = new AuthRole(authority: 'ROLE_LAB').save(flush: true,failOnError:true)
        }

        thisRole = AuthRole.findByAuthority("ROLE_EXPERT") as AuthRole
        if (thisRole == null)
        {
            //make a new role if we found nothing in the DB
            thisRole = new AuthRole(authority: 'ROLE_EXPERT').save(flush: true,failOnError:true)
        }

        thisRole = AuthRole.findByAuthority("ROLE_DEV") as AuthRole
        if (thisRole == null)
        {
            //make a new role if we found nothing in the DB
            thisRole = new AuthRole(authority: 'ROLE_DEV').save(flush: true,failOnError:true)
        }

    }

    void makeBaseClinContexts()
    {
        def clinContextMaps = ['Actinic Keratosis':'AK',
                              'Acute Lymphoblastic Leukemia':'ALL',
                              'Acute Nonlymphocytic Leukemias':'ANLL',
                              'Acute Promyelocytic Leukemia':'APL',
                              'Aggressive Systemic Mastocytosis':'ASM',
                              'B-cell Non-Hodgkin Lymphoma':'BNHL',
                              'Basal Cell Carcinoma Breast cancer':'BCCBC',
                              'Bladder Cancer':'BLC',
                              'Breast Cancer':'BC',
                              'Cervical Cancer':'CC',
                              'Chronic Myelogenous Leukemia':'CLL',
                              'Colorectal Cancer':'CRC',
                              'Gastric Adenocarcinoma':'GA',
                              'Cutaneous T-cell Lymphoma':'CTL',
                              'Dukes stage C colon cancer':'DSCCC',
                              'Follicular Non-Hodgkin Lymphoma':'FNHL',
                              'Gastric or Gastroesophageal':'GOG',
                              'Junction Adenocarcinoma':'GEJA',
                              'Head and Neck Cancer':'HNC',
                              'Hodgkin Lymphoma ':'HL',
                              'Large B-cell Non-Hodgkin Lymphoma':'LBNHL',
                              'Leukemia':'LEU',
                              'Lymphoma':'LYMPH',
                              'Malignant Mesothelioma':'MM',
                              'Melanoma':'MEL',
                              'Non-small Cell Lung Cancer':'NSCLC',
                              'Ovarian Cancer':'OC',
                              'Pancreatic Cancer':'PC',
                              'Testicular Cancer':'TC',
                              'Cancer of unknown primary':'CUP',
                              'Germline':'GL'
        ]

        clinContextMaps.each{ desc, code ->
            if(!ClinContext.findByCode(code) && !ClinContext.findByDescription(desc)) {
                def mc = new ClinContext(code:code,description:desc).save(flush:true,failOnError:false)
            }
        }
    }

    void makeFilterTemplates() 
    {


       def defaultTemplates =
        [ 
                'topSom':["Top Somatic", """{ 'groupOp': 'AND',
                            'rules': [
                                { 'field': 'filterFlag', 'op': 'nc', 'data': 'blk' },
                                { 'field': 'filterFlag', 'op': 'nc', 'data': 'pnl' },
                                { 'field': 'filterFlag', 'op': 'nc', 'data': 'gaf' },
                                { 'field': 'filterFlag', 'op': 'nc', 'data': 'con' },
                                { 'field': 'filterFlag', 'op': 'nc', 'data': 'sin' },
                                { 'field': 'varFreq', 'op': 'ge', 'data': '3'},
                                { 'field': 'varcaller', 'op': 'ne', 'data': 'Canary' }
                        ]
                        }"""],
                'topCRC':["Top Colorectal","""{ "groupOp": "AND",
                        "groups": [
                        {"groupOp": "OR",
                          "rules": [
                            { "field": "gene", "op": "eq", "data": "BRAF" },
                            { "field": "gene", "op": "eq", "data": "KRAS" },
                            { "field": "gene", "op": "eq", "data": "NRAS" }
                          ]
                        }],
                          "rules": [
                            { "field": "filterFlag", "op": "nc", "data": "blk" },
                            { "field": "filterFlag", "op": "nc", "data": "pnl" },
                            { "field": "filterFlag", "op": "nc", "data": "gaf" },
                            { "field": "filterFlag", "op": "nc", "data": "con" },
                            { "field": "filterFlag", "op": "nc", "data": "sin" },
                            { "field": "varFreq", "op": "ge", "data": "3"}
                          ]
                    };
                    """],
                'topMel':["Melanoma",'''
                            { "groupOp": "AND",
                                "groups": [
                                {"groupOp": "OR",
                                  "rules": [
                                    { "field": "gene", "op": "eq", "data": "BRAF" },
                                    { "field": "gene", "op": "eq", "data": "NRAS" },
                                    { "field": "gene", "op": "eq", "data": "RAC1" },
                                    { "field": "gene", "op": "eq", "data": "KIT" }
                                  ]
                                }],
                                  "rules": [
                                    { "field": "filterFlag", "op": "nc", "data": "blk" },
                                    { "field": "filterFlag", "op": "nc", "data": "pnl" },
                                    { "field": "filterFlag", "op": "nc", "data": "gaf" },
                                    { "field": "filterFlag", "op": "nc", "data": "con" },
                                    { "field": "filterFlag", "op": "nc", "data": "sin" },
                                    { "field": "varFreq", "op": "ge", "data": "3"},
                                    { "field": "varcaller", "op": "ne", "data": "Canary" }
                                  ]
                            }
                            '''],
                'topLung':["Lung",'''
                                { "groupOp": "AND",
                                    "groups": [
                                    {"groupOp": "OR",
                                      "rules": [
                                        { "field": "gene", "op": "eq", "data": "BRAF" },
                                        { "field": "gene", "op": "eq", "data": "KRAS" },
                                        { "field": "gene", "op": "eq", "data": "MET" },
                                        { "field": "gene", "op": "eq", "data": "EGFR" }
                                      ]
                                    }],
                                      "rules": [
                                        { "field": "filterFlag", "op": "nc", "data": "blk" },
                                        { "field": "filterFlag", "op": "nc", "data": "pnl" },
                                        { "field": "filterFlag", "op": "nc", "data": "gaf" },
                                        { "field": "filterFlag", "op": "nc", "data": "con" },
                                        { "field": "filterFlag", "op": "nc", "data": "sin" },
                                        { "field": "varFreq", "op": "ge", "data": "3"},
                                        { "field": "varcaller", "op": "ne", "data": "Canary" }
                                      ]
                                }
                                                '''],
                'topGist':["GIST", '''
                                                { "groupOp": "AND",
                                    "groups": [
                                    {"groupOp": "OR",
                                      "rules": [
                                        { "field": "gene", "op": "eq", "data": "PDGFRA" },
                                        { "field": "gene", "op": "eq", "data": "KIT" }
                                      ]
                                    }],
                                      "rules": [
                                        { "field": "filterFlag", "op": "nc", "data": "blk" },
                                        { "field": "filterFlag", "op": "nc", "data": "pnl" },
                                        { "field": "filterFlag", "op": "nc", "data": "gaf" },
                                        { "field": "filterFlag", "op": "nc", "data": "con" },
                                        { "field": "filterFlag", "op": "nc", "data": "sin" },
                                        { "field": "varFreq", "op": "ge", "data": "3"},
                                        { "field": "varcaller", "op": "ne", "data": "Canary" }
                                      ]
                                }
                                                '''],
                'topGerm':["Top Germline",'''
                    { "groupOp": "AND",
                          "rules": [
                            { "field": "filterFlag", "op": "nc", "data": "blk" },
                            { "field": "filterFlag", "op": "nc", "data": "pnl" },
                            { "field": "filterFlag", "op": "nc", "data": "gaf" },
                            { "field": "filterFlag", "op": "nc", "data": "con" },
                            { "field": "varFreq", "op": "ge", "data": "15"},
                            { "field": "varcaller", "op": "ne", "data": "Canary" }
                          ]
                    }
                    '''],
                'topHaem':["Top Haem",'''
                { "groupOp": "AND",
                      "rules": [
                        { "field": "filterFlag", "op": "nc", "data": "blk" },
                        { "field": "filterFlag", "op": "nc", "data": "pnl" },
                        { "field": "filterFlag", "op": "nc", "data": "gaf" },
                        { "field": "filterFlag", "op": "nc", "data": "con" },
                        { "field": "varFreq", "op": "ge", "data": "3"},
                      ]
                }'''],
                'mpnSimple':["MPN Simple",'''
                        { "groupOp": "AND",
                            "groups": [
                            {"groupOp": "OR",
                              "rules": [
                                { "field": "gene", "op": "eq", "data": "JAK2"  },
                                { "field": "gene", "op": "eq", "data": "MPL"   },
                                { "field": "gene", "op": "eq", "data": "CALR"  },
                                { "field": "gene", "op": "eq", "data": "KIT"  },
                                { "field": "gene", "op": "eq", "data": "SF3B1"  },
                                { "field": "gene", "op": "eq", "data": "CSF3R"  },
                                { "field": "gene", "op": "eq", "data": "ASXL1" }
                              ]
                            }],
                              "rules": [
                                { "field": "filterFlag", "op": "nc", "data": "blk" },
                                { "field": "filterFlag", "op": "nc", "data": "pnl" },
                                { "field": "filterFlag", "op": "nc", "data": "gaf" }
                               ]
                        }
                '''],
                'brcaOnly':["BRCA Only",'''
                                        { "groupOp": "AND",
                        "groups": [
                        {"groupOp": "OR",
                          "rules": [
                            { "field": "gene", "op": "eq", "data": "BRCA1" },
                            { "field": "gene", "op": "eq", "data": "BRCA2" }
                          ]
                        }],
                          "rules": [
                            { "field": "filterFlag", "op": "nc", "data": "blk" },
                            { "field": "varFreq", "op": "ge", "data": "15"},
                            { "field": "varcaller", "op": "ne", "data": "Canary" }
                          ]
                    }
                '''],
                reportableVars:["Reportable",'''
                        { "groupOp": "AND",
                              "rules": [
                                { "field": "reportable", "op": "eq", "data": "1"}
                              ]
                        }'''],
                'rahmanGenes':["Rahman Genes", '''
                        {"groupOp": "AND",
                          "rules": [
                            { "field": "gene", "op": "in", "data": "ABCB11,ALK,APC,ATM,AXIN2,BAP1,BLM,BMPR1A,BRCA1,BRCA2,BRIP1,BUB1B,CBL,CDC73,CDH1,CDK4,CDKN1B,CDKN2A,CEBPA,CHEK2,COL7A1,CYLD,DDB2,DICER1,DIS3L2,DKC1,DOCK8,EGFR,ELANE,ERCC2,ERCC3,ERCC4,ERCC5,EXT1,EXT2,FAH,FANCA,FANCC,FANCG,FH,FLCN,GATA2,GBA,GJB2,GPC3,HFE,HMBS,HRAS,ITK,KIT,MAX,MEN1,MET,MLH1,MSH2,MSH6,MTAP,MUTYH,NBN,NF1,NF2,PALB2,PDGFRA,PHOX2B,PMS2,POLD1,POLE,POLH,PRKAR1A,PRSS1,PTCH1,PTEN,PTPN11,RAD51C,RAD51D,RB1,RECQL4,RET,RHBDF2,RMRP,RUNX1,SBDS,SDHA,SDHAF2,SDHB,SDHC,SDHD,SERPINA1,SH2D1A,SLC25A13,SMAD4,SMARCA4,SMARCB1,SMARCE1,SOS1,SRY,STAT3,STK11,SUFU,TERT,TGFBR1,TMEM127,TNFRSF6,TP53,TRIM37 ,TSC1,TSC2,UROD,VHL,WAS,WRN,WT1,XPA,XPC"
                            }
                        ]
                    }'''],
                'targetGenes':['TARGET Genes','''
                        {"groupOp": "AND",
                          "rules": [
                            { "field": "gene", "op": "in", "data": "ABL1,AKT1,AKT2,AKT3,ALK,APC,AR,ARAF,ASXL1,ATM,ATR,AURKA,BAP1,BCL2,BRAF,BRCA1,BRCA2,BRD2,BRD3,BRD4,NUTM1,CCND1,CCND2,CCND3,CCNE1,CDH1,CDK12,CDK4,CDK6,CDKN1A,CDKN1B,CDKN2A,CDKN2B,CEBPA,CREBBP,CRKL,CTNNB1,DDR2,DNMT3A,EGFR,EPHA3,ERBB2,ERBB3,ERBB4,ERCC2,ERG,ERRFI1,ESR1,ETV1,ETV4,ETV5,ETV6,EWSR1,EZH2,FBXW7,FGFR1,FGFR2,FGFR3,FLCN,FLT3,GNA11,GNAQ,GNAS,HRAS,IDH1,IDH2,IGF1R,JAK2,JAK3,KDR,KIT,KRAS,MAP2K1,MAP2K2,MAP2K4,MAP3K1,MAPK1,MAPK3,MCL1,MDM2,MDM4,MED12,MEN1,MET,MITF,MLH1,KMT2A,MPL,MSH2,MSH6,MTOR,MYC,MYD88,NF1,NF2,NFKBIA,NKX2-1,NOTCH1,NOTCH2,NPM1,NRAS,NTRK3,PDGFRA,PDGFRB,PIK3CA,PIK3CB,PIK3R1,PTCH1,PTEN,RAB35,RAF1,RARA,RB1,RET,RHEB,RNF43,ROS1,RSPO2,RUNX1,SMAD2,SMAD4,SMARCA4,SMARCB1,SMO,STK11,SYK,TET2,TMPRSS2,TP53,TSC1,TSC2,VHL,WT1,XPO1,ZNRF3,PALB2,CSF1R,HNF1A,PTPN11,SRC"
                            }
                        ]
                    }''']
        ]
//        ["Top Somatic", "Colorectal", "Melanoma", "Lung", "GIST", "Top Germline", "Top Haem", "MPN Simple", "BRCA Only", "Reportable", "Rahman Genes", "TARGET Genes"]
  //      [ 'topSom','topCrc','topMel','topLung','topGist','topGerm','topHaem','mpnSimple','brcaOnly','reportableVars','rahmanGenes','targetGenes']]
                defaultTemplates.each{ name, template ->
                    if(!FilterTemplate.findByTemplateName(name) && !FilterTemplate.findByTemplate(template)) {
                        def ft = new FilterTemplate(templateName:name,displayName:template[0],template:template[1]).save(flush:true)
                    }
                }

    }
    
    
    
    void makeBaseSpringUsers()
    {

        //  make debug users
        //
        AuthRole viewerRole
        AuthRole adminRole
        AuthRole curatorRole
        AuthRole labRole
        AuthRole expertRole
        AuthRole devRole

        String defaultpassword = loc.defaultTestUserPassword

        devRole = AuthRole.find{authority=="ROLE_DEV"}
        viewerRole = AuthRole.find{authority=="ROLE_VIEWER"}
        adminRole = AuthRole.find{authority=="ROLE_ADMIN"}
        curatorRole = AuthRole.find{authority=="ROLE_CURATOR"}
        labRole = AuthRole.find{authority=="ROLE_LAB"}
        expertRole = AuthRole.find{authority=="ROLE_EXPERT"}

        if (AuthUser.findByUsername('pathosviewer') == null)
        {
            def testViewerUser = new AuthUser(username: 'pathosviewer', password: defaultpassword,displayName: 'Pathos Viewer',email:'no.such.user@petermac.org')
            if (testViewerUser.save(flush: true,failOnError:false))
            {
                AuthUserAuthRole.create testViewerUser, viewerRole, true
            }
        }

        if (AuthUser.findByUsername('pathosadmin') == null)
        {
            def testAdminUser = new AuthUser(username: 'pathosadmin', password: defaultpassword,displayName: 'Pathos Admin',email:'no.such.user@petermac.org')
            if (testAdminUser.save(flush: true,failOnError:false))
            {
                AuthUserAuthRole.create testAdminUser, adminRole, true
            }
        }

        if (AuthUser.findByUsername('pathoscurator') == null)
        {
            def testCuratorUser = new AuthUser(username: 'pathoscurator', password: defaultpassword,displayName: 'Pathos Curator',email:'no.such.user@petermac.org')
            if (testCuratorUser.save(flush: true,failOnError:false))
            {
                AuthUserAuthRole.create testCuratorUser, curatorRole, true
            }
        }


        if (AuthUser.findByUsername('pathoslab') == null)
        {
            def testCuratorUser = new AuthUser(username: 'pathoslab', password: defaultpassword,displayName: 'Pathos Lab',email:'no.such.user@petermac.org')
            if (testCuratorUser.save(flush: true,failOnError:false))
            {
                AuthUserAuthRole.create testCuratorUser, labRole, true
            }
        }

        if (AuthUser.findByUsername('pathosexpert') == null)
        {
            def testCuratorUser = new AuthUser(username: 'pathosexpert', password: defaultpassword,displayName: 'Pathos Expert',email:'no.such.user@petermac.org')
            if (testCuratorUser.save(flush: true,failOnError:false))
            {
                AuthUserAuthRole.create testCuratorUser, expertRole, true
            }
        }

        if (AuthUser.findByUsername('pathosdev') == null)
        {
            def testCuratorUser = new AuthUser(username: 'pathosdev', password: defaultpassword,displayName: 'Pathos Developer',email:'no.such.user@petermac.org')
            if (testCuratorUser.save(flush: true,failOnError:false))
            {
                AuthUserAuthRole.create testCuratorUser, devRole, true
            }
        }

        if (AuthUser.findByUsername('pathosguest') == null)
        {
            def testGuestUser = new AuthUser(username: 'pathosguest', password: defaultpassword,displayName: 'Pathos Guest',email:'no.such.user@petermac.org')
            if (testGuestUser.save(flush: true,failOnError:true))
            {
                //AuthUserAuthRole.create testCuratorUser, expertRole, true
                //NO ROLE for guest
            }
        }



    }

    def searchableService

    /**
     * Create users but only for development environments
     *
     */
    def init =
    {
        servletContext ->


            //  Bootstrap users for development environment
        //
        switch(GrailsUtil.environment)
        {

            case ["pa_uat","pa_test","pa_dev"]:
                makeBaseSpringRoles()
                makeBaseSpringUsers()
                makeBaseClinContexts()

                break;

            case "pa_local":
                makeBaseSpringRoles()
                makeBaseSpringUsers()
                makeBaseClinContexts()
                makeFilterTemplates()
                break;

            case "pa_prod":
                //makeBaseSpringUsers()
                break;
        }

        // Don't bother reindexing Searchable if the environment is pa_local
        // On pa_local, just press the "reindex" button in Admin Options to reindex
        // DKGM 6-Sept-2016
        if(GrailsUtil.environment != "pa_local") {
            println "Reindexing the search"
            searchableService.reindex()
        } else {
            println "GrailsUtil.environment is pa_local, don't bother reindexing the search"
        }

        // Manually start the mirroring process to ensure that it comes after the automated migrations.
        println "Starting Searchable mirroring service"
        searchableService.startMirroring()
    }

    def destroy =
    {
    }
}
