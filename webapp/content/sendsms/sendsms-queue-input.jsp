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
	<link rel="stylesheet" href="${ctx}/s/jquery-file-upload/css/jquery.fileupload.css">
    <script type="text/javascript">
$(function() {
    $("#smsQueueForm").validate({
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

<form id="smsQueueForm" method="post" action="sendsms-queue-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="smsQueue_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsQueue_sender">手机</label>
	<div class="col-sm-5">
	  <input id="smsQueue_sender" type="text" name="mobile" value="${model.mobile}" minlength="11" maxlength="11">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsQueue_data">信息</label>
	<div class="col-sm-5">
	  <textarea id="smsQueue_data" name="message" maxlength="70">${model.message}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsQueue_smsConfigId">短信服务器</label>
	<div class="col-sm-5">
	  <select id="smsQueue_smsConfigId" name="sendsmsConfigId">
	  <c:forEach items="${sendsmsConfigs}" var="item">
	    <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
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

