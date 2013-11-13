<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "acl");%>
<%pageContext.setAttribute("currentMenu", "acl");%>
<!doctype html>
<html lang="zh_CN">

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
    $('#accessForm').attr('action', 'access-batch.do');
    $('#accessForm').submit();
}
    </script>
  </head>

  <body>
    <%@include file="/header/acl.jsp"%>

    <div class="row-fluid">
    <%@include file="/menu/acl.jsp"%>

    <!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="auth.access.batchinput.title" text="批量录入"/></h4>
		</header>

        <div class="content content-inner">

<form id="accessForm" method="post" action="user-connector-batch!save.do" class="form-horizontal">
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
          <s:iterator value="userDtos">
            <tr>
              <td><input type="checkbox" class="selectedItem" name="userIds" value="${id}"></td>
              <td>${id}</td>
              <td>${username}</td>
              <td>${status == 1 ? 'enable' : 'disable'}</td>
            </tr>
          </s:iterator>
        </tbody>
      </table>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="access_role">资源类型</label>
    <div class="controls">
	  <input type="text" name="resourceType" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="access_role">资源ID</label>
    <div class="controls">
	  <input type="text" name="resourceId" value="">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="access_role">掩码</label>
    <div class="controls">
	  <input type="text" name="mask" value="">
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="button" class="btn" onclick="doPrev()"><spring:message code='core.step.prev' text='上一步'/></button>
	  &nbsp;
      <button id="submitButton" class="btn btn-primary"><spring:message code='core.step.next' text='下一步'/></button>
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
