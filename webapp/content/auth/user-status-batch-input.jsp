<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.access.batchinput.title" text="批量录入"/></title>
    <%@include file="/common/s.jsp"%>
    <style type="text/css">
.dragClass {
    background-color: #EEEEEE;
}
    </style>
    <script type="text/javascript" src="${ctx}/jquery.tablednd.0.7.min.js"></script>
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
});

function doPrev() {
    $('#accessForm').attr('method', 'get');
    $('#accessForm').attr('action', 'user-status-batch-list.do');
    $('#accessForm').submit();
}
    </script>
  </head>

  <body>
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
    <%@include file="/menu/auth.jsp"%>

    <!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="auth.access.batchinput.title" text="批量录入"/></h4>
		</header>

        <div class="content content-inner">

<form id="accessForm" method="post" action="user-status-batch-save.do" class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="access_perm">用户</label>
    <div class="controls">
      <table class="table table-striped table-bordered table-hover" style="width:auto;">
        <thead>
		  <tr>
			<th width="10" style="text-indent:0px;text-align:center;"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
			<th class="sorting" name="id"><spring:message code="user.user.list.id" text="编号"/></th>
			<th class="sorting" name="username"><spring:message code="user.user.list.username" text="账号"/></th>
			<th class="sorting" name="status"><spring:message code="user.user.list.status" text="状态"/></th>
		  </tr>
        </thead>
        <tbody>
          <c:forEach items="${userStatuses}" var="item">
            <tr>
              <td><input type="checkbox" class="selectedItem" name="userIds" value="${id}"></td>
              <td>${item.id}</td>
              <td>${item.username}</td>
              <td>${item.status == 1 ? '启用' : '急用'}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="access_role">角色</label>
    <div class="controls">
	  <select name="roleIds" size="10" multiple>
	    <c:forEach items="${roles}" var="item">
		  <option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="button" class="btn" onclick="doPrev()"><spring:message code='core.step.prev' text='上一步'/></button>
	  &nbsp;
      <button id="submitButton" class="btn btn-primary"><spring:message code='core.step.next' text='下一步'/></button>
    </div>
  </div>
  <textarea name="userText" style="display:none;">${userText}</textarea>
</form>
        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
    <!-- end of main -->
	</div>

  </body>

</html>
