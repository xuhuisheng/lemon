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
    <div class="panel-heading" role="tab" id="collapse-header-humantask" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-humantask" aria-expanded="true" aria-controls="collapse-body-humantask">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        人工任务
      </h4>
    </div>
    <div id="collapse-body-humantask" class="panel-collapse collapse ${currentMenu == 'humantask' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-humantask">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/humantask/task-info-list.do"><i class="glyphicon glyphicon-list"></i> 任务列表</a></li>
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

