<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
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
	exportUrl: 'dynamic-model!exportExcel.do'
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
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

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

		  <form name="userForm" method="post" action="form-template.do" class="form-inline">
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="user:create">
		  <button class="btn btn-small" onclick="location.href='form-template!input.do'">新建</button>
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

<form id="dynamicModelGridForm" name="dynamicModelGridForm" method='post' action="form-template!removeAll.do" class="m-form-blank">
  <table id="dynamicModelGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="code">编号</th>
        <th class="sorting" name="category">分类</th>
        <th class="sorting" name="status">状态</th>
        <th class="sorting" name="ref">引用</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <s:iterator value="records" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.code}"></td>
        <td>${item.code}</td>
        <td>${item.category}</td>
        <td>${item.status}</td>
        <td>${item.ref}</td>
        <td>
          <a href="form!viewStartForm.do?businessKey=${item.code}&processDefinitionId=${item.category}">发起流程</a>
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
