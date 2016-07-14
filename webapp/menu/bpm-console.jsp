<%@ page language="java" pageEncoding="UTF-8" %>
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>

      <!-- start of sidebar -->
<div class="panel-group col-md-2" id="accordion" role="tablist" aria-multiselectable="true" style="padding-top:65px;">

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-bpm-category" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-bpm-category" aria-expanded="true" aria-controls="collapse-body-bpm-category">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        分类管理
      </h4>
    </div>
    <div id="collapse-body-bpm-category" class="panel-collapse collapse ${currentMenu == 'bpm-category' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-bpm-category">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/bpm/bpm-category-list.do"><i class="glyphicon glyphicon-list"></i> 流程分类</a></li>
		  <li><a href="${tenantPrefix}/bpm/bpm-process-list.do"><i class="glyphicon glyphicon-list"></i> 流程配置</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-bpm-process" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-bpm-process" aria-expanded="true" aria-controls="collapse-body-bpm-process">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        流程管理
      </h4>
    </div>
    <div id="collapse-body-bpm-process" class="panel-collapse collapse ${currentMenu == 'bpm-process' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-bpm-process">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/modeler/modeler-list.do"><i class="glyphicon glyphicon-list"></i> 发布流程</a></li>
		  <li><a href="${tenantPrefix}/bpm/console-listProcessDefinitions.do"><i class="glyphicon glyphicon-list"></i> 流程定义</a></li>
		  <li><a href="${tenantPrefix}/bpm/console-listProcessInstances.do"><i class="glyphicon glyphicon-list"></i> 流程实例</a></li>
		  <li><a href="${tenantPrefix}/bpm/console-listTasks.do"><i class="glyphicon glyphicon-list"></i> 任务</a></li>
		  <li><a href="${tenantPrefix}/bpm/console-listDeployments.do"><i class="glyphicon glyphicon-list"></i> 部署</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-bpm-history" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-bpm-history" aria-expanded="true" aria-controls="collapse-body-bpm-history">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        流程历史
      </h4>
    </div>
    <div id="collapse-body-bpm-history" class="panel-collapse collapse ${currentMenu == 'history' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-bpm-history">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/bpm/console-listHistoricProcessInstances.do"><i class="glyphicon glyphicon-list"></i> 流程实例</a></li>
		  <li><a href="${tenantPrefix}/bpm/console-listHistoricActivityInstances.do"><i class="glyphicon glyphicon-list"></i> 流程节点</a></li>
		  <li><a href="${tenantPrefix}/bpm/console-listHistoricTasks.do"><i class="glyphicon glyphicon-list"></i> 流程任务</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-bpm-delegate" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-bpm-delegate" aria-expanded="true" aria-controls="collapse-body-bpm-delegate">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        自动委托
      </h4>
    </div>
    <div id="collapse-body-bpm-delegate" class="panel-collapse collapse ${currentMenu == 'delegate' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-bpm-delegate">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/delegate/delegate-listDelegateInfos.do"><i class="glyphicon glyphicon-list"></i> 自动委托</a></li>
		  <li><a href="${tenantPrefix}/delegate/delegate-listDelegateHistories.do"><i class="glyphicon glyphicon-list"></i> 自动委托记录</a></li>
        </ul>
      </div>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="collapse-header-" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-job" aria-expanded="true" aria-controls="collapse-body-job">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        异步消息管理
      </h4>
    </div>
    <div id="collapse-body-job" class="panel-collapse collapse ${currentMenu == 'job' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-job">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/bpm/job-list.do"><i class="glyphicon glyphicon-list"></i> 异步消息管理</a></li>
        </ul>
      </div>
    </div>
  </div>

		<footer id="m-footer" class="text-center">
		  <hr>
		  &copy;Mossle
		</footer>

</div>
      <!-- end of sidebar -->

