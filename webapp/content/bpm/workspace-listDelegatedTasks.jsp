<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-task");%>
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
        <th class="sorting" name="suspended">状态</th>
        <th width="170">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <s:iterator value="tasks" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
	    <td>${item.id}</td>
	    <td>${item.name}</td>
	    <td>${item.createTime}</td>
	    <td>${item.assignee}</td>
	    <td>${item.suspended ? '挂起' : '激活'}</td>
        <td>
          <a href="workspace!viewHistory.do?processInstanceId=${item.processInstanceId}">历史</a>
        </td>
      </tr>
      </s:iterator>
    </tbody>
  </table>
        </div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
