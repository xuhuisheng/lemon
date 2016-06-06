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
    <div class="panel-heading" role="tab" id="collapse-header-schedule" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-schedule" aria-expanded="true" aria-controls="collapse-body-delegate">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        日程管理
      </h4>
    </div>
    <div id="collapse-body-schedule" class="panel-collapse collapse ${currentMenu == 'pim-schedule' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-schedule">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/pim/pim-schedule-list.do"><i class="glyphicon glyphicon-list"></i> 管理日程</a></li>
		  <li><a href="${tenantPrefix}/pim/pim-schedule-view.do"><i class="glyphicon glyphicon-list"></i> 日程视图</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-msg" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-msg" aria-expanded="true" aria-controls="collapse-body-msg">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        私信
      </h4>
    </div>
    <div id="collapse-body-msg" class="panel-collapse collapse ${currentMenu == 'msg' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-msg">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/msg/msg-info-listReceived.do"><i class="glyphicon glyphicon-list"></i> 收件箱</a></li>
		  <li><a href="${tenantPrefix}/msg/msg-info-listSent.do"><i class="glyphicon glyphicon-list"></i> 发件箱</a></li>
		  <li><a href="${tenantPrefix}/msg/msg-info-input.do"><i class="glyphicon glyphicon-list"></i> 新建消息</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-address-list" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-address-list" aria-expanded="true" aria-controls="collapse-body-address-list">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        通讯录
      </h4>
    </div>
    <div id="collapse-body-address-list" class="panel-collapse collapse ${currentMenu == 'address-list' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-address-list">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/pim/address-list-list.do"><i class="glyphicon glyphicon-list"></i> 通讯录</a></li>
		  <li><a href="${tenantPrefix}/pim/pim-info-list.do"><i class="glyphicon glyphicon-list"></i> 联系人</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-note" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-note" aria-expanded="true" aria-controls="collapse-body-note">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        便签
      </h4>
    </div>
    <div id="collapse-body-note" class="panel-collapse collapse ${currentMenu == 'note' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-note">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/pim/pim-note-list.do"><i class="glyphicon glyphicon-list"></i> 便签</a></li>
		  <li><a href="${tenantPrefix}/pim/pim-note-view.do"><i class="glyphicon glyphicon-list"></i> 便签墙</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-workReport" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-workReport" aria-expanded="true" aria-controls="collapse-body-workReport">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        日报
      </h4>
    </div>
    <div id="collapse-body-workReport" class="panel-collapse collapse ${currentMenu == 'workReport' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-workReport">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/pim/work-report-info-list.do"><i class="glyphicon glyphicon-list"></i> 日报</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-favorite" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-favorite" aria-expanded="true" aria-controls="collapse-body-favorite">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        收藏
      </h4>
    </div>
    <div id="collapse-body-favorite" class="panel-collapse collapse ${currentMenu == 'favorite' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-favorite">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/pim/pim-favorite-list.do"><i class="glyphicon glyphicon-list"></i> 收藏</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-remind" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-remind" aria-expanded="true" aria-controls="collapse-body-remind">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        提醒
      </h4>
    </div>
    <div id="collapse-body-remind" class="panel-collapse collapse ${currentMenu == 'remind' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-remind">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/pim/pim-remind-list.do"><i class="glyphicon glyphicon-list"></i> 提醒</a></li>
        </ul>
      </div>
    </div>
  </div>

</div>

      <!-- end of sidebar -->

