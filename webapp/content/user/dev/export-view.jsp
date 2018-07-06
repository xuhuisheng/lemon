<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.list.title" text="用户列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">

    </script>
  </head>

  <body>
    <%@include file="/header/user.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/user.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">



      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  批量导出
		</div>

		<div class="panel-body">

  <div class="form-group">
    <label class="control-label col-md-1" for="text">信息</label>
	<div class="col-sm-6">
	  <textarea id="text" name="text" class="form-control required" rows="10">
"USERNAME","DISPLAY_NAME","CELL_PHONE" ,"EMAIL"<c:forEach var="item" items="${userDtos}">
"${item.username}","${item.displayName}","${item.mobile}","${item.email}"</c:forEach>
	  </textarea>
    </div>
  </div>

		</div>
      </div>

      </section>
	<!-- end of main -->
	</div>

  </body>

</html>
