package org.petermac.util

import groovy.io.FileType
import org.broadinstitute.gatk.tools.walkers.variantutils.ValidateVariants

import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: doig ken
 * Date: 30/04/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
class TestTest extends GroovyTestCase
{
    void testIN()
    {
        println "IN"
        assert true: "This is really interesting, something in GroovyTestCase is failing"
    }

    void testDummy()
    {
        String s = "xxxx URL"

        assert s.contains('URL') : "String function is BAD"
        assert s.endsWith('URL') : "String function is BAD"

    }

    void testDate()
    {
        String seqdate = '131231_blah'
        def sdf = new SimpleDateFormat("yyMMdd")
        Date runDate = sdf.parse(seqdate[0..5])

        assert runDate.toString() =~ /Tue Dec 31 00:00:00 .* 2013/ :"To String command is not working"
    }

    void testParseInsilico()
    {
        String t = 'possibly_damaging(0.57)'

        def m = ( t as String =~ /(\w+)\(([\d\.]+)\)/ )
        log.info( "${t} ${m}")

        assert m.count == 1 : "m = ( t as String =~ /(\\w+)\\(([\\d\\.]+)\\)/ ).count() fails"
        assert m[0][2] == '0.57' : "m[0][2] has adifferent value"
        assert m[0][1] == 'possibly_damaging': "m[0][1] has a different value"
    }

    //TODO: New updates in the DB system do not require this
//    void testVariables()
//    {
//        //def config = System.getenv('PATHOS_CONFIG') ?: config_file
//       // if ( System.getProperty('pathos.config')) config = System.getProperty('pathos.config')
//
//        //def DB = System.getenv('PATHOS_DATABASE')
//
//        def DBEnv = getDB()
//        def DB
//
//        def ProxyEnv = getProxyConf()
//        def httpsHost
//        def httpsPort
//
//        def DBFound = false
//        def ProxyFound = false
//
//        if(!DBEnv[0] || !ProxyEnv[0])
//            println "Enviroment variables Missing ${DBEnv} and ${ProxyEnv}"
//        else
//        {
//            if (DBEnv[0])
//            {
//                DB = DBEnv[1]
//                DBFound = true
//            }
//
//            if (ProxyEnv[0] && ProxyEnv.size() == 3)
//            {
//                httpsHost = ProxyEnv[1]
//                httpsPort = ProxyEnv[2]
//                ProxyFound = true
//            }
//
//            println "ENV ::::: DB: ${DB} and proxy: ${httpsHost} : ${httpsPort}"
//        }
//
//
//
//        if(!DBFound)
//        {
//            println "Checking properties for DB"
//            DB =  System.properties.find { it.key == "pathos.database" }.toString().split('=')[1]
//            if(DB != null)
//            {
//                DBFound = true
//                println "DB found in properties ${DB}"
//            }
//
//        }
//
//        if(!ProxyFound)
//        {
//            println "Checking properties for proxy configuration "
//            httpsHost = System.properties.find { it.key == "https.proxyHost" }.toString().split('=')[1]
//            httpsPort = System.properties.find { it.key == "https.proxyPort" }.toString().split('=')[1]
//
//            if(httpsHost != null && httpsPort != null)
//                ProxyFound = true
//        }
//
//
//        assert DBFound && ProxyFound, "DB or proxy settings not found in env or properties"
//        println "DB: ${DB} and proxy: ${httpsHost} : ${httpsPort}"
//
//    }

//    def getDB()
//    {
//        def DB = System.getenv('PATHOS_DATABASE')
//
//        if (DB == null)
//            return [false, ""]
//
//        if (DB.startsWith("pa_"))
//            return [true, DB]
//        else
//            return [false, ""]
//    }

