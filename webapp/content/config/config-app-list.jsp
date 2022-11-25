<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "tenant");%>
<%pageContext.setAttribute("currentMenu", "tenant");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>应用</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'tenant-infoGrid',
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
	gridFormId: 'tenant-infoGridForm',
	exportUrl: 'tenant-info-export.do'
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
    <%@include file="/header/config.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/config.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="pimRemindSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="tenant-infoForm" method="post" action="tenant-info-list.do" class="form-inline">
		    <label for="tenant-info_name"><spring:message code='tenant-info.tenant-info.list.search.name' text='名称'/>:</label>
		    <input type="text" id="tenant-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.tenant-infoForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='tenant-info-input.do'">新建</button>
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

<form id="account-deviceGridForm" name="account-deviceGridForm" method='post' action="account-device-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>

  <table id="scope-infoGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="code">编码</th>
        <th class="sorting" name="name"><spring:message code="tenant-info.tenant-info.list.name" text="名称"/></th>
        <th class="sorting" name="shared">注册时间</th>
		<!--
        <th class="sorting" name="userRepoCode">登录方式</th>
		-->
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.code}</td>
        <td>${item.name}</td>
        <td>${item.shared}</td>
		<!--
        <td>${item.userRepoCode}</td>
		-->
        <td>
          <a class="a-update" href="tenant-info-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      </c:forEach>
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>user</td>
        <td>帐号中心</td>
        <td>2022-08-25</td>
        <td>
          <a class="a-update" href="tenant-info-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>auth</td>
        <td>权限中心</td>
        <td>2022-08-25</td>
        <td>
          <a class="a-update" href="tenant-info-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>visitor</td>
        <td>访客邀约</td>
        <td>2022-08-25</td>
        <td>
          <a class="a-update" href="tenant-info-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>meeting</td>
        <td>会议室预订</td>
        <td>2022-08-25</td>
        <td>
          <a class="a-update" href="tenant-info-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
    </tbody>
  </table>
      </div>
</form>

	  <div>
	    <div class="m-page-info pull-left">
		  共4条记录 显示1到4条记录
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
