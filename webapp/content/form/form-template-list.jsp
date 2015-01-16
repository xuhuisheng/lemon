<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "form");%>
<%pageContext.setAttribute("currentMenu", "form");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>表单列表</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'dynamicModelGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'dynamicModelGridForm',
	exportUrl: 'form-template-export.do'
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
    <%@include file="/header/form.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/form.jsp"%>

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

		  <form name="userForm" method="post" action="form-template-list.do" class="form-inline">
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="user:create">
		  <button class="btn btn-small" onclick="location.href='form-template-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="user:delete">
		  <button class="btn btn-small" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
		  <button class="btn btn-small" onclick="table.exportExcel()">导出</button>
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
		  <h4 class="title">表单列表</h4>
		</header>
		<div class="content">

<form id="dynamicModelGridForm" name="dynamicModelGridForm" method='post' action="form-template-remove.do" class="m-form-blank">
  <table id="dynamicModelGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="user.user.list.id" text="编号"/></th>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="code">标识</th>
        <th class="sorting" name="type">类型</th>
        <th width="100">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.code}</td>
        <td>${item.type == '1' ? '外部' : '内部'}</td>
        <td>
          <a href="form-template-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
		  <a href="form-template-copy.do?id=${item.id}">复制</a>
		  <a href="${scopePrefix}/operation/form-operation-preview.do?code=${item.code}">预览</a>
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
