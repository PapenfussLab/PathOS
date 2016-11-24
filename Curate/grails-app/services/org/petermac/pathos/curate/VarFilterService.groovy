/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import org.hibernate.Hibernate
import org.hibernate.Session;
import org.hibernate.Transaction;
import groovy.util.logging.Log4j
import org.petermac.util.Locator

/**
 * Created for PathOS
 *
 * Description:
 *
 * Filter services to process all sequenced variants and apply a filter to each
 *
 * Author: Ken Doig
 * Date:   26-Apr-2014      Refactored filtering to bring all filtering methods into this class
 *                          Added filtering for singleton variants within duplicate samples in a run
 */

@Log4j
class VarFilterService
{
    static final int MAXFLUSH = 5000    // flush batch size

    static def loc = Locator.instance   // file locator

    static transactional = false        // modifying all SeqVariants can't be a single transaction

    def rules = null                    // holder for rules

    //  Constructor reads in all panel filter rules
    //
    VarFilterService()
    {
        rules = getRules()              // read in rules
    }

    /**
     * Apply the filter rules and curated links to each SeqVariant
     *
     * @param       force       Force overwrite of all filtering flags
     * @param       setCurate   List of vars to set the curate link for records
     * @return                  Number of rows updated
     */
    public int applyFilter( Session session, boolean force )
    {
      int mod   = 0

        //  Get variants that have a ROI
        //
        Map haveregions = getHasRoiVariants(force)


        // Get singleton vars (ones that occur only once in a replicate r/ship)
        //
        Map singletons = getSingletons()


        // Re read filter rules from config file
        //
        rules = getRules()

        //  Get the lookup table for the frequency of variant in the panel by sample
        //
        def varfreqMap = variantSamples()

        //  Get variants that are in their ROI
        //
        Map inregions = getInRegionSeqVariants()




        if ( force )
        {

                def rows = SeqVariant.executeQuery( "select count(*) from org.petermac.pathos.curate.SeqVariant")

                int cnt    = rows[0] as int

                int offset = 0

                while ( offset < cnt ) {
                    //  Loop through all Sequenced Variants and set Filter flag
                    //
                    SeqVariant.withTransaction
                            {
                                SeqVariant.findAll([max: MAXFLUSH, offset: offset]).each
                                        {
                                            SeqVariant variant ->
                                                ++mod

                                                def initFlagList = []
                                                if (singletons.containsKey(variant.id ))  initFlagList << 'sin'
                                                if (!(inregions.containsKey(variant.id))) {
                                                   if (haveregions.containsKey(variant.id))  initFlagList << 'oor'
                                                }

                                                setFilter( initFlagList, varfreqMap, variant )

                                                session.flush()

                                        }

                                offset += MAXFLUSH
                                log.info("Filtered ${mod} variants")
                                session.flush()
                                session.clear()

                                // workaround for a GORM memory leak bug (see http://burtbeckwith.com/blog/?p=73 for details)
                                //
                                DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP.get().clear()
                            }

                }

        }
        else
        {
            SeqVariant.withTransaction
            {
                //  capture all null flag seqvars
                //
                def nullSeqVars = SeqVariant.findAllByFilterFlagIsNull()


                //  Loop through all unset variants only and set Filter flag
                //
                nullSeqVars.eachWithIndex //these are vars that were null before updateRoiFlags was called
                {
                    SeqVariant variant, int i ->
                        ++mod

                        def initFlagList = []
                        if (singletons.containsKey(variant.id ))  initFlagList << 'sin'
                        if (!(inregions.containsKey(variant.id))) {
                            if (haveregions.containsKey(variant.id))  initFlagList << 'oor'
                        }

                        setFilter( initFlagList, varfreqMap, variant )
                }
            }
        }

        return mod
    }

