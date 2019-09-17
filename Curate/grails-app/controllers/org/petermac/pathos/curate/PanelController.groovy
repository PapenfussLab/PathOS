/*
 * Copyright (c) 2013. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.curate

import grails.converters.JSON

class PanelController
{
    def scaffold = Panel

    /**
     * Display the panel groups...
     * And the tests...
     * @return
     */
    def groups() {
        groupsInfo()
    }

    def groupsJSON() {
        render groupsInfo() as JSON
    }

    def latestTests() {
        def tests = []

        testNames().each { testName ->
            def info = PatAssay.executeQuery("""
select pa, sr, ss.panel
from PatAssay pa, PatSample ps, SeqSample ss, Seqrun sr
where pa.testName = :testName and ps = pa.patSample and ps = ss.patSample and sr = ss.seqrun

order by sr.runDate desc
""", [testName: testName, offset: 0, fetchSize: 1, readOnly: true, max: 1])[0]

            if(info)
                tests.push([
                    group: info[2]?.panelGroup,
                    panel: info[2].toString(),
                    testSet: info[0]?.testSet,
                    test: info[0].testName,
                    seqrun: info[1],
                    date: formatDate(date:info[1].runDate, format:'dd-MMM-yyyy'),
                    id: info[0].id
                ])
        }

        render tests.sort({it.seqrun.runDate}).reverse() as JSON
    }

    private ArrayList<String> testNames(){
        PatAssay.executeQuery("""
select pa.testName
from PatAssay pa
group by pa.testName
""")
    }

    private HashMap groupsInfo(){
ArrayList<String> pgs = Panel.executeQuery("""
select panel.panelGroup
from Panel panel
group by panel.panelGroup
""")

def panelGroups = []

pgs.each{ pg ->
    def info = Seqrun.executeQuery("""
select p, sr
from Seqrun sr, SeqSample ss, Panel p

where p.panelGroup = :pg and ss.panel = p and ss.seqrun = sr

order by sr.runDate desc
""", [pg: pg, offset: 0, fetchSize: 1, readOnly: true, max: 1])[0]

    panelGroups.push(info);
}

        HashMap results = [ "panelGroups" : panelGroups, "tests": testNames() ]

        return results
    }

    def fetchAllData() {
        render Panel.executeQuery("select p.manifest, p.panelGroup, p.description from Panel p") as JSON
    }



}
