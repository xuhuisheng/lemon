<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "attendance");%>
<%pageContext.setAttribute("currentMenu", "attendance");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.attendance-info.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/attendance-user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/attendance-user.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

        <div class="col-md-4">
	    <div class="panel panel-default">
		  <div class="panel-heading">
		    考勤
		  </div>
		  <div class="panel-body">
		    <div class="text-center">
		      <div style="font-size:36px;" id="attendenceTime">8:20:00</div>
			  <div id="attendenceDate">2018年5月6日(星期日)</div>
			  <div>
			    <c:if test="${empty attendanceDto.upTime}">
			    <a class="btn btn-success" href="record">上班打卡</a>
				</c:if>
			    <c:if test="${not empty attendanceDto.upTime}">
			    <c:if test="${empty attendanceDto.downTime}">
			    <a class="btn btn-success" href="record">下班打卡</a>
				</c:if>
			    <c:if test="${not empty attendanceDto.downTime}">
			    <a class="btn btn-success" href="record">更新打卡</a>
				</c:if>
				</c:if>
			  </div>
			  <c:if test="${not empty attendanceDto.upTime}">
			  <div>
			    上班打卡：<fmt:formatDate value="${attendanceDto.upTime}" type="both"/>
			  </div>
			  </c:if>
			  <c:if test="${not empty attendanceDto.downTime}">
			  <div>
			    下班打卡：<fmt:formatDate value="${attendanceDto.downTime}" type="both"/>
			  </div>
			  </c:if>
		    </div>
			<script>
  var week = {
   "0" : "日",
   "1" : "一",
   "2" : "二",
   "3" : "三",
   "4" : "四",
   "5" : "五",
   "6" : "六",
  }
function updateAttendenceTime() {
	var now = new Date();
    $('#attendenceTime').text(
        now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds()
    );
    $('#attendenceDate').text(
        now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate() + "日(星期" + week[now.getDay()] + ")"
    );
}

updateAttendenceTime();

setInterval(updateAttendenceTime, 1000);
			</script>
		  </div>
		</div>
		</div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

