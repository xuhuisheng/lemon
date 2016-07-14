<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "history");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.consoleHistoricProcessInstances.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'consoleHistoricProcessInstancesGrid',
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
	gridFormId: 'consoleHistoricProcessInstancesGridForm',
	exportUrl: 'consoleHistoricProcessInstances-export.do'
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
	  <a class="btn btn-default btn-xs"><i id="consoleHistoricProcessInstancesSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="consoleHistoricProcessInstancesForm" method="post" action="consoleHistoricProcessInstances-list.do" class="form-inline">
		    <label for="consoleHistoricProcessInstances_name"><spring:message code='consoleHistoricProcessInstances.consoleHistoricProcessInstances.list.search.name' text='名称'/>:</label>
		    <input type="text" id="consoleHistoricProcessInstances_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.consoleHistoricProcessInstancesForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>
--%>
      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		<%--
		  <button class="btn btn-default a-insert" onclick="location.href='consoleHistoricProcessInstances-input.do'">新建</button>
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

<form id="consoleHistoricProcessInstancesGridForm" name="consoleHistoricProcessInstancesGridForm" method='post' action="consoleHistoricProcessInstances-remove.do" class="m-form-blank">
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
        <th class="sorting" name="name">发起人</th>
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
	    <td><tags:user userId="${item.startUserId}"/></td>
        <td>
          <a href="console-reopen.do?processInstanceId=${item.id}">重启</a>
          <a href="workspace-viewHistory.do?processInstanceId=${item.id}">历史</a>
          <a href="${tenantPrefix}/widgets/diagram-viewer/index.html?processInstanceId=${item.id}&processDefinitionId=${item.processDefinitionId}">diagram-viewer</a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
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

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
