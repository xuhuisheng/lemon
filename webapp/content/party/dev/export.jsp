<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.org.input.title" text="编辑用户"/></title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>

    <div class="row-fluid">

<c:forEach items="${partyEntities}" var="item">
<p>
INSERT INTO PARTY_ENTITY(ID,TYPE_ID,NAME,REF,TENANT_ID,LEVEL) VALUES('${item.id}','${item.partyType.id}','${item.name}','${item.ref}','${item.tenantId}','${item.level}');
</p>
</c:forEach>

<c:forEach items="${partyStructs}" var="item">
<p>
INSERT INTO PARTY_STRUCT(ID,PARENT_ENTITY_ID,CHILD_ENTITY_ID,STRUCT_TYPE_ID,PRIORITY,TENANT_ID,PARTY_TIME,LINK,ADMIN) VALUES('${item.id}','${item.parentEntity.id}','${item.childEntity.id}','${item.partyStructType.id}','${item.priority}','${item.tenantId}','${item.partTime}','${item.link}','${item.admin}');
</p>
</c:forEach>


	</div>

  </body>

</html>
