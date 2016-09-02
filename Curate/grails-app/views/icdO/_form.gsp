<%@ page import="org.petermac.pathos.curate.IcdO" %>



<div class="fieldcontain ${hasErrors(bean: icdOInstance, field: 'histCode', 'error')} ">
	<label for="histCode">
		<g:message code="icdO.histCode.label" default="Hist Code" />
		
	</label>
	<g:textField name="histCode" value="${icdOInstance?.histCode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: icdOInstance, field: 'histDetail', 'error')} ">
	<label for="histDetail">
		<g:message code="icdO.histDetail.label" default="Hist Detail" />
		
	</label>
	<g:textField name="histDetail" value="${icdOInstance?.histDetail}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: icdOInstance, field: 'histDetailCode', 'error')} ">
	<label for="histDetailCode">
		<g:message code="icdO.histDetailCode.label" default="Hist Detail Code" />
		
	</label>
	<g:textField name="histDetailCode" value="${icdOInstance?.histDetailCode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: icdOInstance, field: 'histology', 'error')} ">
	<label for="histology">
		<g:message code="icdO.histology.label" default="Histology" />
		
	</label>
	<g:textField name="histology" value="${icdOInstance?.histology}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: icdOInstance, field: 'site', 'error')} ">
	<label for="site">
		<g:message code="icdO.site.label" default="Site" />
		
	</label>
	<g:textField name="site" value="${icdOInstance?.site}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: icdOInstance, field: 'siteCode', 'error')} ">
	<label for="siteCode">
		<g:message code="icdO.siteCode.label" default="Site Code" />
		
	</label>
	<g:textField name="siteCode" value="${icdOInstance?.siteCode}"/>
</div>

