/*
 * Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: andreiseleznev
 */

package org.petermac.pathos.curate

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.runtime.StackTraceUtils
import org.hibernate.Session

import org.petermac.pathos.pipeline.HGVS
import org.petermac.pathos.pipeline.SampleName
import org.petermac.util.Classify
import org.petermac.util.DbConnect
import org.petermac.util.DbLock
import org.petermac.util.FileUtil
import org.petermac.util.Vcf
import org.springframework.context.ApplicationContext
import groovy.util.logging.Log4j
import org.petermac.util.AnomalyRest
import org.springframework.context.support.ClassPathXmlApplicationContext

import java.text.MessageFormat

/**
 * Created by andreiseleznev on 25/9/18.
 *
 * Load a list of VCF files into PathOS by first submitting them to Anomaly for processing and anotation, and then
 * parsing the provided results and loading into the database.
 */
@Log4j
class VcfAnomaly {
    //  DbLock for DB access in series
    //
    private static DbLock dblock  = null

    //  DB lock map
    //
    private static Map lockMap = null

    static ApplicationContext context

    static DbConnect db
    static boolean testMode = false
    static boolean createSeqSampleIfNotExists = true // create seqsamples if they dont exist
    static String defaultAnomalySchema = 'pathos_schema'

    /**
     * main method for CLI execution
     *
     * @param args
     */
    static void main(args) {

        //	Collect and parse command line args
        //
        def cli = new CliBuilder(usage: "VcfAnomaly [options] [in.vcf ...]",
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nSubmit VCF files for normalisation and annotation to Anomaly and load the results into PathOS\n')

        //	Options to command
        //
        cli.with
        {
            h(longOpt: 'help',   'This help message')
            d(longOpt: 'debug',  'Turn on debugging')
            f(longOpt: 'filter', 'Apply filter flags to variants')
            t(longOpt: 'test',   'Test mode - do not submit for processing, simply fetch results')
            r(longOpt: 'rdb',    args: 1, required: false, 'RDB to use')
            q(longOpt: 'seqrun', args: 1, required: false, 'Seqrun of VCF files [default: inferred from file path]')
            p(longOpt: 'panel',  args: 1, 'Panel name if new seqsample [inferred from seqrun or Dummy]')
        }


        Logger.getRootLogger().setLevel(Level.INFO)

        def opt = cli.parse(args)
        if (!opt) return

        if (opt.h) {
            cli.usage()
            return
        }

        if (opt.debug) Logger.getRootLogger().setLevel(Level.DEBUG)

        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        db  = new DbConnect(opt.rdb as String)

        context = new ClassPathXmlApplicationContext(db.hibernateXml)


        if(opt.p && !Panel.findByManifest(opt.p)) {
            log.fatal("No such panel: ${opt.p}")
            System.exit(1)
        }

        List<String> fileNames = opt.arguments()
        List<File> files = constructFileList(fileNames)

        if (fileNames.size() < 1) {
            cli.usage()
            System.exit(1)
        }

        if (opt.t) {
            testMode = true
        }

        List<Seqrun> loadedSeqruns = []

        if(opt.seqrun) {
            //  if seqrun given, process files for single seqrun
            //
            Seqrun seqrun = Seqrun.findBySeqrun(opt.q)
            if(!seqrun) {
                log.fatal("No such seqrun ${opt.q}")
                System.exit(1)
            }

            Integer processed = processFiles(seqrun, files, opt.panel ?: '')
            log.info("Finished processing ${processed} files")

            loadedSeqruns.add(seqrun)
        } else {
            //  if seqrun not given, process by inferred seqrun, may have more than one seqrun
            //
            Map filesBySeqrun = segregateFiles(files)
            if (filesBySeqrun.size() == 0) {
                log.fatal("No data files to process, could not infer seqrun from path")
                System.exit(0)
            }

            filesBySeqrun.each { String seqrunName, List<File> vcfFiles ->
                Seqrun sr = Seqrun.findBySeqrun(seqrunName)
                if(!sr) {
                    log.error("No such seqrun ${seqrunName}, refusing to process files ${vcfFiles}")
                } else {
                    log.info("Processing ${vcfFiles.size()} for inferred seqrun ${sr}")
                    Integer processed = processFiles(sr, vcfFiles, opt.panel ?: '')
                    log.info("Finished processing ${processed} files for seqrun ${sr}")
                    loadedSeqruns.add(sr)
                }
            }
            log.info("Processed ${loadedSeqruns.size()} seqruns")
        }



        //  run VarFilterService if set
        //
        if(opt.filter) {
            log.info("About to apply filter flags.")
            applyFilter(opt.rdb)
            log.info("Finished applying filter flags.")
        }
    }



    /**
     * process VCF files for anomaly upload
     * @param files files to process
     * @param seqrun seqrun to load in
     * @param opts user provided options
     * @param panel panel for new seqruns
     * @param filter run filer or not
     * @param rdb db to run on
     * @return number of successfully processed files
     */
    static Integer processFiles(Seqrun seqrun, List<File> files, String panel) {
        Integer count = 0

        //  upload to anomaly
        //
        if(!testMode) {
            Integer failedUploads = uploadVcfsToAnomaly(files)

            log.info("Uploaded " + (files.size() - failedUploads) + " files to Anomaly")
            if (failedUploads > 0) {
                log.warn("Warning: ${failedUploads} failed uploads to Anomaly")
            }
            if(files.size() == failedUploads) {
                log.warn("All uploads for seqrun ${seqrun} failed")
                return 0
            }
        }

        //  process each VCF individually by grabbing results
        //
        for(f in files) {
            log.info("Processing file ${f}")
            processVcf(seqrun, f, panel)
            log.info("Processed file ${f.getName()}")
            count++
        }

        return count
    }

    /**
     * upload Vcfs to anomaly AND poll until complete
     * @param files list of vcfs to upload
     * @return list of uploads that did not return success
     */
    static Integer uploadVcfsToAnomaly(List<File> files) {
        Set<String> allTags = []
        for (f in files) {
            log.info("Uploading file to anomaly: ${AnomalyRest.formulateTagFromFile(f)}")
            allTags.add(AnomalyRest.formulateTagFromFile(f))
            AnomalyRest.postAnnotateVcf(f, AnomalyRest.formulateTagFromFile(f),defaultAnomalySchema)
        }
        Integer failedUploads = AnomalyRest.pollForMultipleResultCompletion(allTags)

        return failedUploads
    }

    /**
     * process a single Vcf File
     * @param vcfFile
     */
    static void processVcf(Seqrun seqrun, File vcfFile, String panelManifest) {

        SeqSample seqSample = vcfSeqSample(seqrun,vcfFile,panelManifest)
        if(!seqSample)
        {
            log.error("File ${vcfFile} not processed due to no SeqSample error")
        }
        log.debug("Loading ${vcfFile} for SeqSample ${seqSample}")

        //  fetch result for VCF from anomaly
        //
        Map anomalyResult = AnomalyRest.resultVcfMap(AnomalyRest.formulateTagFromFile(vcfFile))

        //  load result for VCF from anomlay into the DB
        //
        Integer processResults = loadAnomalyResult(seqSample,anomalyResult)
        log.info("Processed ${processResults} variants")
    }


    /**
     * process the anomaly result for a specific VCF file and produce and load seqVariants from it
     *
     * @param sr
     * @param res
     * @return
     */
    static Integer loadAnomalyResult(SeqSample seqSample, Map res)
    {
        Integer count = 0
        if(! (res?.okResults)) {
            return 0
        }
        log.debug("ok results size " + res['okResults']?.size())


        //  for each variant in VCF file's okResults, load it in
        //
        SeqVariant.withSession
        {
            res['okResults']?.each
            {
                variant, varmap ->
                    if( loadVariant( seqSample, varmap )) count++
            }
        }

        return count
    }

    /**
     * load a variant into the database from an anomaly okResults section
     * @param seqSample
     * @param varResults anomaly okResults for a variant
     * @return true if loaded
     */
    static boolean loadVariant( SeqSample seqSample, Map varResults )
    {
        if( ! varResults.domainModel.derived) { //  bounce back if we don't have a domainModel.derived (this means something went wrong)
            log.debug("No domainModel.derived entry")
            return false
        }

        def svmap = mapAnnotationAnomaly(seqSample, varResults)

        if (!isVariantMasked(seqSample,svmap)) {
            def saved = saveSeqVariant(svmap)
            log.debug("Save returned: ${saved}")
            return (saved)
        }
        return false
    }

    /**
     * is variant masked ie should it not appear in the sample due to masking
     *
     * @return true if masked (and should not appear), false otherwise
     */
    static boolean isVariantMasked(SeqSample seqSample,Map var) {
        log.debug("Checking ${var.hgvsg} gene ${var.gene} for mask")
        if(seqSample.geneMask() && !(var.gene in seqSample.geneMask())) {
            log.debug("in mask")
            return true
        }
        log.debug("not in mask")
        return false
    }

    /**
     * clean a value from a string we've recieved from VcfAnomaly for insertion
     *
     * @param String fieldName name of field
     * @param fieldValue value of field from anomaly's domainModel.derived schema results (Object)
     * @param Map var varmap (anomaly okResults for a variant) we are writing to
     * @return
     */
    static def cleanStringResultFieldForSeqVariant(String fieldName,fieldValue,var) {
        def fieldResult
        //  we might get a stringified list - clean it if so
        //
        if (fieldValue.contains("[") && fieldValue.contains("]") && fieldValue.contains('"')) {
            fieldValue = fieldValue.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll('"', "")
        }

        if (fieldValue == '[]') {   //empty lists
            return ""
        }

        if (fieldValue.contains("[") && fieldValue.contains("]") ) {
            fieldValue = fieldValue.replaceAll("\\[", "").replaceAll("\\]", "")
        }

        //  handle percentages
        //
        if (isFieldANumber(fieldName) && fieldValue.contains('%')) {
            BigDecimal value = new BigDecimal(fieldValue.trim().replace("%", "")).divide(BigDecimal.valueOf(100));
            fieldResult = value
        } else {
            //  if value ends up blank and varmap already has a value, use the varmap so we don't overwrite an existign field with a blank one
            if (fieldValue == '' && var.find { it.key == fieldValue }?.value != '') {
                fieldResult = var[fieldValue]
            } else {
                fieldResult = fieldValue
            }
        }



        return fieldResult
    }


    static Map mapAnnotationAnomaly(SeqSample seqSample, Map var)
    {
        // assemble map
        // note that these are defaults and may be overwritten
        Map svmap =     [
                            seqSample:          seqSample,
                            sampleName:         seqSample.sampleName,
                            filtered:           false,
                            reportable:         false,
                            ens_variant:        ensVariant(var),
                            consequence:        'none found',
                        ]

        log.debug("Initial Var Map " + svmap)

        if(!var.domainModel.derived) {
            log.error("No derived element in domainModel, cannot process this file")
            return [:]
        }

        //  domainModel.derived has key value pairs for all neccessary fields, populate map with them. will make an sv out
        //  of the map.
        //
        var.domainModel.derived.each
        {
            String fieldName, Object fieldValue ->
                String className = fieldValue.getClass().name   //  class of value

                Class svPropertyType = GrailsClassUtils.getPropertyType(Class.forName("org.petermac.pathos.curate.SeqVariant"), fieldName.toString())
                try {
                    switch (className) {
                        case "java.lang.String":
                            def fieldResult = cleanStringResultFieldForSeqVariant( fieldName, fieldValue, var )


                            if (validateValueForSeqVariant( svmap, fieldName, fieldValue )) {
                                    svmap.put(fieldName, fieldResult.asType(svPropertyType))
                            }

                            break;
                        case "java.util.ArrayList":
                            //  write lists as comma-seperated string. lists come from Anomaly wrapped in square brackets so must be cleaned too
                            //
                            svmap.put(fieldName, cleanStringResultFieldForSeqVariant(fieldName, fieldValue.join(","),var))

                            break;
                        case "org.codehaus.groovy.runtime.NullObject":
                            break;
                        default:
                            if (validateValueForSeqVariant( svmap, fieldName, fieldValue )) {
                                    svmap.put(fieldName, fieldValue.asType(svPropertyType))
                            }
                    }
                } catch (e) {
                    log.error("Got exception: " + e)
                    log.debug( StackTraceUtils.sanitize(e).printStackTrace() )
                }
        }

        //  if certain fields were not in annotations, see if we can grab them from the VCF
        //
        if (!(svmap.gene)) {
            svmap.gene = inferGene(var)
            log.debug("Gene grabbed from VCF " + inferGene(var) + " / " + svmap.gene )
        }

        if(!svmap.hgvsc) {
            svmap.hgvsc = cleanStringResultFieldForSeqVariant('hgvsc',inferFromVariantCall(var,'HGVSc'),var)?:""
            log.debug("HGVSC grabbed " + svmap.hgvsc)
        }

        if (svmap.hgvsp) {  //  hard-set AA1 with internal function
            svmap.hgvspAa1 = HGVS.toAA1(svmap.hgvsp)
        }

        if (svmap.chr?.toString()?.startsWith("chr"))   //  strip away any 'chr' from anomaly
            svmap.chr = svmap.chr.replaceAll("chr","")

        //  add depths (readdepth, vardepth, etc)
        //
        Map depths =  parseDepths(var.domainModel.derived)
        depths.each
                {
                    depth, value ->

                        if( value )
                            svmap[depth] = value
                }

        //  Add classifications for the in-silico predictors
        //
        try
        {
            svmap.metaLrCat = Classify.metaLr(      svmap.metaLrVal as String )
            svmap.siftCat = Classify.sift((         svmap.siftVal as String)?.split(',') as List)
            svmap.lrtCat = Classify.lrt(            svmap.lrtVal as String )
            svmap.mutTasteCat = Classify.mutTaste(( svmap.mutTasteVal as String)?.split(',') as List)
            svmap.mutAssessCat = Classify.mutAssess(svmap.mutAssessVal as String )
            svmap.fathmmCat = Classify.fathmm((     svmap.fathmmVal as String)?.split(',') as List)
            svmap.metaSvmCat = Classify.metaSvm(    svmap.metaSvmVal as String)
            svmap.polyphenCat = Classify.polyphen(( svmap.polyphenVal as String)?.split(',') as List)
        }
        catch( Exception e ) { log.error("Exception classifying in-silico predictors: ${e.message}")}

        return svmap
    }

    /**
     * Convert VCF variant to ensembl format
     *
     * @param   vcf     Map of vcf fields
     * @return          Ensembl format eg
     */
    static String ensVariant( Map var )
    {
        String chrom = var.vcf?.variantCall?.hgvs?.ensembl?.chromosome
        String pos = var.vcf?.variantCall?.hgvs?.ensembl?.pos
        String ref = var.vcf?.variantCall?.hgvs?.ensembl?.ref?.sequence?.join("")
        String alt = var.vcf?.variantCall?.hgvs?.ensembl?.alt?.sequence?.join("")

        //  Normalise and convert VCF variant
        //
        Map normvar = HGVS.normaliseVcfVar( chrom, pos, ref, alt )

        return normvar.ensvar ?: ''
    }

    /**
     * validate whether a value should be written into the svmap
     * return false if we have a blank value written into a number field, OR if we have a blank value written into a field
     * that is already populated
     * @param svmap
     * @param fieldName
     * @param fieldValue
     * @return
     */
    static Boolean validateValueForSeqVariant( Map svmap, String fieldName, Object fieldValue)
    {
        if ( isFieldANumber(fieldName) && fieldValue == '' ) {   //  don't write blanks to numbers
            return false
        }

        if ( (fieldValue?.toString()?.trim() == '' && svmap.containsKey(fieldName)) ) { //  dont write blanks into an existing field
            return false
        }

        if ( isFieldANumber(fieldName) && fieldValue.toString()?.contains(',') ) {   //  this is a depth, don't write now, it will be parsed later
            return false
        }

        return true
    }

    /**
     * get a field value from the VCF part of an anomaly result (as opposed to the okResults schema)
     * @param var
     * @param fieldName
     * @return
     */
    static String inferFromVariantCall(var, String fieldName) {
        def infoMap = var?.vcf?.variantCall?.info
        if (!infoMap) return  ""

        if(infoMap[fieldName]) {
            log.debug("Inferred ${fieldName} from VCF, value " + infoMap[fieldName])
            return infoMap[fieldName]
        }
        return ""
    }

    /**
     * infer gene symbol for variant
     * @param var
     * @return
     */
    static String inferGene(Map var) {
        //  gene in besttx
        String gene = var?.vep?.besttx?.gene_symbol
        if(gene) {
            return gene
        }

        //  gene in vcf
        gene = var?.vcf?.variantCall?.info?.gene
        if(gene) {
            log.debug("Gene from variant call info gene ${gene}")
            return gene?.replaceAll("\\[",'')?.replaceAll("\\]",'')
        }

        return null
    }

    /**
     * is a given SeqVariant field (domain property) a number (int/float/double/bigdecimal)
     *
     * @param fieldName a seqvariant field
     * @return true if number false if not
     */
    static boolean isFieldANumber(String fieldName)
    {
        Class svPropertyType = GrailsClassUtils.getPropertyType( Class.forName("org.petermac.pathos.curate.SeqVariant"), fieldName )

        if (svPropertyType.name.contains("Integer") || svPropertyType.name.contains("Float") || svPropertyType.name.contains("Double") || svPropertyType.name.contains("BigDecimal")) {
            return true
        }
        return false
    }



    /**
     * from a List of file paths, return a List of Files and throw warnings if any are missing
     *
     * @param fileNames   List of filenames
     * @return            List of valid opened files.
     */
    static List<File> constructFileList(List<String> fileNames) {
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
     * get a seqsample object for a vcf (based on samplename inside vcf file)
     *
     * @param seqrun seqrun
     * @param sampleName name of sample
     * @param panelManifest panel if creating new seqsample
     * @param createIfNotExsits create seqsample if it doesnt exist -  not sure if we ever want this???
     * @return
     */
    static SeqSample vcfSeqSample(Seqrun seqrun, File vcfFile,String panelManifest) {
        String sampleName =  new Vcf(vcfFile).sampleName()

        def ssService = new SeqSampleService()
        // return ss if exist
        def ss = SeqSample.findBySampleNameAndSeqrun(sampleName,seqrun)
        if (ss) {
            return ss
        }

        // create if not
        //
        if(createSeqSampleIfNotExists) {
            SeqSample newSample = null
            SeqSample.withSession { Session session ->
                newSample = createSeqSampleForVcf(seqrun, sampleName, panelManifest)
                seqrun.addToSeqSamples(newSample)   //  you'd think this is superfluous but it's neccessary for replicate relation run - we are in groovy, not grails.

                //  assign reps
                int c = RelationService.assignReplicateRelation([seqrun], true)
                log.info("Added ${c} replicate relations")

                ssService.assignGeneMask(newSample)
            }
            return newSample
        }
        return null
    }


    /**
     * make a seq sample for a Vcf
     * @param seqrun
     * @param sampleName
     * @return
     */
    static SeqSample createSeqSampleForVcf(Seqrun seqrun, String sampleName, String panelManifest) {

        Panel p
        if(panelManifest) {
            p = Panel.findByManifest(panelManifest)
        } else {
            p = seqrun.mostCommonPanel()
            if(!p) {
                log.error("Failed to create SeqSample ${sampleName}: there is no panel in seqrun ${seqrun}. You must specify a panel with the -p option.")
                return null
            }
        }

        SeqSampleService ssService = new SeqSampleService()
        SeqSample ss = ssService.makeNewSeqSample(seqrun,sampleName,p,'vcfAnomaly','vcfAnomaly','vcfAnomaly','unknown')


        saveRecord( ss, true)
        return ss
    }


    static boolean saveSeqVariant( Map svmap )
    {
        log.debug( "Adding new var [${svmap.seqSample} ${svmap.hgvsg}]")

        //  Check if SeqVariant exists: look for specific SeqSample object and variant string
        //
        if ( SeqVariant.findBySeqSampleAndVariant( svmap.seqSample , svmap.hgvsg )) {
            log.info("Refusing to import ${svmap.hgvsg} seqsample ${svmap.seqSample} id ${svmap.seqSample.id} because it already exists")
            log.info(SeqVariant.findBySeqSampleAndVariant( svmap.seqSample, svmap.hgvsg ))
            return 0
        }

        //  Save record
        //
        def sv = new SeqVariant( svmap )
        log.debug("Trying to save: " + svmap)
        //  Save the new SeqVariant instance
        //
        return saveRecord( sv, false )
    }

    /**
     * Parse allele depths
     *
     * @param   vcf     Map of VCF params
     * @return          Map of ad:<allele depth>, dp:<read depth>, vaf:<variant allele depth>
     */
    static Map parseDepths(Map vcf )
    {
        Double  vaf  = null
        Integer ad   = null
        Integer dp   = null
        Integer adf  = null
        Integer rdf  = null
        Integer adr  = null
        Integer rdr  = null

        //  Set variant frequency AD and DP, otherwise use VCF FREQ
        //
        try
        {
            ad = Integer.parseInt( stripAlleles( vcf.varDepth, 'A', ','))       // retrieve ALT allele depth
            dp = Integer.parseInt( stripAlleles( vcf.readDepth, 'R', ','))       // retrieve REF allele depth
            if ( dp != 0 ) {
                vaf = ad * 100.0 / dp
            }
            else
            {
                //  frequency with trailing % provided
                //
                if ( vcf.varFreq )
                {
                    String freq = vcf.varFreq.replaceAll('%','')
                    vaf = Double.parseDouble( freq )
                }
            }
        }
        catch (Exception e)
        {
            StackTraceUtils.sanitize(e).printStackTrace()
        }

        //  Set strand depths
        //
        try
        {
            if( vcf.fwdVarDepth ) adf = Integer.parseInt(vcf.fwdVarDepth)
            if( vcf.fwdReadDepth ) rdf = Integer.parseInt(vcf.fwdReadDepth)
            if( vcf.revVarDepth ) adr = Integer.parseInt(vcf.revVarDepth)
            if( vcf.revReadDepth ) rdr = Integer.parseInt(vcf.revReadDepth)
        }
        catch (Exception e)
        {
            StackTraceUtils.sanitize(e).printStackTrace()
        }

        return [ varFreq:vaf, varDepth:ad, readDepth:dp, fwdVarDepth:adf, fwdReadDepth:rdf, revVarDepth:adr, revReadDepth:rdr ]
    }


    /**
     * Find first value for multiple alleles
     *
     * @param   val     Possible token separated value
     * @param   type    VCF fields type 'R'=ref,alt1,alt2... and 'A'=alt1,alt2...
     * @return          Second element if 'R' type
     */
    static String stripAlleles( String val, String type, String sep )
    {
        if ( ! val ) return '0'

        List l = val.tokenize( sep )

        if ( l.size() == 2 )
        {
            if ( type == 'R' ) return l[0]      // return reference allele depth
            if ( type == 'A' ) return l[1]      // return alternate allele depth
        }

        return( val.isInteger() ? val : '0' )
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
     * @param   cmd  Command to parse
     * @return       Map [ basedir: , seqrun: , sampledir: , sample: , extension: ]
     */
    static Map inferPathSample( File file )
    {
        Map pm = [sample: new Vcf(file).sampleName(), extension: FileUtil.nameExt(file.name), file: file ]

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

    /**
     * Open the listed files and infer the seqrun.
     *
     * @param files   List of Files
     * @return            Map from seqrun name to list of opened files
     */
    static Map<String,File> segregateFiles(List<File> files) {
        Map res = [:]
        for (File infile : files) {
            if (!infile.isFile()) {
                log.warn( "File ${infile} doesn't exist")
                continue
            }

            Map pm = inferPathSample(infile)
            def sr = pm.seqrun
            if (!(sr in res)) {
                res[sr] = []
            }
            res[sr] << infile
        }
        return res
    }

    /**
     * Save a domain record and format any errors
     *
     * @param   rec domain record to save
     * @param   flush true if the record should be flushed
     * @return  True if save successful
     */
    static boolean saveRecord( Object rec, boolean flush )
    {
        boolean ok = true
        rec.withTransaction
                {
                    status ->

                        if ( ! rec.validate())
                        {
                            rec?.errors?.allErrors?.each
                                    {
                                        ok = false
                                        log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
                                    }
                        }

                        try
                        {
                            rec.save( flush: flush , failOnError: true )
                        }
                        catch( Exception ex )
                        {
                            ok = false
                            status.setRollbackOnly()
                        }
                }

        return ok
    }
}
