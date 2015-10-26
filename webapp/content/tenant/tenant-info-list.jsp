<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "tenant");%>
<%pageContext.setAttribute("currentMenu", "tenant");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.tenant-info.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'tenant-infoGrid',
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
	gridFormId: 'tenant-infoGridForm',
	exportUrl: 'tenant-info-export.do'
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
    <%@include file="/header/tenant.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/tenant.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="tenant-infoSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="tenant-infoSearch" class="content content-inner">

		  <form name="tenant-infoForm" method="post" action="tenant-info-list.do" class="form-inline">
		    <label for="tenant-info_name"><spring:message code='tenant-info.tenant-info.list.search.name' text='名称'/>:</label>
		    <input type="text" id="tenant-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small a-search" onclick="document.tenant-infoForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <button class="btn btn-small a-insert" onclick="location.href='tenant-info-input.do'">新建</button>
		  <button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()">导出</button>
		  <button class="btn btn-small a-insert" onclick="location.href='tenant-info-init-view.do'">创建并初始化租户</button>
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
		  <h4 class="title"><spring:message code="tenant-info.tenant-info.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="tenant-infoGridForm" name="tenant-infoGridForm" method='post' action="tenant-info-remove.do" class="m-form-blank">
  <table id="tenant-infoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="tenant-info.tenant-info.list.id" text="编号"/></th>
        <th class="sorting" name="code">编码</th>
        <th class="sorting" name="name"><spring:message code="tenant-info.tenant-info.list.name" text="名称"/></th>
        <th class="sorting" name="shared">共享</th>
		<!--
        <th class="sorting" name="userRepoCode">登录方式</th>
		-->
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.code}</td>
        <td>${item.name}</td>
        <td>${item.shared}</td>
		<!--
        <td>${item.userRepoCode}</td>
		-->
        <td>
          <a class="a-update" href="tenant-info-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
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
