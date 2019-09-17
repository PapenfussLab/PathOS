<%@ page import="org.joda.time.DateTime" %>
<%@ page import="org.joda.time.Instant" %>
<%@ page import="org.joda.time.LocalTime" %>
<%@ page import="org.joda.time.LocalDate" %>
<%@ page import="org.joda.time.LocalDateTime" %>

<span id="${ctrlAttrs.id}-container" style="${ctrlAttrs.style}">
  <g:if test="${Date.isAssignableFrom(ctrlAttrs.domainProperty.type)}">
    <%=g.datePicker(ctrlAttrs)%>
  </g:if>
  <g:elseif test="${DateTime.isAssignableFrom(ctrlAttrs.domainProperty.type) ||
          Instant.isAssignableFrom(ctrlAttrs.domainProperty.type) ||
          LocalDateTime.isAssignableFrom(ctrlAttrs.domainProperty.type)}">
    <%=joda.dateTimePicker(ctrlAttrs)%>
  </g:elseif>
  <g:elseif test="${LocalTime.isAssignableFrom(ctrlAttrs.domainProperty.type)}">
    <%=joda.timePicker(ctrlAttrs)%>
  </g:elseif>
  <g:elseif test="${LocalDate.isAssignableFrom(ctrlAttrs.domainProperty.type)}">
    <%=joda.datePicker(ctrlAttrs)%>
  </g:elseif>

  <g:if test="${ctrlAttrs.name?.endsWith('To')}">
    <input type="hidden"
           name="filter.${ctrlAttrs.domain}.${ctrlAttrs.propertyName}_isDayPrecision"
           value="${ctrlAttrs.isDayPrecision}"/>
  </g:if>
</span>
