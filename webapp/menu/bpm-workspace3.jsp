<%@ page language="java" pageEncoding="UTF-8" %>
<!-- start of sidebar -->
<style type="text/css">
nav ul.nav li a {
    padding-top: 8px;
    padding-bottom: 8px;
}
</style>
<script type="text/javascript">
$(function() {
	$.getJSON('${tenantPrefix}/bpm/rs/bpm-counts', {}, function(data) {
		if (data.data.active > 0) {
			$('#active-count').text(data.data.active);
			$('#active-count').show();
		}
		if (data.data.draft > 0) {
			$('#draft-count').text(data.data.draft);
			$('#draft-count').show();
		}
		if (data.data.personal > 0) {
			$('#personal-count').text(data.data.personal);
			$('#personal-count').show();
		}
		if (data.data.group > 0) {
			$('#group-count').text(data.data.group);
			$('#group-count').show();
		}
	});
});
</script>
<nav class="col-md-2" style="padding-top:65px;">
  <a class="btn btn-default" href="${tenantPrefix}/bpm/workspace-home.do" style="width:100%;padding:10px;margin-bottom:10px;">
    <i class="glyphicon glyphicon-list"></i>
    流程列表
  </a>

  <div class="panel panel-default">
    <div class="panel-heading">
	  <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        我的任务
      </h4>
	</div>

	<ul class="nav nav-list">
	  <li>
	    <a href="${tenantPrefix}/humantask/workspace-personalTasks.do">
	      <i class="glyphicon glyphicon-list"></i>
		  待办任务
	      <span class="badge" id="personal-count" style="float:right;display:none;">0</span>
	    </a>
	  </li>
	  <li>
	    <a href="${tenantPrefix}/humantask/workspace-groupTasks.do">
	      <i class="glyphicon glyphicon-list"></i>
		  待领任务
	      <span class="badge" id="group-count" style="float:right;display:none;">0</span>
	    </a>
	  </li>
	  <li>
	    <a href="${tenantPrefix}/humantask/workspace-historyTasks.do">
	      <i class="glyphicon glyphicon-list"></i>
		  已办任务
	    </a>
	  </li>
	  <li>
	    <a href="${tenantPrefix}/humantask/workspace-delegatedTasks.do">
	      <i class="glyphicon glyphicon-list"></i>
		  经手任务
	    </a>
	  </li>
	</ul>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading">
	  <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        我的流程
      </h4>
	</div>
	<ul class="nav nav-list">
	  <li>
	    <a href="${tenantPrefix}/bpm/workspace-listRunningProcessInstances.do">
	      <i class="glyphicon glyphicon-list"></i>
		  未结流程
	      <span class="badge" id="active-count" style="float:right;display:none;">0</span>
	    </a>
	  </li>
	  <li>
	    <a href="${tenantPrefix}/bpm/workspace-listCompletedProcessInstances.do">
	      <i class="glyphicon glyphicon-list"></i>
		  办结流程
	    </a>
	  </li>
	  <li>
	    <a href="${tenantPrefix}/bpm/workspace-listInvolvedProcessInstances.do">
	      <i class="glyphicon glyphicon-list"></i>
		  参与的流程
	    </a>
	  </li>
	  <li>
	    <a href="${tenantPrefix}/operation/process-operation-listDrafts.do">
	      <i class="glyphicon glyphicon-list"></i>
		  草稿箱
	      <span class="badge" id="draft-count" style="float:right;display:none;">0</span>
	    </a>
	  </li>
	</ul>
  </div>

  <div class="panel panel-default">
    <div class="panel-heading">
	  <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        规则设置
      </h4>
	</div>
    <ul class="nav nav-list">
	  <li>
	    <a href="${tenantPrefix}/delegate/delegate-listMyDelegateInfos.do">
	      <i class="glyphicon glyphicon-list"></i>
		  代理规则
	    </a>
	  </li>
	  <!--
	  <li><a href="#"><i class="glyphicon glyphicon-list"></i> 常用语</a></li>
	  -->
    </ul>
  </div>

</nav>
<!-- end of sidebar -->

