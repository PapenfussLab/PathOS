package org.petermac.pathos.curate



import grails.persistence.Entity

@Entity
class PanelFreq
{
    Panel   panel
    String   hgvsg

    Integer nvar
    Integer nall

    BigDecimal calcFreq() {
      return (nvar/nall)
    }

    static constraints =
    {
         hgvsg(unique:'panel')
    }

    String toString()
    {
        "${hgvsg}:${panel} " + calcFreq()
    }
}
