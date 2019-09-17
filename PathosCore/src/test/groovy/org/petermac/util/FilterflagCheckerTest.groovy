package org.petermac.util

/**
 * Created by lara luis on 25/01/2016.
 */
class FilterflagCheckerTest extends GroovyTestCase
{

    String DB
    Boolean isPaLocal
    /* DEPRECATED

    //TODO WE need to re-assess these functions
    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

        if(DB == 'pa_local')
            isPaLocal = true
        else
            isPaLocal = false

    }

    void testCheckFiltersT()
    {
        def fc  = new FilterflagChecker()

        // TODO: Validate command for null
        fc.checkFilters(DB, true)
    }

    void testCheckFiltersF()
    {
        def fc = new FilterflagChecker()
        fc.checkFilters(DB, false)
    }


    void  testGetFilterflagIdsForRuleset( )
    {
        def fc  = new FilterflagChecker()

        fc.getFilterflagIdsForRuleset(DB, 'MP Development',['nof':'MP Development'])
    }

    void testGetFilterRulesConfig()
    {

        //def cfg = new ConfigSlurper().parse(rf.toURL())// to.URL deprecated?
        assert false, "Where is the rules file?"
    }

    // Takes a lot of time!!
    void testGetOorSeqvars()
    {
        def fc  = new FilterflagChecker()
        def db   = new DbConnect( DB )
        def sql = db.sql()


    }

    void testGetOorSeqvarsNoFlagSet()
    {
        def fc  = new FilterflagChecker()
        def db   = new DbConnect( DB )
        def sql = db.sql()
        println fc.getOorSeqvarsNoFlagSet(sql)
    }

    void testGetNotOorSeqvarsFlagSet()
    {
        def fc  = new FilterflagChecker()
        def db   = new DbConnect( DB )
        def sql = db.sql()
        println fc.getNotOorSeqvarsFlagSet( sql )
        //query fails
        assert false
    }

    void testListToStringForSql( )
    {
        def fc  = new FilterflagChecker()
        assert (fc.listToStringForSql([1,2,3,4,5]) == "\'1\',\'2\',\'3\',\'4\',\'5\'" &&
                (fc.listToStringForSql(["A","B","C","D","E"]) == "\'A\',\'B\',\'C\',\'D\',\'E\'" ) &&
                (fc.listToStringForSql(["AB","BC","CD","DE"]) == "\'AB\',\'BC\',\'CD\',\'DE\'" ) )

    }

    void testDuplicateSampleSingletons()
    {
        def fc  = new FilterflagChecker()
        def db   = new DbConnect( DB )
        def sql = db.sql()
        println fc.duplicateSampleSingletons(sql)
    }

    void testSingletonVars()
    {
        def fc  = new FilterflagChecker()
        def db   = new DbConnect( DB )
        def sql = db.sql()

        def run = ["3" , "150217" ,"10"]

        assert false, "singleton Vars is called by testDuplicateSampleSingletons"
        fc.singletonVars(run,sql)
    }

    */



}
