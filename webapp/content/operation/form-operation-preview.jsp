<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "form");%>
<%pageContext.setAttribute("currentMenu", "form");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="demo.demo.input.title" text="编辑"/></title>
    <%@include file="/common/s3.jsp"%>
	<link href="${cdnPrefix}/public/mossle-xform/0.0.11/styles/xform.css" rel="stylesheet">
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-xform/0.0.11/xform-all.js"></script>

    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.css">
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.js"></script>

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

	if ('${xform.jsonData}' != '') {
		xform.setValue(${xform.jsonData});
	}

	$("#demoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

	createUserPicker({
		multiple: true,
		url: '${tenantPrefix}/rs/user/search'
	});

	setTimeout(function() {
		$('.datepicker').datepicker({
			autoclose: true,
			language: 'zh_CN',
			format: 'yyyy-mm-dd'
		})
	}, 500);
})
    </script>
  </head>

  <body>
    <%@include file="/header/form.jsp"%>

    <div class="row-fluid">

	<!-- start of main -->
      <section id="m-main" class="col-md-12" style="padding-top:65px;">

      <form id="xf-form" method="post" action="${tenantPrefix}/operation/form-operation-test.do" class="xf-form" enctype="multipart/form-data">
        <input id="ref" type="hidden" name="ref" value="${record.ref}">
		<div id="xf-form-table"></div>
		<br>
		<div style="text-align:center;">
		</div>
	  </form>

    </section>
	<!-- end of main -->

    <form id="f" action="form-template-save.do" method="post" style="display:none;">
	  <textarea id="__gef_content__" name="content">${xform.content}</textarea>
	</form>

  </body>

</html>
