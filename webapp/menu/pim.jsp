<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-process">
              <i class="icon-list"></i>
              <span class="title">我的流程</span>
            </a>
          </div>
          <div id="collapse-bpm-process" class="accordion-body collapse ${currentMenu == 'bpm-process' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/bpm/workspace-home.do"><i class="icon-list"></i>发起新流程</a></li>
			  <li><a href="${tenantPrefix}/bpm/workspace-listRunningProcessInstances.do"><i class="icon-list"></i>运行的流程</a></li>
			  <li><a href="${tenantPrefix}/bpm/workspace-listCompletedProcessInstances.do"><i class="icon-list"></i>办结的流程</a></li>
			  <li><a href="${tenantPrefix}/bpm/workspace-listInvolvedProcessInstances.do"><i class="icon-list"></i>参与的流程</a></li>
			  <li><a href="${tenantPrefix}/operation/process-operation-listDrafts.do"><i class="icon-list"></i>草稿箱</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-task">
              <i class="icon-list"></i>
              <span class="title">我的任务</span>
            </a>
          </div>
          <div id="collapse-bpm-task" class="accordion-body collapse ${currentMenu == 'bpm-task' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/humantask/workspace-personalTasks.do"><i class="icon-list"></i>待办任务</a></li>
			  <li><a href="${tenantPrefix}/humantask/workspace-groupTasks.do"><i class="icon-list"></i>待领任务</a></li>
			  <li><a href="${tenantPrefix}/humantask/workspace-historyTasks.do"><i class="icon-list"></i>已办任务</a></li>
			  <li><a href="${tenantPrefix}/humantask/workspace-delegatedTasks.do"><i class="icon-list"></i>代理中的任务</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-delegate">
              <i class="icon-list"></i>
              <span class="title">委托设置</span>
            </a>
          </div>
          <div id="collapse-bpm-delegate" class="accordion-body collapse ${currentMenu == 'bpm-delegate' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/delegate/delegate-prepareAutoDelegate.do"><i class="icon-list"></i>设置自动委托</a></li>
			  <li><a href="${tenantPrefix}/delegate/delegate-listMyDelegateInfos.do"><i class="icon-list"></i>自动委托规则</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-cal">
              <i class="icon-list"></i>
              <span class="title">日程管理</span>
            </a>
          </div>
          <div id="collapse-cal" class="accordion-body collapse ${currentMenu == 'pim-schedule' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${tenantPrefix}/pim/pim-schedule-list.do">管理日程</a></li>
              <li><a href="${tenantPrefix}/pim/pim-schedule-view.do">日程视图</a></li>
           </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-msg">
              <i class="icon-list"></i>
              <span class="title">私信</span>
            </a>
          </div>
          <div id="collapse-msg" class="accordion-body collapse ${currentMenu == 'msg' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/msg/msg-info-listReceived.do"><i class="icon-list"></i>收件箱</a></li>
			  <li><a href="${tenantPrefix}/msg/msg-info-listSent.do"><i class="icon-list"></i>发件箱</a></li>
			  <li><a href="${tenantPrefix}/msg/msg-info-input.do"><i class="icon-list"></i>新建消息</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-address-list">
              <i class="icon-list"></i>
              <span class="title">通讯录</span>
            </a>
          </div>
          <div id="collapse-address-list" class="accordion-body collapse ${currentMenu == 'address-list' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/pim/address-list-list.do"><i class="icon-list"></i>通讯录</a></li>
              <li><a href="${tenantPrefix}/pim/pim-info-list.do"><i class="icon-list"></i>联系人</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-note">
              <i class="icon-list"></i>
              <span class="title">便签</span>
            </a>
          </div>
          <div id="collapse-note" class="accordion-body collapse ${currentMenu == 'note' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/pim/pim-note-list.do"><i class="icon-list"></i>便签</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-workReport">
              <i class="icon-list"></i>
              <span class="title">日报</span>
            </a>
          </div>
          <div id="collapse-workReport" class="accordion-body collapse ${currentMenu == 'workReport' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/pim/work-report-info-list.do"><i class="icon-list"></i>日报</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-favorite">
              <i class="icon-list"></i>
              <span class="title">收藏</span>
            </a>
          </div>
          <div id="collapse-favorite" class="accordion-body collapse ${currentMenu == 'favorite' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/pim/pim-favorite-list.do"><i class="icon-list"></i>收藏</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->

