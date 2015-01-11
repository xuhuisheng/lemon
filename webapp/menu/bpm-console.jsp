<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
      <aside id="m-sidebar" class="accordion span2" data-spy="affix" data-offset-top="100">

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-category">
              <i class="icon-user"></i>
              <span class="title">分类管理</span>
            </a>
          </div>
          <div id="collapse-bpm-category" class="accordion-body collapse ${currentMenu == 'bpm-category' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/bpm/bpm-category-list.do"><i class="icon-user"></i>流程分类</a></li>
			  <li><a href="${scopePrefix}/bpm/bpm-process-list.do"><i class="icon-user"></i>流程配置</a></li>
            </ul>
          </div>
		</div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-bpm-process">
              <i class="icon-user"></i>
              <span class="title">流程管理</span>
            </a>
          </div>
          <div id="collapse-bpm-process" class="accordion-body collapse ${currentMenu == 'bpm-process' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/modeler/modeler-list.do"><i class="icon-user"></i>发布流程</a></li>
			  <li><a href="${scopePrefix}/bpm/console-listProcessDefinitions.do"><i class="icon-user"></i>流程定义</a></li>
			  <li><a href="${scopePrefix}/bpm/console-listProcessInstances.do"><i class="icon-user"></i>流程实例</a></li>
			  <li><a href="${scopePrefix}/bpm/console-listTasks.do"><i class="icon-user"></i>任务</a></li>
			  <li><a href="${scopePrefix}/bpm/console-listDeployments.do"><i class="icon-user"></i>部署</a></li>
            </ul>
          </div>
		</div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-history">
              <i class="icon-user"></i>
              <span class="title">流程历史</span>
            </a>
          </div>
          <div id="collapse-history" class="accordion-body collapse ${currentMenu == 'history' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/bpm/console-listHistoricProcessInstances.do"><i class="icon-user"></i>流程实例</a></li>
			  <li><a href="${scopePrefix}/bpm/console-listHistoricActivityInstances.do"><i class="icon-user"></i>流程节点</a></li>
			  <li><a href="${scopePrefix}/bpm/console-listHistoricTasks.do"><i class="icon-user"></i>流程任务</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-delegate">
              <i class="icon-user"></i>
              <span class="title">自动委托</span>
            </a>
          </div>
          <div id="collapse-delegate" class="accordion-body collapse ${currentMenu == 'delegate' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/bpm/delegate-listDelegateInfos.do"><i class="icon-user"></i>自动委托</a></li>
			  <li><a href="${scopePrefix}/bpm/delegate-listDelegateHistories.do"><i class="icon-user"></i>自动委托记录</a></li>
            </ul>
          </div>
        </div>

        <div class="accordion-group">
          <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#m-sidebar" href="#collapse-job">
              <i class="icon-user"></i>
              <span class="title">异步消息管理</span>
            </a>
          </div>
          <div id="collapse-job" class="accordion-body collapse ${currentMenu == 'job' ? 'in' : ''}">
            <ul class="accordion-inner nav nav-list">
			  <li><a href="${scopePrefix}/bpm/job-list.do"><i class="icon-user"></i>异步消息管理</a></li>
            </ul>
          </div>
        </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>
      </aside>
      <!-- end of sidebar -->
