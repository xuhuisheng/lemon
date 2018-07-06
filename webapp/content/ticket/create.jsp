<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "ticket");%>
<%pageContext.setAttribute("currentMenu", "ticket");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.ticket-group.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>

	<!-- blueimp Gallery styles -->
    <link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/blueimp-gallery.min.css">
    <!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
    <link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload.css">
    <link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload-ui.css">
    <!-- CSS adjustments for browsers with JavaScript disabled -->
    <noscript><link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload-noscript.css"></noscript>
    <noscript><link rel="stylesheet" href="${tenantPrefix}/s/jquery-file-upload/css/jquery.fileupload-ui-noscript.css"></noscript>

	<script type="text/javascript" src="${ctx}/s/ckeditor/ckeditor.js"></script>

    <script type="text/javascript">
$(function() {
	var editor = CKEDITOR.replace('content');
	editor.config.filebrowserImageUploadUrl = "${tenantPrefix}/cms/cms-article-uploadImage.do";
})
	</script>
  </head>

  <body>
    <%@include file="/header/ticket-admin.jsp"%>

    <div class="row-fluid" style="padding-top:65px;">
	  <form method="post" action="save.do" id="f">

<div class="row-fluid">
  <div class="col-md-8 col-md-offset-2">
    <div class="form-group">
      <label for="title">工单标题</label>
      <input type="text" class="form-control" id="title" placeholder="工单标题" name="name">
    </div>
    <div class="form-group">
      <label for="content">详细描述</label>
      <textarea class="form-control" id="content" placeholder="详细描述" name="content"></textarea>
    </div>
	<div class="form-group">
      <div class="fileupload-buttonbar">
        <div class="col-lg-7">
          <!-- The fileinput-button span is used to style the file input field as button -->
          <span class="btn btn-success fileinput-button">
            <i class="glyphicon glyphicon-plus"></i>
            <span>添加附件</span>
            <input type="file" name="attachment[]" multiple data-no-uniform="true">
          </span>
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
      <table role="presentation" class="table table-striped"><tbody class="files"></tbody></table>
	</div>
    <div class="form-group">
      <button class="btn btn-default">提交</button>
    </div>
  </div>
</div>

	  </form>
	</div>

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

<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-upload fade">
        <td>
            <span class="preview"></span>
        </td>
        <td>
            <p class="name">{%=file.name%}</p>
            <strong class="error text-danger"></strong>
        </td>
        <td>
            <p class="size">Processing...</p>
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
    <input type="hidden" name="files" value="{%=file.key%}">
    <tr class="template-download fade">
        <td>
            <span class="preview">
                {% if (file.thumbnailUrl) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" data-gallery><img src="{%=file.thumbnailUrl%}" width="80" height="60"></a>
                {% } %}
            </span>
        </td>
        <td>
            <p class="name">
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

	<script>
$(function() {

    // Initialize the jQuery File Upload widget:
    $('#f').fileupload({
        // Uncomment the following to send cross-domain cookies:
        //xhrFields: {withCredentials: true},
        url: 'upload.do'
    });

    // Enable iframe cross-domain access via redirect option:
    $('#f').fileupload(
        'option',
        'redirect',
        window.location.href.replace(
            /\/[^\/]*$/,
            '/cors/result.html?%s'
        )
    );

	$('#f').fileupload('option', {
		// Enable image resizing, except for Android and Opera,
		// which actually support image resizing, but fail to
		// send Blob objects via XHR requests:
		disableImageResize: /Android(?!.*Chrome)|Opera/
			.test(window.navigator.userAgent),
		maxFileSize: 5000000,
		acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i
	});
});
	</script>

  </body>

</html>

