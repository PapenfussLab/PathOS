/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: Kenneth Doig
 */

package org.petermac.pathos.loader

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.StackTraceUtils
import org.hibernate.Session
import org.petermac.amqp.AmqpConnection
import org.petermac.annotate.*
import org.petermac.pathos.curate.SeqVariant
import org.petermac.pathos.curate.VarFilterService
import org.petermac.pathos.pipeline.*
import org.petermac.util.*
import org.petermac.yaml.YamlCodec
import org.petermac.yaml.YamlConfig
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Load a list of VCF files into PathOS
 * Optionally Normalise and Annotate each VCF file a add new and unique variants into an annotation cache
 *
 * Default behaviour is not ot Normalise the input VCFs as this is usually done by the pipeline. The Annotator
 * will be run for VEP as this provides the annotation data from VEP used to provide consequences and in-silico
 * predictor values. It is assumed that Seqrun/SeqSamples have already been loaded via SeqrunLoader before
 * VcfLoader is run, otherwise if the Seqrun/SeqSample doesn't exist, the variants won't be loaded.
 *
 * Author:  Kenneth Doig
 * History:
 *
 * 01   4-Sep-16    kdd     Initial create
 * 02   25-Apr-17   kdd     Rewrite to allow for automated loading after pipeline completion
 */

@Log4j
class VcfLoader
{
    //  DbLock for DB access in series
    //
    private static DbLock dblock  = null

    //  DB lock map
    //
    private static Map lockMap = null

    static ApplicationContext context
    static DbLoader dbl
    static DbConnect db

    static class OurConsumer extends DefaultConsumer {
        private YamlCodec codec
        private Closure handler

        OurConsumer(Channel chan, Closure handler) {
            super(chan)
            this.codec = new YamlCodec()
            this.handler = handler
        }

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

