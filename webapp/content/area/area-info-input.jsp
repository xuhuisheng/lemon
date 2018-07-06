<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "area");%>
<%pageContext.setAttribute("currentMenu", "area");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#area-infoForm").validate({
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
    <%@include file="/header/area-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/area-info.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="area-infoForm" method="post" action="area-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="area-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="areaInfo_name">上级区域</label>
	<div class="col-sm-5">
	  <select name="parentId" class="form-control">
	    <option value=""></option>
	  <c:forEach var="item" items="${areaInfos}">
	    <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="areaInfo_name">编码</label>
	<div class="col-sm-5">
	  <input id="areaInfo_name" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="areaInfo_name">名称</label>
	<div class="col-sm-5">
	  <input id="areaInfo_name" type="text" name="name" value="${model.name}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="areaInfo_status">类型</label>
	<div class="col-sm-5">
	  <input id="areaInfo_content" type="text" name="type" value="${model.type}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="areaInfo_status">排序</label>
	<div class="col-sm-5">
	  <input id="areaInfo_content" type="text" name="priority" value="${model.priority}" size="40" class="form-control required number" minlength="1" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="areaInfo_status">状态</label>
	<div class="col-sm-5">
	  <input id="areaInfo_content" type="text" name="status" value="${model.status}" size="40" class="form-control required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="areaInfo_status">备注</label>
	<div class="col-sm-5">
	  <textarea id="areaInfo_descn" name="description" class="form-control" minlength="0" maxlength="10">${model.description}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
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

