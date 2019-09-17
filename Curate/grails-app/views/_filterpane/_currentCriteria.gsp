<%@ page import="org.grails.plugin.filterpane.FilterPaneOperationType" %>
<g:if test="${isFiltered == true}">
  <ul id="${id}" class="${styleClass}" style="${style}" title="${title}">
    <g:each in="${criteria}" var="c">
      <li>
        ${c.fieldName}
        <g:message code="fp.op.${c.filterOp}" default="${c.filterOp}"/>
        <g:if test="${![FilterPaneOperationType.IsNull.operation, FilterPaneOperationType.IsNotNull.operation].contains(c.filterOp)}">
          <g:if test="${quoteValues == true}">
            "${c.filterValue}"
          </g:if>
          <g:else>
            ${c.filterValue}
          </g:else>
        </g:if>
        <g:if test="${'between'.equalsIgnoreCase(c.filterOp)}">
          <g:message code="fp.tag.filterPane.property.betweenValueSeparatorText" default="and"/>
          <g:if test="${quoteValues == true}">
            "${c.filterValueTo}"
          </g:if>
          <g:else>
            ${c.filterValueTo}
          </g:else>
        </g:if>
        <a href="${g.createLink(action: action, params: c.params)}" class="remove">
          <g:if test="${removeImgFile != null}">
            <img src="${g.resource(dir: removeImgDir, file: removeImgFile)}" alt="(X)" title="${g.message(code: 'fp.currentCriteria.removeTitle', default: 'Remove')}"/>
          </g:if>
          <g:else>
            (X)
          </g:else>
        </a>
      </li>
    </g:each>
  </ul>
</g:if>