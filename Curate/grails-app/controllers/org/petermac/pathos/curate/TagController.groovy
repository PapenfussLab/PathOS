package org.petermac.pathos.curate

import grails.converters.JSON
import org.grails.plugin.filterpane.FilterPaneUtils
import org.springframework.dao.DataIntegrityViolationException
import groovy.util.logging.Log4j

@Log4j
class TagController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def filterPaneService

    def springSecurityService

    def list(Integer max) {
        params.max = Math.min(max ?: 25, 100)
        if (!params.max) params.max = 10
        [tagList: Tag.list(params), filterParams: FilterPaneUtils.extractFilterParams(params)]
    }

    def filter = {
        if (!params.max) params.max = 25
        render(view: 'list',
                model: [tagList     : filterPaneService.filter(params, Tag),
                        tagCount    : filterPaneService.count(params, Tag),
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params      : params
                ]
        )
    }

    def create() {
        [tagInstance: new Tag(params)]
    }

    def save() {
        def tagInstance = new Tag(params)
        tagInstance.createdBy = springSecurityService.currentUser as AuthUser

        if (!tagInstance.save(flush: true)) {
            render(view: "create", model: [tagInstance: tagInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'tag.label', default: 'Tag'), tagInstance.id])
        redirect(action: "show", id: tagInstance.id)
    }

    def show(Long id) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }

        [tagInstance: tagInstance]
    }

    def edit(Long id) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }

        [tagInstance: tagInstance]
    }

    def update(Long id, Long version) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (tagInstance.version > version) {
                tagInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'tag.label', default: 'Tag')] as Object[],
                        "Another user has updated this Tag while you were editing")
                render(view: "edit", model: [tagInstance: tagInstance])
                return
            }
        }

        tagInstance.properties = params

        if (!tagInstance.save(flush: true)) {
            render(view: "edit", model: [tagInstance: tagInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'tag.label', default: 'Tag'), tagInstance.id])
        redirect(action: "show", id: tagInstance.id)
    }

    def delete(Long id) {
        def tagInstance = Tag.get(id)
        if (!tagInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
            return
        }

        try {
            tagInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'tag.label', default: 'Tag'), id])
            redirect(action: "show", id: id)
        }
    }


    def lookUp(Long id){
        render Tag.get(id) as JSON
    }

    /**
     * Add a tag to a taggable object
     * If the tag already exists (case insensitive), use the existing one
     * If it doesn't exist, create a new one with that user
     *
     * David Ma
     * 22 June 2016
     *
     * Maybe this should be called "addLink"...
     *
     * @return the tag's string or fail
     */
    def addTag() {
        def type = params.type.toLowerCase()
        def id = params.id
        def user = params.user
        def tag = params.tag

        def result = "fail"

        if( !type || !id || !user || !tag ) {
            result = "Missing paramater, please provide a type, id, user and tag"
        } else {
            def tagobject = Tag.withCriteria
            {
                eq( "label", params.tag, [ignoreCase: true])
            }[0] ?: new Tag([
                label: tag,
                isAuto: false,
                createdBy: AuthUser.get(user)
            ]).save(flush: true)

            if ( !tagobject.isAuto ) {
                if(type == 'pubmed') {
                    Pubmed.findByPmid(id).addToTags(tagobject)
                } else if(type == 'seqrun') {
                    Seqrun.get(id).addToTags(tagobject)
                } else if(type == 'curvariant') {
                    CurVariant.get(id).addToTags(tagobject)
                } else if(type == 'patsample') {
                    PatSample.get(id).addToTags(tagobject)
                } else if(type == 'seqsample') {
                    SeqSample.get(id).addToTags(tagobject)
                } else if(type == 'seqvariant') {
                    SeqVariant.get(id).addToTags(tagobject)
                } else {
                    tagobject = [fail:"Your type isn't supported yet"]
                }
                result = tagobject as JSON
            } else {
                result =  "This is a smart tag, you shouldn't be able to add this"
                // WARNING THIS IS A SMART TAG.
                // USERS SHOULD NOT BE TAGGING SMART TAGS???

                // What do we do here???
            }
        }
        render result
    }

    Map objGetterTable = [
        curVariant: { id -> CurVariant.get(id) },
        curvariant: { id -> CurVariant.get(id) },
        patSample:  { id -> PatSample.get(id) },
        patsample:  { id -> PatSample.get(id) },
        pubmed:     { id -> Pubmed.findByPmid(id) },
        seqCnv:     { id -> SeqCnv.get(id) },
        seqcnv:     { id -> SeqCnv.get(id) },
        seqrun:     { id -> Seqrun.get(id) },
        seqSample:  { id -> SeqSample.get(id) },
        seqsample:  { id -> SeqSample.get(id) },
        seqVariant: { id -> SeqVariant.get(id) },
        seqvariant: { id -> SeqVariant.get(id) }
    ]

    Map objNamerTable = [
        curvariant: { obj -> obj.hgvsc },
        seqrun:     { obj -> obj.seqrun }
    ]

    /**
     * Please refactor into addTag when you have time
     * DKGM 27-June-2017
     */
    def betterAddTag() {
        HashMap result = [error: 0]

        if(request.JSON) {
            AuthUser currentUser = springSecurityService.currentUser as AuthUser
            log.info "betterAddTag ${request.JSON}"
            Long id = request.JSON.id;
            Tag tag = Tag.findByLabel(request.JSON.label);

            if(tag) {
                tag.setDescription(request.JSON.description);
            } else {
                tag = new Tag([
                    label: request.JSON.label,
                    isAuto: false,
                    createdBy: currentUser,
                    description: request.JSON.description
                ]);
            }

            def type = request.JSON.type
            if(type && objGetterTable[type]) {
                objGetterTable[type](id).addToTags(tag);
            }
            result = [
                success: 1,
                tag: tag
            ]
        }

        render result as JSON;
    }





    def putDescription() {
        def description = params.description as String
        def id = params.id as Integer
        Tag.get(id).setProperty('description', description)
        render "success"
    }

    def fetchTags() {
        Long id = params.id as Long
        String type = params.type

        def results = [name:id, tags:[]];

        if (objGetterTable[type]) {
            def obj = objGetterTable[type](id)
            if (obj) {
                results.tags = obj.tags
                if (objNamerTable[type]) {
                    results.name = objNamerTable[type](obj)
                } else {
                    results.name = obj.toString()
                }
            } else {
                log.warn "no such id, while attempting to fetch tags for ${type}/${id}"
                results.error = "true"
                results.errorTagType = type
                results.errorTaggedId = id
                results.errorMessage = "no such id, while attempting to fetch tags for ${type}/${id}"
            }
        }
        render results as JSON
    }

    /**
     * Remove a link
     * Params required: type, id, tag.label
     *
     * @ToDo:
     * Check that it isn't an auto tag
     * Check that the user has permission to remove this link?
     * This function could be more eloquent
     * Perhaps it should use a try/catch block
     *
     * @return
     */
    def removeLink(Long tagid, String type, Long objid) {
        def result = 'fail'
        Tag tag = Tag.get(tagid)
        if(!tag?.isAuto) {
            type = type.toLowerCase()

            if (objGetterTable[type]) {
                result = objGetterTable[type](objid).removeFromTags(tag)
            }
        }
        render result
    }







}



































