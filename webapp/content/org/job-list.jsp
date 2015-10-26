<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "group-sys");%>
<%pageContext.setAttribute("currentMenu", "job");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>职位矩阵</title>
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
    <%@include file="/header/org-sys.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/org-sys.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">职位矩阵</h4>
		</header>

		<div class="content content-inner">

<table class="table">
  <thead>
    <tr>
	  <th>职等</th>
	  <th>职级</th>
	  <c:forEach items="${jobTypes}" var="item">
      <th>${item.name}</th>
	  </c:forEach>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${list}" var="map">
    <tr>
	  <c:if test="${map.printJobGrade}">
      <td rowspan="${map.jobGradeSize}" style="vertical-align:middle">${map.jobGrade.name}</td>
	  </c:if>
	  <td>${map.jobLevel.name}</td>
	  <c:forEach items="${map.jobInfos}" var="jobInfo">
	  <td>${jobInfo.jobTitle.name}&nbsp;</td>
	  </c:forEach>
	</tr>
	</c:forEach>
  </tbody>
</table>

        </div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
