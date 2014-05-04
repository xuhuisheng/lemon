<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.bpmCategory.list.title" text="用户库列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'bpmCategoryGrid',
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
	gridFormId: 'bpmCategoryGridForm',
	exportUrl: 'bpm-category-export.do'
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
    <%@include file="/header/bpm-console.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">返回</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="bpmCategorySearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="bpmCategorySearch" class="content" style="padding:10px;">

			<a class="btn btn-small" href="bpm-conf-node-list.do?bpmConfBaseId=${bpmConfBaseId}">返回</a>

		</div>
	  </article>

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">添加</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="bpmCategorySearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="bpmCategorySearch" class="content content-inner">

		  <form name="bpmCategoryForm" method="post" action="bpm-conf-rule-save.do" class="form-inline">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
		    <label for="value">规则:</label>
		    <input type="text" id="value" name="value" value="">
			<button class="btn btn-small" onclick="document.bpmCategoryForm.submit()">提交</button>
		  </form>

		</div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">规则</h4>
		</header>
		<div class="content">

  <form id="bpmCategoryGridForm" name="bpmCategoryGridForm" method='post' action="bpm-conf-rule-remove.do" style="margin:0px;">
    <input type="hidden" name="bpmTaskDefId" value="${bpmTaskDefId}">
    <table id="bpmCategoryGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" style="text-indent:0px;text-align:center;"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="user.bpmCategory.list.id" text="编号"/></th>
          <th class="sorting" name="name"><spring:message code="user.bpmCategory.list.name" text="名称"/></th>
          <th class="sorting" name="priority">类型</th>
          <th width="100">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${bpmConfRules}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.value}</td>
          <td>
		    <a href="bpm-conf-rule-remove.do?id=${item.id}">删除</a>
          </td>
        </tr>
        </c:forEach>
      </tbody>
    </table>
  </form>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
