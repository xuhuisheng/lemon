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
    <div class="panel-heading" role="tab" id="collapse-header-account-device" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-account-device" aria-expanded="true" aria-controls="collapse-body-account-device">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
	    <span class="title">设备管理</span>
      </h4>
    </div>
    <div id="collapse-body-account-device" class="panel-collapse collapse ${currentMenu == 'account-device' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-account-device">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/user/account-device-list.do"><i class="glyphicon glyphicon-list"></i> 设备管理</a></li>
        </ul>
      </div>
    </div>
  </div>

</div>

      <!-- end of sidebar -->



