<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "doc");%>
<%pageContext.setAttribute("currentMenu", "doc");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.stamp-info.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'stamp-infoGrid',
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
	gridFormId: 'stamp-infoGridForm',
	exportUrl: 'stamp-info-export.do'
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
    <%@include file="/header/doc.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/doc.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    查询
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="stamp-infoSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
  <div class="panel-body">

		  <form name="stamp-infoForm" method="post" action="index.do" class="form-inline">
		    <label for="stamp-info_name"><spring:message code='stamp-info.stamp-info.list.search.name' text='名称'/>:</label>
		    <input type="text" id="stamp-info_name" name="filter_LIKES_name" value="${param.filter_LIKES_name}" class="form-control">
			<button class="btn btn-default a-search" onclick="document.stamp-infoForm.submit()">查询</button>&nbsp;
		  </form>

		</div>
	  </div>

      <div style="margin-bottom: 20px;">
	    <div class="pull-left btn-group" role="group">
		  <button class="btn btn-default a-insert" onclick="location.href='${tenantPrefix}/operation/process-operation-viewStartFormByKey.do?key=doc-dispatch'">新建申请</button>
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

<form id="stamp-infoGridForm" name="stamp-infoGridForm" method='post' action="stamp-info-remove.do" class="m-form-blank">
      <div class="panel panel-default">
        <div class="panel-heading">
		  <i class="glyphicon glyphicon-list"></i>
		  <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		</div>

  <table id="stamp-infoGrid" class="table table-hover">
    <thead>
      <tr>
        <!--
        <th width="10" class="table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
        -->
        <th>单号</th>
        <th>申请人</th>
        <th>申请人部门</th>
        <th>标题</th>
        <th>发文时间</th>
        <th>申请时间</th>
        <th>状态</th>
        <th>&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <c:forEach items="${page.result}" var="item">
      <tr>
        <!--
        <td><input type="checkbox" class="selectedItem a-check" name="selectedItem" value="${item.id}"></td>
        -->
        <td><a href="${tenantPrefix}/operation/form-operation-view.do?businessKey=${item.code}">${item.code}</a></td>
        <td><tags:user userId="${item.userId}"/></td>
        <td>${item.deptCode}</td>
        <td>${item.title}</td>
        <td>${item.dispatchDate}</td>
        <td><fmt:formatDate value="${item.createTime}" type="both"/></td>
        <td>${item.status}</td>
        <td>
          <c:if test="${item.status == '草稿'}">
          <a href="${ctx}/operation/process-operation-viewStartForm.do?businessKey=${item.code}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
          </c:if>
          <c:if test="${item.status == '驳回'}">
          <a href="${ctx}/operation/task-operation-viewTaskFormByBusinessKey.do?businessKey=${item.code}" class="a-update"><spring:message code="core.list.edit" text="编辑"/></a>
          </c:if>
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

