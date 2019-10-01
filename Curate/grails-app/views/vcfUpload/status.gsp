<%@ page import="org.petermac.pathos.curate.VarFilterService; org.petermac.pathos.curate.SeqVariant; org.petermac.pathos.curate.PatSample" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Upload VCF Result</title>

    <parameter name="history" value="hide" />

    <style type="text/css">

    #page-body {
        line-height: 1.5;
        margin: 2em;
    }
    </style>
</head>

<body>

<div class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <div id="page-body" role="main">

        <h1>VcfUpload status of ${seqrunName} ${sampleName}</h1>
       <p> Tagged in Anomaly as ${tagName}</p>
        <p>
           ${anomalyMessage}
        </p>
        <g:if test="${dbMessage}">
        <p>
            ${dbMessage}
        </p>
        </g:if>
    </div>
</div>
</body>
</html>
