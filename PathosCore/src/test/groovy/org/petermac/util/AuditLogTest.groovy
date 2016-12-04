package org.petermac.util

/**
 * Created by lara luis on 19/01/16.
 */
class AuditLogTest extends GroovyTestCase
{
    void testLogAudit()
    {
        def au = new AuditLog()

    }

    void testParams()
    {

        // How can I test the time?
        def params = ['seqrun':'ACTG','variant':'VAR', 'sample':'SAMPLE', 'complete':'', 'elapsed':'25', 'software':'PathOS','version':'2.0','username':'luis lara', 'description':'DESCRIPTION' ]
        File f = new File("Audit.txt")

        AuditLog.logAudit( f, "pipeline", "sequence",  params)
        AuditLog.logAudit( f, "lims", "sequence",  params)
        AuditLog.logAudit( f, "record", "sequence",  params)
        AuditLog.logAudit( f, "curation", "sequence",  params)
        AuditLog.logAudit( f, "database", "sequence",  params)

        AuditLog.logAudit( f, "pipeline", "align",  params)
        AuditLog.logAudit( f, "lims", "align",  params)
        AuditLog.logAudit( f, "record", "align",  params)
        AuditLog.logAudit( f, "curation", "align",  params)
        AuditLog.logAudit( f, "database", "align",  params)

        AuditLog.logAudit( f, "pipeline", "annotate",  params)
        AuditLog.logAudit( f, "lims", "annotate",  params)
        AuditLog.logAudit( f, "record", "annotate",  params)
        AuditLog.logAudit( f, "curation", "annotate",  params)
        AuditLog.logAudit( f, "database", "annotate",  params)

        AuditLog.logAudit( f, "pipeline", "register",  params)
        AuditLog.logAudit( f, "lims", "register",  params)
        AuditLog.logAudit( f, "record", "register",  params)
        AuditLog.logAudit( f, "curation", "register",  params)
        AuditLog.logAudit( f, "database", "register",  params)

        AuditLog.logAudit( f, "pipeline", "report",  params)
        AuditLog.logAudit( f, "lims", "report",  params)
        AuditLog.logAudit( f, "record", "report",  params)
        AuditLog.logAudit( f, "curation", "report",  params)
        AuditLog.logAudit( f, "database", "report",  params)

        AuditLog.logAudit( f, "pipeline", "dbload",  params)
        AuditLog.logAudit( f, "lims", "dbload",  params)
        AuditLog.logAudit( f, "record", "dbload",  params)
        AuditLog.logAudit( f, "curation", "dbload",  params)
        AuditLog.logAudit( f, "database", "dbload",  params)

        AuditLog.logAudit( f, "pipeline", "dbmerge",  params)
        AuditLog.logAudit( f, "lims", "dbmerge",  params)
        AuditLog.logAudit( f, "record", "dbmerge",  params)
        AuditLog.logAudit( f, "curation", "dbmerge",  params)
        AuditLog.logAudit( f, "database", "dbmerge",  params)

    }

    void testLogError()
    {
        //TODO:
        //This will break the code, 'complete':'2016-01-01 00:01:01'
      //  param = ['seqrun':'ACTG','variant':'VAR', 'sample':'SAMPLE', 'complete':'2016-01-01 00:01:01', 'elapsed':'25', 'software':'PathOS','version':'2.0','username':'luis lara', 'description':'DESCRIPTION' ]
       // println AuditLog.logAudit( f, "database", "dbmerge",  param)

        def params = ['seqrun':'ACTG','variant':'VAR', 'sample':'SAMPLE', 'complete':'', 'elapsed':'25', 'software':'PathOS','version':'2.0','username':'luis lara', 'description':'DESCRIPTION' ]
        File f = new File("Audit.txt")

        //This following should display an error but not break the code
        AuditLog.logAudit( f, "", "dbmerge",  params)
        AuditLog.logAudit( f, "database", "",  params)
        AuditLog.logAudit( f, "", "",  params)
        AuditLog.logAudit( f, "TEST", "dbmerge",  params)
        AuditLog.logAudit( f, "", "TEST",  params)
        AuditLog.logAudit( f, "TEST", "TEST",  params)
    }


}
