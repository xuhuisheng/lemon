<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>表单列表</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'dynamicModelGrid',
    pageNo: ${empty page.pageNo ? 1 : page.pageNo},
    pageSize: ${empty page.pageSize ? 10 : page.pageSize},
    totalCount: ${empty page.totalCount ? 10 : page.totalCount},
    resultSize: ${empty page.resultSize ? 10 : page.resultSize},
    pageCount: ${empty page.pageCount ? 1 : page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${empty page.asc ? 'true' : page.asc},
    params: {
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'dynamicModelGridForm',
	exportUrl: 'form-template-export.do'
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
<!--
<div class="panel panel-default">
  <div class="panel-heading">
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="pimRemindSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

  </div>
</div>
-->

      <div style="margin-bottom: 20px;">
	  <!--
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='pim-info-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
		</div>
		-->

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
	  
<form id="pimRemindGridForm" name="pimRemindGridForm" method='post' action="#" class="m-form-blank">
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
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="name">创建时间</th>
        <th class="sorting" name="status">状态</th>
        <th width="120">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <%--
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.code}"></td>
		--%>
        <td>${item.name}</td>
        <td><fmt:formatDate value="${item.createTime}" type="both"/></td>
        <td>${item.status == 0 ? '流程草稿' : '任务草稿'}</td>
        <td>
          <a href="process-operation-viewStartForm.do?businessKey=${item.code}&bpmProcessId=${item.category}">编辑</a>
		  &nbsp;
          <a href="process-operation-removeDraft.do?code=${item.code}">删除</a>
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
