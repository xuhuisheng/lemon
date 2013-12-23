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
	<link href="${ctx}/xform/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${ctx}/xform/designer-xform-packed.js"></script>
    <script type="text/javascript" src="${ctx}/xform/container-layout.js"></script>
    <script type="text/javascript" src="${ctx}/xform/adaptor.js"></script>
    <script type="text/javascript">
$(function() {
    $("#demoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	$(document).delegate('#button0', 'click', function(e) {
		$('#xf-form').attr('action', 'workspace!saveDraft.do');
		$('#xf-form').submit();
	});

	$(document).delegate('#button1', 'click', function(e) {
		$('#xf-form').submit();
	});

	setTimeout(function() {
		xform.setValue(${json});

		var id = '#xf-form-table-body-row' + (xform.model.template.positions.length - 1);
		var el = $(id)[0];
		el.parentNode.removeChild(el);
	}, 500);

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
						+'<td><input id="selectedItem' + i + '" type="checkbox" class="selectedItem" name="selectedItem" value="' + item + '"></td>'
						+'<td>' + item + '</td>'
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
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

	  <div id="__gef_container__" style="padding-left:5px;">
		<div id="__gef_canvas__" style="float:left;clear:right;overflow:auto;">
		  <div id="xf-center" class="xf-center" unselectable="on">
			<div id="xf-layer-form" class="xf-layer-form">
			  <form id="xf-form" method="post" action="${scopePrefix}/form/form!startProcessInstance.do?operationMode=STORE" class="xf-form">
<input id="processDefinitionId" type="hidden" name="processDefinitionId" value="${formInfo.processDefinitionId}">
<input id="autoCompleteFirstTask" type="hidden" name="autoCompleteFirstTask" value="${formInfo.autoCompleteFirstTask}">
<input id="businessKey" type="hidden" name="businessKey" value="${param.businessKey}">
<!--
<input id="taskId" type="hidden" name="taskId" value="${taskId}">
-->
				<table id="xf-form-table" class="xf-form-table">
				  <thead id="xf-form-table-head"><tr><th>Title</th></tr></thead>
				  <tbody id="xf-form-table-body"><tr><td>Body</td></tr></tbody>
				  <tfoot id="xf-form-table-foot"><tr><td>Footer</td></tr></tfoot>
				</table>
			  </form>
			</div>
		  </div>
		</div>
	  </div>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template!save.do" method="post" style="display:none;">
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
