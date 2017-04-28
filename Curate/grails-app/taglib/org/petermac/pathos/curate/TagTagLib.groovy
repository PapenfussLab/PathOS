/*
 * Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: David Ma (DKGM)
 */

package org.petermac.pathos.curate

class TagTagLib
{
    /**
     * Return all tags as a javascript array
     *
     * Todo:
     */
    def allTags =
    {
        def tags = []

        org.petermac.pathos.curate.Tag.findAll().each { obj ->
            if(!obj.isAuto) {
                tags.add(obj.label)
            }
        }

        out << "${tags as grails.converters.JSON}"
    }



    def showPageTags =
    {
        out << """
            <li class="fieldcontain" id="showTags">
                <span class="property-label">Tags</span>
                <div id="showTagBox" class="outlined-box tags_field property-value">
                    <textarea id="showTagTextArea" placeholder="Enter Tags Here" class="ui-autocomplete-input" autocomplete="off"></textarea>
                </div>
            </li>
                """
    }

    /*
        Maybe this stuff should be in our main.js script
        DKGM 20-July-2016
     */
    def showPageTagsScript =
    { attrs ->
        def tags = allTags

        out << """
            d3.select("#showTags .property-label").html("Tags of ${attrs.controller} instance")
            var tags = ${attrs.tags},
                tagbox = d3.select("#showTagBox").on('click', function(){
                    \$('#showTagTextArea').focus();
                });

                \$("#showTagTextArea").autocomplete({source:${tags}});

            tags.forEach(function(tag){
                PathOS.tags.drawTag(tagbox, tag);
            })
            PathOS.tags.current_object = parseInt("${attrs.id}");

            \$("#showTagTextArea").on("keydown", function(e){
                if (e && e.keyCode && e.keyCode == 13 && \$(document.activeElement).is("#showTagTextArea")){

                    var tag = \$('#showTagTextArea').val().trim();
                    \$('#showTagTextArea').val('');

                    if (tag && tag !== '' && PathOS.tags.current_object) {
                        PathOS.tags.addTag(tagbox, tag, "${attrs.controller}");
                    }
                } else if (e && e.keyCode && e.keyCode == 8 && \$(document.activeElement).is("#showTagTextArea") && \$("#showTagTextArea").val() === ""){

            if(\$("#showTagBox.tags_field .tagdiv:last").length !== 0){
                if (\$("#showTagBox .tagdiv:last").hasClass('deleteFlag')) {
                    var data = d3.select(\$("#showTagBox.tags_field .tagdiv:last")[0]).datum();

                    if(confirm('Remove tag "'+data.label+'" from this object?')) {
                        var params = {
                            type: PathOS.controller,
                            objid: PathOS.tags.current_object,
                            tagid: data.id
                        };
                        \$.ajax({
                            type: "DELETE",
                            url: "/PathOS/tag/removeLink?" + \$.param(params),
                            success: function (result) {
                                if(result != 'fail') {
                                    \$('.tag-'+data.id).remove();
                                }
                            },
                            cache: false,
                            contentType: false,
                            processData: false
                        });
                    }
                } else {
                    \$("#showTagBox.tags_field .tagdiv:last").toggleClass("deleteFlag").on('click', function(){
                        var data = d3.select(\$("#showTagBox.tags_field .tagdiv:last")[0]).datum();
                        var params = {
                            type: "${attrs.controller}",
                            objid: PathOS.tags.current_object,
                            tagid: data.id
                        };
                        \$.ajax({
                            type: "DELETE",
                            url: "/PathOS/tag/removeLink?" + \$.param(params),
                            success: function (result) {
                                if(result != 'fail') {
                                    \$('.tag-'+data.id).remove();
                                }
                            },
                            cache: false,
                            contentType: false,
                            processData: false
                        });
                    });
                }
            }
            }
            });
                """


    }
}
