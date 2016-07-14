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


<form id="cmsArticleForm" method="post" action="cms-article-save.do" class="form-horizontal" enctype="multipart/form-data">
  <c:if test="${model != null}">
  <input id="cms-article_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">栏目</label>
	<div class="col-sm-5">
      <select id="perm_resc" name="cmsCatalogId" class="form-control">
	    <c:forEach items="${cmsCatalogs}" var="item">
	    <option value="${item.id}" ${model.cmsCatalog.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">标题</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="title" value="${model.title}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
<!--
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">短标题</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="shortTitle" value="${model.shortTitle}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">副标题</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="subTitle" value="${model.subTitle}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
-->
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsArticle_summary">摘要</label>
	<div class="col-sm-5">
	  <textarea id="cmsArticle_summary" name="summary" maxlength="200" class="form-control">${model.summary}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">内容</label>
	<div class="col-sm-5">
	  <textarea id="cmsArticle_content" name="content" class="form-control required" minlength="2" maxlength="50">${model.content}</textarea>
    </div>
  </div>
<!--
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">图标</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="logo" value="${model.logo}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">关键字</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="keyword" value="${model.keyword}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">标签</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="tags" value="${model.tags}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">来源</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="source" value="${model.source}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">允许评论</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="checkbox" name="allowComment" value="1" class="text" ${model.allowComment == 1 ? 'checked' : ''}>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">状态</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="checkbox" name="status" value="1" class="text" ${model.status == 1 ? 'checked' : ''}>
    </div>
  </div>
-->
<!--
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">创建时间</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="createTime" value="${model.createTime}" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">发布时间</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="publishTime" value="${model.publishTime}" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">关闭时间</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="closeTime" value="${model.closeTime}" class="text">
    </div>
  </div>
-->
<!--
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">类型</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="type" value="${model.type}" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">置顶</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="top" value="${model.top}" class="text">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cms-article_cmsArticlename">权重</label>
	<div class="col-sm-5">
	  <input id="cms-article_cmsArticlename" type="text" name="weight" value="${model.weight}" class="text">
    </div>
  </div>
-->

  <div class="form-group">
    <label class="control-label col-md-1" for="cmsArticle_file">附件</label>
	<div class="col-sm-5">
	  <input id="cmsArticle_file" type="file" name="file" value="" class="">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
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

