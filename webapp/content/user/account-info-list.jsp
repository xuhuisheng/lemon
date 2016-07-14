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
	exportUrl: 'account-info-export.do'
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

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="pimRemindSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">
		  <form name="userForm" method="post" action="account-info-list.do" class="form-inline">
		    <label for="user_username"><spring:message code='user.user.list.search.username' text='账号'/>:</label>
		    <input type="text" id="user_username" name="filter_LIKES_username" value="${param.filter_LIKES_username}" class="form-control">
		    <label for="user_enabled"><spring:message code='user.user.list.search.status' text='状态'/>:</label>
		    <select id="user_enabled" name="filter_EQS_status" class="form-control">
			  <option value=""></option>
			  <option value="active" ${param.filter_EQS_status == 'active' ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.true' text='启用'/></option>
			  <option value="disabled" ${param.filter_EQS_status == 'disabled' ? 'selected' : ''}><spring:message code='user.user.list.search.enabled.false' text='禁用'/></option>
		    </select>
			<button class="btn btn-default" onclick="document.userForm.submit()">查询</button>
		  </form>
  </div>
</div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='account-info-input.do'">新建</button>
		  <!--
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
		  -->
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
	  
<form id="pimRemindGridForm" name="pimRemindGridForm" method='post' action="account-info-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="user.user.list.title" text="用户列表"/>
		</div>
  <table id="userGrid" class="table table-hover">
    <thead>
      <tr>
	    <%--
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
		--%>
        <th class="sorting" name="username"><spring:message code="user.user.list.username" text="账号"/></th>
        <th class="sorting" name="displayName">显示名</th>
        <th class="sorting" name="createTime">类型</th>
        <th class="sorting" name="status"><spring:message code="user.user.list.status" text="状态"/></th>
        <th class="sorting" name="createTime">创建时间</th>
        <th width="120">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
	    <%--
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
		--%>
        <td>${item.username}</td>
        <td>${item.displayName}</td>
        <td>${item.type}</td>
        <td>
		  <c:if test="${item.status=='active'}">
		    <span style="color:green;">启用</span>(<a href="account-info-disable.do?id=${item.id}">禁用</a>)
		  </c:if>
		  <c:if test="${item.status=='disabled'}">
		    <span style="color:red;">禁用</span>(<a href="account-info-active.do?id=${item.id}">启用</a>)
		  </c:if>
		</td>
        <td><fmt:formatDate value="${item.createTime}" type="both"/></td>
        <td>
          <a href="account-info-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
          <a href="person-info-account-input.do?code=${item.id}">详细信息</a>
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

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
