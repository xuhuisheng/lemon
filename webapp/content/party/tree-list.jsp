<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<%@page import="java.util.*"%>
<%@page import="com.mossle.party.domain.*"%>
<%!
	public String generatePartyEntities(List<PartyEntity> partyEntities, long partyStructTypeId) {
		if (partyEntities == null) {
			return "";
		}
		try {
			String text = "<ul>";
			for (PartyEntity partyEntity : partyEntities) {
				text += generatePartyEntity(partyEntity, partyStructTypeId);
			}
			text += "</ul>";
			return text;
		} catch(Exception ex) {
			System.out.println("19 : " + ex);
			// ex.printStackTrace();
			return "";
		}
	}

	public String generatePartyEntity(PartyEntity partyEntity, long partyStructTypeId) {
		try {
			String text = "<li>";
			text += partyEntity.getName();
			List<PartyEntity> partyEntities = new ArrayList<PartyEntity>();
			for (PartyStruct partyStruct : partyEntity.getChildStructs()) {
				if (partyStruct.getPartyStructType().getId() == partyStructTypeId) {
					partyEntities.add(partyStruct.getChildEntity());
				}
			}
			text += generatePartyEntities(partyEntities, partyStructTypeId);
			text += "</li>";
			return text;
		} catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("35 : " + ex);
			return "";
		}
	}
%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.org.list.title" text="组织机构列表"/></title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/party.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/party.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
			<a class="btn"><i id="orgSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="orgSearch" class="content content-inner">

		  <form name="orgForm" method="post" action="tree-list.do" class="form-inline">
			<select name="partyStructTypeId">
			  <c:forEach items="${partyStructTypes}" var="item">
			  <option value="${item.id}" ${param.partyStructTypeId == item.id ? 'selected' : ''}>${item.name}</option>
			  </c:forEach>
			</select>
			<button class="btn"><spring:message code='org.tree.list.view' text='查看'/></button>
		  </form>

		</div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="org.org.list.title" text="组织机构列表"/></h4>
		</header>
		<div class="content content-inner">

<c:set var="partyEntities" value="${partyEntities}"/>
<%
long partyStructTypeId = 0L;
String id = request.getParameter("partyStructTypeId");
try {
	partyStructTypeId = Long.parseLong(id);
} catch(Exception ex) {
}
List<PartyEntity> partyEntities = (List<PartyEntity>) pageContext.getAttribute("partyEntities");
out.print(generatePartyEntities(partyEntities, partyStructTypeId));
%>
        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
