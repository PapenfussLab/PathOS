package org.petermac.util

/**
 * Created by lara luis on 25/01/2016.
 */
class MysqlCommandTest extends GroovyTestCase
{

    String DB
    Boolean isPaLocal

    void setUp()
    {
        def env = System.getenv()

        DB = env["PATHOS_DATABASE"]

        if(DB == 'pa_local')
            isPaLocal = true
        else
            isPaLocal = false

    }
    /**
     * TESTING Mysqlcommand
     */
    void testClass()
    {
        def dbc = new MysqlCommand(DB)
        assert dbc instanceof MysqlCommand :"[T E S T]: cannot create MysqlCommand() instance"

    }

    /**
     * TESTING  String run( String cmd )
     */
    void testRun()// throws some warnings
    {
        def dbc = new MysqlCommand(DB)
        assert dbc.run( "SELECT DATABASE()" ) != null : "[T E S T]: invalid query"
        assert dbc.run( "SHOW TABLES" ) != null :"[T E S T]: invalid query"
        assert dbc.run("SELECT version()") != null : "[T E S T]: invalid query"
        assert dbc.run("select * from seqrun LIMIT 100 \\G") != null : "[T E S T]: invalid query"
        assert dbc.run("SELECT  sum(round(((data_length + index_length) / 1024 / 1024 / 1024), 2))  as 'Size in GB' FROM information_schema.TABLES  WHERE table_schema = 'dbtest'") != null :"[T E S T]: invalid query"

    }
    /**
     * TESTING    String snapshot( String dir, List tables = [] )
     */
    void testSnapshot()
    {
        def dbc = new MysqlCommand(DB)

        File dir = new File (Locator.getInstance().backupDir())
        if(dir.exists())
        {
            String outFile = dbc.snapshot("${dir.toString()}/backup_${DB}")
            File file = new File(outFile)
            assert file.exists():"[T E S T]: snapshot file does not exist"
            file.delete()

        }
        else
            assert false, "[T E S T]: Backup Dir does not exists"

    }
    /**
     * TESTING String backup( String backupFile, List tables = [] )
     */
    void testBackupCmd()
    {
        def dbc = new MysqlCommand(DB)

        File dir = new File (Locator.getInstance().backupDir())
        println dir
        if(dir.exists())
        {
            dbc.backup("${dir.toString()}/backup_${DB}")
            File file = new File("${dir.toString()}/backup_${DB}")
            assert file.exists():"[T E S T]: Backup file in ${dir.toString()}/backup_${DB} does not exist"
            file.delete()
        }
        else
            assert false, "[TEST]: Backup Dir does not exists"

    }

}


