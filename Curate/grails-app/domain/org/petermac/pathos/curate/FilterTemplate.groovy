package org.petermac.pathos.curate



import grails.persistence.Entity
import java.util.regex.Pattern
@Entity
class FilterTemplate
{
    String templateName //no spaces
    String displayName

    String template

    static mapping =
        {
            template        (type: 'text')
        }

    static constraints =
    {
        // no spaces for template name since it's used as a var name
        //
        templateName( unique:true, validator: { val -> if (val.contains(' ')) return 'value.hasASpace' } )
    }
}
