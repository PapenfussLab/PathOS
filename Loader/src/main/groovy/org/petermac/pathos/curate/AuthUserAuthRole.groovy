package org.petermac.pathos.curate

import org.apache.commons.lang.builder.HashCodeBuilder
import grails.persistence.Entity

@Entity
class AuthUserAuthRole implements Serializable {

	private static final long serialVersionUID = 1

	AuthUser authUser
	AuthRole authRole

	boolean equals(other) {
		if (!(other instanceof AuthUserAuthRole)) {
			return false
		}

		other.authUser?.id == authUser?.id &&
			other.authRole?.id == authRole?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (authUser) builder.append(authUser.id)
		if (authRole) builder.append(authRole.id)
		builder.toHashCode()
	}

	static AuthUserAuthRole get(long authUserId, long authRoleId) {
		AuthUserAuthRole.where {
			authUser == AuthUser.load(authUserId) &&
			authRole == AuthRole.load(authRoleId)
		}.get()
	}

	static AuthUserAuthRole create(AuthUser authUser, AuthRole authRole, boolean flush = false) {
		new AuthUserAuthRole(authUser: authUser, authRole: authRole).save(flush: flush, insert: true)
	}

	static boolean remove(AuthUser u, AuthRole r, boolean flush = false) {

		int rowCount = AuthUserAuthRole.where {
			authUser == AuthUser.load(u.id) &&
			authRole == AuthRole.load(r.id)
		}.deleteAll()

		rowCount > 0
	}

	static void removeAll(AuthUser u) {
		AuthUserAuthRole.where {
			authUser == AuthUser.load(u.id)
		}.deleteAll()
	}

	static void removeAll(AuthRole r) {
		AuthUserAuthRole.where {
			authRole == AuthRole.load(r.id)
		}.deleteAll()
	}

	static mapping = {
		id composite: ['authRole', 'authUser']
		version false
	}
}
