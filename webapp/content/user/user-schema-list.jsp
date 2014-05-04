<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user-sys");%>
<%pageContext.setAttribute("currentMenu", "user-sys");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.userSchema.list.title" text="用户属性列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'userSchemaGrid',
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
	gridFormId: 'userSchemaGridForm',
	exportUrl: 'user-schema-export.do'
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
		    <a href="javascript:$('#userSchemaSearch').toggle(200);$('#userSchemaSearchIcon').toggleClass('icon-chevron-down');$('#userSchemaSearchIcon').toggleClass('icon-chevron-up');void(0);" class="btn"><i id="userSchemaSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="userSchemaSearch" class="content content-inner">

		  <form name="userSchemaForm" method="post" action="user-schema-list.do" class="form-inline">
		    <label for="userSchema_name"><spring:message code="auth.userSchema.list.search.name" text="名称"/>:</label>
		    <input type="text" id="userSchema_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.userSchemaForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article style="margin-bottom:10px;">
	    <div class="pull-left">
		  <region:region-permission permission="userschema:create">
		  <button class="btn btn-small a-insert" onclick="location.href='user-schema-input.do?userRepoId=${param.userRepoId}'"><spring:message code="core.list.create" text="新建"/></button>
		  </region:region-permission>
		  <region:region-permission permission="userschema:delete">
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
		  <h4 class="title"><spring:message code="auth.userSchema.list.title" text="用户库列表"/></h4>
		</header>
		<div class="content">

  <form id="userSchemaGridForm" name="userSchemaGridForm" method='post' action="user-schema-remove.do?userRepoId=${param.userRepoId}" style="margin:0px;">
    <table id="userSchemaGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" style="text-indent:0px;text-align:center;"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="user.userSchema.list.id" text="编号"/></th>
          <th class="sorting" name="name"><spring:message code="user.userSchema.list.name" text="名称"/></th>
          <th class="sorting" name="type">类型</th>
          <th class="sorting" name="readOnly">只读</th>
          <th class="sorting" name="notNull">非空</th>
          <th class="sorting" name="uniqueConstraint">唯一</th>
          <th class="sorting" name="validator">校验方式</th>
          <th class="sorting" name="conversionPattern">转换方式</th>
          <th class="sorting" name="multiple">多值</th>
          <th width="50">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.name}</td>
          <td>${item.type}</td>
          <td>${item.readOnly == '1'}</td>
          <td>${item.notNull == '1'}</td>
          <td>${item.uniqueConstraint == '1'}</td>
          <td>${item.validator}</td>
          <td>${item.conversionPattern}</td>
          <td>${item.multiple == '1'}</td>
          <td>
			<region:region-permission permission="userschema:write">
            <a href="user-schema-input.do?id=${item.id}&userRepoId=${param.userRepoId}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>&nbsp;
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
