<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.party-struct.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'party-structGrid',
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
	gridFormId: 'party-structGridForm',
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
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="party-structSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="party-structForm" method="post" action="party-struct-list.do" class="form-inline">
		    <label for="party-struct_name"><spring:message code='party-struct.party-struct.list.search.name' text='名称'/>:</label>
			<select id="orgStruct_orgStructType" name="filter_EQL_partyStructType.id" class="form-control">
			  <option value=""></option>
			  <c:forEach items="${partyStructTypes}" var="item">
			  <option value="${item.id}" ${param['filter_EQL_partyStructType.id'] == item.id ? 'selected' : ''}>${item.name}</option>
			  </c:forEach>
		    </select>
			<button class="btn btn-default a-search" onclick="document.party-structForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='party-struct-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
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

<form id="party-structGridForm" name="party-structGridForm" method='post' action="party-struct-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>


    <table id="orgStructGrid" class="table table-hover">
      <thead>
        <tr>
          <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
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

