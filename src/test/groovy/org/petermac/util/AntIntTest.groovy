package org.petermac.util

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 8/05/13
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */

class AntIntTest extends GroovyTestCase
{
    def testDir = "./Ant/"
    def fromDir = testDir + "fromdir/"
    def toDir   = testDir + "todir/"
    def vcfDir  = testDir + "vcfdir/"

    void testCopy()
    {
        //TODO: legacy code
//        def ant = new AntBuilder()
//
//        // lets just call one task
//        ant.echo("testing copy")
//
//        // here is an example of a block of Ant inside GroovyMarkup
//        ant.sequential
//                {
//                    delete(dir: toDir)
//                    mkdir(dir: toDir)
//                    copy(todir: toDir)
//                            {
//                                fileset(dir: fromDir)
//                                        {
//                                            include(name: "**/*p53")
//                                        }
//                            }
//                }
//
//        def f = new File( toDir + "130422_Hiseq_p53")
//        assert (f.exists())
//        f = new File(toDir + "130422_Hiseq_p53")
//        assert (f.exists())
//        ant.delete( file: toDir+"130422_Hiseq_p53" )
//        assert( ! f.exists())
    }

    void testVcf()
    {
        //TODO: legacy code
//        def ant = new AntBuilder()
//
//        // lets just call one task
//        ant.echo("testing vcf")
//
//        // here is an example of a block of Ant inside GroovyMarkup
//        ant.sequential
//                {
//                    delete(file: vcfDir+"mp_vcf.tsv")
//                    mkdir(dir: vcfDir)
//                    concat( destfile:vcfDir+"mp_vcf.tsv", fixlastline:"yes")
//                            {
//                                filterchain()
//                                        {
//                                            striplinecomments() { comment(value: '##') }
//                                            headfilter( lines:1)
//                                        }
//                                fileset(dir: fromDir) { include(name: "**/*.vcf.tsv") }
//                            }
//                    concat( destfile:vcfDir+"mp_vcf.tsv", append: "yes", fixlastline:"yes")
//                            {
//                                filterchain()
//                                        {
//                                            striplinecomments() { comment(value: '#') }
//                                            tokenfilter() { replaceregex( pattern:'%', replace:'xxxx')}
//                                        }
//                                fileset(dir: fromDir) { include(name: "**/*.vcf.tsv") }
//                            }
//                }
//
//        def f = new File( vcfDir + "mp_vcf.tsv" )
//        assert( f.exists())
    }
}