        private void handleDeliveryInner(String consumerTag, Envelope env, AMQP.BasicProperties prop, byte[] body) {
            String msg = new String(body, "UTF-8")
            Object stuff = codec.load(msg)

            List<String> fileNames = []
            switch (stuff) {
                case {it instanceof String}:
                    fileNames << stuff
                    break
                case {it instanceof List}:
                    fileNames = stuff
                    break
                default:
                    log.error "Unexpected message. Expected string or list of strings."
                    return
            }

            handler(fileNames)
        }

    }
    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main(args) {
        BasicConfigurator.configure()
        Logger.getRootLogger().setLevel(Level.INFO)

        //	Collect and parse command line args
        //
        def cli = new CliBuilder(   usage: "VcfLoader [options] [in.vcf ...]",
                                    header: '\nAvailable options (use -h for help):\n',
                                    footer: '\nNormalise, annotate, cache and load a set of VCF files\n')

        //	Options to command
        //
        cli.with
        {
            h( longOpt: 'help',		    'This help message' )
            d( longOpt: 'debug',		'Turn on debugging' )
            f( longOpt: 'filter',		'Apply filter flags to variants' )
            l(longOpt: 'log-config', args:1, 'log4j properties file')
            L(longOpt: 'log-level',  args:1, 'set the logging level')
            n( longOpt: 'nocache',		'Dont use annotation cache' )
            norm( longOpt: 'normalise', "Normalise VCF files first [don't normalise]" )
            r( longOpt: 'rdb',          args: 1, required: true, 'RDB to use' )
            q( longOpt: 'seqrun',       args: 1, required: false, 'Seqrun of VCF files [inferred from file path]' )
            s( longOpt: 'datasource',   args: 1, 'Comma separated list of datasources to use for annotation [no annotation]' )
            c( longOpt: 'columns',      args: 1, 'File of columns to output [PATHOS_HOME/etc/vcfcols.txt]' )
            p( longOpt: 'panel',        args: 1, 'Panel name [NoPanel] (must exist in PathOS)' )
            e( longOpt: 'errors',       args: 1, 'File name for error records' )
            a( longOpt: 'amqp',         args: 1, 'Config file name for retrieving vcf filenames from an AMQP server' )
            mut(longOpt:'mutalyzer',    args: 1, 'Mutalyzer annotation server host [https://mutalyzer.nl]' )
        }

        def opt = cli.parse( args )
        if ( ! opt ) return

        if (opt.h) {
            cli.usage()
            return
        }

        List<String> fileNames = opt.arguments()
        if (!opt.a && fileNames.size() < 1 ) {
            cli.usage()
            return
        }

        if (opt.l) {
            PropertyConfigurator.configure(opt.l)
        }

        if (opt.L) {
            Logger.getRootLogger().setLevel(Level.toLevel(opt.L))
        }

        //  Debug ?
        //
        if (opt.debug) {
            Logger.getRootLogger().setLevel(Level.DEBUG)
            if (opt.L) {
                log.warn "Note that debug and log-level have both been passed. Debug wins."
            }
        }
        log.debug( "Debugging turned on!" )

        //  Validate data sources
        //
        List dss = [ ]
        if (opt.datasource) {
            dss = (opt.datasource as String).tokenize(',')
        }

        //  Optional list of columns to output
        //
        File colsf = new File( Locator.instance.etcDir + "vcfcols.txt" )
        if (opt.columns) {
            colsf = new File( opt.columns as String)
        }

        if ( ! colsf.exists())
        {
            log.error( "File ${colsf} doesn't exist")
            return
        }

        //  Test Mutalyzer is available if we need the mutalyzer datasource OR we are going to Normalise
        //
        String defaultMut = (opt.mutalyzer ?: 'https://mutalyzer.nl')
        if ( dss.contains( 'mutalyzer' ) || opt.normalise )
        {
            if ( ! (new Mutalyzer(defaultMut)).ping())
            {
                log.fatal( "Can't connect to ${defaultMut} server")
                System.exit(1)
            }
        }

        //  Open error file and zero it if required
        //
        File errFile = null
        if ( opt.errors )
        {
            errFile = new File( opt.errors as String )
            errFile.delete()
        }


        if (opt.a) {
            //  Perform data load from AMQP sourced VCF files
            //

            Map config = YamlConfig.load(opt.a)

            AmqpConnection amqp = new AmqpConnection(config)

            if (config['queue'] == null) {
                log.fatal "Required configuration parameter 'queue' missing."
                return
            }


            amqp.apply { theConn, theChan ->
                Consumer consumer = new OurConsumer(theChan, {stuff ->
                    Map vcfs = VcfLoader.segregateFiles(stuff)

                    if (vcfs.size() == 0) {
                        log.warn "No data files to process"
                        return
                    }

                    vcfs.each { seqrun, vcfFiles ->
                        log.info "Processing ${vcfFiles.size()} files from ${seqrun} into ${opt.rdb}"
                        log.info "Files ${vcfFiles}"
                        VcfLoader.establishContextAndLoadVcf(vcfFiles, seqrun, opt.panel ?: 'NoPanel',
                                opt.rdb as String, dss, colsf, errFile,
                                opt.filter, opt.nocache, opt.normalise, defaultMut)
                        log.info "Done: processed ${vcfFiles.size()} files into ${opt.rdb}"
                    }
                })
                theChan.basicConsume(amqp.queueConfig['name'], false, consumer)
            }

        } else {
            //  Perform data load from command line VCF files
            //

            if (opt.seqrun) {
                // Seqrun has been set explicitly, so we just open the files and process them.
                //
                List<File> vcfFiles = openFiles(fileNames)
                if (!vcfFiles) {
                    log.fatal( "No data files to process")
                    return
                }
                String seqrun = opt.seqrun
                log.info "Processing ${vcfFiles.size()} files from ${seqrun} into ${opt.rdb}"
                establishContextAndLoadVcf(vcfFiles, seqrun, opt.panel ?: 'NoPanel',
                        opt.rdb as String, dss, colsf, errFile,
                        opt.filter, opt.nocache, opt.normalise, defaultMut)
                log.info "Done: processed ${vcfFiles.size()} files into ${opt.rdb}"
            } else {
                // Seqrun is being inferred, so we open the files and aggregate them
                // according to the inferred seqrun, before processing them one seqrun at a time.
                //
                Map vcfs = segregateFiles(fileNames)

                if (vcfs.size() == 0) {
                    log.warn "No data files to process"
                    return
                }

                vcfs.each { seqrun, vcfFiles ->
                    log.info "Processing ${vcfFiles.size()} files from ${seqrun} into ${opt.rdb}"
                    establishContextAndLoadVcf(vcfFiles, seqrun, opt.panel ?: 'NoPanel',
                            opt.rdb as String, dss, colsf, errFile,
                            opt.filter, opt.nocache, opt.normalise, defaultMut)
                    log.info "Done: processed ${vcfFiles.size()} files into ${opt.rdb}"
                }
            }
        }
    }

