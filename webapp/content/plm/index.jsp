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
        &nbsp;
      </h4>
    </div>
    <div id="collapse-body-auth" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-auth">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/plm/projects.do"><i class="glyphicon glyphicon-list"></i> 所有项目</a></li>
		  <li><a href="${tenantPrefix}/plm/sprints.do"><i class="glyphicon glyphicon-list"></i> 所有迭代</a></li>
		  <li><a href="${tenantPrefix}/plm/issues.do"><i class="glyphicon glyphicon-list"></i> 所有任务</a></li>
        </ul>
      </div>
    </div>
  </div>
<!--
		<hr>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-plm">
              <i class="icon-user"></i>
              <span class="title">动态</span>
            </a>
          </div>
          <div id="collapse-plm" class="accordion-body collapse}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${tenantPrefix}/plm/projects.do"><i class="icon-user"></i>&nbsp;所有项目</a></li>
              <li><a href="${tenantPrefix}/plm/issues.do"><i class="icon-user"></i>&nbsp;所有任务</a></li>
            </ul>
          </div>
        </div>
-->

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
    需要处理的任务
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <table id="plmGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="33%">名称</th>
        <th width="33%">类型</th>
        <th width="33%">项目</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach var="item" items="${todoIssues}">
      <tr>
        <td><a href="issue.do?id=${item.id}">${item.name}</a></td>
        <td>${item.type}</td>
        <td><a href="project.do?projectId=${item.plmProject.id}">${item.plmProject.name}</a></td>
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
    发起的任务
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <table id="plmGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="33%">名称</th>
        <th width="33%">类型</th>
        <th width="33%">项目</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach var="item" items="${myIssues}">
      <tr>
        <td><a href="issue.do?id=${item.id}">${item.name}</a></td>
        <td>${item.type}</td>
        <td><a href="project.do?projectId=${item.plmProject.id}">${item.plmProject.name}</a></td>
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
    迭代
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <table id="plmGrid" class="table table-hover">
    <thead>
      <tr>
        <th width="18%">名称</th>
        <th width="18%">项目</th>
        <th width="18%">开始</th>
        <th width="18%">结束</th>
        <th width="18%">状态</th>
		<th>&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach var="item" items="${plmSprints}">
      <tr>
        <td><a href="kanban.do?sprintId=${item.id}">${item.name}</a></td>
        <td><a href="project.do?projectId=${item.plmProject.id}">${item.plmProject.name}</a></td>
        <td><fmt:formatDate value="${item.startTime}" type="date"/></td>
        <td><fmt:formatDate value="${item.endTime}" type="date"/></td>
        <td>${item.status}</td>
        <td>
		  <a href="kanban.do?sprintId=${item.id}">看板</a>
		  <a href="sprint.do?sprintId=${item.id}">列表</a>
		</td>
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


