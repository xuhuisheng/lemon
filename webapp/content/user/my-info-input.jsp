<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "my");%>
<%pageContext.setAttribute("currentMenu", "my");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>修改信息</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

    $('#myTab a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/my.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/my.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  维护信息
		</div>

		<div class="panel-body">

<form id="pimRemindForm" method="post" action="my-info-save.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_repeatType">账号</label>
	<div class="col-sm-5">
	  <label class="control-label">${accountInfo.username}</label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_repeatType">显示名</label>
	<div class="col-sm-5">
	  <label class="control-label">${accountInfo.displayName}</label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_description">邮箱</label>
	<div class="col-sm-5">
	  <input id="pimInfo_name" type="text" name="email" value="${personInfo.email}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_infoTime">电话</label>
	<div class="col-sm-5">
	  <input id="pimInfo_name" type="text" name="cellphone" value="${personInfo.cellphone}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
