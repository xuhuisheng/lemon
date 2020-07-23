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


<form id="mailConfigForm" method="post" action="sendmail-app-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="mailConfig_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_name">名称</label>
	<div class="col-md-10">
	  <input id="mailConfig_name" type="text" name="name" value="${model.name}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_host">分组</label>
	<div class="col-md-10">
	  <input id="mailConfig_host" type="text" name="groupName" value="${model.groupName}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_password">配置</label>
	<div class="col-md-10">
	  <input id="mailConfig_password" type="text" name="configCode" value="${model.configCode}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_defaultFrom">流量</label>
	<div class="col-md-10">
	  <input id="mailConfig_defaultFrom" type="text" name="priority" value="${model.priority}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_defaultFrom">账号</label>
	<div class="col-md-10">
	  <input id="mailConfig_defaultFrom" type="text" name="appId" value="${model.appId}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_defaultFrom">密码</label>
	<div class="col-md-10">
	  <input id="mailConfig_defaultFrom" type="text" name="appKey" value="${model.appKey}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_status">状态</label>
	<div class="col-md-10">
	  <label class="radio-inline">
	    <input id="mailConfig_status0" type="radio" name="status" value="active" ${empty model || model.status == 'active' ? 'checked' : ''}>
		启用
	  </label>
	  <label class="radio-inline">
	    <input id="mailConfig_status2" type="radio" name="status" value="disable" ${model.status == 'disable' ? 'checked' : ''}>
		禁用
	  </label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailConfig_defaultFrom">备注</label>
	<div class="col-md-10">
	  <textarea id="mailConfig_defaultFrom" type="text" name="description" maxlength="50" class="form-control">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-10 col-md-offset-2">
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

