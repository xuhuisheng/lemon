<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>流程定义</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userRepoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });
})
    </script>
  </head>

  <body>
    <%@include file="/header/bpm-console.jsp"%>

	<div class="row-fluid">
	<%@include file="/menu/bpm-console.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">流程配置</h4>
		</header>
		<div class="content content-inner">
	<table class="table">
      <thead>
        <tr>
          <th>编号</th>
          <th>类型</th>
          <th>节点</th>
          <th>人员</th>
          <th>事件</th>
          <th>规则</th>
          <th>表单</th>
          <th>操作</th>
          <th>提醒</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${bpmConfNodes}" var="item">
        <tr>
          <td>${item.id}</td>
		  <td>${item.type}</td>
          <td>${item.name}</td>
          <td>
		    <c:if test="${item.confUser == 0}">
			  <a href="bpm-conf-user-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="icon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confUser == 1}">
			  <a href="bpm-conf-user-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="icon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfUsers}">
              <i class="badge">${fn:length(item.bpmConfUsers)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confListener == 0}">
			  <a href="bpm-conf-listener-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="icon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confListener == 1}">
			  <a href="bpm-conf-listener-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="icon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfListeners}">
              <i class="badge">${fn:length(item.bpmConfListeners)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confRule == 0}">
			  <a href="bpm-conf-rule-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="icon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confRule == 1}">
			  <a href="bpm-conf-rule-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="icon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfRules}">
              <i class="badge">${fn:length(item.bpmConfRules)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confForm == 0}">
			  <a href="bpm-conf-form-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="icon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confForm == 1}">
			  <a href="bpm-conf-form-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="icon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfForms}">
              <i class="badge">${fn:length(item.bpmConfForms)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confOperation == 0}">
			  <a href="bpm-conf-operation-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="icon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confOperation == 1}">
			  <a href="bpm-conf-operation-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="icon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfOperations}">
              <i class="badge">${fn:length(item.bpmConfOperations)}</i>
			</c:if>
			&nbsp;
	      </td>
          <td>
		    <c:if test="${item.confNotice == 0}">
			  <a href="bpm-conf-notice-list.do?bpmConfNodeId=${item.id}" class="btn"><i class="icon-edit"></i></a>
			</c:if>
		    <c:if test="${item.confNotice == 1}">
			  <a href="bpm-conf-notice-list.do?bpmConfNodeId=${item.id}" class="btn btn-primary"><i class="icon-edit"></i></a>
			</c:if>
			<c:if test="${not empty item.bpmConfNotices}">
              <i class="badge">${fn:length(item.bpmConfNotices)}</i>
			</c:if>
			&nbsp;
	      </td>
        </tr>
        </c:forEach>
      </tbody>
	  </tbody>
	</table>
		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
