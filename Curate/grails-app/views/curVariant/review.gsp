<%@ page import="org.petermac.pathos.curate.CurVariant" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Variant Review</title>
    <parameter name="hotfix" value="off" />
    <r:require module="datatables"/>

<r:style>
td {
    text-align: center;
}

#curVariantTable_filter {
    padding: 5px 35px 0 0;
}
</r:style>

</head>
<body>

<section>
    <div class="container-fluid">
        <div class="row">
            <table id="curVariantTable">
                <thead>
                    <tr>
                        <th>Reviewed</th>
                        <th class="filter">Gene</th>
                        <th class="filter">HGVSG</th>
                        <th class="filter">Context</th>
                        <th class="filter">PM Class</th>
                        <th class="filter">Classifier</th>
                        <th class="filter">Date Created</th>
                    </tr>
                </thead>
                <tbody>
                <g:each in="${curVariants}" var="cv">
                    <tr>
                        <td><input <g:if test="${cv.tags.find{it.label == "Reviewed"}}">checked value="1"</g:if> type="checkbox" class="reviewBox" id="cv-${cv.id}"></td>
                        <td>${cv.gene}</td>
                        <td><a href="<g:context/>/curVariant/show/${cv.id}">${cv.hgvsg}</a></td>
                        <td>${cv.clinContext}</td>
                        <td>${cv.pmClass}</td>
                        <td>${cv.classified}</td>
                        <td>${cv.dateCreated.format("dd MMM")}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-xs-12" style="text-align: center">
                <p>
                <g:if test="${offset}">
                    <span><a href="<g:context/>/curVariant/review?offset=${offset - max}">Previous</a></span>
                </g:if>
                <span>Showing ${offset + 1 }-${curVariants.size() + offset} of ${total}</span>
                <span><a href="<g:context/>/curVariant/review?offset=${offset + max}">Next</a></span>
                </p>
            </div>
        </div>

    </div>
 </section>
<r:script>
var table;
$(function(){
    $('#curVariantTable thead th.filter').each( function () {
        var title = $(this).text();
        d3.select(this).append("input").attrs({
            type: 'text',
            placeholder: 'Filter '+title
        }).on("click", function(d){
            d3.event.preventDefault();
            d3.event.stopPropagation();
        });
    } );

    table = $("#curVariantTable").dataTable({
        "paging": false,
        "order": [6, 'desc'],
        "orderCellsTop": true,
        "info": false
    });

    table.api().columns().every( function () {
        var that = this;

        $( 'input', this.header() ).on( 'keyup change', function () {
            if ( that.search() !== this.value ) {
                that
                .search( this.value )
                .draw();
            }
        } );
    } );

    d3.selectAll(".reviewBox").on("change", function(d){
        var id = d3.select(this).attr("id").split('-')[1];
        $.post({
            url: "<g:context/>/curVariant/toggleReviewed/"+id,
            success: function(d){
                console.log(d);
            }
        });
    });

    d3.select("#curVariantTable_wrapper div.row div.col-sm-6:first-child").append("h1").text("Curated Variant Review").style("padding", "0 0 0 35px");

});
</r:script>


</body>
</html>










