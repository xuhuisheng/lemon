<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "acl");%>
<%pageContext.setAttribute("currentMenu", "acl");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="用户列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'aclObjectIdentityGrid',
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
	gridFormId: 'aclObjectIdentityGridForm',
	exportUrl: 'acl-object-identity!exportExcel.do'
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
    <%@include file="/header/acl.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/acl.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="userSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="userSearch" class="content content-inner">

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <button class="btn btn-small" onclick="location.href='acl-object-identity!input.do'">新建</button>
		  <button class="btn btn-small" onclick="table.removeAll()">删除</button>
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
          <h4 class="title"><spring:message code="user.user.list.title" text="用户列表"/></h4>
		</header>
		<div class="content">

<form id="aclObjectIdentityGridForm" name="userGridForm" method='post' action="acl-object-identity!removeAll.do" class="m-form-blank">
  <table id="userGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="reference">引用</th>
        <th class="sorting" name="entriesHieriting">支持继承</th>
        <th class="sorting" name="aclObjectType.id">对象类型</th>
        <th class="sorting" name="aclObjectIdentity.id">上级对象</th>
        <th class="sorting" name="aclSid.id">拥有者</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <s:iterator value="page.result" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.reference}</td>
        <td>${item.entriesHieriting == 1}</td>
        <td>${item.aclObjectType.name}</td>
        <td>${item.aclObjectIdentity.reference}</td>
        <td>${item.ownerId}</td>
        <td>
          <a href="acl-object-identity!input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
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
