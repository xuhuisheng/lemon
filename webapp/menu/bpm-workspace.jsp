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
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!home.do">发起新流程</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!listRunningProcessInstances.do">运行的流程</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!listCompletedProcessInstances.do">办结的流程</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!listInvolvedProcessInstances.do">参与的流程</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/form/workspace!listDrafts.do">草稿箱</a></li>
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
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!listPersonalTasks.do">待办任务</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!listGroupTasks.do">待领任务</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!listHistoryTasks.do">已办任务</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/workspace!listDelegatedTasks.do">代理中的任务</a></li>
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
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/delegate!prepareAutoDelegate.do">设置自动委托</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/bpm/delegate!listMyDelegateInfos.do">自动委托规则</a></li>
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
			  <li class="m-icn-add-user"><a href="${scopePrefix}/cal/cal-info.do">查看日程</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/cal/cal-info!input.do">添加日程</a></li>
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
			  <li class="m-icn-add-user"><a href="${scopePrefix}/msg/msg-info!listReceived.do">收件箱</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/msg/msg-info!listSent.do">发件箱</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/msg/msg-info!input.do">新建消息</a></li>
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
			  <li class="m-icn-add-user"><a href="${scopePrefix}/doc/doc-info.do">查看文件</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/doc/doc-info!input.do">添加文件</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-forum">
              <i class="icon-user"></i>
              <span class="title">论坛</span>
            </a>
          </div>
          <div id="collapse-forum" class="accordion-body collapse ${currentMenu == 'forum' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-add-user"><a href="${scopePrefix}/forum/forum-topic!view.do">查看贴子</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/forum/forum-topic!create.do">发表贴子</a></li>
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
			  <li class="m-icn-add-user"><a href="${scopePrefix}/addresslist/address-list.do">通讯录</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-car">
              <i class="icon-user"></i>
              <span class="title">车辆管理</span>
            </a>
          </div>
          <div id="collapse-car" class="accordion-body collapse ${currentMenu == 'car' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-add-user"><a href="${scopePrefix}/car/car-info.do">车辆管理</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-meeting">
              <i class="icon-user"></i>
              <span class="title">会议室管理</span>
            </a>
          </div>
          <div id="collapse-meeting" class="accordion-body collapse ${currentMenu == 'meeting' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-add-user"><a href="${scopePrefix}/meeting/meeting-room.do">会议室管理</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/meeting/meeting-info.do">会议预定</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
