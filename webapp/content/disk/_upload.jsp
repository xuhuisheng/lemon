<%@page contentType="text/html;charset=UTF-8"%>
<div>
  <div id="uploadFileButton" class="btn btn-primary fileinput-button">
    <span><i class="glyphicon glyphicon-cloud-upload"></i> 上传文件</span>
    <input type="file" name="file" class="fileupload" data-no-uniform="true" data-url="upload.do" data-form-data='{"path":"${path}","spaceId":"${diskSpace.id}"}'>
  </div>

  &nbsp;

  <button id="createDirButton" class="btn btn-default" data-toggle="modal" data-target="#createDirDialog">新建文件夹</button>

  &nbsp;

  <button id="createDirButton" class="btn btn-default" data-toggle="modal" data-target="#createShareDialog" onclick="$('#createShareInfoId').val($('.selectedItem').val())">共享</button>

  <button id="removeDirButton" class="btn btn-default" data-toggle="modal" data-target="#removeDirDialog" onclick="$('#removeDirInfoId').val($('.selectedItem').val())">删除</button>
<!--
  <a href="${tenantPrefix}/disk/disk-info-list.do" class="btn btn-default"><i class="glyphicon glyphicon-list"></i>&nbsp;我的文件</a>

  <a href="${tenantPrefix}/disk/disk-share-list.do" class="btn btn-default"><i class="glyphicon glyphicon-list"></i>&nbsp;我的分享</a>

  <a href="${tenantPrefix}/disk/disk-home.do" class="btn btn-default"><i class="glyphicon glyphicon-list"></i>&nbsp;分享首页</a>
-->
  <div class="btn-group pull-right" role="group" aria-label="" data-toggle="buttons">
    <label class="btn btn-default glyphicon glyphicon-th-list ${listType == 'list' ? 'active' : ''}" onclick="location.href='disk-info-list.do?path=${path}'">
      <input type="radio" name="options" id="option1" autocomplete="off" checked>
    </label>
    <label class="btn btn-default glyphicon glyphicon-th-large ${listType == 'grid' ? 'active' : ''}" onclick="location.href='disk-info-grid.do?path=${path}'">
      <input type="radio" name="options" id="option1" autocomplete="off">
    </label>
  </div>
</div>

<div style="margin-top:10px;margin-bottom:10px;" class="clearfix">
  <div class="pull-left">
<c:if test="${not empty path}">
  <a href="disk-info-parentDir.do?path=${path}">返回上一级</a>
  |
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
	  if (i < array.length - 1) {
  %>
  <a href="?path=${currentPath}">${item == '' ? '根目录' : item}</a>
  /
  <%
	  } else {
  %>
	    ${item}
  <%
      }
    }
  %>
</ol>
</c:if>
<c:if test="${empty path}">
  全部文件
</c:if>
  </div>
  <div class="pull-right">
    共 ${fn:length(diskInfos)} 个
  </div>
</div>

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
    <form action="create-dir.do" method="post">
      <input type="hidden" name="path" value="${path}">
      <input type="hidden" name="spaceId" value="${diskSpace.id}">
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

<div id="removeDirDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
    <form action="remove-dir.do" method="post">
      <input type="hidden" name="infoId" value="" id="removeDirInfoId">
      <input type="hidden" name="spaceId" value="${diskSpace.id}">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">删除</h4>
      </div>
      <div class="modal-body">
      </div>
      <div class="modal-footer">
        <button id="uploadFileCancelButton" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button id="uploadFileConfirmButton" type="submit" class="btn btn-primary">确认</button>
      </div>
    </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<link rel="stylesheet" href="${cdnPrefix}/public/jquery-file-upload/5.42.0/css/jquery.fileupload.css">
<script src="${cdnPrefix}/public/jquery-file-upload/5.42.0/js/vendor/jquery.ui.widget.js"></script>
<script src="${cdnPrefix}/public/jquery-file-upload/5.42.0/js/jquery.iframe-transport.js"></script>
<script src="${cdnPrefix}/public/jquery-file-upload/5.42.0/js/jquery.fileupload.js"></script>

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
			data.formData = {
				lastModified: data.files[0].lastModified,
				path: '${path}',
        spaceId: '${diskSpace.id}'
			};
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

  <link href="${cdnPrefix}/public/select2/4.0.5/css/select2.min.css" rel="stylesheet" />
  <script src="${cdnPrefix}/public/select2/4.0.5/js/select2.min.js"></script>
<div id="createShareDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
    <form action="create-share.do" method="post">
      <input type="hidden" name="infoId" value="" id="createShareInfoId">
      <input type="hidden" name="mask" value="15">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">共享</h4>
      </div>
      <div class="modal-body">
        <div>
          路径: 个人文档/xxx
        </div>
        <div>
          <div style="float:left;">用户:</div>
          <select class="form-control" id="username_search" name="username" style="float:left;width:200px;clear:none;" multiple></select>
        </div>
        <table class="table">
        <thead>
          <tr>
            <th>用户</th>
            <th>权限</th>
            <th>有效期</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>用户</td>
            <td>显示/预览/下载/复制</td>
            <td>永久有效</td>
            <td><i class="glyphicon glyphicon-remove"></i></td>
          </tr>
        </tbody>
      </table>
      </div>
      <div class="modal-footer">
        <button id="createShareCancelButton" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button id="createShareConfirmButton" type="submit" class="btn btn-primary">保存</button>
      </div>
    </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
  <script>
  $('#username_search').select2({
    ajax: {
      url: '${ctx}/localuser/rs/search/select2',
      dataType: 'json'
    }
  });
</script>



