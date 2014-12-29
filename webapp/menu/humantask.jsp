<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-process">
              <i class="icon-user"></i>
              <span class="title">我的流程</span>
            </a>
          </div>
          <div id="collapse-bpm-process" class="accordion-body collapse ${currentMenu == 'bpm-process' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/bpm/workspace-home.do"><i class="icon-user"></i>发起新流程</a></li>
			  <li><a href="${scopePrefix}/bpm/workspace-listRunningProcessInstances.do"><i class="icon-user"></i>运行的流程</a></li>
			  <li><a href="${scopePrefix}/bpm/workspace-listCompletedProcessInstances.do"><i class="icon-user"></i>办结的流程</a></li>
			  <li><a href="${scopePrefix}/bpm/workspace-listInvolvedProcessInstances.do"><i class="icon-user"></i>参与的流程</a></li>
			  <li><a href="${scopePrefix}/form/form-listDrafts.do"><i class="icon-user"></i>草稿箱</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-task">
              <i class="icon-user"></i>
              <span class="title">我的任务</span>
            </a>
          </div>
          <div id="collapse-bpm-task" class="accordion-body collapse ${currentMenu == 'bpm-task' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/bpm/workspace-listPersonalTasks.do"><i class="icon-user"></i>待办任务</a></li>
			  <li><a href="${scopePrefix}/bpm/workspace-listGroupTasks.do"><i class="icon-user"></i>待领任务</a></li>
			  <li><a href="${scopePrefix}/bpm/workspace-listHistoryTasks.do"><i class="icon-user"></i>已办任务</a></li>
			  <li><a href="${scopePrefix}/bpm/workspace-listDelegatedTasks.do"><i class="icon-user"></i>代理中的任务</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-delegate">
              <i class="icon-user"></i>
              <span class="title">委托设置</span>
            </a>
          </div>
          <div id="collapse-bpm-delegate" class="accordion-body collapse ${currentMenu == 'bpm-delegate' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/bpm/delegate-prepareAutoDelegate.do"><i class="icon-user"></i>设置自动委托</a></li>
			  <li><a href="${scopePrefix}/bpm/delegate-listMyDelegateInfos.do"><i class="icon-user"></i>自动委托规则</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-cal">
              <i class="icon-user"></i>
              <span class="title">日程管理</span>
            </a>
          </div>
          <div id="collapse-cal" class="accordion-body collapse ${currentMenu == 'cal' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/cal/cal-info-list.do"><i class="icon-user"></i>查看日程</a></li>
			  <li><a href="${scopePrefix}/cal/cal-info-input.do"><i class="icon-user"></i>添加日程</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-msg">
              <i class="icon-user"></i>
              <span class="title">站内消息</span>
            </a>
          </div>
          <div id="collapse-msg" class="accordion-body collapse ${currentMenu == 'msg' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/msg/msg-info-listReceived.do"><i class="icon-user"></i>收件箱</a></li>
			  <li><a href="${scopePrefix}/msg/msg-info-listSent.do"><i class="icon-user"></i>发件箱</a></li>
			  <li><a href="${scopePrefix}/msg/msg-info-input.do"><i class="icon-user"></i>新建消息</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-doc">
              <i class="icon-user"></i>
              <span class="title">文件管理</span>
            </a>
          </div>
          <div id="collapse-doc" class="accordion-body collapse ${currentMenu == 'doc' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/doc/doc-info-list.do"><i class="icon-user"></i>查看文件</a></li>
			  <li><a href="${scopePrefix}/doc/doc-info-input.do"><i class="icon-user"></i>添加文件</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-address-list">
              <i class="icon-user"></i>
              <span class="title">通讯录</span>
            </a>
          </div>
          <div id="collapse-address-list" class="accordion-body collapse ${currentMenu == 'address-list' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/pim/address-list-list.do"><i class="icon-user"></i>通讯录</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
