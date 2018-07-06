<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

		<h4>账号详情 - ${accountInfo.username}</h4>

		<ul class="nav nav-tabs">
		  <li role="presentation" class="active"><a href="account-detail-index.do?infoId=${param.infoId}">详情</a></li>
		  <li role="presentation" class=""><a href="account-detail-password.do?infoId=${param.infoId}">密码</a></li>
		  <li role="presentation" class=""><a href="account-detail-avatar.do?infoId=${param.infoId}">头像</a></li>
		  <li role="presentation" class=""><a href="account-detail-log.do?infoId=${param.infoId}">日志</a></li>
		  <li role="presentation" class=""><a href="account-detail-device.do?infoId=${param.infoId}">设备</a></li>
		  <li role="presentation" class=""><a href="account-detail-token.do?infoId=${param.infoId}">会话</a></li>
		  <li role="presentation" class=""><a href="account-detail-person.do?infoId=${param.infoId}">人员</a></li>
		</ul>

      <div class="panel panel-default">

		<div class="panel-body">

<form id="userBaseForm" method="post" action="account-info-save.do" class="form-horizontal">
  <input id="userBase_userRepoId" type="hidden" name="userRepoId" value="1">
  <c:if test="${accountInfo != null}">
  <input id="userBase_id" type="hidden" name="id" value="${accountInfo.id}">
  </c:if>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_username"><spring:message code="user.user.input.username" text="账号"/></label>
	<div class="col-sm-5">
	  <input id="userBase_username" type="text" name="username" value="${accountInfo.username}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>

  <c:if test="${empty accountInfo || empty accountInfo.accountCredentials}">
  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_password"><spring:message code="user.user.input.password" text="密码"/></label>
	<div class="col-sm-5">
	  <input id="userBase_password" type="password" name="password" size="40" class="form-control required" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_confirmPassword"><spring:message code="user.user.input.confirmpassword" text="验证密码"/></label>
	<div class="col-sm-5">
	  <input id="userBase_confirmPassword" type="password" name="confirmPassword" size="40" class="form-control required" maxlength="10" equalTo="#userBase_password">
    </div>
  </div>
  </c:if>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_status"><spring:message code="user.user.input.enabled" text="启用"/></label>
	<div class="col-sm-5">
	  <label class="checkbox checkbox-inline">
	    <input id="userBase_status" type="checkbox" name="status" value="active" ${accountInfo.status == 'active' ? 'checked' : ''} >
	  </label>
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_displayName">显示名</label>
	<div class="col-sm-5">
	  <input id="userBase_displayName" type="text" name="displayName" value="${accountInfo.displayName}" size="40" class="form-control" minlength="2" maxlength="50">
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_type">类型</label>
	<div class="col-sm-5">
	  <input id="userBase_type" type="text" name="type" value="${accountInfo.type}" size="40" class="form-control" minlength="2" maxlength="50">
    </div>
  </div>

  <%--
  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_ref"><spring:message code="user.user.input.ref" text="引用"/></label>
	<div class="col-sm-5">
	  <input id="userBase_ref" type="text" name="ref" value="${accountInfo.ref}" class="form-control">
    </div>
  </div>
  --%>

  <c:if test="${not empty accountInfo}">
  <div class="form-group">
    <label class="control-label col-md-1" for="accountInfo_code">标识</label>
	<div class="col-sm-5">
	  <input id="accountInfo_code" type="text" name="code" value="${accountInfo.code}" class="form-control">
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="accountInfo_status">关闭时间</label>
	<div class="col-sm-5">
	  <div class="input-group datepicker date" style="padding:0;">
	    <input id="accountInfo_closeTime" type="text" name="closeTime" value="<fmt:formatDate value='${accountInfo.closeTime}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default;" class="form-control">
	    <span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>
	  </div>
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="accountInfo_locked">锁定</label>
	<div class="col-sm-5">
	  <label class="checkbox checkbox-inline">
	    <input id="accountInfo_locked" type="checkbox" name="locked" value="locked" ${accountInfo.locked == 'locked' ? 'checked' : ''}>
	  </label>
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="accountInfo_nickName">昵称</label>
	<div class="col-sm-5">
	  <input id="accountInfo_nickName" type="text" name="nickName" value="${accountInfo.nickName}" class="form-control">
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="accountInfo_description">备注</label>
	<div class="col-sm-5">
	  <input id="accountInfo_description" type="text" name="description" value="${accountInfo.description}" class="form-control">
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="accountInfo_language">语言</label>
	<div class="col-sm-5">
	  <input id="accountInfo_language" type="text" name="language" value="${accountInfo.language}" class="form-control">
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="accountInfo_timezone">时区</label>
	<div class="col-sm-5">
	  <input id="accountInfo_timezone" type="text" name="timezone" value="${accountInfo.timezone}" class="form-control">
    </div>
  </div>
  </c:if>

  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn btn-link a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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
