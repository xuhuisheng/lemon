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

  <div class="panel panel-default" style="margin-bottom:20px;">
    <div class="panel-heading" role="tab" id="collapse-header-asset" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-asset" aria-expanded="true" aria-controls="collapse-body-asset">
      <h4 class="panel-title">
        <i class="glyphicon glyphicon-list"></i>
        资产管理
      </h4>
    </div>
    <div class="panel-body">
      <ul class="nav nav-list">
        <li><a href="${tenantPrefix}/asset/asset-category-list.do"><i class="glyphicon glyphicon-list"></i> 资产分类</a></li>
        <li><a href="${tenantPrefix}/asset/asset-info-list.do"><i class="glyphicon glyphicon-list"></i> 资产管理</a></li>
        <li><a href="${tenantPrefix}/asset/asset-lend-list.do"><i class="glyphicon glyphicon-list"></i> 资产领用记录</a></li>
      </ul>
    </div>
  </div>

  <div class="panel panel-default">
    <div class="panel-body">
      <ul class="nav nav-list">
        <li><a href="${tenantPrefix}/asset/sku-category-list.do"><i class="glyphicon glyphicon-list"></i> SKU分类</a></li>
        <li><a href="${tenantPrefix}/asset/sku-info-list.do"><i class="glyphicon glyphicon-list"></i> SKU信息</a></li>
        <li><a href="${tenantPrefix}/asset/stock-info-list.do"><i class="glyphicon glyphicon-list"></i> 库存</a></li>
        <li><a href="${tenantPrefix}/asset/stock-item-list.do"><i class="glyphicon glyphicon-list"></i> 库存明细</a></li>
      </ul>
    </div>
  </div>

        <footer id="m-footer" class="text-center">
          <hr>
          &copy;Mossle
        </footer>

</div>
      <!-- end of sidebar -->

