package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class AuthUser {

	transient springSecurityService

	String username
	String password
    String email
    String displayName
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static transients = ['springSecurityService']

	static constraints = {
		username blank: false, unique: true
		password blank: false
        email blank: false
        displayName blank: false
	}

	static mapping = {
		password column: '`password`'
	}

	static searchable = {
		only = [ 'username', 'displayName', 'email' ]
	}

	Set<AuthRole> getAuthorities() {
		AuthUserAuthRole.findAllByAuthUser(this).collect { it.authRole } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}

    String toString() {
        return displayName
    }

}
