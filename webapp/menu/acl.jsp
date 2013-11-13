<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-acl">
              <i class="icon-user"></i>
              <span class="title">数据权限</span>
            </a>
          </div>
          <div id="collapse-acl" class="accordion-body collapse ${currentMenu == 'acl' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${scopePrefix}/acl/acl-sid.do">主体管理</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/acl/acl-object-type.do">数据类型管理</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/acl/acl-object-identity.do">数据标识管理</a></li>
			  <li class="m-icn-view-users"><a href="${scopePrefix}/acl/acl-entry.do">数据权限</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
