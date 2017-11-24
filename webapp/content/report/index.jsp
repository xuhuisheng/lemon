<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "report");%>
<%pageContext.setAttribute("currentMenu", "chart");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>报表</title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/report.jsp"%>

	<div class="row-fluid">
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>

<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <c:forEach items="${list}" var="item">
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-report" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-report" aria-expanded="true" aria-controls="collapse-body-bpm-process">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        ${item.reportSubject.name}
      </h4>
    </div>
    <div id="collapse-body-report" class="panel-collapse collapse ${currentMenu == 'chart' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-report">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <c:forEach items="${item.reportInfos}" var="reportInfo">
	      <li><a href="${tenantPrefix}/report/view.do?code=${reportInfo.code}"><i class="glyphicon glyphicon-list"></i> ${reportInfo.name}</a></li>
		  </c:forEach>
        </ul>
      </div>
    </div>
  </div>
  </c:forEach>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

	  <div class="panel panel-default">
        <div class="panel-heading">
		  报表
		</div>
		<div class="panel-body">
		  <div></div>
		</div>
	  </div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
