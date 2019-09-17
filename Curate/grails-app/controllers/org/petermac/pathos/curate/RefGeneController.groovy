package org.petermac.pathos.curate

class RefGeneController
{
    def scaffold = true

    def genedesc(String gene) {
        String result = RefGene.findByGene(gene)?.genedesc ?: ""
        render result
    }
}
