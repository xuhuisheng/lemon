<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "model");%>
<%pageContext.setAttribute("currentMenu", "model");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.model-info.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
	<script src="${tenantPrefix}/widgets/querybuilder/QueryBuilder-2.3.2.js"></script>

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
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-model">
              <i class="icon-user"></i>
              <span class="title">模型</span>
            </a>
          </div>
          <div id="collapse-model" class="accordion-body collapse in">
            <ul class="accordion-inner nav nav-list">
			  <c:forEach items="${modelInfos}" var="item">
			  <li><a href="${tenantPrefix}/model/list.do?id=${item.id}"><i class="icon-user"></i>${item.name}</a></li>
			  </c:forEach>
            </ul>
          </div>
		</div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->

	  <!-- start of main -->
      <section id="m-main" class="span10">

<nav class="navbar navbar-default" role="navigation" id="queryBuilder">
  <div class="navbar-inner">
    <div class="container">
      <div class="nav">
        <form class="navbar-form form-group">
		  <!--
          <input type="text" class="form-control" placeholder="搜索" id="queryBuilderText" name="text" style="margin-top:7px;">
		  -->
        </form>
      </div>

      <ul class="nav navbar-nav" id="queryBuilderFields">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">
            Account
            <b class="caret"></b>
          </a>
          <ul class="dropdown-menu">
            ...
          </ul>
        </li>
      </ul>

	  <ul class="nav">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
		    + 更多条件
		    <span class="caret"></span>
		  </a>
          <ul class="dropdown-menu" role="menu" id="queryBuilderMenu">
          </ul>
        </li>
      </ul>
    
	  <div class="nav pull-right">
        <button type="submit" class="btn btn-default" id="queryBuilderButton">搜索</button>
      </div>
    </div>
  </div>
</nav>

	  <article class="m-blank">
	    <div class="pull-left">
		  <button class="btn btn-small a-export" onclick="table.exportExcel()">导出</button>
		</div>

		<div class="pull-right">
		  每页显示
		  <select class="m-page-size">
		    <option value="10">10</option>
		    <option value="20">20</option>
		    <option value="50">50</option>
		  </select>
		  条
		</div>

	    <div class="m-clear"></div>
	  </article>



      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="model-info.model-info.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="modelGridForm" name="modelGridForm" method='post' action="model-remove.do" class="m-form-blank">
  <table id="modelGrid" class="m-table table-hover">
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
</form>
        </div>
      </article>

	  <article>
	    <div class="m-page-info pull-left">
		  共100条记录 显示1到10条记录
		</div>

		<div class="btn-group m-pagination pull-right">
		  <button class="btn btn-small">&lt;</button>
		  <button class="btn btn-small">1</button>
		  <button class="btn btn-small">&gt;</button>
		</div>

	    <div class="m-clear"></div>
      </article>

      <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
