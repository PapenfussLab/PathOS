/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.pathos.curate

import org.petermac.util.Locator
import org.petermac.annotate.DataSource
import org.grails.taggable.*
/**
 * Created by seleznev andrei on 30/09/2015.
 */
class AnnoVariantController {
    def loc = Locator.instance


    /**
     * list annotation for a seqvariant
     * @param id seqvariant id
     * @return
     */
    def listAnno( Long id ) {

        def sv = SeqVariant.get( params.id )


        def allAnnoMaps = []
        def rdb = loc.pathosEnv
        def dsource = new DataSource(rdb)


        List<String> dss = dsource.getDataSources( sv.hgvsg )
        def annoMap = new HashMap<String,Map>()

        def annoByCategory = new HashMap<String,ArrayList>()

        //get all categories, and initialise an empty map for them
        def allAnnoMeta = AnnoMetaData.findAll()
        for (annoMeta in allAnnoMeta) {
            annoByCategory[annoMeta.category] = []
        }

        def allTags
        def allTagClasses = []

        for ( code in dss ) //for each datasource...
        {

            def datasourceAnnotation = dsource.getValueMap( code, sv.hgvsg )
            def outputAnnoLists = [:] //a list of annotation maps

            if ( datasourceAnnotation )
            {

                for (item in datasourceAnnotation) {
                    if (item.value != null && item.value != "") {   //let's not care about fields with blank/null vals
                        AnnoMetaData thisMetaData = AnnoMetaData.findByColNameAndAnnoDataSource(item.key, code)

                        if (!thisMetaData) {    //make a new, blank entry if no metadata exists
                            thisMetaData = new AnnoMetaData(annoDataSource: code, colName: item.key, displayName: item.key, category: code).save(flush: true, failOnError: true)
                        }

                        def valueMap = [:]  //store all of an annotation column's data here


                        //set it
                        valueMap['colValue'] = item.value
                        valueMap['source'] = code
                        valueMap['colName'] = item.key.replaceAll('\\.','_')    //replace dots since they break CSS classnames that we use in the view
                        String tagString = ""
                        if (thisMetaData) {
                            valueMap['metadata'] = thisMetaData
                            valueMap['displayName'] = thisMetaData?.displayName
                            //for (tag in thisMetaData.tags) {
                            //    tagString = tagString + tag.replaceAll(" ", "_") + " "
                            //}
                        } else {
                            valueMap['metadata'] = [:]
                            valueMap['displayName'] = ""

                        }

                        valueMap['tags'] = tagString.trim()


                        def annoMapUniqueKey = item.key + '|' + code  //each annotation field is unqiue by name+code combination

                        annoMap[annoMapUniqueKey] = valueMap          //a map of everything, keyed uniquely

                        def category = thisMetaData.category
                        if (!annoByCategory.containsKey(category)) {  //avoid NullPointnerException if we have a previously-uncencountered category
                            annoByCategory[category] = []
                            
                        }
                        annoByCategory[category].add(valueMap)        //a map of everything, keyed by category
                        allTags = []
                        //allTags = AnnoMetaData.getAllTags()           //get all tags for metadata
                        //allTags = sortAnnoTags(allTags)               //arbitary sort function

                        //get a list of all tags for metadata with whitespaces removed, to use as css class names in the view
                        //
                        for (tag in allTags) {
                            allTagClasses.add(tag.replaceAll(" ", "_"))
                        }
                    }

                }
            }
        }


        // remove empty categories
        def annoByCategoryNotEmpty = annoByCategory.findAll { !it.value.isEmpty() }



        render( view: 'listAnno', model: [ hgvsg: sv.hgvsg, annoByCategory: annoByCategoryNotEmpty, allTags: allTags, allTagClasses: allTagClasses])
     }


    //this hard-sorts tags based on our arbitrary sorting order
    //
    def sortAnnoTags(tagMap) {

        //here we sort, hard coded.
        def sortOrderMap = ["Variant":1, "Insilico":2,"Population":3,"Identifier":4,"Reference":5,"Position":6,"Other":7]
        def sorted = tagMap.sort { a, b ->
            sortOrderMap[a] <=> sortOrderMap[b]
        }


        return sorted
    }

    //this hard-sorts categories based on our arbitrary sorting order
    //currently unused
    //
    def sortAnnoCategories(annoMap) {

        //here we sort, hard coded.
        def sortOrderMap = ["Variant":1, "Insilico":2,"Population":3,"Identifier":4,"Reference":5,"Position":6,"Other":7]
        HashMap sorted = annoMap.sort { a, b ->
            sortOrderMap[a.key] <=> sortOrderMap[b.key]
        }

         
        return sorted
    }





}
