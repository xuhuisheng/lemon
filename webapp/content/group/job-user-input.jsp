<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group-sys");%>
<%pageContext.setAttribute("currentMenu", "job");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑人员职位</title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#orgForm").validate({
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
    <%@include file="/header/group-sys.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/group-sys.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑人员职位</h4>
		</header>

		<div class="content content-inner">

<form id="orgForm" method="post" action="job-user-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="org_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="org_orgname">人员</label>
	<div class="controls">
	  <input id="org_orgname" type="text" name="userRef" value="${model.userRef}" size="40" class="text required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="job_level">职位</label>
	<div class="controls">
	  <select id="job_level" name="jobId">
	  <c:forEach items="${jobInfos}" var="item">
	    <option value="${item.id}" ${item.id==model.jobInfo.id ? 'selected' : ''}>${item.jobTitle.name}${item.jobLevel.jobGrade.name}${item.jobLevel.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
