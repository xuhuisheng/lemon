<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group-sys");%>
<%pageContext.setAttribute("currentMenu", "job");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.job.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
/*
var config = {
    id: 'jobGrid',
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
	gridFormId: 'jobGridForm',
	exportUrl: 'job-export.do'
};

var table;

$(function() {
	table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
*/
    </script>
  </head>

  <body>
    <%@include file="/header/org-sys.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/org-sys.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  职位矩阵
		</div>


<table class="table">
  <thead>
    <tr>
	  <th>职等</th>
	  <th>职级</th>
	  <c:forEach items="${jobTypes}" var="item">
      <th>${item.name}</th>
	  </c:forEach>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${list}" var="map">
    <tr>
	  <c:if test="${map.printJobGrade}">
      <td rowspan="${map.jobGradeSize}" style="vertical-align:middle">${map.jobGrade.name}</td>
	  </c:if>
	  <td>${map.jobLevel.name}</td>
	  <c:forEach items="${map.jobInfos}" var="jobInfo">
	  <td>${jobInfo.jobTitle.name}&nbsp;</td>
	  </c:forEach>
	</tr>
	</c:forEach>
  </tbody>
</table>

      </div>


      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

