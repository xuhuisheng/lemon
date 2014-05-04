<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-task");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">迁移</h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="console-migrateSave.do" class="form-horizontal">
  <input id="demo_id" type="hidden" name="processInstanceId" value="${processInstanceId}">
  <div class="control-group">
    <label class="control-label">流程定义</label>
	<div class="controls">
	  <select name="processDefinitionId">
	    <c:forEach items="${processDefinitions}" var="item">
	    <option value="${item.id}">${item.id}</option>
		</c:forEach>
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
