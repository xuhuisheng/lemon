<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "cms");%>
<%pageContext.setAttribute("currentMenu", "cms");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>${cmsArticle.title}</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'cmsCatalogGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_cmsCatalogname': '${param.filter_LIKES_cmsCatalogname}',
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'cmsCatalogGridForm',
	exportUrl: 'cms-catalog-export.do'
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
    <%@include file="/header/cms.jsp"%>

    <div class="row-fluid">

      <article class="span10 offset1">
	    <a href="${scopePrefix}/cms/index.do">首页</a>
		&gt;
		<a href="${scopePrefix}/cms/catalog.do?id=${cmsArticle.cmsCatalog.id}">${cmsArticle.cmsCatalog.name}</a>
	  </article>
    
	</div>

	<div class="row-fluid">

      <article class="m-widget span10 offset1">
        <header class="header">
		  <div class="title">${cmsArticle.title}</div>
		</header>
		<div class="content">
		  <div>${cmsArticle.content}</div>
		</div>
	  </article>

  </body>

</html>