    /**
     * Open the listed files, returning a vector of Files.
     *
     * @param fileNames   List of filenames
     * @return            List of valid opened files.
     */
    static List<File> openFiles(List<String> fileNames) {
        //  Open files
        //
        List<File> vcfFiles = []
        for (String inf : fileNames) {
            def infile = new File( inf as String )
            if (!infile.isFile()) {
                log.warn( "File ${infile} doesn't exist")
                continue
            }
            vcfFiles << infile
        }
        return vcfFiles
    }

    /**
     * Open the listed files and infer the seqrun.
     *
     * @param fileNames   List of filenames
     * @return            Map from seqrun name to list of opened files
     */
    static Map segregateFiles(List fileNames) {
        Map res = [:]
        for (String inf : fileNames) {
            def infile = new File( inf as String )
            if (!infile.isFile()) {
                log.warn( "File ${infile} doesn't exist")
                continue
            }

            Map pm = getPathSample(infile)
            def sr = pm.seqrun
            if (!(sr in res)) {
                res[sr] = []
            }
            res[sr] << infile
        }
        return res
    }

    /**
     * call LoadVcf but first load applicaiton context
     * use for loadVcf() calls when VcfLoader is run independently as a standalone program
     * @param   vcfs      List of VCF Files
     * @param   seqrun    Sequencing run name
     * @param   panel     Sequencing panel name
     * @param   rdb       Database name to populate
     * @param   dss       List of annotation datasources
     * @param   vcfcols   Columns to match in VCF file
     * @param   errFile   Error output file
     * @param   filter    Apply filtering
     * @param   nocache   Dont use annotation cache
     * @param   normalise Normalise the VCF file
     * @param   muthost   Mutalyzer host
     * @return  Rows loaded
     */
    static int establishContextAndLoadVcf( List<File> vcfs, String seqrun, String panel, String rdb, List dss, File vcfcols, File errFile, boolean filter, boolean nocache, boolean normalise, String mutHost )
    {
        println "Called establishContextAndLoadVcf"
        log.info( "Called establishContextAndLoadVcf" )
        if (!dbl) {
            // Acquire the resources to load the data
            //
            dbl = new DbLoader()

            //  Annotation object for DB
            //
            db  = new DbConnect(rdb)
            dbl.sql = db.sql()
        }

        if (!context) {
            //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
            //
            context = new ClassPathXmlApplicationContext(db.hibernateXml)
        }
        return VcfLoader.loadVcf(vcfs,seqrun,panel,rdb,dss,vcfcols,errFile,filter,nocache,normalise,mutHost)
    }

