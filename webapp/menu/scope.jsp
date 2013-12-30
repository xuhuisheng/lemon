<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-scope">
              <i class="icon-user"></i>
              <span class="title">应用管理</span>
            </a>
          </div>
          <div id="collapse-scope" class="accordion-body collapse ${currentMenu == 'scope' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/scope/scope-info.do">应用管理</a></li>
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
			  <li><a href="${scopePrefix}/user/user-repo.do">用户库列表</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-group">
              <i class="icon-user"></i>
              <span class="title">组织管理</span>
            </a>
          </div>
          <div id="collapse-group" class="accordion-body collapse ${currentMenu == 'group' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${scopePrefix}/group/org-company.do">公司</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/group/org-department.do">部门</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/group/org-group.do">小组</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/group/org-position-type.do">岗位类型</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/group/org-position.do">岗位</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-party">
              <i class="icon-user"></i>
              <span class="title">组织机构管理</span>
            </a>
          </div>
          <div id="collapse-party" class="accordion-body collapse ${currentMenu == 'party' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/tree.do"><spring:message code="layout.leftmenu.tree" text="组织机构图"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/party-entity.do"><spring:message code="layout.leftmenu.org" text="组织机构"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/party-struct.do"><spring:message code="layout.leftmenu.struct" text="组织机构结构"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/party-type.do"><spring:message code="layout.leftmenu.type" text="组织机构类型"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/party-struct-type.do"><spring:message code="layout.leftmenu.struct.type" text="组织机构结构类型"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/party-struct-rule.do"><spring:message code="layout.leftmenu.struct.rule" text="组织机构结构规则"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/party-dim-root.do">维度</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/party/party-dim-root.do">顶级组织</a></li>
            </ul>
          </div>
        </div>
		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
