<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
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
    <div class="panel-heading" role="tab" id="collapse-header-group" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-group" aria-expanded="true" aria-controls="collapse-body-group">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
	    <span class="title">群组管理</span>
      </h4>
    </div>
    <div id="collapse-body-group" class="panel-collapse collapse ${currentMenu == 'group' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-group">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/group/group-info-list.do"><i class="glyphicon glyphicon-list"></i> 群组列表</a></li>
		  <li><a href="${tenantPrefix}/group/group-info-input.do"><i class="glyphicon glyphicon-list"></i> 添加群组</a></li>
        </ul>
      </div>
    </div>
  </div>

</div>

      <!-- end of sidebar -->

