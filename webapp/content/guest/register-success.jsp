<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>注册</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/guest.jsp"%>

    <div class="row-fluid">

	<!-- start of main -->
    <section id="m-main" class="span12">

	  <div class="alert m-alert-info">
		<button type="button" class="close" data-dismiss="alert" style="margin-right:30px;">×</button>
		<strong>注册成功</strong>
	  </div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
