<%@ page import="org.petermac.pathos.curate.Amplicon" %>



<div class="fieldcontain ${hasErrors(bean: ampliconInstance, field: 'panel', 'error')} ">
	<label for="panel">
		<g:message code="amplicon.panel.label" default="Panel" />
		
	</label>
	<g:textField name="panel" value="${ampliconInstance?.panel}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ampliconInstance, field: 'amplicon', 'error')} ">
	<label for="amplicon">
		<g:message code="amplicon.amplicon.label" default="Amplicon" />
		
	</label>
	<g:textField name="amplicon" value="${ampliconInstance?.amplicon}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ampliconInstance, field: 'chr', 'error')} ">
	<label for="chr">
		<g:message code="amplicon.chr.label" default="Chr" />
		
	</label>
	<g:textField name="chr" value="${ampliconInstance?.chr}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ampliconInstance, field: 'startpos', 'error')} ">
	<label for="startpos">
		<g:message code="amplicon.startpos.label" default="Startpos" />
		
	</label>
	<g:textField name="startpos" value="${ampliconInstance?.startpos}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ampliconInstance, field: 'endpos', 'error')} ">
	<label for="endpos">
		<g:message code="amplicon.endpos.label" default="Endpos" />
		
	</label>
	<g:textField name="endpos" value="${ampliconInstance?.endpos}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ampliconInstance, field: 'primerlen1', 'error')} ">
	<label for="primerlen1">
		<g:message code="amplicon.primerlen1.label" default="Primerlen1" />
		
	</label>
	<g:textField name="primerlen1" value="${ampliconInstance?.primerlen1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: ampliconInstance, field: 'primerlen2', 'error')} ">
	<label for="primerlen2">
		<g:message code="amplicon.primerlen2.label" default="Primerlen2" />
		
	</label>
	<g:textField name="primerlen2" value="${ampliconInstance?.primerlen2}"/>
</div>

