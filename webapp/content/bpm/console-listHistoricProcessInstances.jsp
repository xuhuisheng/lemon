<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "history");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程列表</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10" style="float:right">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">列表</h4>
		</header>
		<div class="content">

  <table id="demoGrid" class="m-table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="name">流程定义</th>
        <th class="sorting" name="name">开始时间</th>
        <th class="sorting" name="name">结束时间</th>
        <th class="sorting" name="name">发起人</th>
        <th width="170">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${historicProcessInstances}" var="item">
      <tr>
	    <td>${item.id}</td>
	    <td>${item.processDefinitionId}</td>
	    <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	    <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	    <td><tags:user userId="${item.startUserId}"/></td>
        <td>
          <a href="console-reopen.do?processInstanceId=${item.id}">重启</a>
          <a href="workspace-viewHistory.do?processInstanceId=${item.id}">历史</a>
          <a href="${scopePrefix}/widgets/diagram-viewer/index.html?processInstanceId=${item.id}&processDefinitionId=${item.processDefinitionId}">diagram-viewer</a>
        </td>
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
