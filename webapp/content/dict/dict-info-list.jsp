<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "dict");%>
<%pageContext.setAttribute("currentMenu", "dict");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.dict-info.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'dict-infoGrid',
    pageNo: 1,
    pageSize: ${fn:length(dictInfos)},
    totalCount: ${fn:length(dictInfos)},
    resultSize: ${fn:length(dictInfos)},
    pageCount: 1,
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: true,
    params: {
        'filter_LIKES_name': '${param.filter_LIKES_name}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'dict-infoGridForm',
	exportUrl: 'dict-info-export.do'
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
    <%@include file="/header/dict.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/dict.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
		    <a class="btn"><i id="dict-infoSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="dict-infoSearch" class="content content-inner">

		  <form name="dict-infoForm" method="post" action="dict-info-list.do" class="form-inline">
		    <label for="dict-info_name"><spring:message code='dict-info.dict-info.list.search.name' text='名称'/>:</label>
		    <input type="text" id="dict-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}">
			<button class="btn btn-small a-search" onclick="document.dict-infoForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="dict-info:create">
		  <button class="btn btn-small a-insert" onclick="location.href='dict-info-input.do?typeId=${param.typeId}'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="dict-info:delete">
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
		  <h4 class="title"><spring:message code="dict-info.dict-info.list.title" text="列表"/></h4>
		</header>
        <div class="content">
<form id="dict-infoGridForm" name="dict-infoGridForm" method='post' action="dict-info-remove.do?typeId=${param.typeId}" class="m-form-blank">
  <table id="dict-infoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="dict-info.dict-info.list.id" text="编号"/></th>
        <th class="sorting" name="name"><spring:message code="dict-info.dict-info.list.name" text="名称"/></th>
        <th class="sorting" name="value">数据</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${dictInfos}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.value}</td>
        <td>
          <a href="dict-info-input.do?id=${item.id}&typeId=${param.typeId}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
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
