<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
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
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <th class="sorting" name="id">编号</th>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="createTime">创建时间</th>
        <th class="sorting" name="assignee">负责人</th>
        <th width="170">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${tasks}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
	    <td>${item.id}</td>
	    <td>${item.name}</td>
	    <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	    <td><tags:user userId="${item.assignee}"/></td>
        <td>
          <a href="workspace-prepareCompleteTask.do?taskId=${item.id}">完成</a>
		  <c:if test="${assignee==null}">
          <a href="workspace-claimTask.do?taskId=${item.id}">认领</a>
		  </c:if>
		  <c:if test="${owner==null}">
          <a href="workspace-delegateTask.do?taskId=${item.id}">代理</a>
		  </c:if>
		  <c:if test="${assignee != null && owner != null}">
          <a href="workspace-resolveTask.do?taskId=${item.id}">处理</a>
		  </c:if>
          <a href="workspace-viewHistory.do?processInstanceId=${item.processInstanceId}">历史</a>
          <a href="console-prepareJump.do?executionId=${item.executionId}">自由跳转</a>
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
