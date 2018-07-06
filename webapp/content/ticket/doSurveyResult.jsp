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

	    <div class="panel panel-default">
		  <div class="panel-heading">
		    感谢您的建议
		  </div>
		  <div class="panel-body">
		  </div>
		</div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

