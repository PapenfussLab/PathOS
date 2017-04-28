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

            //  Make default filtering templates
            //



            //  Bootstrap users for development environment
            //
            switch(GrailsUtil.environment)
            {
                case [ 'pa_uat', 'pa_test', 'pa_dev' ]:
                    makeBaseSpringRoles()
                    makeBaseSpringUsers()
                    makeBaseClinContexts()
                    break;

                case 'pa_local':
                    makeBaseSpringRoles()
                    makeBaseSpringUsers()
                    makeBaseClinContexts()
                    break;

                case 'pa_prod':
                    //makeBaseSpringUsers()
                    break;
            }

            // Don't bother reindexing Searchable if the environment is pa_local
            // On pa_local, just press the "reindex" button in Admin Options to reindex
            // DKGM 6-Sept-2016
            if(GrailsUtil.environment != 'pa_local' ) {
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
