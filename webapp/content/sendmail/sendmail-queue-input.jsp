<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sendmail");%>
<%pageContext.setAttribute("currentMenu", "sendmail");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#sendmail-queueForm").validate({
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


<form id="mailQueueForm" method="post" action="sendmail-queue-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="mailQueue_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_sender">发件人</label>
	<div class="col-sm-5">
	  <input id="mailQueue_sender" type="text" name="sender" value="${model.sender}" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_receiver">收件人</label>
	<div class="col-sm-5">
	  <textarea id="mailQueue_receiver" name="receiver" maxlength="200">${model.receiver}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_cc">抄送</label>
	<div class="col-sm-5">
	  <textarea id="mailQueue_cc" name="cc" maxlength="200">${model.cc}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_bcc">暗送</label>
	<div class="col-sm-5">
	  <textarea id="mailQueue_bcc" name="bcc" maxlength="200">${model.bcc}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_subject">标题</label>
	<div class="col-sm-5">
	  <input id="mailQueue_subject" type="text" name="subject" value="${model.subject}" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_data">数据</label>
	<div class="col-sm-5">
	  <textarea id="mailQueue_data" name="data" maxlength="65535">${model.data}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_mailConfigId">SMTP服务器</label>
	<div class="col-sm-5">
	  <select id="mailQueue_mailConfigId" name="sendmailConfigId">
	  <c:forEach items="${sendmailConfigs}" var="item">
	    <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailQueue_mailTemplateId">模板</label>
	<div class="col-sm-5">
	  <select id="mailQueue_mailTemplateId" name="sendmailTemplateId">
	  <c:forEach items="${sendmailTemplates}" var="item">
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

