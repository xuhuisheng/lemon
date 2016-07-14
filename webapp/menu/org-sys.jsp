<%@ page language="java" pageEncoding="UTF-8" %>
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>

      <!-- start of sidebar -->
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-group" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-group" aria-expanded="true" aria-controls="collapse-body-group">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        组织管理
      </h4>
    </div>
    <div id="collapse-body-group" class="panel-collapse collapse ${currentMenu == 'group' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-job">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/org/org-company-list.do"><i class="glyphicon glyphicon-list"></i> 公司</a></li>
		  <li><a href="${tenantPrefix}/org/org-department-list.do"><i class="glyphicon glyphicon-list"></i> 部门</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-job" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-job" aria-expanded="true" aria-controls="collapse-body-job">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        职务管理
      </h4>
    </div>
    <div id="collapse-body-job" class="panel-collapse collapse ${currentMenu == 'job' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-job">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/org/job-grade-list.do"><i class="glyphicon glyphicon-list"></i> 职等管理</a></li>
		  <li><a href="${tenantPrefix}/org/job-level-list.do"><i class="glyphicon glyphicon-list"></i> 职级管理</a></li>
		  <li><a href="${tenantPrefix}/org/job-type-list.do"><i class="glyphicon glyphicon-list"></i> 职务类型管理</a></li>
		  <li><a href="${tenantPrefix}/org/job-title-list.do"><i class="glyphicon glyphicon-list"></i> 职务名称管理</a></li>
		  <li><a href="${tenantPrefix}/org/job-info-list.do"><i class="glyphicon glyphicon-list"></i> 职务管理</a></li>
		  <li><a href="${tenantPrefix}/org/job-user-list.do"><i class="glyphicon glyphicon-list"></i> 人员职务管理</a></li>
		  <li><a href="${tenantPrefix}/org/job-list.do"><i class="glyphicon glyphicon-list"></i> 职等职级表</a></li>
        </ul>
      </div>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>
      <!-- end of sidebar -->

