<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "scope");%>
<%pageContext.setAttribute("currentMenu", "workcal");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>工作日历</title>
    <%@include file="/common/s.jsp"%>

	<script type="text/javascript" src="${ctx}/s/mossle/js/WorkCalendar.js"></script>
    <script type="text/javascript">
$(function() {
	var workCalendar = new WorkCalendar(${param.year});
	workCalendar.render('#m-main');
	workCalendar.activeByWeek(${weeks});
	workCalendar.markHolidays(${holidays});
	workCalendar.markWorkdays(${workdays});
	workCalendar.markExtrdays(${extrdays});
});
    </script>
  </head>

  <body>
    <%@include file="/header/scope.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/workcal.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
