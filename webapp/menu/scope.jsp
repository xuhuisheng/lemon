<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-scope">
              <i class="icon-list"></i>
              <span class="title">应用管理</span>
            </a>
          </div>
          <div id="collapse-scope" class="accordion-body collapse ${currentMenu == 'scope' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${tenantPrefix}/scope/scope-info-list.do"><i class="icon-list"></i>应用管理</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-user-admin">
              <i class="icon-list"></i>
              <span class="title">用户配置</span>
            </a>
          </div>
          <div id="collapse-user-admin" class="accordion-body collapse ${currentMenu == 'user-admin' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/user/user-repo-list.do"><i class="icon-list"></i>用户库列表</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-party">
              <i class="icon-list"></i>
              <span class="title">组织机构管理</span>
            </a>
          </div>
          <div id="collapse-party" class="accordion-body collapse ${currentMenu == 'party' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/party/tree-list.do"><i class="icon-list"></i><spring:message code="layout.leftmenu.tree" text="组织机构图"/></a></li>
			  <li><a href="${tenantPrefix}/party/party-entity-list.do"><i class="icon-list"></i><spring:message code="layout.leftmenu.org" text="组织机构"/></a></li>
			  <li><a href="${tenantPrefix}/party/party-struct-list.do"><i class="icon-list"></i><spring:message code="layout.leftmenu.struct" text="组织机构结构"/></a></li>
			  <li><a href="${tenantPrefix}/party/party-type-list.do"><i class="icon-list"></i><spring:message code="layout.leftmenu.type" text="组织机构类型"/></a></li>
			  <li><a href="${tenantPrefix}/party/party-struct-type-list.do"><i class="icon-list"></i><spring:message code="layout.leftmenu.struct.type" text="组织机构结构类型"/></a></li>
			  <li><a href="${tenantPrefix}/party/party-struct-rule-list.do"><i class="icon-list"></i><spring:message code="layout.leftmenu.struct.rule" text="组织机构结构规则"/></a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-workcal">
              <i class="icon-list"></i>
              <span class="title">工作日历</span>
            </a>
          </div>
          <div id="collapse-workcal" class="accordion-body collapse ${currentMenu == 'workcal' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/workcal/workcal-type-list.do">工作日历类型</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/workcal/workcal-rule-list.do">工作日历规则</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/workcal/workcal-part-list.do">工作日历时间段</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/workcal/workcal-view.do">工作日历</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
