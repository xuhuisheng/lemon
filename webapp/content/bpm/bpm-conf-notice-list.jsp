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

		  <form name="bpmCategoryForm" method="get" action="bpm-conf-notice-input.do" class="form-inline">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
			<button class="btn btn-small" onclick="document.bpmCategoryForm.submit()">新增</button>
		  </form>



		</div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">提醒</h4>
		</header>
		<div class="content">

  <form id="bpmCategoryGridForm" name="bpmCategoryGridForm" method='post' action="bpm-conf-notice-remove.do" style="margin:0px;">
    <input type="hidden" name="bpmTaskDefId" value="${bpmTaskDefId}">
    <table id="bpmCategoryGrid" class="m-table table-hover">
      <thead>
        <tr>
		  <th width="20%">类型</th>
		  <th width="20%">提醒人</th>
		  <th width="20%">提醒时间</th>
		  <th>邮件模板</th>
		  <th width="10%">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${bpmConfNotices}" var="item">
        <tr>
		  <td>${item.type == 0 ? '到达' : item.type == 1 ? '完成' : '超时'}</td>
		  <td>${item.receiver}</td>
		  <td>${item.dueDate}</td>
		  <td>${item.templateCode}</td>
		  <td><a class="btn btn-small" href="bpm-conf-notice-remove.do?id=${item.id}">删除</a></td>
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
