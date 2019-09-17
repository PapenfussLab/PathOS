package org.petermac.pathos.curate

import grails.util.Holders

import java.text.MessageFormat

class AuditService {
    def SpringSecurityService

    /**
     * Audit logger
     * Most things are optional
     *
     * Please include:
     * category, task, description
     *
     * @param opt
     * @return
     */
    boolean audit(HashMap opt) {
        AuthUser currentUser = springSecurityService.currentUser as AuthUser

        HashMap options = [
            category:    opt.category       ?: 'unknown',
            variant:     opt.variant        ?: null,
            patSample:   opt.patSample      ?: null,
            sample:      opt.sample         ?: null,
            seqrun:      opt.seqrun         ?: null,
            complete:    opt.complete       ?: new Date(),
            elapsed:     opt.elapsed        ?: 0,
            software:    opt.software       ?: 'PathOS',
            swVersion:   opt.swVersion      ?: Holders.grailsApplication.metadata['app.version'],
            task:        opt.task           ?: 'unknown',
            username:    opt.username       ?: currentUser.getUsername() ?: "",
            description: opt.description    ?: "no description"
        ]

        Audit audit = new Audit(options)

        if ( ! audit.save( flush: true )) {
            audit?.errors?.allErrors?.each {
                log.error( new MessageFormat(it?.defaultMessage)?.format(it?.arguments))
            }
            log.error( "Failed to log audit message: ${options.description}")
            return false
        }
        return true
    }
}














