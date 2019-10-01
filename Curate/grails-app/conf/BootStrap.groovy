import org.petermac.pathos.curate.*
import org.petermac.pathos.curate.AuthRole
import org.petermac.pathos.curate.AuthUser
import org.petermac.pathos.curate.AuthUserAuthRole
import org.petermac.util.Locator
import org.petermac.util.DbConnect

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

        thisRole = AuthRole.findByAuthority("ROLE_UNMASKER") as AuthRole
        if (thisRole == null)
        { //make a new role if we found nothing in the DB
            thisRole = new AuthRole(authority: 'ROLE_UNMASKER').save(flush: true,failOnError:true)
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

    /*
     peter mac specific clinical context for PMC environments
     */
    void makeBaseClinContexts()
    {
        def clinContextMaps = [
            'Generic Clinical Context': 'Generic',
            'Colorectal Cancer': 'CRC',
            'Neuroblastoma': 'NB',
            'Head and Neck Cancer': 'HNC',
            'Melanoma': 'MEL',
            'Small Cell Lung Cancer': 'SCLC',
            'Ovarian Cancer': 'OVCA',
            'Testicular Cancer': 'TC',
            'Cancer of unknown primary': 'CUP',
            'Germline': 'GL',
            'Malignant peripheral nerve sheath tumour': 'MPNST',
            'Pleomorphic xanthoastrocytoma': 'PXA',
            'Merkel cell carcinoma': 'MCC',
            'Non-small Cell Lung Cancer': 'NSCLC',
            'Medulloblastoma': 'MB',
            'Mesonephric adenocarcinoma': 'MADC',
            'Non-melanoma skin cancer': 'SKIN',
            'Thymic carcinoma': 'THYMIC',
            'Adenoid Cystic Carcinoma': 'ACC',
            'Gastrointestinal Stromal Tumor': 'GIST',
            'Kidney cancer': 'KIDNEY',
            'Langerhans Cell Histiocytosis': 'LCH',
            'Mesothelioma': 'MES',
            'Sarcoma': 'SARC',
            'Cholangiocarcinoma': 'CCC'
        ]

        clinContextMaps.each{ desc, code ->
            if(!ClinContext.findByCode(code) && !ClinContext.findByDescription(desc)) {
                def mc = new ClinContext(code:code,description:desc).save(flush:true,failOnError:false)
            }
        }
    }

    void makeCvWeights()
    {
        Map guidelines = [
            ACMG: [
                "Unclassified",
                "C1: Not pathogenic",
                "C2: Unlikely pathogenic",
                "C3: Unknown pathogenicity (Level C)",
                "C3: Unknown pathogenicity (Level B)",
                "C3: Unknown pathogenicity",
                "C3: Unknown pathogenicity (Level A)",
                "C4: Likely pathogenic",
                "C5: Pathogenic"
            ],
            AMP: [
                "Unclassified",
                "Tier IV",
                "Tier III",
                "Tier II",
                "Tier I"
            ],
            Overall: [
                "Unclassified",
                "NCS: Not Clinically Significant",
                "UCS: Unclear Clinical Significance",
                "CS: Clinically Significant"
            ]
        ]

        guidelines.each{ guideline, array ->
            array.eachWithIndex { classification, weight ->
                if(!CvWeight.findByGuidelineAndClassification(guideline, classification)) {
                    CvWeight cvw = new CvWeight([
                        guideline: guideline,
                        classification: classification,
                        weight: weight
                    ])
                    cvw.save()
                }
            }
        }
    }

    void makeBasePanels() {
        Panel noPanel = Panel.find{manifest=="NoPanel"}
        if(!noPanel) {
            def newPanel = new Panel(manifest:"NoPanel",panelGroup:"NoPanel").save(flush: true,failOnError:false)
        }

    }


        void makeBaseSpringUsers()
    {

        //  make debug users
        //

        String defaultpassword = loc.defaultTestUserPassword

        AuthRole devRole = AuthRole.find{authority=="ROLE_DEV"}
        AuthRole viewerRole = AuthRole.find{authority=="ROLE_VIEWER"}
        AuthRole adminRole = AuthRole.find{authority=="ROLE_ADMIN"}
        AuthRole curatorRole = AuthRole.find{authority=="ROLE_CURATOR"}
        AuthRole labRole = AuthRole.find{authority=="ROLE_LAB"}
        AuthRole expertRole = AuthRole.find{authority=="ROLE_EXPERT"}
        AuthRole unmaskRole = AuthRole.find{authority=="ROLE_UNMASKER"}

        if(!unmaskRole) {
            unmaskRole = new AuthRole(authority:'ROLE_UNMASKER').save()
        }

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

            //  create default generic clin context if none exists
            //
            def defaultCcCode = ClinContext.defaultClinContextCode
            def defaultCcDesc = ClinContext.defaultClinContextDescription
           if(!ClinContext.findByCode(defaultCcCode)) {
                    def mc = new ClinContext(code:defaultCcCode,description:defaultCcDesc).save(flush:true,failOnError:false)
           }

            //  Bootstrap users for development environment
            //
            if(loc.pathosEnv != 'pa_prod') {
                makeBaseSpringRoles()
                makeBasePanels()
                makeBaseSpringUsers()
                makeBaseClinContexts()
                makeCvWeights()
            }

            // Check the database version against the PathOS version.
            //
            if (true) {
                // The SQL connection checks the version when created,
                // so it is sufficient to simply create it then discard it.
                def db = new DbConnect(loc.pathosEnv)
                def sql = db.sql()
                sql.close()
            }

            // Don't bother reindexing Searchable if the environment is in list of exclusions
            // On exclusions, just press the "reindex" button in Admin Options to reindex
            // DKGM 6-Sept-2016

            ArrayList<String> exclusions = [
//                'pa_test',
//                'pa_uat',
//                'pa_dev',
                'pa_local'
            ]

            if( exclusions.indexOf(loc.pathosEnv) == -1 ) {
                println "Reindexing the search"
                searchableService.reindex()
            } else {
                println "loc.pathosEnv is in list of exclusions, don't bother reindexing the search"
            }

            // Manually start the mirroring process to ensure that it comes after the automated migrations.
            println "Starting Searchable mirroring service"
            searchableService.startMirroring()

            //println "BootStrap done"
    }
}
