<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
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

var xform;

$(function() {
	xform = new xf.Xform('xf-form-table');
	xform.render();

	if ($('#__gef_content__').val() != '') {
		xform.doImport($('#__gef_content__').val());
	}

	if ('${json}' != '') {
		xform.setValue(${json});
	}

	$("#demoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	$(document).delegate('#button0', 'click', function(e) {
		$('#xf-form').attr('action', 'form-saveDraft.do');
		$('#xf-form').submit();
	});

	$(document).delegate('#button1', 'click', function(e) {
		$('#xf-form').attr('action', 'form-${nextStep}.do');
		$('#xf-form').submit();
	});

	$(document).delegate('.userPickerBtn', 'click', function(e) {
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
						+'<td><input id="selectedItem' + i + '" type="checkbox" class="selectedItem" name="selectedItem" value="' + item.id + '"></td>'
						+'<td>' + item.displayName + '</td>'
					  +'</tr>'
				}
				$('#userPickerBody').html(html);
			}
		});
	});

	$(document).delegate('#userPickerBtnSelect', 'click', function(e) {
		var selectedItems = $('.selectedItem');
		var value = '';
		for (var i = 0; i < selectedItems.length; i++) {
			var item = selectedItems[i];
			if (item.checked) {
				value += ',' + selectedItems[i].value;
			}
		}
		if (value[0] == ',') {
			value = value.substring(1);
		}
		$('.userPicker').val(value);
		$('#userPicker').modal('hide');
	});

	setTimeout(function() {
		$('.datepicker').datepicker({
			language: 'zh_CN',
			format: 'yyyy-mm-dd'
		})
	}, 500);
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

      <form id="xf-form" method="post" action="${scopePrefix}/form/form-startProcessInstance.do" class="xf-form">
<input id="processDefinitionId" type="hidden" name="processDefinitionId" value="${formInfo.processDefinitionId}">
<input id="bpmProcessId" type="hidden" name="bpmProcessId" value="${bpmProcessId}">
<input id="autoCompleteFirstTask" type="hidden" name="autoCompleteFirstTask" value="${formInfo.autoCompleteFirstTask}">
<input id="businessKey" type="hidden" name="businessKey" value="${businessKey}">
<!--
<input id="taskId" type="hidden" name="taskId" value="${taskId}">
-->
		<div id="xf-form-table"></div>
		<br>
		<div style="text-align:center;">
		  <button id="button0" type="button">保存草稿</button>
		  <button id="button1" type="button">发起流程</button>
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
