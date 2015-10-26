<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-online">
              <i class="icon-user"></i>
              <span class="title">在线用户</span>
            </a>
          </div>
          <div id="collapse-online" class="accordion-body collapse ${currentMenu == 'online' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${tenantPrefix}/user/account-online-list.do">在线用户</a></li>
              <li><a href="${tenantPrefix}/user/account-lock-info-list.do">锁定用户</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
