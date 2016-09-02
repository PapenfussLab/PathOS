<%@ page import="org.petermac.pathos.curate.*" %>



<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'variant', 'error')} required">
    <label for="variant">
        <g:message code="variant.variant.label" default="Variant"/>
    </label>
    ${variantInstance?.variant}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'gene', 'error')} ">
    <label for="gene">
        <g:message code="variant.gene.label" default="Gene"/>

    </label>
    ${variantInstance?.gene}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'gene_type', 'error')} ">
    <label for="gene_type">
        <g:message code="variant.gene_type.label" default="Genetype"/>

    </label>
    ${variantInstance?.gene_type}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'hgvsc', 'error')} ">
    <label for="hgvsc">
        <g:message code="variant.hgvsc.label" default="Hgvsc"/>

    </label>
    ${variantInstance?.hgvsc}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'hgvsp', 'error')} ">
    <label for="hgvsp">
        <g:message code="variant.hgvsp.label" default="Hgvsp"/>

    </label>
    ${variantInstance?.hgvsp}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'consequence', 'error')} ">
    <label for="consequence">
        <g:message code="variant.consequence.label" default="Consequence"/>

    </label>
    ${variantInstance?.consequence}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'exon', 'error')} ">
    <label for="exon">
        Location
    </label>
    ${variantInstance?.chr}:${variantInstance?.pos} ${variantInstance?.exon}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'cosmicURL', 'error')} ">
    <label for="cosmicURL"><g:message code="variant.cosmicURL.label" default="Cosmic"/></label>
    <g:cosmicUrl cosmic="${variantInstance.cosmic}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'dbsnp', 'error')} ">
    <label for="dbsnpURL"><g:message code="variant.dbsnpURL.label" default="dbSNP"/></label>
    <g:dbsnpUrl  dbsnp="${variantInstance.dbsnp}"/>
</div>

<div class="fieldcontain mcg">
    <label for="mcgURL">
        MyCancerGenome
    </label>
    <a href="http://www.mycancergenome.org/" target="_blank">http://www.mycancergenome.org/</a>
</div>

<div class="fieldcontain bic">
    <label for="bicURL">
        BIC URL
    </label>
    <a href="http://research.nhgri.nih.gov/bic/" target="_blank">http://research.nhgri.nih.gov/bic/</a>
</div>

<div class="fieldcontain BIC ">
    <label for="bicURL">
        BIC DB
    </label>
    <g:if test="${RefBic.countByVariant(variantInstance.variant)}">
        <g:link controller="refBic" action="filter" params="${['filter.variant': variantInstance.variant, 'filter.op.variant': 'Equal']}">
            ${RefBic.countByVariant(variantInstance.variant)}
        </g:link>
    </g:if>
</div>

<div class="fieldcontain kconfab">
    <label for="kconfabURL">
        kConFab URL
    </label>
    <a href="http://www.kconfab.org" target="_blank">http://www.kconfab.org</a>
</div>

<div class="fieldcontain kconfab ">
    <label for="kconfabURL">
        kConFab DB
    </label>
    <g:if test="${RefKconfab.countByVariant(variantInstance.variant)}">
        <g:link controller="refKconfab" action="filter" params="${['filter.variant': variantInstance.variant, 'filter.op.variant': 'Equal']}">
            ${RefKconfab.countByVariant(variantInstance.variant)}
        </g:link>
    </g:if>
</div>

<div class="fieldcontain iarc">
    <label for="iarcURL">
        IARC URL
    </label>
    <a href="http://p53.iarc.fr" target="_blank">http://p53.iarc.fr</a>
</div>

<div class="fieldcontain iarc ">
    <label for="iarcURL">
        IARC DB
    </label>
    <g:if test="${RefIarc.countByVariant(variantInstance.variant)}">
        <g:link controller="refIarc" action="filter" params="${['filter.variant': variantInstance.variant, 'filter.op.variant': 'Equal']}">
            ${RefIarc.countByVariant(variantInstance.variant)}
        </g:link>
    </g:if>
</div>

<div class="fieldcontain clinvar">
    <label for="clinvarURL">
        NIH Clinvar URL
    </label>
    <a href="http://www.ncbi.nlm.nih.gov/clinvar" target="_blank">http://www.ncbi.nlm.nih.gov/clinvar</a>
</div>

<div class="fieldcontain clinvar ">
    <label for="clinvarURL">
        NIH Clinvar DB
    </label>
    <g:if test="${RefClinvar.countByVariant(variantInstance.variant)}">
        <g:link controller="refClinvar" action="filter" params="${['filter.variant': variantInstance.variant, 'filter.op.variant': 'Equal']}">
            ${RefClinvar.countByVariant(variantInstance.variant)}
        </g:link>
    </g:if>
