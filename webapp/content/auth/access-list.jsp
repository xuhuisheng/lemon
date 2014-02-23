<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>资源列表</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'accessGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_value': '${param.filter_LIKES_value}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'accessGridForm',
	exportUrl: 'access-export.do'
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
		    <a class="btn"><i id="accessSearchIcon" class="icon-chevron-up"></i></a>
          </div>
		</header>
        <div id="accessSearch" class="content content-inner">

		  <form name="accessForm" method="post" action="access-list.do" class="form-inline">
		    <label for="access_name"><spring:message code="auth.access.list.search.value" text="资源"/>:</label>
		    <input type="text" id="access_value" name="filter_LIKES_value" value="${param.filter_LIKES_value}">
			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="access:create">
		  <button class="btn btn-small a-insert" onclick="location.href='access-input.do'"><spring:message code="core.list.create" text="新建"/></button>
		  </region:region-permission>
		  <region:region-permission permission="access:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()"><spring:message code="core.list.delete" text="删除"/></button>
		  </region:region-permission>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()">导出</button>
		  <button class="btn btn-small btn-info a-batch" onclick="location.href='access-batch-list.do'">批量处理</button>
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
          <h4 class="title"><spring:message code="auth.access.list.title" text="资源权限"/></h4>
		</header>
		<div class="content">

  <form id="accessGridForm" name="accessGridForm" method='post' action="access-remove.do" class="m-form-blank">
    <table id="accessGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="auth.access.list.id" text="编号"/></th>
          <th class="sorting" name="type"><spring:message code="auth.access.list.type" text="类型"/></th>
          <th class="sorting" name="value"><spring:message code="auth.access.list.value" text="资源"/></th>
          <th class="sorting" name="perm"><spring:message code="auth.access.list.perm" text="权限"/></th>
          <th class="sorting" name="priority"><spring:message code="auth.access.list.priority" text="排序"/></th>
          <th class="sorting" name="descn"><spring:message code="auth.access.list.descn" text="备注"/></th>
          <th width="50">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.type}</td>
          <td>${item.value}</td>
          <td>${item.perm.name}</td>
          <td>${item.priority}</td>
          <td>${item.descn}</td>
          <td>
			<region:region-permission permission="app:write">
            <a href="access-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>&nbsp;
			</region:region-permission>
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
