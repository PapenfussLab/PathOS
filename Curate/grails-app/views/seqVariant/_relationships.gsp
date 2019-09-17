<%@ page import="org.petermac.pathos.curate.RelationService; org.petermac.pathos.pipeline.UrlLink" %>

<g:set var="relation" bean="relationService"/>
<g:set var="sample" value="${seqSample.sampleName}"/>
<g:set var="seqrun" value="${seqSample.seqrun.seqrun}"/>
<g:set var="baseUrl" value="${UrlLink.dataUrl(seqrun, sample)}"/>

<div id="relationshipDiv" style="overflow:scroll;" class="shrink outlined-box">

<table id="relationshipTable">
    <thead>
    <tr>
        <th>Relation</th>
        <th style="width:99%;">Sample</th>
        <th>Type</th>
        <th>IGV</th>
        <th>IGV.js</th>
    </tr>
    </thead>
    <tbody>
    <g:if test="${seqSample.seqrun.platform == 'MiSeq'}">
        <tr><td><p>Canary</p></td>
            <td></td>
            <td></td>
            <td><a target="_blank" href="http://localhost:60151/load?file=${baseUrl}CAN/${sample}.bam&merge=true">load</a></td>
            <td><a href="#igvjs" onclick="PathOS.igv.addDirectBAM('${baseUrl}CAN/${sample}.bam', 'Canary')">load</a></td>
        </tr>
    </g:if>
    <g:elseif test="${seqSample.seqrun.platform == 'NextSeq'}">
        <tr><td>Haplotype</td>
            <td></td>
            <td></td>
            <td><a target="_blank" href="http://localhost:60151/load?file=${baseUrl}BAM/${sample}_HAP.bam&merge=true">load</a></td>
            <td><a href="#igvjs" onclick="PathOS.igv.addDirectBAM('${baseUrl}BAM/${sample}_HAP.bam', 'Haplotype')">load</a></td>
        </tr>

        <tr><td>SV</td>
            <td></td>
            <td></td>
            <td><a target="_blank" href="http://localhost:60151/load?file=${baseUrl}SV/Bam/${sample}_gridss.assembly.bam.sv.bam&merge=true">load</a></td>
            <td><a href="#igvjs" onclick="PathOS.igv.addDirectBAM('${baseUrl}SV/Bam/${sample}_gridss.assembly.bam.sv.bam', 'Structural Variant')">load</a></td>
        </tr>
    </g:elseif>

    <g:each in="${relation.relationships(seqSample)}" var="blob">
        <g:set var="ss" value="${blob.seqSample}"/>
        <tr>
            <td>${blob.relation}</td>
            <td style="line-height: 1.1em;"><a href="<g:context/>/seqrun/show/${ss?.seqrun?.id}">${ss.seqrun.seqrun}</a><br>
                <a href="<g:context/>/seqVariant/svlist/${ss.id}">${ss}</a></td>
            <td>${ss.sampleType ?: ""}</td>
            <td><a target="_blank" href="http://localhost:60151/load?file=${UrlLink.dataUrl(ss?.seqrun?.seqrun, ss?.sampleName)}${ss?.sampleName}.bam&merge=true">load</a></td>
            <td><a href="#igvjs" onclick="PathOS.igv.addDirectBAM('${UrlLink.dataUrl(ss?.seqrun?.seqrun, ss?.sampleName)}${ss?.sampleName}.bam', '${ss?.sampleName}')">load</a></td>
        </tr>
    </g:each>
    </tbody>
</table>

</div>

















































