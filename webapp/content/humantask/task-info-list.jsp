<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "humantask");%>
<%pageContext.setAttribute("currentMenu", "humantask");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.task-info.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'task-infoGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_name': '${param.filter_LIKES_name}',
        'filter_LIKES_status': '${param.filter_LIKES_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'task-infoGridForm',
	exportUrl: 'task-info-export.do'
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
    <%@include file="/header/humantask.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/humantask.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="task-infoSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="task-infoForm" method="post" action="task-info-list.do" class="form-inline">
		    <label for="task-info_name"><spring:message code='task-info.task-info.list.search.name' text='名称'/>:</label>
		    <input type="text" id="task-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
		    <label for="taskInfo_status"><spring:message code='user.user.list.search.status' text='状态'/>:</label>
		    <select id="taskInfo_status" name="filter_EQS_status" class="form-control">
			  <option value=""></option>
			  <option value="active" ${param.filter_EQS_status == 'active' ? 'selected' : ''}>进行中</option>
			  <option value="completed" ${param.filter_EQS_status == 'completed' ? 'selected' : ''}>完成</option>
		    </select>
			<button class="btn btn-default a-search" onclick="document.task-infoForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='task-info-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
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

<form id="task-infoGridForm" name="task-infoGridForm" method='post' action="task-info-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>


  <table id="dynamicModelGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
		<!--
        <th class="sorting" name="id"><spring:message code="user.user.list.id" text="编号"/></th>
		-->
        <th>优先级</th>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="name">创建人</th>
        <th class="sorting" name="name">负责人</th>
        <th class="sorting" name="name">创建时间</th>
        <th class="sorting" name="name">完成时间</th>
        <th class="sorting" name="name">过期时间</th>
        <th class="sorting" name="name">状态</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
		<!--
        <td>${item.id}</td>
		-->
        <td>${item.priority}</td>
        <td>${item.presentationSubject}</td>
        <td><tags:user userId="${item.creator}"/></td>
        <td><tags:user userId="${item.assignee}"/></td>
        <td><fmt:formatDate value="${item.createTime}" type="both"/></td>
        <td><fmt:formatDate value="${item.completeTime}" type="both"/></td>
        <td><fmt:formatDate value="${item.expirationTime}" type="both"/></td>
        <td>${item.status}</td>
        <td>
          <a href="task-info-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
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

