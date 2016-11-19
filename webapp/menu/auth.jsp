<%@ page language="java" pageEncoding="UTF-8" %>
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>

      <!-- start of sidebar -->
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-user" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-auth" aria-expanded="true" aria-controls="collapse-body-auth">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        权限配置
      </h4>
    </div>
    <div id="collapse-body-auth" class="panel-collapse collapse ${currentMenu == 'auth' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-auth">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/auth/user-connector-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.usermanage" text="用户管理"/></a></li>
		  <li><a href="${tenantPrefix}/auth/role-viewList.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.rolemanage" text="角色管理"/></a></li>
		  <li><a href="${tenantPrefix}/auth/perm-type-list.do"><i class="glyphicon glyphicon-list"></i> 授权分类</a></li>
		  <li><a href="${tenantPrefix}/auth/perm-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.permmanage" text="授权管理"/></a></li>
		  <li><a href="${tenantPrefix}/auth/access-list.do"><i class="glyphicon glyphicon-list"></i> <spring:message code="layout.leftmenu.accessmanage" text="访问权限"/></a></li>
		  <li><a href="${tenantPrefix}/auth/menu-list.do"><i class="glyphicon glyphicon-list"></i> 菜单管理</a></li>
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

