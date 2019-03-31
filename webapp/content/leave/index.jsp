<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "leave");%>
<%pageContext.setAttribute("currentMenu", "leave");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.leave-info.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'leave-infoGrid',
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
	gridFormId: 'leave-infoGridForm',
	exportUrl: 'leave-info-export.do'
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
    <%@include file="/header/leave-user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/leave-user.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="leave-infoSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="leave-infoForm" method="post" action="index.do" class="form-inline">
		    <label for="leave-info_name"><spring:message code='leave-info.leave-info.list.search.name' text='名称'/>:</label>
		    <input type="text" id="leave-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.leave-infoForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='${ctx}/operation/process-operation-viewStartFormByKey.do?key=leave'">新建申请</button>
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

<form id="leave-infoGridForm" name="leave-infoGridForm" method='post' action="leave-info-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>

  <table id="leave-infoGrid" class="table table-hover">
    <thead>
      <tr>
        <th>单号</th>
        <th>申请人</th>
        <th>申请人部门</th>
        <th>请假类型</th>
        <th>开始时间</th>
        <th>结束时间</th>
        <th>申请时间</th>
        <th>状态</th>
        <th>&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><a href="${tenantPrefix}/operation/form-operation-view.do?businessKey=${item.code}">${item.code}</a></td>
        <td><tags:user userId="${item.userId}"/></td>
        <td>${item.deptCode}</td>
        <td>${item.type}</td>
        <td>${item.startTime}</td>
        <td>${item.endTime}</td>
        <td><fmt:formatDate value="${item.createTime}" type="both"/></td>
        <td>${item.status}</td>
        <td>
          <c:if test="${item.status == '草稿'}">
          <a href="${ctx}/operation/process-operation-viewStartForm.do?businessKey=${item.code}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
          </c:if>
          <c:if test="${item.status == '驳回'}">
          <a href="${ctx}/operation/task-operation-viewTaskFormByBusinessKey.do?businessKey=${item.code}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
          </c:if>
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

