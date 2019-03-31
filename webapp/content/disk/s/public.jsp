<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>网盘</title>
    <%@include file="/common/s3.jsp"%>
	  <link rel="stylesheet" href="${cdnPrefix}/public/mossle-disk/0.0.3/sprite_list_icon.css">
	  <style type="text/css">
.text-left .disk-tool {
	display: none;
	float: right;
}
.text-left.active .disk-tool {
	display: inline-block;
}
	  </style>
	  <style type="text/css">
body {
    padding-top: 50px;
}
	  </style>
  </head>
  <body>
    <div id="wrap">&nbsp;

      <%@include file="/header/_disk.jsp"%>

      <div class="container-fluid" style="padding: 0px 15px 0;" id="top">
        <div class="row">
		      <%@include file="/menu/disk.jsp"%>

          <div class="col-md-10">
            <div class="alert-fixed-top" data-alerts="alerts" data-titles="{}" data-ids="myid" data-fade="1000"></div>

    <table id="tablereimburserecord1" class="table table-hover table-bordered">
      <thead>
        <tr>
          <th class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="col-md-7 text-left">文件名</th>
        </tr>
      </thead>
      <tbody id="tbodyFileInfo">
	      <c:forEach items="${diskShares}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td class="text-left" onmouseover="this.className='text-left active'" onmouseout="this.className='text-left'">
		        <i class="icon-16 icon-16-${item.type}"></i>
			      <c:if test="${item.type == 'dir'}">
			      <a href="index.do?path=${path}/${item.name}">
		          <span class="file-16-name">${item.name}</span>
			      </a>
			      </c:if>
			      <c:if test="${item.type != 'dir'}">
		        <a href="disk-info-view.do?id=${item.id}">
			        <span class="file-16-name">${item.name}</span>
			      </a>
			      </c:if>
	        </td>
        </tr>
		    </c:forEach>
      </tbody>
    </table>

          </div>
        </div><!--/col-->
      </div><!--/row-->
      <hr class="soften">
    </div>

  </body>
</html>
