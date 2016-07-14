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
 
            <%pageContext.setAttribute("listType", "grid");%> 
            <%@include file="_upload.jsp"%>

<div class="row">
  <c:forEach items="${diskInfos}" var="item" varStatus="status">
  <div class="col-md-2 text-center">
    <c:if test="${item.type == 'dir'}">
    <a href="disk-info-grid.do?path=${path}/${item.name}"><div class="icon-62 icon-62-${item.type}"></div></a>
    <a href="disk-info-grid.do?path=${path}/${item.name}"><div class="file-62-name">${item.name}</div></a>
	</c:if>
    <c:if test="${item.type != 'dir'}">
    <a href="disk-info-view.do?id=${item.id}"><div class="icon-62 icon-62-${item.type}"></div></a>
    <a href="disk-info-view.do?id=${item.id}"><div class="file-62-name">${item.name}</div></a>
	</c:if>
  </div>
<c:if test="${status.count % 6 == 0}">
</div>
<div class="row">
</c:if>
  </c:forEach>
</div>



          </div>
        </div><!--/col-->
      </div><!--/row-->
      <hr class="soften">
    </div>

    <%@include file="_footer.jsp"%>

  </body>
</html>
