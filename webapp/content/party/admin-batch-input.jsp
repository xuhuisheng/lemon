<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.org.input.title" text="编辑用户"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#orgForm").validate({
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
    <%@include file="/header/party.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/party.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="org.org.input.title" text="编辑用户"/></h4>
		</header>

		<div class="content content-inner">

<form id="orgForm" method="post" action="admin-batch-save.do?id=${id}&groupTypeId=${groupTypeId}" class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="org_orgname">用户</label>
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
          <c:forEach items="${userDtos}" var="item">
            <tr>
              <td><input type="checkbox" class="selectedItem" name="userIds" value="${item.id}"></td>
              <td>${id}</td>
              <td>${username}</td>
              <td>${status == 1 ? 'enable' : 'disable'}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
