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
    <div class="panel-heading" role="tab" id="collapse-header-bpm-process" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-bpm-process" aria-expanded="true" aria-controls="collapse-body-bpm-process">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        个人信息
      </h4>
    </div>
    <div id="collapse-body-bpm-process" class="panel-collapse collapse ${currentMenu == 'my' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-bpm-process">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/user/my-info-input.do"><i class="glyphicon glyphicon-list"></i> 个人信息</a></li>
		  <li><a href="${tenantPrefix}/user/my-avatar-input.do"><i class="glyphicon glyphicon-list"></i> 修改头像</a></li>
		  <li><a href="${tenantPrefix}/user/my-change-password-input.do"><i class="glyphicon glyphicon-list"></i> 修改密码</a></li>
		  <li><a href="${tenantPrefix}/user/my-device-list.do"><i class="glyphicon glyphicon-list"></i> 设备管理</a></li>
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
