package org.petermac.pathos.curate


import grails.persistence.Entity

@Entity
class ClinContext
{
    String      code
    String      description
    AuthUser    createdBy

    public static final defaultClinContextCode = 'Generic'    //this is the final
    public static final defaultClinContextDescription = 'Generic Clinical Context'    //this is the final

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

    boolean varIsGeneric() {
        return this.code == ClinContext.defaultClinContextCode
    }

    public static ClinContext generic() {
        return ClinContext.findByCode(ClinContext.defaultClinContextCode)
    }
}
