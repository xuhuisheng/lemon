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
    <div class="panel-heading" role="tab" id="collapse-header-asset" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-asset" aria-expanded="true" aria-controls="collapse-body-asset">
      <h4 class="panel-title">
        <i class="glyphicon glyphicon-list"></i>
        资产
      </h4>
    </div>
    <div class="panel-body">
      <ul class="nav nav-list">
        <li><a href="${tenantPrefix}/asset/my/index.do"><i class="glyphicon glyphicon-list"></i> 个人资产</a></li>
        <li><a href="${tenantPrefix}/asset/my/request.do"><i class="glyphicon glyphicon-list"></i> 资产申请</a></li>
        <!--
        <li><a href="${tenantPrefix}/asset/my/check.do"><i class="glyphicon glyphicon-list"></i> 资产盘点</a></li>
      -->
      </ul>
    </div>
  </div>

        <footer id="m-footer" class="text-center">
          <hr>
          &copy;Mossle
        </footer>

</div>
      <!-- end of sidebar -->

