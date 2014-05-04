<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
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
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="workspace-saveCounterSign.do?operationMode=STORE" class="form-horizontal">
  <input id="demo_id" type="hidden" name="taskId" value="${taskId}">
  <div class="control-group">
    <label class="control-label">分配人</label>
	<div class="controls">
	  <input type="text" name="username" value="">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button name="operationType" type="submit" class="btn" value="add">加签</button>
	  &nbsp;
      <button name="operationType" type="submit" class="btn" value="remove">减签</button>
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
