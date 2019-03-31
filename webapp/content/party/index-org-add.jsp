<%@page contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp"%>
<%pageContext.setAttribute("currentHeader", "org");%>
<%pageContext.setAttribute("currentMenu", "org");%>
<c:set var="partyType" value="org"/>
<c:set var="partyBaseUrl" value="${tenantPrefix}/party/index-org.do"/>
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
          <div class="panel-heading">
		    <i class="glyphicon glyphicon-list"></i>
		    添加下级组织
		  </div>

		  <div class="panel-body">
		    <form name="mainForm" method="post" action="index-org-save.do" class="form-horizontal">
			  <div class="form-group">
			    <label class="control-label col-md-2" for="parentId">上级组织编码</label>
				<div class="col-sm-5">
				  <p class="form-control-static">${parent.code}</p>
  			  	  <input id="parentId" type="hidden" name="parentId" value="${param.parentId}">
				</div>
			  </div>
			  <div class="form-group">
			    <label class="control-label col-md-2" for="parentId">上级组织名称</label>
				<div class="col-sm-5">
				  <p class="form-control-static">${parent.name}</p>
				</div>
			  </div>
			  <div class="form-group">
			    <label class="control-label col-md-2" for="code">编码</label>
				<div class="col-sm-5">
	  			  <input id="code" type="text" name="code" value="" size="40" class="form-control required" minlength="1" maxlength="50" autocomplete="off">
				</div>
			  </div>
			  <div class="form-group">
			    <label class="control-label col-md-2" for="name">名称</label>
				<div class="col-sm-5">
	  			  <input id="name" type="text" name="name" value="" size="40" class="form-control required" minlength="1" maxlength="50" autocomplete="off">
				</div>
			  </div>
			  <div class="form-group">
			    <div class="col-md-offset-2 col-sm-5">
			      <button id="submitButton" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
			      <button type="button" onclick="history.back();" class="btn btn-link"><spring:message code='core.input.back' text='返回'/></button>
			    </div>
			  </div>
		    </form>
		  </div>

        </div>

        <div class="m-spacer"></div>

      </section>
	  <!-- end of main -->
	</div>

  </body>

</html>

