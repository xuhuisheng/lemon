<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scope");%>
<%pageContext.setAttribute("currentMenu", "workcal");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>工作日历规则列表</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'workcalType',
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
	gridFormId: 'workcalType_form',
	exportUrl: 'workcal-rule-export.do'
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
    <%@include file="/header/scope.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/workcal.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="workcalType_searchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="workcalType_searchIcon" class="content content-inner">

		  <form name="workcalType_form" method="post" action="workcal-rule-list.do" class="form-inline">
		    <label for="workcalType_name"><spring:message code='scope-global.scope-global.list.search.name' text='名称'/>:</label>
		    <input type="text" id="workcalType_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.workcalType_form.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="scope-global:create">
		  <button class="btn btn-small a-insert" onclick="location.href='workcal-rule-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="scope-global:delete">
		  <button class="btn btn-small a-remove" onclick="table.removeAll()">删除</button>
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
		  <h4 class="title"><spring:message code="scope-global.scope-global.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="workcalType_form" name="workcalType_form" method='post' action="workcal-rule-remove.do" class="m-form-blank">
  <table id="workcalType_grid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="year">年度</th>
        <th class="sorting">周</th>
        <th class="sorting">名称</th>
        <th class="sorting">日期</th>
        <th class="sorting">状态</th>
        <th class="sorting">类型</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.year}</td>
        <td>
          <c:if test="${item.week==1}">周日</c:if>
          <c:if test="${item.week==2}">周一</c:if>
          <c:if test="${item.week==3}">周二</c:if>
          <c:if test="${item.week==4}">周三</c:if>
          <c:if test="${item.week==5}">周四</c:if>
          <c:if test="${item.week==6}">周五</c:if>
          <c:if test="${item.week==7}">周六</c:if>
		</td>
        <td>${item.name}</td>
        <td><fmt:formatDate type="date" value="${item.workDate}"/></td>
        <td>
          <c:if test="${item.status==0}">规则</c:if>
          <c:if test="${item.status==1}">节假日</c:if>
          <c:if test="${item.status==2}">调休</c:if>
          <c:if test="${item.status==3}">补休</c:if>
		</td>
        <td>${item.workcalType.name}</td>
        <td>
          <a class="a-update" href="workcal-rule-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
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
