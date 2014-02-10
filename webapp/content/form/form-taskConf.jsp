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
	<script type="text/javascript">
$(function() {
var taskDefinitionId = null;

	$(document).delegate('.userPickerBtn', 'click', function(e) {
		taskDefinitionId = $(this).attr("id");
		$('#userPicker').modal();
		$.ajax({
			url: '${scopePrefix}/rs/user/search',
			data: {
				username: ''
			},
			success: function(data) {
				var html = '';
				for (var i = 0; i < data.length; i++) {
					var item = data[i];
					html +=
					  '<tr>'
						+'<td><input id="selectedItem' + i + '" type="radio" class="selectedItem" name="selectedItem" value="'
						+ item.id + '" title="' + item.displayName + '"></td>'
						+'<td><label for="selectedItem' + i + '">' + item.displayName + '</label></td>'
					  +'</tr>'
				}
				$('#userPickerBody').html(html);
			}
		});
	});

	$(document).delegate('#userPickerBtnSelect', 'click', function(e) {
		$('#_task' + taskDefinitionId).val($('.selectedItem:checked').val());
		$('#_task_name' + taskDefinitionId).val($('.selectedItem:checked').attr('title'));
		$('#userPicker').modal('hide');
	});
});
	</script>
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

<form id="demoForm" method="post" action="form!confirmStartProcess.do?operationMode=STORE" class="form-horizontal">
  <input id="demo_bpmProcessId" type="hidden" name="bpmProcessId" value="${bpmProcessId}">
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
	    <td>
		  <input type="hidden" name="taskAssignees" class="input-medium userPicker" id="_task_${item.key}" value="${item.assigneeExpression}">
		  <input type="text" name="taskAssigneeNames" class="input-medium userPicker" id="_task_name_${item.key}" value="">
		  <span style="padding:2px;" class="add-on"><i id='_${item.key}' style="cursor:pointer;" class="icon-user userPickerBtn"></i></span>
		</td>
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


<div id="userPicker" class="modal hide fade">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>选择用户</h3>
  </div>
  <div class="modal-body">



      <!--
	  <article class="m-blank">
	    <div class="pull-left">
		  <form name="userForm" method="post" action="javascript:void(0);return false;" class="form-inline m-form-bottom">
    	    <label for="user_username">账号:</label>
			<input type="text" id="user_username" name="filter_LIKES_username" value="">
			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>
		  </form>
		</div>
	    <div class="m-clear"></div>
	  </article>
      -->

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">用户列表</h4>
		</header>
		<div class="content">

<form id="userPickerForm" name="userPickerForm" method='post' action="#" class="m-form-blank">
  <table id="userPickerGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check">&nbsp;</th>
        <th>账号</th>
      </tr>
    </thead>

    <tbody id="userPickerBody">

      <tr>
        <td><input id="selectedItem1" type="checkbox" class="selectedItem" name="selectedItem" value="1"></td>
        <td>admin</td>
      </tr>

      <tr>
        <td><input id="selectedItem2" type="checkbox" class="selectedItem" name="selectedItem" value="2"></td>
        <td>user</td>
      </tr>

    </tbody>
  </table>
</form>

        </div>
      </article>



  </div>
  <div class="modal-footer">
    <span id="userPickerResult"></span>
    <a id="userPickerBtnClose" href="#" class="btn">关闭</a>
    <a id="userPickerBtnSelect" href="#" class="btn btn-primary">选择</a>
  </div>
</div>

  </body>

</html>
