<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "model");%>
<%pageContext.setAttribute("currentMenu", "model");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.model-info.list.title" text="列表"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'model-infoGrid',
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
	gridFormId: 'model-infoGridForm',
	exportUrl: 'model-info-export.do'
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
    <%@include file="/header/model.jsp"%>

    <div class="row-fluid">
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-model">
              <i class="icon-user"></i>
              <span class="title">模型</span>
            </a>
          </div>
          <div id="collapse-model" class="accordion-body collapse in">
            <ul class="accordion-inner nav nav-list">
			  <c:forEach items="${modelInfos}" var="item">
			  <li><a href="${tenantPrefix}/model/list.do?id=${item.id}"><i class="icon-user"></i>${item.name}</a></li>
			  </c:forEach>
            </ul>
          </div>
		</div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->

	  <!-- start of main -->
      <section id="m-main" class="span10">

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
