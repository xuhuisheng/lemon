<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "asset");%>
<%pageContext.setAttribute("currentMenu", "asset");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.asset-lend.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'asset-lendGrid',
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
    gridFormId: 'asset-lendGridForm',
    exportUrl: 'asset-lend-export.do'
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
    <%@include file="/header/asset-user.jsp"%>

    <div class="row-fluid">
      <%@include file="/menu/asset-user.jsp"%>

      <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
    <i class="glyphicon glyphicon-list"></i>
    查询
    <div class="pull-right ctrl">
      <a class="btn btn-default btn-xs"><i id="asset-lendSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

          <form name="asset-lendForm" method="post" action="asset-lend-list.do" class="form-inline">
            <label for="asset-lend_name"><spring:message code='asset-lend.asset-lend.list.search.name' text='名称'/>:</label>
            <input type="text" id="asset-lend_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
            <button class="btn btn-default a-search" onclick="document.asset-lendForm.submit()">查询</button>&nbsp;
          </form>

        </div>
      </div>

      <div style="margin-bottom: 20px;">
        <div class="pull-left btn-group" role="group">
          <a class="btn btn-default a-insert" href="request-input.do">申请资产</a>
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

<form id="asset-lendGridForm" name="asset-lendGridForm" method='post' action="asset-lend-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
          <i class="glyphicon glyphicon-list"></i>
          <spring:message code="scope-info.scope-info.list.title" text="列表"/>
        </div>

  <table id="asset-lendGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        <!--
        <th class="sorting" name="id"><spring:message code="asset-lend.asset-lend.list.id" text="编号"/></th>
        -->
        <th>编码</th>
        <th>名称</th>
        <th>分类</th>
        <th>型号</th>
        <th>SN</th>
        <th>数量</th>
        <th>申请时间</th>
        <th>&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        <!--
        <td>${item.id}</td>
        -->
        <td>${item.code}</td>
        <td>${item.stockInfo.skuInfo.name}</td>
        <td>${item.stockInfo.skuInfo.skuCategory.name}</td>
        <td>${item.stockInfo.skuInfo.model}</td>
        <td>${item.sn}</td>
        <td>${item.usingCount}</td>
        <td></td>
        <td>
          <a href="request-return.do?id=${item.id}">归还</a>
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

