<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="用户列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'userGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_username': '${param.filter_LIKES_username}',
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'userGridForm',
	exportUrl: 'user-status-export.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
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
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="userSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="userSearch" class="content content-inner">

		  <form name="userForm" method="post" action="user-status-list.do" class="form-inline">
		    <label for="user_username"><spring:message code='user.user.list.search.username' text='账号'/>:</label>
		    <input type="text" id="user_username" name="filter_LIKES_username" value="${param.filter_LIKES_username}">
		    <label for="user_enabled"><spring:message code='user.user.list.search.status' text='状态'/>:</label>
		    <select id="user_enabled" name="filter_EQI_status" class="input-mini">
			  <option value=""></option>
			  <option value="1" ${param.filter_EQI_status == 1 ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.true' text='启用'/></option>
			  <option value="0" ${param.filter_EQI_status == 0 ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.false' text='禁用'/></option>
		    </select>
			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <tags:hasPerm value="user:create">
		  <button class="btn btn-small" onclick="location.href='user-status-input.do'">新建</button>
		  </tags:hasPerm>
		  <tags:hasPerm value="user:delete">
		  <button class="btn btn-small" onclick="table.removeAll()">删除</button>
		  </tags:hasPerm>
		  <button class="btn btn-small" onclick="table.exportExcel()">导出</button>
		  <button class="btn btn-small btn-info" onclick="location.href='user-status-batch-list.do'">批量处理</button>
		</div>

		<div class="pull-right">
		  每页显示
		  <select class="m-page-size">
		    <option value="10">10</option>
		    <option value="20">20</option>
		    <option value="50">50</option>
		  </select>
		  条
		</div>

	    <div class="m-clear"></div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.list.title" text="用户列表"/></h4>
		</header>
		<div class="content">

<form id="userGridForm" name="userGridForm" method='post' action="user-status-remove.do" class="m-form-blank">
  <table id="userGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="user.user.list.id" text="编号"/></th>
        <th class="sorting" name="username"><spring:message code="user.user.list.username" text="账号"/></th>
        <th name="password"><spring:message code="user.user.list.password" text="密码"/></th>
        <th class="sorting" name="status"><spring:message code="user.user.list.status" text="状态"/></th>
        <th class="sorting" name="ref"><spring:message code="user.user.list.ref" text="引用"/></th>
        <th name="description"><spring:message code="user.user.list.authorities" text="权限"/></th>
        <th width="150">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.username}</td>
        <td>[protected]</td>
        <td>${item.enabled ? '启用' : '禁用'}</td>
        <td>${item.ref}</td>
        <td>${item.authorities}</td>
        <td>
			<tags:hasPerm value="user:write">
            <a href="user-status-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>&nbsp;
			</tags:hasPerm>
			<tags:hasPerm value="user:auth">
            <a href="user-status-password.do?id=${item.id}"><spring:message code="user.user.list.password" text="设置密码"/></a>
            <a href="javascript:void(0);location.href='user-role-list.do?id=${item.id}'"><spring:message code="user.user.list.role" text="设置权限"/></a>
			</tags:hasPerm>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
</form>

        </div>
      </article>

	  <article>
	    <div class="m-page-info pull-left">
		  共100条记录 显示1到10条记录
		</div>

		<div class="btn-group m-pagination pull-right">
		  <button class="btn btn-small">&lt;</button>
		  <button class="btn btn-small">1</button>
		  <button class="btn btn-small">&gt;</button>
		</div>

	    <div class="m-clear"></div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
