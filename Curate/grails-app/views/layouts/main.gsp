<%@ page import="org.petermac.pathos.curate.*" %>
<%@ page import="org.petermac.util.Locator" %>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1">

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}?v=2" type="image/x-icon">
    <link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">

    <title><g:layoutTitle default="PathOS"/></title>


<script>
    %{--<g:set var="app" value="/${grailsApplication.metadata['app.context']}"/>--}%
    const application = "<g:context/>" || "/PathOS";
</script>

    <script src="<g:context/>/dist/js/vendor.min.js?v=<g:render template='/gitHash'/>"></script>
    <script src="<g:context/>/dist/js/scripts.min.js?v=<g:render template='/gitHash'/>"></script>
    <link href="<g:context/>/dist/css/main.css?v=<g:render template='/gitHash'/>" rel="stylesheet">
    <link href="<g:context/>/dist/css/jquery-ui.min.css?v=<g:render template='/gitHash'/>" rel="stylesheet">
    <link href="<g:context/>/dist/css/jquery.highlight-within-textarea.css?v=<g:render template='/gitHash'/>" rel="stylesheet">
    <link href="//fonts.googleapis.com/css?family=Open+Sans:300" rel="stylesheet">

    %{--
        Use this if you want to hide the hotfix stuff:
        <parameter name="hotfix" value="hide" />
        This way new, working pages will not be affected by old hotfix stuff.
    --}%
    <g:if test="${pageProperty(name: 'page.hotfix')!='off'}">
        <script src="<g:context/>/hotfix.js?v=<g:render template='/gitHash'/>"></script>
        <link href="<g:context/>/hotfix.css?v=<g:render template='/gitHash'/>" rel="stylesheet">
    </g:if>

    <g:layoutHead />
    <r:layoutResources />
    <style>

    .sidebar-table tr>td:first-child, .sidebar-table tr>th:first-child {
        padding-left: 0;
    }

    .sidebar-table tr>td:last-child, .sidebar-table tr>th:last-child {
        padding-right: 0;
    }

    <g:if test="${Locator.pathosEnv == 'pa_prod'}"></g:if>
    <g:elseif test="${Locator.pathosEnv == 'pa_uat'}">
    #pathos_logo {
        content:url("<g:context/>/dist/images/pathos_logo_uat.svg");
        /*background: #077c44;*/
        /*padding: 3px;*/
        /*border-radius: 3px;*/
    }

    .navbar-default,
    #pathos-footer #resizer,
    #footer-toggle i {
    background: #077c44;
    }
    #sidebar .module .moduletitle i {
    color: #077c44;
    }
    #searchResultsPage > form input {
    border: 3px solid #077c44;
    }

    #wrapper #sidebar-footer,
    #wrapper #sidebar-toggle i,
    #wrapper #sidebar-wrapper {
    background: #044c2a;
    }
    .navbar-default #notifications {
    background: #044c2a;
    }
    .navbar-default #top-right #loginHeader a:link,
    .navbar-default #top-right #loginHeader a:visited {
    color: #04044c;
    }

    </g:elseif>
    <g:else>
    #pathos_logo {
        content:url("<g:context/>/dist/images/pathos_logo_dev.svg");
        /*background: #7c0744;*/
        /*padding: 3px;*/
        /*border-radius: 3px;*/
    }


    .navbar-default,
    #pathos-footer #resizer,
    #footer-toggle i {
    background: #7c0744;
    }
    #sidebar .module .moduletitle i {
    color: #7c0744;
    }
    #searchResultsPage > form input {
    border: 3px solid #7c0744
    }

    #wrapper #sidebar-footer,
    #wrapper #sidebar-toggle i,
    #wrapper #sidebar-wrapper {
    background: #4c042a;
    }
    .navbar-default #notifications {
    background: #4c042a;
    }
    </g:else>

    </style>
</head>
<body>


