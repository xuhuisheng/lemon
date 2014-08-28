<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "msg");%>
<%pageContext.setAttribute("currentMenu", "msg");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>查看</title>
    <%@include file="/common/s.jsp"%>
  </head>

  <body>
    <%@include file="/header/msg-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/msg-info.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">${model.name}</h4>
		</header>

		<div class="content content-inner">

		  <p><span class="label"><tags:user userId="${model.senderId}"/></span> <span class="label"><fmt:formatDate value="${model.createTime}" type="both"/></span></p>

		  <p>${model.content}</p>

        </div>
      </article>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
