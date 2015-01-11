<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "cms");%>
<%pageContext.setAttribute("currentMenu", "cms");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>文章列表</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'cmsArticleGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_cmsArticlename': '${param.filter_LIKES_cmsArticlename}',
        'filter_EQI_status': '${param.filter_EQI_status}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'cmsArticleGridForm',
	exportUrl: 'cms-article-export.do'
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
	  <%@include file="/menu/cms.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
			<a class="btn"><i id="cmsArticleSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="cmsArticleSearch" class="content content-inner">

		  <form name="cmsArticleForm" method="post" action="cms-article-list.do" class="form-inline">
		    <label for="cmsArticle_cmsArticlename"><spring:message code='cmsArticle.cmsArticle.list.search.cmsArticlename' text='账号'/>:</label>
		    <input type="text" id="cmsArticle_cmsArticlename" name="filter_LIKES_cmsArticlename" value="${param.filter_LIKES_cmsArticlename}">
		    <label for="cmsArticle_enabled"><spring:message code='cmsArticle.cmsArticle.list.search.status' text='状态'/>:</label>
		    <select id="cmsArticle_enabled" name="filter_EQI_status" class="input-mini">
			  <option value=""></option>
			  <option value="1" ${param.filter_EQI_status == 1 ? 'selected' : ''}><spring:message code='cmsArticle.cmsArticle.list.search.enabled.true' text='启用'/></option>
			  <option value="0" ${param.filter_EQI_status == 0 ? 'selected' : ''}><spring:message code='cmsArticle.cmsArticle.list.search.enabled.false' text='禁用'/></option>
		    </select>
			<button class="btn btn-small" onclick="document.cmsArticleForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="cmsArticle:create">
		  <button class="btn btn-small a-insert" onclick="location.href='cms-article-input.do'">新建</button>
		  <button class="btn btn-small a-insert" onclick="location.href='cms-article-image.do'">新建图库</button>
		  <button class="btn btn-small a-insert" onclick="location.href='cms-article-audio.do'">新建音频</button>
		  <button class="btn btn-small a-insert" onclick="location.href='cms-article-video.do'">新建视频</button>
		  <button class="btn btn-small a-insert" onclick="location.href='cms-article-pdf.do'">新建文档</button>
		  <button class="btn btn-small a-insert" onclick="location.href='cms-article-etc.do'">新建附件</button>
		  </region:region-permission>
		  <region:region-permission permission="cmsArticle:delete">
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
		  <h4 class="title">文章列表</h4>
		</header>
		<div class="content">

<form id="cmsArticleGridForm" name="cmsArticleGridForm" method='post' action="cms-article-remove.do" class="m-form-blank">
  <table id="cmsArticleGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id"><spring:message code="cmsArticle.cmsArticle.list.id" text="编号"/></th>
        <th class="sorting" name="title">标题</th>
        <th class="sorting" name="status">状态</th>
        <th class="sorting" name="userId">作者</th>
        <th class="sorting" name="createTime">创建时间</th>
        <th width="100">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <td>${item.id}</td>
        <td>${item.title}</td>
        <td>${item.status}</td>
        <td><tags:user userId="${item.userId}"/></td>
        <td>${item.createTime}</td>
        <td>
          <a href="cms-article-view.do?id=${item.id}">预览</a>
          <a href="cms-article-publish.do?id=${item.id}">发布</a>
          <a href="cms-article-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
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

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