    /**
     * Set the filter flags for this SeqVariant. Called for each sv to be updated
     *
     * @param initFlag      Initial flag setting
     * @param varfreqMap    Map of panels and variants to frequency of occurrence
     * @param variant       SeqVariant to be modified
     * @param cnt           Records count of Variants
     */
    private void setFilter( List initFlagList, Map varfreqMap, SeqVariant variant )
    {
        def    panelGroup   = variant.seqSample.panel.panelGroup        // panel group of the variant
        def    varfreq      = varfreqMap[ panelGroup ]                  // get the map of variant freq.
        //Double varPanelPct  = varfreq ? (varfreq[ variant.variant ]  as Double) : 0.0   // lookup the %

        //set panel freq calculations
        def panfreq = calcPanelFrequency(variant)
        if (panfreq.size() == 2) {
            variant.varSamplesSeenInPanel = panfreq[0]
            variant.varSamplesTotalInPanel = panfreq[1]
        }


        List flag = applyRules( variant.properties, panelGroup, rules, initFlagList )
        if ( flag )
        {
            variant.filterFlag = flag.join(',')
            variant.filtered   = false
        }
        else
        {
            variant.filterFlag = 'pass'
            variant.filtered   = true
        }

        //  Persist changes
        //
        variant.save()
    }

