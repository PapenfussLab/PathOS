
<%@ page import="org.petermac.pathos.curate.SeqVariant" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'seqVariant.label', default: 'SeqVariant')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>

        %{--Javascript Files--}%
        <g:javascript src="quasipartikel/jquery.min.js" />
        <g:javascript src="quasipartikel/jquery-ui.min.js" />
        <g:javascript src="quasipartikel/ui.multiselect.js" />
        <g:javascript src='jquery/jquery.jgrowl.js' plugin='spring-security-ui'/>

        <style type='text/css' media='screen'>
            .annotable tr td { line-height: normal; vertical-align: top; }
            .annotable .fieldcontain { overflow: scroll; }
            .annotable td { word-wrap: break-word }
            .annotable  tr:hover { background: none; }
            .annoShowHideMsg { text-decoration: underline; color: blue; cursor: pointer; }
        </style>
	</head>
	<body>
		<a href="#show-seqVariant" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="allsvlist" action="allsvlist"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-seqVariant" class="content scaffold-show" role="main">
			<h1>Show Annotation For ${hgvsg}</h1>

            <g:set var="counter" value="${1}" />

            <table class="annotable" style="table-layout:fixed;"><tbody>

                <g:each in="${allAnnoLists}" var="annoList">

                    <g:if test="${(counter % 2)}">
                        <tr>
                    </g:if>

                    <td style="width:300px;">
                        <ol class="property-list ">

                            <li class="fieldcontain"><b>
                                <span id="total-label" class="property-label">DataSource</span>
                                <span class="property-value" aria-labelledby="total-label">${annoList['sourcename']} <span class="annoShowHideMsg" id="annoShowHide_${counter}">Show annotation</span></span></b>
                            </li>
                            <div id="details_${counter}" style="display:none;">
                                <g:each in="${annoList}" var="item">
                                    <g:if test="${item.value}">
                                        <li class="fieldcontain">
                                            <span id="total-label" class="property-label"><g:message code="DataSource.${annoList['sourcename']}.${item.key}.label" default="${item.key}"/></span>
                                            <span class="property-value" aria-labelledby="total-label">${item.value}</span>
                                        </li>
                                    </g:if>
                                </g:each>
                            </div>

                        </ol>

                    </td>

                    <g:if test="${!(counter % 2)}">
                        </tr>
                    </g:if>

                    <g:set var="counter" value="${counter + 1}" />

                </g:each>
            </tbody>
            </table>
<r:script>

$( document ).ready(function() {

  <g:each var="j" in="${ (1..counter)}">
            $("#annoShowHide_"+${j}).click(function(){

                $("#details_"+${j}).slideToggle("fast");


                if ($("#annoShowHide_"+${j}).text() == 'Show annotation') {
                    $("#annoShowHide_"+${j}).text('Hide annotation')
                } else {
                    $("#annoShowHide_"+${j}).text('Show annotation')
                }

            });
  </g:each>

 });
 </r:script>

        <%--  <g:form>
              <fieldset class="buttons">
                  <g:hiddenField name="id" value="${seqVariantInstance?.id}" />
                  <g:link class="edit" action="edit" id="${seqVariantInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                  <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
              </fieldset>
          </g:form> --%>
		</div>
	</body>
</html>
