<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "plm");%>
<%pageContext.setAttribute("currentMenu", "plm");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>列表</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'pimInfoGrid',
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
	gridFormId: 'pimInfoGridForm',
	exportUrl: 'pim-info-export.do'
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
    <%@include file="/header/plm.jsp"%>

    <div class="row-fluid">

      <!-- start of sidebar -->
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-user" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-auth" aria-expanded="true" aria-controls="collapse-body-auth">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        ${plmProject.name}
      </h4>
    </div>
    <div id="collapse-body-auth" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-auth">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <tags:isUser>
		  <li><a href="${tenantPrefix}/plm/create.do?projectId=${plmProject.id}"><i class="glyphicon glyphicon-list"></i> 新建任务</a></li>
		  </tags:isUser>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-user" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-auth" aria-expanded="true" aria-controls="collapse-body-auth">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        &nbsp;
      </h4>
    </div>
    <div id="collapse-body-auth" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-auth">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/plm/index.do"><i class="glyphicon glyphicon-list"></i> 返回首页</a></li>
        </ul>
      </div>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<form id="plmGridForm" name="plmGridForm" method='post' action="plm-project-remove.do" class="m-form-blank">
<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    版本
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <table id="plmGrid" class="table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="name">名称</th>
        <th class="sorting" name="name">状态</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach var="item" items="${plmVersions}">
      <tr>
        <td>${item.name}</td>
        <td>${item.status}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
</div>
</form>
        <div class="m-spacer"></div>

<form id="plmGridForm" name="plmGridForm" method='post' action="plm-project-remove.do" class="m-form-blank">
<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    任务
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <table id="plmGrid" class="table table-hover">
    <thead>
      <tr>
        <th class="sorting" name="id"><spring:message code="plm.plm.list.id" text="编号"/></th>
        <th>类型</th>
        <th>名称</th>
        <th>创建时间</th>
        <th>报告人</th>
        <th>负责人</th>
        <th>状态</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach var="item" items="${page.result}">
      <tr>
        <td>${item.id}</td>
        <td>${item.type}</td>
        <td><a href="issue.do?id=${item.id}">${item.name}</a></td>
        <td><fmt:formatDate value="${item.createTime}" type="both"/></td>
        <td><tags:user userId="${item.reporterId}"/></td>
        <td><tags:user userId="${item.assigneeId}"/></td>
        <td>${item.status}</td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
</div>
</form>
        <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>


