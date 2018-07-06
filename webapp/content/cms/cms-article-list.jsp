<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "cms");%>
<%pageContext.setAttribute("currentMenu", "cms");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.cms-article.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'cms-articleGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_title': '${param.filter_LIKES_title}',
		'filter_EQL_cmsCatalog.id': '${param["filter_EQL_cmsCatalog.id"]}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'cms-articleGridForm',
	exportUrl: 'cms-article-export.do'
};

var table;


var setting = {
	async: {
		enable: true,
		url: "${tenantPrefix}/cms/rs/catalog/tree.do"
	},
	callback: {
		onClick: function(event, treeId, treeNode) {
			location.href = '${tenantPrefix}/cms/cms-article-list.do?filter_EQL_cmsCatalog.id=' + treeNode.id;
		}
	}
};

var zNodes = [];

$(function(){
	$.fn.zTree.init($("#treeMenu"), setting, zNodes);
});

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
	<style type="text/css">
	  .ztree * {
	  	font-size: 14px;
	  }
	</style>
  </head>

  <body>
    <%@include file="/header/cms.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/cms.jsp"%>

	  <div class="col-md-2" style="padding-top:65px;">
	    <div class="panel panel-default">
		  <div class="panel-heading">
		    <i class="glyphicon glyphicon-list"></i>
		    栏目
		  </div>
		  <div class="panel-body" style="padding:15px 0px;">
			<ul id="treeMenu" class="ztree"></ul>
		  </div>
		</div>
	  </div>

	  <!-- start of main -->
      <section id="m-main" class="col-md-8" style="padding-top:65px;">

		  <form name="cms-articleForm" method="post" action="cms-article-list.do" class="form-inline" style="padding-bottom:15px;">
		    <input type="hidden" name="filter_EQL_cmsCatalog.id" value="${param["filter_EQL_cmsCatalog.id"]}">
		    <label for="cms-article_name">标题:</label>
		    <input type="text" id="cms-article_name" name="filter_LIKES_title" value="${param.filter_LIKES_title}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.cms-articleForm.submit()">查询</button>&nbsp;
		  </form>

      <div style="margin-bottom: 15px;">

		<div class="btn-group">
		  <a type="button" class="btn btn-default" href="cms-article-input.do?catalogId=${param['filter_EQL_cmsCatalog.id']}">新建</a>
		  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			<span class="caret"></span>
			<span class="sr-only">下拉</span>
		  </button>
		  <ul class="dropdown-menu">
			<li><a href='cms-article-image.do'>新建图库</a></li>
			<li><a href='cms-article-audio.do'>新建音频</a></li>
			<li><a href='cms-article-video.do'>新建视频</a></li>
			<li><a href='cms-article-pdf.do'>新建文档</a></li>
			<li><a href='cms-article-etc.do'>新建附件</a></li>
		  </ul>
		</div>

		<button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
	    <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>

		<div class="pull-right">
		  每页显示
		  <select class="m-page-size form-control" style="display:inline;width:auto;">
		    <option value="10">10</option>
		    <option value="20">20</option>
		    <option value="50">50</option>
		  </select>
		  条
        </div>

	    <div class="clearfix"></div>
	  </div>

<form id="cms-articleGridForm" name="cms-articleGridForm" method='post' action="cms-article-remove.do" class="m-form-blank">
      <div class="panel panel-default">

  <table id="cmsArticleGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="title">标题</th>
        <th class="sorting" name="userId">发布者</th>
        <th class="sorting" name="publishTime">发布时间</th>
        <th class="sorting" name="status">状态</th>
        <th width="110">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.title}</td>
        <td><tags:user userId="${item.userId}"/></td>
        <td><fmt:formatDate value="${item.publishTime}" type="both"/></td>
        <td>
		  <c:if test="${item.status == 1}">
		    发布
		  </c:if>
		</td>
        <td>
          <a href="cms-article-view.do?id=${item.id}">预览</a>
		  <c:if test="${item.status == 1}">
          <a href="cms-article-withdraw.do?id=${item.id}">下线</a>
		  </c:if>
		  <c:if test="${item.status != 1}">
          <a href="cms-article-publish.do?id=${item.id}">发布</a>
		  </c:if>
          <a href="cms-article-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>


      </div>
</form>

	  <div>
	    <div class="m-page-info pull-left">
		  共100条记录 显示1到10条记录
		</div>

		<div class="btn-group m-pagination pull-right">
		  <button class="btn btn-default">&lt;</button>
		  <button class="btn btn-default">1</button>
		  <button class="btn btn-default">&gt;</button>
		</div>

	    <div class="clearfix"></div>
      </div>

      <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

