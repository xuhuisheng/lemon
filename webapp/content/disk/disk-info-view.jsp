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
	<script type="text/javascript" src="${ctx}/s/jquery.qrcode.min.js"></script>
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
    <a href="disk-info-list.do?path=${item.parentPath}"><i class=" glyphicon glyphicon-arrow-left"></i>返回</a>
  </div>
  <div class="col-md-12 text-center">
    <i class="icon-62 icon-62-${diskInfo.type}"></i>
	<div>
      ${diskInfo.name}
	</div>
	<div>

<div class="btn-group" role="group" aria-label="...">
  <a href="disk-info-download.do?id=${diskInfo.id}" class="btn btn-default">下载</a>

  <div class="btn-group" role="group">
    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
      二维码
      <span class="caret"></span>
    </button>
    <ul class="dropdown-menu">
      <li><span id="qrcode"></span></li>
	  <script>
$('#qrcode').qrcode("<tags:baseUrl/>/disk/disk-info-download.do?id=${diskInfo.id}");
	  </script>
    </ul>
  </div>
</div>

	  <hr>
	</div>
  </div>
  <div class="col-md-12">
    <table class="table">
	  <tbody>
	    <tr>
	      <td class="col-md-3">文件类型</td>
	      <td class="col-md-3">${diskInfo.type}</td>
	      <td class="col-md-3">文件大小</td>
	      <td class="col-md-3"><tags:fileSize fileSize="${diskInfo.fileSize}"/></td>
		</tr>
	    <tr>
	      <td>创建人</td>
	      <td><tags:user userId="${diskInfo.creator}"/></td>
	      <td>创建时间</td>
	      <td><fmt:formatDate value="${diskInfo.createTime}" type="both"/></td>
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
