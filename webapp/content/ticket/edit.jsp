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

	<script type="text/javascript" src="${ctx}/s/ckeditor/ckeditor.js"></script>
    <script type="text/javascript">
$(function() {
	var editor = CKEDITOR.replace('content');
	editor.config.filebrowserImageUploadUrl = "${tenantPrefix}/cms/cms-article-uploadImage.do";
})
	</script>
  </head>

  <body>
    <%@include file="/header/ticket-admin.jsp"%>

    <div class="row-fluid" style="padding-top:65px;">
	  <form method="post" action="update.do">
	    <input type="hidden" name="id" value="${ticketInfo.id}">

<div class="row-fluid">
  <div class="col-md-3">
    <div class="form-group">
      <label for="type">问题类型</label>
      <input type="text" class="form-control" id="type" placeholder="问题类型">
    </div>
    <div class="form-group">
      <label for="priority">优先级</label>
      <input type="text" class="form-control" id="priority" placeholder="优先级">
    </div>
    <div class="form-group">
      <label for="expireTime">预期解决时间</label>
      <input type="text" class="form-control" id="expireTime" placeholder="预期解决时间">
    </div>
    <div class="form-group">
      <label for="tag">标签</label>
      <input type="text" class="form-control" id="tag" placeholder="标签">
    </div>
  </div>
  <div class="col-md-9">
    <div class="form-group">
      <label for="title">工单标题</label>
      <input type="text" class="form-control" id="title" placeholder="工单标题" name="name" value="${ticketInfo.name}">
    </div>
    <div class="form-group">
      <label for="content">详细描述</label>
      <textarea class="form-control" id="content" placeholder="详细描述" name="content">${ticketInfo.content}</textarea>
    </div>
  </div>
</div>

    <div class="row-fluid">
    <div class="col-md-12 text-center">
      <button class="btn btn-default">提交</button>
	</div>
	</div>


	  </form>
	</div>

  </body>

</html>

