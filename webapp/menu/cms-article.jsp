<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-cms">
              <i class="icon-user"></i>
              <span class="title">公告管理</span>
            </a>
          </div>
          <div id="collapse-cms" class="accordion-body collapse ${currentMenu == 'cms' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-add-user"><a href="${scopePrefix}/cms/cms-article.do">查看公告</a></li>
			  <li class="m-icn-add-user"><a href="${scopePrefix}/cms/cms-article!input.do">填写公告</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
