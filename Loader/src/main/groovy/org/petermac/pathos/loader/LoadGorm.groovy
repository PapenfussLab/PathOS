package org.petermac.pathos.loader

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext
import groovy.util.logging.Log4j
import java.text.MessageFormat;
import org.petermac.pathos.curate.*

/**
 * The test class for the Loader: Tests hibernate xml context
 */
@Log4j
public class LoadGorm
{

    /**
     * Main method launching the application.
     */
    public static void main(String[] args)
    {
        log.info( "Starting LoadGorm in " + new File('.').absolutePath )

        ApplicationContext context = new ClassPathXmlApplicationContext("demo_loaderContext.xml");

        //  Add a new user
        //
        //def user = new User( login:"kdd", password: "kdoig", email: "ken.doig@petermac.org", displayName: "Ken Doig", role: "administrator")

        //testUser( user )

//        int cnt = User.count()
        //println( "Total Count of Users: ${cnt}")

//        for ( usr in User.list())
//            println( "User: ${usr}")

        //  Test HQL
        //
        log.info( "Testing HQL..." )

        testSomatic()

        log.info( "Done" )
    }

    static void testSomatic()
    {
        def runs = duplicateSamples()

        log.info( "Found ${runs.size()} duplicate samples")

        List vars = []
        for( run in runs )
        {
            vars << singletonVars( run.toList())
        }

        vars = vars.flatten()
        log.info( "Found ${vars.size()} singleton vars")

        int cnt = setSingletonVars( vars )

        log.info( "Updated ${cnt} singleton vars")
    }

    /**
     * Find all runs with duplicate sample prefixes (PM sample names)
     *
     * @return  List of arrays [ Seqrun, <sample prefix>, <no of Samples>]
     */
    public static List duplicateSamples()
    {
        //  HQL query to find all Seqruns with duplicate PM sample prefixes
        //
        def qry =   """
                    select  sa.seqrun,
                            max(substring(sa.sampleName,1,7)) as prefix,
                            count(*) as noSamples
                    from    org.petermac.pathos.curate.SeqSample as sa
                    where   sa.id = 1
                    """

        def runs = SeqSample.executeQuery( qry )

        return runs
    }

    /**
     * Find all variants occurring once only in the same run in the same sample
     *
     * @param   run     Run/prefix to search [ Seqrun, <sample prefix>, <no of Samples>]
     * @return          List of SeqVariant ids to be updated
     */
    public static List singletonVars( List run )
    {
        def seqrun = run[0]
        def prefix = run[1]
        def samcnt = run[2]

        //  HQL query to find all Seqruns with duplicate PM sample prefixes
        //
        def qry =   """
                    select	sv.id
                    from	org.petermac.pathos.curate.SeqVariant as sv
                    join	sv.seqSample  as sa
                    where	sa.seqrun.seqrun = '${seqrun.seqrun}'
                    and		substring(sv.sampleName,1,7) = '${prefix}'
                    group
                    by 		sv.variant
                    """

        def vars = SeqVariant.executeQuery( qry )

        return vars
    }

    public static int setSingletonVars( List vars )
    {
        int upd = 0
        println "HELLO"
        SeqVariant.withTransaction
        {
            for( var in vars )
            {
                def sv = SeqVariant.get(var as long)
                if ( sv.filterFlag.contains('sin')) continue

                println "${sv.variant} ${sv.filterFlag}"

                if ( sv.filterFlag == 'nof' || sv.filterFlag == 'pass' )
                {
                    sv.filterFlag = 'sin'
                }
                else
                {
                    sv.filterFlag = sv.filterFlag + ',sin'
                }

                if ( ! sv.save())
                {
                    log.error( "Failed to save ${sv}")
                }
                else
                    ++upd
            }
        }

        return upd
    }

    /**
     * Save the test User object in Gorm
     *
     * @param rec   User to save
     */
    static void testUser( Object rec )
    {
        rec.withTransaction
        {
            status ->

                if ( ! rec.validate())
                {
                    rec?.errors?.allErrors?.each
                    {
                        log.error( "Error: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                    }
                    return
                }

                try
                {
                    rec.save()
                }
                catch( Exception ex )
                {
                    //println ("Error: ${user.displayName} ${ex}" )
                    status.setRollbackOnly()
                }
        }
    }
}
