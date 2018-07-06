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

</div>

	  <!-- start of main -->
      <section id="m-main" class="col-md-10" style="padding-top:65px;">

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    详情
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
        <div class="panel-content">
<form id="car-infoForm" method="post" action="requirement-save.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-1" for="pimIssueProject">项目</label>
	<div class="col-md-10">
	  <p class="form-control-static">${plmProject.name}</p>
	  <input id="pimIssueProject" type="hidden" name="projectId" value="${plmProject.id}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimIssueProject">上级需求</label>
	<div class="col-md-10">
	  <p class="form-control-static">${plmRequirement.name}</p>
	  <input id="pimIssueProject" type="hidden" name="parentId" value="${plmRequirement.id}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="car-info_name"><spring:message code="car-info.car-info.input.name" text="名称"/></label>
	<div class="col-md-10">
	  <input id="car-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control  required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-5 col-md-offset-1">
      <button type="submit" class="btn btn-default a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn btn-link a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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


