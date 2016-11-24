package org.petermac.util

/**
 * Created by lara luis on 19/01/16.
 */
class AuditLogTest extends GroovyTestCase
{
    /**
        Test loading class Audit
    */
    void testAuditLogConstructor()
    {
        def au = new AuditLog()
        assert au instanceof AuditLog:"[T E S T]: cannot load class AuditLog()"
    }

    /**
        Failing proving an invalid constructor
    */
    void testFailLoaAudit()
    {
        def Error = shouldFail GroovyRuntimeException, {  new AuditLog("d") }
        assert Error.contains("Could not find matching constructor for") : "[T E S T]: Class is loading with an invalid constructor parameter, new AuditLog(\"d\")  "
    }

    /**
        Testing  AuditLog.logAudit( File out, String category, String task, Map params)

        Test the creation writing of ./src/test/resources/Audit_TEST.txt
        This test will assert 3 main tasks,
        1) The creation of the file and writing 5 lines of elements.
        2) Checking the number of columns.
        3) Checking if the file got deleted.
     */
    void testLogAudit()
    {

        def params = ['seqrun':'ACTG','variant':'VAR', 'sample':'SAMPLE', 'complete':'', 'elapsed':'25', 'software':'PathOS','version':'2.0','username':'luis lara', 'description':'DESCRIPTION' ]
        File TestFile = new File("src/test/resources/Audit_TEST.txt")

        AuditLog.logAudit( TestFile, "pipeline", "sequence",  params)
        AuditLog.logAudit( TestFile, "lims", "sequence",  params)
        AuditLog.logAudit( TestFile, "record", "sequence",  params)
        AuditLog.logAudit( TestFile, "curation", "sequence",  params)
        AuditLog.logAudit( TestFile, "database", "sequence",  params)

        def Data = TestFile.text
        def lines = Data.tokenize('\n')
        def columns = lines[0].tokenize('\t')


        assert lines.size() == 5 : "[T E S T] assert 1/3: src/test/resources/Audit_TEST.txt file does not contain 5 lines"
        assert columns.size() == 11 : "[T E S T] assert 2/3: src/test/resources/Audit_TEST.txt file does not contain 11 columns"

        def Removed_File = TestFile.delete()
        assert Removed_File : "[T E S T] assert 3/3: src/test/resources/Audit_TEST.txt cannot be deleted"

    }

    /**
        Test Failure by invalid input argument
     */
    void testFailLogAudit_Date()
    {

        File TestFile = new File("src/test/resources/Audit_TEST.txt")
        def param = ['seqrun':'ACTG','variant':'VAR', 'sample':'SAMPLE', 'complete':'2016-01-01 00:01:01', 'elapsed':'25', 'software':'PathOS','version':'2.0','username':'luis lara', 'description':'DESCRIPTION' ]

        def Error = shouldFail IllegalArgumentException, { AuditLog.logAudit( TestFile, "database", "dbmerge",  param) }
        assert Error.contains( "Cannot format given Object as a Date") : "[T E S T] assert 2/2: Generated type of error is giving a different comment than,  Cannot format given Object as a Date"

    }


    /**
        Testing provide input arguments that do no get logged.
     */
    void testWarningMessages()
    {
        File TestFile = new File("src/test/resources/Audit_TEST.txt")
        def params = ['seqrun':'ACTG','variant':'VAR', 'sample':'SAMPLE', 'complete':'', 'elapsed':'25', 'software':'PathOS','version':'2.0','username':'luis lara', 'description':'DESCRIPTION' ]
        AuditLog.logAudit( TestFile, "", "dbmerge",  params)
        AuditLog.logAudit( TestFile, "database", "",  params)
        AuditLog.logAudit( TestFile, "", "",  params)
        AuditLog.logAudit( TestFile, "TEST", "dbmerge",  params)
        AuditLog.logAudit( TestFile, "", "TEST",  params)
        AuditLog.logAudit( TestFile, "TEST", "TEST",  params)

        println("[T E S T]: 6 Warning messages should be printed")

        assert TestFile.delete() == false : "[T E S T]: Failed in breaking the test and a log File was created"

    }


}
