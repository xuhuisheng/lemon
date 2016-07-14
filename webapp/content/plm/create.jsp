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
    <div class="panel-heading" role="tab" id="collapse-header-user" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-auth" aria-expanded="true" aria-col-md-5="collapse-body-auth">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        ${plmProject.name}
      </h4>
    </div>
    <div id="collapse-body-auth" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-auth">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/plm/project.do?projectId=${plmProject.id}"><i class="glyphicon glyphicon-list"></i> 返回项目</a></li>
		  <tags:isUser>
		  <li><a href="${tenantPrefix}/plm/create.do?projectId=${plmProject.id}"><i class="glyphicon glyphicon-list"></i> 新建任务</a></li>
		  </tags:isUser>
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

<div class="panel panel-default">
  <div class="panel-heading">
	<i class="glyphicon glyphicon-list"></i>
    详情
	<div class="pull-right ctrl">
	  <a class="btn btn-default btn-xs"><i id="audit-baseSearchIcon" class="glyphicon glyphicon-chevron-up"></i></a>
    </div>
  </div>
        <div class="panel-content">
<form id="car-infoForm" method="post" action="save.do" class="form-horizontal">
  <div class="form-group">
    <label class="control-label col-md-1" for="pimIssueProject">项目</label>
	<div class="col-md-5">
	  ${plmProject.name}
	  <input id="pimIssueProject" type="hidden" name="projectId" value="${plmProject.id}">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="pimIssueProject">类型</label>
	<div class="col-md-5">
	  <select name="type" class="required">
	    <option value=""></option>
	    <option value="story">需求</option>
	    <option value="task">任务</option>
	    <option value="bug">BUG</option>
	  </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="car-info_name"><spring:message code="car-info.car-info.input.name" text="名称"/></label>
	<div class="col-md-5">
	  <input id="car-info_name" type="text" name="name" value="${model.name}" size="40" class="form-control  required" minlength="2" maxlength="10">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="plmIssue_version">修复版本</label>
    <div class="col-md-5">
	  <select id="plmIssue_version" name="versionIds" class="chzn-select required" multiple data-placeholder="选择版本">
	    <c:forEach var="item" items="${plmVersions}">
	    <option value="${item.id}">${item.name}</option>
		</c:forEach>
	  </select>
	  <label for="plmIssue_version" class="validate-error" style="display:none" generated="true"></label>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-md-1" for="plmIssueContent">描述</label>
	<div class="col-md-5">
	  <textarea id="plmIssueContent" name="content" class="required" rows="6"></textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-md-5">
      <button type="submit" class="btn a-submit"><spring:message code='core.input.save' text='保存'/></button>
	  &nbsp;
      <button type="button" class="btn a-cancel" onclick="history.back();"><spring:message code='core.input.back' text='返回'/></button>
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


