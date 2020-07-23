<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "asset");%>
<%pageContext.setAttribute("currentMenu", "asset");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.asset-info.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'asset-infoGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_LIKES_name': '${param.filter_LIKES_name}'
    },
    selectedItemClass: 'selectedItem',
    gridFormId: 'asset-infoGridForm',
    exportUrl: 'asset-info-export.do'
};

var table;

$(function() {
    table = new Table(config);
    table.configPagination('.m-pagination');
    table.configPageInfo('.m-page-info');
    table.configPageSize('.m-page-size');
});
    </script>
  </head>

  <body>
    <%@include file="/header/asset-info.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/asset-info.jsp"%>

      <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
    <i class="glyphicon glyphicon-list"></i>
    查询
    <div class="pull-right ctrl">
      <a class="btn btn-default btn-xs"><i id="asset-infoSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

          <form name="asset-infoForm" method="post" action="asset-info-list.do" class="form-inline">
            <label for="asset-info_name"><spring:message code='asset-info.asset-info.list.search.name' text='名称'/>:</label>
            <input type="text" id="asset-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
            <button class="btn btn-default a-search" onclick="document.asset-infoForm.submit()">查询</button>&nbsp;
          </form>

        </div>
      </div>

      <div style="margin-bottom: 20px;">
        <div class="pull-left btn-group" role="group">
          <button class="btn btn-default a-insert" onclick="location.href='asset-info-input.do'">新建</button>
          <button class="btn btn-default a-remove" onclick="table.removeAll()">删除</button>
          <button class="btn btn-default a-export" onclick="table.exportExcel()">导出</button>
        </div>

        <div class="pull-right">
          每页显示
          <select class="m-page-size form-control" style="display:inline;width:auto;">
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
          </select>
          条
        </div>

        <div class="clearfix"></div>
      </div>

<form id="asset-infoGridForm" name="asset-infoGridForm" method='post' action="asset-info-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
          <i class="glyphicon glyphicon-list"></i>
          <spring:message code="scope-info.scope-info.list.title" text="列表"/>
        </div>

  <table id="asset-infoGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <!--
        <th class="sorting" name="id"><spring:message code="asset-info.asset-info.list.id" text="编号"/></th>
        -->
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="name">编码</th>
        <th class="sorting" name="name">所属大类</th>
        <th class="sorting" name="name">所属小类</th>
        <th class="sorting" name="name">型号</th>
        <th class="sorting" name="name">SN</th>
        <th class="sorting" name="name">价格</th>
        <th class="sorting" name="name">购入时间</th>
        <th class="sorting" name="name">位置</th>
        <th class="sorting" name="name">状态</th>
        <!--
        <th class="sorting" name="name">经手人</th>
        -->
        <th width="80">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <!--
        <td>${item.id}</td>
        -->
        <td>${item.name}</td>
        <td>${item.code}</td>
        <td>${item.assetCategoryByCategoryId.name}</td>
        <td>${item.assetCategoryBySubCategoryId.name}</td>
        <td>${item.model}</td>
        <td>${item.sn}</td>
        <td>${item.price}</td>
        <td>${item.buyDate}</td>
        <td>${item.location}</td>
        <td>
          <c:choose>
            <c:when test="${item.status == '0'}">在库</c:when>
            <c:when test="${item.status == '1'}">使用中</c:when>
            <c:when test="${item.status == '2'}">维修中</c:when>
            <c:when test="${item.status == '3'}">已报废</c:when>
            <c:otherwise>异常</c:otherwise>
          </c:choose>
        </td>
        <!--
        <td>${item.userId}</td>
        -->
        <td>
          <a href="asset-info-input.do?id=${item.id}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>


      </div>
</form>

      <div>
        <div class="m-page-info pull-left">
          共100条记录 显示1到10条记录
        </div>

        <div class="btn-group m-pagination pull-right">
          <button class="btn btn-default">&lt;</button>
          <button class="btn btn-default">1</button>
          <button class="btn btn-default">&gt;</button>
        </div>

        <div class="clearfix"></div>
      </div>

      <div class="m-spacer"></div>

      </section>
      <!-- end of main -->
    </div>

  </body>

</html>

