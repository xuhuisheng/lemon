<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "history");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.consoleHistoricTasks.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'consoleHistoricTasksGrid',
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
	gridFormId: 'consoleHistoricTasksGridForm',
	exportUrl: 'consoleHistoricTasks-export.do'
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
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/bpm-console.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">
<%--
<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="consoleHistoricTasksSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="consoleHistoricTasksForm" method="post" action="consoleHistoricTasks-list.do" class="form-inline">
		    <label for="consoleHistoricTasks_name"><spring:message code='consoleHistoricTasks.consoleHistoricTasks.list.search.name' text='名称'/>:</label>
		    <input type="text" id="consoleHistoricTasks_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.consoleHistoricTasksForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>
--%>
      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		<%--
		  <button class="btn btn-default a-insert" onclick="location.href='consoleHistoricTasks-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
		  --%>
		</div>

		<div class="pull-right">
		  每页显示
		  <select class="m-page-size form-control" style="display:inline;width:auto;">
		    <option value="10">10</option>
		    <option value="20">20</option>
		    <option value="50">50</option>
		  </select>
		  条
        </div>

	    <div class="clearfix"></div>
	  </div>

<form id="consoleHistoricTasksGridForm" name="consoleHistoricTasksGridForm" method='post' action="consoleHistoricTasks-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>


  <table id="demoGrid" class="table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="name">流程定义</th>
        <th class="sorting" name="name">开始时间</th>
        <th class="sorting" name="name">结束时间</th>
        <th class="sorting" name="name">分配人</th>
        <th class="sorting" name="name">领取时间</th>
        <th width="170">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <td>${item.id}</td>
	    <td>${item.processDefinitionId}</td>
	    <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	    <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	    <td><tags:user userId="${item.assignee}"/></td>
	    <td><fmt:formatDate value="${item.claimTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		<td>
          <a href="workspace-viewHistory.do?processInstanceId=${item.processInstanceId}">历史</a>
          <a href="${tenantPrefix}/widgets/diagram-viewer/index.html?processInstanceId=${item.id}&processDefinitionId=${item.processDefinitionId}">diagram-viewer</a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>


      </div>
</form>

	  <div>
	    <div class="m-page-info pull-left">
		  共100条记录 显示1到10条记录
		</div>

		<div class="btn-group m-pagination pull-right">
		  <button class="btn btn-default">&lt;</button>
		  <button class="btn btn-default">1</button>
		  <button class="btn btn-default">&gt;</button>
		</div>

	    <div class="clearfix"></div>
      </div>

      <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

