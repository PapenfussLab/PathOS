<%@ page import="org.petermac.pathos.curate.AuthUser " %>
<!DOCTYPE html>
<html>
       <head>
               <meta name="layout" content="main">
               <%-- <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />  --%>
               %{--<title><g:message code="default.show.label" args="user.username.label" /></title>--}%
<title>${userInstance.displayName} - Show User</title>

       </head>
       <body>
              <%-- <a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
               <div class="nav" role="navigation">
                           <ul>
                                       <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                                       <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                                       <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                               </ul>
                   </div>   --%>
               <div id="show-user" class="content scaffold-show" role="main">
                       <h1>Showing ${userInstance.displayName}<%--<g:message code="default.show.label" args="${userInstance.username}" />--%></h1>
                       <g:if test="${flash.message}">
                           <div class="message" role="status">${flash.message}</div>
                           </g:if>
                       <ol class="property-list user">
    
                                   <g:if test="${userInstance?.username}">
                                       <li class="fieldcontain">
                                                   <span id="username-label" class="property-label"><g:message code="user.username.label" default="Username" /></span>
            
                                                           <span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${userInstance}" field="username"/></span>
            
                                           </li>
                                       </g:if>
    
                                   <g:if test="${userInstance?.displayName}">
                                       <li class="fieldcontain">
                                                   <span id="displayName-label" class="property-label"><g:message code="user.displayName.label" default="Display Name" /></span>
            
                                                           <span class="property-value" aria-labelledby="displayName-label"><g:fieldValue bean="${userInstance}" field="displayName"/></span>
            
                                           </li>
                                       </g:if>
    
                                   <g:if test="${userInstance?.email}">
                                       <li class="fieldcontain">
                                                   <span id="email-label" class="property-label"><g:message code="user.email.label" default="Email" /></span>
            
                                                           <span class="property-value" aria-labelledby="email-label"><a href="mailto:${userInstance.email}"><g:fieldValue bean="${userInstance}" field="email"/></a></span>
            
                                           </li>
                                       </g:if>
    

    

                     <%--  <g:form>
                                   <fieldset class="buttons">
                                           <g:hiddenField name="id" value="${userInstance?.id}" />
                                           <g:link class="edit" action="edit" id="${userInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                                           <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return con
                               </fieldset>
                       </g:form>    --%>
                        <g:if test="${loggedInAdmin}">
                           <li class="fieldcontain">
                           <span class="property-label">&nbsp;</span> <span class="property-value">
                            <g:link action="edit" id="${userInstance.id}">Edit ${fieldValue(bean: userInstance, field: "username")}</g:link>
                           </span>
                           </li>
                        </g:if>
                       </ol>
               </div>
       </body>
</html>
