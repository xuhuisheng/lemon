<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.perm.list.title" text="权限列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'permGrid',
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
	gridFormId: 'permGridForm',
	exportUrl: 'perm-export.do'
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
			<a class="btn"><i id="permSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="permSearch" class="content content-inner">

		  <form name="permForm" method="post" action="perm-list.do" class="form-inline">
		    <label for="perm_name"><spring:message code="auth.perm.list.search.name" text="名称"/>:</label>
		    <input type="text" id="perm_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.permForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="perm:create">
		  <button class="btn btn-small a-insert" onclick="location.href='perm-input.do'"><spring:message code="core.list.create" text="新建"/></button>
		  </region:region-permission>
		  <region:region-permission permission="perm:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()"><spring:message code="core.list.delete" text="删除"/></button>
		  </region:region-permission>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()"><spring:message code="core.list.export" text="导出"/></button>
		  <button class="btn btn-small a-batch" onclick="location.href='perm-batch-list.do'">批量</button>
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
          <h4 class="title"><spring:message code="auth.perm.list.title" text="权限列表"/></h4>
		</header>
		<div class="content">

  <form id="permGridForm" name="permGridForm" method='post' action="perm-remove.do" class="m-form-blank">
    <table id="permGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="auth.perm.list.id" text="编号"/></th>
          <th class="sorting" name="id">代码</th>
          <th class="sorting" name="name"><spring:message code="auth.perm.list.name" text="名称"/></th>
          <th class="sorting" name="id">分类</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.code}</td>
          <td>${item.name}</td>
          <td>${item.permType.name}</td>
          <td>
			<region:region-permission permission="perm:write">
            <a href="perm-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
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
