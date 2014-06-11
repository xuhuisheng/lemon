<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "cms");%>
<%pageContext.setAttribute("currentMenu", "cms");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>首页</title>
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
<c:forEach items="${cmsCatalogs}" var="cmsCatalog">
      <article class="m-widget span10 offset1">
        <header class="header">
		  <h4 class="title"><a href="${scopePrefix}/cms/catalog.do?id=${cmsCatalog.id}">${cmsCatalog.name}</a></h4>
		</header>
		<div class="content">
		  <c:forEach items="${cmsCatalog.cmsArticles}" var="cmsArticle">
		    <div>
			  <div style="font-size:24px;"><strong><a href="${scopePrefix}/cms/article.do?id=${cmsArticle.id}">${cmsArticle.title}</strong></div>
			  <p>${cmsArticle.content}</p>
			</div>
		  </c:forEach>
		</div>
	  </article>
</c:forEach>

  </body>

</html>
