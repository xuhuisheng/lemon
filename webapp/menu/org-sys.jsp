<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-group">
              <i class="icon-user"></i>
              <span class="title">组织管理</span>
            </a>
          </div>
          <div id="collapse-group" class="accordion-body collapse ${currentMenu == 'group' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/org-company-list.do">公司</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/org-department-list.do">部门</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-job">
              <i class="icon-user"></i>
              <span class="title">职务管理</span>
            </a>
          </div>
          <div id="collapse-job" class="accordion-body collapse ${currentMenu == 'job' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/job-grade-list.do">职等管理</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/job-level-list.do">职级管理</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/job-type-list.do">职务类型管理</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/job-title-list.do">职务名称管理</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/job-info-list.do">职务管理</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/job-user-list.do">人员职务管理</a></li>
			  <li class="m-icn-view-users"><a href="${tenantPrefix}/org/job-list.do">职等职级表</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
