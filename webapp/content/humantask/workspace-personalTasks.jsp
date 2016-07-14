<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "task");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'processGrid',
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
	gridFormId: 'processGridForm',
	exportUrl: 'process-export.do'
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
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/bpm-workspace3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div style="margin-bottom: 20px;">

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
	  
<form id="pimRemindGridForm" name="pimRemindGridForm" method='post' action="pim-note-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  列表
		</div>
  <table id="pimRemindGrid" class="table table-hover">
    <thead>
      <tr>
	    <%--
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
		--%>
		<td>单号</th>
        <th>标题</th>
        <th>到达时间</th>
        <th>流程</th>
        <th>环节</th>
        <th>状态</th>
        <th width="90">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <%--
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
		--%>
		<td>${item.businessKey}</td>
	    <td>${item.presentationSubject}</td>
	    <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td><tags:processName processDefinitionId="${item.processDefinitionId}"/></td>
	    <td>
		  ${item.name}
		</td>
		<td>
		  (${item.catalog})
		</td>
		<%--
	    <td>
		  <tags:user userId="${item.assignee}"/>
		  <c:if test="${not empty item.owner && item.assignee != item.owner}">
		  <b>(原执行人:<tags:user userId="${item.owner}"/>)</b>
		  </c:if>
		</td>
		--%>
        <td>
          <a href="${tenantPrefix}/operation/task-operation-viewTaskForm.do?humanTaskId=${item.id}">处理</a>
		  <%--
		  <c:if test="${delegationState != 'PENDING'}">
          <a href="${tenantPrefix}/bpm/workspace-prepareDelegateTask.do?taskId=${item.taskId}">代理</a>
		  </c:if>
		  <c:if test="${delegationState == 'PENDING'}">
          <a href="${tenantPrefix}/bpm/workspace-resolveTask.do?taskId=${item.taskId}">还回</a>
		  </c:if>
          <a href="${tenantPrefix}/bpm/workspace-rollback.do?taskId=${item.taskId}">回退</a>
		  --%>
          <a href="${tenantPrefix}/bpm/workspace-viewHistory.do?processInstanceId=${item.processInstanceId}">详情</a>
		  <%--
          <a href="${scpoePrefix}/bpm/workspace-changeCounterSign.do?taskId=${item.id}">加减签</a>
		  --%>
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

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
