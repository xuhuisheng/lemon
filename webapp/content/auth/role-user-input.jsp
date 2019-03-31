<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#role-viewInputForm").validate({
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
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="roleDefForm" method="post" action="role-user-save.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-1" for="roleDef_name">角色</label>
    <div class="col-sm-5">
      <input type="hidden" name="id" value="${role.id}">
      <p class="form-control-static">${role.name}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="roleDef_descn">账号</label>
    <div class="col-sm-5">
      <textarea id="roleDef_descn" name="text" maxlength="60" rows="10" class="form-control"><c:forEach var="item" items="${role.userStatuses}">${item.username}
</c:forEach></textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5 col-md-offset-1">
      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link"><spring:message code='core.input.back' text='返回'/></button>
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

