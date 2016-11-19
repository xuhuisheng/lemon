<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "report");%>
<%pageContext.setAttribute("currentMenu", "chart");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>最常用流程</title>
    <%@include file="/common/s3.jsp"%>

    <link rel="stylesheet" href="${ctx}/s/jqplot/jquery.jqplot.min.css" type="text/css" media="screen" />
    <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../s/jqplot/excanvas.min.js"></script><![endif]-->
	<script type="text/javascript" src="${ctx}/s/jqplot/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="${ctx}/s/jqplot/plugins/jqplot.barRenderer.min.js"></script>
    <script type="text/javascript" src="${ctx}/s/jqplot/plugins/jqplot.categoryAxisRenderer.min.js"></script>
    <script type="text/javascript" src="${ctx}/s/jqplot/plugins/jqplot.pointLabels.min.js"></script>
	<script type="text/javascript" src="${ctx}/s/jqplot/plugins/jqplot.pieRenderer.min.js"></script>

	<script type="text/javascript">

function drawPie() {
  var data = [
<c:forEach items="${list}" var="item">
	["${item.name}", ${item.c}],
</c:forEach>
	[]
  ];
  var plot1 = jQuery.jqplot ('pie', [data],
    {
      seriesDefaults: {
        // Make this a pie chart.
        renderer: jQuery.jqplot.PieRenderer,
        rendererOptions: {
          // Put data labels on the pie slices.
          // By default, labels show the percentage of the slice.
          showDataLabels: true
        }
      },
      legend: { show:true, location: 'e' }
    }
  );
}

$(function () {
	drawPie();
});
    </script>
  </head>

  <body>
    <%@include file="/header/report.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/report.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

	  <div class="panel panel-default">
        <div class="panel-heading">
		  最活跃流程
		</div>
		<div class="panel-body">
		  <div id="pie" style="height:300px;"></div>
		</div>
	  </div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
