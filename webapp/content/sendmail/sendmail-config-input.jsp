<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sendmail");%>
<%pageContext.setAttribute("currentMenu", "sendmail");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#sendmail-configForm").validate({
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
    <%@include file="/header/sendmail.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/sendmail.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="mailConfigForm" method="post" action="sendmail-config-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="mailConfig_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_name">名称</label>
	<div class="col-sm-5">
	  <input id="mailConfig_name" type="text" name="name" value="${model.name}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_host">SMTP服务器</label>
	<div class="col-sm-5">
	  <input id="mailConfig_host" type="text" name="host" value="${model.host}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_port">SMTP端口</label>
	<div class="col-sm-5">
	  <input id="mailConfig_port" type="text" name="port" value="${empty model.port ? '25' : model.port}" class="form-control required number" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_smtpAuth0">需要认证</label>
	<div class="col-sm-5">
	  <label class="pull-left"><input id="mailConfig_smtpAuth0" type="radio" name="smtpAuth" value="1" ${empty model || model.smtpAuth == 1 ? 'checked' : ''}>是</label>
	  <label><input id="mailConfig_smtpAuth1" type="radio" name="smtpAuth" value="0" ${model.smtpAuth == 0 ? 'checked' : ''}>否</label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_smtpStarttls0">使用TLS</label>
	<div class="col-sm-5">
	  <label class="pull-left"><input id="mailConfig_smtpStarttls0" type="radio" name="smtpStarttls" value="1" ${empty model || model.smtpStarttls == 1 ? 'checked' : ''}>是</label>
	  <label><input id="mailConfig_smtpStarttls1" type="radio" name="smtpStarttls" value="0" ${model.smtpStarttls == 0 ? 'checked' : ''}>否</label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_smtpSsl0">使用SSL</label>
	<div class="col-sm-5">
	  <label class="pull-left"><input id="mailConfig_smtpSsl0" type="radio" name="smtpSsl" value="1" ${model.smtpSsl == 1 ? 'checked' : ''}>是</label>
	  <label><input id="mailConfig_smtpSsl1" type="radio" name="smtpSsl" value="0" ${empty model || model.smtpSsl == 0 ? 'checked' : ''}>否</label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_username">账号</label>
	<div class="col-sm-5">
	  <input id="mailConfig_username" type="text" name="username" value="${model.username}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_password">密码</label>
	<div class="col-sm-5">
	  <input id="mailConfig_password" type="password" name="password" value="${model.password}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_defaultFrom">默认发件人</label>
	<div class="col-sm-5">
	  <input id="mailConfig_defaultFrom" type="text" name="defaultFrom" value="${model.defaultFrom}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_status">状态</label>
	<div class="col-sm-5">
	  <label class="pull-left"><input id="mailConfig_status0" type="radio" name="status" value="normal" ${empty model || model.status == 'normal' ? 'checked' : ''}>正常</label>
	  <label class="pull-left"><input id="mailConfig_status1" type="radio" name="status" value="test" ${model.status == 'test' ? 'checked' : ''}>测试</label>
	  <label class="pull-left"><input id="mailConfig_status2" type="radio" name="status" value="skip" ${model.status == 'skip' ? 'checked' : ''}>忽略</label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailConfig_testMail">测试邮件</label>
	<div class="col-sm-5">
	  <input id="mailConfig_testMail" type="text" name="testMail" value="${model.testMail}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
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

