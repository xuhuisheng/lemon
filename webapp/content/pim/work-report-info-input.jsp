<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "pim");%>
<%pageContext.setAttribute("currentMenu", "workReport");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>编辑</title>
    <%@include file="/common/s.jsp"%>

    <!-- blueimp Gallery styles -->
    <link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/blueimp-gallery.min.css">
    <!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
    <link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload.css">
    <link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload-ui.css">
    <!-- CSS adjustments for browsers with JavaScript disabled -->
    <noscript><link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload-noscript.css"></noscript>
    <noscript><link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload-ui-noscript.css"></noscript>

    <script type="text/javascript">
$(function() {
    $("#workReportInfoForm").validate({
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
    <%@include file="/header/pim.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/pim.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="span10">

      <article class="m-widget">
        <header class="header">
		  <h4 class="title">编辑</h4>
		</header>

		<div class="content content-inner">

<form id="workReportInfoForm" method="post" action="work-report-info-save.do" class="form-horizontal">
  <c:if test="${not empty model}">
  <input id="workReportInfo_id" type="hidden" name="id" value="${model.id}">
  </c:if>
  <div class="control-group">
    <label class="control-label" for="workReportInfo_reportDate">日报时间</label>
	<div class="controls">
      <div class="input-append datepicker date" style="padding-left: 0px;">
	    <input id="workReportInfo_reportDate" type="text" name="reportDate" value="<fmt:formatDate value='${model.reportDate}' pattern='yyyy-MM-dd'/>" readonly style="background-color:white;cursor:default; width: 175px;">
	    <span class="add-on" style="padding-top: 2px; padding-bottom: 2px;"><i class="icon-calendar"></i></span>
	  </div>
    </div>
  </div>
  <div class="control-group">
    <label class="control-label" for="workReportInfo_content">日报内容</label>
	<div class="controls">
	  <textarea id="workReportInfo_content" name="content" class="required" minlength="2" maxlength="200">${model.content}</textarea>
    </div>
  </div>
  <div class="control-group">
	<div class="controls">
      <div class="fileupload-buttonbar">
        <div class="col-lg-7">
          <!-- The fileinput-button span is used to style the file input field as button -->
          <span class="btn btn-success fileinput-button">
            <i class="glyphicon glyphicon-plus"></i>
            <span>添加</span>
            <input type="file" name="files[]" multiple data-no-uniform="true">
          </span>
          <button type="submit" class="btn btn-primary start">
            <i class="glyphicon glyphicon-upload"></i>
            <span>上传</span>
          </button>
          <button type="reset" class="btn btn-warning cancel">
            <i class="glyphicon glyphicon-ban-circle"></i>
            <span>取消</span>
          </button>
          <button type="button" class="btn btn-danger delete">
            <i class="glyphicon glyphicon-trash"></i>
            <span>删除</span>
          </button>
          <input type="checkbox" class="toggle">
          <!-- The global file processing state -->
          <span class="fileupload-process"></span>
        </div>
        <!-- The global progress state -->
        <div class="col-lg-5 fileupload-progress fade">
          <!-- The global progress bar -->
          <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
            <div class="progress-bar progress-bar-success" style="width:0%;"></div>
          </div>
          <!-- The extended global progress state -->
          <div class="progress-extended">&nbsp;</div>
        </div>
      </div>
      <!-- The table listing the files available for upload/download -->
      <table role="presentation" class="table table-striped">
	    <tbody class="files">
<c:forEach var="item" items="${model.workReportAttachments}">
    <tr class="template-download">
        <td>
            <p class="name">
		        <input type="hidden" name="attachmentIds" value="${item.id}">
                <a href="work-report-info-attachment.do?id=${item.id}" title="${item.name}" download="work-report-info-download.do?id=${item.id}">${item.name}</a>
            </p>
        </td>
        <td>
            <span class="size">0</span>
        </td>
        <td>
                <button class="btn btn-danger delete" data-type="{%=file.deleteType%}" data-url="">
                    <i class="glyphicon glyphicon-trash"></i>
                    <span>删除</span>
                </button>
                <input type="checkbox" name="delete" value="1" class="toggle">
        </td>
    </tr>
</c:forEach>
		</tbody>
      </table>
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

    <!-- The blueimp Gallery widget -->
    <div id="blueimp-gallery" class="blueimp-gallery blueimp-gallery-controls" data-filter=":even">
      <div class="slides"></div>
      <h3 class="title"></h3>
      <a class="prev">‹</a>
      <a class="next">›</a>
      <a class="close">×</a>
      <a class="play-pause"></a>
      <ol class="indicator"></ol>
    </div>
        </div>
      </article>

      </section>
	  <!-- end of main -->
	</div>

  </body>


<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-upload fade">
        <td>
            <p class="name">{%=file.name%}</p>
            <strong class="error text-danger"></strong>
        </td>
        <td>
            <p class="size">上传中...</p>
            <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
        </td>
        <td>
            {% if (!i && !o.options.autoUpload) { %}
                <button class="btn btn-primary start" disabled>
                    <i class="glyphicon glyphicon-upload"></i>
                    <span>上传</span>
                </button>
            {% } %}
            {% if (!i) { %}
                <button class="btn btn-warning cancel">
                    <i class="glyphicon glyphicon-ban-circle"></i>
                    <span>取消</span>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}
</script>
<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-download fade">
        <td>
            <p class="name">
		        <input type="hidden" name="attachmentIds" value="{%=file.id%}">
                {% if (file.url) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" {%=file.thumbnailUrl?'data-gallery':''%}>{%=file.name%}</a>
                {% } else { %}
                    <span>{%=file.name%}</span>
                {% } %}
            </p>
            {% if (file.error) { %}
                <div><span class="label label-danger">Error</span> {%=file.error%}</div>
            {% } %}
        </td>
        <td>
            <span class="size">{%=o.formatFileSize(file.size)%}</span>
        </td>
        <td>
            {% if (file.deleteUrl) { %}
                <button class="btn btn-danger delete" data-type="{%=file.deleteType%}" data-url="{%=file.deleteUrl%}"{% if (file.deleteWithCredentials) { %} data-xhr-fields='{"withCredentials":true}'{% } %}>
                    <i class="glyphicon glyphicon-trash"></i>
                    <span>删除</span>
                </button>
                <input type="checkbox" name="delete" value="1" class="toggle">
            {% } else { %}
                <button class="btn btn-warning cancel">
                    <i class="glyphicon glyphicon-ban-circle"></i>
                    <span>取消</span>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}
</script>

    <!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/vendor/jquery.ui.widget.js"></script>
    <!-- The Templates plugin is included to render the upload/download listings -->
    <script src="${tenantPrefix}/s/jquery-file-upload/tmpl.min.js"></script>
    <!-- The Load Image plugin is included for the preview images and image resizing functionality -->
    <script src="${tenantPrefix}/s/jquery-file-upload/load-image.all.min.js"></script>
    <!-- The Canvas to Blob plugin is included for image resizing functionality -->
    <script src="${tenantPrefix}/s/jquery-file-upload/canvas-to-blob.min.js"></script>
    <!-- blueimp Gallery script -->
    <script src="${tenantPrefix}/s/jquery-file-upload/jquery.blueimp-gallery.min.js"></script>
    <!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.iframe-transport.js"></script>
    <!-- The basic File Upload plugin -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.fileupload.js"></script>
    <!-- The File Upload processing plugin -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.fileupload-process.js"></script>
    <!-- The File Upload image preview & resize plugin -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.fileupload-image.js"></script>
    <!-- The File Upload audio preview plugin -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.fileupload-audio.js"></script>
    <!-- The File Upload video preview plugin -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.fileupload-video.js"></script>
    <!-- The File Upload validation plugin -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.fileupload-validate.js"></script>
    <!-- The File Upload user interface plugin -->
    <script src="${tenantPrefix}/s/jquery-file-upload/js/jquery.fileupload-ui.js"></script>
    <!-- The XDomainRequest Transport is included for cross-domain file deletion for IE 8 and IE 9 -->
    <!--[if (gte IE 8)&(lt IE 10)]>
    <script src="${tenantPrefix}/s/jquery-file-upload/js/cors/jquery.xdr-transport.js"></script>
    <![endif]-->
  </body>
  <script>
$(function () {
    'use strict';

    // Initialize the jQuery File Upload widget:
    $('#workReportInfoForm').fileupload({
        // Uncomment the following to send cross-domain cookies:
        //xhrFields: {withCredentials: true},
        url: 'work-report-info-upload.do?id=${model.id}'
    });

    // Enable iframe cross-domain access via redirect option:
    $('#workReportInfoForm').fileupload(
        'option',
        'redirect',
        window.location.href.replace(
            /\/[^\/]*$/,
            '/cors/result.html?%s'
        )
    );

	$('#workReportInfoForm').fileupload('option', {
		// Enable image resizing, except for Android and Opera,
		// which actually support image resizing, but fail to
		// send Blob objects via XHR requests:
		disableImageResize: /Android(?!.*Chrome)|Opera/
			.test(window.navigator.userAgent),
		maxFileSize: 5000000,
		acceptFileTypes: /(\.|\/)(gif|jpe?g|png|doc|docx|xls|xlsx|ppt|pptx|zip|rar|txt)$/i
	});

	// Upload server status check for browsers with CORS support:
	if ($.support.cors) {
		$.ajax({
			url: 'work-report-info-download.do?id=${model.id}',
			type: 'HEAD'
		}).fail(function () {
			$('<div class="alert alert-danger"/>')
				.text('服务器异常，暂时无法上传 - ' +
						new Date())
				.appendTo('#fileupload');
		});
	}

	// Load existing files:
	$('#workReportInfoForm').addClass('fileupload-processing');
	
	$.ajax({
		// Uncomment the following to send cross-domain cookies:
		//xhrFields: {withCredentials: true},
		url: 'work-report-info-download.do?id=${model.id}',
		dataType: 'json',
		context: $('#fileupload')[0]
	}).always(function () {
		$(this).removeClass('fileupload-processing');
	}).done(function (result) {
		$(this).fileupload('option', 'done')
			.call(this, $.Event('done'), {result: result});
	});

});
  </script>
</html>