<nav class="navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">

            <div id="top-left">
                <a href="<g:context/>/"><img src="<g:context/>/dist/images/pathos_logo_transparent.svg" class="navlogo" id="pathos_logo" alt="PathOS"></a>
                <a target="_blank" href="//www.petermac.org"><img src="<g:context/>/dist/images/petermac_logo.png" class="navlogo" alt="PeterMac" id="petermac_logo"></a>
            </div>

            <div id="top-middle">
            <sec:ifLoggedIn>
                <div id="searchHeader" class="search-div">
                    <form action="<g:context/>/search" method="get" id="searchableForm" name="searchableForm">
                        <input type="text" placeholder="Search" name="q" value="" size="15" id="q">
                    </form>
                </div>
            </sec:ifLoggedIn>
            </div>

            <div id="top-right">
                <div id="loginHeader">
                    <sec:ifLoggedIn>
                        <p>Welcome <authdetails:displayName/> <authdetails:roles/><br><g:link controller='logout'>Logout</g:link></p>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <h1><g:link controller='login' action='auth'>Login</g:link></h1>
                    </sec:ifNotLoggedIn>
                </div>
            </div>
            <div id="notifications" class="shrink">
                <table>
                    <thead><tr>
                        <td>Notifications</td>
                        <td title="Press \ to toggle notifications."><i class="fa fa-list"></i></td>
                    </tr></thead>

                    <tbody></tbody>

                    <tfoot><tr>
                        <td id="markAsRead"><a style="color: white;" href="#markAllAsRead" onclick="PathOS.notes.markAllAsRead()">Mark all as read</a> - <a style="color: white;" href="#clearNotes" onclick="PathOS.notes.clearNotes()">Clear all</a></td>
                        <td><a style="color: white;" href="#menu" onclick="PathOS.modules.menu.show()"><i class="fa fa-cog"></i></a></td>
                    </tr></tfoot>

                </table>
            </div>
        </div>
    </div>
</nav>



<div id="wrapper" <g:if test="${pageProperty(name: 'page.sidebar')=='show'}"></g:if><g:else>class="toggled"</g:else>>


    <div id="sidebar-toggle" title="Press ~ to toggle the menu.">
        <i class="fa pm-sidebar-chevron fa-large" aria-hidden="true"></i>
    </div>

    <!-- Sidebar -->
    <div id="sidebar-wrapper">
        <div id="sidebar">


            <g:if test="${pageProperty(name: 'page.sidebar')=='show'}">
            <div class="sidebar-module">
                <h4>Welcome to PathOS<br>Variant Management System</h4>
                <br>
                <p style="margin:0">
                    This is an application to curate variations found by high throughput sequencing of cancer patient blood or tumour samples. While every effort has been made to ensure the accuracy of this application and its data, it is the users responsibility to verify results via external sources when reporting.
                </p>
            </div>
            </g:if>




<sec:ifLoggedIn>

<div class="module" id="tables">
    <table>
        <tr class="moduletitle">
            <td>
                <a href="#tables" class="btn btn-default module-tables"><h1>Tables</h1></a>
            </td>
        </tr>
    </table>

    <table class="sidebar-table content">
        <thead>
        <tr>
            <th>Tables</th>
            <th>Records</th>
        </tr>
        </thead>
        <tbody>
            <tr id="loading_placeholder"><td>Loading...</td></tr>
        </tbody>
    </table>
</div>

<script>
    var tables = new PathOS.module({
        name: "tables",
        type: "established"
    });

    var table_data = PathOS.data.load("tables");

    if(Object.keys(table_data).length == 0 || table_data.date < Date.now() - (1000 * 60 * 60 * 24)) {
        // 1) Check to see if the timestamp is more than 1 day old
        // 2) Fetch data
        // 3) Save data
        // 4) Use data
        var url = "<g:context/>/search/tables";
        $.ajax(url, {
            success: function(data) {
                PathOS.data.save("tables", {
                    date: Date.now(),
                    data: data
                });
                populate_table_module(data);
            }
        });
    } else {
        // If the data exists, and isn't too old, just use it!
        populate_table_module(table_data.data);
    }
    function populate_table_module(data){
        $("#loading_placeholder").remove();
        d3.select("#tables .content tbody")
            .selectAll("tr")
            .data(Object.keys(data), function(d){return d;})
            .enter()
            .append("tr")
            .each(function(d){
                var row = d3.select(this);

                row.append('td')
                    .append('a')
                    .attr('href', data[d].link)
                    .html(data[d].title)
                    .classed(d, true);

                row.append('td').html("~"+data[d].count);
            });
    }


