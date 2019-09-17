/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */
package org.petermac.util

/**
 * Created by seleznev andrei on 6/01/2015.
 */
//import groovyx.gprof.*
import sun.font.TrueTypeFont
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.lang.Closure


class FilterflagChecker {

    /**
     * check filter flags for all seqvars in db to see if they are set as they should be
     * refactor of checkFilters
     * @param rdb
     * @param debug
     */
    static void checkFilterFlags( rdb, debug ) {
        def db = new DbConnect(rdb)
        def sql = db.sql()

        //load the rules
        def loc = Locator.instance
        def rulesPath = loc.etcDir + 'FilterRules.groovy'
        Map rules = getFilterRulesConfig(rulesPath)

        //get IRs
        //

           Map inregions = inRegionSeqVariants(sql)

            //get singletons
            //
            Map singletons = duplicateSampleSingletons(sql)



        //batch our select query in a closure (batchsize 25000)
        def qry = """
                    SELECT
                    id, filter_flag, var_depth, read_depth,variant, var_freq, var_panel_pct, gmaf, consequence, fwd_read_depth, rev_read_depth, fwd_var_depth, rev_var_depth
                    FROM seq_variant
        WHERE id > ? ORDER BY id LIMIT 50000
 """

        Integer maxId = 0

        Closure executeQuery = {

            def oldMaxId = maxId
            sql.eachRow(qry, [maxId]) { sv ->

                def initShouldHaveFlagList = []

                if (singletons.containsKey(sv.id.toString())) initShouldHaveFlagList << 'sin'
                if (!(inregions.containsKey(sv.id.toString()))) initShouldHaveFlagList << 'oor'


                def panelqry = "SELECT panel_group FROM panel INNER JOIN seq_sample ON seq_sample.panel_id=panel.id INNER JOIN seq_variant ON seq_variant.seq_sample_id=seq_sample.id WHERE seq_variant.id=${sv.id}"
                def res = sql.rows(panelqry)
                def panelg = ''

                if ( res ) {
                    panelg = res[0][0].toString()
                }

                //here we dont know if it should haev a sin or oor flag
                List shouldHaveFlagList = applyRules(sv, panelg, rules, initShouldHaveFlagList)
              //  System.exit(0)
                if (shouldHaveFlagList.empty) {
                    shouldHaveFlagList << 'pass'    //no filter flags means we set pass
                }

                def hasFlagsList = sv.filter_flag.tokenize(',')

                if (hasFlagsList.containsAll(shouldHaveFlagList) && shouldHaveFlagList.containsAll(hasFlagsList)) {
                    //all is well
                } else {
                    println "SeqVar ${sv.id} from panel ${panelg} has filter flags ${hasFlagsList.toString()} but should have ${shouldHaveFlagList.toString()}"
                }

                maxId = sv.id

            }
            if ( debug ) {
                Date now = new Date()
                println "Batch at " + now + " maxID ${maxId} "
            }
            return maxId != oldMaxId
        }

        while (executeQuery());

        // Clean up the database connection.
        //
        sql.close()
    }



