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
    $("#menu-statusForm").validate({
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


<form id="menuForm" method="post" action="menu-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="menu_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_title">名称</label>
	<div class="col-sm-5">
	  <input id="menu_title" type="text" name="title" value="${model.title}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_code">编码</label>
	<div class="col-sm-5">
	  <input id="menu_code" type="text" name="code" value="${model.code}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_url">URL</label>
	<div class="col-sm-5">
	  <input id="menu_url" type="text" name="url" value="${model.url}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_type">类型</label>
	<div class="col-sm-5">
	  <input id="menu_type" type="text" name="type" value="${model.type}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_display">状态</label>
	<div class="col-sm-5">
	  <input id="menu_display" type="text" name="display" value="${model.display}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_priority">排序</label>
	<div class="col-sm-5">
	  <input id="menu_priority" type="text" name="priority" value="${model.priority}" size="40" class="form-control required number" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_parentId">上级菜单</label>
	<div class="col-sm-5">
	  <select id="menu_parentId" name="parentId" class="form-control">
	    <option value=""></option>
		<c:forEach items="${menus}" var="item">
		<option value="${item.id}" ${model.menu.id == item.id ? 'selected' : ''}>${item.title}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="menu_permId">权限</label>
	<div class="col-sm-5">
	  <select id="menu_permId" name="permId" class="form-control">
	    <option value=""></option>
		<c:forEach items="${perms}" var="item">
		<option value="${item.id}" ${model.perm.id == item.id ? 'selected' : ''}>${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button id="submitButton" class="btn btn-default"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" onclick="history.back();" class="btn btn-link"><spring:message code='core.input.back' text='返回'/></button>
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

