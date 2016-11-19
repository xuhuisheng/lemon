<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>网盘</title>
    <%@include file="_s.jsp"%>
	<link rel="stylesheet" href="${ctx}/s/disk/sprite_list_icon.css">
  </head>
  <body>
    <div id="wrap">&nbsp;

<%@include file="/header/_disk.jsp"%>

      <div class="container-fluid" style="padding: 0px 15px 0;" id="top">
        <div class="row">
          <div class="col-md-12">
            <div class="alert-fixed-top" data-alerts="alerts" data-titles="{}" data-ids="myid" data-fade="1000"></div>

            <%pageContext.setAttribute("listType", "list");%> 
            <%@include file="_upload.jsp"%>

    <table id="tablereimburserecord1" class="table table-hover table-bordered">
      <thead>
        <tr class="active">
          <th class="col-md-1 text-left"></th>
          <th class="col-md-7 text-left">文件名</th>
          <th class="col-md-2 text-left">大小</th>
          <th class="col-md-2 text-left">修改时间</th>
        </tr>
      </thead>
      <tbody id="tbodyFileInfo">
	    <c:forEach items="${diskInfos}" var="item">
        <tr>
		  <td class="text-center">
			<c:if test="${item.type != 'dir'}">
            <a href="disk-info-download.do?id=${item.id}"><i class=" glyphicon glyphicon-download-alt"></i></a>
			</c:if>
			<a href="javascript:void(0);renameFile(${item.id}, '${item.name}');"><i class="glyphicon glyphicon-pencil"></i></a>
			<a href="javascript:void(0);moveFile(${item.id})"><i class="glyphicon glyphicon-move"></i></a>
            <a href="javascript:void(0);removeFile(${item.id});"><i class="glyphicon glyphicon-remove"></i></a>
            <a href="javascript:void(0);shareFile(${item.id});"><i class="glyphicon glyphicon-share"></i></a>
          </td>
          <td class="text-left">
		    <i class="icon-16 icon-16-${item.type}"></i>
			<c:if test="${item.type == 'dir'}">
			<a href="disk-info-list.do?path=${path}/${item.name}">
		    <span class="file-16-name">${item.name}</span>
			</a>
			</c:if>
			<c:if test="${item.type != 'dir'}">
		    <a href="disk-info-view.do?id=${item.id}">
			<span class="file-16-name">${item.name}</span>
			</a>
			</c:if>
	      </td>
          <td class="text-left"><tags:fileSize fileSize="${item.fileSize}"/></td>
          <td class="text-left"><fmt:formatDate value="${item.lastModifiedTime}" type="both"/></td>
        </tr>
		</c:forEach>
      </tbody>
    </table>

<div id="removeDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
	  <form action="disk-info-remove.do" method="post">
	  <input type="hidden" name="path" value="${path}">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">确认删除？</h4>
      </div>
      <div class="modal-footer">
	  <form action="disk-info-remove.do">
	    <input id="removeId" type="hidden" name="id" value="">
        <button id="removeCancelButton" type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button id="removeConfirmButton" type="submit" class="btn btn-primary">确认</button>
	  </form>
      </div>
	  </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script type="text/javascript">
function removeFile(id) {
	$('#removeId').val(id);
	$('#removeDialog').modal("show");
}
</script>

<div id="renameDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
	  <form action="disk-info-rename.do" method="post">
	  <input id="renameId" type="hidden" name="id" value="">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">重命名</h4>
      </div>
      <div class="modal-body">
		<input type="text" class="form-control" id="renameName" placeholder="名称" name="name">
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button id="renameConfirmButton" type="submit" class="btn btn-primary">保存</button>
      </div>
	  </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script type="text/javascript">
function renameFile(id, name) {
	$('#renameId').val(id);
	$('#renameName').val(name);
	$('#renameDialog').modal("show");
}
</script>

<div id="moveDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
	  <form action="disk-info-move.do" method="post">
	  <input id="moveId" type="hidden" name="id" value="">
	  <input id="moveParentId" type="hidden" name="parentId" value="">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">移动</h4>
      </div>
      <div class="modal-body">
	    <ul id="moveFileTree" class="ztree"></ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="submit" class="btn btn-primary">保存</button>
      </div>
	  </form>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script type="text/javascript">
var setting = {
	async: {
		enable: true,
		url: 'disk-info-tree.do'
	},
	callback: {
		onClick: function(event, treeId, treeNode) {
			$('#moveParentId').val(treeNode.id);
		}
	}
};

var zNodes =[];

function moveFile(id) {
	$('#moveId').val(id);
	$('#moveDialog').modal("show");
	
	$.fn.zTree.init($("#moveFileTree"), setting, zNodes);
}

function shareFile(id) {
	location.href = 'disk-share-sharePublic.do?id=' + id;
}

</script>

          </div>
        </div><!--/col-->
      </div><!--/row-->
      <hr class="soften">
    </div>

    <%@include file="_footer.jsp"%>

  </body>
</html>
