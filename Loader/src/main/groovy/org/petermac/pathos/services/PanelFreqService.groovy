package org.petermac.pathos.curate

import org.petermac.util.DbConnect
import org.petermac.util.Locator
import groovy.util.logging.Log4j
import groovy.time.*

class PanelFreqService {

    def sql
    static def loc = Locator.instance



    boolean calcLatestPanelFrequencies(String rdb = loc.pathosEnv) {
        def db  = new DbConnect( rdb )
        sql = db.sql()

        /*
        Exclude all samples where sampleType = 'Control' or sampleType='NTC' or sampleType='Synthetic' // eg tumour-normal joint called samples
        Use all other samples
        Don't calculate a panelFreq unless at least 100 samples are in the set
        Store both the denominator and numerator of the calculation and display this as a "hover-over" for panel frequency
        Recalculate panel frequencies nightly as a PathOS housekeeping process, ie not in real time as samples are added
         */

        //  get last seqrun that was updated
        //
        def sel = 	"""
                    select	id,
                             seqrun,
                            datetime
                    from	panel_freq_updated
                    where	id = 1
                    """

        def rows  = sql.rows(sel)

        Seqrun sr

        if( rows.size() == 1 && rows[0]?.seqrun) {
            def thisseqrun = rows[0].seqrun
            if (Seqrun.findBySeqrun(thisseqrun)) {
                sr = Seqrun.findBySeqrun(thisseqrun)
            }
        }
        if (!sr) {  //get earliest seqrun, if no last updated
            def srid = Seqrun.executeQuery("select min(id) from " + Seqrun.class.getName())[0]
            sr = Seqrun.get(srid)
        }
        if (!sr) {  //no seqruns? die
            println ("Could not get seqrun to start panel freq calculation from. Exiting")
            return false
        }

        //  get the latest seqrun, and the earliest seqrun. we need the latest to update the panel_freq_updated table.
        //

        def sridLatest = Seqrun.executeQuery("select max(id) from " + Seqrun.class.getName())[0]
        Seqrun    srLatest = Seqrun.get(sridLatest)


        println "sr " + sr + " and latest " + srLatest
        println "our sr to update from is now " + sr

        //  get panels that need their vars to have a recalc var freq
        //
        def seqrunsToUpdate = Seqrun.findAll("from "  +  Seqrun.class.getName() + " where id > ${sr.id}")

        //  for all our seqruns : add the involved panels to a set
        Set panels = []
        for(srun in seqrunsToUpdate) {
            println "getting panelset for " + srun + ":"
            def theseSamples = srun.getSeqSamples()

            for (ss in theseSamples) {
                panels.add(ss.panel)
            }

        }

        for (p in panels) {
            calcPanelFrequencies( p  )
        }

        //  update with srlatest.seqrun
        //
        def now = new Date().getTime()
        def timestamp = new java.sql.Timestamp(now)
        def upd =   """
                    update  panel_freq_updated
                    set     seqrun = ${srLatest.seqrun},
                            datetime  = ${timestamp}
                    where   id = 1;
                    """

        sql.execute( upd )

        return true;
        //idnoge
    }


    boolean calcPanelFrequencies(Panel p) {
        //  Exclude all samples where sampleType = 'Control' or sampleType='NTC' or sampleType='Synthetic' // eg tumour-normal joint called samples

     def qry = """SELECT sv.hgvsg, count(*) as c, ss.sampleType FROM org.petermac.pathos.curate.SeqVariant as sv
          join sv.seqSample as ss
            WHERE ((ss.sampleType != 'Control' AND ss.sampleType != 'NTC' AND ss.sampleType !=' Synthetic') OR ss.sampleType IS NULL)
            AND ss.panel=:thisPanel
            GROUP BY hgvsg"""
        def res = SeqVariant.executeQuery(qry,[thisPanel:p])
        println "got " + res.size() + " rows for panel " + p

        def nall = res.sum { it[1] }

        for (r in res) {    //todo catch exceptions
            def var = r[0]
            def varcount = r[1]

            def thisFreq = PanelFreq.findByHgvsgAndPanel(var,p)
            if(!thisFreq) {
                //this is very expensive! we actually pre-pop createPanelFreqUpdateTable
                def newPanelFrequency = new PanelFreq(hgvsg: var, nvar: varcount, nall: nall, panel: p)
            } else {
                thisFreq.setNvar(varcount)
                thisFreq.setNall(nall)
            }
        }


        return true
    }


    /*
     *  this is a run-once convenience setup function
     *  functions for sql db update table: direct sql, no gorm/domains
     */
    boolean createPanelFreqUpdateTable(String rdb = loc.pathosEnv) {
        def db  = new DbConnect( rdb )
        sql = db.sql()

        def create= """
                    create table if not exists panel_freq_updated
                    (
                        id          int unique,
                        seqrun        varchar(255),

                        datetime    datetime
                    )
                    """

        assert ! sql.execute( create ), "DB panel freq updated failed"
        assert ! sql.execute( "insert into panel_freq_updated values ( 1, '', '2000-01-01 12:00:00.0')" ) : " panel freq update failed "


        //  this populates the panel freq table
        println "Populating Panel Freq table with dummy data"
        def insertQry = """INSERT INTO panel_freq (hgvsg,nall,nvar,panel_id)
        SELECT sv.hgvsg, 0, 0, ss.panel_id
        FROM seq_variant as sv inner join seq_sample as ss on sv.seq_sample_id=sv.id"""

        assert ! sql.execute( create ), "DB panel insert failed"
        return  true
    }

}
