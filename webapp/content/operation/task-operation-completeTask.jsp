<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-workspace");%>
<%pageContext.setAttribute("currentMenu", "bpm-process");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>表单列表</title>
    <%@include file="/common/s3.jsp"%>
  </head>

  <body>
    <%@include file="/header/bpm-workspace3.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/bpm-workspace3.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">
	 
	  <div class="alert alert-info" role="alert">
		<button type="button" class="close" data-dismiss="alert" style="margin-right:30px;">×</button>
		<strong>任务已完成</strong>
	  </div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
