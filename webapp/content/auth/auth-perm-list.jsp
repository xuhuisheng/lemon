<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "auth");%>
<%pageContext.setAttribute("currentMenu", "auth");%>
<!doctype html>
<html lang="en">

  <head>
    <%@include file="/common/meta.jsp"%>
    <title><spring:message code="dev.perm.list.title" text="列表"/></title>
    <%@include file="/common/s3.jsp"%>
    <script type="text/javascript">
var config = {
    id: 'permGrid',
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
	gridFormId: 'permGridForm',
	exportUrl: 'perm-export.do'
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
    <%@include file="/header/auth.jsp"%>

    <div class="row-fluid">
	  <%@include file="/menu/auth.jsp"%>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

	    <div class="panel panel-default">
	      <div class="panel-heading">
	        <i class="glyphicon glyphicon-list"></i>
	        权限
	      </div>
          <div class="panel-body">
            <ul id="treeMenu" class="ztree"></ul>
		  </div>
	    </div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

<script type="text/javascript">
		var setting = {
			async: {
				enable: true,
				url: "${tenantPrefix}/auth/auth-perm-tree-data.do"
			},
			callback: {
				onClick: function(event, treeId, treeNode) {
					// location.href = '${tenantPrefix}/party/org-list.do?partyStructTypeId=${partyStructTypeId}&partyEntityId=' + treeNode.id;
				}
			}
		};

		var zNodes =[];

		$(function(){
			$.fn.zTree.init($("#treeMenu"), setting, zNodes);
		});
</script>

</html>

