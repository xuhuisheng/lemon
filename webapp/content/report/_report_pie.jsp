<%@page contentType="text/html;charset=UTF-8"%>
		  <div id="pie-${reportInfo.code}" style="height:300px;"></div>
	<script type="text/javascript">

function drawPie() {
  var data = [
<c:forEach items="${data}" var="item" varStatus="status">
	["${item[metaData.name]}", ${item[metaData.count]}]<c:if test="${not status.last}">,</c:if>
</c:forEach>
  ];
  var plot1 = jQuery.jqplot ('pie-${reportInfo.code}', [data],
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

