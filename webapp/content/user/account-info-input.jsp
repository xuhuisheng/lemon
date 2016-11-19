<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#userBaseForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error',
        rules: {
            username: {
                remote: {
                    url: 'account-info-checkUsername.do',
                    data: {
                        <c:if test="${model != null}">
                        id: function() {
                            return $('#userBase_id').val();
                        }
                        </c:if>
                    }
                }
            }
        },
        messages: {
            username: {
                remote: "<spring:message code='user.user.input.duplicate' text='存在重复账号'/>"
            }
        }
    });
})
    </script>

	<link rel="stylesheet" href="${ctx}/s/jquery-file-upload/css/jquery.fileupload.css">
	<script src="${ctx}/s/jquery-file-upload/js/vendor/jquery.ui.widget.js"></script>
	<script src="${ctx}/s/jquery-file-upload/js/jquery.iframe-transport.js"></script>
	<script src="${ctx}/s/jquery-file-upload/js/jquery.fileupload.js"></script>

    <script type="text/javascript">
function generateFileupload(maxLimitedSize) {
    $('.fileupload').fileupload({
        dataType: 'json',
        add: function (e, data) {
			var file = data.files[0];
			if (file.size > maxLimitedSize) {
				alert("图片过大");
			} else {
				data.submit();
			}
        },
		submit: function (e, data) {
			var $this = $(this);
			data.jqXHR = $this.fileupload('send', data);
			$(this).parent('.btn').attr('disabled', true);
			$(this).parent('.btn').removeClass('btn-success');
			return false;
		},
        done: function (e, data) {
			var id = data.result.id;

			var imgId = data.formData.imgId;
			var btnId = data.formData.btnId;
			var viewUrl = data.formData.viewUrl + id + '&_=' + new Date().getTime();

			$("#" + imgId).html('<img src="' + viewUrl + '" style="width:50px;">');
			$('#' + btnId).attr('disabled', false);
			$('#' + btnId).addClass('btn-success');
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .bar').css(
                'width',
                progress + '%'
            ).html(progress + '%');
        }
    });
}

$(function () {
	generateFileupload(1024 * 1024);
});
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
		  <spring:message code="user.user.input.title" text="编辑用户"/>
		</div>

		<div class="panel-body">

<form id="userBaseForm" method="post" action="account-info-save.do" class="form-horizontal">
  <input id="userBase_userRepoId" type="hidden" name="userRepoId" value="1">
  <c:if test="${model != null}">
  <input id="userBase_id" type="hidden" name="id" value="${model.id}">
  </c:if>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_username"><spring:message code="user.user.input.username" text="账号"/></label>
	<div class="col-sm-5">
	  <input id="userBase_username" type="text" name="username" value="${model.username}" size="40" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>

  <c:if test="${empty model || empty model.accountCredentials}">
  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_password"><spring:message code="user.user.input.password" text="密码"/></label>
	<div class="col-sm-5">
	  <input id="userBase_password" type="password" name="password" size="40" class="form-control required" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_confirmPassword"><spring:message code="user.user.input.confirmpassword" text="验证密码"/></label>
	<div class="col-sm-5">
	  <input id="userBase_confirmPassword" type="password" name="confirmPassword" size="40" class="form-control required" maxlength="10" equalTo="#userBase_password">
    </div>
  </div>
  </c:if>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_status"><spring:message code="user.user.input.enabled" text="启用"/></label>
	<div class="col-sm-5">
	  <input id="userBase_status" type="checkbox" name="status" value="active" ${model.status == 'active' ? 'checked' : ''} >
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_displayName">显示名</label>
	<div class="col-sm-5">
	  <input id="userBase_displayName" type="text" name="displayName" value="${model.displayName}" size="40" class="form-control" minlength="2" maxlength="50">
    </div>
  </div>

  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_type">类型</label>
	<div class="col-sm-5">
	  <input id="userBase_type" type="text" name="type" value="${model.type}" size="40" class="form-control" minlength="2" maxlength="50">
    </div>
  </div>

  <%--
  <div class="form-group">
    <label class="control-label col-md-1" for="userBase_ref"><spring:message code="user.user.input.ref" text="引用"/></label>
	<div class="col-sm-5">
	  <input id="userBase_ref" type="text" name="ref" value="${model.ref}" class="form-control">
    </div>
  </div>
  --%>

  <div class="form-group">
    <div class="col-md-offset-1 col-md-11">
      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn btn-link a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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
