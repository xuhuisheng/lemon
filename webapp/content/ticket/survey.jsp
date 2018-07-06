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
	  <form method="post" action="doSurvey.do">
	  <input type="hidden" name="id" value="${param.id}">

	    <div class="panel panel-default">
		  <div class="panel-heading">
		    您对本次工单处理的满意度如何
		  </div>
		  <div class="panel-body">
			<div class="form-group">
		    <label class="checkbox">
			  <input type="radio" name="survey" value="非常满意">
			  非常满意
			</label>
		    <label class="checkbox">
			  <input type="radio" name="survey" value="满意">
			  满意
			</label>
		    <label class="checkbox">
			  <input type="radio" name="survey" value="不满意">
			  不满意
			</label>
			</div>
		  </div>
		</div>

	    <div class="panel panel-default">
		  <div class="panel-heading">
		    您对本次工单处理有什么建议？
		  </div>
		  <div class="panel-body">
			<div class="form-group">
			  <textarea name="surveyMessage" class="form-control"></textarea>
			</div>
		  </div>
		</div>

		<div class="text-center">
		  <button class="btn btn-default">提交</button>
		</div>

      </form>
      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

