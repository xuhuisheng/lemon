<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "form");%>
<%pageContext.setAttribute("currentMenu", "form");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="数据列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'dynamicModelGrid',
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
	gridFormId: 'dynamicModelGridForm',
	exportUrl: 'dynamic-model!exportExcel.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
});
    </script>
  </head>

  <body>
    <%@include file="/header/form.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/form.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="m-column">

	  <article class="m-module m-width-full">
        <header>
		  <h3 style="width:50%">查询</h3>
		  <a style="float:right;margin-top:5px;margin-right:10px;" href="javascript:$('#userSearch').toggle(200);$('#userSearchIcon').toggleClass('icon-chevron-down');$('#userSearchIcon').toggleClass('icon-chevron-up');void(0);"><i id="userSearchIcon" class="icon-chevron-up"></i></a>
		</header>
        <div id="userSearch" class="m-module-content">

		  <form name="userForm" method="post" action="dynamic-model.do" class="form-inline">
		  </form>

		</div>
	  </article>

	  <article class="m-module-blank m-width-full">
	    <div style="float:left;">
		  <region:region-permission permission="user:create">
		  <button class="btn btn-small" onclick="location.href='dynamic-model!input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="user:delete">
		  <button class="btn btn-small" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
		  <button class="btn btn-small" onclick="table.exportExcel()">导出</button>
		</div>

		<div class="btn-group m-pagination" style="float:right;">
		  <button class="btn btn-small">&lt;</button>
		  <button class="btn btn-small">1</button>
		  <button class="btn btn-small">&gt;</button>
		</div>

	    <div class="m-clear"></div>
	  </article>

      <article class="m-module m-width-full">
        <header><h3><spring:message code="user.user.list.title" text="数据列表"/></h3></header>

<form id="dynamicModelGridForm" name="dynamicModelGridForm" method='post' action="dynamic-model!removeAll.do" style="margin:0px;">
  <table id="dynamicModelGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" style="text-indent:0px;text-align:center;"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="user.user.list.id" text="编号"/></th>
        <th class="sorting" name="username">名称</th>
        <th class="sorting" name="status">状态</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <s:iterator value="page.result" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.status == 1 ? '启用' : '禁用'}</td>
        <td>
          <a href="dynamic-model!input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      </s:iterator>
    </tbody>
  </table>
</form>

      </article>

	  <article class="m-module-blank m-width-full">
		<div class="btn-group m-pagination" style="float:right;">
		  <button class="btn btn-small">&lt;</button>
		  <button class="btn btn-small">1</button>
		  <button class="btn btn-small">&gt;</button>
		</div>

	    <div class="m-clear"></div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->

  </body>

</html>
