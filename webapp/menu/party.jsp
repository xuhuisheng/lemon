<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">
<!--
        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-scope">
              <i class="icon-user"></i>
              <span class="title">应用管理</span>
            </a>
          </div>
          <div id="collapse-scope" class="accordion-body collapse ${currentMenu == 'scope' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/scope/scope-info-list.do"><i class="icon-user"></i>应用管理</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-user-admin">
              <i class="icon-user"></i>
              <span class="title">用户配置</span>
            </a>
          </div>
          <div id="collapse-user-admin" class="accordion-body collapse ${currentMenu == 'user-admin' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/user/user-repo-list.do"><i class="icon-user"></i>用户库列表</a></li>
            </ul>
          </div>
        </div>
-->
        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-party">
              <i class="icon-user"></i>
              <span class="title">组织机构管理</span>
            </a>
          </div>
          <div id="collapse-party" class="accordion-body collapse ${currentMenu == 'party' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/party/tree-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.tree" text="组织机构图"/></a></li>
			  <li><a href="${scopePrefix}/party/party-entity-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.org" text="组织机构"/></a></li>
			  <li><a href="${scopePrefix}/party/party-struct-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.struct" text="组织机构结构"/></a></li>
			  <li><a href="${scopePrefix}/party/party-type-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.type" text="组织机构类型"/></a></li>
			  <li><a href="${scopePrefix}/party/party-struct-type-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.struct.type" text="组织机构结构类型"/></a></li>
			  <li><a href="${scopePrefix}/party/party-struct-rule-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.struct.rule" text="组织机构结构规则"/></a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-workcal">
              <i class="icon-user"></i>
              <span class="title">工作日历</span>
            </a>
          </div>
          <div id="collapse-workcal" class="accordion-body collapse ${currentMenu == 'workcal' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${scopePrefix}/workcal/workcal-type-list.do">工作日历类型</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/workcal/workcal-rule-list.do">工作日历规则</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/workcal/workcal-part-list.do">工作日历时间段</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/workcal/workcal-view.do">工作日历</a></li>
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
			  <li><a href="${scopePrefix}/car/car-info-list.do"><i class="icon-user"></i>车辆管理</a></li>
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
			  <li><a href="${scopePrefix}/meeting/meeting-room-list.do"><i class="icon-user"></i>会议室管理</a></li>
			  <li><a href="${scopePrefix}/meeting/meeting-info-list.do"><i class="icon-user"></i>会议预定</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-template">
              <i class="icon-user"></i>
              <span class="title">模板管理</span>
            </a>
          </div>
          <div id="collapse-template" class="accordion-body collapse ${currentMenu == 'template' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${scopePrefix}/template/template-info-list.do">模板管理</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/template/template-field-list.do">模板项管理</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-sendmail">
              <i class="icon-user"></i>
              <span class="title">发送邮件</span>
            </a>
          </div>
          <div id="collapse-sendmail" class="accordion-body collapse ${currentMenu == 'sendmail' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/sendmail/sendmail-config-list.do">邮件配置</a></li>
              <li><a href="${scopePrefix}/sendmail/sendmail-template-list.do">邮件模板</a></li>
              <li><a href="${scopePrefix}/sendmail/sendmail-queue-list.do">邮件队列</a></li>
              <li><a href="${scopePrefix}/sendmail/sendmail-history-list.do">邮件历史</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-audit">
              <i class="icon-user"></i>
              <span class="title">审计日志</span>
            </a>
          </div>
          <div id="collapse-audit" class="accordion-body collapse ${currentMenu == 'audit' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/audit/audit-base-list.do">审计日志</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-store">
              <i class="icon-user"></i>
              <span class="title">附件管理</span>
            </a>
          </div>
          <div id="collapse-store" class="accordion-body collapse ${currentMenu == 'audit' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/store/store-info-list.do">附件管理</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-whitelist">
              <i class="icon-user"></i>
              <span class="title">白名单管理</span>
            </a>
          </div>
          <div id="collapse-whitelist" class="accordion-body collapse ${currentMenu == 'whitelist' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/whitelist/whitelist-admin-list.do">白名单管理</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-dict">
              <i class="icon-user"></i>
              <span class="title">数据字典</span>
            </a>
          </div>
          <div id="collapse-dict" class="accordion-body collapse ${currentMenu == 'dict' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/dict/dict-type-list.do">数据字典</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-online">
              <i class="icon-user"></i>
              <span class="title">在线用户</span>
            </a>
          </div>
          <div id="collapse-online" class="accordion-body collapse ${currentMenu == 'online' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/user/account-online-list.do">在线用户</a></li>
              <li><a href="${scopePrefix}/user/account-lock-info-list.do">锁定用户</a></li>
            </ul>
          </div>
        </div>
<!--
        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-ticket">
              <i class="icon-user"></i>
              <span class="title">桌面支持</span>
            </a>
          </div>
          <div id="collapse-ticket" class="accordion-body collapse ${currentMenu == 'ticke' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/ticket/ticket-catalog-list.do">分类</a></li>
              <li><a href="${scopePrefix}/ticket/ticket-group-list.do">小组</a></li>
            </ul>
          </div>
        </div>
-->
		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
