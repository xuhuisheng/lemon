<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sendsms");%>
<%pageContext.setAttribute("currentMenu", "sendsms");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#smsConfigForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/sendsms.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/sendsms.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  测试
		</div>

		<div class="panel-body">

<form id="smsConfigForm" method="post" action="sendsms-config-send.do" class="form-horizontal">
  <input id="smsConfig_id" type="hidden" name="id" value="${param.id}">
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_name">手机</label>
	<div class="col-sm-5">
	  <input id="smsConfig_name" type="text" name="mobile" value="" class="form-control required" minlength="11" maxlength="11">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_name">信息</label>
	<div class="col-sm-5">
	  <input id="smsConfig_name" type="text" name="message" value="test" class="form-control required" minlength="1" maxlength="70">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>

