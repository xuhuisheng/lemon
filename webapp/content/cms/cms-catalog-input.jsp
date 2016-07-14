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
    $("#cms-catalogForm").validate({
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


<form id="cmsCatalogForm" method="post" action="cms-catalog-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="cmsCatalog_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_name">名称</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_code">别名</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_code" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_logo">图标</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_logo" type="text" name="logo" value="${model.logo}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_type">类型</label>
	<div class="col-sm-5">
	  <select id="cmsCatalog_type" name="type">
	    <option value="0" ${model.type == 0 ? 'selected' : ''}>文字</option>
	    <option value="1" ${model.type == 1 ? 'selected' : ''}>图片</option>
	    <option value="2" ${model.type == 2 ? 'selected' : ''}>音乐</option>
	    <option value="3" ${model.type == 3 ? 'selected' : ''}>视频</option>
	    <option value="4" ${model.type == 4 ? 'selected' : ''}>文档</option>
	    <option value="5" ${model.type == 5 ? 'selected' : ''}>文档</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_templateIndex">首页模板</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_templateIndex" type="text" name="templateIndex" value="${model.templateIndex}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_templateList">列表页模板</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_templateList" type="text" name="templateList" value="${model.templateList}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_temlateDetail">详情页模板</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_temlateDetail" type="text" name="templateDetail" value="${model.templateDetail}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_keyword">关键字</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_keyword" type="text" name="keyword" value="${model.keyword}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="cmsCatalog_description">描述</label>
	<div class="col-sm-5">
	  <input id="cmsCatalog_description" type="text" name="description" value="${model.description}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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

