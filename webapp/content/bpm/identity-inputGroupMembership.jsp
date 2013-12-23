<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentMenu", "user");%>
<%pageContext.setAttribute("HEADER_MODEL", "bpm-admin");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="identity!saveGroupMembership.do?operationMode=STORE" class="form-horizontal">
  <input type="hidden" name="groupId" value="${group.id}">
  <div class="control-group">
    编号：${group.id} 名称：${group.name} 类型：${group.type}
  </div>
  <div class="control-group">
    <label class="control-label">用户</label>
	<div class="controls">
	  <select name="selectedUserIds" size="5" multiple>
	    <s:iterator value="users" var="item">
	    <option value="${item.id}"
		  <s:iterator value="selectedUserIds" var="userId">${userId == item.id ? 'selected' : ''}</s:iterator>
		>${item.firstName} ${item.lastName}</option>
		</s:iterator>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn">保存</button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn">返回</button>
    </div>
  </div>
</form>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
