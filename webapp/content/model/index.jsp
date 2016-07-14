<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "model");%>
<%pageContext.setAttribute("currentMenu", "model");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.model-info.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
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

<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-model" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-model" aria-expanded="true" aria-controls="collapse-body-model">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
	    <span class="title">模型</span>
      </h4>
    </div>
    <div id="collapse-body-model" class="panel-collapse collapse ${currentMenu == 'model' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-model">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <c:forEach items="${modelInfos}" var="item">
		  <li><a href="${tenantPrefix}/model/list.do?id=${item.id}"><i class="glyphicon glyphicon-list"></i> ${item.name}</a></li>
		  </c:forEach>
        </ul>
      </div>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>
      <!-- end of sidebar -->

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