    /**
     * Load VCF files
     *
     * @param   vcfs      List of VCF Files
     * @param   seqrun    Sequencing run name
     * @param   panel     Sequencing panel name
     * @param   rdb       Database name to populate
     * @param   dss       List of annotation datasources
     * @param   vcfcols   Columns to match in VCF file
     * @param   errFile   Error output file
     * @param   filter    Apply filtering
     * @param   nocache   Dont use annotation cache
     * @param   normalise Normalise the VCF file
     * @return            Rows loaded
     */
    static int loadVcf( List<File> vcfs, String seqrun, String panel, String rdb, List dss, File vcfcols, File errFile, boolean filter, boolean nocache, boolean normalise, String mutHost )
    {
        println "Called loadVcf"
        log.info( "Called loadVcf" )
        //  Normalise all VCFs
        //
        if ( normalise )
        {
            vcfs = normaliseVcfs( vcfs, rdb, nocache, mutHost )
        }

        //  Add VCFs to annotation cache
        //
        log.info( "Annotating variants with ${dss}")
        if ( dss )
        {
            new Annotator().annotateVcf( vcfs, rdb, dss, errFile, mutHost, null )
        }

        //  Create an aggregate TSV file of all variants
        //
        File tsv = tsvVcfs( vcfs, seqrun, panel, vcfcols )

        //  Populate GORM database from RDB
        //
        int loaded = loadGorm( tsv, rdb, dss, seqrun, filter )
        return loaded
    }

    /**
     * Normalise each VCF file
     *
     * @param vcfs      List of VCF Files
     * @param rdb       DN name of annotation cache
     * @param nocache   Use cache flag
     * @return          List of normalised VCF Files
     */
    static List<File> normaliseVcfs( List<File> vcfs, String rdb, boolean nocache, String mutHost )
    {
        List<File> normFiles = []

        log.info( "Normalising variants with ${mutHost}")

        //todo try catch here, covertVcf can throw an exception
        for ( vcf in vcfs )
        {
            File normVcf = FileUtil.tmpFixedFile()
            log.info( "Normalising VCF ${vcf.name} into ${normVcf}" )
            try
            {
                new MutalyzerUtil(mutHost).convertVcf( vcf, normVcf, rdb, nocache )
            }
            catch( Exception e )
            {
                StackTraceUtils.sanitize(e).printStackTrace()
                log.fatal( "Exiting: Couldn't normalise file ${vcf} " + e.toString())
                System.exit(1)
            }

            normFiles << normVcf
        }

        return normFiles
    }

    /**
     * Create a TSV containing each VCF file variants
     *
     * @param vcfs      List of Maps of VCF Files and sample names
     * @param seqrun    Seqrun name
     * @param panel     Panel name
     * @param vcfcols   Column names of TSV file
     * @return          List of samples
     */
    static File tsvVcfs( List<File> vcfFiles, String seqrun, String panel, File vcfcols )
    {
        int       nl     = 0
        List<Map> sammap = extractSamples( vcfFiles, seqrun )
        File      tsv    = FileUtil.tmpFixedFile( '/tmp', "vcf" )               //  Create temporary TSV file for all variants

        log.info( "Creating variant TSV file ${tsv}")

        //  Process each VCF file into a TSV
        //
        boolean header = true
        for ( vcf in sammap )
        {
            nl += Vcf2Tsv.vcf2Tsv( vcf.file, tsv, vcf.sample, vcf.seqrun, panel, vcfcols, header )

            header = false
        }

        return tsv
    }

    /**
     * Extract a list of samples
     *
     * @param vcfs      List of VCF Files
     * @param seqrun    Seqrun name or null
     * @return          List<Map> of [file: , sample: , sampledir: , seqrun: , extension: , basedir: ]
     */
    static List<Map> extractSamples( List<File> vcfs, String seqrun )
    {
        List<Map> samples = []

        //  Parse file path for meta data
        //
        for ( vcf in vcfs )
        {
            Map pm = getPathSample( vcf )

            //  Use directory parent as seqrun unless its explicitly supplied
            //
            if ( seqrun ) pm.seqrun = seqrun

            //  Some sanity checks
            //
            if ( pm.extension != 'vcf' )        log.warn( "Expecting VCF extension but got ${pm.extension}")
            if ( pm.sample != pm.sampledir )    log.warn( "Sample file doesn't match directory ${pm}")
            if ( ! ( pm.seqrun =~ /\d{6}_/ ))   log.warn( "Non Illumina seqrun name ${pm.seqrun}")

            samples << pm
        }

        //  Check for sample name uniqueness
        //
        List sampleList = new ArrayList(samples.sample as List<String>)
        if ( sampleList?.size() != samples.sample?.unique()?.size())                   // unique() modifies List in situ
        {
            log.fatal( "Duplicate samples in a run ${samples.sample.sort()}")
        }

        return samples
    }