</script>


<div class="module" id="history"></div>
<div class="module" id="tags"></div>

<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_DEV">
<div class="module" id="admin"></div>

<script>
    var admin = new PathOS.module({
        name: "admin",
        title: "Admin Options",
        type: "default",
        data: [
            '<g:link controller="user" action="">Users</g:link>',
            '<g:link controller="vcfUpload" action="upload">Upload VCF</g:link>',
            '<g:link controller="seqrun" action="create">Create Seqrun</g:link>',
            '<g:link controller="seqSample" action="create">Create SeqSample</g:link>',
            '<g:link controller="admin" action="reportUpload">Upload Report Template</g:link>',
            '<g:link controller="search" action="reindex">Reindex Search</g:link>'
        ]
    })
</script>
</sec:ifAnyGranted>

</sec:ifLoggedIn>


            <div id="sidebar-footer" class="footer" role="contentinfo"><span><i class="fa fa-envelope" aria-hidden="true"></i> <a href="mailto:ken.doig@petermac.org?subject=PathOS Feedback on v<g:meta name="app.version"/> ${Locator.pathosEnv}&body=Hi Ken and team,%0A%0AThis is regarding on v<g:meta name="app.version"/> ${Locator.pathosEnv}.%0A%0A<Enter your message here>">Mail Feedback</a> PathOS v<g:meta name="app.version"/><g:if test="${Locator.pathosEnv} != 'pa_prod'"> ${Locator.pathosEnv}</g:if></span>
            </div>

            <div class="footer-filler"></div>
        </div>

    </div>
    <!-- /#sidebar-wrapper -->







    <!-- Page Content -->
    <div id="page-content-wrapper">
        <g:layoutBody/>
    </div>
    <!-- /#page-content-wrapper -->

</div>


<g:if test="${pageProperty(name: 'page.footer')=='on'}">
    <script type="text/javascript" src="<g:context/>/igv/igv.min.js?v=<g:render template='/gitHash'/>"></script>

    <div class="footer-filler"></div>
    <div title="Press / to toggle IGV.js" id="footer-toggle">
        <i class="fa pm-sidebar-chevron fa-large" aria-hidden="true"></i>
    </div>

    <div id="pathos-footer">
        <div id="resizer"></div>
        <div id="igvDiv"></div>
        <div id="footer-message">
            <h1>Hi this is the footer.</h1>
        </div>
    </div>
    <script>

        PathOS.hotkeys.add(191, toggleFooter);

        var drag = d3.drag();
        d3.select("#resizer").call(drag);

        var height = 300,
            curHeight = height;
        var mousePlace = 0;
        drag.on("start", function(){
            height = $("#pathos-footer").height(),
            curHeight = height;
            mousePlace = d3.event.sourceEvent.screenY;
        });
        drag.on("drag", function(){
            var dy = d3.event.sourceEvent.screenY - mousePlace;
            var pos = height - dy;
            var newHeight = Math.round(pos/10) * 10 + 6;

            if ( curHeight !== newHeight ) {
                curHeight = newHeight;
                d3.select("#pathos-footer").style("height", newHeight + "px");
                d3.select("#footer-toggle").style("bottom", newHeight + "px");
            }
        });
        drag.on("end", function(){
            height = $("#pathos-footer").height();
            d3.selectAll(".footer-filler").style("height", height+"px");
        });

        $("#footer-toggle").click(toggleFooter);

        function toggleFooter(e) {
            if(e) e.preventDefault();

            $("#pathos-footer").toggleClass("footerActive");
            $("#footer-toggle").toggleClass("footerActive");
            $(".footer-filler").toggleClass("footerActive");

            if ($("#pathos-footer").hasClass("footerActive")) {
                d3.select("#pathos-footer").style("height", height + "px");
                d3.select("#footer-toggle").style("bottom", height + "px");
                d3.selectAll(".footer-filler").style("height", height + "px");
            } else {
                d3.select("#pathos-footer").style("height", 0);
                d3.select("#footer-toggle").style("bottom", "20px");
                d3.selectAll(".footer-filler").style("height", 0);
            }
        }
    </script>

