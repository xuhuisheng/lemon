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
			  <form id="xf-form" method="post" action="${scopePrefix}/form/form!completeTask.do?operationMode=STORE" class="xf-form">
<input id="taskId" type="hidden" name="taskId" value="${formInfo.taskId}">
<input id="businessKey" type="hidden" name="businessKey" value="${dynamicModel.id}">
<!--
<input id="processDefinitionId" type="hidden" name="processDefinitionId" value="${processDefinitionId}">
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
  </body>

</html>
