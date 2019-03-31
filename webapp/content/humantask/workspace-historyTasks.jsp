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

    <link type="text/css" rel="stylesheet" href="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.css">
    <script type="text/javascript" src="${cdnPrefix}/public/mossle-userpicker/3.0/userpicker.js"></script>
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

	createUserPicker({
		modalId: 'userPicker',
		searchUrl: '${tenantPrefix}/rs/user/search',
		treeUrl: '${tenantPrefix}/party/rs/tree-data?type=struct',
		childUrl: '${tenantPrefix}/party/rs/search-user'
	});
});

var ROOT_URL = '${tenantPrefix}';

function doTransfer(humanTaskId) {
	$('#modal form').attr('action', ROOT_URL + '/humantask/workspace-transferTask.do');
	$('#humanTaskId').val(humanTaskId);
	$('#modal').modal();
}
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
        <th>标题</th>
        <th>到达时间</th>
        <th>完成时间</th>
        <th>持续时间</th>
        <th>流程</th>
        <th>环节</th>
        <th>状态</th>
        <th width="110">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <td>${item.presentationSubject}</td>
	    <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td><fmt:formatDate value="${item.completeTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td><tags:duration startTime="${item.createTime}" endTime="${item.completeTime}"/></td>
	    <td><tags:processName processDefinitionId="${item.processDefinitionId}"/></td>
	    <td>
		  ${item.name}
		</td>
	    <td>完成</td>
        <td>
          <a href="${tenantPrefix}/operation/task-operation-withdraw.do?humanTaskId=${item.id}&comment=">撤销</a>
          <a href="javascript:void(0);doTransfer(${item.id})">转发</a>
          <a href="${tenantPrefix}/bpm/workspace-viewHistory.do?processInstanceId=${item.processInstanceId}">详情</a>
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

	<div id="modal" class="modal fade">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-body">
	        <form>
	          <input type="hidden" id="humanTaskId" name="humanTaskId" value=""/>
			  <div class="input-group userPicker" style="width:200px;">
				<input id="_assignee_key_" type="hidden" name="assignee" class="input-medium" value="">
				<input type="text" class="form-control" name="username" placeholder="" value="">
				<div class="input-group-addon"><i class="glyphicon glyphicon-user"></i></div>
			  </div>
		      <br>
		      <button class="btn btn-default">提交</button>
		    </form>
	      </div>
		</div>
	  </div>
	</div>

  </body>

</html>
