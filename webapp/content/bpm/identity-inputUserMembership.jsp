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

<form id="demoForm" method="post" action="identity!saveUserMembership.do?operationMode=STORE" class="form-horizontal">
  <input type="hidden" name="userId" value="${user.id}">
  <div class="control-group">
    编号：${user.id} 名：${user.firstName} 姓：${user.lastName} 邮箱：${user.email}
  </div>
  <div class="control-group">
    <label class="control-label">群组</label>
	<div class="controls">
	  <select name="selectedGroupIds" size="5" multiple>
	    <s:iterator value="groups" var="item">
	    <option value="${item.id}"
		  <s:iterator value="selectedGroupIds" var="groupId">${groupId == item.id ? 'selected' : ''}</s:iterator>
		>${item.name}</option>
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
