<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "msg");%>
<%pageContext.setAttribute("currentMenu", "msg");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="msg-info.msg-info.input.title" text="编辑"/></title>
    <%@include file="/common/s.jsp"%>

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
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="msg-info.msg-info.input.title" text="编辑"/></h4>
		</header>

		<div class="content content-inner">

<form id="msg-infoForm" method="post" action="msg-info-save.do" class="form-horizontal">
  <c:if test="${model != null}">
  <input id="msg-info_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="msgInfo_username">收件人</label>
	<div class="controls">
	  <input id="msgInfo_username" type="text" name="username" value="" size="40" class="original">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="msg-info_name"><spring:message code="msg-info.msg-info.input.name" text="名称"/></label>
	<div class="controls">
	  <input id="msg-info_name" type="text" name="name" value="${model.name}" class="text required" minlength="2" maxlength="50" style="width: 350px;">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="msg-info_descn">内容</label>
	<div class="controls">
	  <textarea id="msg-info_descn" name="content" style="width: 350px;">${model.content}</textarea>
    </div>
  </div>
  <div class="control-group">
    <div class="controls">
      <button type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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