</g:if>

<script>
$("#sidebar-toggle").click(function(e){
    e.preventDefault();
    toggleSidebar();
});

PathOS.hotkeys.add(192, toggleSidebar);

function toggleSidebar() {
    $("#wrapper").toggleClass("toggled");
    if (PathOS.user) {
        if(d3.select("#wrapper").classed("toggled")) {
            PathOS.modules.settings[PathOS.user].sidebar[window.location.pathname] = 'hide';
        } else {
            PathOS.modules.settings[PathOS.user].sidebar[window.location.pathname] = 'show';
        }
        PathOS.data.save("modules", PathOS.modules.settings);
    }
}

PathOS.init({
    <sec:ifLoggedIn>
    user: "<authdetails:id/>",
    username: "<authdetails:displayName/>",
    </sec:ifLoggedIn>
    controller: "${controllerName}",
    action: "${actionName}"
});


<g:if test="${pageProperty(name: 'page.history')!='hide'}">
PathOS.history.add({
    title: document.title,
    url: window.location.href,
    time: Date.now()
});
</g:if>



<sec:ifLoggedIn>

var history = new PathOS.module({
    name: "history",
    title: "History",
    type: 'history',
    data: PathOS.history.show()
});

</sec:ifLoggedIn>



if(PathOS.params().q) {
    $("#searchableForm input").val(PathOS.params().q);
}

</script>

<r:layoutResources />

<script>

    $(document).ready(function(){



        // PathOS Notifications
        // PATHOS-2630
        // DKGM 10-August-2017
        PathOS.notes.init();

        <g:if test="${flash.message}">
            PathOS.notes.add("${flash.message}");
        </g:if>
        <g:if test="${flash.error}">
            PathOS.notes.addError("${flash.error}");
        </g:if>

        // PATHOS-2736
        // Some notifications come through like this, e.g. authorise first review
        // DKGM 11-Sept-2017
        <g:if test="${flash.messages}">
            <g:each in="${flash.messages}" var="message">
                PathOS.notes.add("${message}");
            </g:each>
        </g:if>
        <g:if test="${flash.errors}">
            <g:each in="${flash.errors}" var="error">
                PathOS.notes.addError("${error}");
            </g:each>
        </g:if>



//        $("#searchHeader").attr("id","searchResultsPage").detach().appendTo("#searchbar");

// This function keeps the legend box on the page, in the right position.
// Note that we have 2 magic numbers here, 70 and 105
// Fix if possible
        $(window).scroll(function(){
            if ($(window).scrollTop() > 70) {
                $("nav").addClass("shrink-nav");
                $("#wrapper").addClass("shrink-nav");
            } else {
                $("nav").removeClass("shrink-nav");
                $("#wrapper").removeClass("shrink-nav");
            }
        });
    });


    var environments_to_show_watermark = {
        "pa_haem": true,
        "pa_uat" : true,
        "pa_dev" : true,
        "pa_stage": true,
        "pa_local": true
    };

    if (environments_to_show_watermark.hasOwnProperty("${Locator.pathosEnv}")) {
        d3.select("body").append("span").classed("watermark", true).text("${Locator.pathosEnv} <g:render template='/gitHash'/>")
    }


    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-76178877-1', document.location.hostname);
    ga('set', 'screenResolution', window.innerWidth+'x'+window.innerHeight);
    ga('set', 'campaignKeyword', '<g:layoutTitle default="unknown"/>');
    <sec:ifLoggedIn>
    ga('set', 'userId', '<authdetails:id/>');
    </sec:ifLoggedIn>
    ga('set', 'location', document.location.href);
    ga('send', 'pageview');



</script>
</body>
</html>

