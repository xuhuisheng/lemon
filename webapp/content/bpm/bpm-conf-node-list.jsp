<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.bpm-conf-node.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'bpm-conf-nodeGrid',
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
	gridFormId: 'bpm-conf-nodeGridForm',
	exportUrl: 'bpm-conf-node-export.do'
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
	  <a class="btn btn-default btn-xs"><i id="bpm-conf-nodeSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="bpm-conf-nodeForm" method="post" action="bpm-conf-node-list.do" class="form-inline">
		    <label for="bpm-conf-node_name"><spring:message code='bpm-conf-node.bpm-conf-node.list.search.name' text='名称'/>:</label>
		    <input type="text" id="bpm-conf-node_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.bpm-conf-nodeForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='bpm-conf-node-input.do'">新建</button>
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
--%>
	  <ul class="breadcrumb">
	    <li><a href="bpm-process-list.do">流程配置</a></li>
	    <li class="active">${bpmConfBase.processDefinitionKey}</li>
	  </ul>

<form id="bpm-conf-nodeGridForm" name="bpm-conf-nodeGridForm" method='post' action="bpm-conf-node-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  流程配置
		</div>

	<table class="table">
      <thead>
        <tr>
          <th>编号</th>
          <th>类型</th>
          <th>节点</th>
          <th>人员</th>
          <th>事件</th>
          <th>规则</th>
          <th>表单</th>
          <th>操作</th>
          <th>提醒</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${bpmConfNodes}" var="item">
        <tr>
          <td>${item.id}</td>
		  <td>${item.type}</td>
          <td>${item.name}</td>
          <td>
		    <c:if test="${item.confUser == 0}">
			  <a href="bpm-conf-user-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confUser == 1}">
			  <a href="bpm-conf-user-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfUsers}">
              <i class="badge">${fn:length(item.bpmConfUsers)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confListener == 0}">
			  <a href="bpm-conf-listener-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confListener == 1}">
			  <a href="bpm-conf-listener-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfListeners}">
              <i class="badge">${fn:length(item.bpmConfListeners)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confRule == 0}">
			  <a href="bpm-conf-rule-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confRule == 1}">
			  <a href="bpm-conf-rule-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfRules}">
              <i class="badge">${fn:length(item.bpmConfRules)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confForm == 0}">
			  <a href="bpm-conf-form-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confForm == 1}">
			  <a href="bpm-conf-form-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfForms}">
              <i class="badge">${fn:length(item.bpmConfForms)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confOperation == 0}">
			  <a href="bpm-conf-operation-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confOperation == 1}">
			  <a href="bpm-conf-operation-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfOperations}">
              <i class="badge">${fn:length(item.bpmConfOperations)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confNotice == 0}">
			  <a href="bpm-conf-notice-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confNotice == 1}">
			  <a href="bpm-conf-notice-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="glyphicon glyphicon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfNotices}">
              <i class="badge">${fn:length(item.bpmConfNotices)}</i>
			</c:if>
			&nbsp;
	      </td>
        </tr>
        </c:forEach>
      </tbody>
	</table>


      </div>
</form>
<%--
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
--%>
      <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

