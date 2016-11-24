package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class ClinContext
{
    String      code
    String      description
    AuthUser    createdBy

    static constraints =
    {
        description( unique:true)
        code( unique:true)
        createdBy(nullable:true)
    }

    String	toString()
    {
        "${description} (${code})"
    }
}
