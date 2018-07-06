<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="用户列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'userGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_username': '${param.filter_LIKES_username}',
        'filter_EQS_status': '${param.filter_EQS_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'userGridForm',
	exportUrl: 'export.do'
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
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">
	  
<form id="userGridForm" name="userGridForm" method='post' action="remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="user.user.list.title" text="用户列表"/>
		</div>
  <table id="userGrid" class="table table-hover">
    <thead>
      <tr>
		<th>账号</th>
		<th>显示名</th>
		<th>手机</th>
		<th>邮箱</th>
		<th>结果</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${list}" var="item">
      <tr>
		<td>${item.username}</td>
        <td>${item.displayName}</td>
        <td>${item.mobile}</td>
        <td>${item.email}</td>
        <td>${item.result}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
      </div>
</form>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
