<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "pim-schedule");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>PIM</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'pimRemindGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_content': '${param.filter_LIKES_content}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'pimRemindGridForm',
	exportUrl: 'pim-schedule-export.do'
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
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid">

	  <!-- left start -->
	  <div class="col-md-2" style="padding-top:65px;">
	    <div class="panel panel-default">
		  <ul class="list-group">
		    <a class="list-group-item" href="index.do">今天</a>
		    <a class="list-group-item" href="pim-schedule-list.do">日程</a>
		    <a class="list-group-item" href="pim-task-index.do">任务</a>
		    <a class="list-group-item" href="pim-note-list.do">备忘</a>
		    <a class="list-group-item" href="address-list-list.do">通讯录</a>
		    <a class="list-group-item" href="../javamail/index.do">邮件</a>
		    <a class="list-group-item" href="../msg/msg-info-listReceived.do">私信</a>
		    <a class="list-group-item" href="pim-plan-list.do">计划</a>
		  </ul>
		</div>
	  </div>
	  <!-- left end -->

	  <!-- right start -->
	  <div class="col-md-10" style="padding-top:65px;">

	    <div class="row">
		  <div class="col-md-12" style="padding-bottom:10px;">
		    <a class="btn btn-default" href="pim-schedule-input.do">
			  <i class="glyphicon glyphicon-plus"></i>
			  添加新日程
			</a>
		    <a class="btn btn-default" href="pim-task-index.do">
			  <i class="glyphicon glyphicon-plus"></i>
			  添加新任务
			</a>
		  </div>
		</div>

        <div class="row">

		  <div class="col-md-4">

			<div class="panel panel-default">
			  <div class="panel-heading">
			    日程
				<div class="pull-right">
				  <fmt:formatDate value="${today}" type="date"/>
				</div>
			  </div>
			  <c:if test="${not empty todaySchedules}">
			  <ul class="list-group">
			    <c:forEach items="${todaySchedules}" var="item">
			    <a class="list-group-item" href="pim-schedule-input.do?id=${item.id}">
				  <fmt:formatDate value="${item.startTime}" pattern="HH:mm"/>
				  -
				  <fmt:formatDate value="${item.endTime}" pattern="HH:mm"/>
				  ${item.name}
				</a>
				</c:forEach>
			  </ul>
			  </c:if>
			  <c:if test="${empty todaySchedules}">
			  <div class="panel-body">
			    无
			  </div>
			  </c:if>
		    </div>

			<div class="panel panel-default">
			  <div class="panel-heading">
			    明天日程
				<div class="pull-right">
				  <fmt:formatDate value="${tomorrow}" type="date"/>
				</div>
			  </div>
			  <c:if test="${not empty tomorrowSchedules}">
			  <ul class="list-group">
			    <c:forEach items="${tomorrowSchedules}" var="item">
			    <a class="list-group-item" href="pim-schedule-input.do?id=${item.id}">
				  <fmt:formatDate value="${item.startTime}" pattern="HH:mm"/>
				  -
				  <fmt:formatDate value="${item.endTime}" pattern="HH:mm"/>
				  ${item.name}
				</a>
				</c:forEach>
			  </ul>
			  </c:if>
			  <c:if test="${empty tomorrowSchedules}">
			  <div class="panel-body">
			    无
			  </div>
			  </c:if>
		    </div>

			<div class="panel panel-default">
			  <div class="panel-heading">
			    七天内日程
			  </div>
			  <c:if test="${not empty nextWeekSchedules}">
			  <ul class="list-group">
			    <c:forEach items="${nextWeekSchedules}" var="item">
			    <a class="list-group-item" href="pim-schedule-input.do?id=${item.id}">
				  <fmt:formatDate value="${item.startTime}" pattern="HH:mm"/>
				  -
				  <fmt:formatDate value="${item.endTime}" pattern="HH:mm"/>
				  ${item.name}
				</a>
				</c:forEach>
			  </ul>
			  </c:if>
			  <c:if test="${empty nextWeekSchedules}">
			  <div class="panel-body">
			    无
			  </div>
			  </c:if>
		    </div>

	      </div>

	      <div class="col-md-4">

	        <div class="panel panel-default">
			  <div class="panel-heading">
			    任务
				<div class="pull-right">
				  <fmt:formatDate value="${today}" type="date"/>
				</div>
			  </div>
			  <c:if test="${not empty tasks}">
			  <ul class="list-group">
			    <c:forEach items="${tasks}" var="item">
			    <a class="list-group-item" href="pim-task-input.do?id=${item.id}">
				  ${item.name}
				</a>
				</c:forEach>
			  </ul>
			  </c:if>
			  <c:if test="${empty tasks}">
			  <div class="panel-body">
			    无
			  </div>
			  </c:if>
		    </div>

	      </div>

	      <div class="col-md-4">

	        <div class="panel panel-default">
			  <div class="panel-heading">
			    计划
				<div class="pull-right">
				  <a href="pim-plan-input.do"><i class="glyphicon glyphicon-plus"></i></a>
				</div>
			  </div>
			  <c:if test="${not empty plans}">
			  <ul class="list-group">
			    <c:forEach items="${plans}" var="item">
			    <a class="list-group-item" href="pim-plan-input.do?id=${item.id}">
				  ${item.name}
				</a>
				</c:forEach>
			  </ul>
			  </c:if>
			  <c:if test="${empty plans}">
			  <div class="panel-body">
			    无
			  </div>
			  </c:if>
		    </div>

	        <div class="panel panel-default">
			  <div class="panel-heading">
			    备忘
				<div class="pull-right">
				  <a href="pim-note-input.do"><i class="glyphicon glyphicon-plus"></i></a>
				</div>
			  </div>
			  <c:if test="${not empty notes}">
			  <ul class="list-group">
			    <c:forEach items="${notes}" var="item">
			    <a class="list-group-item" href="pim-note-input.do?id=${item.id}">
				  ${item.content}
				</a>
				</c:forEach>
			  </ul>
			  </c:if>
			  <c:if test="${empty notes}">
			  <div class="panel-body">
			    无
			  </div>
			  </c:if>
		    </div>

	        <div class="panel panel-default">
			  <div class="panel-heading">
			    提醒
				<div class="pull-right">
				  <a href="pim-remind-input.do"><i class="glyphicon glyphicon-plus"></i></a>
				</div>
			  </div>
			  <c:if test="${not empty reminds}">
			  <ul class="list-group">
			    <c:forEach items="${reminds}" var="item">
			    <a class="list-group-item" href="pim-remind-input.do?id=${item.id}">
				  ${item.description}
				</a>
				</c:forEach>
			  </ul>
			  </c:if>
			  <c:if test="${empty reminds}">
			  <div class="panel-body">
			    无
			  </div>
			  </c:if>
		    </div>

		  </div>
        
		</div>
	  </div>
	  <!-- right end -->

	</div>

  </body>

</html>
