<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-user">
              <i class="icon-user"></i>
              <span class="title">个人信息</span>
            </a>
          </div>
          <div id="collapse-user" class="accordion-body collapse ${currentMenu == 'my' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/user/my-info-input.do"><i class="icon-user"></i>个人信息</a></li>
			  <li><a href="${tenantPrefix}/user/my-avatar-input.do"><i class="icon-user"></i>修改头像</a></li>
			  <li><a href="${tenantPrefix}/user/my-change-password-input.do"><i class="icon-user"></i>修改密码</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
