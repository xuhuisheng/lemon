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
    <div class="panel-heading" role="tab" id="collapse-header-user" data-toggle="collapse" data-parent="#accordion" href="#collapse-body-product" aria-expanded="true" aria-controls="collapse-body-product">
      <h4 class="panel-title">
	    <i class="glyphicon glyphicon-list"></i>
        产品管理
      </h4>
    </div>
    <div id="collapse-body-product" class="panel-collapse collapse ${currentMenu == 'product' ? 'in' : ''}" role="tabpanel" aria-labelledby="collapse-header-product">
      <div class="panel-body">
        <ul class="nav nav-list">
		  <li><a href="${tenantPrefix}/product/product-info-list.do"><i class="glyphicon glyphicon-list"></i> 产品管理</a></li>
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

