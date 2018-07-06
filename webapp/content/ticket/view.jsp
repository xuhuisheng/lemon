<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "ticket");%>
<%pageContext.setAttribute("currentMenu", "ticket");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.ticket-group.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>

	<link href="${ctx}/s/bootstrap-step/css/bs-is-fun.css" rel="stylesheet" />
	<link href="${ctx}/s/layim.css" rel="stylesheet" />

	<style>
.ticket .active, 
.ticket .active a {
	color: white;
}
	</style>

	<script>
function sendMessage() {
	// var message = $('#commentMessage').val();
	var message = CKEDITOR.instances.commentMessage.getData();
	location.href = 'sendMessage.do?id=${param.id}&message=' + message;
}
	</script>

	<script type="text/javascript" src="${ctx}/s/ckeditor/ckeditor.js"></script>
    <script type="text/javascript">
$(function() {
	var editor = CKEDITOR.replace('commentMessage');
	editor.config.filebrowserImageUploadUrl = "${tenantPrefix}/cms/cms-article-uploadImage.do";
})
	</script>
  </head>

  <body>
    <%@include file="/header/ticket-admin.jsp"%>

    <div class="row-fluid" style="padding-top:65px;">

	  <!-- start of main -->
      <section id="m-main" class="col-md-8 col-md-offset-2">
	    <c:if test="${not empty param.id}">

		<ul class="nav nav-pills nav-justified step step-arrow" style="margin-top:10px;">
			<li class="${ticketInfo.status=='new' || ticketInfo.status=='open' || ticketInfo.status=='pending' || ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>新发起</a>
			</li>
			<li class="${ticketInfo.status=='open' || ticketInfo.status=='pending' || ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>处理中</a>
			</li>
			<li class="${ticketInfo.status=='pending' || ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>等待回复</a>
			</li>
			<li class="${ticketInfo.status=='resolved' || ticketInfo.status=='closed' ? 'active' : ''}">
				<a>已处理</a>
			</li>
			<li class="${ticketInfo.status=='closed' ? 'active' : ''}">
				<a>已关闭</a>
			</li>
		</ul>

		<div style="background-color:#eee;padding:10px;margin-bottom: 20px;">
		  <h3 style="margin-top:0px;">${ticketInfo.name}</h3>

		  <p>客户: <tags:user userId="${ticketInfo.creator}"/> 问题类型:  优先级:  状态: ${ticketInfo.status} </p>

		  <p>请求时间: <fmt:formatDate value="${ticketInfo.createTime}" type="both"/> </p>

		  <p>更新时间:  受理人: <tags:user userId="${ticketInfo.assignee}"/> </p>

		  <p>${ticketInfo.content}</p>

          <c:if test="${not empty ticketInfo.ticketAttachments}">
		  <p>附件: </p>
          <ul>
		    <c:forEach var="item" items="${ticketInfo.ticketAttachments}">
		    <li><a href="download.do?key=${item.code}">${item.name}</a></li>
			</c:forEach>
		  </ul>
		  </c:if>
		</div>

        <c:if test="${ticketInfo.status != 'closed'}">
		<div>
		  <textarea class="form-control" name="message" id="commentMessage"></textarea>
		  <div style="margin-top:10px;" class="pull-right">
			<button class="btn btn-primary" onclick="sendMessage()">发送</button>
		  </div>
		  <div style="clear:both;"></div>
		</div>
		</c:if>

<ul class="layim_chatview layim_chatthis" id="layim_areaone100001">
  <c:forEach var="item" items="${ticketComments}">
  <li class="${item.type == 'send' ? 'layim_chateme' : ''}">
    <div class="layim_chatuser">
	  <c:if test="${item.type == 'send'}">
      <span class="layim_chattime"><fmt:formatDate value="${item.createTime}" type="both"/></span>
	  <span class="layim_chatname"><tags:user userId="${item.creator}"/></span>
	  <img src="${ctx}/s/logo16.png">
	  </c:if>
	  <c:if test="${item.type != 'send'}">
	  <img src="${ctx}/s/logo16.png">
	  <span class="layim_chatname"><tags:user userId="${item.creator}"/></span>
      <span class="layim_chattime"><fmt:formatDate value="${item.createTime}" type="both"/></span>
	  </c:if>
	</div>
	<div class="layim_chatsay">${item.content}
      <em class="layim_zero"></em>
	</div>
  </li>
  </c:forEach>
</ul>

        </c:if>
      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