    /**
     * Load GORM db from rdb tables
     *
     * @param dbname
     * @return
     */
    synchronized static int loadGorm( File tsv, String dbname, List<String> dss, String seqrunName, boolean filter )
    {
        println "Called loadGorm"
        log.info( "Called loadGorm" )

        log.info( "Loading variant file ${tsv} into GORM [${dbname}]")

        int nrow = 0



        // Load HGVS transcripts
        //
        HGVS.ensureTranscriptsLoaded(dbname)

        //  Add the SeqVariants
        //
        boolean usemyv = dss.contains('myvariant')
        SeqVariant.withTransaction {
            nrow = LoadSeqVariant.addSeqVariant(tsv, dbname, seqrunName, usemyv)
            log.info("Added ${nrow} SeqVariants")
        }

        //  Apply filtering to variants if requested
        //
        if (filter) {
            applyFilter(dbname)
        }

        return nrow
    }

    /**
     * Apply filter flags to SeqVariant
     *
     * @param rdb
     */
    static void applyFilter( String rdb )
    {
        //  Set up lock
        //
        dblock = new DbLock( rdb, 120 )

        while ( lockMap = dblock.hasLock())
        {
            log.info( "Waiting for DB Lock on ${rdb} lock=${lockMap}")
            sleep( 60 * 1000 )                           // 1 minute wait
        }

        //  Acquire lock
        //
        lockMap = dblock.setLock()
        log.info( "Set DB Lock on ${rdb} lock=${lockMap}")

        //   Filter added SeqVariants
        //
        try {
            SeqVariant.withSession
                    {
                        Session session ->
                            def vfs = new VarFilterService()
                            int cnt = vfs.applyFilter(session, false)
                            log.info("Set Filter for ${cnt} Variants")
                    }
        }  catch (Exception e) {

            //  If we have an exception, dump the stack trace and error message.
            //  Don't exit, though.
            //
            StackTraceUtils.sanitize(e).printStackTrace()
            log.fatal( "Exception while running applyFilter: " + e.toString() )
        } finally {

            //  Clear the lock despite what happened.
            //
            if ( lockMap )
            {
                lockMap = dblock.clearLock( lockMap )
                log.info( "Cleared DB Lock on ${rdb} lock=${lockMap}")
            }
        }
    }

    /**
     * Find Seqrun/Sample from filenames embedded in path
     * Note: this assumes pipeline is being run within the pipeline data repository eg /pathology/NGS/Samples/<base>/<seqrun>/<sample>
     *
     * Todo: Allow the user to decide how to determine seqrun/sample
     *
     * @param   cmd  Command to parse
     * @return       Map [ basedir: , seqrun: , sampledir: , sample: , extension: ]
     */
    static Map getPathSample( File file )
    {
        Map pm = [ sample: new Vcf(file).sampleName(), extension: FileUtil.nameExt(file.name), file: file ]

        //  Use file name if no sample from header
        //
        if ( ! pm.sample )
        {
            pm.sample = FileUtil.nameNoExt(file.name)
        }

        //  Clean sample names: replace non alphanum with '-'
        //
        pm.sample = SampleName.clean( pm.sample as String )

        //  Sample directory
        //
        File p1 = new File( file.absolutePath )
        if ( p1.parent )
        {
            p1 = new File(p1.parent)
            if ( ! p1 ) return pm
            pm.sampledir = p1.name
        }

        //  Seqrun directory
        //
        if ( p1.parent )
        {
            p1 = new File(p1.parent as String)
            if (!p1) return pm
            pm.seqrun = p1.name
        }

        //  Samples base directory
        //
        if ( p1.parent )
        {
            p1 = new File( p1.parent as String )
            if ( ! p1 ) return pm
            pm.basedir = p1.name
        }

        return pm
    }

}