    def getProxyConf()
    {
        def Host = ""
        def Port = ""
        def Uname =""
        def Pass = ""
        def fullProxy
        def httpsProxy = System.getenv('https_proxy')

        if(httpsProxy == null)
            return [false, "", ""]

        if(httpsProxy.startsWith("http://"))
            httpsProxy = httpsProxy.minus("http://")

        // This is is for a http://host:port pattern
        if(httpsProxy.contains(':')  && !httpsProxy.contains('@'))
        {
            fullProxy = httpsProxy.split(":")
            Host = fullProxy[0]
            Port = fullProxy[1]
        }
        else
        {
            fullProxy = httpsProxy.split(":")
            Uname = fullProxy[0]
            def login = fullProxy[1].split('@')
            Pass = login[0]
            Host = login[1]
            Port = fullProxy[2]
        }

        if(Host == "" && Port == "" && Uname == "" && Pass == "")
            return [false,"",""]
        else if(Host != "" && Port != "" && Uname == "" && Pass == "")
            return [true, Host, Port]
        else
            return [true, Host, Port, Uname, Pass]

    }

//    // TODO: Temporal location of testing pipeline
//    void testPipeline()
//    {
//        def Tolerance = 5.0
//        def list_Muts = []
//        def list_Vcfs = []
//        def VariantMap = [:]
//        def outputMutation
//        def outputVaf
//        def Tests = []
//        Tsv tsv_mut
//        Vcf vcf
//        String pathToVariant
//        String pathToTsvMut
//        def Found
//        String Type_of_case
//        def InvalidRange = []
//        def NotFound = []
//        def FoundV = []
//
//        // This will generate the files
//        ( list_Muts, list_Vcfs ) = GetPath_of_Files()
//
//        [list_Muts, list_Vcfs].transpose().collect
//                {
//                    pathToVariant = it[0]
//                    pathToTsvMut = it[1]
//
//                    print pathToTsvMut + " >> "
//
//                    (tsv_mut, vcf) = ReadFiles( pathToVariant,  pathToTsvMut)
//
//                    VariantMap = SetList_of_Varaints( vcf )
//
//                    ( outputMutation, outputVaf ) = ReadTestVariant( tsv_mut )
//
//                    // Code this in a sub function
//                    (Found, Type_of_case) = Checker( VariantMap,  outputMutation,  outputVaf,  Tolerance )
//
//                    Tests.add(Found)
//                    if(Type_of_case == "FI")
//                    {
//                        InvalidRange.add(Type_of_case)
//                    }
//                    else if(Type_of_case == "F")
//                    {
//                        FoundV.add(Type_of_case)
//                    }
//                    else
//                    {
//                        NotFound.add(Type_of_case)
//                    }
//                }
//
//        printResults( list_Muts, Tests)
//
//        def AccFreq = 0.0
//
//        for (value in NotFound)
//        {
//            AccFreq = AccFreq + Double.valueOf( value.split(":")[1] )
//        }
//
//
//
//        println "Founf INV:${InvalidRange.size()}, FOUND:${FoundV.size()}, NOT FOUND:${NotFound.size()} with average of ${AccFreq/NotFound.size()} "
//
//
//    }
//


    def printResults(List list_Muts, List Tests)
    {
        def Test_ID = ""
        def k = 0

        [list_Muts, Tests].transpose().collect
                {
                    Test_ID = it[0].split("/")[6]
                    println "${Test_ID}: ${it[1]}"
                    if(it[1])
                        k = k + 1
                }

        println "Valid ${k} tests from ${Tests.size()}"

    }

    // Extra Variables context
    def GetPath_of_Files()
    {
        def list_Muts = []
        def list_Vcfs = []

        String targetExtension = "/variant.tsv"
        String testExtension = ".vcf"
        // TODO revert this once the Runpipe problem gets fixed
        String ReportableAmplicon = "/pathology/NGS/Samples/PipeCleaner/ReportableAmpliconTS_Original/"

        new File(ReportableAmplicon).eachFile() { file->
            String dirName = file.getName()
            if(dirName.startsWith("Test"))
            {
                //All paths will be populated with this iteration
                list_Muts << ReportableAmplicon + dirName + targetExtension
                list_Vcfs << ReportableAmplicon + dirName + "/" + dirName + testExtension
            }

        }

        assert list_Muts.size() == list_Vcfs.size() :"Lists have different sizes, it is probable that a file is missing... Test FAILED"

        return[list_Muts, list_Vcfs]
    }

    def ReadFiles(String pathToVariant, String pathToTsvMut)
    {
        def tsv_mut = new Tsv( new File (pathToVariant) )
        tsv_mut.load( true )

        def vcf = new Vcf( new File (pathToTsvMut) )
        vcf.load()

        return[tsv_mut, vcf]
    }

    def SetList_of_Varaints(Vcf vcf)
    {
        def dataMap = vcf.getRowMaps()
        def VariantMap = [:]

        dataMap.each
                {
                    VariantMap[it['HGVSg']] = it['FREQ']
                }

        return VariantMap


    }

    def ReadTestVariant(Tsv tsv_mut)
    {
        def outMap = tsv_mut.getRowMaps()
        def outputMutation = outMap[0]['hgvsg']
        def outputVaf = outMap[0]['vaf']

        return [outputMutation, outputVaf]

    }

    def Checker(Map VariantMap, String outputMutation, String outputVaf, double Tolerance)
    {
        def Type_of_case = ""
        def ValidVariant = true
        if( VariantMap.containsKey(outputMutation) )
        {
            if(  Double.valueOf( outputVaf ) - Tolerance > Double.valueOf(VariantMap[outputMutation] ) )
            {
                println "FOUND ${outputMutation} but in Invalid Tolerance [ ${outputVaf} - Tol >  ${VariantMap[outputMutation]} ]"
                ValidVariant = false
                Type_of_case ="FI"
            }
            else
            {
                println "FOUND ${outputMutation} in Valid range"
                Type_of_case = "F"
            }

        }
        else
        {
            println "VARIANT ${outputMutation} NOT FOUND Freq ${outputVaf}"
            ValidVariant = false
            Type_of_case = "NF:${outputVaf}"


        }
        return [ValidVariant, Type_of_case]
    }


}