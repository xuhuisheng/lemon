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
		  <li role="presentation" class=""><a href="account-detail-index.do?infoId=${param.infoId}">详情</a></li>
		  <li role="presentation" class=""><a href="account-detail-password.do?infoId=${param.infoId}">密码</a></li>
		  <li role="presentation" class=""><a href="account-detail-avatar.do?infoId=${param.infoId}">头像</a></li>
		  <li role="presentation" class=""><a href="account-detail-log.do?infoId=${param.infoId}">日志</a></li>
		  <li role="presentation" class=""><a href="account-detail-device.do?infoId=${param.infoId}">设备</a></li>
		  <li role="presentation" class=""><a href="account-detail-token.do?infoId=${param.infoId}">会话</a></li>
		  <li role="presentation" class="active"><a href="account-detail-person.do?infoId=${param.infoId}">人员</a></li>
		</ul>

      <div class="panel panel-default">

		<div class="panel-body">

<form id="userBaseForm" method="post" action="person-info-save.do" class="form-horizontal">
  <c:if test="${personInfo != null}">
  <input id="userBase_id" type="hidden" name="id" value="${personInfo.id}">
  </c:if>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_email">邮箱</label>
	<div class="col-sm-5">
	  <input id="userBase_email" type="text" name="email" value="${personInfo.email}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_cellphone">手机</label>
	<div class="col-sm-5">
	  <input id="userBase_cellphone" type="text" name="cellphone" value="${personInfo.cellphone}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>

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
