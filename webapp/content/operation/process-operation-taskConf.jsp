<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  编辑
		</div>

		<div class="panel-body">

<form id="demoForm" method="post" action="process-operation-confirmStartProcess.do" class="form-horizontal">
  <input id="demo_bpmProcessId" type="hidden" name="bpmProcessId" value="${bpmProcessId}">
  <input id="demo_businessKey" type="hidden" name="businessKey" value="${businessKey}">
  <input id="demo_status" type="hidden" name="status" value="taskConf">
  <c:if test="${humanTaskDefinitions != null}">
  <table class="table table-border">
    <thead>
	  <tr>
	    <td>任务</td>
	    <td>负责人</td>
	  </tr>
	</thead>
	<tbody>
  <c:forEach items="${humanTaskDefinitions}" var="item">
      <tr>
	    <td><input type="hidden" name="taskDefinitionKeys" value="${item.key}">${item.name}</td>
	    <td><input type="text" name="taskAssignees" value="${item.assignee}" class="form-control"></td>
	  </tr>
  </c:forEach>
    </tbody>
  </table>
  </c:if>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" type="submit" class="btn btn-default">保存</button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link">返回</button>
    </div>
  </div>
</form>
        </div>
      </div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
