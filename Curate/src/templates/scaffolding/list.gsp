<% import grails.persistence.Event %>
<%=packageName%>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require module="filterpane" />
</head>
<body>
<a href="#list-${domainClass.propertyName}" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="\${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
    </ul>
</div>
<div id="list-${domainClass.propertyName}" class="content scaffold-list" role="main" style="white-space: nowrap; overflow-x:auto">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="\${flash.message}">
        <div class="message" role="status">\${flash.message}</div>
    </g:if>
    <filterpane:currentCriteria domainBean="${className}"
                                removeImgDir="images" removeImgFile="bullet_delete.png" fullAssociationPathFieldNames="no" />

    <table>
        <thead>
        <tr>
            <%  excludedProps = Event.allEvents.toList() << 'id' << 'version'
            allowedNames = domainClass.persistentProperties*.name << 'dateCreated' << 'lastUpdated'
            props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && it.type != null && !Collection.isAssignableFrom(it.type) }
            Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
            props.eachWithIndex { p, i ->
                if (i < 100) {
                    if (p.isAssociation()) { %>
            <th><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}" /></th>
            <%      } else { %>
            <g:sortableColumn property="${p.name}" title="\${message(code: '${domainClass.propertyName}.${p.name}.label', default: '${p.naturalName}')}"  params="\${filterParams}"/>
            <%  }   }   } %>
        </tr>
        </thead>
        <tbody>
        <g:each in="\${${domainClass.propertyName}List}" status="i" var="${propertyName}">
            <tr class="\${(i % 2) == 0 ? 'even' : 'odd'}">
                <%  props.eachWithIndex { p, i ->
                    if (i == 0) { %>
                <td><g:link action="show" id="\${${propertyName}.id}">\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</g:link></td>
                <%      } else if (i < 100) {
                    if (p.type == Boolean || p.type == boolean) { %>
                <td><g:formatBoolean boolean="\${${propertyName}.${p.name}}" /></td>
                <%          } else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) { %>
                <td><g:formatDate date="\${${propertyName}.${p.name}}"  format="dd-MMM-yyyy" /></td>
                <%          } else if (p.naturalName.endsWith(' URL')) { %>
                <td>
                    <g:if test="\${fieldValue(bean: ${propertyName}, field: "${p.name}")}">
                        <a href="\${fieldValue(bean: ${propertyName}, field: "${p.name}")}" target="_blank">${p.naturalName.replaceAll(' URL','')}</a>
                    </g:if>
            </td>
                <%          } else { %>
                <td>\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</td>
                <%  }   }   } %>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <filterpane:paginate total="\${${domainClass.propertyName}Count}" domainBean="${className}"/>
        <filterpane:filterButton text="Filter" appliedText="Change Filter"/>
        <filterpane:isNotFiltered>No filter</filterpane:isNotFiltered>
        <filterpane:isFiltered>Filtered!</filterpane:isFiltered>
    </div>
    <filterpane:filterPane domain="${className}" dialog="y"/>
</div>
</body>
</html>
