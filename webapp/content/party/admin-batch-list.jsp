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

var config = {
    id: 'userGrid',
	selectedItemClass: 'selectedItem',
	gridFormId: 'userGridForm'
};

var table;

$(function() {
	table = new Table(config);
});
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

<form id="orgForm" method="post" action="admin-batch-input.do?id=${id}&groupTypeId=${groupTypeId}" class="form-horizontal">
  <div class="control-group">
    <label class="control-label" for="org_orgname">用户</label>
	<div class="controls">
	  <textarea id="org_orgname" type="text" name="text"></textarea>
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

	  <article class="m-blank">
	    <div class="pull-left">
		  <button class="btn btn-small" onclick="table.removeAll()">删除</button>
		</div>

	    <div class="m-clear"></div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">人员列表</h4>
		</header>
		<div class="content">

  <form id="userGridForm" name="orgEntityGridForm" method='post' action="admin-batch-remove.do?id=${id}&groupTypeId=${groupTypeId}" class="m-form-blank">
    <table id="userGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id">编号</th>
          <th class="sorting" name="name">名称</th>
          <th width="50">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${partyEntities}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.name}</td>
          <td>&nbsp;</td>
        </tr>
        </c:forEach>
      </tbody>
    </table>
  </form>

        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
