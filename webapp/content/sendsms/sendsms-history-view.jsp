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
    <%@include file="/header/sendmail.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/sendmail.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  编辑
		</div>

		<div class="panel-body">

<p><div style="display:inline-block;width:70px;">电话号码：</div>${sendsmsHistory.mobile}</p>

<p><div style="display:inline-block;width:70px;">信息：</div>${sendsmsHistory.message}</p>

<p><div style="display:inline-block;width:70px;">状态：</div>${sendsmsHistory.status}</p>

<p><div style="display:inline-block;width:70px;">信息：</div>${sendsmsHistory.info}</p>

<p><div style="display:inline-block;width:70px;">创建时间：</div>${sendsmsHistory.createTime}</p>

<p><div style="display:inline-block;width:70px;">配置名称：</div>${sendsmsHistory.sendsmsConfig.name}</p>

		</div>
      </article>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>

