package org.petermac.pathos.curate


import grails.persistence.Entity
import org.petermac.pathos.curate.UserPrefs
@Entity

class UserPersonalPrefs extends UserPrefs {
    AuthUser authUser
    static constraints =
            {
                authUser nullable: true
            }
}
