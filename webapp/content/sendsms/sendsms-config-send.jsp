<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sendsms");%>
<%pageContext.setAttribute("currentMenu", "sendsms");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#smsConfigForm").validate({
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
    <%@include file="/header/sendsms.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/sendsms.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">${smsDto.success ? '发送成功' : '发送失败'}</h4>
		</header>

		<div class="content content-inner">

<c:if test="${smsDto.success}">
<p>发送成功</p>
<p>${smsDto.result}</p>
</c:if>
<c:if test="${not smsDto.success}">
<p>发送失败</p>
<p>${smsDto.result}</p>
<pre>${exception}</pre>
</c:if>

        </div>
      </article>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
