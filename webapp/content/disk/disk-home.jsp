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

        <div class="row-fluid">
		  <div class="col-md-12">
		    <form action="disk-home.do" class="form-inline">
			  <input type="text" class="form-control" name="username" value="${param.username}"/>
			  <button class="btn btn-default">搜索</button>
			</form>
			<br>
		  </div>
		</div>

        <div class="row">
          <div class="col-md-12">
            <div class="alert-fixed-top" data-alerts="alerts" data-titles="{}" data-ids="myid" data-fade="1000"></div>

    <table id="tablereimburserecord1" class="table table-hover table-bordered">
      <thead>
        <tr class="active">
          <th class="col-md-1 text-left">用户</th>
        </tr>
      </thead>
      <tbody id="tbodyFileInfo">
	    <c:forEach items="${userDtos}" var="item">
        <tr>
          <td class="text-left">
		    <a href="disk-list.do?u=${item.id}">${item.username}</a>
	      </td>
        </tr>
		</c:forEach>
      </tbody>
    </table>

<div id="removeDialog" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">确认删除？</h4>
      </div>
      <div class="modal-footer">
	  <form action="disk-share-remove.do" method="post">
	    <input type="hidden" name="path" value="${path}">
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

          </div>
        </div><!--/col-->
      </div><!--/row-->
      <hr class="soften">
    </div>

    <%@include file="_footer.jsp"%>

  </body>
</html>
