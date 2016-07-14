<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "msg");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>

	<link href="${tenantPrefix}/s/jquery-ui/jquery-ui.min.css" rel="stylesheet">
    <script src="${tenantPrefix}/s/jquery-ui/jquery-ui.min.js"></script>
    <link rel='stylesheet' href='${tenantPrefix}/s/inputosaurus/inputosaurus.css' type='text/css' media='screen' />
    <script src='${tenantPrefix}/s/inputosaurus/inputosaurus.js' type='text/javascript'></script>
    <script type="text/javascript">
$(function() {
    $("#msg-infoForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        'rules': {
            'username': {
                'required': true,
                'remote': '${tenantPrefix}/rs/user/exists'
            }
		}
    });

	
	$('#msgInfo_username').inputosaurus({
		width : '350px',
		autoCompleteSource : function(request, response) {
			var term = request.term;
			if (term.length > 2) {
				$.get('${tenantPrefix}/rs/user/search', {
					username: term
				}, function(result) {
					var data = [];
					for (var i = 0; i < result.length; i++) {
						data.push(result[i].username);
					}
					response(data);
				});
			}
		},
		activateFinalResult: true,
		change : function(ev) {
			// $('#widget2_reflect').val(ev.target.value);
		}
	});
})
    </script>
  </head>

  <body>
    <%@include file="/header/msg-info.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/msg-info.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">

		<div class="panel-body">

		  <p><span class="label label-default"><tags:user userId="${model.senderId}"/></span> <span class="label label-default"><fmt:formatDate value="${model.createTime}" type="both"/></span></p>

		  <p>${model.content}</p>

        </div>
      </div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
