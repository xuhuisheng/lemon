<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group-sys");%>
<%pageContext.setAttribute("currentMenu", "job");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#job-infoForm").validate({
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
    <%@include file="/header/org-sys.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/org-sys.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="orgForm" method="post" action="job-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="org_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="job_title">职务名称</label>
	<div class="col-sm-5">
	  <select id="job_title" name="jobTitleId">
	  <c:forEach items="${jobTitles}" var="item">
	    <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="job_level">职级</label>
	<div class="col-sm-5">
	  <select id="job_level" name="jobLevelId">
	  <c:forEach items="${jobLevels}" var="item">
	    <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="job_type">职称分类</label>
	<div class="col-sm-5">
	  <select id="job_type" name="jobTypeId">
	  <c:forEach items="${jobTypes}" var="item">
	    <option value="${item.id}">${item.name}</option>
	  </c:forEach>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-5">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>

		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>

