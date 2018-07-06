<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "report");%>
<%pageContext.setAttribute("currentMenu", "chart");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>${reportInfo.name}</title>
    <%@include file="/common/s3.jsp"%>

    <link rel="stylesheet" href="${cdnPrefix}/public/jqplot/1.0.9/jquery.jqplot.min.css" type="text/css" media="screen" />
    <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="${cdnPrefix}/public/jqplot/1.0.9/excanvas.min.js"></script><![endif]-->
	<script type="text/javascript" src="${cdnPrefix}/public/jqplot/1.0.9/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/jqplot/1.0.9/plugins/jqplot.barRenderer.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/jqplot/1.0.9/plugins/jqplot.categoryAxisRenderer.js"></script>
    <script type="text/javascript" src="${cdnPrefix}/public/jqplot/1.0.9/plugins/jqplot.pointLabels.js"></script>
	<script type="text/javascript" src="${cdnPrefix}/public/jqplot/1.0.9/plugins/jqplot.pieRenderer.js"></script>
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
		  ${reportInfo.name}
		</div>
		<div class="panel-body">
		  <c:if test="${reportInfo.type=='pie'}">
		    <%@include file="_report_pie.jsp"%>
		  </c:if>
		  <c:if test="${reportInfo.type=='bar'}">
		    <%@include file="_report_bar.jsp"%>
		  </c:if>
		</div>
	  </div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
