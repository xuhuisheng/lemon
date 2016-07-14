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
    <%@include file="/header/pim3.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim3.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <div class="panel panel-default">
        <div class="panel-heading">
		  编辑
		</div>

		<div class="panel-body">

<form id="pimRemindForm" method="post" action="msg-info-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="pimRemind_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="msgInfo_username">收件人</label>
	<div class="col-sm-5">
	  <input id="msgInfo_username" type="text" name="username" value="" size="40" class="form-control original">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_infoTime">名称</label>
	<div class="col-sm-5">
	  <input id="msg-info_name" type="text" name="name" value="${model.name}" class="form-control required" minlength="2" maxlength="50" style="width: 350px;">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimRemind_description">内容</label>
	<div class="col-sm-5">
	  <textarea id="msg-info_descn" name="content" style="width: 350px;" rows="10" class="form-control">${model.content}</textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
    </div>
  </div>
</form>
        </div>
      </div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>
