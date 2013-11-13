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
    id: 'aclEntryGrid',
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
	gridFormId: 'aclEntryGridForm',
	exportUrl: 'acl-entry!exportExcel.do'
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
		  <button class="btn btn-small" onclick="location.href='acl-entry!input.do'">新建</button>
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

<form id="aclEntryGridForm" name="userGridForm" method='post' action="acl-entry!removeAll.do" class="m-form-blank">
  <table id="userGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="aceOrder">次序</th>
        <th class="sorting" name="granting">是否授权</th>
        <th class="sorting" name="mask">权限</th>
        <th class="sorting" name="auditSuccess">审计授权</th>
        <th class="sorting" name="auditFailure">审计收回</th>
        <th class="sorting" name="aclObjectIdentity.id">对象</th>
        <th class="sorting" name="aclSid.id">主体</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <s:iterator value="page.result" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.aceOrder}</td>
        <td>${item.granting == 1 ? '是' : '否'}</td>
        <td>${item.mask}</td>
        <td>${item.auditSuccess == 1 ? '是' : '否'}</td>
        <td>${item.auditFailure == 1 ? '是' : '否'}</td>
        <td>${item.aclObjectIdentity.aclObjectType.name}#${item.aclObjectIdentity.reference}</td>
        <td>${item.sidId}</td>
        <td>
          <a href="acl-entry!input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
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
