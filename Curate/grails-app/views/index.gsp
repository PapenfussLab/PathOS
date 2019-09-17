<%@ page import="grails.util.Environment; org.petermac.util.Locator; org.petermac.pathos.curate.*" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to PathOS</title>
    <parameter name="sidebar" value="show" />
    <parameter name="hotfix" value="off" />

<style>
#home-page {
    min-width: 1000px;
    width: 90%;
    margin: auto;
    padding: 20px;
    max-width: 1400px;
}
</style>

</head>
<body>
<div id="page-body" role="main">
    <sec:ifNotLoggedIn>
        <div class="container">
            <div class="row">
                <div class="col-md-6 col-md-offset-3">
                    <h1 class="text-center" style="padding:20%;">Welcome to PathOS, please <g:link controller='login' action='auth'>Login</g:link></h1>
                </div>
            </div>
        </div>
    </sec:ifNotLoggedIn>
    <sec:ifLoggedIn>
        <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_DEV, ROLE_VIEWER, ROLE_CURATOR, ROLE_LAB, ROLE_EXPERT">
            <script>
                $("#searchHeader").attr("id","searchBody").detach().appendTo("#page-body")
            </script>
            <section id='home-page'>
                <div class='row'>
                    <div class="outlined-box">
                        <h1 style="text-align:center; margin-bottom:10px;">Latest Sequenced Runs:</h1>
                        <table id="seqrun-list">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Seq Run</th>
                                    <th>Panel</th>
                                    <th>Experiment</th>
                                    <th>Platform</th>
                                    <th>QC</th>
                                </tr>
                            </thead>
                            <tbody>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
<tr><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>
                            </tbody>
                        </table>
                        <div id="seqrunListButtons"><span><a href="#showMore" onclick="showMoreSeqruns()">Show More</a><span id="showAllSeqruns" style="display:none;">&nbsp-&nbsp<a href="#showMore" onclick="showAllSeqruns()">Show All</a></span></span></div>
                    </div>
                </div>
            </section>
        </sec:ifAnyGranted>
        <sec:ifNotGranted roles="ROLE_ADMIN, ROLE_DEV, ROLE_VIEWER, ROLE_CURATOR, ROLE_LAB, ROLE_EXPERT">
            <div class="container">
                <div class="row">
                    <div class="col-md-8 col-md-offset-2">
<h1 class="text-center" style="text-align: center; padding:20%;">Your PeterMac login was successfully verified, however we could not find your PathOS account.<br><br>Please <a href="mailto:Christopher.Welsh@petermac.org?subject=New PathOS User <Your name here>&body=<Please CC your manager for approval>%0A%0AHi Ken and team,%0A%0AI need a PathOS user account%0AMy PeterMac username is: <Your username here>%0AI am from <Your department here> and I am a <Curator, Lab Tech, Clinician, Other>%0AThe Environment is: ${Locator.pathosEnv}%0A%0AThanks,%0A<Your real name here>">email us</a> to enable your account.</h1>
                    </div>
                </div>
            </div>
        </sec:ifNotGranted>
    </sec:ifLoggedIn>
</div>
<script>
    <g:set var="env" value="${Locator.pathosEnv}"/>

