package org.petermac.pathos.curate


import grails.persistence.Entity

//  this is a mediator object
//  represents a bidirectional r/ship between a cv and its matching sv

@Entity
class VarLink
{
    CurVariant  curVariant
    SeqVariant  seqVariant
    Boolean     originating
    Boolean     preferred           //  Todo: deprecated - The users don't need this information.

    // constraints to add:
    // custom validator: combo of (cv, sq) must be unique
    // custom validator: a cv can only have one Originating
    // custom validator: a sv can only have one Preferred
    //
//    static constraints =
//    {
//        //seqVariant( unique: 'curVariant' )  this is wrong, unless we have ClinContext too?
//
//        /*
//        originating validator: { val, obj ->
//           // println "IN VAL!" + val
//           // println "IN VAL!" + obj
//            if (val) {  //if we are tyring to set bool true
//                VarLink.findAllByCurVariant(obj.curVariant).each {
//                    if(it != obj && it.originating) { return false } //already have a true
//                }
//            }
//            return true
//        }
//
//        preferred validator: { val, obj ->
//           // println "IN VAL!" + val
//           // println "IN VAL!" + obj
//            if (val) {  //if we are tyring to set bool true
//                VarLink.findAllBySeqVariant(obj.seqVariant).each {
//                    if(it != obj && it.preferred) { return false } //already have a true
//                }
//            }
//            return true
//        }*/
//    }

    static mapping =
    {
        originating defaultValue: false
        preferred defaultValue: false
    }

    String	toString()
    {
        "${seqVariant} <-> ${curVariant}"
    }
}
