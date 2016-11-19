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

      <div class="container" style="padding: 0px 15px 0;" id="top">
        <div class="row">
          <div class="col-md-12">
            <div class="alert-fixed-top" data-alerts="alerts" data-titles="{}" data-ids="myid" data-fade="1000"></div>


<div class="row">
  <div class="col-md-12">
    &nbsp;
  </div>
  <div class="col-md-12 text-center">
    <i class="icon-62 icon-62-${diskShare.type}"></i>
	<div>
      ${diskShare.name}
	</div>
	<div>
	  <a href="disk-download.do?id=${diskShare.id}" class="btn btn-default">下载</a>
	  <hr>
	</div>
  </div>
  <div class="col-md-12">
    <table class="table">
	  <tbody>
	    <tr>
	      <td class="col-md-3">文件类型</td>
	      <td class="col-md-3">${diskShare.type}</td>
	      <td class="col-md-3">文件大小</td>
	      <td class="col-md-3"><tags:fileSize fileSize="${diskShare.diskInfo.fileSize}"/></td>
		</tr>
	    <tr>
	      <td>创建人</td>
	      <td><tags:user userId="${diskShare.creator}"/></td>
	      <td>分享时间</td>
	      <td><fmt:formatDate value="${diskShare.shareTime}" type="both"/></td>
		</tr>
	  </tbody>
	</table>
  </div>
</div>



          </div>
        </div><!--/col-->
      </div><!--/row-->
      <hr class="soften">
    </div>

    <%@include file="_footer.jsp"%>

  </body>
</html>
