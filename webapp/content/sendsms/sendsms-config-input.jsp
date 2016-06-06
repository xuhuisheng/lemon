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

<form id="smsConfigForm" method="post" action="sendsms-config-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="smsConfig_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_name">名称</label>
	<div class="col-sm-5">
	  <input id="smsConfig_name" type="text" name="name" value="${model.name}" class="required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_host">服务器</label>
	<div class="col-sm-5">
	  <input id="smsConfig_host" type="text" name="host" value="${model.host}" class="required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_username">账号</label>
	<div class="col-sm-5">
	  <input id="smsConfig_username" type="text" name="username" value="${model.username}" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_password">密码</label>
	<div class="col-sm-5">
	  <input id="smsConfig_password" type="password" name="password" value="${model.password}" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_appId">appId</label>
	<div class="col-sm-5">
	  <input id="smsConfig_appId" type="text" name="appId" value="${model.appId}" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_appId">mobile</label>
	<div class="col-sm-5">
	  <input id="smsConfig_mobileFieldName" type="text" name="mobileFieldName" value="${model.mobileFieldName}" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsConfig_appId">message</label>
	<div class="col-sm-5">
	  <input id="smsConfig_messageFieldName" type="text" name="messageFieldName" value="${model.messageFieldName}" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
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

