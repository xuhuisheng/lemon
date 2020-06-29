<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "card");%>
<%pageContext.setAttribute("currentMenu", "card");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#card-avatarForm").validate({
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
    <%@include file="/header/card-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/card-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="card-avatarForm" method="post" action="card-avatar-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="card-avatar_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="card-avatar_address">类型</label>
	<div class="col-sm-5">
	  <input id="card-avatar_address" type="text" name="type" value="${model.type}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="card-avatar_address">卡号</label>
	<div class="col-sm-5">
	  <input id="card-avatar_address" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="card-avatar_address">用户</label>
	<div class="col-sm-5">
	  <input id="card-avatar_address" type="text" name="userId" value="${model.userId}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="card-avatar_address">状态</label>
	<div class="col-sm-5">
	  <input id="card-avatar_address" type="text" name="status" value="${model.status}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="card-avatar_description">备注</label>
	<div class="col-sm-5">
	  <textarea id="card-avatar_description" name="description" class="form-control">${model.description}</textarea>
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

