<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sign");%>
<%pageContext.setAttribute("currentMenu", "sign");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#sign-infoForm").validate({
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
    <%@include file="/header/sign-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/sign-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="sign-infoForm" method="post" action="sign-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="sign-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="signInfo_userId">用户</label>
	<div class="col-sm-5">
	  <input id="signInfo_userId" type="text" name="userId" value="${model.userId}" size="40" class="form-control required" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="signInfo_catalog">类别</label>
	<div class="col-sm-5">
	  <input id="signInfo_catalog" type="text" name="catalog" value="${model.catalog}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="signInfo_type">签到方式</label>
	<div class="col-sm-5">
	  <input id="signInfo_type" type="text" name="type" value="${model.type}" size="40" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="signInfo_ref">引用</label>
	<div class="col-sm-5">
	  <input id="signInfo_ref" type="text" name="ref" value="${model.ref}" size="40" class="text">
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

