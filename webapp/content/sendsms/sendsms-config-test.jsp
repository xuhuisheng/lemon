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
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">测试短信</h4>
		</header>

		<div class="content content-inner">


<form id="smsConfigForm" method="post" action="sms-config-send.do" class="form-horizontal">
  <input id="smsConfig_id" type="hidden" name="id" value="${param.id}">
  <div class="control-group">
    <label class="control-label" for="smsConfig_name">手机</label>
	<div class="controls">
	  <input id="smsConfig_name" type="text" name="mobile" value="" class="required" minlength="11" maxlength="11">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="smsConfig_name">信息</label>
	<div class="controls">
	  <input id="smsConfig_name" type="text" name="message" value="test" class="required" minlength="1" maxlength="70">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
