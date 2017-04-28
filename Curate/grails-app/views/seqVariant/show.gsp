<%@ page import="org.petermac.pathos.curate.SeqVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'seqVariant.label', default: 'SeqVariant')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-seqVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                 default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-seqVariant" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list seqVariant">

        <g:if test="${seqVariantInstance?.variant}">
            <li class="fieldcontain">
                <span id="variant-label" class="property-label"><g:message code="seqVariant.variant.label"
                                                                           default="Variant"/></span>

                <span class="property-value" aria-labelledby="variant-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="variant"/></span>

            </li>
        </g:if>
        <g:if test="${seqVariantInstance?.ens_variant}">
            <li class="fieldcontain">
                <span id="ens_variant-label" class="property-label"><g:message code="seqVariant.ens_variant.label"
                                                                               default="Ensvariant"/></span>

                <span class="property-value" aria-labelledby="ens_variant-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="ens_variant"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.gene}">
            <li class="fieldcontain">
                <span id="gene-label" class="property-label"><g:message code="seqVariant.gene.label"
                                                                        default="Gene"/></span>

                <span class="property-value" aria-labelledby="gene-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                        field="gene"/></span>

            </li>
        </g:if>


        <g:if test="${seqVariantInstance?.filtered}">
            <li class="fieldcontain">
                <span id="filtered-label" class="property-label"><g:message code="seqVariant.filtered.label"
                                                                            default="Filtered"/></span>

                <span class="property-value" aria-labelledby="filtered-label"><g:formatBoolean
                        boolean="${seqVariantInstance?.filtered}"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.filterFlag}">
            <li class="fieldcontain">
                <span id="filterFlag-label" class="property-label"><g:message code="seqVariant.filterFlag.label"
                                                                              default="Filter Flag"/></span>

                <span class="property-value" aria-labelledby="filterFlag-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="filterFlag"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.reportable}">
            <li class="fieldcontain">
                <span id="reportable-label" class="property-label"><g:message code="seqVariant.reportable.label"
                                                                              default="Reportable"/></span>

                <span class="property-value" aria-labelledby="reportable-label"><g:formatBoolean
                        boolean="${seqVariantInstance?.reportable}"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.consequence}">
            <li class="fieldcontain">
                <span id="consequence-label" class="property-label"><g:message code="seqVariant.consequence.label"
                                                                               default="Consequence"/></span>

                <span class="property-value" aria-labelledby="consequence-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="consequence"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.hgvsc}">
            <li class="fieldcontain">
                <span id="hgvsc-label" class="property-label"><g:message code="seqVariant.hgvsc.label"
                                                                         default="Hgvsc"/></span>

                <span class="property-value" aria-labelledby="hgvsc-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                         field="hgvsc"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.hgvsg}">
            <li class="fieldcontain">
                <span id="hgvsg-label" class="property-label"><g:message code="seqVariant.hgvsg.label"
                                                                         default="Hgvsg"/></span>

                <span class="property-value" aria-labelledby="hgvsg-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                         field="hgvsg"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.hgvsp}">
            <li class="fieldcontain">
                <span id="hgvsp-label" class="property-label"><g:message code="seqVariant.hgvsp.label"
                                                                         default="Hgvsp"/></span>

                <span class="property-value" aria-labelledby="hgvsp-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                         field="hgvsp"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.vepHgvsg}">
            <li class="fieldcontain">
                <span id="vepHgvsg-label" class="property-label"><g:message code="seqVariant.vepHgvsg.label"
                                                                            default="Vep Hgvsg"/></span>

                <span class="property-value" aria-labelledby="vepHgvsg-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="vepHgvsg"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.vepHgvsc}">
            <li class="fieldcontain">
                <span id="vepHgvsc-label" class="property-label"><g:message code="seqVariant.vepHgvsc.label"
                                                                            default="Vep Hgvsc"/></span>

                <span class="property-value" aria-labelledby="vepHgvsc-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="vepHgvsc"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.vepHgvsp}">
            <li class="fieldcontain">
                <span id="vepHgvsp-label" class="property-label"><g:message code="seqVariant.vepHgvsp.label"
                                                                            default="Vep Hgvsp"/></span>

                <span class="property-value" aria-labelledby="vepHgvsp-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="vepHgvsp"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.hgvspAa1}">
            <li class="fieldcontain">
                <span id="hgvspAa1-label" class="property-label"><g:message code="seqVariant.hgvspAa1.label"
                                                                            default="Hgvsp Aa1"/></span>

                <span class="property-value" aria-labelledby="hgvspAa1-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="hgvspAa1"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.sampleName}">
            <li class="fieldcontain">
                <span id="sampleName-label" class="property-label"><g:message code="seqVariant.sampleName.label"
                                                                              default="Sample Name"/></span>

                <span class="property-value" aria-labelledby="sampleName-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="sampleName"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.readDepth}">
            <li class="fieldcontain">
                <span id="readDepth-label" class="property-label"><g:message code="seqVariant.readDepth.label"
                                                                             default="Read Depth"/></span>

                <span class="property-value" aria-labelledby="readDepth-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="readDepth"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.varDepth}">
            <li class="fieldcontain">
                <span id="varDepth-label" class="property-label"><g:message code="seqVariant.varDepth.label"
                                                                            default="Var Depth"/></span>

                <span class="property-value" aria-labelledby="varDepth-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="varDepth"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.varFreq}">
            <li class="fieldcontain">
                <span id="varFreq-label" class="property-label"><g:message code="seqVariant.varFreq.label"
                                                                           default="Var Freq"/></span>

                <span class="property-value" aria-labelledby="varFreq-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="varFreq"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.fwdReadDepth}">
            <li class="fieldcontain">
                <span id="fwdReadDepth-label" class="property-label"><g:message code="seqVariant.fwdReadDepth.label"
                                                                                default="Fwd Read Depth"/></span>

                <span class="property-value" aria-labelledby="fwdReadDepth-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="fwdReadDepth"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.fwdVarDepth}">
            <li class="fieldcontain">
                <span id="fwdVarDepth-label" class="property-label"><g:message code="seqVariant.fwdVarDepth.label"
                                                                               default="Fwd Var Depth"/></span>

                <span class="property-value" aria-labelledby="fwdVarDepth-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="fwdVarDepth"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.revReadDepth}">
            <li class="fieldcontain">
                <span id="revReadDepth-label" class="property-label"><g:message code="seqVariant.revReadDepth.label"
                                                                                default="Rev Read Depth"/></span>

                <span class="property-value" aria-labelledby="revReadDepth-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="revReadDepth"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.revVarDepth}">
            <li class="fieldcontain">
                <span id="revVarDepth-label" class="property-label"><g:message code="seqVariant.revVarDepth.label"
                                                                               default="Rev Var Depth"/></span>

                <span class="property-value" aria-labelledby="revVarDepth-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="revVarDepth"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.chr}">
            <li class="fieldcontain">
                <span id="chr-label" class="property-label"><g:message code="seqVariant.chr.label"
                                                                       default="Chr"/></span>

                <span class="property-value" aria-labelledby="chr-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                       field="chr"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.pos}">
            <li class="fieldcontain">
                <span id="pos-label" class="property-label"><g:message code="seqVariant.pos.label"
                                                                       default="Pos"/></span>

                <span class="property-value" aria-labelledby="pos-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                       field="pos"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.exon}">
            <li class="fieldcontain">
                <span id="exon-label" class="property-label"><g:message code="seqVariant.exon.label"
                                                                        default="Exon"/></span>

                <span class="property-value" aria-labelledby="exon-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                        field="exon"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.cosmic}">
            <li class="fieldcontain">
                <span id="cosmic-label" class="property-label"><g:message code="seqVariant.cosmic.label"
                                                                          default="Cosmic"/></span>

                <span class="property-value" aria-labelledby="cosmic-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                          field="cosmic"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.dbsnp}">
            <li class="fieldcontain">
                <span id="dbsnp-label" class="property-label"><g:message code="seqVariant.dbsnp.label"
                                                                         default="Dbsnp"/></span>

                <span class="property-value" aria-labelledby="dbsnp-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                         field="dbsnp"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.alamutClass}">
            <li class="fieldcontain">
                <span id="alamutClass-label" class="property-label"><g:message code="seqVariant.alamutClass.label"
                                                                               default="Alamut Class"/></span>

                <span class="property-value" aria-labelledby="alamutClass-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="alamutClass"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.gmaf}">
            <li class="fieldcontain">
                <span id="gmaf-label" class="property-label"><g:message code="seqVariant.gmaf.label"
                                                                        default="Gmaf"/></span>

                <span class="property-value" aria-labelledby="gmaf-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                        field="gmaf"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.varPanelPct}">
            <li class="fieldcontain">
                <span id="varPanelPct-label" class="property-label"><g:message code="seqVariant.varPanelPct.label"
                                                                               default="Var Panel Pct"/></span>

                <span class="property-value" aria-labelledby="varPanelPct-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="varPanelPct"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.ens_transcript}">
            <li class="fieldcontain">
                <span id="ens_transcript-label" class="property-label"><g:message code="seqVariant.ens_transcript.label"
                                                                                  default="Enstranscript"/></span>

                <span class="property-value" aria-labelledby="ens_transcript-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="ens_transcript"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.ens_gene}">
            <li class="fieldcontain">
                <span id="ens_gene-label" class="property-label"><g:message code="seqVariant.ens_gene.label"
                                                                            default="Ensgene"/></span>

                <span class="property-value" aria-labelledby="ens_gene-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="ens_gene"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.ens_protein}">
            <li class="fieldcontain">
                <span id="ens_protein-label" class="property-label"><g:message code="seqVariant.ens_protein.label"
                                                                               default="Ensprotein"/></span>

                <span class="property-value" aria-labelledby="ens_protein-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="ens_protein"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.ens_canonical}">
            <li class="fieldcontain">
                <span id="ens_canonical-label" class="property-label"><g:message code="seqVariant.ens_canonical.label"
                                                                                 default="Enscanonical"/></span>

                <span class="property-value" aria-labelledby="ens_canonical-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="ens_canonical"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.refseq_mrna}">
            <li class="fieldcontain">
                <span id="refseq_mrna-label" class="property-label"><g:message code="seqVariant.refseq_mrna.label"
                                                                               default="Refseqmrna"/></span>

                <span class="property-value" aria-labelledby="refseq_mrna-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="refseq_mrna"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.refseq_peptide}">
            <li class="fieldcontain">
                <span id="refseq_peptide-label" class="property-label"><g:message code="seqVariant.refseq_peptide.label"
                                                                                  default="Refseqpeptide"/></span>

                <span class="property-value" aria-labelledby="refseq_peptide-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="refseq_peptide"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.existing_variation}">
            <li class="fieldcontain">
                <span id="existing_variation-label" class="property-label"><g:message
                        code="seqVariant.existing_variation.label" default="Existingvariation"/></span>

                <span class="property-value" aria-labelledby="existing_variation-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="existing_variation"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.domains}">
            <li class="fieldcontain">
                <span id="domains-label" class="property-label"><g:message code="seqVariant.domains.label"
                                                                           default="Domains"/></span>

                <span class="property-value" aria-labelledby="domains-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="domains"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.genedesc}">
            <li class="fieldcontain">
                <span id="genedesc-label" class="property-label"><g:message code="seqVariant.genedesc.label"
                                                                            default="Genedesc"/></span>

                <span class="property-value" aria-labelledby="genedesc-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="genedesc"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.cytoband}">
            <li class="fieldcontain">
                <span id="cytoband-label" class="property-label"><g:message code="seqVariant.cytoband.label"
                                                                            default="Cytoband"/></span>

                <span class="property-value" aria-labelledby="cytoband-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="cytoband"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.omim_ids}">
            <li class="fieldcontain">
                <span id="omim_ids-label" class="property-label"><g:message code="seqVariant.omim_ids.label"
                                                                            default="Omimids"/></span>

                <span class="property-value" aria-labelledby="omim_ids-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="omim_ids"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.clin_sig}">
            <li class="fieldcontain">
                <span id="clin_sig-label" class="property-label"><g:message code="seqVariant.clin_sig.label"
                                                                            default="Clinsig"/></span>

                <span class="property-value" aria-labelledby="clin_sig-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="clin_sig"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.biotype}">
            <li class="fieldcontain">
                <span id="biotype-label" class="property-label"><g:message code="seqVariant.biotype.label"
                                                                           default="Biotype"/></span>

                <span class="property-value" aria-labelledby="biotype-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="biotype"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.pubmed}">
            <li class="fieldcontain">
                <span id="pubmed-label" class="property-label"><g:message code="seqVariant.pubmed.label"
                                                                          default="Pubmed"/></span>

                <span class="property-value" aria-labelledby="pubmed-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                          field="pubmed"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.esp}">
            <li class="fieldcontain">
                <span id="esp-label" class="property-label"><g:message code="seqVariant.esp.label"
                                                                       default="Esp"/></span>

                <span class="property-value" aria-labelledby="esp-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                       field="esp"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.cosmicOccurs}">
            <li class="fieldcontain">
                <span id="cosmicOccurs-label" class="property-label"><g:message code="seqVariant.cosmicOccurs.label"
                                                                                default="Cosmic Occurs"/></span>

                <span class="property-value" aria-labelledby="cosmicOccurs-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="cosmicOccurs"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.cadd}">
            <li class="fieldcontain">
                <span id="cadd-label" class="property-label"><g:message code="seqVariant.cadd.label"
                                                                        default="Cadd"/></span>

                <span class="property-value" aria-labelledby="cadd-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                        field="cadd"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.cadd_phred}">
            <li class="fieldcontain">
                <span id="cadd_phred-label" class="property-label"><g:message code="seqVariant.cadd_phred.label"
                                                                              default="Caddphred"/></span>

                <span class="property-value" aria-labelledby="cadd_phred-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="cadd_phred"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.exac}">
            <li class="fieldcontain">
                <span id="exac-label" class="property-label"><g:message code="seqVariant.exac.label"
                                                                        default="Exac"/></span>

                <span class="property-value" aria-labelledby="exac-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                        field="exac"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.clinvarCat}">
            <li class="fieldcontain">
                <span id="clinvarCat-label" class="property-label"><g:message code="seqVariant.clinvarCat.label"
                                                                              default="Clinvar Cat"/></span>

                <span class="property-value" aria-labelledby="clinvarCat-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="clinvarCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.lrtCat}">
            <li class="fieldcontain">
                <span id="lrtCat-label" class="property-label"><g:message code="seqVariant.lrtCat.label"
                                                                          default="Lrt Cat"/></span>

                <span class="property-value" aria-labelledby="lrtCat-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                          field="lrtCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.mutTasteCat}">
            <li class="fieldcontain">
                <span id="mutTasteCat-label" class="property-label"><g:message code="seqVariant.mutTasteCat.label"
                                                                               default="Mut Taste Cat"/></span>

                <span class="property-value" aria-labelledby="mutTasteCat-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="mutTasteCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.mutAssessCat}">
            <li class="fieldcontain">
                <span id="mutAssessCat-label" class="property-label"><g:message code="seqVariant.mutAssessCat.label"
                                                                                default="Mut Assess Cat"/></span>

                <span class="property-value" aria-labelledby="mutAssessCat-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="mutAssessCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.fathmmCat}">
            <li class="fieldcontain">
                <span id="fathmmCat-label" class="property-label"><g:message code="seqVariant.fathmmCat.label"
                                                                             default="Fathmm Cat"/></span>

                <span class="property-value" aria-labelledby="fathmmCat-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="fathmmCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.metaSvmCat}">
            <li class="fieldcontain">
                <span id="metaSvmCat-label" class="property-label"><g:message code="seqVariant.metaSvmCat.label"
                                                                              default="Meta Svm Cat"/></span>

                <span class="property-value" aria-labelledby="metaSvmCat-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="metaSvmCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.metaLrCat}">
            <li class="fieldcontain">
                <span id="metaLrCat-label" class="property-label"><g:message code="seqVariant.metaLrCat.label"
                                                                             default="Meta Lr Cat"/></span>

                <span class="property-value" aria-labelledby="metaLrCat-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="metaLrCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.siftCat}">
            <li class="fieldcontain">
                <span id="siftCat-label" class="property-label"><g:message code="seqVariant.siftCat.label"
                                                                           default="Sift Cat"/></span>

                <span class="property-value" aria-labelledby="siftCat-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="siftCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.polyphenCat}">
            <li class="fieldcontain">
                <span id="polyphenCat-label" class="property-label"><g:message code="seqVariant.polyphenCat.label"
                                                                               default="Polyphen Cat"/></span>

                <span class="property-value" aria-labelledby="polyphenCat-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="polyphenCat"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.clinvarVal}">
            <li class="fieldcontain">
                <span id="clinvarVal-label" class="property-label"><g:message code="seqVariant.clinvarVal.label"
                                                                              default="Clinvar Val"/></span>

                <span class="property-value" aria-labelledby="clinvarVal-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="clinvarVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.lrtVal}">
            <li class="fieldcontain">
                <span id="lrtVal-label" class="property-label"><g:message code="seqVariant.lrtVal.label"
                                                                          default="Lrt Val"/></span>

                <span class="property-value" aria-labelledby="lrtVal-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                          field="lrtVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.mutTasteVal}">
            <li class="fieldcontain">
                <span id="mutTasteVal-label" class="property-label"><g:message code="seqVariant.mutTasteVal.label"
                                                                               default="Mut Taste Val"/></span>

                <span class="property-value" aria-labelledby="mutTasteVal-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="mutTasteVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.mutAssessVal}">
            <li class="fieldcontain">
                <span id="mutAssessVal-label" class="property-label"><g:message code="seqVariant.mutAssessVal.label"
                                                                                default="Mut Assess Val"/></span>

                <span class="property-value" aria-labelledby="mutAssessVal-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="mutAssessVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.fathmmVal}">
            <li class="fieldcontain">
                <span id="fathmmVal-label" class="property-label"><g:message code="seqVariant.fathmmVal.label"
                                                                             default="Fathmm Val"/></span>

                <span class="property-value" aria-labelledby="fathmmVal-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="fathmmVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.metaSvmVal}">
            <li class="fieldcontain">
                <span id="metaSvmVal-label" class="property-label"><g:message code="seqVariant.metaSvmVal.label"
                                                                              default="Meta Svm Val"/></span>

                <span class="property-value" aria-labelledby="metaSvmVal-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="metaSvmVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.metaLrVal}">
            <li class="fieldcontain">
                <span id="metaLrVal-label" class="property-label"><g:message code="seqVariant.metaLrVal.label"
                                                                             default="Meta Lr Val"/></span>

                <span class="property-value" aria-labelledby="metaLrVal-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="metaLrVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.siftVal}">
            <li class="fieldcontain">
                <span id="siftVal-label" class="property-label"><g:message code="seqVariant.siftVal.label"
                                                                           default="Sift Val"/></span>

                <span class="property-value" aria-labelledby="siftVal-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="siftVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.polyphenVal}">
            <li class="fieldcontain">
                <span id="polyphenVal-label" class="property-label"><g:message code="seqVariant.polyphenVal.label"
                                                                               default="Polyphen Val"/></span>

                <span class="property-value" aria-labelledby="polyphenVal-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="polyphenVal"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.mutStatus}">
            <li class="fieldcontain">
                <span id="mutStatus-label" class="property-label"><g:message code="seqVariant.mutStatus.label"
                                                                             default="Mut Status"/></span>

                <span class="property-value" aria-labelledby="mutStatus-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="mutStatus"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.mutError}">
            <li class="fieldcontain">
                <span id="mutError-label" class="property-label"><g:message code="seqVariant.mutError.label"
                                                                            default="Mut Error"/></span>

                <span class="property-value" aria-labelledby="mutError-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                            field="mutError"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.numamps}">
            <li class="fieldcontain">
                <span id="numamps-label" class="property-label"><g:message code="seqVariant.numamps.label"
                                                                           default="Numamps"/></span>

                <span class="property-value" aria-labelledby="numamps-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="numamps"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.amps}">
            <li class="fieldcontain">
                <span id="amps-label" class="property-label"><g:message code="seqVariant.amps.label"
                                                                        default="Amps"/></span>

                <span class="property-value" aria-labelledby="amps-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                        field="amps"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.ampbias}">
            <li class="fieldcontain">
                <span id="ampbias-label" class="property-label"><g:message code="seqVariant.ampbias.label"
                                                                           default="Ampbias"/></span>

                <span class="property-value" aria-labelledby="ampbias-label"><g:fieldValue bean="${seqVariantInstance}"
                                                                                           field="ampbias"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.homopolymer}">
            <li class="fieldcontain">
                <span id="homopolymer-label" class="property-label"><g:message code="seqVariant.homopolymer.label"
                                                                               default="Homopolymer"/></span>

                <span class="property-value" aria-labelledby="homopolymer-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="homopolymer"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.varcaller}">
            <li class="fieldcontain">
                <span id="varcaller-label" class="property-label"><g:message code="seqVariant.varcaller.label"
                                                                             default="Varcaller"/></span>

                <span class="property-value" aria-labelledby="varcaller-label"><g:fieldValue
                        bean="${seqVariantInstance}" field="varcaller"/></span>

            </li>
        </g:if>

        <g:if test="${seqVariantInstance?.seqSample}">
            <li class="fieldcontain">
                <span id="seqSample-label" class="property-label"><g:message code="seqVariant.seqSample.label"
                                                                             default="Seq Sample"/></span>

                <span class="property-value" aria-labelledby="seqSample-label"><g:link controller="seqSample"
                                                                                       action="show"
                                                                                       id="${seqVariantInstance?.seqSample?.id}">${seqVariantInstance?.seqSample?.encodeAsHTML()}</g:link></span>

            </li>
        </g:if>
        <g:showPageTags/>
    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${seqVariantInstance?.id}"/>
            <g:link class="edit" action="edit" id="${seqVariantInstance?.id}"><g:message
                    code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete"
                            value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
<script>
    <g:showPageTagsScript tags="${seqVariantInstance?.tags as grails.converters.JSON}" id="${seqVariantInstance?.id}" controller="seqvariant"/>
</script>
</body>
</html>
