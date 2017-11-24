<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "pim-task");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'pimRemindGrid',
    pageNo: ${page.pageNo}1,
    pageSize: ${page.pageSize}1,
    totalCount: ${page.totalCount}1,
    resultSize: ${page.resultSize}1,
    pageCount: ${page.pageCount}1,
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc}1,
    params: {
        'filter_LIKES_content': '${param.filter_LIKES_content}'
    },
    selectedItemClass: 'selectedItem',
    gridFormId: 'pimRemindGridForm',
    exportUrl: 'pim-remind-export.do'
};

var table;

$(function() {
    table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});

function reopenTask(id) {
	location.href = 'pim-task-reopen.do?id=' + id;
}

function completeTask(id) {
	location.href = 'pim-task-complete.do?id=' + id;
}

function editTask(id) {
	location.href = 'pim-task-input.do?id=' + id;
}

function removeTask(id) {
	if (confirm('是否确认')) {
		location.href = 'pim-task-remove.do?id=' + id;
	}
}
    </script>
  </head>

  <body>
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid">
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-schedule" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-schedule" aria-expanded="true" aria-controls="collapse-body-delegate">
      <h4 class="panel-title">
        <i class="glyphicon glyphicon-list"></i>
        个人任务
      </h4>
    </div>
    <div id="collapse-body-schedule" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="collapse-header-schedule">
      <div class="panel-body">
        <ul class="nav nav-list">
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 最近任务</a></li>
		  <!--
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 所有任务</a></li>
		  -->
        </ul>
      </div>
    </div>
  </div>

  <!--
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-schedule" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-schedule" aria-expanded="true" aria-controls="collapse-body-delegate">
      <h4 class="panel-title">
        <i class="glyphicon glyphicon-list"></i>
        分类
      </h4>
    </div>
    <div id="collapse-body-schedule" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="collapse-header-schedule">
      <div class="panel-body">
        <ul class="nav nav-list">
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 工作</a></li>
          <li><a href="${tenantPrefix}/pim/pim-task-index.do"><i class="glyphicon glyphicon-list"></i> 学习</a></li>
        </ul>
      </div>
    </div>
  </div>
  -->

</div>

      <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div style="margin-bottom: 20px;">

        <form name="pimTaskForm" method="post" action="pim-task-create.do" class="form-inline">
          <input type="text" id="pimTask_name" name="name" value="" class="form-control">
          <button class="btn btn-default a-search" onclick="document.pimTaskForm.submit()">新建</button>&nbsp;
        </form>

      </div>

      <div class="panel panel-default">
        <div class="panel-heading">
          今天
        </div>
        <table class="table table-hover">
          <tbody>
		    <c:forEach items="${todayTasks}" var="item">
            <tr>
              <td width="50">
                &nbsp;
				<c:if test="${item.status == 'active'}">
                <input type="checkbox" name="${item.id}" value="${item.id}" onclick="completeTask(${item.id})">
				</c:if>
				<c:if test="${item.status == 'completed'}">
                <input type="checkbox" name="${item.id}" value="${item.id}" checked onclick="reopenTask(${item.id})">
				</c:if>
              </td>
              <td>
			    <c:if test="${item.status == 'active'}">
                ${item.name}
				<!--
				<span class="text-muted">#favorite</span>
				-->
				</c:if>
			    <c:if test="${item.status == 'completed'}">
				<span class="text-muted">
				<del>
                ${item.name}
				</del>
				</span>
				</c:if>
              </td>
              <td width="90">
			    &nbsp;
				<span class="text-success">今天</span>
				<i class="glyphicon glyphicon-pencil" style="cursor:pointer" onclick="editTask(${item.id})"></i>
				<i class="glyphicon glyphicon-remove" style="cursor:pointer" onclick="removeTask(${item.id})"></i>
			  </td>
            </tr>
			</c:forEach>
          </tbody>
        </table>
      </div>

      <div class="panel panel-default">
        <div class="panel-heading">
          明天
        </div>
        <table class="table table-hover">
          <tbody>
		    <c:forEach items="${tomorrowTasks}" var="item">
            <tr>
              <td width="50">
                &nbsp;
				<c:if test="${item.status == 'active'}">
                <input type="checkbox" name="${item.id}" value="${item.id}" onclick="completeTask(${item.id})">
				</c:if>
				<c:if test="${item.status == 'completed'}">
                <input type="checkbox" name="${item.id}" value="${item.id}" checked onclick="reopenTask(${item.id})">
				</c:if>
              </td>
              <td>
			    <c:if test="${item.status == 'active'}">
                ${item.name}
				<!--
				<span class="text-muted">#favorite</span>
				-->
				</c:if>
			    <c:if test="${item.status == 'completed'}">
				<span class="text-muted">
				<del>
                ${item.name}
				</del>
				</span>
				</c:if>
              </td>
              <td width="60">
			    &nbsp;
				<span class="text-success">&nbsp;</span>
			  </td>
            </tr>
			</c:forEach>
          </tbody>
        </table>
      </div>

      <div class="panel panel-default">
        <div class="panel-heading">
          7天内
        </div>
        <table class="table table-hover">
          <tbody>
		    <c:forEach items="${fiveDayTasks}" var="item">
            <tr>
              <td width="50">
                &nbsp;
				<c:if test="${item.status == 'active'}">
                <input type="checkbox" name="${item.id}" value="${item.id}" onclick="completeTask(${item.id})">
				</c:if>
				<c:if test="${item.status == 'completed'}">
                <input type="checkbox" name="${item.id}" value="${item.id}" checked onclick="reopenTask(${item.id})">
				</c:if>
              </td>
              <td>
			    <c:if test="${item.status == 'active'}">
                ${item.name}
				<!--
				<span class="text-muted">#favorite</span>
				-->
				</c:if>
			    <c:if test="${item.status == 'completed'}">
				<span class="text-muted">
				<del>
                ${item.name}
				</del>
				</span>
				</c:if>
              </td>
              <td width="60">
			    &nbsp;
				<span class="text-success">&nbsp;</span>
			  </td>
            </tr>
			</c:forEach>
          </tbody>
        </table>
      </div>

      </section>
      <!-- end of main -->
    </div>

  </body>

</html>
