<%@page contentType="text/html;charset=UTF-8"%>
		  <div id="bar-${reportInfo.code}" style="height:300px;"></div>
	<script type="text/javascript">

function drawBar() {
  var data = [
<c:forEach items="${data}" var="item" varStatus="status">
	${item[metaData.count]}<c:if test="${not status.last}">,</c:if>
</c:forEach>
  ];

  var ticks = [
<c:forEach items="${data}" var="item" varStatus="status">
	"${item[metaData.name]}"<c:if test="${not status.last}">,</c:if>
</c:forEach>
  ];

  var plot1 = jQuery.jqplot ('bar-${reportInfo.code}', [data],
    {
      seriesDefaults: {
        // Make this a bar chart.
        renderer: jQuery.jqplot.BarRenderer,
        rendererOptions: {
          // Put data labels on the bar slices.
          // By default, labels show the percentage of the slice.
          showDataLabels: true
        }
      },
      legend: { show:true, placement: 'outsideGrid' },
        series:[
            {label:'Value'}
        ],
      axes: {
            // Use a category axis on the x axis and use our custom ticks.
            xaxis: {
                renderer: $.jqplot.CategoryAxisRenderer,
                ticks: ticks
            },
            // Pad the y axis just a little so bars can get close to, but
            // not touch, the grid boundaries.  1.2 is the default padding.
            yaxis: {
                pad: 1.05,
                tickOptions: {formatString: '$%d'}
            }
      }
    }
  );
}

$(function () {
	drawBar();
});
    </script>

