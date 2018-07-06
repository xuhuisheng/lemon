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
    $("#sendmail-templateForm").validate({
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


<form id="mailTemplateForm" method="post" action="sendmail-template-send.do" class="form-horizontal">
  <input id="mailTemplate_id" type="hidden" name="id" value="${sendmailTemplate.id}">
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_name">名称</label>
	<div class="col-md-11">
	  <p class="form-control-static">${sendmailTemplate.name}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_sender">发件人</label>
	<div class="col-md-11">
	  <p class="form-control-static"><c:out value="${sendmailTemplate.sender}"/></p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_receiver">收件人</label>
	<div class="col-md-11">
	  <p class="form-control-static"><c:out value="${sendmailTemplate.receiver}"/></p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_cc">抄送</label>
	<div class="col-md-11">
	  <p class="form-control-static"><c:out value="${sendmailTemplate.cc}"/></p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_bcc">暗送</label>
	<div class="col-md-11">
	  <p class="form-control-static"><c:out value="${sendmailTemplate.bcc}"/></p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_subject">标题</label>
	<div class="col-md-11">
	  <p class="form-control-static">${sendmailTemplate.subject}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_content">内容</label>
	<div class="col-md-11">
	  <p class="form-control-static">${sendmailTemplate.content}</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_attachment">附件</label>
	<div class="col-md-11">
      <c:forEach items="${mailTemplate.mailAttachments}" var="item">
		<a href="sendmail-attachment-download.do?id=${item.id}"><i class="badge">${item.name}</i></a>
      </c:forEach>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_mailConfigId">SMTP服务器</label>
	<div class="col-md-4">
	  <select id="mailTemplate_mailConfigId" name="sendmailConfigId" class="form-control">
	    <c:forEach items="${sendmailConfigs}" var="item">
		<option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-11 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit">发送测试邮件</button>
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

