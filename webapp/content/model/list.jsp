<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "model");%>
<%pageContext.setAttribute("currentMenu", "model");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.model-info.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
	<script src="${tenantPrefix}/widgets/querybuilder/QueryBuilder-3.3.5.js"></script>

    <script type="text/javascript">
var config = {
    id: 'modelGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
		'id': '${param.id}',
		'q': '${param.q}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'modelGridForm',
	exportUrl: 'export.do?id=${param.id}'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});

$(function() {
	var queryBuilder = new QueryBuilder('queryBuilder');
	queryBuilder.setFields({
		<c:forEach items="${searchableFields}" var="item" varStatus="status">
		${item.code}: {
			name: '${item.code}',
			operator: '包含',
			label: '${item.name}'
		}${status.last ? "" : ","}
		</c:forEach>
	});
	var q = '${param.q}';
	if (q != '') {
		var array = q.split('\\|');

		var data = [];
		for (var i = 0; i < array.length; i++) {
			var name = array[i].split('=')[0];
			var value = array[i].split('=')[1];
			data.push({
				name: name,
				value: value
			});
		}
		queryBuilder.setData(data);
	}
	queryBuilder.setUrl('list.do?id=${param.id}');
	queryBuilder.render();
});
    </script>
  </head>

  <body>
    <%@include file="/header/model.jsp"%>

    <div class="row-fluid">
      <!-- start of sidebar -->

<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-model" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-model" aria-expanded="true" aria-controls="collapse-body-model">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
	    <span class="title">模型</span>
      </h4>
    </div>
    <div id="collapse-body-model" class="panel-collapse collapse ${currentMenu == 'model' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-model">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <c:forEach items="${modelInfos}" var="item">
		  <li><a href="${tenantPrefix}/model/list.do?id=${item.id}"><i class="glyphicon glyphicon-list"></i> ${item.name}</a></li>
		  </c:forEach>
        </ul>
      </div>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>
      <!-- end of sidebar -->

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<nav class="navbar navbar-default" role="navigation" id="queryBuilder">
  <div class="container-fluid">
    <div class="navbar-header">
      <form class="navbar-form form-group">
        <input type="text" class="form-control" placeholder="Search" id="queryBuilderText" name="text">
      </form>
    </div>

    <ul class="nav navbar-nav" id="queryBuilderFields">
    </ul>

	<ul class="nav navbar-nav">
      <li class="dropdown">
        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
		  + 更多条件
		  <span class="caret"></span>
		</a>
        <ul class="dropdown-menu" role="menu" id="queryBuilderMenu">
        </ul>
      </li>
    </ul>
    
	<div class="navbar-form navbar-right">
      <button type="submit" class="btn btn-default" id="queryBuilderButton">搜索</button>
    </div>
  </div>
</nav>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		<%--
		  <button class="btn btn-default a-insert" onclick="location.href='sendmail-history-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  --%>
		  <button class="btn btn-small a-export" onclick="table.exportExcel()">导出</button>
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


<form id="modelGridForm" name="modelGridForm" method='post' action="model-remove.do" class="m-form-blank">
<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    列表
  </div>

  <table id="modelGrid" class="table table-hover">
    <thead>
      <tr>
		<c:forEach items="${listFields}" var="item">
        <th class="sorting" name="${item.code}">${item.name}</th>
		</c:forEach>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
		<c:forEach items="${listFields}" var="headerItem">
        <td>${item[headerItem.code]}</td>
		</c:forEach>
        <td>
          &nbsp;
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
