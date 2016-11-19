<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#permForm").validate({
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
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="permForm" method="post" action="perm-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="perm_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
	<label class="control-label col-md-1" for="perm_code">代码:</label>
    <div class="col-sm-5">
      <input id="perm_code" type="text" name="code" value="${model.code}" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="perm_name">名称:</label>
    <div class="col-sm-5">
      <input id="perm_name" type="text" name="name" value="${model.name}" class="form-control">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="access_perm">权限分类</label>
    <div class="col-sm-5">
	  <select id="access_perm" name="permTypeId" class="form-control">
	    <c:forEach items="${permTypes}" var="item">
	    <option value="${item.id}" ${model.permType.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
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

