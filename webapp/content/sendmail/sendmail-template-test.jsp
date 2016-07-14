<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sendmail");%>
<%pageContext.setAttribute("currentMenu", "sendmail");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>测试</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/sendmail.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/sendmail.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="mailTemplateForm" method="post" action="sendmail-template-send.do" class="form-horizontal">
  <input id="mailTemplate_id" type="hidden" name="id" value="${sendmailTemplate.id}">
  <div class="control-group">
    <label class="control-label" for="mailTemplate_name">名称</label>
	<div class="controls">
	  ${sendmailTemplate.name}
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_sender">发件人</label>
	<div class="controls">
	  <c:out value="${sendmailTemplate.sender}"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_receiver">收件人</label>
	<div class="controls">
	  <c:out value="${sendmailTemplate.receiver}"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_cc">抄送</label>
	<div class="controls">
	  <c:out value="${sendmailTemplate.cc}"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_bcc">暗送</label>
	<div class="controls">
	  <c:out value="${sendmailTemplate.bcc}"/>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_subject">标题</label>
	<div class="controls">
	  ${sendmailTemplate.subject}
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_content">内容</label>
	<div class="controls">
	  ${sendmailTemplate.content}
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_attachment">附件</label>
	<div class="controls">
      <c:forEach items="${mailTemplate.mailAttachments}" var="item">
		<a href="sendmail-attachment-download.do?id=${item.id}"><i class="badge">${item.name}</i></a>
      </c:forEach>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="mailTemplate_mailConfigId">SMTP服务器</label>
	<div class="controls">
	  <select id="mailTemplate_mailConfigId" name="sendmailConfigId">
	    <c:forEach items="${sendmailConfigs}" var="item">
		<option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="submit" class="btn a-submit">发送测试邮件</button>
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
