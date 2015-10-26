<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-category">
              <i class="icon-user"></i>
              <span class="title">模型管理</span>
            </a>
          </div>
          <div id="collapse-bpm-category" class="accordion-body collapse ${currentMenu == 'model' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${tenantPrefix}/model/model-info-list.do"><i class="icon-user"></i>模型</a></li>
			  <li><a href="${tenantPrefix}/model/model-field-list.do"><i class="icon-user"></i>模型项</a></li>
            </ul>
          </div>
		</div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
