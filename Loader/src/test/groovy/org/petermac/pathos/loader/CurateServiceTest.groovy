/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.pathos.loader

import org.petermac.pathos.curate.*
import org.petermac.util.DbConnect
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Created by lara luis on 25/01/2016.
 */
class CurateServiceTest extends GroovyTestCase
{
    static def curVariantService = new CurVariantService()    // only need a new because stand alone doesn't have Spring

    void setUp()
    {
        //  Load stand-alone Hibernate context - Database JDBC is embedded in <schema>_loaderContext.xml
        //
        def db = new DbConnect( "pa_local" )
        ApplicationContext context = new ClassPathXmlApplicationContext(db.hibernateXml)


    }

    void testFindCurVariantsByGenomic()
    {
        CurVariant.withTransaction
                {
                    status ->

                        def cnt = CurVariant.count()
                        assert cnt > 1500

                        def sample = '16M1234'
                        def var    = 'chr20:g.31024609G>A'

                        def sv = SeqVariant.findBySampleNameAndHgvsg( sample, var )
                        if ( ! sv?.curated )
                        {
                            return
                        }

                        CurVariant cv = sv.curated

                        assert sv.sampleName == sample
                        assert cv.hgvsg      == var

                        def cvs = curVariantService.findCurVariantsByGenomic( sv )
                        println "Found cvs[${cvs.size()}] ${cv}"

                        assert cvs.size() == 1
                }

        println "Done !"
    }
}
