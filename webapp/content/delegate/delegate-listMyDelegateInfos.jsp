<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "bpm-delegate");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/bpm-workspace3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='delegate-prepareAutoDelegate.do'">新建</button>
		</div>

	    <div class="clearfix"></div>
	  </div>
	  
<form id="pimRemindGridForm" name="pimRemindGridForm" method='post' action="pim-note-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  列表
		</div>
  <table id="pimRemindGrid" class="table table-hover">
    <thead>
      <tr>
	    <%--
        <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
		--%>
        <th class="sorting" name="key">&nbsp;委托人</th>
        <th class="sorting" name="name">被委托人</th>
        <th class="sorting" name="category">开始时间</th>
        <th class="sorting" name="version">结束时间</th>
        <th class="sorting" name="description">流程定义</th>
        <th class="sorting" name="description">任务</th>
        <th class="sorting" name="suspended">状态</th>
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${delegateInfos}" var="item">
      <tr>
	    <%--
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
		--%>
	    <td>&nbsp;<tags:user userId="${item.assignee}"/></td>
	    <td><tags:user userId="${item.attorney}"/></td>
	    <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd"/></td>
	    <td><fmt:formatDate value="${item.endTime}" pattern="yyyy-MM-dd"/></td>
	    <td>${item.processDefinitionId}</td>
	    <td>${item.taskDefinitionKey}</td>
	    <td>${item.status == 1 ? '有效' : '无效'}</td>
	    <td><a href="delegate-removeDelegateInfo.do?id=${item.id}">删除</a></td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
      </div>
</form>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
