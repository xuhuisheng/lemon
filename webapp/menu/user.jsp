<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-user">
              <i class="icon-user"></i>
              <span class="title">用户管理</span>
            </a>
          </div>
          <div id="collapse-user" class="accordion-body collapse ${currentMenu == 'user' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/user/account-info-list.do"><i class="icon-user"></i>用户列表</a></li>
			  <li><a href="${scopePrefix}/user/account-info-input.do"><i class="icon-user"></i>添加用户</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
