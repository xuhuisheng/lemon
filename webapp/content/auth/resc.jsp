<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.resc.list.title" text="资源列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'rescGrid',
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
	gridFormId: 'rescGridForm',
	exportUrl: 'resc!exportExcel.do'
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
			<a class="btn"><i id="rescSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="rescSearch" class="content content-inner">

		  <form name="rescForm" method="post" action="resc.do" class="form-inline">
		    <label for="resc_name"><spring:message code="auth.resc.list.search.name" text="名称"/>:</label>
		    <input type="text" id="resc_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.rescForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="resc:create">
		  <button class="btn btn-small" onclick="location.href='resc!input.do'"><spring:message code="core.list.create" text="新建"/></button>
		  </region:region-permission>
		  <region:region-permission permission="resc:delete">
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
		  <h4 class="title"><spring:message code="auth.resc.list.title" text="资源列表"/></h4>
		</header>
		<div class="content">

  <form id="rescGridForm" name="rescGridForm" method='post' action="resc!removeAll.do" class="m-form-blank">
    <table id="rescGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="auth.resc.list.id" text="编号"/></th>
          <th class="sorting" name="name"><spring:message code="auth.resc.list.name" text="名称"/></th>
          <th width="50">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <s:iterator value="page.result" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.name}</td>
          <td>
			<region:region-permission permission="resc:write">
            <a href="resc!input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>&nbsp;
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
