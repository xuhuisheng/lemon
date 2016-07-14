<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-tenant">
              <i class="icon-list"></i>
              <span class="title">租户管理</span>
            </a>
          </div>
          <div id="collapse-tenant" class="accordion-body collapse ${currentMenu == 'tenant' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${tenantPrefix}/tenant/tenant-info-list.do"><i class="icon-list"></i>租户管理</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
