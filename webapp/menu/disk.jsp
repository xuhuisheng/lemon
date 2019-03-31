<%@ page language="java" pageEncoding="UTF-8" %>
      <!-- start of sidebar -->
<style type="text/css">
#accordion .panel-heading {
	cursor: pointer;
}
#accordion .panel-body {
	padding:0px;
}
</style>
<div class="panel-group col-md-2" id="accordion">

  <div class="panel panel-default" style="margin-bottom:20px;">
    <div class="panel-body">
      <ul class="nav nav-list">
        <li><a href="${tenantPrefix}/disk/index.do"><i class="glyphicon glyphicon-list"></i> 个人文档</a></li>
        <li><a href="${tenantPrefix}/disk/share.do"><i class="glyphicon glyphicon-list"></i> 共享文档</a></li>
        <li><a href="${tenantPrefix}/disk/group.do"><i class="glyphicon glyphicon-list"></i> 群组文档</a></li>
        <li><a href="${tenantPrefix}/disk/repo.do"><i class="glyphicon glyphicon-list"></i> 文档库</a></li>
        <li><a href="${tenantPrefix}/disk/trash.do"><i class="glyphicon glyphicon-list"></i> 回收站</a></li>
      </ul>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-body">
      <ul class="nav nav-list">
        <li><a href="${tenantPrefix}/disk/s/internal.do"><i class="glyphicon glyphicon-list"></i> 内部共享</a></li>
        <li><a href="${tenantPrefix}/disk/s/external.do"><i class="glyphicon glyphicon-list"></i> 外链分享</a></li>
        <li><a href="${tenantPrefix}/disk/s/public.do"><i class="glyphicon glyphicon-list"></i> 全员分享</a></li>
        <li><a href="${tenantPrefix}/disk/s/shield.do"><i class="glyphicon glyphicon-list"></i> 屏蔽分享</a></li>
      </ul>
    </div>
  </div>

</div>

      <!-- end of sidebar -->
