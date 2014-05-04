<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.struct.list.title" text="组织机构结构"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'orgStructGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
		'filter_EQL_partyStructType.id': '${param["filter_EQL_partyStructType.id"]}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'orgStructGridForm',
	exportUrl: 'party-struct-export.do'
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
    <%@include file="/header/party.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/party.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="orgstructSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="orgstructSearch" class="content content-inner">

		  <form name="orgstructForm" method="post" action="party-struct-list.do" class="form-inline">
		    <label for="orgStruct_orgStructType.id"><spring:message code="org.struct.list.search.type" text="类型"/>:</label>
		    <select id="orgStruct_orgStructType" name="filter_EQL_partyStructType.id">
			  <option value=""></option>
			  <c:forEach items="${partyStructTypes}" var="item">
			  <option value="${item.id}" ${param['filter_EQL_partyStructType.id'] == item.id ? 'selected' : ''}>${item.name}</option>
			  </c:forEach>
		    </select>
			<button class="btn btn-small" onclick="document.orgstructForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="struct:create">
		  <button class="btn btn-small" onclick="location.href='party-struct-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="struct:delete">
		  <button class="btn btn-small" onclick="table.removeAll()">删除</button>
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
		  <h4 class="title"><spring:message code="org.struct.list.title" text="组织机构结构"/></h4>
		</header>
		<div class="content">

  <form id="orgStructGridForm" name="orgStructGridForm" method='post' action="party-struct-remove.do" class="m-form-blank">
    <table id="orgStructGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="partyStructType.id"><spring:message code="org.struct.list.type" text="类型"/></th>
          <th class="sorting" name="parentEntity.id"><spring:message code="org.struct.list.parententity" text="上级组织"/></th>
          <th class="sorting" name="childEntity.id"><spring:message code="org.struct.list.childentity" text="下级组织"/></th>
          <th class="sorting" name="partTime">兼职</th>
          <th class="sorting" name="link">关联</th>
          <th class="sorting" name="priority">排序</th>
          <th class="sorting" name="admin">管理</th>
          <th width="50">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
          <td>${item.partyStructType.name}</td>
          <td>${item.parentEntity.name}</td>
          <td>${item.childEntity.name}</td>
          <td>${item.partTime}</td>
          <td>${item.link}</td>
          <td>${item.priority}</td>
          <td>${item.admin}</td>
          <td>
            <a href="party-struct-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
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
