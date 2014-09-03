<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-auth">
              <i class="icon-user"></i>
              <span class="title">权限配置</span>
            </a>
          </div>
          <div id="collapse-auth" class="accordion-body collapse ${currentMenu == 'auth' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/auth/user-connector-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.usermanage" text="用户管理"/></a></li>
			  <li><a href="${scopePrefix}/auth/role-viewList.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.rolemanage" text="角色管理"/></a></li>
			  <li><a href="${scopePrefix}/auth/perm-type-list.do"><i class="icon-user"></i>授权分类</a></li>
			  <li><a href="${scopePrefix}/auth/perm-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.permmanage" text="授权管理"/></a></li>
			  <li><a href="${scopePrefix}/auth/access-list.do"><i class="icon-user"></i><spring:message code="layout.leftmenu.accessmanage" text="访问权限"/></a></li>
		    </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
