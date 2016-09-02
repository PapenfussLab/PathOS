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

    void testClass()
    {
        def dbc = new MysqlCommand(DB)
    }

    void testRun()// throws some warnings
    {
        def dbc = new MysqlCommand(DB)
        dbc.run( "SELECT DATABASE()" )
        dbc.run( "SHOW TABLES" )
        dbc.run("SELECT version()")
        dbc.run("select * from seqrun \\G")
        dbc.run("SELECT  sum(round(((data_length + index_length) / 1024 / 1024 / 1024), 2))  as 'Size in GB' FROM information_schema.TABLES  WHERE table_schema = 'dbtest'")

    }

    void testSnapshot()
    {
        def dbc = new MysqlCommand(DB)

        File dir = new File (Locator.getInstance().backupDir())
        if(dir.exists())
        {
            String outFile = dbc.snapshot("${dir.toString()}/backup_${DB}")
            File file = new File(outFile)
            assert file.exists()
            file.delete()

        }
        else
            assert false, "Backup Dir does not exists"

    }

    void testBackupCmd()
    {
        def dbc = new MysqlCommand(DB)

        File dir = new File (Locator.getInstance().backupDir())
        println dir
        if(dir.exists())
        {
            dbc.backup("${dir.toString()}/backup_${DB}")
            File file = new File("${dir.toString()}/backup_${DB}")
            assert file.exists()
            file.delete()
        }
        else
            assert false, "Backup Dir does not exists"

    }

}


