<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "my");%>
<%pageContext.setAttribute("currentMenu", "my");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="user.user.input.title" text="编辑用户"/></title>
    <%@include file="/common/s3.jsp"%>

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
			var viewUrl = data.formData.viewUrl + '?_=' + new Date().getTime();

			$("#" + imgId).html('<img src="' + viewUrl + '" style="width:512px;">');
			$('#' + btnId).attr('disabled', false);
			$('#' + btnId).addClass('btn-success');
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .progress-bar').css(
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
    <%@include file="/header/my.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/my.jsp"%>

	<!-- start of main -->
      <section id="m-main" class="col-md-10" style="margin-top:65px;">

      <article class="panel panel-default">
        <header class="panel-heading">
		  <spring:message code="user.user.input.title" text="编辑用户"/>
		</header>
		<div class="panel-body">

<form id="userBaseForm" method="post" action="my-avatar-crop.do" class="form-horizontal">
  <input id="userBase_id" type="hidden" name="id" value="${userBase.id}">

  <div class="control-group">
    <label class="control-label" for="userBase_avatar">头像</label>
	<div class="controls">
	  <div id="avatarImage">
	    <c:if test="${not empty userBase.avatar}">
		  <img src="my-base-avatar.do" style="width:512px;">
		</c:if>
	  </div>
	  <div id="avatarButton" class="btn btn-success fileinput-button">
		<span>上传一个新图片</span>
		<input type="file" name="avatar" class="fileupload"
		  data-no-uniform="true"
		  data-url="my-avatar-upload.do"
		  data-form-data='{"id":"${userBase.id}","imgId":"avatarImage","btnId":"avatarButton","viewUrl":"my-avatar-view.do"}'>
	  </div>
	  <div id="progress" class="progress" style="width:200px;margin-top:5px;height:20px;margin-bottom:0px;">
        <div class="progress-bar bar-success"></div>
      </div>
    </div>
  </div>

  <div class="control-group">
    <div class="controls">
      <button id="submitButton" class="btn btn-default a-submit">确认</button>
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
