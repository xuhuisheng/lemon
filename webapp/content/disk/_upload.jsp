<%@page contentType="text/html;charset=UTF-8"%>
<div>
  <div id="uploadFileButton" class="btn btn-primary fileinput-button">
    <span>上传文件</span>
    <input type="file" name="file" class="fileupload" data-no-uniform="true" data-url="disk-info-upload.do" data-form-data='{"path":"${path}"}'>
  </div>

  <button id="createDirButton" class="btn btn-default" data-toggle="modal" data-target="#createDirDialog">新建文件夹</button>

  <div class="btn-group pull-right" role="group" aria-label="" data-toggle="buttons">
    <label class="btn btn-default glyphicon glyphicon-th-list ${listType == 'list' ? 'active' : ''}" onclick="location.href='disk-info-list.do?path=${path}'">
      <input type="radio" name="options" id="option1" autocomplete="off" checked>
    </label>
    <label class="btn btn-default glyphicon glyphicon-th-large ${listType == 'grid' ? 'active' : ''}" onclick="location.href='disk-info-grid.do?path=${path}'">
      <input type="radio" name="options" id="option1" autocomplete="off">
    </label>
  </div>
</div>

<br>
 
<c:if test="${path != ''}">
<ol class="breadcrumb">
  <li><a href="disk-info-parentDir.do?path=${path}">返回上一级</a></li>
  <%
    String path = (String) request.getAttribute("path");
	String currentPath = "";
	String[] array = path.split("/");
	for (int i = 0; i < array.length; i++) {
	  String item = array[i];
      if (i != 0) {
        currentPath += "/" + item;
      }
	  pageContext.setAttribute("item", item);
	  pageContext.setAttribute("currentPath", currentPath);
  %>
  <li><a href="?path=${currentPath}">${item == '' ? '根目录' : item}</a></li>
  <%
    }
  %>
</ol>
</c:if>

<div id="uploadFileProgress" class="modal fade" data-backdrop="static">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title">上传文件</h4>
      </div>
      <div class="modal-body">
        <div class="progress">
		  <div class="progress-bar" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
			<span class="sr-only">0%</span>
		  </div>
		</div>
      </div>
      <div class="modal-footer">
        <button id="uploadFileCancelButton" type="button" class="btn btn-default" data-dismiss="modal" onclick="location.reload()">取消</button>
        <button id="uploadFileConfirmButton" type="button" class="btn btn-primary" onclick="location.reload()">确认</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div id="createDirDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
	  <form action="disk-info-createDir.do" method="post">
	  <input type="hidden" name="path" value="${path}">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">创建目录</h4>
      </div>
      <div class="modal-body">
		<input type="text" class="form-control" id="dirName" placeholder="目录名" name="name">
      </div>
      <div class="modal-footer">
        <button id="uploadFileCancelButton" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button id="uploadFileConfirmButton" type="submit" class="btn btn-primary">保存</button>
      </div>
	  </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div id="createDirDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
	  <form action="disk-info-createDir.do" method="post">
	  <input type="hidden" name="path" value="${path}">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">创建目录</h4>
      </div>
      <div class="modal-body">
		<input type="text" class="form-control" id="dirName" placeholder="目录名" name="name">
      </div>
      <div class="modal-footer">
        <button id="uploadFileCancelButton" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button id="uploadFileConfirmButton" type="submit" class="btn btn-primary">保存</button>
      </div>
	  </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

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
			$('.progress-bar').css(
                'width',
                '0%'
            ).html('0%');
			$('#uploadFileConfirmButton').hide();
			$('#uploadFileProgress').modal('show');
			return false;
		},
        done: function (e, data) {
			$('#uploadFileConfirmButton').show();
			// location.reload();
        },
		fail: function (e, data) {
			alert("上传失败");
		},
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('.progress-bar').css(
                'width',
                progress + '%'
            ).html(progress + '%');
        }
    });
}

$(function () {
	generateFileupload(1024 * 1024 * 1024);
});
</script>



