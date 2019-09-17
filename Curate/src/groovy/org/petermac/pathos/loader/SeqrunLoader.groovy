/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Ken Doig
 */

package org.petermac.pathos.loader

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.petermac.pathos.curate.ClinContext
import org.petermac.pathos.curate.Panel
import org.petermac.pathos.curate.PatSample
import org.petermac.pathos.curate.RelationService
import org.petermac.pathos.curate.SeqRelation
import org.petermac.pathos.curate.SeqSample
import org.petermac.pathos.curate.Seqrun
import org.petermac.pathos.pipeline.SampleName
import org.petermac.util.DateUtil
import org.petermac.util.DbConnect
import org.petermac.util.Locator
import org.petermac.util.YamlUtil
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.yaml.snakeyaml.Yaml

import java.text.MessageFormat
import com.rabbitmq.client.*
import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j

/**
 * Load a YAML file of Seqruns into PathOS Gorm DB
 *
 * Author:  Kenneth Doig
 * Date:    31-mar-2017
 */

@Log4j
class SeqrunLoader
{
    static def loc = Locator.instance

    private static String EXCHANGE_NAME = loc.mqExchange
    private static String MQ_HOST       = loc.mqHost

    private final static def js = new JsonSlurper()

    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main( args )
    {
        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "SeqrunLoader [options]",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nLoad a YAML file of Seqruns and Samples into PathOS\n' +
                                            'If no seqruns or seqrels option, will load from queue')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		    'This help message' )
            d( longOpt: 'debug',		'Turn on debugging' )
            s( longOpt: 'seqruns',      args: 1, required: false, 'YAML file of Seqruns/Samples' )
            sr( longOpt: 'seqrels',      args: 1, required: false, 'YAML file of SeqRelations (this option prevents loading from queue)' )
            r( longOpt: 'rdb',          args: 1, required: true,  'RDB to use' )
            x( longOpt: 'exchange',     args: 1, 'Exchange name [mario]' )
            m( longOpt: 'mqhost',       args: 1, 'Message queue hostname [pmc-mgc-test.petermac.org.au]' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return
        if ( opt.help )
        {
            cli.usage()
            return
        }

        if ( opt.mqhost )
        {
            MQ_HOST = opt.mqhost
        }

        if ( opt.exchange )
        {
            EXCHANGE_NAME = opt.exchange
        }

        //  Debug ?
        //
        if ( opt.debug ) Logger.getRootLogger().setLevel(Level.DEBUG)
        log.debug( "Debugging turned on!" )

        //  Open Seqrun file
        //
        File srf = null
        if ( opt.seqruns )
        {
            srf = new File(opt.seqruns as String )
            if ( ! srf.isFile())
            {
                log.fatal( "File ${srf} doesn't exist")
                System.exit(1)
            }
        }


        //  Perform data load
        //
        log.info( "Start SeqrunLoader " + args )

        //  Process Seqruns
        //
        int nsr = 0
        if ( srf )
        {
            //  Load in seqruns from YAML file
            //
            List<Map> srs = YamlUtil.load( srf )

            nsr = loadAllSeqruns( srs, opt.rdb )
        }
        else if (! opt.seqrels )
        {
            //  Load in a Seqrun from a message queue
            //
            loadAllSeqrunMessages( opt.rdb )
        }

        log.info( "Done Loading Seqruns: processed ${srf} and ${nsr} records into ${opt.rdb}" )



        //  Load SeqRelations if they are passed to us
        //  Open SeqRelation file
        //
        File seqrels = null
        if ( opt.seqrels )
        {
            seqrels = new File(opt.seqrels as String )
            if ( ! seqrels.isFile())
            {
                log.fatal( "File ${seqrels} doesn't exist")
                System.exit(1)
            }
        }

        if(seqrels) {
            List<Map> srs
            try {
                srs = YamlUtil.load(seqrels.text)
            } catch (Exception e) {
                log.error("Error parsing YAML from file, please check if its in YAML format. Got exception: ${e}")
                System.exit(1)
            }
            def srloaded = loadAllSeqRelations(srs, opt.rdb)
            log.info("Done Loading SeqRelation: processed ${srloaded} into ${opt.rdb}")

        }

    }

    /**
     *
     *
     * @param dbname
     */
    static void loadAllSeqrunMessages( String dbname )
    {
        def db = new DbConnect( dbname )

        ConnectionFactory factory = new ConnectionFactory()
        factory.setHost( MQ_HOST )
        factory.setUsername( loc.mqUsername )
        factory.setPassword( loc.mqPassword )

        Connection connection = factory.newConnection()
        Channel    channel    = connection.createChannel()

        channel.exchangeDeclare( EXCHANGE_NAME, "fanout", true )
        String queueName = channel.queueDeclare().getQueue()
        channel.queueBind( queueName, EXCHANGE_NAME, "")

        println( "Waiting for messages. To exit press CTRL+C");;

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope env, AMQP.BasicProperties prop, byte[] body) {
                Boolean succeeded = false
                try {
                    handleDeliveryInner(consumerTag, env, prop, body)
                    succeeded = true
                } finally {
                    if (succeeded) {
                        this.getChannel().basicAck(env.getDeliveryTag(), false)
                    } else {
                        this.getChannel().basicNack(env.getDeliveryTag(), false, true)
                    }
                }
            }

            private void handleDeliveryInner(String consumerTag,
                                             Envelope envelope,
                                             AMQP.BasicProperties properties,
                                             byte[] body ) throws IOException
            {
                String message = new String(body, "UTF-8");
                unpackJsonMessage( message, dbname )
            }
        }

        channel.basicConsume(queueName, false, consumer);
    }

    /**
     * Unpack the message received from the queue
     *
     * @param message   Raw message String
     * @param dbname    DB name to populate
     */
    static void unpackJsonMessage( String message, String dbname )
    {
        Map jsonMsg

        try
        {
            jsonMsg = js.parseText( message ) as Map
        }
        catch( JsonException e )
        {
            log.error( "Failed to parse message [${message}] " + e )
        }

        //  Check we have a valid message
        //
        if ( ! jsonMsg?.content )
        {
            log.error( "Message has no content field [${jsonMsg}]")
            return
        }

        //  Report header details
        //
        reportMessageHeader( jsonMsg )

        //  Load message as a Seqrun message
        //
        int i = loadAllSeqruns( [ jsonMsg as Map ], dbname )
        log.info( "Loaded seqrun ${jsonMsg.content?.seqrun} ${i} record(s)")
    }

    /**
     * Output message header
     *
     * @param header    Map of header details
     */
    static void reportMessageHeader( Map header )
    {
        String display = "Message header:\n"
        for ( kv in header )
        {
            if ( kv.key != 'content' )
                display += sprintf( "%-20s %-20s\n", kv.key+':', kv.value )
        }

        log.info( display )
    }

    /**
     * Load YAML file of seqruns into GORM db
     * todo this is a function for 1.4 and is not currently used
     * @param srf      YAML file of Seqruns
     * @param dbname    Database to load
     * @return          Number of seqruns loaded
     */
    static int loadAllSeqruns( List<Map> srs, String dbname )
    {
        int nsr = 0
        def db   = new DbConnect( dbname )

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)

        Seqrun.withTransaction
        {
            status ->

                for ( sr in srs )
                {
                    try
                    {
                        //  Load Seqrun
                        //
                        Map added = [relations: []]      //  initialise with a list of seqSample relations
                        Seqrun seqrun = loadSeqrun( sr.content , added )
                        if ( ! seqrun ) continue

                        //  Report on records added
                        //
                        Map recs = new HashMap(added)
                        recs.remove('relations')
                        if (recs)
                        {
                            log.info("${++nsr}: Seqruns ${recs}")
                        }

                        //  Create SeqSample relationships
                        //
//                        boolean ok = loadSeqRelation(seqrun, added.relations)
//                        if (!ok)
//                        {
//                            log.warn("SeqRelationships failed for ${seqrun}")
//                        }
                    }
                    catch(e)
                    {
                        log.error( "Failed to load Seqrun(${sr}) Exception: ${e}")
                    }
                }
        }

        return nsr
    }

    /**
     * Load or update a Seqrun record
     * 
     * @param   sr      Map of Seqrun attributes
     * @param   added   Map of records added
     */
    static Seqrun loadSeqrun( Map sr, Map added )
    {
        //  Map values for Seqrun
        //
        String srn    = sr.seqrun
        if ( ! srn )
        {
            log.error( "No seqrun, is this a SeqrunLoader compliant message ? [${sr}]" )
            return null
        }

        Date runDate  = new Date()
        if ( srn =~ /\d{6}/ ) runDate = DateUtil.dateParse( 'yyMMdd', srn[0..5] )

        Map s =     [
                    seqrun:     sr.seqrun,
                    runDate:    runDate,
                    experiment: sr.experiment,
                    library:    sr.library,
                    platform:   sr.platform,
                    readlen:    sr.readlen,
                    scanner:    sr.scanner,
                    sepe:       sr.sepe
                    ]

        //  Find or Load Seqrun
        //
        Seqrun seqrun = Seqrun.findBySeqrun( sr.seqrun )
        if ( ! seqrun )
        {
            seqrun = new Seqrun( s )
            if ( ! saveRecord(seqrun))
            {
                return null
            }
            added.Seqrun = 1
        }

        //  Load Seqrun Samples
        //
        for ( ss in sr.seqSamples )
        {
            loadSeqSample( seqrun, ss, added )
        }

        return seqrun
    }

    /**
     * Load a SeqSample record
     *
     * @param seqrun    Parent Seqrun record
     * @param seqSample Map of SeqSample
     * @param added     Map of records added
     * @return          Map of records added
     */
    static Map loadSeqSample( Seqrun seqrun, Map seqSample, Map added )
    {
        log.debug( "SeqSample: ${seqSample}")

        String sample = seqSample.sampleName   // convenience variable

        //	Lookup panel
        //
        def panel = Panel.findByManifest( seqSample.panel )
        if ( ! panel )
        {
            log.warn( "Couldn't find panel [${seqSample.panel}]" )
        }

        //  Lookup patient sample
        //
        String sn = SampleName.impliedPatientSampleName(sample)
        PatSample patSample = PatSample.findBySample(sn)
        if ( ! patSample ) log.debug( "Couldn't find patient sample [${sn}]")

        //  Map values for SeqSample
        //
        Map ss =    [
                    seqrun:     seqrun,
                    sampleName: sample,
                    sampleType: seqSample.sampleType,
                    panel:      panel,
                    patSample:  patSample,
                    analysis:   seqSample.analysis,
                    userName:   seqSample.userName,
                    userEmail:  seqSample.userEmail,
                    laneNo:     seqSample.laneNo,
                    clinContext: seqSample.clinContext ?: ClinContext.generic()
                    ]

        //  Non template control sample type
        //
        if ( sample.startsWith("NTC"))
        {
            ss.sampleType = "NTC"
        }

        //  Control sample type
        //
        if ( sample.startsWith("CTRL") || sample.startsWith("CONTROL") || sample.startsWith("NA12878") || sample.startsWith("NA19240"))
        {
            ss.sampleType = "Control"
        }

        //  Load SeqSample
        //
        SeqSample addss = SeqSample.findBySeqrunAndSampleName( seqrun, sample )
        if ( ! addss )
        {
            addss = new SeqSample( ss )
            if ( ! saveRecord( addss) )
            {
                return added
            }

            if ( ! added.SeqSample )
            {
                added.SeqSample = 0
            }
            ++added.SeqSample
        }

        //  keep any seqRelations attributes as a Map
        //
        if ( seqSample.relations )
        {
            for ( rel in seqSample.relations )
            {
                rel.seqSample = addss   // keep this SeqSample object for adding to relationship
                added.relations << rel
            }
        }

        return added
    }

    /**
     * DEPRECATED RMOVE
     * Create any seqSample relationships from this Seqrun
     *
     * @param   sr  Map of Seqrun properties (including any relationships eg TumourNormal)
     */
    static boolean loadSeqRelation( Seqrun seqrun, List relations )
    {
        if ( ! seqrun ) return false

        //  Set replicates for seqrun
        //
        int c = RelationService.assignReplicateRelation( [ seqrun ], true )
        log.info("Assigned Replicates to ${c} samples")

        //  Set duplicates for seqrun
        //
        println( "seqSamples>>> ${seqrun.seqSamples}")

        List pats = seqrun.seqSamples.collect { ss -> ss.patSample }
        println( "patSamples>>> $pats")

        c = RelationService.assignDuplicateRelation( pats, true )
        log.info( "Assigned Duplicate to ${c} samples")

        //  Set TumourNormals for seqrun
        //
        if ( relations )
        {
            return setTumourNormalRelations( relations )
        }

        return true
    }

    /**
     * this is for PATHOS-2522
     * assign SeqRelations based on a SeqRelatons YAML file
     * @return
     */
    static int loadAllSeqRelations(List<Map> sr, String dbname) {
        def db   = new DbConnect( dbname )

        ApplicationContext context = new ClassPathXmlApplicationContext( db.hibernateXml)
        int c = 0
        Seqrun.withTransaction
                {

                    for (srel in sr.content.sampleRelations[0]) {
                        println srel
                        //  load in a relation
                        Set seqsamples = new HashSet<SeqSample>()
                        Set runs = new HashSet<String>()    //set of seqruns for log info messages
                        String relationType

                        for (relSample in srel) {
                            

                            def srun = Seqrun.findBySeqrun(relSample.seqrun)
                            SeqSample ss = SeqSample.findBySeqrunAndSampleName(srun, relSample.sampleName)
                            if (ss) {
                                seqsamples.add(ss)
                                runs.add(relSample.seqrun)
                            } else {
                                log.info("Warning: unable to find SeqSample ${relSample.sampleName} in Seqrun ${srun}")
                            }
                            relationType = relSample.relationType

                            //  also set sampleType if its set and allowed and different
                            //
                            if(ss && relSample.sampleType && relSample.sampleType in SeqSample.constraints.sampleType.inList && ss.sampleType != relSample.sampleType) {
                                log.info("Setting sample type for ${ss.sampleName} ${ss.seqrun} to ${relSample.sampleType} ${ss.sampleType?"(changed from: " + ss.sampleType + ")":""}")
                                ss.sampleType = relSample.sampleType
                            }
                        }


                        if (seqsamples.size() > 1 && relationType) {
                             if(RelationService.assignRelationToSeqSamples(seqsamples, relationType))
                             {
                                 log.info("Created SeqRelation ${relationType} samples " + seqsamples + " from seqrun(s) " + runs)
                                 c++
                             }  else {
                                 log.info("Cannot make SeqRelation, RelationService failed on relation ${relationType} samples " + seqsamples + " from seqrun(s) " + runs)
                             }
                        } else {
                            log.info("Cannot make SeqRelation, Need 2 or more samples & valid relation to make SeqRelation, have ${seqsamples.size()} and ${relationType}")
                        }
                    }


                }

        return c

    }

    /**
     * Add any TumourNormal relationships in samples
     *
     * @param relations List of relation parameters
     * @return
     */
    static boolean setTumourNormalRelations( List<Map> relations )
    {
        for ( rel in relations )
        {
            if ( rel.relation == 'TumourNormal' )
                setTumourNormal( rel )
        }

        return true
    }

    /**
     * Add a TumourNormal relationship
     *
     * @param relation  Map of relation parameters
     * @return
     */
    static boolean setTumourNormal( Map relation )
    {
        //  Get other SeqSample of the relationship
        //
        Seqrun othersr = relation.seqrun      // the default other Seqrun of relationship is the same as the other SeqSample
        if ( relation.seqrun )
        {
            othersr = Seqrun.findBySeqrun( relation.seqrun )
        }
        SeqSample otherss = SeqSample.findBySeqrunAndSampleName( othersr, relation.sampleName )

        //  Add the TN relationship
        //
        return RelationService.assignRelationPair( true, relation.relation, relation.seqSample, otherss )
    }

    /**
     * Save a GORM record with validation
     *
     * @param rec   GORM domain object
     * @param flush flag to flush the record
     * @return      true is successfully saved
     */
    static boolean saveRecord( Object rec, boolean flush = true )
    {
        rec.withTransaction
        {

            //  Validate record
            //
            if (!rec?.validate())
            {
                rec?.errors?.allErrors?.each {
                    log.error("GORM record failed validation: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                }
                return false
            }

            //  Save the record
            //
            if (!rec?.save(flush: flush))
            {
                rec?.errors?.allErrors?.each {
                    log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                }
                return false
            }
            return true
        }


    }

    /**
     * Update a changed GORM record with the provided properties map and validation
     *
     *
     * @param rec   GORM domain object
     * @param flush flag to flush the record
     * @return int number of record properties changed, or null upon failure
     */
    static int updateRecord( Object rec, Map props, boolean flush = true )
    {

        rec.withTransaction
                {

                    int propertiesChanged = 0
                    props.each{ k,v ->
                        //  we can't just set .properties on an Object
                        //  so we iterate instead
                        if(rec.properties.containsKey(k)) {
                            if (rec[k] != v ) {
                                rec[k] = v
                                println "AA"
                                return null
                                propertiesChanged++
                            }
                        }
                    }

                    //  Return false if record not changed
                    if(propertiesChanged == 0) return propertiesChanged

                    //  Validate record
                    //
                    if (!rec?.validate())
                    {
                        rec?.errors?.allErrors?.each {
                            log.error("GORM record failed validation: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return null
                    }

                    //  Save the record
                    //
                    if (!rec?.save(flush: flush))
                    {
                        rec?.errors?.allErrors?.each {
                            log.error("GORM record failed to save: " + new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                        }
                        return null
                    }

                    return propertiesChanged

                }

    }




}
