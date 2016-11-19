<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.modeler/modeler.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'modeler/modelerGrid',
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
	gridFormId: 'modeler/modelerGridForm',
	exportUrl: 'modeler/modeler-export.do'
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
	  <a class="btn btn-default btn-xs"><i id="modeler/modelerSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="modeler/modelerForm" method="post" action="modeler/modeler-list.do" class="form-inline">
		    <label for="modeler/modeler_name"><spring:message code='modeler/modeler.modeler/modeler.list.search.name' text='名称'/>:</label>
		    <input type="text" id="modeler/modeler_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.modeler/modelerForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>
--%>
      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		<%--
		  <button class="btn btn-default a-insert" onclick="location.href='modeler/modeler-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
		  --%>
	      <a href="modeler-open.do" class="btn btn-default">新建模型</a>
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

<form id="modeler/modelerGridForm" name="modeler/modelerGridForm" method='post' action="modeler/modeler-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>

  <table id="demoGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id">id</th>
        <th class="sorting" name="name">key</th>
        <th class="sorting" name="name">name</th>
        <th class="sorting" name="name">version</th>
        <th class="sorting" name="name">category</th>
        <th class="sorting" name="name">createTime</th>
        <th class="sorting" name="name">lastUpdateTime</th>
        <th class="sorting" name="name">deploymentId</th>
        <th class="sorting" name="name">metaInfo</th>
        <th>&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${models}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
	    <td>${item.id}</td>
	    <td>${item.key}</td>
	    <td>${item.name}</td>
	    <td>${item.version}</td>
	    <td>${item.category}</td>
	    <td>${item.createTime}</td>
	    <td>${item.lastUpdateTime}</td>
	    <td>${item.deploymentId}</td>
	    <td>${item.metaInfo}</td>
        <td>
		  <a href="modeler-open.do?id=${item.id}" target="_blank">编辑</a>
		  <a href="modeler-remove.do?id=${item.id}">删除</a>
		  <a href="modeler-deploy.do?id=${item.id}">发布</a>
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