    static List applyRules ( var, filterGroup, rules, flagList ) {

        //  Only process TSCA and Germline panels at this stage
        //

        //  Technical filtering: artifacts from assay type
        //  ==============================================


        def filters = rules.filters.keySet() as List


        if ( ! filters.contains(filterGroup))
        {

            if (!flagList) {
                flagList = ['nof']  //only set flag to NOF
            }

            return flagList
        }

        //  Technical filtering: artifacts from assay type
        //  ==============================================

        //  Test panel specific rules
        //
        Map filterRules = rules.filters."${filterGroup}"

        //  CurVariant read depth
        //
        if ( var.var_depth  < filterRules.varDepth  ) flagList << "vad"

        //  Total read depth
        //
        if ( var.read_depth < filterRules.readDepth ) flagList << "vrd"


        //  Black list variants
        //
        if (  filterRules.blackList && (var.variant in  filterRules.blackList)) flagList << "blk"

        //  CurVariant allele frequency
        //
        if (  filterRules.allelePct && (var.var_freq   < ( filterRules.allelePct as double))) flagList << "vaf"

        //  CurVariant panel frequency
        //
        if (  filterRules.varPanelPct && (var.var_panel_pct > ( filterRules.varPanelPct as double))) flagList << "pnl"

        //  Amplicon read distribution
        //

        if ( filterRules.ampCutoff && ampBias( var, rules.varDepth as Integer, filterRules.ampCutoff as int )) flagList << "amp"

        //  Biological filtering
        //  ====================

        //  Polymorphism check
        //
        if ( rules.gmaf && (var.gmaf > (rules.gmaf as double))) flagList << "gaf"

        //  Consequence of variant
        //
        String cons = var.consequence
        Boolean exclCons = false
        Boolean inclCons = false

        //  Exclusion consequences
        //
        List exc = rules.exclCons
        for ( String cs in exc )
            if ( cons.contains(cs))
            {
                exclCons = true
                break
            }

        //  Inclusion consequences
        //
        List inc = rules.inclCons
        for ( String cs in inc )
            if ( cons.contains(cs))
            {
                inclCons = true
                break
            }

        //  Filter excluded conseq. that are not also included
        //
        if ( exclCons && ! inclCons ) flagList << "con"

        return flagList
    }

    static boolean ampBias(  var,  varDepth,  cutoff )
    {
        //  Forward total reads but no variant reads
        //
        if ( var.fwd_read_depth > varDepth && var.fwd_var_depth < cutoff ) return true

        //  Reverse total reads but no variant reads
        //
        if ( var.rev_read_depth > varDepth && var.rev_var_depth < cutoff ) return true

        return false
    }




    /**
     * Load rules from a config file
     *
     * @param rulesPath Path fo config file
     *
     * @return  Map of rules parameters
     */
    static Map getFilterRulesConfig( String rulesPath )
    {
        Map rules = [:]

        File rf = new File(rulesPath)
        if ( ! rf.exists())
        {
            //log.fatal( "Filter rules file doesn't exist " + rulesPath )
            return rules
        }

        //
        //  Load ETL configuration
        //
        def cfg = new ConfigSlurper().parse(rf.toURL())

        return cfg.rules
    }

    private static Map getIrSeqvarsMap ( sql ) {

    }

    /**
     * returns a map where key is id of IR seqvaer
     * @param sql
     * @return
     */
    private static Map inRegionSeqVariants( sql ) {

        def qry = """
            select sv.id
            from
                  seq_variant as sv,
                  seq_sample as ss,
                  panel as pl,
                  roi as roi
            where sv.seq_sample_id = ss.id
                  and  ss.panel_id = pl.id
                  and  pl.id   = roi.panel_id
                  and  sv.chr  = roi.chr
                  and  sv.pos >= roi.start_pos and sv.pos <= roi.end_pos
        """

        def rowsBatch = sql.rows(qry).collect{it.values().toString().replaceAll("\\[",'').replaceAll("\\]",'')} //could batch this if needed


        // svs that do not have ROI are considered "in region" that is "not out of region" for our purposes
        //
        def qry_noroi = """
             select sv.id
            from
                  seq_variant as sv,
                  seq_sample as ss,
                  panel as pl
            where sv.seq_sample_id = ss.id
                  and  ss.panel_id = pl.id
                 and NOT EXISTS (SELECT 1 FROM roi WHERE roi.panel_id = pl.id)
        """

        def rowsBatchNoRoi = sql.rows(qry_noroi).collect{it.values().toString().replaceAll("\\[",'').replaceAll("\\]",'')} //could batch this if needed

//        println rowsBatchNoRoi
        def rows = rowsBatchNoRoi + rowsBatch


        def svmap = [:] // new HashMap<Integer,Boolean>();
        for (sv in rows) {
            svmap[sv] = true
        }


        return svmap

    }

