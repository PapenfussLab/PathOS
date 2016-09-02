package org.petermac.pathos.curate



import grails.persistence.Entity

@Entity
class JiraIssue
{

    AuthUser triggered_by

    String issueType
    //String issueStatus //in case we're doing PATHOS-489. NOT USED YET
    String issueIdentifier //the pathos-xxx identifier
    CurVariant curVariant

    static constraints =
    {
        issueType(  inList: [ "new_variant", "changed_class" ] )
        curVariant ( nullable: true )   //sometimes CurVariants get deleted. set this to nullable so we can null it for deleted CurVariants
        //issueStatus( nullable: true) //use an enum, when we finally do this. NOT USED YET
    }
}
