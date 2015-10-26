<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>菜单列表</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'menuGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_name': '${param.filter_LIKES_name}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'menuGridForm',
	exportUrl: 'menu-export.do'
};

var table;

$(function() {
    table = new Table(config);
    table.configPagination('.m-pagination')
    table.configPageSize('.m-page-size');;
    table.configPageInfo('.m-page-info');
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
			<a class="btn"><i id="menuSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="menuSearch" class="content content-inner">

		  <form name="menuForm" method="post" action="menu-list.do" class="form-inline">
		    <label for="menu_name"><spring:message code="auth.menu.list.search.name" text="名称"/>:</label>
		    <input type="text" id="menu_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.menuForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <button class="btn btn-small a-insert" onclick="location.href='menu-input.do'"><spring:message code="core.list.create" text="新建"/></button>
		  <button class="btn btn-small a-remove" onclick="table.removeAll()"><spring:message code="core.list.delete" text="删除"/></button>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()"><spring:message code="core.list.export" text="导出"/></button>
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
          <h4 class="title">菜单列表</h4>
		</header>
		<div class="content">

  <form id="menuGridForm" name="menuGridForm" method='post' action="menu-remove.do" class="m-form-blank">
    <table id="menuGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="auth.menu.list.id" text="编号"/></th>
          <th class="sorting" name="name"><spring:message code="auth.menu.list.name" text="名称"/></th>
          <th class="sorting" name="name">URL</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.title}</td>
          <td>${item.url}</td>
          <td>
            <a href="menu-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
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
