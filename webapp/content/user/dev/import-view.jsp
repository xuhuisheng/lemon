<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="用户列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">

    </script>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">



      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  批量导入
		</div>

		<div class="panel-body">

<form id="userBaseForm" method="post" action="import-save.do" class="form-horizontal">

  <div class="form-group">
    <label class="control-label col-md-1" for="text">信息</label>
	<div class="col-sm-6">
	  账号 username {tab} 显示名 displayName {tab} 手机 mobile {tab} 邮件 email
	  <textarea id="text" name="text" class="form-control required" rows="10"></textarea>
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
