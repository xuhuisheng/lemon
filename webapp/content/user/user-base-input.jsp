<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "user");%>
<%pageContext.setAttribute("currentMenu", "user");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s.jsp"%>
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
                    url: 'user-base-checkUsername.do',
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
    <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="user.user.input.title" text="编辑用户"/></h4>
		</header>
		<div class="content content-inner">

<form id="userBaseForm" method="post" action="user-base-save.do?operationMode=STORE" class="form-horizontal">
  <input id="userBase_userRepoId" type="hidden" name="userRepoId" value="1">
  <c:if test="${model != null}">
  <input id="userBase_id" type="hidden" name="id" value="${model.id}">
  </c:if>

  <div class="control-group">
    <label class="control-label" for="userBase_username"><spring:message code="user.user.input.username" text="账号"/></label>
	<div class="controls">
	  <input id="userBase_username" type="text" name="username" value="${model.username}" size="40" class="text required" minlength="2" maxlength="50">
    </div>
  </div>
  <c:if test="${model == null || model.password == null}">
  <div class="control-group">
    <label class="control-label" for="userBase_password"><spring:message code="user.user.input.password" text="密码"/></label>
	<div class="controls">
	  <input id="userBase_password" type="password" name="password" size="40" class="text required" maxlength="10">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_confirmPassword"><spring:message code="user.user.input.confirmpassword" text="验证密码"/></label>
	<div class="controls">
	  <input id="userBase_confirmPassword" type="password" name="confirmPassword" size="40" class="text required" maxlength="10" equalTo="#userBase_password">
    </div>
  </div>
  </c:if>
  <div class="control-group">
    <label class="control-label" for="userBase_status"><spring:message code="user.user.input.enabled" text="启用"/></label>
	<div class="controls">
	  <input id="userBase_status" type="checkbox" name="status" value="1" ${model.status == 1 ? 'checked' : ''}>
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_email">邮箱</label>
	<div class="controls">
	  <input id="userBase_email" type="text" name="email" value="${model.email}" class="email" maxlength="100">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_mobile">手机</label>
	<div class="controls">
	  <input id="userBase_mobile" type="text" name="mobile" value="${model.mobile}" maxlength="20">
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_nickName">显示名</label>
	<div class="controls">
	  <input id="userBase_nickName" type="text" name="nickName" value="${model.nickName}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_fullName">全名</label>
	<div class="controls">
	  <input id="userBase_fullName" type="text" name="fullName" value="${model.fullName}" size="40" class="text" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_lastName">姓氏</label>
	<div class="controls">
	  <input id="userBase_lastName" type="text" name="lastName" value="${model.lastName}" size="40" class="text" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_firstName">名字</label>
	<div class="controls">
	  <input id="userBase_firstName" type="text" name="firstName" value="${model.firstName}" size="40" class="text" minlength="1" maxlength="50">
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_avatar">头像</label>
	<div class="controls">
	  <span id="avatarImage">
	    <c:if test="${not empty model.avatar}">
		  <img src="user-base-avatar.do?id=${model.id}" style="width:50px;">
		</c:if>
	  </span>
	  <span id="avatarButton" class="btn btn-success fileinput-button">
		<span>上传头像</span>
		<input type="file" name="avatar" class="fileupload"
		  data-no-uniform="true"
		  data-url="user-base-upload.do"
		  data-form-data='{"id":"${model.id}","imgId":"avatarImage","btnId":"avatarButton","viewUrl":"user-base-avatar.do?id="}'>
	  </span>
	  <div id="progress" class="progress" style="width:200px;margin-top:5px;height:20px;margin-bottom:0px;">
        <div class="bar bar-success"></div>
      </div>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_description">简介</label>
	<div class="controls">
	  <textarea id="userBase_description" name="description" maxlength="200">${model.description}</textarea>
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_gender">性别</label>
	<div class="controls">
	  <label class="radio inline"><input id="userBase_gender0" type="radio" name="gender" value="male" ${model.gender == 'male' ? 'checked' : ''}">男</label>
	  <label class="radio inline"><input id="userBase_gender1" type="radio" name="gender" value="femal" ${model.gender == 'female' ? 'checked' : ''}">女</label>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_birthday">生日</label>
	<div class="controls">
      <div class="input-append datepicker date" style="padding-left: 0px;">
	    <input type="text" value="<fmt:formatDate value='${model.birthday}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_location">地点</label>
	<div class="controls">
	  <input id="userBase_location" type="text" name="location" value="${model.location}" maxlength="200">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_station">工位</label>
	<div class="controls">
	  <input id="userBase_station" type="text" name="station" value="${model.station}" maxlength="50">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_telephone">分机</label>
	<div class="controls">
	  <input id="userBase_telephone" type="text" name="telephone" value="${model.telephone}" maxlength="20">
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_language">语言</label>
	<div class="controls">
	  <input id="userBase_language" type="text" name="language" value="${model.language}" maxlength="20">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_country">国家</label>
	<div class="controls">
	  <input id="userBase_country" type="text" name="country" value="${model.country}" maxlength="20">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_timezone">时区</label>
	<div class="controls">
	  <input id="userBase_timezone" type="text" name="timezone" value="${model.timezone}" maxlength="20">
    </div>
  </div>

  <div class="control-group">
    <label class="control-label" for="userBase_employeeNo">工号</label>
	<div class="controls">
	  <input id="userBase_employeeNo" type="text" name="employeeNo" value="${model.employeeNo}" maxlength="20">
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="userBase_cardNo">工卡</label>
	<div class="controls">
	  <input id="userBase_cardNo" type="text" name="cardNo" value="${model.cardNo}" maxlength="64">
    </div>
  </div>

  <%--
  <div class="control-group">
    <label class="control-label" for="userBase_ref"><spring:message code="user.user.input.ref" text="引用"/></label>
	<div class="controls">
	  <input id="userBase_ref" type="text" name="ref" value="${model.ref}">
    </div>
  </div>
  --%>
  <c:forEach items="${userBaseWrapper.userAttrWrappers}" var="item">
  <div class="control-group">
    <label class="control-label" for="user-base_${item.code}">${item.name}</label>
	<div class="controls">
	  <input id="user-base_${item.code}" type="text" name="_user_attr_${item.code}" size="40" class="text" maxlength="50" value="${item.value}">
    </div>
  </div>
  </c:forEach>
  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
      <button type="button" onclick="history.back();" class="btn a-cancel"><spring:message code='core.input.back' text='返回'/></button>
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
