package org.petermac.pathos.curate

import grails.persistence.Entity

@Entity
class AuthRole {

	String authority

	static mapping = {
		//cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
