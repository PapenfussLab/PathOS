/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.pipeline

import groovy.util.logging.Log4j

/**
 * Created for PathOS.
 *
 * Description:
 *
 * Core filtering routines shared by modules Grails:Curate and Hibernate:Loader
 *
 * User: doig ken
 * Date: 28/10/2013
 * Time: 11:39 AM
 */

@Log4j
class FilterCore
{
    /**
     * Apply filtering rules sequentially
     *
     * @param var           Map of SeqVariant properties to filter - Must match Domain Class SeqVariant.groovy
     * @param filterGroup   Filter group of sample containing variant
     * @param rules         Filtering rules to apply to variant
     * @param initFlag      Initial flags to use for variant (contains singleton flag if needed)
     * @return              Flag List to apply to variant
     */
    static List applyRules( Map var, String filterGroup, Map rules, String initFlag )
    {
        List flagList = []      // List of flags showing what filters were applied to variant

        //  Initialise flag list
        //
        if ( initFlag )
            flagList = initFlag.split(',')

        //  Only process TSCA and Germline panels at this stage
        //
        def filters = rules.filters.keySet() as List
        if ( ! filters.contains(filterGroup))
        {
            flagList << "nof"
            return flagList
        }

        //  Technical filtering: artifacts from assay type
        //  ==============================================

        //  Variant read depth
        //
        if ( var.varDepth  < rules.varDepth  ) flagList << "vad"

        //  Total read depth
        //
        if ( var.readDepth < rules.readDepth ) flagList << "vrd"

        //  Test panel specific rules
        //
        def filterRules = rules.filters."${filterGroup}"

        //  Black list variants
        //
        if (  filterRules.blackList && (var.variant in  filterRules.blackList)) flagList << "blk"

        //  Variant allele frequency
        //
        if (  filterRules.allelePct && (var.varFreq   < ( filterRules.allelePct as double))) flagList << "vaf"

        //  Variant panel frequency
        //
        if (  filterRules.varPanelPct && (var.varPanelPct > ( filterRules.varPanelPct as double))) flagList << "pnl"

        //  Biological filtering
        //  ====================

        //  Polymorphism check
        //
        if ( rules.gmaf && (var.gmaf > (rules.gmaf as double))) flagList << "gaf"

        //  Benign variants with only Missense consequences
        //
        //  Remove by AF 5-Mar-14
        //        if ( var.polyphenClass == 'benign' && var.siftClass == 'tolerated' && var.consequence == 'missense_variant' ) flagList << "ben"

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
}
