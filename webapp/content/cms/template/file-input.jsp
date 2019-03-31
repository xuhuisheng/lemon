<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "cms");%>
<%pageContext.setAttribute("currentMenu", "cms");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#cms-articleForm").validate({
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
    <%@include file="/header/cms.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/cms.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="cmsArticleForm" method="post" action="file-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
    <input type="hidden" name="id" value="${model.id}">
  </c:if>
  <input type="hidden" name="catalogId" value="${param.catalogId}">
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">名称</label>
    <div class="col-md-11">
      <input id="cms-article_cmsArticlename" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">内容</label>
    <div class="col-md-11">
      <textarea name="content" class="form-control required" minlength="1" maxlength="65535" rows="20"><c:out value="${model.content}"/></textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-10 col-md-offset-1">
      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn btn-link a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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

