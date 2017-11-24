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
    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/userpicker3-v2/userpicker.css">
    <script type="text/javascript" src="${cdnPrefix}/userpicker3-v2/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		showExpression: true,
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="container">

	<!-- start of main -->
      <section id="m-main" class="col-md-12" style="padding-top:65px;">

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
	    <td>
		    <div class="input-group userPicker" style="width: 175px;">
			  <input id="_task_name_key" type="hidden" name="value" class="input-medium" value="${item.assignee}">
			  <input type="text" name="taskAssignees" style="width: 175px;background-color:white;" value="<tags:user userId='${item.assignee}'/>" class="form-control" readonly>
			  <div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
		    </div>

		</td>
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
