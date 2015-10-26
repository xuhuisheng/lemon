<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group-sys");%>
<%pageContext.setAttribute("currentMenu", "group");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>岗位列表</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'orgGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_orgname': '${param.filter_LIKES_orgname}',
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'orgGridForm',
	exportUrl: 'org-position-export.do'
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
    <%@include file="/header/org-sys.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/org-sys.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
			<a class="btn"><i id="orgSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="orgSearch" class="content content-inner">

		  <form name="orgForm" method="post" action="org-position-list.do" class="form-inline">
		    <label for="org_orgname"><spring:message code='org.org.list.search.orgname' text='账号'/>:</label>
		    <input type="text" id="org_orgname" name="filter_LIKES_orgname" value="${param.filter_LIKES_orgname}">
		    <label for="org_enabled"><spring:message code='org.org.list.search.status' text='状态'/>:</label>
		    <select id="org_enabled" name="filter_EQI_status" class="input-mini">
			  <option value=""></option>
			  <option value="1" ${param.filter_EQI_status == 1 ? 'selected' : ''}><spring:message code='org.org.list.search.enabled.true' text='启用'/></option>
			  <option value="0" ${param.filter_EQI_status == 0 ? 'selected' : ''}><spring:message code='org.org.list.search.enabled.false' text='禁用'/></option>
		    </select>
			<button class="btn btn-small" onclick="document.orgForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="org:create">
		  <button class="btn btn-small a-insert" onclick="location.href='org-position-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="org:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
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
		  <h4 class="title">岗位列表</h4>
		</header>
		<div class="content">

<form id="orgGridForm" name="orgGridForm" method='post' action="org-position-remove.do" class="m-form-blank">
  <table id="orgGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="org.org.list.id" text="编号"/></th>
        <th class="sorting" name="name">名称</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>
          <a href="org-position-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
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
