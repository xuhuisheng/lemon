<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "pim");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.pimScheduler.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'pimSchedulerGrid',
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
	gridFormId: 'pimSchedulerGridForm',
	exportUrl: 'pim-scheduler-export.do'
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
    <%@include file="/header/pim.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="pimSchedulerSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="pimSchedulerSearch" class="content content-inner">

		  <form name="pimSchedulerForm" method="post" action="pim-scheduler-list.do" class="form-inline">
		    <label for="pimScheduler_name"><spring:message code='pimScheduler.pimScheduler.list.search.name' text='名称'/>:</label>
		    <input type="text" id="pimScheduler_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small a-search" onclick="document.pimSchedulerForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="pimScheduler:create">
		  <button class="btn btn-small a-insert" onclick="location.href='pim-scheduler-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="pimScheduler:delete">
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
		  <h4 class="title"><spring:message code="pimScheduler.pimScheduler.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="pimSchedulerGridForm" name="pimSchedulerGridForm" method='post' action="pim-scheduler-remove.do" class="m-form-blank">
  <table id="pimSchedulerGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="pimScheduler.pimScheduler.list.id" text="编号"/></th>
        <th class="sorting" name="name"><spring:message code="pimScheduler.pimScheduler.list.name" text="名称"/></th>
        <th class="sorting" name="name">开始时间</th>
        <th class="sorting" name="name">结束时间</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.startTime}</td>
        <td>${item.endTime}</td>
        <td>
          <a href="pim-scheduler-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
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
