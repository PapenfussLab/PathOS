package org.petermac.pathos.curate


import grails.persistence.Entity
import org.petermac.pathos.curate.UserPrefs
@Entity

class ReviewedSamplePrefs extends UserPrefs {
    SeqSample seqSample //the seqsample that the prefs are tied to
    static constraints =
            {
                seqSample unique: true
            }
}
