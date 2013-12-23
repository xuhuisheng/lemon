<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-delegate");%>
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
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="demoForm" method="post" action="delegate!autoDelegate.do?operationMode=STORE" class="form-horizontal">
  <input id="demo_id" type="hidden" name="taskId" value="${taskId}">
  <div class="control-group">
    <label class="control-label">代理人</label>
	<div class="controls">
	  <input type="text" name="attorney" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">开始时间</label>
	<div class="controls">
      <div class="input-append datepicker date" style="padding-left: 0px;">
	    <input type="text" name="startTime" value="" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">结束时间</label>
	<div class="controls">
      <div class="input-append datepicker date" style="padding-left: 0px;">
	    <input type="text" name="endTime" value="" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label">流程定义</label>
	<div class="controls">
	  <select name="processDefinitionId">
	    <option value=""></option>
		<s:iterator value="processDefinitions" var="item">
	    <option value="${item.id}">${item.name}</option>
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
