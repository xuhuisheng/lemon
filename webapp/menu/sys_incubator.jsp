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
    <div class="panel-heading" role="tab" id="collapse-header-ticket" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-ticket" aria-expanded="true" aria-controls="collapse-body-ticket">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        桌面支持
      </h4>
    </div>
    <div id="collapse-body-ticket" class="panel-collapse collapse ${currentMenu == 'ticket' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-ticket">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/ticket/ticket-catalog-list.do"><i class="glyphicon glyphicon-list"></i> 分类</a></li>
		  <li><a href="${tenantPrefix}/ticket/ticket-group-list.do"><i class="glyphicon glyphicon-list"></i> 小组</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-plm" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-plm" aria-expanded="true" aria-controls="collapse-body-plm">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        项目管理
      </h4>
    </div>
    <div id="collapse-body-plm" class="panel-collapse collapse ${currentMenu == 'plm' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-plm">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/plm/index.do"><i class="glyphicon glyphicon-list"></i> 项目</a></li>
		  <li><a href="${tenantPrefix}/plm/plm-project-list.do"><i class="glyphicon glyphicon-list"></i> 产品管理</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-report" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-report" aria-expanded="true" aria-controls="collapse-body-report">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        报表管理
      </h4>
    </div>
    <div id="collapse-body-report" class="panel-collapse collapse ${currentMenu == 'report' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-report">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/report/report-subject-list.do"><i class="glyphicon glyphicon-list"></i> 报表主题</a></li>
		  <li><a href="${tenantPrefix}/report/report-query-list.do"><i class="glyphicon glyphicon-list"></i> 报表查询</a></li>
		  <li><a href="${tenantPrefix}/report/report-dim-list.do"><i class="glyphicon glyphicon-list"></i> 报表维度</a></li>
		  <li><a href="${tenantPrefix}/report/report-info-list.do"><i class="glyphicon glyphicon-list"></i> 报表信息</a></li>
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

