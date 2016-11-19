<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程列表</title>
    <%@include file="/common/s3.jsp"%>

    <link type="text/css" rel="stylesheet" href="../widgets/userpicker3-v2/userpicker.css">
    <script type="text/javascript" src="../widgets/userpicker3-v2/userpicker.js"></script>
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
		treeUrl: '${tenantPrefix}/rs/party/tree?partyStructTypeId=1',
		childUrl: '${tenantPrefix}/rs/party/searchUser'
	});
});

var ROOT_URL = '${tenantPrefix}';

function doTransfer(processInstanceId) {
	$('#modal form').attr('action', ROOT_URL + '/bpm/workspace-transferProcessInstance.do');
	$('#processInstanceId').val(processInstanceId);
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
	  
<form id="bpmForm" name="bpmForm" method='post' action="pim-note-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  列表
		</div>
  <table id="pimRemindGrid" class="table table-hover">
    <thead>
      <tr>
	    <!--
        <th class="sorting" name="id">编号</th>
		-->
        <th>标题</th>
        <th class="sorting" name="name">流程定义</th>
        <th class="sorting" name="createTime">创建时间</th>
        <th class="sorting" name="endTime">结束时间</th>
        <th>持续时间</th>
        <th>状态</th>
        <th width="110">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <!--
	    <td>${item.id}</td>
		-->
	    <td><a href="workspace-viewHistory.do?processInstanceId=${item.id}">${item.name}</a></td>
	    <td><tags:processName processDefinitionId="${item.processDefinitionId}"/></td>
	    <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td><tags:duration startTime="${item.startTime}" endTime="${item.endTime}"/></td>
	    <td>结束</td>
        <td>
          <a href="workspace-copyProcessInstance.do?processInstanceId=${item.id}">复制</a>
          <a href="javascript:void(0);doTransfer(${item.id})">转发</a>
          <a href="workspace-viewHistory.do?processInstanceId=${item.id}">详情</a>
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
	          <input type="hidden" id="processInstanceId" name="processInstanceId" value=""/>
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
