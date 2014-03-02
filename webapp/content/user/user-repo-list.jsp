<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user-sys");%>
<%pageContext.setAttribute("currentMenu", "user-sys");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.userRepo.list.title" text="用户库列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'userRepoGrid',
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
	gridFormId: 'userRepoGridForm',
	exportUrl: 'user-repo-export.do'
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
    <%@include file="/header/user-sys.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/user-sys.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a href="javascript:$('#userRepoSearch').toggle(200);$('#userRepoSearchIcon').toggleClass('icon-chevron-down');$('#userRepoSearchIcon').toggleClass('icon-chevron-up');void(0);" class="btn"><i id="userRepoSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="userRepoSearch" class="content content-inner">

		  <form name="userRepoForm" method="post" action="user-repo-list.do" class="form-inline">
		    <label for="userRepo_name"><spring:message code="auth.userRepo.list.search.name" text="名称"/>:</label>
		    <input type="text" id="userRepo_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.userRepoForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article style="margin-bottom:10px;">
	    <div class="pull-left">
		  <region:region-permission permission="userrepo:create">
		  <button class="btn btn-small a-insert" onclick="location.href='user-repo-input.do'"><spring:message code="core.list.create" text="新建"/></button>
		  </region:region-permission>
		  <region:region-permission permission="userrepo:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()"><spring:message code="core.list.delete" text="删除"/></button>
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
		  <h4 class="title"><spring:message code="auth.userRepo.list.title" text="用户库列表"/></h4>
		</header>
		<div class="content">

  <form id="userRepoGridForm" name="userRepoGridForm" method='post' action="user-repo-remove.do" style="margin:0px;">
    <table id="userRepoGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" style="text-indent:0px;text-align:center;"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="user.userRepo.list.id" text="编号"/></th>
          <th class="sorting" name="code"><spring:message code="user.userRepo.list.code" text="代码"/></th>
          <th class="sorting" name="name"><spring:message code="user.userRepo.list.name" text="名称"/></th>
          <th class="sorting" name="ref">引用</th>
          <th width="100">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.code}</td>
          <td>${item.name}</td>
          <td>${item.ref}</td>
          <td>
            <a href="user-schema-list.do?userRepoId=${item.id}">配置属性</a>&nbsp;
			<region:region-permission permission="userrepo:write">
            <a href="user-repo-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>&nbsp;
			</region:region-permission>
          </td>
        </tr>
        </c:forEach>
      </tbody>
    </table>
  </form>
        </div>
      </article>

	  <article style="margin-bottom: 10px;">
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

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
