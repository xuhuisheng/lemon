<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group");%>
<%pageContext.setAttribute("currentMenu", "group");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>管理下属</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'orgGrid',
    pageNo: '${page.pageNo}',
    pageSize: '${page.pageSize}',
    totalCount: '${page.totalCount}',
    resultSize: '${page.resultSize}',
    pageCount: '${page.pageCount}',
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: '${page.asc}',
    params: {
        'filter_LIKES_orgname': '${param.filter_LIKES_orgname}',
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'orgGridForm',
	exportUrl: 'group-base-export.do'
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
    <%@include file="/header/group.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/group.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">管理</h4>
		  <div class="ctrl">
			<a class="btn"><i id="orgSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="orgSearch" class="content content-inner">
<!--
		  <button class="btn" onclick="location.href='group-base-inputRoot.do'">添加顶级部门</button>
-->
		  <button class="btn" onclick="location.href='org-users.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=${partyEntityId}'">管理下属</button>
		  <button class="btn" onclick="location.href='org-children.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=${partyEntityId}'">下级组织</button>
		  <br>
		  <br>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="org:create">
		  <button class="btn btn-small a-insert" onclick="location.href='org-inputUser.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=${partyEntityId}'">新建</button>
		  </region:region-permission>
		  <%--
		  <region:region-permission permission="org:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
		  --%>
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
		  <h4 class="title">部门下属</h4>
		</header>
		<div class="content">

<form id="orgGridForm" name="orgGridForm" method='post' action="org-removeUser.do?partyDimId=${partyDimId}&partyEntityId=${partyEntityId}" class="m-form-blank">
  <table id="orgGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="org.org.list.id" text="编号"/></th>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="partTime">兼职</th>
        <th class="sorting" name="link">关联</th>
        <th class="sorting" name="priorty">排序</th>
        <th class="sorting" name="admin">管理</th>
        <th>操作</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.childEntity.id}"></td>
        <td>${item.childEntity.id}</td>
        <td>${item.childEntity.name}</td>
        <td>${item.partTime == 1}</td>
        <td>${item.link}</td>
        <td>${item.priority}</td>
        <td>${item.admin == 1}</td>
        <td>
		  <a href="org-removeUser.do?selectedItem=${item.id}&partyStructTypeId=${param.partyStructTypeId}&partyEntityId=${param.partyEntityId}" class="a-remove">删除</a>
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
