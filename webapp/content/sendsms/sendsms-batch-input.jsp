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
	<link rel="stylesheet" href="${cdnPrefix}/jquery-file-upload/css/jquery.fileupload.css">
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
    <%@include file="/header/sendsms.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/sendsms.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  批量发送
		</div>

		<div class="panel-body">

<form id="smsQueueForm" method="post" action="sendsms-batch-send.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-1" for="smsQueue_sender">手机</label>
	<div class="col-sm-5">
	  <textarea id="smsQueue_data" name="text" class="form-control"></textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsQueue_data">信息</label>
	<div class="col-sm-5">
	  <textarea id="smsQueue_data" name="message" maxlength="200" class="form-control"></textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="smsQueue_smsConfigId">配置</label>
	<div class="col-sm-5">
	  <select id="smsQueue_smsConfigId" name="sendsmsConfigId" class="form-control">
	  <c:forEach items="${sendsmsConfigs}" var="item">
	    <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
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

