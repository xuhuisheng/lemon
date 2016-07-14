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
    $("#accessForm").validate({
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


<form id="accessForm" method="post" action="access-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="access_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
	<label class="control-label col-md-1" for="access_type"><spring:message code='auth.access.input.type' text='类型'/></label>
    <div class="col-sm-5">
	  <select id="access_type" name="type" class="form-control">
	    <option value="URL" ${model.type == 'URL' ? selected : ''}>URL</option>
	    <option value="METHOD" ${model.type == 'METHOD' ? selected : ''}>METHOD</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="access_value"><spring:message code='auth.access.input.value' text='资源'/></label>
    <div class="col-sm-5">
      <input id="access_value" type="text" name="value" value="${model.value}" size="40" class="form-control required" minlength="1" maxlength="200">
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="access_perm"><spring:message code='auth.access.input.perm' text='权限'/></label>
    <div class="col-sm-5">
	  <select id="access_perm" name="permId" class="form-control">
	    <c:forEach items="${perms}" var="item">
	    <option value="${item.id}" ${model.perm.id==item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
	<label class="control-label col-md-1" for="access_priority"><spring:message code='auth.access.input.priority' text='排序'/></label>
    <div class="col-sm-5">
      <input id="access_priority" type="text" name="priority" value="${model.priority}" size="40" class="form-control required number" minlength="1" maxlength="10">
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

