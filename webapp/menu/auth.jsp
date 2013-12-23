<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-auth-user">
              <i class="icon-user"></i>
              <span class="title"><spring:message code="layout.leftmenu.authmanage" text="权限管理"/></span>
            </a>
          </div>
          <div id="collapse-auth-user" class="accordion-body collapse ${currentMenu == 'auth-user' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/user-connector.do"><spring:message code="layout.leftmenu.usermanage" text="用户管理"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/role-def.do">角色管理</a></li>
		    </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-auth">
              <i class="icon-user"></i>
              <span class="title">权限配置</span>
            </a>
          </div>
          <div id="collapse-auth" class="accordion-body collapse ${currentMenu == 'auth' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/user-status.do"><spring:message code="layout.leftmenu.usermanage" text="用户管理"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/role-def.do">角色模板管理</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/role.do"><spring:message code="layout.leftmenu.rolemanage" text="角色管理"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/perm.do"><spring:message code="layout.leftmenu.permmanage" text="授权管理"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/resc.do"><spring:message code="layout.leftmenu.rescmanage" text="资源管理"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/oper.do"><spring:message code="layout.leftmenu.opermanage" text="操作管理"/></a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/auth/access.do"><spring:message code="layout.leftmenu.accessmanage" text="访问权限"/></a></li>
		    </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
