<%@ page import="org.petermac.pathos.curate.Seqrun; org.petermac.pathos.curate.StatsService" %>

<div id="ntcReads" class="chartBox">
    <h3>NTC Reads</h3>

    <g:if test="${StatsService.ntcAmplicons(seqrunInstance,20)}">
        <table border="1" style="width: 400pt">
            <thead>
            <tr>
                <th>Sample</th>
                <th>Amplicon</th>
                <th>Reads</th>
            </tr>
            </thead>
            <g:each in="${StatsService.ntcAmplicons(seqrunInstance,20)}" var="amp">
                <tr>
                    <td>${amp.sampleName}</td>
                    <td>${amp.amplicon}</td>
                    <td>${amp.readsout}</td>
                </tr>
            </g:each>
        </table>
    </g:if>
    <g:else>
        <g:if test="${StatsService.ntcAmplicons(seqrunInstance,0)}">
            <p>There are less than 20 NTC Reads for this Sequenced Run.</p>
        </g:if>
        <g:else>
            <p>This are no NTC Reads for this Sequenced Run.</p>
        </g:else>
    </g:else>
</div>