    /**
     * Flatten a list into a single character delimited string. strings are wrapped in ''
     *
     * @param thisList  List to flatten
     * @param delimit   List delimiter, default ','
     * @return
     */
    static String listToStringForSql( List thisList, delimit=',')
    {
        StringBuilder sb = new StringBuilder();
        for (String s : thisList)
        {
            sb.append("'${s}'");
            sb.append(delimit);
        }
        def outList = sb.toString()
        outList = (outList.substring(0, outList.length()-1)) //remove last delimit character
        return outList
    }


    /**
     *
     *
     * Find all runs with duplicate sample prefixes (PM sample names)
     *
     * @return  List of ids of singleton variants for samples that are replicates
     */
    private static Map duplicateSampleSingletons(sql)
    {
        //   query to find all Seqruns with duplicate PM sample prefixes
        //  returns a List of arrays [ Seqrun, <sample prefix>, <no of Samples>]
        //
        //



        //build a nice long string for our LIKE clause
        def likestring = "sa.sample_name LIKE '%-1'"
        for (def i = 2; i < 10; i++) {
            likestring = likestring + " OR sa.sample_name LIKE '%-${i}'"

        }

        //sql to grab replicate samples. recall that a (-[1-9]) suffix means a replicate
        def qry =  "SELECT sa.seqrun_id, sa.sample_name," +
                "                            substring(sa.sample_name,1,char_length(sa.sample_name) - 2) as prefix," +
                "                            count(*) as noReps FROM seq_sample as sa " +
                "   where   " +
                "   ( ${likestring} )" +
                "   group by sa.seqrun_id," +
                "   sa.pat_sample_id, prefix"

        def runs = sql.rows( qry )
       



        //  Find all singleton variants in duplicate samples
        //
        List vars = []
        for( run in runs )
        {
            def isRep = true
            //have a check that the non-prefix verison exists, if there's only one. e.g. if 14K123-1 exists
            //but 14K123 does not, it's not really a rep, somebody just messed up.
            //we could disable this check to speed things up.

            //DISABLE
            if (run['noReps'] == 1) {
                def check = "SELECT ss.seqrun_id FROM seq_sample as ss WHERE sample_name='"+ run['prefix'] +"'"
                def checkruns = sql.rows( check )
                if (!checkruns) {
                    isRep = false
                }
            }
            if (isRep) {

                run['noSamples'] = run['noReps'] + 1
                vars = vars + (singletonVars(run, sql))
            }
        }


        vars = vars.flatten()
        def varmap = new HashMap<Integer,Boolean>();
        for (var in vars) {
            varmap[var] = true
        }

        return varmap
    }

    /**
     *
     *
     * Find all variants occurring once only in the same run in the same sample
     *
     * @param   run     Array of replicate samples to search [ Seqrun, <sample prefix>, <no of Samples>]
     * @return          List of SeqVariant ids that are singletons for replicate samples
     */
    private static List singletonVars( run, sql )
    {
        def seqrunid = run.seqrun_id
        def prefix = run.prefix
        def samcnt = run.noSamples

        def likestring = "sv.sample_name='${prefix}'"
        for (def i = 1; i < 10; i++) {
            likestring = likestring + " OR sv.sample_name='${prefix}-${i}'"

        }
        //  get all seqvars that only occur once in a set of replicate seqruns
        //
        def qry =
                    "select	sv.id "+
                    "from	seq_variant as sv "+
                    "join	seq_sample  as sa ON sv.seq_sample_id=sa.id "+
                    "where	sa.seqrun_id=${seqrunid} "+
                    "and		( ${likestring} ) "+
                    "group "+
                    "by 		sv.variant "+
                    "having  count(*) < 2"


        //below: toString throws [brackets] around the IDs so we strip them
        def vars = sql.rows(qry).collect{it.values().toString().replaceAll("\\[",'').replaceAll("\\]",'')}



        vars.trimToSize()
        return vars
    }



}
