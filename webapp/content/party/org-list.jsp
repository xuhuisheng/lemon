<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "org");%>
<%pageContext.setAttribute("currentMenu", "org");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.org.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
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
        'filter_LIKES_name': '${param.filter_LIKES_name}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'orgGridForm',
	exportUrl: 'org-export.do'
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
    <%@include file="/header/org.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/org.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="orgSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="orgForm" method="post" action="org-list.do" class="form-inline">
		    <label for="org_name"><spring:message code='org.org.list.search.name' text='名称'/>:</label>
		    <input type="text" id="org_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.orgForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <c:forEach items="${childTypes}" var="item">
		  <button class="btn btn-default a-insert" onclick="location.href='org-input.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=${partyEntityId}&partyTypeId=${item.id}'">新建${item.name}</button>
		  </c:forEach>
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

<form id="orgGridForm" name="orgGridForm" method='post' action="org-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>


  <table id="orgGrid" class="table table-hover">
    <thead>
      <tr>
	    <!--
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
		-->
        <th class="sorting" name="id"><spring:message code="org.org.list.id" text="编号"/></th>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="partyType">类型</th>
        <th class="sorting" name="admin">管理</th>
        <th>操作</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <!--
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.childEntity.id}"></td>
		-->
        <td>${item.childEntity.id}</td>
        <td>${item.childEntity.name}</td>
        <td>${item.childEntity.partyType.name}</td>
        <td>${item.admin == 1}</td>
        <td>
		  <a href="org-remove.do?selectedItem=${item.id}&partyStructTypeId=${partyStructTypeId}&partyEntityId=${partyEntityId}" class="a-remove">删除</a>
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

