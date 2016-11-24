<%@ page import="grails.util.Environment; org.petermac.util.Locator; org.petermac.pathos.curate.*" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to PathOS</title>
    <parameter name="sidebar" value="show" />
    <parameter name="hotfix" value="off" />
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
                <div class='container'>
                    <div class='row'>
                        <div class="fb-box">
                            <h1 style="text-align:center; margin-bottom:10px;">Latest 10 Sequenced Runs:</h1>
                            <table id="seqrun-list">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Seq Run</th>
                                        <th>Panel</th>
                                        <th>Library</th>
                                        <th>Platform</th>
                                        <th>QC</th>
                                    </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
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

// Since search is the main point of this page, hide the search bar in the header.
//d3.select("#searchHeader").style("display", "none");

// Write the new patch notes here:
    var features =  [
                    "Logins match PeterMac logins",
                    "Added sample relationships (replicates,duplicates,tumour/normal)",
                    "User interface makeover, logo, navigation, object colours etc",
                    "Sliding panel IGV Browser",
                    "User tags and Smart system tags",
                    "Google-style search on all major objects",
                    "Simplified PathOS properties file",
                    "Added second review button",
                    "Automated release deployment"
                    ];


    var patch = new PathOS.module({
        name: 'patch',
        type: 'default',
        hide: true,
        title: "PathOS v1.2 (Jon Snow) feature list:",
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
            "Database Schema": "${Locator.dbSchema}",
            "Grails version":"<g:meta name="app.grails.version"/>",
            "Groovy version":"${GroovySystem.getVersion()}",
            "JVM version":"${System.getProperty('java.version')}",
            "Reloading active":"${Environment.reloadingAgentEnabled}",
            "Controllers":"${grailsApplication.controllerClasses.size()}",
            "Domains":"${grailsApplication.domainClasses.size()}",
            "Services":"${grailsApplication.serviceClasses.size()}",
            "Tag Libraries":"${grailsApplication.tagLibClasses.size()}",
            "git rev-parse HEAD":"<g:render template='/git'/>"
        }
    });





<sec:ifLoggedIn>

var url = '/PathOS/Seqrun/latestRuns';
var list = d3.select("#seqrun-list tbody");

$.ajax({
    type: "GET",
    url: url,
    success: function (d) {
        list.selectAll("tr")
                .data(d)
                .enter()
                .append('tr')
                .each(function(d){
                    var row = d3.select(this);

                    // This stupid split then join shit, is because we want a nonbreaking space here. DKGM 28-10-16
                    row.append('td').html(d.runDate.split(" ").join("&nbsp;"));

                    row.append('td')
                        .attr("nowrap", true)
                        .append('a')
                        .attr('href', function(d){
                            return '/PathOS/seqrun/show/'+ d.seqrun;
                        })
                        .text(function(d){
                            return d.seqrun;
                        });

                    row.append('td')
                        .text(d.panelList.split(',').join(', '));

                    row.append('td')
                            .text(d.library.split(',').join(', '));

                    row.append('td')
                        .text(d.platform.split(',').join(', '));

                    var auth = row.append('td');

                    if(d.authorised === null) {
                        auth.text("Not set")
                        .classed('AuthNotSet', true);
                    } else if (d.passfailFlag) {
                        auth.text("Pass")
                        .classed('AuthPass', true);
                    } else {
                        auth.text("Fail")
                        .classed('AuthFail', true);
                    }
                });
    },
    cache: false,
    contentType: false,
    processData: false
});






</sec:ifLoggedIn>
</script>


</body>
</html>
