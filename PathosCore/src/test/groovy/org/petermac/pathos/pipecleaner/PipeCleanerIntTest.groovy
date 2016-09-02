package org.petermac.pathos.pipecleaner

/**
 * Created with IntelliJ IDEA.
 * User: ken
 * Date: 27/07/13
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */

class PipeCleanerIntTest extends GroovyTestCase
{
    PipeCleaner pc
    File        cf

    void setUp()
    {
//        pc = new PipeCleaner()
//        cf = new File('/usr/local/dev/PathOS/Run/PipeCleaner/AR001.pc')
    }

    void testConfig()
    {
//        def cnf = pc.config( cf, null )
//        assert cnf,                 "Expecting a config"
//        assert cnf.test,            "Expecting a test block"
//        assert cnf.reference,       "Expecting a ref block"
//        assert cnf.test.testName,   "Expecting a seqrun"
//        assert cnf.reads,           "Expecting a reads block"
//        assert cnf.systemUnderTest, "Expecting a SUT block"
//        assert cnf.report,          "Expecting a report block"
//
//        assert cnf.reads.readDir == "/usr/local/dev/PathOS/Run/PipeCleaner/AmpliconRegression/AR001"
//
//        //  repeat variables
//        //
//        assert cnf.repeat.loopArr.size() == 8
    }

    void testGenReads()
    {
        //assert pc.pipeCleaner( cf, 'reads' )
    }

//    void testPipeline()
//    {
//        assert pc.pipeCleaner( cf, 'test,report' )
//    }

    void testGraph()
    {
      //  assert pc.pipeCleaner( cf, 'graph' )
    }

    void testLoop()
    {
//        def config = new ConfigSlurper().parse(new File('PipeCleaner/test.pc').toURL())
//        println( "NoLoop: Suffix=${config.loopSuffix} idx=${config.loopIdx} cmd=${config.run.cmd}" )
//
//        assert config.repeat
//        def loops = config.repeat.loopArr.size()
//
//        for ( loop in 0..loops-1 )
//        {
//            config = new ConfigSlurper( loop as String ).parse(new File('PipeCleaner/test.pc').toURL())
//            println( "Loop: Suffix=${config.repeat.loopSuffix} idx=${config.repeat.loopIdx} cmd=${config.run.cmd}" )
//
//            if ( loop != 0 )
//                assert loop as String == config.repeat.loopIdx as String
//
//            if ( loop == 3 )
//                assert "run -i 10000 -f AR001.003" == config.run.cmd
//        }  def config = new ConfigSlurper().parse(new File('PipeCleaner/test.pc').toURL())
//        println( "NoLoop: Suffix=${config.loopSuffix} idx=${config.loopIdx} cmd=${config.run.cmd}" )
//
//        assert config.repeat
//        def loops = config.repeat.loopArr.size()
//
//        for ( loop in 0..loops-1 )
//        {
//            config = new ConfigSlurper( loop as String ).parse(new File('PipeCleaner/test.pc').toURL())
//            println( "Loop: Suffix=${config.repeat.loopSuffix} idx=${config.repeat.loopIdx} cmd=${config.run.cmd}" )
//
//            if ( loop != 0 )
//                assert loop as String == config.repeat.loopIdx as String
//
//            if ( loop == 3 )
//                assert "run -i 10000 -f AR001.003" == config.run.cmd
//        }
    }
}
