<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "forum");%>
<%pageContext.setAttribute("currentMenu", "forum");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.cal-info.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'cal-infoGrid',
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
	gridFormId: 'cal-infoGridForm',
	exportUrl: 'cal-info-export.do'
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
    <%@include file="/header/forum.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/forum.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

	  <article class="m-blank">
		<button class="btn btn-small a-insert" onclick="location.href='forum-topic-create.do'">新帖</button>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="cal-info.cal-info.list.title" text="列表"/></h4>
		</header>
        <div class="content">

        <table class="m-table">
            <thead>
                <tr>
                    <th>标题</th>
                    <th>回复数</th>
                    <th>作者</th>
                    <th>点击</th>
                    <th>最后回复时间</th>
                    <th>最后回复作者</th>
                </tr>
            </thead>
            <tbody>
<c:forEach var="item" items="${forumTopics}" varStatus="status">
                <tr class="${status.index % 2 != 0 ? 'odd' : 'even'}">
                    <td><a href="${scopePrefix}/forum/forum-post-view.do?id=${item.id}">${item.title}</a></td>
                    <td>${item.postCount}</td>
                    <td>${item.userId}</td>
                    <td>${item.hitCount}</td>
                    <td>${item.updateTime}</td>
                </tr>
</c:forEach>
            </tbody>
        </table>

        </div>
      </article>

      <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
