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
    <div class="panel-heading" role="tab" id="collapse-header-workcal" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-workcal" aria-expanded="true" aria-controls="collapse-body-workcal">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        工作日历
      </h4>
    </div>
    <div id="collapse-body-workcal" class="panel-collapse collapse ${currentMenu == 'workcal' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-workcal">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/workcal/workcal-type-list.do"><i class="glyphicon glyphicon-list"></i> 工作日历类型</a></li>
		  <li><a href="${tenantPrefix}/workcal/workcal-rule-list.do"><i class="glyphicon glyphicon-list"></i> 工作日历规则</a></li>
		  <li><a href="${tenantPrefix}/workcal/workcal-part-list.do"><i class="glyphicon glyphicon-list"></i> 工作日历时间段</a></li>
		  <li><a href="${tenantPrefix}/workcal/workcal-view.do"><i class="glyphicon glyphicon-list"></i> 工作日历</a></li>

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

