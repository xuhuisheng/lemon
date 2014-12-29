<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程列表</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">流程图</h4>
		</header>
        <div id="demoSearch" class="content">

		  <img src="workspace-graphHistoryProcessInstance.do?processInstanceId=${param.processInstanceId}">
		</div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">列表</h4>
		</header>
		<div class="content">

  <table id="demoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="startTime">开始时间</th>
        <th class="sorting" name="endTime">结束时间</th>
        <th class="sorting" name="assignee">负责人</th>
        <th class="sorting" name="deleteReason">处理结果</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${historicTasks}" var="item">
      <tr>
	    <td>${item.id}</td>
	    <td>${item.name}</td>
	    <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	    <td>
		  <tags:user userId="${item.assignee}"/>
		  <c:if test="${not empty item.owner && item.assignee != item.owner}">
		  <b>(原执行人:<tags:user userId="${item.owner}"/>)</b>
		  </c:if>
		</td>
	    <td>${item.deleteReason}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
        </div>
      </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">表单</h4>
		</header>
		<div class="content">

  <table id="demoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="variableName">名称</th>
        <th class="sorting" name="value">值</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${historicVariableInstances}" var="item">
      <tr>
	    <td>${item.variableName}</td>
	    <td>${item.value}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
