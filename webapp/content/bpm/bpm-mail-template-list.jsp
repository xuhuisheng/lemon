<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="auth.bpmMailTemplate.list.title" text="用户库列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'bpmMailTemplateGrid',
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
	gridFormId: 'bpmMailTemplateGridForm',
	exportUrl: 'bpm-mail-template-export.do'
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
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a href="javascript:$('#bpmMailTemplateSearch').toggle(200);$('#bpmMailTemplateSearchIcon').toggleClass('icon-chevron-down');$('#bpmMailTemplateSearchIcon').toggleClass('icon-chevron-up');void(0);" class="btn"><i id="bpmMailTemplateSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="bpmMailTemplateSearch" class="content content-inner">

		  <form name="bpmMailTemplateForm" method="post" action="bpm-mail-template-list.do" class="form-inline">
		    <label for="bpmMailTemplate_name"><spring:message code="auth.bpmMailTemplate.list.search.name" text="名称"/>:</label>
		    <input type="text" id="bpmMailTemplate_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small" onclick="document.bpmMailTemplateForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article style="margin-bottom:10px;">
	    <div class="pull-left">
		  <region:region-permission permission="bpmMailTemplate:create">
		  <button class="btn btn-small a-insert" onclick="location.href='bpm-mail-template-input.do'"><spring:message code="core.list.create" text="新建"/></button>
		  </region:region-permission>
		  <region:region-permission permission="bpmMailTemplate:delete">
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
		  <h4 class="title">流程分类</h4>
		</header>
		<div class="content">

  <form id="bpmMailTemplateGridForm" name="bpmMailTemplateGridForm" method='post' action="bpm-mail-template-remove.do" style="margin:0px;">
    <table id="bpmMailTemplateGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" style="text-indent:0px;text-align:center;"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="id"><spring:message code="user.bpmMailTemplate.list.id" text="编号"/></th>
          <th class="sorting" name="name"><spring:message code="user.bpmMailTemplate.list.name" text="名称"/></th>
          <th width="100">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td>${item.id}</td>
          <td>${item.name}</td>
          <td>
			<region:region-permission permission="bpmMailTemplate:write">
            <a href="bpm-mail-template-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>&nbsp;
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
