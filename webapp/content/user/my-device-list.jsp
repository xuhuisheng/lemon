<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "my");%>
<%pageContext.setAttribute("currentMenu", "my");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.account-device.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'account-deviceGrid',
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
	gridFormId: 'account-deviceGridForm',
	exportUrl: 'account-device-export.do'
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
    <%@include file="/header/my.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/my.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <article style="margin-bottom: 20px;">

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
	  </article>

<form id="account-deviceGridForm" name="account-deviceGridForm" method='post' action="my-device-remove.do" class="m-form-blank">
      <article class="panel panel-default">
        <header class="panel-heading">
		  <spring:message code="account-device.account-device.list.title" text="列表"/>
		</header>
  <table id="account-deviceGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="name">类型</th>
        <th class="sorting" name="name">系统</th>
        <th class="sorting" name="name">客户端</th>
        <th class="sorting" name="name">创建时间</th>
        <th class="sorting" name="name">最近访问</th>
        <th class="sorting" name="name">状态</th>
        <th width="120">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.type}</td>
        <td>${item.os}</td>
        <td>${item.client}</td>
        <td>${item.createTime}</td>
        <td>${item.lastLoginTime}</td>
        <td>${item.status}</td>
        <td>
		  <c:if test="${item.status != 'active'}">
          <a href="my-device-active.do?id=${item.id}" class="a-active">信任</a>
		  </c:if>
		  <c:if test="${item.status != 'disabled'}">
          <a href="my-device-disable.do?id=${item.id}" class="a-disable">禁用</a>
		  </c:if>
          <a href="my-device-remove.do?id=${item.id}" class="a-remove">删除</a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
      </article>
</form>

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
