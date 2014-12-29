<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "pim");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="用户列表"/></title>
    <%@include file="/common/s.jsp"%>
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
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'userGridForm',
	exportUrl: 'user-base-export.do'
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
    <%@include file="/header/pim.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
			<a class="btn"><i id="userSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="userSearch" class="content content-inner">

		  <form name="userForm" method="post" action="address-list-list.do" class="form-inline">
		    <label for="user_username"><spring:message code='user.user.list.search.username' text='账号'/>:</label>
		    <input type="text" id="user_username" name="username" value="${param.username}">
			<button class="btn btn-small" onclick="document.userForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.list.title" text="用户列表"/></h4>
		</header>
		<div class="content">

		<div style="height:20px;"></div>

      <c:forEach items="${list}" var="item">

	    <table style="padding-bottom:20px;">
		  <tr>
		    <td rowspan="2"><img src="${scopePrefix}/rs/avatar?id=${item.id}&width=80" style="width:80px;height:80px;margin-left:10px;"/></td>
			<td width="20">&nbsp;</td>
			<td align="right">账号: </td>
			<td>${item.username}</td>
			<td width="20">&nbsp;</td>
			<td align="right">显示名: </td>
			<td>${item.displayName}</td>
		  </tr>
		  <tr>
			<td width="20">&nbsp;</td>
			<td align="right">邮箱: </td>
			<td>${item.email}</td>
			<td width="20">&nbsp;</td>
			<td align="right">电话: </td>
			<td>${item.mobile}</td>
		  </tr>
		</table>

		<div style="height:20px;"></div>

      </c:forEach>

		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
