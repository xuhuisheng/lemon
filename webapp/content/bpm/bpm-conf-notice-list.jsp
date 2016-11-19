<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "bpm-console");%>
<%pageContext.setAttribute("currentMenu", "bpm-category");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.bpm-conf-notice.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'bpm-conf-noticeGrid',
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
	gridFormId: 'bpm-conf-noticeGridForm',
	exportUrl: 'bpm-conf-notice-export.do'
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
    <%@include file="/header/bpm-console.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/bpm-console.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">


<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    返回
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="bpm-confenerSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

			<a class="btn btn-default" href="bpm-conf-node-list.do?bpmConfBaseId=${bpmConfBaseId}">返回</a>

		</div>
	  </div>

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    添加
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="bpm-conf-noticeSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="bpmCategoryForm" method="get" action="bpm-conf-notice-input.do" class="form-inline">
			<input type="hidden" name="bpmConfNodeId" value="${param.bpmConfNodeId}">
			<button class="btn btn-small" onclick="document.bpmCategoryForm.submit()">新增</button>
		  </form>

		</div>
	  </div>
<%--
      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='bpm-conf-notice-input.do'">新建</button>
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
--%>
<form id="bpm-conf-noticeGridForm" name="bpm-conf-noticeGridForm" method='post' action="bpm-conf-notice-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>


    <input type="hidden" name="bpmTaskDefId" value="${bpmTaskDefId}">
    <table id="bpmCategoryGrid" class="table table-hover">
      <thead>
        <tr>
		  <th width="20%">类型</th>
		  <th width="20%">提醒人</th>
		  <th width="20%">提醒时间</th>
		  <th>邮件模板</th>
		  <th width="10%">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${bpmConfNotices}" var="item">
        <tr>
		  <td>${item.type == 0 ? '到达' : item.type == 1 ? '完成' : '超时'}</td>
		  <td>${item.receiver}</td>
		  <td>${item.dueDate}</td>
		  <td>${item.templateCode}</td>
		  <td><a class="btn btn-small" href="bpm-conf-notice-remove.do?id=${item.id}">删除</a></td>
        </tr>
        </c:forEach>
      </tbody>
    </table>


      </div>
</form>
<%--
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
--%>
      <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

