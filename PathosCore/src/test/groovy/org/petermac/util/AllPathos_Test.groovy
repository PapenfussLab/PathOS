/*
 * Copyright (c) 2014. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import junit.framework.*


/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 8/05/13
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */

static TestSuite suite()
{
    def suite  = new TestSuite()
    def gsuite = new GroovyTestSuite()

    def test_util = '/usr/local/dev/PathOS/PathosCore/src/test/groovy/org/petermac/util/'
    def test_pipeline = '/usr/local/dev/PathOS/PathosCore/src/test/groovy/org/petermac/pathos/pipeline/'

    // Testing Util tests
    suite.addTestSuite( gsuite.compile( test_util + 'AlignStatsToTsvTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'AuditLogTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'ClassifyTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'CompareTsvTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'DateUtilTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'DbConnectIntTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'DbConnectTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'DbCountTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'DbLockTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'DirCheckerTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'ExtractorTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'FastaTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'FileUtilTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'FilterflagCheckerTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'GATKTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'GenSeqrunIntTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'GoogleCountTest.groovy'))
    //suite.addTestSuite( gsuite.compile( test_util + 'HouseKeepingTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'LocatorTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'LogTestTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'MysqlCommandTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'PubmedTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'SmithWatermanTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'SqlIntTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'StatisticsTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'TestTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'TsvTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'VcfTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'Vcf2TsvTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'VcfCompareTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_util + 'VcfDbCheckTest.groovy'))

    // Testing Pipeline tests
    suite.addTestSuite( gsuite.compile( test_pipeline + 'HGVSIntTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'LocusTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'MakePanelIntTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'MutalyzerIntTest.groovy')) // Temperal comment is failing
    suite.addTestSuite( gsuite.compile( test_pipeline + 'MutalyzerUtilTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'NormaliseVcfTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'SampleNameTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'SeqrunLimsTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'TranscriptIntTest.groovy'))
    suite.addTestSuite( gsuite.compile( test_pipeline + 'UrlLinkTest.groovy'))


    return suite
}