    /**
     * Apply filtering rules sequentially
     *
     * @param var           Map of SeqVariant properties to filter - Must match Domain Class SeqVariant.groovy
     * @param filterGroup   Filter group of sample containing variant
     * @param rules         Filtering rules to apply to variant
     * @param initFlag      Initial flags to use for variant (contains singleton flag if needed)
     * @return              Flag List to apply to variant
     */
    private static List applyRules( Map var, String filterGroup, Map rules, List flagList )
    {


        //  Only process TSCA and Germline panels at this stage
        //
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
        if ( var.varDepth  < filterRules.varDepth  ) flagList << "vad"

        //  Total read depth
        //
        if ( var.readDepth < filterRules.readDepth ) flagList << "vrd"


        //  Black list variants
        //
        if (  filterRules.blackList && (var.variant in  filterRules.blackList)) flagList << "blk"

        //  CurVariant allele frequency
        //
        if (  filterRules.allelePct && (var.varFreq   < ( filterRules.allelePct as double))) flagList << "vaf"

        //  CurVariant panel frequency
        //
        if (  filterRules.varPanelPct && (var.panelFreq() > ( filterRules.varPanelPct as double))) flagList << "pnl"

        //  Amplicon read distribution
        //

        if ( filterRules.ampCutoff && ampBias( var, filterRules.varDepth?.toInteger(), filterRules.ampCutoff?.toInteger() )) flagList << "amp"

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

    /**
     * Calculate if reads are distributed across amplicons Todo: use Canary ampBias
     *
     * @param var       Map of variant parameters
     * @param varDepth  Minimum variant depth
     * @param cutoff    Cutoff for an empty amplicon
     * @return          True if uneven reads across amplicons
     */
    static boolean ampBias( Map var, Integer varDepth, Integer cutoff )
    {
        //  Forward total reads but no variant reads
        //
        if ( var.fwdReadDepth > varDepth && var.fwdVarDepth < cutoff ) return true

        //  Reverse total reads but no variant reads
        //
        if ( var.revReadDepth > varDepth && var.revVarDepth < cutoff ) return true

        return false
    }

    /**
     * Set the curated link flag for all SeqVariants
     * New Variants may have been added
     *
     * @param   vars    CurVariant keys to update (empty == all Variants)
     * @return          Count of rows modified
     */
    public static int setCurated( List vars )
    {
        int mod = 0

        //  Find all CurVariant records in List vars
        //
        def variants
        if ( ! vars )
            variants = CurVariant.findAll()                            // collect all CurVariant rows
        else
        {
            def uniqVars = vars.unique()
            variants = CurVariant.findAllByVariantInList( uniqVars )   // collect Variants from List
        }

        log.info( "Setting curated for ${variants.size()} Variants")

        //  Loop through all Variants and set Curated link in SeqVariants
        //
        variants.eachWithIndex
        {
            CurVariant variant, int i ->

                if ( i % 100 == 0 )
                    log.info( "Row: ${i} Adding CurVariant associations [${variant}]" )

                //	Add all instances of this CurVariant found in SeqVariant
                //
                def seqvars = SeqVariant.findAllByVariant( variant.variant )

                for ( SeqVariant seqvar in seqvars )
                    variant.addToSeqVariants( seqvar )

                mod += seqvars.size()
        }

        log.info( "Set curated for ${mod} SeqVariants")

        return mod
    }

    //  Defined filtering criteria and short description
    //
    static public final Map filters =   [
                                            pass:'Passed',
                                            nof:'No Filtering',
                                            vrd:'Total Read Depth',
                                            vad:'Variant Read Depth',
                                            blk:'Black List',
                                            con:'Inferred Benign Consequences',
                                            vaf:'Low Variant Allele Frequency',
                                            gaf:'High Global Minor Allele Frequency',
                                            pnl:'High Variant Frequency in Panel',
                                            sin:'Singleton in duplicate sample for run',
                                            amp:'Uneven amplicon read distribution',
                                            oor:'Out of region of interest',
                                        ]

    /**
     * Generate summary stats for filtering
     *
     * @return
     */
    public static Map stats()
    {
        //  Loop through all Sequenced Variants and set Filter flag
        //
        def results = SeqVariant.withCriteria
        {
            //  Perform "group by" count of filterFlag property
            //
            projections
            {
                groupProperty("filterFlag")
                rowCount()
            }
        }

        //  Return a Map of filter flag descriptions, the 3-letter code and a count of occurrences in SeqVariants
        //
        Map stats = [:]

        for ( flags in results )
        {
            //  Add count for all flags in comma separated list
            //
            if ( flags[0] )
            {
                for ( flag in (flags[0] as String).split(','))
                {
                    if ( filters[flag] && flags.size() == 2)
                    {
                        def descFlag = filters[flag] + ": ${flag}"
                        stats[descFlag] = (stats[descFlag] ?: 0) + flags[1]
                    }
                }
            }
        }

        return stats
    }

    /**
     * Return a Map of panel groups and a count of the non-control samples
     *
     * @return  Map = [ panel_group: <no of samples> ]
     */
    public static Map panelSize()
    {
        //  Create single-line HQL query to take advantage of Gorm and Hibernate
        //
        //  Count all the non-control samples in SeqVariant by panel group
        //
        def qry =   """
                    select  pnl.panelGroup,
                            count(distinct sv.sampleName)
                    from    org.petermac.pathos.curate.SeqVariant as sv
                    join    sv.seqSample as ss
                    join    ss.panel     as pnl
                    where   pnl.panelGroup != 'R&D'
                    and     upper(sv.sampleName) not like 'NTC%'
                    and     upper(sv.sampleName) not like 'CTRL%'
                    and     upper(sv.sampleName) not like 'CONTROL%'
                    and     upper(sv.sampleName) not like 'HL60%'
                    and     upper(sv.sampleName) not like 'ACD1%'
                    and     upper(sv.sampleName) not like '1975%'
                    group
                    by      pnl.panelGroup
                    """

        //  Must cast query to java.lang.String for Hibernate
        //
        def panels = SeqSample.executeQuery( qry )

        //  Convert array to a Map of [ panel_group : no_samples ]
        //
        Map m = [:]
        for ( pnl in panels )
            m[(pnl[0])] = pnl[1]

        return m
    }

    /**
     * Calculate the frequency of a variant within all the samples of a panel group
     * Returned list should be ~ 40,000 variants/panels
     *
     * @return  Map of Maps of form [ panel_group : [ variant : <% of var frequency by sample>]]
     */
    public static def variantSamples()
    {
        Map pnls = panelSize()
        log.debug( "Found panels: " + pnls.size())

        //  Create single-line HQL query to take advantage of Gorm and Hibernate
        //
        //  Count all the non-control samples in SeqVariant by variant and panel group
        //
        def qry =   """
                    select  sv.variant,
                            pnl.panelGroup,
                            count(distinct ss.sampleName)
                    from    org.petermac.pathos.curate.SeqVariant as sv
                    join    sv.seqSample  as ss
                    join    ss.panel      as pnl
                    where   pnl.panelGroup != 'R&D'
                    and     upper(ss.sampleName) not like 'NTC%'
                    and     upper(ss.sampleName) not like 'CTRL%'
                    and     upper(ss.sampleName) not like 'CONTROL%'
                    and     upper(ss.sampleName) not like 'HL60%'
                    and     upper(ss.sampleName) not like 'ACD1%'
                    and     upper(ss.sampleName) not like '1975%'
                    group
                    by      sv.variant,
                            pnl.panelGroup"""

        //  Must cast query to java.lang.String for Hibernate
        //
        def vars = SeqVariant.executeQuery( qry )

        Map varpnls = [:]
        int row = 0

        for ( var in vars )
        {
            def    variant = var[0]
            def    panel   = var[1]
            double cnt     = var[2] as double

            if ((row++ % 100) == 0 )
                log.debug( "Row: ${row} CurVariant: ${variant} Panel: ${panel} Count: ${cnt}")

            // create empty map if first time we've seen panel
            //
            if ( ! varpnls[panel] ) varpnls[panel] = [:]

            //  Add on new entry for this variant
            //  [<variant> : <pct of samples with variant for this panel]
            //
            if ( pnls[panel] )
                varpnls[panel] << [(variant): cnt * 100.0 / (pnls[panel] as double)]
            else
                log.warn( "Unknown panel in pnls Map: ${panel}")
        }

        //  Returns a Map of Maps
        //
        //  [ panel_group : [ variant : pct_of_samples ]]
        //
        return varpnls
    }

    /**
     * Create a map of filter group parameters
     * Note: Used by Loader module
     *
     * @param   panel   Panel to find
     * @return          Map of filter group parameters
     */
    public Map getFilterGroup( String panel )
    {
        Map filterGroup = [ manifest: panel ?: 'NoPanel', filterGroup: 'Research', description: 'Research Panel' ]  // default filter group

        //  Search for panel in filter manifest lists or manifest patterns
        //
        def filters = rules.filters.keySet() as List
        for ( flt in filters )
        {
            def fr = rules.filters."${flt}"
            if ( fr.manifests.contains(panel)
            || ( fr.manifestPattern && (panel =~ /^${fr.manifestPattern}/ )))  // manifest pattern is anchored at start
            {
                //  Found a match set map and exit
                //
                filterGroup.filterGroup = flt
                filterGroup.description = fr.description
                break
            }
        }

        return filterGroup
    }

    /**
     * get singleton variant ids (that is, variants that are in a replicate sample, and are not present in all replicates
     * @return  List of ids of singleton variants for samples that are replicates
     */
    private static Map getSingletons()
    {
        //grab all seqsamples w replicate relationships (do check if they have more than 1 rep? not sure)

        SeqRelation.withTransaction {

            def qry = """
                      SELECT sr.id FROM org.petermac.pathos.curate.SeqRelation as sr
                      WHERE
                         relation='Replicate'
                      """
            def srids = SeqSample.executeQuery( qry )


            log.info( "Found ${srids.size()} Replicate SeqRelations")

            List vars = []

            for (srid in srids) {
               def thisSr = SeqRelation.read(srid)
               vars = vars + getSingletonsForRelation(thisSr)

            }

            def varmap = [:]
            for (var in vars) {
                varmap[var] = true
            }
            return varmap
        }


    }

    /**
     * get all singleton samples in a seqrelation
     * @param ssid - seqsample id
     * @param srid - seqrelation id
     * @return
     */
    private static List getSingletonsForRelation( SeqRelation sr  )
    {

        def qry2 = """
                   select sv.id FROM org.petermac.pathos.curate.SeqVariant as sv
                   join sv.seqSample as ss
                   WHERE :thisSeqRelation in elements(ss.relations)
                   GROUP BY
                         sv.variant
                        HAVING count(*) < 2
                   """

        def theseSvs = SeqVariant.executeQuery(qry2,[thisSeqRelation:sr])

        return theseSvs
    }


    /**
     * Get rules from known file config location Todo: move into grails config framework
     *
     * @return  Map of filtering rules
     */
    public Map getRules()
    {
        //  Read in rules
        //
        def rulesPath = loc.etcDir + 'FilterRules.groovy'

        log.info( "VarFilterService: ${rulesPath}")

        // Get filter rules from config file
        //
        return getFilterRulesConfig( rulesPath )
    }

    /**
     * Load rules from a config file
     *
     * @param rulesPath Path fo config file
     *
     * @return  Map of rules parameters Todo: make these the rules themselves
     */
    static Map getFilterRulesConfig( String rulesPath )
    {
        Map rules = [:]

        File rf = new File(rulesPath)
        if ( ! rf.exists())
        {
            log.fatal( "Filter rules file doesn't exist " + rulesPath )
            return rules
        }

        //
        //  Load ETL configuration
        //
        def cfg = new ConfigSlurper().parse(rf.toURL())

        return cfg.rules
    }


    /**
     * get all svs that have a ROI (& a null filter flag)
     * @param force get all, even those without null filter flag
     * @return
     */
    private static Map getHasRoiVariants( boolean force = true ) {
        def nullOnly = false
        if (!force )  nullOnly = true

        int maxId = 0
        int batchSize = 1500000
        int count = 0
        def svmap = [:]
        Closure executeQuery = {

            def oldMaxId = maxId

            def qry = """
        select sv.id
        from
              org.petermac.pathos.curate.SeqVariant as sv,
              org.petermac.pathos.curate.SeqSample as ss,
              org.petermac.pathos.curate.Panel as pl,
              org.petermac.pathos.curate.Roi as roi
        where sv.seqSample.id = ss.id
              and ss.panel.id = pl.id
              and pl.id   = roi.panel.id
              and sv.id > ${maxId} """

            if (nullOnly) {

                qry = qry + " AND sv.filterFlag IS NULL "
            }

            qry = qry + """
              order by sv.id
       """


            def rows = SeqVariant.executeQuery(qry, [max: batchSize]) //could batch this if needed

            for (sv in rows) {
                svmap[sv] = true
                maxId = sv
                count++
            }
            Date now = new Date()

            return (maxId != oldMaxId)
        }

        while (executeQuery());


        return svmap
    }



    /**
     * get IDs of all SeqVars that are in region & that have a null filter flag.
     * @param force get all, even those without null filter flag
     * @return
     */
    private static Map getInRegionSeqVariants( boolean force = true ) {
        def nullOnly = false
        if (!force )  nullOnly = true

        Date now = new Date()


        def qry = """
        select sv.id
        from
              org.petermac.pathos.curate.SeqVariant as sv,
              org.petermac.pathos.curate.SeqSample as ss,
              org.petermac.pathos.curate.Panel as pl,
              org.petermac.pathos.curate.Roi as roi
        where sv.seqSample.id = ss.id
              and  ss.panel.id = pl.id
              and  pl.id   = roi.panel.id
              and  sv.chr  = roi.chr  and  sv.pos >= roi.startPos and sv.pos <= roi.endPos
       """

        if (nullOnly) {

            qry = qry + " AND sv.filterFlag IS NULL"
        }

        def rowsBatch = SeqVariant.executeQuery(qry) //could batch this if needed
        def svmap = [:]
        for (sv in rowsBatch) {
            svmap[sv] = true
        }

        return svmap

    }


    /**
     * list to map with KEYS as values
     * @param thisList
     * @return
     */
    Map listToMap (thislist) {
        //collectEntries?
        def thismap = [:]
        for (listitem in thislist) {
            thismap[listitem] = true
        }

        return thismap

    }
 

    /**
     * Make a list of lists. we use this to batch update statements
     *
     * @param rows
     * @param increment
     * @return
     */
    List<String> buildBatchList( rows, increment = 200 )
    {

        List<String> rowbatches = new ArrayList<String>()
        def x = 0
        for (int i = 0; i < rows.size(); i = i + increment) {

            def from = i
            def to = i + increment
            if (to >= rows.size()) {

                to = (rows.size() - 1)

            }

            if (to > from) {    //check to be safe
                rowbatches[x] = rows[from..to]
                x++
            }
        }
        return rowbatches
    }

    /**
     * Flatten a list into a single character delimited string
     *
     * @param thisList  List to flatten
     * @param delimit   List delimiter, default ','
     * @return
     */
    String listToString( List thisList, delimit=',')
    {
        StringBuilder sb = new StringBuilder();
        for (String s : thisList)
        {
            sb.append(s);
            sb.append(delimit);
        }
        def outList = sb.toString()
        outList = (outList.substring(0, outList.length()-1)) //remove last delimit character
        return outList
    }




    /**
     * calc panel frequency for a given seqvariant
     * returns a list of 2 ints: nominator and denominator
     * @param sv
     * @return
     */
    List<Integer> calcPanelFrequency (SeqVariant sv) {

        def qry = """
                SELECT sv.hgvsg, ss.id, ss.sampleType FROM org.petermac.pathos.curate.SeqVariant as sv
                join sv.seqSample as ss
                WHERE ((ss.sampleType != 'Control' AND ss.sampleType != 'NTC' AND ss.sampleType !=' Synthetic') OR ss.sampleType IS NULL)
                AND ss.panel=:thisPanel
                """
        def res = SeqVariant.executeQuery(qry,[thisPanel:sv.seqSample.panel])
        def updated = 0

        def nall =  res.groupBy { it[1] }.size()


        def nvar
        try {
            nvar = res.findAll { it[0] == sv.hgvsg }.groupBy { it[1] }.size()
        } catch (all) { //we might not find anything because eg the sv is only in a control sample
           nvar = null
        }

        return  [nvar,nall]
    }


    /**
     * mass update function
     * sets panel freqs for all supplied variants that are in supplied panel
     * if an sv in the list is not in panel p, it will be ingored
     * @param svs list of svs
     * @param p panel
     * @return int num of svs updated
     */
    int setPanelFrequenciesForVariantsInPanel (Panel p) {
        def sSamples = SeqSample.findAllByPanel(p)
        def svs = SeqVariant.findAllBySeqSampleInList(sSamples)

        def qry = """
                SELECT sv.hgvsg, ss.id, ss.sampleType FROM org.petermac.pathos.curate.SeqVariant as sv
                join sv.seqSample as ss
                WHERE ((ss.sampleType != 'Control' AND ss.sampleType != 'NTC' AND ss.sampleType !=' Synthetic') OR ss.sampleType IS NULL)
                AND ss.panel=:thisPanel
                """
        def res = SeqVariant.executeQuery(qry,[thisPanel:p])
        def updated = 0

        def nall =  res.groupBy { it[1] }.size()

        for (sv in svs) {
            def nvar
            try {
                nvar = res.findAll { it[0] == sv.hgvsg }.groupBy{ it[1] }.size()
            } catch (all) { //we might not find anything because eg the sv is only in a control sample
                nvar = null
            }
            if (nvar) {
                sv.varSamplesSeenInPanel = nvar
                sv.varSamplesTotalInPanel = nall
                sv.save(flush:true,failOnError:true)
                updated++
            }

        }

        return updated
    }


}
