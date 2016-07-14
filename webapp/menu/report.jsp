<%@ page language="java" pageEncoding="UTF-8" %>      <!-- start of sidebar -->
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>

<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-report" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-report" aria-expanded="true" aria-controls="collapse-body-bpm-process">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        报表管理
      </h4>
    </div>
    <div id="collapse-body-report" class="panel-collapse collapse ${currentMenu == 'chart' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-report">
      <div class="panel-body">
        <ul class="nav nav-list">
			  <li><a href="${tenantPrefix}/report/chart-mostActiveProcess.do"><i class="glyphicon glyphicon-list"></i> 最活跃流程</a></li>
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
