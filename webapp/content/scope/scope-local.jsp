<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scope");%>
<%pageContext.setAttribute("currentMenu", "scope");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.scope-local.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'scope-localGrid',
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
	gridFormId: 'scope-localGridForm',
	exportUrl: 'scope-local!exportExcel.do'
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
    <%@include file="/header/scope.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/scope.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="scope-localSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="scope-localSearch" class="content content-inner">

		  <form name="scope-localForm" method="post" action="scope-local.do" class="form-inline">
		    <label for="scope-local_name"><spring:message code='scope-local.scope-local.list.search.name' text='名称'/>:</label>
		    <input type="text" id="scope-local_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small a-search" onclick="document.scope-localForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="scope-local:create">
		  <button class="btn btn-small a-insert" onclick="location.href='scope-local!input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="scope-local:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()">导出</button>
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
		  <h4 class="title"><spring:message code="scope-local.scope-local.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="scope-localGridForm" name="scope-localGridForm" method='post' action="scope-local!removeAll.do" class="m-form-blank">
  <table id="scope-localGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="scope-local.scope-local.list.id" text="编号"/></th>
        <th class="sorting" name="name"><spring:message code="scope-local.scope-local.list.name" text="名称"/></th>
        <th class="sorting" name="ngame">global</th>
        <th class="sorting" name="ngame">共享</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <s:iterator value="page.result" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td><a href="${ctx}/${item.scopeGlobal.name}/${item.name}/">${item.name}</a></td>
        <td>${item.scopeGlobal.name}</td>
        <td>${item.shared == 1}</td>
        <td>
          <a class="a-update" href="scope-local!input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      </s:iterator>
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
