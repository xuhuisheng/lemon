<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-workcal">
              <i class="icon-user"></i>
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
