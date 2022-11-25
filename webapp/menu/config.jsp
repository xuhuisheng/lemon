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
    <div class="panel-heading">
      <h4 class="panel-title">
      <i class="glyphicon glyphicon-list"></i>
        配置管理
      </h4>
    </div>
      <div class="panel-body">
      <ul class="nav nav-list">
      <li><a href="${tenantPrefix}/content/config/config-app-list.do"><i class="glyphicon glyphicon-list"></i> 应用</a></li>
      <li><a href="${tenantPrefix}/content/config/config-app-input.do"><i class="glyphicon glyphicon-list"></i> 配置项</a></li>
      </ul>
    </div>
  </div>

    <footer id="m-footer" class="text-center">
      <hr>
      &copy;Mossle
    </footer>

</div>
      <!-- end of sidebar -->

