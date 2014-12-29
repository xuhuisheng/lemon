<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "store");%>
<%pageContext.setAttribute("currentMenu", "store");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#storeInfoForm").validate({
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
    <%@include file="/header/store.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/store.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="storeInfoForm" method="post" action="store-info-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="storeInfo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="storeInfo_name">名称</label>
	<div class="controls">
	  <input id="storeInfo_name" type="text" name="name" value="${model.name}" class="required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="storeInfo_host">服务器</label>
	<div class="controls">
	  <input id="storeInfo_host" type="text" name="host" value="${model.host}" class="required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="storeInfo_username">账号</label>
	<div class="controls">
	  <input id="storeInfo_username" type="text" name="username" value="${model.username}" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="storeInfo_password">密码</label>
	<div class="controls">
	  <input id="storeInfo_password" type="password" name="password" value="${model.password}" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="storeInfo_appId">appId</label>
	<div class="controls">
	  <input id="storeInfo_appId" type="text" name="appId" value="${model.appId}" maxlength="50">
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