// Write the new patch notes here:
    var features = {
        "v1.5.3": [
            "Allow multiple column sort on svlist",
            "Add info diagram for AMP Curation",
            "Show correct AMP and CS info on svlist hover-over",
            "Update easygrid filters for research"
        ],
        "v1.5.2": [
            "Integrate VcfLoader into pipelines"
        ],
        "v1.5.1": [
            "Change how PathOS is deployed"
        ],
        "v1.5.0": [
            "Use ACMG and AMP guidelines for curating variants",
            "Don't load research runs into production PathOS",
            "Clean up command line binaries",
            "Make PathOS application context agnostic",
            "VCF Upload Page",
            "Don't delete SeqRelations with DbUtil",
            "Generate IGV_Session.xml at runtime"
        ],
        "v1.4.7": [
            "Fix a typo in DbUtil",
            "Fix Babble test 015",
            "Fix an annotation error",
            "PatientLoader should only send an acknowledgement to the message queue after a message has been successfully loaded."
        ],
        "v1.4.6": [
            "Record GnomAD as 0.0 instead of NULL when no data is found, since the JSON from VEP reports 0 as null.",
            "Better logging for SeqCNV loader",
            "Fixed a bug where tags could not be removed from a variant",
            "Fixed a bug where gene masks could not be edited on labAssays"
        ],
        "v1.4.5": [
            "Add support for gene masking to VcfDbCheck",
            "Give proper 401 responses after AJAX requests",
            "Use null safe accessors when saving curVariantReport objects",
            "Provide links to the GAFFA metrics pdf in PathOS per run"
        ],
        "v1.4.4": [
            "Fix google links for variants with deletions",
            "Stop Babble using too much memory"
        ],
        "v1.4.3": [
            "Allow users to refresh the Seqrun heatmap",
            "Show multiple COSMIC links on the CurVariant show and edit pages",
            "Navigate IGV.js by variant instead of gene",
            "Fix a bug in decomposeTumourNormal"
        ],
        "v1.4.2": [
            "Allow the database to handle SeqVariants with more than 255 characters worth of Cosmic annotations",
            "Allow the CurVariant edit page to properly display CurVariants which have long Cosmic annotations",
            "Upgrade the preferences menu",
            "Properly show Seqrun status for NextSeq runs",
            "Don't set Gene Mask to NULL when reloading samples",
            "Do not set the Sin flag on HybCap panels",
            "Properly set sampleType for TumourNormal samples",
            "Remove PNL condition from BRCA only filter",
            "Don't crash the svlist page if a LabAssay does not exist for a sample"
        ],
        "v1.4.1": [
            "Fixed searching on the panels field in the seqrun list",
            "Fixed a bug to prevent loading research runs into PathOS",
            "Improved gene masking",
            "Use gnomAD instead of ExAC",
            "Optimise svlist and seqrun pages"
        ],
        "v1.4.0": [
            "Updated VEP annotations",
            "Authorise Final Review no longer requires C5 variants to be reported",
            "Report Builder splits HGVSp field into reference and position",
            "New look for Seqrun Show page",
            "Multiple BAMs are now supported in IGV",
            "Curators can now override the pmClass of a variant",
            "GAFFA1 link has been added",
            "A QC graph for showing ROIs fro Hybrid Capture has been added",
            "Users can now create derived samples",
            "CiVIC has been added to PathOS"
        ],
        "v1.3.13": [
            "Canary will now correctly merge DelIns variants",
            "Babble message log now includes a datestamp",
            "Updated PathOS deploy script",
            "Parameterised the port number for PathOS database connections"
        ],
        "v1.3.12": [
            "Add an unmasking page to PathOS",
            "Allow unmasking of specific genes instead of the whole panel",
            "Improve the Audit Log",
            "Fix notification formatting when C5s are not marked as reported during a final review",
            "Sanitise title text on the svlist page",
            "Don't use a 24 hour clock in the bpipe event notifier"
        ],
        "v1.3.11": [
            "Remove NM_015338.5:c.1934dup from the Top Haem and MPN Simple filter",
            "Don't apply filter before verifying variants when authorising a review"
        ],
        "v1.3.10": [
            "Make Curate/Loader independent of Auslab",
            "[logmessage] displays a password when doing ps -ef logmess",
            "Update transcript and cds length in transcript table in database",
            "New seqsamples now get generic clincontext set by default",
            "We now create an audit record if clincontext is changed for a seqsample",
            "Remove hardcoded Atlassian addresses",
            "Replace unused Clinical Contexts from bootstrap function with real CCs"
        ],
        "v1.3.9": [
            'Modify "Top Haem" filter template',
            'Modify "MPN Simple" filter template',
            "Fixed error messages not showing when returned as a group",
            "Fixed SeqSample List Page",
            "Improved notification system"
        ],
        "v1.3.8": [
            "Added gitHash to Javascript and CSS resources",
            "Fixed date-time format for Patient DOB in SeqSampleReports"
        ],
        "v1.3.7": [
            "Tags on the svlist page are now filterable",
            "Git branch added to Application Status on the home page",
            "CurVariant inspector has been improved",
            "Better warnings and notification system",
            "utf8 characters are allowed in Evidence",
            "PubMed articles can now be added from the CurVariant Show page",
            "Fixed a bug where Pubmed articles were not added properly",
            "Citations are only added from mailmerge fields that are used",
            "Samples can now be cancelled from Auslab",
            "Allow users to delete CurVariants",
            "Changed the behaviour of PatSample details in reports, they will now use current details instead of time-of-creation details.",
            "Bugfix: Related samples need to be linked in PathOS",
            "Datasource has been refactored",
            "Filterpane has been fixed on many pages"
        ],
        "v1.3.6": [
            "All Sequenced Variants List no longer has Seqrun ordered by default, to prevent a crash"
        ],
        "v1.3.5": [
            "Fixed a bug where PMIDs were not being processed properly for reports",
            "Changed the behaviour of PatSample details in reports, they will now use current details instead of time-of-creation details.",
            "Fixed a bug where Lab and Curator could not view PDF reports.",
            "Curators can now use a local report template.",
            "Fixed a bug where 'Add Variant' button was broken for certain users",
            "Added exception handling so that messages are not lost if Auslab fails to correctly match their own specifications when sending messages to PeterMac",
            "Improved the Heartbeat function so it won't spam the server if it can't connect",
            "PathOS can now start up connections to Auslab without restarting the Curate server",
            "Audit log will now record transcript changes",
            "Turning off dbCreate update on datasource for production",
            "Changed remote mutatlyzer to local in Annotator",
            "Report Builder now respects the 'useAuslab' property",
            "Removed Report Templates from repository",
            "Cleaned up 'var percent' calculations",
            "PathOS now saves external sample number in PatSample",
            "Added heartbeat properties to example properties",
            "Give user-friendly error messages if there are database encoding errors",
            "Remove insecure pages from PathOS front end"
        ],
        "v1.3.4": [
            "Added support for utf-8 characters in reports",
            "Better handling of Auslab data",
            "The PathOS deploy script no longer overwrites the report templates",
            "Heartbeat flag is properly parsed into the system",
            "A security hole in Babble has been fixed",
            "Increased size of Audit.description to cope with large indels",
            "Certain Mail Merge fields are now sourced directly from the SeqSample"
        ],
        "v1.3.3": [
            "Fixed a bug where Pubmed articles without authors would cause a crash",
            "Fixed a bug where 'readonly' was not indicated in Firefox",
            "Fixed a pipeline bug",
            "Added a Heartbeat function to maintain the Iguana-Auslab connection",
            "Refactored Patient Loader"
        ],
        "v1.3.2": [
            "Fixed Patient Loading bug",
            "Fixed Authorised First Review bug"
        ],
        "v1.3.auslab": [
            "PathOS now supports HL7 messaging",
            "PathOS is now integrated with Auslab",
            "Report Builder Page allows users to customise reports"
        ],
        "v1.3.1": [
            "Fixed a bug where reporting a SeqVariant without Curating it would prevent a report being printed",
            "Fixed a bug where variants could be Curated in the same context twice"
        ],
        "v1.3.0": [
            "Clinical Context added",
            "New mailmerge fields for reports",
            "Admins can upload Report Templates",
            "Admins can edit Filter Flag Templates",
            "Panel Frequency has been reworked",
            "Automatic MolPath JIRA Issues have been cleaned up",
            "Variants are sorted differently on the Sequenced Sample page",
            "Tags are more accessible on the Sequenced Sample page",
            "Citations are generated from PubMed references"
        ]
    };


    var patch = new PathOS.module({
        name: 'patch',
        type: 'changelog',
        hide: true,
        title: "PathOS changelog",
        data: features
    });

    var status = new PathOS.module({
        name: 'status',
        type: 'default',
        hide: true,
        title: "Application Status",
        data: {
            "PathOS version":"<g:meta name="app.version"/>",
            "Build version":"${grailsApplication.metadata['app.buildNumber']}",
            "Environment":"${Locator.pathosEnv}",
            "Database Host": "${Locator.dbHost}",
            "Database Port": "${Locator.dbPort}",
            "Database Schema": "${Locator.dbSchema}",
            "Grails version":"<g:meta name="app.grails.version"/>",
            "Groovy version":"${GroovySystem.getVersion()}",
            "JVM version":"${System.getProperty('java.version')}",
            "Reloading active":"${Environment.reloadingAgentEnabled}",
            "Controllers":"${grailsApplication.controllerClasses.size()}",
            "Domains":"${grailsApplication.domainClasses.size()}",
            "Services":"${grailsApplication.serviceClasses.size()}",
            "Tag Libraries":"${grailsApplication.tagLibClasses.size()}",
            "git hash":"<g:render template='/gitHash'/>",
            "git date":"<g:render template='/gitDate'/>",
            "git branch": "<g:render template='/gitBranch'/>",
            "git tag": "<g:render template='/gitTag'/>",
            "build date": "<g:render template='/buildDate'/>"
        }
    });