</div>

<div class="fieldcontain emory">
    <label for="emoryURL">
        Emory URL
    </label>
    <a href="http://genetics.emory.edu/egl/emvclass/emvclass.php" target="_blank">http://genetics.emory.edu/egl/emvclass/emvclass.php</a>
</div>

<div class="fieldcontain emory ">
    <label for="emoryURL">
        Emory DB
    </label>
    <g:if test="${RefEmory.countByVariant(variantInstance.variant)}">
        <g:link controller="refEmory" action="filter" params="${['filter.variant': variantInstance.variant, 'filter.op.variant': 'Equal']}">
            ${RefEmory.countByVariant(variantInstance.variant)}
        </g:link>
    </g:if>
</div>

<div class="fieldcontain hgmd ">
    <label for="hgmdURL">
        HGMD DB
    </label>
    <g:if test="${RefHgmd.countByVariant(variantInstance.variant)}">
        <g:link controller="refHgmd" action="filter" params="${['filter.variant': variantInstance.variant, 'filter.op.variant': 'Equal']}">
            ${RefHgmd.countByVariant(variantInstance.variant)}
        </g:link>
    </g:if>
</div>

<div class="fieldcontain hgmd ">
    <label for="hgmdURL">
        HGMD Imputed DB
    </label>
    <g:if test="${RefHgmdImputed.countByVariant(variantInstance.variant)}">
        <g:link controller="refHgmdImputed" action="filter" params="${['filter.variant': variantInstance.variant, 'filter.op.variant': 'Equal']}">
            ${RefHgmdImputed.countByVariant(variantInstance.variant)}
        </g:link>
    </g:if>
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'ucscURL', 'error')} ">
    <label for="ucscURL"><g:message code="variant.ucscURL.label" default="UCSC"/></label>
    <g:ucscUrl chr="${variantInstance.chr}" pos="${variantInstance.pos}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'siftCat', 'error')} ">
    <label for="siftCat">
        <g:message code="variant.siftCat.label" default="Sift Class"/>

    </label>
    ${variantInstance?.siftCat}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'polyphenCat', 'error')} ">
    <label for="polyphenCat">
        <g:message code="variant.polyphenCat.label" default="Polyphen Class"/>

    </label>
    ${variantInstance?.polyphenCat}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'alamutURL', 'error')} ">
    <label for="alamutURL"><g:message code="variant.alamutURL.label" default="Alamut"/></label>
    <g:alamutUrl chr="${variantInstance.chr}" pos="${variantInstance.pos}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'alamutClass', 'error')} ">
    <label for="alamutClass">
        <g:message code="variant.alamutClass.label" default="Alamut Class"/>

    </label>
    ${fieldValue(bean: variantInstance, field: "alamutClass")}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'evidence', 'error')} ">
    <label for="evidence">
        <g:message code="variant.evidence.label" default="Evidence"/>
    </label>
    ${fieldValue(bean: variantInstance, field: "evidence")}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'pmClass', 'error')} required">
    <label for="pmClass">
        <g:message code="variant.pmClass.label" default="Pm Class"/>
    </label>
    <%--<g:select name="pmClass" from="${variantInstance.constraints.pmClass.inList}" required=""
              value="${variantInstance?.pmClass}" valueMessagePrefix="variant.pmClass"/>--%>
    ${fieldValue(bean: variantInstance, field: "pmClass")}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'classified', 'error')} ">
    <label for="classified">
        <g:message code="variant.classified.label" default="Classified By"/>

    </label>
    ${variantInstance?.classified}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'authorisedQcFlag', 'error')} ">
    <label for="authorisedFlag">
        <g:message code="variant.authorisedFlag.label" default="Authorised ?" />

    </label>
    <g:checkBox name="authorisedFlag" value="${variantInstance?.authorisedFlag}" />
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'authorised', 'error')} ">
    <label for="authorised">
        <g:message code="variant.authorised.label" default="Authorised By"/>

    </label>
    ${variantInstance?.authorised}
</div>

<div class="fieldcontain ${hasErrors(bean: variantInstance, field: 'reportDesc', 'error')} ">
    <label for="reportDesc">
        <g:message code="variant.reportDesc.label" default="Report Desc"/>

    </label>
    <g:textArea name="reportDesc" cols="40" rows="5" maxlength="8000" value="${variantInstance?.reportDesc}"/>
</div>
