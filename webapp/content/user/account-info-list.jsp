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
	<script src="${cdnPrefix}/public/mossle-usercard/0.0.11/usercard.js"></script>
	<link href="${cdnPrefix}/public/select2/4.0.5/css/select2.min.css" rel="stylesheet" />
	<script src="${cdnPrefix}/public/select2/4.0.5/js/select2.min.js"></script>
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
        'filter_INS_username': '${param.filter_INS_username}'
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

	$('#accountInfo_username').select2({
		ajax: {
			url: '${ctx}/user/rs/search/select2',
			dataType: 'json'
			// Additional AJAX parameters go here; see the end of this chapter for the full code of this example
		}
	});

	// user card
	initUserCard();

});

function doBatch() {
	if ($('.selectedItem:checked').length < 1) {
		alert('请先选择需要操作的账号');
		return;
	}
	document.userGridForm.action = 'account-batch-input.do';
	document.userGridForm.submit();
}
    </script>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

		<form name="userForm" method="post" action="account-info-list.do" class="form-inline" style="margin-bottom:15px;">
		  <label for="user_username"><spring:message code='user.user.list.search.username' text='账号'/>:</label>
	      <select class="form-control" id="accountInfo_username" name="username" multiple="multiple" style="width:500px;">
		  </select>
		  <button class="btn btn-default"><i class="glyphicon glyphicon-search"></i></button>
		</form>

      <div style="margin-bottom: 15px;">

	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='account-info-input.do'">新建</button>
		  <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
		  <!--
		  <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
		  -->
		</div>

		<button class="btn btn-default" onclick="doBatch()" style="margin-left:10px;">批量操作</button>

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

<form id="userGridForm" name="userGridForm" method='post' action="account-info-remove.do" class="m-form-blank">
      <div class="panel panel-default">
  <table id="userGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="username"><spring:message code="user.user.list.username" text="账号"/></th>
        <th class="sorting" name="displayName">显示名</th>
        <th class="sorting" name="createTime">类型</th>
        <th class="sorting" name="status">状态</th>
        <th class="sorting" name="createTime">创建时间</th>
        <th>&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.username}</td>
        <td>
		  <a href="account-detail-index.do?infoId=${item.id}" role="botton" data-toggle="popover" title="" data-content="Loading..." data-username="${item.username}">
		    ${item.displayName}
		  </a>
		</td>
        <td>${item.type}</td>
        <td>
		  <c:set var="status" value="active"/>
		  <c:if test="${item.status=='disabled'}">
		    <span class="label label-danger">禁用</span>
		    <c:set var="status" value="disabled"/>
		  </c:if>
		  <c:if test="${item.locked=='locked'}">
		    <span class="label label-danger">锁定</span>
		    <c:set var="status" value="locked"/>
		  </c:if>
		  <c:if test="${item.closeTime lt now}">
		    <span class="label label-danger">过期</span>
		    <c:set var="status" value="expired"/>
		  </c:if>
		  <c:if test="${status=='active'}">
		    <span class="label label-info">正常</span>
		  </c:if>
		</td>
        <td><fmt:formatDate value="${item.createTime}" type="both"/></td>
        <td>
          <a href="account-detail-index.do?infoId=${item.id}">详情</a>
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
