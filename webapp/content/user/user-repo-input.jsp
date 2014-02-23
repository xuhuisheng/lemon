<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user-sys");%>
<%pageContext.setAttribute("currentMenu", "user-sys");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.userRepo.input.title" text="编辑用户库"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userRepoForm").validate({
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
    <%@include file="/header/user-sys.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/user-sys.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="auth.userRepo.input.title" text="编辑用户库"/></h4>
		</header>
		<div class="content content-inner">

<form id="userRepoForm" method="post" action="user-repo-save.do?userRepoationMode=STORE" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="userRepo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
	<label class="control-label" for="user-repo_code"><spring:message code='auth.userRepo.input.code' text='代码'/></label>
    <div class="controls">
      <input id="user-repo_code" type="text" name="code" value="${model.code}" size="40" class="text required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-repo_name"><spring:message code='auth.userRepo.input.name' text='名称'/></label>
    <div class="controls">
      <input id="user-repo_name" type="text" name="name" value="${model.name}" size="40" class="text required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="control-group">
	<label class="control-label" for="user-repo_ref">引用</label>
    <div class="controls">
      <input id="user-repo_ref" type="text" name="ref" value="${model.ref}" size="40" class="text">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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
