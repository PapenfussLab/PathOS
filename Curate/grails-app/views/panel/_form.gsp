<%@ page import="org.petermac.pathos.curate.Panel" %>



<div class="fieldcontain ${hasErrors(bean: panelInstance, field: 'manifest', 'error')} ">
    <label for="manifest">
        <g:message code="panel.manifest.label" default="Manifest"/>

    </label>
    <g:textField name="manifest" value="${panelInstance?.manifest}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: panelInstance, field: 'description', 'error')} ">
    <label for="description">
        <g:message code="panel.description.label" default="Description"/>

    </label>
    <g:textField name="description" value="${panelInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: panelInstance, field: 'panelGroup', 'error')} ">
    <label for="panelGroup">
        <g:message code="panel.panelGroup.label" default="Panel Group"/>

    </label>
    <g:select name="panelGroup" from="${panelInstance.constraints.panelGroup.inList}"
              value="${panelInstance?.panelGroup}" valueMessagePrefix="panel.panelGroup" noSelection="['': '']"/>
</div>

