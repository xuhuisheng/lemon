<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "org");%>
<%pageContext.setAttribute("currentMenu", "org");%>
<c:set var="partyType" value="user"/>
<c:set var="partyBaseUrl" value="${tenantPrefix}/party/index.do"/>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title>组织结构</title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'orgGrid',
    pageNo: ${empty page.pageNo ? 0 : page.pageNo},
    pageSize: ${empty page.pageSize ? 0 : page.pageSize},
    totalCount: ${empty page.totalCount ? 0 : page.totalCount},
    resultSize: ${empty page.resultSize ? 0 : page.resultSize},
    pageCount: ${empty page.pageCount ? 0 : page.pageCount},
    orderBy: '${page.orderBy == null ? "" : page.orderBy}',
    asc: ${empty page.asc ? true : page.asc},
    params: {
        'partyStructTypeId': '${param.partyStructTypeId}',
        'partyEntityId': '${param.partyEntityId}',
        'name': '${param.name}'
    },
	selectedItemClass: 'selectedItem',
	gridFormId: 'orgGridForm',
	exportUrl: 'org-export.do'
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
    <%@include file="/header/org.jsp"%>

    <div class="row-fluid" style="padding-top:65px;">
	  <%@include file="/menu/org-index.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10">

      	<div class="panel panel-default">
      	  <div class="panel-body">
      	  	${partyEntity.name}
      	  </div>
      	</div>

        <div style="margin-bottom: 20px;">
	      <div class="pull-left">
	        <div class="btn-group" role="group">
	          <a class="btn btn-default" href="index-add.do?parentId=${param.partyEntityId}">添加人员</a>
	        </div>
		  </div>
		  <!--
		  <div class="pull-left">
		    <form name="orgForm" method="post" action="org-list.do" class="form-inline">
		      <input type="hidden" name="partyStructTypeId" value="${param.partyStructTypeId}">
		      <input type="hidden" name="partyEntityId" value="${param.partyEntityId}">
		      &nbsp;
		      <label for="org_name"><spring:message code='org.org.list.search.name' text='名称'/>:</label>
		      <input type="text" id="org_name" name="name" value="${param.name}" class="form-control">
			  <button class="btn btn-default a-search" onclick="document.orgForm.submit()">查询</button>&nbsp;
		    </form>
		  </div>
		  -->

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

        <form id="orgGridForm" name="orgGridForm" method='post' action="org-remove.do" class="m-form-blank">
          <div class="panel panel-default">
            <div class="panel-heading">
		      <i class="glyphicon glyphicon-list"></i>
		      <spring:message code="scope-info.scope-info.list.title" text="列表"/>
		    </div>

            <table id="orgGrid" class="table table-hover">
              <thead>
                <tr>
				  <th>账号</th>
		          <th>姓名</th>
		          <!--
				  <th>职位</th>
				  -->
		          <th width="120">操作</th>
		        </tr>
		      </thead>

		      <tbody>
		        <c:forEach items="${page.result}" var="item">
		        <tr>
			      <td>${item.childEntity.code}</td>
		          <td>${item.childEntity.name}</td>
		          <!--
				  <td>
				    <c:forEach items="${item.childEntity.childStructs}" var="childStruct">
				      <c:if test="${childStruct.partyStructType.type=='user-position'}">
				        ${childStruct.childEntity.name}&nbsp;
					  </c:if>
				    </c:forEach>
				  </td>
				  -->
		          <td>
				    <a href="index-remove.do?id=${item.id}" class="a-remove">删除</a>
				    &nbsp;
				    <a href="index-line.do?id=${item.id}" class="a-remove">汇报线</a>
				    <!--
				    <c:if test="${item.childEntity.partyType.type==1}">
				      <a href="org-position-input.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=${item.childEntity.id}&partyTypeType=2" class="a-remove">配置职位</a>
				    </c:if>
				    -->
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

