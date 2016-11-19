<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "my");%>
<%pageContext.setAttribute("currentMenu", "my");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s3.jsp"%>

  </head>

  <body>
    <%@include file="/header/my.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/my.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <article class="panel panel-default">
        <header class="panel-heading">
		  <spring:message code="user.user.input.title" text="编辑用户"/>
		</header>
		<div class="panel-body">

  <div class="control-group">
    <label class="control-label" for="userBase_avatar">头像</label>
	<div class="controls">
	  <div id="avatarImage">
		<img id="target" src="my-avatar-view.do">
	  </div>
    </div>
  </div>

		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
