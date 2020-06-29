<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sendmail");%>
<%pageContext.setAttribute("currentMenu", "sendmail");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>测试</title>
    <%@include file="/common/s3.jsp"%>
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
		  查看
		</div>

		<div class="panel-body">

<form id="mailHistoryForm" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_sender">发件人</label>
	<div class="controls">
	  <p class="form-control-static">
	    <c:out value="${mailHistory.sender}"/>
      </p>
	</div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_receiver">收件人</label>
	<div class="controls">
	  <p class="form-control-static">
	    <c:out value="${mailHistory.receiver}"/>
	  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_cc">抄送</label>
	<div class="controls">
	  <p class="form-control-static">
	    <c:out value="${mailHistory.cc}"/>
	  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_bcc">暗送</label>
	<div class="controls">
	  <p class="form-control-static">
	    <c:out value="${mailHistory.bcc}"/>
	  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_subject">标题</label>
	<div class="controls">
	  <p class="form-control-static">
	    ${mailHistory.subject}
	  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_content">内容</label>
	<div class="controls">
	  <p class="form-control-static">
	    ${mailHistory.content}
	  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_content">数据</label>
	<div class="controls">
    <p class="form-control-static">
	    ${mailHistory.data}
    </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_attachment">附件</label>
	<div class="controls">
    <p class="form-control-static">
      <c:forEach items="${mailHistory.sendmailTemplate.sendmailAttachments}" var="item">
		<a href="sendmail-attachment-download.do?id=${item.id}"><i class="badge">${item.name}</i></a>
      </c:forEach>
    </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_mailTemplateId">模板</label>
	<div class="controls">
    <p class="form-control-static">
	  ${mailHistory.sendmailTemplate.name}
  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_mailConfigId">SMTP服务器</label>
	<div class="controls">
    <p class="form-control-static">
	  ${mailHistory.sendmailConfig.name}
  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_mailConfigId">发送结果</label>
	<div class="controls">
    <p class="form-control-static">
	  ${mailHistory.status}
  </p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-2" for="mailHistory_mailConfigId">结果备注</label>
	<div class="controls">
    <p class="form-control-static">
	  ${mailHistory.info}
  </p>
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

