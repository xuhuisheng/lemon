<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "bpm-delegate");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>

    <link type="text/css" rel="stylesheet" href="${tenantPrefix}/widgets/userpicker3/userpicker.css">
    <script type="text/javascript" src="${tenantPrefix}/widgets/userpicker3/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		url: '${tenantPrefix}/rs/user/search'
	});
})

function refreshTaskDefinitions(processDefinitionId) {
	if (processDefinitionId == '') {
		return;
	}
	
	$.get('${tenantPrefix}/rs/bpm/taskDefinitionKeys', {
		processDefinitionId: processDefinitionId
	}, function(data) {
		$('#taskDefinitionKey').empty();
		var optionDefault = $("<option>").val('').text('');
		$("#taskDefinitionKey").append(optionDefault);
		$.each(data, function(index, item) {
			var option = $('<option>').val(item.id).text(item.name);
			console.info(option);
			$('#taskDefinitionKey').append(option);
		});
	});
}
    </script>
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

<form id="demoForm" method="post" action="delegate-autoDelegate.do" class="form-horizontal">
  <input id="demo_id" type="hidden" name="taskId" value="${taskId}">
  <div class="form-group">
    <label class="control-label col-md-1">代理人</label>
	<div class="col-sm-5">
      <div class="input-group userPicker">
        <input id="_task_name_key" type="hidden" name="attorney" class="input-medium" value="">
        <input type="text" class="form-control" name="username" placeholder="" value="">
        <div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
      </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">开始时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date">
	    <input id="workReportInfo_reportDate" type="text" name="startTime" value="<fmt:formatDate value='${model.startTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">结束时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date">
	    <input id="workReportInfo_reportDate" type="text" name="endTime" value="<fmt:formatDate value='${model.endTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">流程定义</label>
	<div class="col-sm-5">
	  <select name="processDefinitionId" onchange="refreshTaskDefinitions(this.value)" class="form-control col-md-9">
	    <option value=""></option>
		<c:forEach items="${page.result}" var="item">
	    <option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1">任务</label>
	<div class="col-sm-5">
	  <select id="taskDefinitionKey" name="taskDefinitionKey" class="form-control col-md-9">
	    <option value=""></option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
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
