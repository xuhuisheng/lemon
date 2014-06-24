<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-task");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="demo.demo.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>
	<link href="${scopePrefix}/widgets/xform/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${scopePrefix}/widgets/xform/xform-packed.js"></script>
	<script type="text/javascript">
document.onmousedown = function(e) {};
document.onmousemove = function(e) {};
document.onmouseup = function(e) {};
document.ondblclick = function(e) {};

var buttons = [];
<c:forEach items="${formInfo.buttons}" var="item">
buttons.push('${item}');
</c:forEach>

if (buttons.length == 0) {
	buttons = ['保存草稿', '完成任务'];
}

var html = '';

for (var i = 0; i < buttons.length; i++) {
	html += '<button type="button">' + buttons[i] + '</button>';
}

var xform;

$(function() {
	$('#xf-form-button').html(html);

	xform = new xf.Xform('xf-form-table');
	xform.render();

	if ($('#__gef_content__').val() != '') {
		xform.doImport($('#__gef_content__').val());
	}

	xform.setValue(${json});

	$("#demoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	$(document).delegate('#xf-form-button button', 'click', function(e) {
		switch($(this).html()) {
			case '保存草稿':
				$('#xf-form').attr('action', 'form-saveDraft.do');
				$('#xf-form').submit();
				break;
			case '完成任务':
				$('#xf-form').attr('action', 'form-completeTask.do');
				$('#xf-form').submit();
				break;
			case '发起流程':
				$('#xf-form').attr('action', 'form-startProcessInstance.do');
				$('#xf-form').submit();
				break;
			case '驳回':
				$('#xf-form').attr('action', '${scopePrefix}/bpm/workspace-rollback.do');
				$('#xf-form').submit();
				break;
			case '转办':
				$('#modal form').attr('action', '${scopePrefix}/bpm/workspace-doDelegate.do');
				$('#modal').modal();
				break;
			case '协办':
				$('#modal form').attr('action', '${scopePrefix}/bpm/workspace-doDelegateHelp.do');
				$('#modal').modal();
				break;
		}
	});

	setTimeout(function() {
		$('.datepicker').datepicker({
			language: 'zh_CN',
			format: 'yyyy-mm-dd'
		})
	}, 500);
})
    </script>
	
	<link type="text/css" rel="stylesheet" href="${scopePrefix}/widgets/userpicker/userpicker.css">
    <script type="text/javascript" src="${scopePrefix}/widgets/userpicker/userpicker.js"></script>
	<script type="text/javascript">
$(function() {
	createUserPicker({
		modalId: 'userPicker',
		url: '${scopePrefix}/rs/user/search'
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

	  <form id="xf-form" method="post" action="${scopePrefix}/form/form-completeTask.do" class="xf-form">
		<input id="taskId" type="hidden" name="taskId" value="${formInfo.taskId}">
		<input id="businessKey" type="hidden" name="businessKey" value="${dynamicModel.id}">
		<div id="xf-form-table"></div>
		<br>
		<div id="xf-form-button" style="text-align:center;">
		</div>
	  </form>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${formTemplate.content}</textarea>
	</form>

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
