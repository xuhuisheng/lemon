<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "audit");%>
<%pageContext.setAttribute("currentMenu", "audit");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'auditBaseGrid',
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
	gridFormId: 'auditBaseGridForm',
	exportUrl: 'audit-base-export.do'
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
    <%@include file="/header/audit.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/audit.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="auditBaseSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="auditBaseSearch" class="content content-inner">

		  <form name="auditBaseForm" method="post" action="audit-base-list.do" class="form-inline">
		    <label for="auditBase_name"><spring:message code='auditBase.auditBase.list.search.name' text='名称'/>:</label>
		    <input type="text" id="auditBase_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small a-search" onclick="document.auditBaseForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="auditBase:create">
		  <button class="btn btn-small a-insert" onclick="location.href='audit-base-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="auditBase:delete">
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
		  <h4 class="title"><spring:message code="auditBase.auditBase.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="auditBaseGridForm" name="auditBaseGridForm" method='post' action="audit-base-remove.do" class="m-form-blank">
  <table id="auditBaseGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="name">用户</th>
        <th class="sorting" name="name">操作</th>
        <th class="sorting" name="name">资源</th>
        <th class="sorting" name="name">应用</th>
        <th class="sorting" name="name">结果</th>
        <th class="sorting" name="name">时间</th>
        <th class="sorting" name="name">客户端IP</th>
        <th class="sorting" name="name">服务端IP</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.user}</td>
        <td>${item.action}</td>
        <td>${item.resourceType}#${item.resourceId}</td>
        <td>${item.application}</td>
        <td>${item.result}</td>
        <td>${item.auditTime}</td>
        <td>${item.client}</td>
        <td>${item.server}</td>
        <td>
          <a href="audit-base-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
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
