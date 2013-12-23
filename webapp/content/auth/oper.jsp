<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.oper.list.title" text="操作列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'operGrid',
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
	gridFormId: 'operGridForm',
	exportUrl: 'oper!exportExcel.do'
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
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="operSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="operSearch" class="content content-inner">

		  <form name="operForm" method="post" action="oper.do" class="form-inline">
		    <label for="oper_name"><spring:message code="auth.oper.list.search.name" text="名称"/>:</label>
		    <input type="text" id="oper_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.operForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="oper:create">
		  <button class="btn btn-small" onclick="location.href='oper!input.do'"><spring:message code="core.list.create" text="新建"/></button>
		  </region:region-permission>
		  <region:region-permission permission="oper:delete">
		  <button class="btn btn-small" onclick="table.removeAll()"><spring:message code="core.list.delete" text="删除"/></button>
		  </region:region-permission>
		  <button class="btn btn-small" onclick="table.exportExcel()">导出</button>
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
          <h4 class="title"><spring:message code="auth.oper.list.title" text="操作列表"/></h4>
		</header>
		<div class="content">

  <form id="operGridForm" name="operGridForm" method='post' action="oper!removeAll.do" class="m-form-blank">
    <table id="operGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="auth.oper.list.id" text="编号"/></th>
          <th class="sorting" name="name"><spring:message code="auth.oper.list.name" text="名称"/></th>
          <th class="sorting" name="mask"><spring:message code="auth.oper.list.mask" text="掩码"/></th>
          <th class="sorting" name="code"><spring:message code="auth.oper.list.code" text="代码"/></th>
          <th width="50">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <s:iterator value="page.result" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.name}</td>
          <td>${item.mask}</td>
          <td>${item.code}</td>
          <td>
			<region:region-permission permission="oper:write">
            <a href="oper!input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>&nbsp;
			</region:region-permission>
          </td>
        </tr>
        </s:iterator>
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
