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

<form id="demoForm" method="post" action="form!startProcessInstance.do?operationMode=STORE" class="form-horizontal">
  <input id="demo_processDefinitionId" type="hidden" name="processDefinitionId" value="${processDefinitionId}">
  <input id="demo_businessKey" type="hidden" name="businessKey" value="${businessKey}">
  <input id="demo_status" type="hidden" name="status" value="taskConf">
  <s:if test="taskDefinitions != null">
  <table class="table table-border">
    <thead>
	  <tr>
	    <td>任务</td>
	    <td>负责人</td>
	  </tr>
	</thead>
	<tbody>
  <s:iterator value="taskDefinitions" var="item">
      <tr>
	    <td><input type="hidden" name="taskDefinitionKeys" value="${item.key}">${item.nameExpression}</td>
	    <td><input type="text" name="taskAssignees" value="${item.assigneeExpression}"></td>
	  </tr>
  </s:iterator>
    </tbody>
  </table>
  </s:if>
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
