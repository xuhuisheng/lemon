<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#user-roleForm").validate({
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
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/auth.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">


<form id="userForm2" method="post" action="user-role-save.do" class="form-horizontal">
  <input type="hidden" name="id" value="${id}">
  <div class="form-group">
    <div class="col-sm-5">
	  <h5>local</h5>
	  <c:forEach items="${roles}" var="item">
        <input id="selectedItem-${item.id}" type="checkbox" name="selectedItem" value="${item.id}" <tags:contains items="${userRoleIds}" item="${item.id}">checked</tags:contains>>&nbsp;
        <label for="selectedItem-${item.id}" style="display:inline;">${item.name}</label><br>
      </c:forEach>
    </div>
  </div>
  <hr>
<c:forEach items="${sharedRoleMap}" var="entry">
  <div class="form-group">
    <div class="col-sm-5">
	  <h5>${entry.key}</h5>
	  <c:forEach items="${entry.value}" var="item">
        <input id="selectedItem-${item.id}" type="checkbox" name="selectedItem" value="${item.id}" <tags:contains items="${userRoleIds}" item="${item.id}">checked</tags:contains>>&nbsp;
        <label for="selectedItem-${item.id}" style="display:inline;">${item.name}</label><br>
      </c:forEach>
    </div>
  </div>
  <hr>
</c:forEach>
  <div class="form-group">
    <div class="col-sm-5">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
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

