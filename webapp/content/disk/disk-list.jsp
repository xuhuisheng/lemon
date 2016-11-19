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

    <table id="tablereimburserecord1" class="table table-hover table-bordered">
      <thead>
        <tr class="active">
          <th class="col-md-1 text-left"></th>
          <th class="col-md-7 text-left">文件名</th>
          <th class="col-md-2 text-left">分享时间</th>
        </tr>
      </thead>
      <tbody id="tbodyFileInfo">
	    <c:forEach items="${diskShares}" var="item">
        <tr>
		  <td class="text-center">
			<c:if test="${item.type != 'dir'}">
            <a href="disk-download.do?id=${item.id}"><i class=" glyphicon glyphicon-download-alt"></i></a>
			</c:if>
          </td>
          <td class="text-left">
		    <i class="icon-16 icon-16-${item.type}"></i>
			<c:if test="${item.type == 'dir'}">
			<a href="disk-list.do?path=${path}/${item.name}">
		    <span class="file-16-name">${item.name}</span>
			</a>
			</c:if>
			<c:if test="${item.type != 'dir'}">
		    <a href="disk-view.do?id=${item.id}">
			<span class="file-16-name">${item.name}</span>
			</a>
			</c:if>
	      </td>
          <td class="text-left"><fmt:formatDate value="${item.shareTime}" type="both"/></td>
        </tr>
		</c:forEach>
      </tbody>
    </table>

          </div>
        </div><!--/col-->
      </div><!--/row-->
      <hr class="soften">
    </div>

    <%@include file="_footer.jsp"%>

  </body>
</html>
