<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
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
	exportUrl: 'account-info-export.do'
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
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

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

		  <form name="userForm" method="post" action="account-info-list.do" class="form-inline">
		    <label for="user_username"><spring:message code='user.user.list.search.username' text='账号'/>:</label>
		    <input type="text" id="user_username" name="filter_LIKES_username" value="${param.filter_LIKES_username}">
		    <label for="user_enabled"><spring:message code='user.user.list.search.status' text='状态'/>:</label>
		    <select id="user_enabled" name="filter_EQI_status" class="input-mini">
			  <option value=""></option>
			  <option value="active" ${param.filter_EQI_status == 'active' ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.true' text='启用'/></option>
			  <option value="disabled" ${param.filter_EQI_status == 'disabled' ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.false' text='禁用'/></option>
		    </select>
			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="user:create">
		  <button class="btn btn-small a-insert" onclick="location.href='account-info-input.do'">新建</button>
		  </region:region-permission>
		  <%--
		  <region:region-permission permission="user:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()">导出</button>
		  --%>
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

<form id="userGridForm" name="userGridForm" method='post' action="account-info-remove.do" class="m-form-blank">
  <table id="userGrid" class="m-table table-hover">
    <thead>
      <tr>
	    <%--
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
		--%>
        <th class="sorting" name="id"><spring:message code="user.user.list.id" text="编号"/></th>
        <th class="sorting" name="username"><spring:message code="user.user.list.username" text="账号"/></th>
        <th class="sorting" name="displayName">显示名</th>
        <th class="sorting" name="status"><spring:message code="user.user.list.status" text="状态"/></th>
        <th class="sorting" name="createTime">创建时间</th>
        <th width="120">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <%--
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
		--%>
        <td>${item.id}</td>
        <td>${item.username}</td>
        <td>${item.displayName}</td>
        <td>
		  <c:if test="${item.status=='active'}">
		    <span style="color:green;">启用</span>(<a href="account-info-disable.do?id=${item.id}">禁用</a>)
		  </c:if>
		  <c:if test="${item.status=='disabled'}">
		    <span style="color:red;">禁用</span>(<a href="account-info-active.do?id=${item.id}">启用</a>)
		  </c:if>
		</td>
        <td>${item.createTime}</td>
        <td>
          <a href="account-info-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
          <a href="account-avatar-input.do?id=${item.id}">头像</a>
          <a href="person-info-account-input.do?code=${item.id}">详细信息</a>
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

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
