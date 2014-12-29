<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-pim">
              <i class="icon-user"></i>
              <span class="title">个人事务</span>
            </a>
          </div>
          <div id="collapse-pim" class="accordion-body collapse ${currentMenu == 'pim' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${scopePrefix}/pim/address-list-list.do">通讯录</a></li>
              <li><a href="${scopePrefix}/pim/pim-info-list.do">联系人</a></li>
              <li><a href="${scopePrefix}/pim/pim-scheduler-list.do">日程</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
