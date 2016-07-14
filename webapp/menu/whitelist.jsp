<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-whitelist">
              <i class="icon-list"></i>
              <span class="title">服务申请</span>
            </a>
          </div>
          <div id="collapse-whitelist" class="accordion-body collapse ${currentMenu == 'whitelist' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
              <li><a href="${ctx}/whitelist/whitelist-list.do">我的应用</a></li>
              <li><a href="${ctx}/whitelist/whitelist-input.do">新申请</a></li>
            </ul>
          </div>
        </div>
		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
