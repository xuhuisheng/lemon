<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "sendmail");%>
<%pageContext.setAttribute("currentMenu", "sendmail");%>
<!doctype html>
<html>

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
$(function() {
    $("#sendmail-templateForm").validate({
        submitHandler: function(form) {
			bootbox.animate(false);
			var box = bootbox.dialog('<div class="progress progress-striped active" style="margin:0px;"><div class="bar" style="width: 100%;"></div></div>');
            form.submit();
        },
        errorClass: 'validate-error'
    });

<c:if test="${model.manual != 1}">
	var editor = CKEDITOR.replace('mailTemplate_content');
</c:if>
})
    </script>
	<script src="${cdnPrefix}/public/jquery-file-upload/5.42.0/js/vendor/jquery.ui.widget.js"></script>
	<script src="${cdnPrefix}/public/jquery-file-upload/5.42.0/js/jquery.iframe-transport.js"></script>
	<script src="${cdnPrefix}/public/jquery-file-upload/5.42.0/js/jquery.fileupload.js"></script>
	<script>
/*jslint unparam: true */
/*global window, $ */
$(function () {
    'use strict';
    // Change this to the location of your server-side upload handler:
    var url = 'sendmail-attachment-upload.do';
    $('#fileupload').fileupload({
        url: url,
        dataType: 'json',
        done: function (e, data) {
			var file = data.result;
			$('#files').append('<span class="badge">'
				+ '<input type="hidden" name="attachmentIds" value="' + file.id + '">'
				+ '<a href="sendmail-attachment-download.do?id=' + file.id + '" style="color:white;">' + file.name + '</a>'
				+ '<i title="' + file.id + '" class="glyphicon glyphicon-remove" style="cursor:pointer;"></i></span>');
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .bar').css(
                'width',
                progress + '%'
            );
        }
    });

	$(document).delegate('.glyphicon-remove', 'click', function() {
		var el = $(this);
		var url = 'sendmail-attachment-removeById.do?id=' + el.attr('title');
		$.getJSON(url, function(data) {
			var tr = el.parent();
			tr.remove();
		});
	});

});
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


<form id="mailTemplateForm" method="post" action="sendmail-template-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="mailTemplate_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_name">名称</label>
	<div class="col-md-11">
	  <input id="mailTemplate_name" type="text" name="name" value="${model.name}" class="form-control required" minlength="2" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_sender">发件人</label>
	<div class="col-md-11">
	  <input id="mailTemplate_sender" type="text" name="sender" value="${model.sender}" maxlength="50" class="form-control">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_receiver">收件人</label>
	<div class="col-md-11">
	  <textarea id="mailTemplate_receiver" name="receiver" maxlength="200" class="form-control">${model.receiver}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_cc">抄送</label>
	<div class="col-md-11">
	  <textarea id="mailTemplate_cc" name="cc" maxlength="200" class="form-control">${model.cc}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_bcc">暗送</label>
	<div class="col-md-11">
	  <textarea id="mailTemplate_bcc" name="bcc" maxlength="200" class="form-control">${model.bcc}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_subject">标题</label>
	<div class="col-md-11">
	  <input id="mailTemplate_subject" type="text" name="subject" value="${model.subject}" class="form-control required" minlength="1" maxlength="50">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_content">内容</label>
	<div class="col-md-11">
	  <textarea id="mailTemplate_content" name="content" class="form-control required" minlength="1" maxlength="65535">${model.content}</textarea>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_attachment">附件</label>
	<div class="col-md-11">
      <!-- The fileinput-button span is used to style the file input field as button -->
	  <span class="btn btn-default btn-success fileinput-button">
	    <i class="glyphicon glyphicon-plus"></i>
		<span>选择文件</span>
		<!-- The file input field used as target for the file upload widget -->
		<input id="fileupload" type="file" name="file" multiple data-no-uniform="true">
	  </span>
	  <br>
	  <br>
	  <!-- The global progress bar -->
	  <div id="progress" class="progress">
		<div class="bar bar-success"></div>
	  </div>
	  <!-- The container for the uploaded files -->
	  <div id="files" class="files">
        <c:forEach items="${model.sendmailAttachments}" var="item">
	      <span class="badge">
		    <input type="hidden" name="attachmentIds" value="${item.id}">
			<a href="sendmail-attachment-download.do?id=${item.id}">${item.name}</a>
			<i title="${item.id}" class="icon-remove" style="cursor:pointer;"></i>
		  </span>
        </c:forEach>
	  </div>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="mailTemplate_manual0">不使用ckeditor</label>
	<div class="col-md-11">
	  <label class="radio-inline">
	    <input id="mailTemplate_manual0" type="radio" name="manual" value="1" ${model.manual == 1 ? 'checked' : ''}>
		手工
	  </label>
	  <label class="radio-inline">
	    <input id="mailTemplate_manual0" type="radio" name="manual" value="0" ${empty model.manual || model.manual == 0 ? 'checked' : ''}>
		ckeditor
	  </label>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-11 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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

