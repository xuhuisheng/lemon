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

            <div class="panel panel-default">
              <div class="panel-body">

                <form name="card-infoForm" method="post" action="acl-add.do" class="form-inline">
                  <input type="hidden" name="diskInfoId" value="${diskInfo.id}">
                  <label for="entityCatalog">类型:</label>
                  <input type="text" id="entityCatalog" name="entityCatalog" value="" class="form-control">
                  <label for="entityRef">标识:</label>
                  <input type="text" id="entityRef" name="entityRef" value="" class="form-control">
                  <label for="mask">权限:</label>
                  <select id="mask" name="mask" class="form-control">
                    <option value="1">显示</option>
                    <option value="17">显示/新建</option>
                    <option value="3">显示/预览</option>
                    <option value="7">显示/预览/下载</option>
                    <option value="15" selected>显示/预览/下载/复制</option>
                    <option value="55">显示/预览/下载/修改/新建</option>
                    <option value="63">显示/预览/下载/复制/修改/新建</option>
                    <option value="127">显示/预览/下载/复制/修改/新建/删除</option>
                  </select>
                  <button class="btn btn-default">添加</button>&nbsp;
                </form>

              </div>
            </div>

            <%pageContext.setAttribute("listType", "list");%> 

            <div style="padding-bottom:20px;">
              <a href="${tenantPrefix}/disk/index.do">返回</a>
              &nbsp;
              ${diskInfo.name}
            </div>

    <table id="tablereimburserecord1" class="table table-hover table-bordered">
      <thead>
        <tr>
          <th class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="col-md-6 text-left">用户名</th>
          <th class="col-md-2 text-left">权限</th>
          <th class="col-md-2 text-left">备注</th>
          <th class="col-md-1 text-left">删除</th>
        </tr>
      </thead>
      <tbody id="tbodyFileInfo">
          <c:forEach items="${diskInfo.diskRule.diskAcls}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
          <td class="text-left">
            ${item.entityRef}
            </td>
          <td class="text-left">${item.mask}</td>
          <td class="text-left">&nbsp;</td>
          <td class="text-left"><a href="acl-remove.do?id=${item.id}">删除</a></td>
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
