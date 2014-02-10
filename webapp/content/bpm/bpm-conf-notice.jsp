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
	exportUrl: 'bpm-category!exportExcel.do'
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

			<a class="btn btn-small" href="bpm-conf-node.do?bpmConfBaseId=${bpmConfBaseId}">返回</a>

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

		  <form name="bpmCategoryForm" method="post" action="bpm-conf-notice!save.do" class="form-inline">
			<input type="hidden" name="bpmConfNodeId" value="${bpmConfNodeId}">
		    <label for="type">类型:</label>
		    <select id="type" name="type" style="width:80px;">
			  <option value="0">到达</option>
			  <option value="1">完成</option>
			  <option value="2">超时</option>
		    </select>
			<label for="receiver">提醒人</label>
			<input type="text" id="receiver" name="receiver" value="" style="width:80px;">
			<label for="dueDate">提醒时间</label>
			<input type="text" id="dueDate" name="dueDate" value="" style="width:80px;">
			<label class="bpmMailTemplateId">邮件模板</label>
			<select id="bpmMailTemplateId" name="bpmMailTemplateId" style="width:120px;">
			  <s:iterator value="bpmMailTemplates" var="item">
			  <option value="${item.id}">${item.name}</option>
			  </s:iterator>
			</select>
			<button class="btn btn-small" onclick="document.bpmCategoryForm.submit()">提交</button>
		  </form>

		</div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">提醒</h4>
		</header>
		<div class="content">

  <form id="bpmCategoryGridForm" name="bpmCategoryGridForm" method='post' action="bpm-task-def-user!save.do" style="margin:0px;">
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
        <s:iterator value="bpmConfNotices" var="item">
        <tr>
		  <td>${item.type == 0 ? '到达' : item.type == 1 ? '完成' : '超时'}</td>
		  <td>${item.receiver}</td>
		  <td>${item.dueDate}</td>
		  <td>${item.bpmMailTemplate.name}</td>
		  <td><a class="btn btn-small" href="bpm-conf-notice!remove.do?id=${item.id}">删除</a></td>
        </tr>
        </s:iterator>
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
