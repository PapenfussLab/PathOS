<label for="${name}.yes">
  <g:message code="fp.tag.filterPane.property.boolean.true" default="Yes"/>
</label>
&nbsp;
<g:radio id="${name}.yes"
         name="${name}"
         value="true"
         checked="${value == 'true'}"
         onClick="grailsFilterPane.selectDefaultOperator('${opName}')"/>
<label for="${name}.no">
  <g:message code="fp.tag.filterPane.property.boolean.false" default="No"/>
</label>
&nbsp;
<g:radio id="${name}.no"
         name="${name}"
         value="false"
         checked="${value == 'false'}"
         onClick="grailsFilterPane.selectDefaultOperator('${opName}')"/>
