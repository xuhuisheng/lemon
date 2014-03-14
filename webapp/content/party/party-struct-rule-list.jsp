<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "party");%>
<%pageContext.setAttribute("currentMenu", "party");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="org.structrule.list.title" text="组织机构结构规则"/></title>
    <%@include file="/common/s.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'orgStructRuleGrid',
    pageNo: ${page.pageNo},
    pageSize: ${page.pageSize},
    totalCount: ${page.totalCount},
    resultSize: ${page.resultSize},
    pageCount: ${page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${page.asc},
    params: {
        'filter_EQL_partyType.id': '${param["filter_EQL_partyType.id"]}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'orgStructRuleGridForm',
	exportUrl: 'party-struct-rule-export.do'
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
    <%@include file="/header/party.jsp"%>

    <div class="row-fluid">
	<%@include file="/menu/party.jsp"%>

	<!-- start of main -->
    <section id="m-main" class="span10">

	  <article class="m-widget">
        <header class="header">
		  <h4 class="title">查询</h4>
		  <div class="ctrl">
			<a class="btn"><i id="orgstructruleSearchIcon" class="icon-chevron-up"></i></a>
		  </div>
		</header>
        <div id="orgstructruleSearch" class="content content-inner">

		  <form name="orgstructruleForm" method="post" action="party-struct-rule-list.do" class="form-inline">
		    <label for="orgStructRule_orgStructType.id"><spring:message code="org.structrule.list.search.type" text="结构类型"/>:</label>
		    <select id="orgStructRule_orgStructType" name="filter_EQL_partyStructType.id">
			  <option value=""></option>
			  <c:forEach items="${partyStructTypes}" var="item">
			  <option value="${item.id}" ${param['filter_EQL_partyStructType.id'] == item.id ? 'selected' : ''}>${item.name}</option>
			  </c:forEach>
		    </select>
			<button class="btn btn-small" onclick="document.orgstructForm.submit()">查询</button>
		  </form>

		</div>
	  </article>

	  <article class="m-blank">
	    <div class="pull-left">
		  <region:region-permission permission="structrule:create">
		  <button class="btn btn-small" onclick="location.href='party-struct-rule-input.do'">新建</button>
		  </region:region-permission>
		  <region:region-permission permission="structrule:delete">
		  <button class="btn btn-small" onclick="table.removeAll()">删除</button>
		  </region:region-permission>
		  <button class="btn btn-small" onclick="table.exportExcel()">导出</button>
		</div>

		<div class="pull-right">
		  每页显示
		  <select class="m-page-size">
		    <option value="10">10</option>
		    <option value="20">20</option>
		    <option value="50">50</option>
		  </select>
		  条
		</div>

	    <div class="m-clear"></div>
	  </article>

      <article class="m-widget">
        <header class="header">
		  <h4 class="title"><spring:message code="org.structrule.list.title" text="组织机构结构规则"/></h4>
		</header>
		<div class="content">

  <form id="orgStructRuleGridForm" name="orgStructRuleGridForm" method='post' action="party-struct-rule-remove.do" class="m-form-blank">
    <table id="orgStructRuleGrid" class="m-table table-hover">
      <thead>
        <tr>
          <th width="10" class="m-table-check"><input type="checkbox" name="checkAll" onchange="toggleSelectedItems(this.checked)"></th>
          <th class="sorting" name="partyStructType.id"><spring:message code="org.structrule.list.type" text="结构类型"/></th>
          <th class="sorting" name="parentType.id"><spring:message code="org.structrule.list.parentype" text="上级类型"/></th>
          <th class="sorting" name="childType.id"><spring:message code="org.structrule.list.childtype" text="下级类型"/></th>
          <th width="50">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${page.result}" var="item">
        <tr>
          <td><input type="checkbox" class="selectedItem" name="selectedItem" value="${item.id}"></td>
          <td>${item.partyStructType.name}</td>
          <td>${item.parentType.name}</td>
          <td>${item.childType.name}</td>
          <td>
            <a href="party-struct-rule-input.do?id=${item.id}"><spring:message code="core.list.edit" text="编辑"/></a>
          </td>
        </tr>
        </c:forEach>
      </tbody>
    </table>
  </form>

        </div>
      </article>

	  <article>
	    <div class="m-page-info pull-left">
		  共100条记录 显示1到10条记录
		</div>

		<div class="btn-group m-pagination pull-right">
		  <button class="btn btn-small">&lt;</button>
		  <button class="btn btn-small">1</button>
		  <button class="btn btn-small">&gt;</button>
		</div>

	    <div class="m-clear"></div>
      </article>

      <div class="m-spacer"></div>

    </section>
	<!-- end of main -->
	</div>

  </body>

</html>
