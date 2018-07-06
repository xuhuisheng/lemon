<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>重置密码</title>
	<%@include file="/common/s3.jsp"%>
  </head>

  <body>

    <!-- start of header bar -->
<div class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="${tenantPrefix}">
	    <img src="${tenantPrefix}/s/logo32.png" class="img-responsive pull-left" style="margin-top:-5px;margin-right:5px;">
	    Lemon <sub><small>1.7.0-SNAPSHOT</small></sub>
      </a>
    </div>

    <div class="navbar-collapse collapse">

      <ul class="nav navbar-nav navbar-right">
	    <li>
          <a href="?locale=zh_CN"><img src="${ctx}/s/flags/china.gif" height="20"></a>
		</li>
	    <li>
          <a href="?locale=en_US"><img src="${ctx}/s/flags/us.gif" height="20"></a>
		</li>
	  </ul>
	</div>
  </div>
</div>
    <!-- end of header bar -->

	<div class="row" style="margin-top:70px;">
	  <div class="container-fluid">

	  <div class="col-md-4"></div>

	<!-- start of main -->
    <section class="col-md-4">

      <article class="panel panel-default">
        <header class="panel-heading">
		  重置密码
		</header>

		<div class="panel-body">

          重置密码信息已发至邮箱。

        </div>
      </article>

      <div class="m-spacer"></div>
	</section>
	<!-- end of main -->

	  <div class="col-md-4"></div>
	  </div>
    </div>

  </body>
</html>
