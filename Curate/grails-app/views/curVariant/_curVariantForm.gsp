<%@ page import="org.petermac.pathos.curate.*" %>

<table id="curVariantTable">

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'variant', 'error')} required">
        <td><g:message code="variant.variant.label" default="Variant"/></td>
        <td>
            ${variantInstance?.variant}
        </td>
    </tr>

    <tr id="clinContextHolder" class="fieldcontain ${hasErrors(bean: variantInstance, field: 'clinContext', 'error')} ">
        <td>Clinical Context</td>
        <td>
            <b>${variantInstance.clinContext? variantInstance.clinContext : 'None'}</b>
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'gene', 'error')} ">
        <td><g:message code="variant.gene.label" default="Gene"/></td>
        <td>
            ${variantInstance?.gene}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'gene_type', 'error')} ">
        <td><g:message code="variant.gene_type.label" default="Genetype"/></td>
        <td>
            ${variantInstance?.gene_type}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'hgvsc', 'error')} ">
        <td><g:message code="variant.hgvsc.label" default="Hgvsc"/></td>
        <td>
            ${variantInstance?.hgvsc}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'hgvsp', 'error')} ">
        <td><g:message code="variant.hgvsp.label" default="Hgvsp"/></td>
        <td>
            ${variantInstance?.hgvsp}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'consequence', 'error')} ">
        <td><g:message code="variant.consequence.label" default="Consequence"/></td>
        <td>
            ${variantInstance?.consequence}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'exon', 'error')} ">
        <td>Location</td>
        <td>
            ${variantInstance?.chr}:${variantInstance?.pos} ${variantInstance?.exon}
        </td>
    </tr>


    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'reportDesc', 'error')} ">
        <td><label for="reportDesc"><g:message code="variant.reportDesc.label" default="Report Description"/></label></td>
        <td style="background: white;">
            <g:textArea placeholder="Description of this variant, to be inserted into reports." class="highlightClass" name="reportDesc" cols="40" rows="5" maxlength="8000" value="${variantInstance?.reportDesc}"/>
        </td>
    </tr>






    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'classified', 'error')} ">
        <td><g:message code="variant.classified.label" default="Classified By"/></td>
        <td>
            ${variantInstance?.classified}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'authorisedQcFlag', 'error')} ">
        <td><label for="authorisedFlag">
            <g:message code="variant.authorisedFlag.label" default="Authorised ?" />
        </label></td>
        <td>
            <g:checkBox name="authorisedFlag" value="${variantInstance?.authorisedFlag}" />
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'authorised', 'error')} ">
        <td><g:message code="variant.authorised.label" default="Authorised By"/></td>
        <td>
            ${variantInstance?.authorised}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'classified', 'error')} ">
        <td><g:message code="variant.classified.label" default="Date of Authorisation"/></td>
        <td>
            ${variantInstance?.lastAuthorised?.format("dd-MMM-yyyy") }
        </td>
    </tr>






    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'siftCat', 'error')} ">
        <td><g:message code="variant.siftCat.label" default="Sift Class"/></td>
        <td>
            ${variantInstance?.siftCat}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'polyphenCat', 'error')} ">
        <td><g:message code="variant.polyphenCat.label" default="Polyphen Class"/></td>
        <td>
            ${variantInstance?.polyphenCat}
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'alamutURL', 'error')} ">
        <td><g:message code="variant.alamutURL.label" default="Alamut"/></td>
        <td>
            <g:alamutUrl chr="${variantInstance.chr}" pos="${variantInstance.pos}"/>
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'alamutClass', 'error')} ">
        <td><g:message code="variant.alamutClass.label" default="Alamut Class"/></td>
        <td>
            ${fieldValue(bean: variantInstance, field: "alamutClass")}
        </td>
    </tr>







    %{-- Under this line = probably useless --}%


    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'cosmicURL', 'error')} ">
        <td><g:message code="variant.cosmicURL.label" default="Cosmic"/></td>
        <td>
            <g:cosmicUrl cosmic="${variantInstance.cosmic}"/>
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'dbsnp', 'error')} ">
        <td><g:message code="variant.dbsnpURL.label" default="dbSNP"/></td>
        <td>
            <g:dbsnpUrl  dbsnp="${variantInstance.dbsnp}"/>
        </td>
    </tr>

    <tr class="fieldcontain ${hasErrors(bean: variantInstance, field: 'ucscURL', 'error')} ">
        <td><g:message code="variant.ucscURL.label" default="UCSC"/></td>
        <td>
            <g:ucscUrl chr="${variantInstance.chr}" pos="${variantInstance.pos}"/>
        </td>
    </tr>

</table>


