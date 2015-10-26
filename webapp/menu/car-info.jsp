<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-car">
              <i class="icon-user"></i>
              <span class="title">车辆管理</span>
            </a>
          </div>
          <div id="collapse-car" class="accordion-body collapse ${currentMenu == 'car' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/car/car-info-list.do"><i class="icon-user"></i>车辆管理</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
