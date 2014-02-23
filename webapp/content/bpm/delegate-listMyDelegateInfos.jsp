<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-delegate");%>
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
        <th class="sorting" name="key">委托人</th>
        <th class="sorting" name="name">被委托人</th>
        <th class="sorting" name="category">开始时间</th>
        <th class="sorting" name="version">结束时间</th>
        <th class="sorting" name="description">流程定义</th>
        <th class="sorting" name="suspended">状态</th>
        <th width="150">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${bpmDelegateInfos}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
	    <td>${item.id}</td>
	    <td><tags:user userId="${item.assignee}"/></td>
	    <td><tags:user userId="${item.attorney}"/></td>
	    <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd"/></td>
	    <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd"/></td>
	    <td>${item.processDefinitionId}</td>
	    <td>${item.status == 1 ? '有效' : '无效'}</td>
	    <td><a href="delegate-removeDelegateInfo.do?id=${item.id}">删除</a></td>
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