<sec:ifLoggedIn>

//var max = PathOS.data.load("numberOfLatestSeqruns", 10);
var max = <preferences:numberOfSeqruns/>;
var panelList = "<preferences:panelList/>";
var list = d3.select("#seqrun-list tbody");

if(panelList) {
    d3.select("#showAllSeqruns").style("display", "inherit");
}

showMoreSeqruns();

    function showMoreSeqruns() {
        var url = '<g:context/>/Seqrun/latestRuns?'+panelList+'max='+max;
        max = max+10;

        $.ajax({
            type: "GET",
            url: url,
            success: drawSeqruns,
            cache: false,
            contentType: false,
            processData: false
        });
    }

    function showAllSeqruns() {
        var url = '<g:context/>/Seqrun/latestRuns?max='+max;
        panelList = "";

        $.ajax({
            type: "GET",
            url: url,
            success: drawSeqruns,
            cache: false,
            contentType: false,
            processData: false
        });
    }

    function drawSeqruns(d) {
        list.selectAll("tr").remove();
        list.selectAll("tr")
            .data(d)
            .enter()
            .append('tr')
            .each(function(d){
                var row = d3.select(this);

                var runDate = d.runDate,
                    seqrun = d.seqrun ? d.seqrun : "",
                    panelList = d.panelList ? d.panelList.split(',').join(', ') : "",
                    experiment = d.experiment ? d.experiment.split(',').join(', ') : "",
                    platform = d.platform ? d.platform.split(',').join(', ') : "";

                row.append('td').html(runDate);

                row.append('td')
                    .attr("nowrap", true)
                    .append('a')
                    .attr('href', '<g:context/>/seqrun/show/' + seqrun)
                    .text(seqrun);

                row.append('td')
                    .text(panelList);

                row.append('td').text(experiment);

                row.append('td').text(platform);

                var auth = row.append('td');

                if(d.authorised === null || d.authorised == "null") {
                    auth.html("Not&nbspset")
                        .classed('AuthNotSet', true);
                } else if (d.passfailFlag) {
                    auth.text("Pass")
                        .classed('AuthPass', true);
                } else {
                    auth.text("Fail")
                        .classed('AuthFail', true);
                }
            });
    }





</sec:ifLoggedIn>
</script>


</body>
</html>


