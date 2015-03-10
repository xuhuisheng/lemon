<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "online");%>
<%pageContext.setAttribute("currentMenu", "online");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.cal-info.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'cal-infoGrid',
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
	gridFormId: 'cal-infoGridForm',
	exportUrl: 'cal-info-export.do'
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
    <%@include file="/header/online.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/online.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">
<%--
	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="cal-infoSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="cal-infoSearch" class="content content-inner">

		  <form name="cal-infoForm" method="post" action="cal-info-list.do" class="form-inline">
		    <label for="cal-info_name"><spring:message code='cal-info.cal-info.list.search.name' text='名称'/>:</label>
		    <input type="text" id="cal-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small a-search" onclick="document.cal-infoForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </article>
--%>
	  <article class="m-blank">
	    <div class="pull-left">
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
		  <h4 class="title"><spring:message code="cal-info.cal-info.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="cal-infoGridForm" name="cal-infoGridForm" method='post' action="cal-info-remove.do" class="m-form-blank">
  <table id="cal-infoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="id"><spring:message code="cal-info.cal-info.list.id" text="编号"/></th>
        <th class="sorting" name="name">用户</th>
        <th class="sorting" name="name">session</th>
        <th class="sorting" name="name">登录时间</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td>${item.id}</td>
        <td><tags:user userId="${item.account}"/></td>
        <td>${item.sessionId}</td>
        <td>${item.loginTime}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
</form>
        </div>
      </article>

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
